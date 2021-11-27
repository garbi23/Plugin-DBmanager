package garbi.plugin.dbmanager.allvar

import garbi.plugin.dbmanager.parse.ObjectParse
import garbi.plugin.dbmanager.config.PluginConfigManager
import java.sql.Connection
import java.sql.DriverManager
import java.util.*

object Allvar {
    var pluginsConnect : MutableMap<String, ObjectParse> = HashMap()
    var driver : String = "org.mariadb.jdbc.Driver"
    var host : String = "127.0.0.1"
    var database : String = "plugins"
    var username : String = "name"
    var password : String = "password"
    var configMap : Map<String, PluginConfigManager> = HashMap()

    fun getConnection() : Connection?{
        var connect : Connection? = null
        try {
            Class.forName(driver)
            connect = DriverManager.getConnection(
                "jdbc:mariadb://" + Allvar.host + "/" + Allvar.database,
                Allvar.username,
                Allvar.password
            )
            return connect
        }catch (e : Exception){
            print("DB 정보를 가져 올 수 없습니다!")
        }
        return null
    }

}