package com.redictor.pulse

import com.redictor.pulse.core.PulseRouter
import com.redictor.pulse.server.PulseServer

class Pulse(private var _port: Int = 8080) {
    private lateinit var server: PulseServer  // ← ОТКЛАДЫВАЕМ создание!
    private val router = PulseRouter()
    
    // Публичное свойство для пользователя
    var port: Int = _port
        set(value) {
            field = value
            // При изменении порта пересоздаём сервер
            server = PulseServer(value)
            server.addRouter(router)
        }
    
    init {
        // Инициализируем сервер с начальным портом
        server = PulseServer(_port)
        server.addRouter(router)
    }
    
    fun get(path: String, handler: (request: com.redictor.pulse.core.PulseRequest) -> String) {
        router.get(path, handler)
    }
    
    fun post(path: String, handler: (request: com.redictor.pulse.core.PulseRequest) -> String) {
        router.post(path, handler)
    }
    
    fun put(path: String, handler: (request: com.redictor.pulse.core.PulseRequest) -> String) {
        router.put(path, handler)
    }
    
    fun delete(path: String, handler: (request: com.redictor.pulse.core.PulseRequest) -> String) {
        router.delete(path, handler)
    }
    
    fun start() {
        println("Starting Pulse Framework...")
        router.printRoutes()
        server.start()  // ← сервер будет с правильным портом!
    }
    
    // HTML DSL функции без изменений
    fun html(block: () -> String): String {
        return "<!DOCTYPE html><html>${block()}</html>"
    }

    fun head(block: () -> String): String {
        return "<head>${block()}</head>"
    }

    fun body(block: () -> String): String {
        return "<body>${block()}</body>"
    }

    fun title(text: String): String {
        return "<title>$text</title>"
    }

    fun h1(text: String): String {
        return "<h1>$text</h1>"
    }

    fun p(text: String): String {
        return "<p>$text</p>"
    }

    fun ok(text: String): String {
        return text
    }

    fun json(text: String): String {
        return text
    }
}

fun pulse(port: Int = 8080, block: Pulse.() -> Unit): Pulse {
    return Pulse(port).apply(block)
}