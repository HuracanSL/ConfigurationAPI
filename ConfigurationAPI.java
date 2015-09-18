import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
 
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.logging.Level;
 
public class ConfigurationAPI {
 
private static JavaPlugin plugin;
private static HashMap<String, FileConfiguration>configs = new HashMap<>();
private static HashMap<String, File>files = new HashMap<>();
private File configFile;
private FileConfiguration config;
private String configName;
 
    public ConfigurationAPI(JavaPlugin plugin, String fileName, File fileLocation){
        this.plugin = plugin;
        this.configName = fileName;
        if (configFile == null) {
            configFile = new File(fileLocation, fileName);
        }
        if(config == null){
            config = YamlConfiguration.loadConfiguration(configFile);
        }
        if(!configs.containsKey(fileName) && !files.containsKey(fileName)){
            configs.put(fileName, config);
            files.put(fileName, configFile);
        }
    }
 
 
    public String getName(){
        return configName;
    }
 
    public File getFile() {
        return configFile;
    }
 
    public static File getFile(JavaPlugin plugin, String fileName) throws IOException {
        loadAllConfigs(plugin);
        return files.get(fileName);
    }
 
    public void reloadConfig(){
        config = YamlConfiguration.loadConfiguration(getFile());
 
        InputStream defConfigStream = plugin.getResource(getName());
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            config.setDefaults(defConfig);
        }
    }
 
    public static void reloadConfig(JavaPlugin plugin, String fileName) throws IOException {
        FileConfiguration config = YamlConfiguration.loadConfiguration(getFile(plugin, fileName));
 
        InputStream defConfigStream = plugin.getResource(fileName);
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            config.setDefaults(defConfig);
        }
    }
 
    public static HashMap<String, FileConfiguration> getConfigs(JavaPlugin plugin){
        loadAllConfigs(plugin);
        return configs;
    }
 
    public FileConfiguration getConfig(){
        return config;
    }
 
    public static FileConfiguration getConfig(JavaPlugin plugin, String fileName) throws IOException{
        return getConfigs(plugin).get(fileName);
    }
 
    public void saveConfig(){
        try {
            getConfig().save(configFile);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "ERROR: Could not save the config file " + getName());
            ex.printStackTrace();
        }
    }
 
    public static void saveConfig(JavaPlugin plugin, String fileName) throws IOException{
        try{
            loadAllConfigs(plugin);
            getConfig(plugin, fileName).save(getFile(plugin, fileName));
        }catch(IOException ex){
            plugin.getLogger().log(Level.SEVERE, "ERROR: Could not save the config file " + fileName);
            ex.printStackTrace();
        }
    }
 
    public static void saveDefaultConfig(JavaPlugin plugin, File fileLocation, String fileName){
        File configFile = new File(fileLocation, fileName);
        if (!configFile.exists()) {
            plugin.saveResource(fileName, false);
        }
    }
 
    public static void loadAllConfigs(JavaPlugin plugin){
        try{
            for(File file : plugin.getDataFolder().listFiles()){
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                if(!configs.containsKey(file.getName()) && !files.containsKey(file.getName())){
                    configs.put(file.getName(), config);
                    files.put(file.getName(), file);
                }
            }
        }catch(Exception e){
            Bukkit.getServer().getLogger().severe("Oh oh! Could not load the configuration files for Plugin " + plugin.getName() + "!");
            e.printStackTrace();
        }
    }
}
