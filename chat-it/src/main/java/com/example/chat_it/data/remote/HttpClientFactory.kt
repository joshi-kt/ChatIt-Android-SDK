package com.example.chat_it.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import com.example.chat_it.util.Logger as ChatItLogger
import kotlin.time.Duration

internal object HttpClientFactory {

    fun create(engine: HttpClientEngine): HttpClient {
        return HttpClient(engine) {
            install(Logging) {
                level = if (ChatItLogger.isLogsEnabled()) LogLevel.ALL else LogLevel.NONE
                logger = Logger.ANDROID
            }
            install(ContentNegotiation) {
                json(
                    json = Json {
                        ignoreUnknownKeys = true
                    }
                )
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 20000
                connectTimeoutMillis = 10000
                socketTimeoutMillis = 10000
            }
            defaultRequest {
                contentType(ContentType.Application.Json)
            }
        }
    }
}