# Plugin-DBmanager

코틀린으로 만들어진 라이브러리로 마인크래프트 플러그인개발에서 DB서버와의 통신 및 쓰기, 읽기를 자유롭게 해줍니다.

## Configuration
```yaml
db:
  driver: org.mariadb.jdbc.Driver
  host: 127.0.0.1
  database: dbname
  username: name
  password: password
```

## License Example
해당 플러그인이 라이센스가 있는지 확인합니다. 
DB서버 license 테이블에서 해당 플러그인 이름에 ip값이 있을경우 true를 리턴합니다.
```kotlin
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
```

```kotlin
        if(!getLicenseBoolean(this.name, ip)){
          //라이센스가 없음
        }else{
          //라이센스가 있음
        }
```

## Create Table

```kotlin
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
```

## Write Example
해당 오브젝트를 Serialize시켜 DB서버에 BLOB형태로 업데이트 해줍니다.

```kotlin
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
```

```kotlin
  var objectpare = ObjectParse("pluginname") //해당 플러그인 이름이 테이블 이름으로 생성됨
  objectparse.setObject("name", object)
```

## Read Example
해당 BLOB를 스트림해 object형태로 .

```kotlin
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
```

```kotlin
  var objectpare = ObjectParse("pluginname") //해당 플러그인 이름이 테이블 이름으로 생성됨
  objectparse.getObject("name")
```
