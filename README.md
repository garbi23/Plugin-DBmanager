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
