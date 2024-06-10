package com.example

import com.example.plugins.configureRouting
import com.example.plugins.configureSecurity
import com.example.plugins.configureSerialization
import com.example.repository.DatabaseFactory
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {

    DatabaseFactory.init()

    configureSecurity()
    configureSerialization()
    configureRouting()
}