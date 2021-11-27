package garbi.plugin.dbmanager.config

import garbi.plugin.dbmanager.PluginDBmanager
import garbi.plugin.dbmanager.allvar.Allvar
import garbi.plugin.dbmanager.allvar.Allvar.database
import garbi.plugin.dbmanager.allvar.Allvar.driver
import garbi.plugin.dbmanager.allvar.Allvar.host
import garbi.plugin.dbmanager.allvar.Allvar.password
import garbi.plugin.dbmanager.allvar.Allvar.username
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.IOException

class ConfigManager(private val plugin : PluginDBmanager) {
    fun configLoad(){
        val file = File(plugin.dataFolder, "config.yml")
        if(!file.exists()){ configSave(); return}
        var config = YamlConfiguration.loadConfiguration(file)
        driver = config["db.driver"] as String
        host = config["db.host"] as String
        database = config["db.database"] as String
        username = config["db.username"] as String
        password = config["db.password"] as String
    }

    fun configSave(){
        val config = YamlConfiguration();
        val file = File(plugin.dataFolder, "config.yml")
        config["db.driver"] = driver
        config["db.host"] = host
        config["db.database"] = database
        config["db.username"] = username
        config["db.password"] = password
        try { config.save(file) } catch (e: IOException) { e.printStackTrace() }
    }
}