package garbi.plugin.dbmanager

import garbi.plugin.dbmanager.allvar.Allvar
import garbi.plugin.dbmanager.allvar.Allvar.pluginsConnect
import garbi.plugin.dbmanager.config.ConfigManager
import garbi.plugin.dbmanager.parse.ObjectParse
import org.bukkit.plugin.java.JavaPlugin
import java.sql.*

class PluginDBmanager : JavaPlugin() {

    private var connection : Connection? = null
    private var statement: Statement? = null
    private var configManager : ConfigManager = ConfigManager(this)

    override fun onEnable() {
        super.onEnable()
        logger.info("활성화")
        configManager.configLoad()
        connectServer()
        val CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS `license` (\n" +
                "  `index` int(11) NOT NULL AUTO_INCREMENT,\n" +
                "  `name` varchar(50) DEFAULT 'default',\n" +
                "  `address` varchar(50) DEFAULT '127.0.0.1',\n" +
                "  `owner` varchar(50) DEFAULT 'non',\n" +
                "  PRIMARY KEY (`index`)\n" +
                ") ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4;"
        val pstmt: PreparedStatement = connection?.prepareStatement(CREATE_TABLE_SQL)!!
        var rs = pstmt.executeQuery()
        rs.close()
        pstmt.close()
        closeServer()
    }

    private fun connectServer(){
        try {
            openConnection()
            statement = connection?.createStatement()!!
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: SQLException) {
            logger.info("라이센스 서버 상태가 불안정합니다!")
            e.printStackTrace()
        }
    }

    private fun closeServer(){
        connection?.close()
        statement?.close()
    }

    fun setObjectParePlguin(name : String) : ObjectParse {
        val objectParse = ObjectParse(name)
        pluginsConnect[name] = objectParse
        return pluginsConnect[name]!!
    }

    fun getLicenseBoolean(name : String, ip : String) : Boolean{
        connectServer()
        var list : ArrayList<String> = ArrayList()
        val result: ResultSet = statement?.executeQuery("SELECT * FROM license WHERE name LIKE '$name';")!!
        while (result.next()) {
            val name = result.getString("address")
            list.add(name)
        }
        closeServer()
        return list.contains(ip)
    }

    fun getLicenseIpList(name : String) : ArrayList<String>{
        connectServer()
        var list : ArrayList<String> = ArrayList()
        val result: ResultSet = statement?.executeQuery("SELECT * FROM license WHERE name LIKE '$name';")!!
        while (result.next()) {
            val name = result.getString("address")
            list.add(name)
        }
        closeServer()
        return list
    }

    override fun onDisable() {
        super.onDisable()
        logger.info("비활성화")
        configManager.configSave()
    }

    @Throws(SQLException::class, ClassNotFoundException::class)
    private fun openConnection() {
        if (connection != null && !connection?.isClosed!!) {
            return
        }
        Class.forName(Allvar.driver)
        connection = DriverManager.getConnection(
            "jdbc:mariadb://" + Allvar.host + "/" + Allvar.database,
            Allvar.username,
            Allvar.password
        )
    }
}