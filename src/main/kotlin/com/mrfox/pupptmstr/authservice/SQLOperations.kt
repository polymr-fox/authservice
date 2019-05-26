package com.mrfox.pupptmstr.authservice

import com.mrfox.pupptmstr.authservice.models.UserModel
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
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
            println("оно не пустое")
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

fun reg(username: String, password: String, fullName: String) {

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

    println("я делаю модель")

    try {
        val resSet: ResultSet = statement.executeQuery("select user_id from users where username='$username';")
        println("ищу user_id")
        if (resSet.next()) {
            userId = resSet.getInt("user_id")
        }
    } catch (e: SQLException) {
        e.printStackTrace()
        println("не нашел")
        throw e
    }


    try {
        val resSet: ResultSet = statement.executeQuery("select fullname from users where username='$username';")
        println("ищу fullname")
        if (resSet.next()) {
            fullName = resSet.getString("fullname")
        }
    } catch (e: SQLException) {
        e.printStackTrace()
        println("не нашел")
        throw e
    }

    try {
        val resSet: ResultSet = statement.executeQuery("select mail from users where username='$username';")
        println("ищу mail")

        if (resSet.next()) {
            mail = resSet.getString("mail")
        }
    } catch (e: SQLException) {
        e.printStackTrace()
        println("не нашел")
        throw e
    }

    try {
        val resSet: ResultSet = statement.executeQuery("select links from users where username='$username';")
        println("ищу links")

        if (resSet.next()) {
            links = resSet.getString("links").split(", ")
        }
    } catch (e: SQLException) {
        e.printStackTrace()
        println("не нашел")
        throw e
    } catch (e: Exception) {
        e.printStackTrace()

    }

    try {
        val resSet: ResultSet = statement.executeQuery("select role from users where username='$username';")
        println("ищу role")

        if (resSet.next()) {
            role = resSet.getString("role")
        }
    } catch (e: SQLException) {
        e.printStackTrace()
        println("не нашел")
        throw e
    }

    try {
        val resSet: ResultSet = statement.executeQuery("select cantags from users where username='$username';")
        println("ищу cantags")

        if (resSet.next()) {
            canTags = resSet.getString("cantags").split(", ")
        }
    } catch (e: SQLException) {
        e.printStackTrace()
        println("не нашел")
        throw e
    } catch (e: Exception) {
        e.printStackTrace()

    }

    try {
        val resSet: ResultSet = statement.executeQuery("select lovetags from users where username='$username';")
        println("ищу lovetags")

        if (resSet.next()) {
            loveTags = resSet.getString("lovetags").split(", ")
        }
    } catch (e: SQLException) {
        e.printStackTrace()
        println("не нашел")
        throw e
    } catch (e: Exception) {
        e.printStackTrace()

    }

    val token = createToken(username, role)

    println("сделал токен")
    println(token)

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

fun createToken(username: String, role: String): String {
    println("я делаю токен")
    try {
        val now = Date()
        println("делаю дату")
        val validity = Date(now.time + validityInMilliseconds)
        val keyBytes = KEY.toByteArray()
        val key = Keys.hmacShaKeyFor(keyBytes)
        println("делаю ключ")
        return Jwts.builder()
            .setHeaderParam("alg", "HS256")
            .setIssuer("mrfox")
            .setSubject(username)
            .claim("role", role)
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(SignatureAlgorithm.HS256, KEY)
            .compact()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return ""
}
