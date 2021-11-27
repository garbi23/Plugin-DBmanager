package garbi.plugin.dbmanager.config

import garbi.plugin.dbmanager.PluginDBmanager
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.IOException

class PluginConfigManager(private val plugin : PluginDBmanager, name : String){
    private var config : YamlConfiguration = YamlConfiguration()
    private var file : File = File(plugin.dataFolder, "${name}.yml")

    init{
        if(!file.exists()){
            try { config.save(file) } catch (e: IOException) { e.printStackTrace() }
        }else{
            config = YamlConfiguration.loadConfiguration(file)
        }
    }

    fun getConfig() : YamlConfiguration{
        return config
    }

}