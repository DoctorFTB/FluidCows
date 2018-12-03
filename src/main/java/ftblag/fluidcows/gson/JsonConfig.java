package ftblag.fluidcows.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraftforge.fml.common.LoaderException;

import java.io.*;

/*
 * @author GenDeathrow with changes by FTB_lag
 */
public class JsonConfig {

    private File file;
    private JsonObject obj;
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private boolean needSave;

    public JsonConfig(File file) {
        this.file = file;
    }

    private void classAuthor() {
        System.out.println("@author class GenDeathrow with changes by FTB_lag");
        System.out.println("But in this moment mod without sources");
    }

    public void load() {
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            obj = gson.fromJson(new BufferedReader(new FileReader(file)), JsonObject.class);
            if (obj == null)
                obj = new JsonObject();
        } catch (Exception e) {
            throw new LoaderException("Failed to load FluidCows config!", e);
        }
    }

    public void save() {
        if (!needSave)
            return;
        try {
            BufferedWriter fw = new BufferedWriter(new FileWriter(file));
            fw.write(gson.toJson(obj));
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void needSave() {
        needSave = true;
    }

    public JsonObject getOrCreateCategory(String category) {
        JsonObject object = new JsonObject();

        if (obj.has(category)) {
            object = obj.getAsJsonObject(category);
        } else {
            obj.add(category, object);
            needSave();
        }

        return object;
    }

    public boolean getOrDefBoolean(String cat, String key, boolean def) {
        JsonObject category = getOrCreateCategory(cat);

        if (category.has(key)) {
            def = category.get(key).getAsBoolean();
        } else {
            category.addProperty(key, def);
            needSave();
        }

        return def;
    }

    public String getOrDefString(String cat, String key, String def) {

        JsonObject object = getOrCreateCategory(cat);

        if (object.has(key)) {
            def = object.get(key).getAsString();
        } else {
            object.addProperty(key, def);
            needSave();
        }

        return def;
    }

    public int getOrDefInt(String cat, String key, int def) {

        JsonObject object = getOrCreateCategory(cat);
        if (object.has(key)) {
            def = object.get(key).getAsInt();
        } else {
            object.addProperty(key, def);
            needSave();
        }

        return def;
    }
}
