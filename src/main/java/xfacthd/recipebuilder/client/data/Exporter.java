package xfacthd.recipebuilder.client.data;

import com.google.gson.*;
import net.minecraft.client.Minecraft;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.storage.FolderName;
import net.minecraftforge.registries.IForgeRegistry;
import xfacthd.recipebuilder.RecipeBuilder;
import xfacthd.recipebuilder.client.util.BuilderException;

import javax.annotation.Nullable;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Exporter
{
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void exportRecipe(Consumer<Consumer<IFinishedRecipe>> simpleSave, BiConsumer<Consumer<IFinishedRecipe>, String> namedSave, String recipeName)
    {
        try
        {
            if (!recipeName.isEmpty())
            {
                namedSave.accept(Exporter::exportRecipe, recipeName);
            }
            else
            {
                simpleSave.accept(Exporter::exportRecipe);
            }
        }
        catch (IllegalStateException e)
        {
            throw new BuilderException(new StringTextComponent(e.getMessage()));
        }
    }

    public static void exportRecipe(IFinishedRecipe recipe)
    {
        String recipeName = recipe.getId().toString();
        String advancementName = Optional.ofNullable(recipe.getAdvancementId()).map(ResourceLocation::getPath).orElse(null);

        JsonObject recipeJson = recipe.serializeRecipe();
        JsonObject advancementJson = recipe.serializeAdvancement();

        //Export to game directory
        Path datapackRoot = getGameDir().resolve(RecipeBuilder.MOD_ID + "/generated_pack");
        exportRecipeToPath(datapackRoot, recipeName, recipeJson, advancementName, advancementJson);

        //Export to datapack in running world in singleplayer
        if (Minecraft.getInstance().hasSingleplayerServer())
        {
            datapackRoot = getServerPackDir().resolve(RecipeBuilder.MOD_ID);
            exportRecipeToPath(datapackRoot, recipeName, recipeJson, advancementName, advancementJson);
        }
    }

    private static void exportRecipeToPath(Path packRoot, String recipeName, JsonObject recipe, @Nullable String advancementName, @Nullable JsonObject advancement)
    {
        ensurePackDefinitionExists(packRoot);

        String domain;
        if (recipeName.contains(":"))
        {
            domain = recipeName.substring(0, recipeName.indexOf(":"));
            recipeName = recipeName.substring(recipeName.indexOf(":") + 1);
        }
        else
        {
            domain = RecipeBuilder.MOD_ID;
        }

        Path recipePath = packRoot.resolve("data/" + domain + "/recipes/" + recipeName + ".json");
        saveToFile(recipePath, recipe);

        if (advancementName != null && advancement != null)
        {
            Path advancementPath = packRoot.resolve("data/" + domain + "/advancements/" + advancementName + ".json");
            saveToFile(advancementPath, advancement);
        }
    }

    public static void exportTag(IForgeRegistry<?> regType, String name, List<String> entries, boolean replace)
    {
        String type = regType.getRegistryName().getPath();

        JsonObject obj = new JsonObject();
        obj.addProperty("replace", replace);

        JsonArray arr = new JsonArray();
        entries.forEach(arr::add);
        obj.add("values", arr);

        //Export to game directory
        Path datapackRoot = getGameDir().resolve(RecipeBuilder.MOD_ID + "/generated_pack");
        exportTagToPath(datapackRoot, type, name, obj);

        if (Minecraft.getInstance().hasSingleplayerServer())
        {
            datapackRoot = getServerPackDir().resolve(RecipeBuilder.MOD_ID);
            exportTagToPath(datapackRoot, type, name, obj);
        }
    }

    private static void exportTagToPath(Path packRoot, String type, String tagName, JsonObject contents)
    {
        ensurePackDefinitionExists(packRoot);

        String domain;
        if (tagName.contains(":"))
        {
            domain = tagName.substring(0, tagName.indexOf(":"));
            tagName = tagName.substring(tagName.indexOf(":") + 1);
        }
        else
        {
            domain = RecipeBuilder.MOD_ID;
        }

        Path tagPath = packRoot.resolve("data/" + domain + "/tags/" + type + "s/" + tagName + ".json");
        saveToFile(tagPath, contents);
    }

    private static void ensurePackDefinitionExists(Path packRoot)
    {
        Path metaFile = packRoot.resolve("pack.mcmeta");
        if (!Files.exists(metaFile))
        {
            JsonObject pack = new JsonObject();
            pack.addProperty("pack_format", SharedConstants.getCurrentVersion().getPackVersion());
            pack.addProperty("description", "Datapack generated by the RecipeBuilder mod");

            JsonObject root = new JsonObject();
            root.add("pack", pack);

            try
            {
                Files.createDirectories(packRoot);
                try (BufferedWriter writer = Files.newBufferedWriter(metaFile))
                {
                    writer.write(GSON.toJson(root));
                }
            }
            catch (IOException e)
            {
                throw new BuilderException(new StringTextComponent(e.getMessage()));
            }
        }
    }

    private static void saveToFile(Path path, JsonElement json)
    {
        try
        {
            Files.createDirectories(path.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(path))
            {
                writer.write(GSON.toJson(json));
            }
        }
        catch (IOException e)
        {
            throw new BuilderException(new StringTextComponent(e.getMessage()));
        }
    }

    private static Path getGameDir() { return Minecraft.getInstance().gameDirectory.toPath(); }

    private static Path getServerPackDir()
    {
        IntegratedServer server = Minecraft.getInstance().getSingleplayerServer();
        //noinspection ConstantConditions
        return server.getWorldPath(FolderName.DATAPACK_DIR);
    }
}