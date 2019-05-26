package com.mrfox.pupptmstr.authservice

import com.mrfox.pupptmstr.authservice.models.AdditionalModel
import com.mrfox.pupptmstr.authservice.models.AuthModel
import com.mrfox.pupptmstr.authservice.models.ErrorModel
import com.mrfox.pupptmstr.authservice.models.RegistrationModel
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
import io.ktor.request.authorization
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import java.sql.SQLException

const val DB_URL = "jdbc:postgresql://127.0.0.1:5432/studcompi"
const val USER = "postgres"
const val PASS = "qazwsx"
const val KEY = "aUstdOnGentRiBumPOsaUstdOnGentRiBumPOsaUstdOnGentRiBumPOs"

fun main() {

    embeddedServer(Netty, 9991) {
        install(ContentNegotiation) {
            gson {
                setPrettyPrinting()
                serializeNulls()
            }
        }

        //недопустимые символы - 400 bad request {status: 400, error:”Illegal character”, message:"Illegal characters inlogin/password", path:”/auth”}
        //не прошел авторизацию - 401 Unauthorized {status: 401, error:"Unauthorized", message: "Wrong login or password", path:"/auth"}
        routing {
            post("/auth") {
                val user = call.receive<AuthModel>()
                if (checkFormat(user.username) && checkFormat(user.password)) {
                    if (auth(user.username, user.password)) {
                        try {
                            call.respond(HttpStatusCode.OK, makeCorrectUserModel(user.username))
                        } catch (e: SQLException) {
                            call.respond(HttpStatusCode.InternalServerError)
                        }
                    } else {
                        call.respond(HttpStatusCode.Unauthorized, makeUnauthorizedLoginResponseMessage())
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest, makeBadResponseMessage())
                }

            }

            post("/reg") {
                val user = call.receive<RegistrationModel>()
                if (
                    checkFormat(user.fullName) &&
                    checkFormat(user.mail) &&
                    checkFormat(user.password) &&
                    checkFormat(user.username) &&
                    checkFormat(user.role)) {
                    if (reg(user.username, user.password, user.fullName, user.mail, user.role)) {
                        call.respond(HttpStatusCode.OK, makeCorrectUserModel(user.username))
                    } else {
                        call.respond(HttpStatusCode.Conflict, makeUsernameOrMailAlreadyExistMessage())
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest, makeBadResponseMessage())
                }
            }

            post("/update_jwt") {
                val token = call.request.authorization()
                if (token != null) {
                    if (validateJws(token)) {
                        call.respond(HttpStatusCode.OK, makeNewJwt(token))
                    } else {
                        call.respond(HttpStatusCode.Unauthorized, makeUnauthorizedJWTResponseMessage())
                    }
                } else {
                    call.respond(HttpStatusCode.Unauthorized, makeNoTokenInHeaderMessage())
                }
            }

            post("update_pass") {
                val user = call.receive<AdditionalModel>()
            }

            post("forgot_pass") {
                val user = call.receive<AdditionalModel>()
            }
        }
    }.start(wait = true)

}

fun checkFormat(text: String): Boolean {
    return Regex("[A-Za-z0-9_ \\-]+").matches(text)
}

fun makeBadResponseMessage() = ErrorModel(status = 400,
    error = "Illegal characters",
    message = "Illegal characters in login/password",
    path = "/auth")

fun makeUnauthorizedLoginResponseMessage() = ErrorModel(status = 401,
    error = "Unauthorized",
    message = "Wrong login or password",
    path = "/auth")

fun makeUnauthorizedJWTResponseMessage() = ErrorModel(status = 401,
    error = "Unauthorized",
    message = "Wrong Jwt, auth again by /auth",
    path = "/update_jwt")

fun makeNoTokenInHeaderMessage() = ErrorModel(status = 401,
    error = "Unauthorized",
    message = "No token in header provided",
    path = "/update_jwt")

fun makeUsernameOrMailAlreadyExistMessage() = ErrorModel(status = 409,
    error = "Conflict",
    message = "User with such username or mail already exist. Choose another one.",
    path = "/reg")