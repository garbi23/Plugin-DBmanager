package garbi.plugin.dbmanager.serialize

import garbi.plugin.dbmanager.allvar.Allvar.getConnection
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

import java.sql.*


class SeriallizeObject(plname : String, conn: Connection) {
    private var WRITE_OBJECT_SQL = ""

    private var READ_OBJECT_SQL = ""

    private var conn : Connection = conn

    init {
        this.WRITE_OBJECT_SQL = "INSERT INTO ${plname}(name, type, object_value) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE object_value=VALUES(object_value);"

        this.READ_OBJECT_SQL = "SELECT * FROM $plname WHERE name = ?"

        val CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS `${plname}` (\n" +
                "  `id` bigint(20) NOT NULL AUTO_INCREMENT,\n" +
                "  `name` varchar(128) CHARACTER SET ucs2 COLLATE ucs2_bin DEFAULT NULL,\n" +
                "  `type` varchar(50) CHARACTER SET ucs2 COLLATE ucs2_bin DEFAULT NULL,\n" +
                "  `object_value` blob DEFAULT NULL,\n" +
                "  PRIMARY KEY (`id`),\n" +
                "  UNIQUE KEY `name` (`name`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;"
        val pstmt: PreparedStatement = conn.prepareStatement(CREATE_TABLE_SQL)
        var rs = pstmt.executeQuery()
        rs.close()
        pstmt.close()
    }


    @Throws(Exception::class)
    fun writeJavaObject(`object`: Any, string: String) {
        if(conn.isClosed){ conn = getConnection()!!}
        val pstmt: PreparedStatement = conn.prepareStatement(WRITE_OBJECT_SQL)
        // set input parameters
        pstmt.setString(1, string)
        var bos = ByteArrayOutputStream()
        var out = BukkitObjectOutputStream(bos)
        out.writeObject(`object`)
        var byte = bos.toByteArray()
        pstmt.setString(2, `object`.javaClass.name)
        pstmt.setBytes(3, byte)
        pstmt.executeUpdate()

        // get the generated key for the id
        pstmt.close()
    }

    @Throws(Exception::class)
    fun readJavaObject(string : String): Any? {
        if(conn.isClosed){ conn = getConnection()!!}
        val pstmt: PreparedStatement = conn.prepareStatement(READ_OBJECT_SQL)
        pstmt.setString(1, string)
        var rs : ResultSet?
        try {
            rs = pstmt.executeQuery()
        }catch (e : SQLSyntaxErrorException){return null}
        if(!rs?.next()!!){return null}
        val buf = rs?.getBytes("object_value")
        var objectIn: BukkitObjectInputStream? = null
        if (buf != null){
            objectIn = BukkitObjectInputStream(ByteArrayInputStream(buf))
        }
        val `object` = objectIn?.readObject()
        rs?.close()
        pstmt.close()
        return `object`
    }
}