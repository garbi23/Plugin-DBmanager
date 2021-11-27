package garbi.plugin.dbmanager.parse

import garbi.plugin.dbmanager.allvar.Allvar.getConnection
import garbi.plugin.dbmanager.serialize.SeriallizeObject
import java.sql.Connection

class ObjectParse(name : String) {
    private var connect : Connection? = null
    var ser : SeriallizeObject? = null

    init {
        connect = getConnection()
        ser = SeriallizeObject(name, connect!!)
    }

    fun setObject(varname : String, `object`: Any){
        ser?.writeJavaObject(`object` ,varname)
    }

    fun getObject(varname : String) : Any?{
        return ser?.readJavaObject(varname)
    }
}