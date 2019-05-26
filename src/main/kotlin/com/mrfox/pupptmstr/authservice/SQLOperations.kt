package com.mrfox.pupptmstr.authservice

import com.mrfox.pupptmstr.authservice.models.UserModel
import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import java.lang.Exception
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.SQLException
import java.util.*

const val validityInMilliseconds = 86400000

fun auth(username: String, password: String): Boolean {
    var res = "error"

    try {
        Class.forName("org.postgresql.Driver")
        val connection = DriverManager.getConnection(DB_URL, USER, PASS)
        val statement = connection.createStatement()
        println("connected")
        println("username = $username, password = $password")

    try {
        val resSet: ResultSet =
            statement.executeQuery("select * from users where username='$username' AND password='$password' AND delete_at IS NULL;")
        println("query executed")
        if (resSet.next()) {
            res = resSet.getInt("user_id").toString()
            println(res)
        }
    } catch (e: SQLException) {
        statement.close()
        connection.close()
        return false
    }
    statement.close()
    connection.close()
    return (res != "error")
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return false
}

fun reg(username: String, password: String, fullName: String, mail: String, role: String): Boolean {
    return try {
        Class.forName("org.postgresql.Driver")
        val connection = DriverManager.getConnection(DB_URL, USER, PASS)
        val statement = connection.createStatement()
        val date = java.time.LocalDate.now().toString()

        return try {
            statement.execute("insert into users (username, password, fullname, mail, role, create_at, update_at) values ('$username', '$password', '$fullName', '$mail', '$role', '$date', '$date');")
            statement.close()
            connection.close()
            true
        } catch (e: SQLException) {
            e.printStackTrace()
            statement.close()
            connection.close()
            false
        }
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

fun makeCorrectUserModel(username: String): UserModel {

    Class.forName("org.postgresql.Driver")
    val connection = DriverManager.getConnection(DB_URL, USER, PASS)
    val statement = connection.createStatement()

    var userId = 0
    val userName = username
    var fullName = ""
    var mail = ""
    var links: List<String> = listOf()
    var role = ""
    var canTags: List<String> = listOf()
    var loveTags: List<String> = listOf()

    try {
        val resSet: ResultSet = statement.executeQuery("select user_id from users where username='$username';")
        if (resSet.next()) {
            userId = resSet.getInt("user_id")
        }
    } catch (e: SQLException) {
        e.printStackTrace()
        throw e
    }


    try {
        val resSet: ResultSet = statement.executeQuery("select fullname from users where username='$username';")
        if (resSet.next()) {
            fullName = resSet.getString("fullname")
        }
    } catch (e: SQLException) {
        e.printStackTrace()
        throw e
    }

    try {
        val resSet: ResultSet = statement.executeQuery("select mail from users where username='$username';")

        if (resSet.next()) {
            mail = resSet.getString("mail")
        }
    } catch (e: SQLException) {
        e.printStackTrace()
        throw e
    }

    try {
        val resSet: ResultSet = statement.executeQuery("select links from users where username='$username';")

        if (resSet.next()) {
            links = resSet.getString("links").split(", ")
        }
    } catch (e: SQLException) {
        e.printStackTrace()
        throw e
    } catch (e: Exception) {
        e.printStackTrace()

    }

    try {
        val resSet: ResultSet = statement.executeQuery("select role from users where username='$username';")

        if (resSet.next()) {
            role = resSet.getString("role")
        }
    } catch (e: SQLException) {
        e.printStackTrace()
        throw e
    }

    try {
        val resSet: ResultSet = statement.executeQuery("select cantags from users where username='$username';")

        if (resSet.next()) {
            canTags = resSet.getString("cantags").split(", ")
        }
    } catch (e: SQLException) {
        e.printStackTrace()
        throw e
    } catch (e: Exception) {
        e.printStackTrace()

    }

    try {
        val resSet: ResultSet = statement.executeQuery("select lovetags from users where username='$username';")

        if (resSet.next()) {
            loveTags = resSet.getString("lovetags").split(", ")
        }
    } catch (e: SQLException) {
        e.printStackTrace()
        throw e
    } catch (e: Exception) {
        e.printStackTrace()

    }

    val token = createToken(username, role)

    println("token: $token")

    statement.close()
    connection.close()

    return UserModel(
        userId,
        userName,
        fullName,
        mail,
        links,
        role,
        canTags,
        loveTags,
        token
    )

}

fun validateJws(token: String): Boolean {
    val jws: Jws<Claims>
    val key = Keys.hmacShaKeyFor(KEY.toByteArray())
    return try {
        jws = Jwts.parser().requireIssuer("com.mrfox").setSigningKey(key).parseClaimsJws(token)
        true
    } catch (e: JwtException) {
        e.printStackTrace()
        false
    }
}

fun makeNewJwt(token: String): String {
    val claims: Jws<Claims>
    val key = Keys.hmacShaKeyFor(KEY.toByteArray())
    return try {
        claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token)
        val username = claims.body.subject
        val role = claims.body["role"].toString()
        createToken(username, role)
    } catch (e: JwtException) {
        e.printStackTrace()
        ""
    }
}

fun createToken(username: String, role: String): String {
    try {
        val now = Date()
        val validity = Date(now.time + validityInMilliseconds)
        val keyBytes = KEY.toByteArray()
        val key = Keys.hmacShaKeyFor(keyBytes)
        return Jwts.builder()
            .setHeaderParam("alg", "HS256")
            .setIssuer("com.mrfox")
            .setSubject(username)
            .claim("role", role)
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(key)
            .compact()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return ""
}
