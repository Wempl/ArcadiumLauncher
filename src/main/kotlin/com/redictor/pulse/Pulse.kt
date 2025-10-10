package com.redictor.pulse

import com.redictor.pulse.core.PulseRouter
import com.redictor.pulse.server.PulseServer
import com.redictor.pulse.dsl.PulseCSSBuilder

class Pulse(private var _port: Int = 8080) {
    private lateinit var server: PulseServer
    private val router = PulseRouter()
    private var globalCSS: String = "" // Храним глобальный CSS
    
    var port: Int = _port
        set(value) {
            field = value
            server = PulseServer(value)
            server.addRouter(router)
        }
    
    init {
        server = PulseServer(_port)
        server.addRouter(router)
    }
    
    // CSS DSL функция
    fun css(block: PulseCSSBuilder.() -> Unit) {
        val builder = PulseCSSBuilder()
        builder.block()
        globalCSS = builder.build()
        
        // Автоматически регистрируем маршрут для CSS
        router.get("/css") { 
            globalCSS 
        }
    }
    
    // Остальные методы без изменений
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
        println(" ")
        println("Starting Pulse Framework...")
        router.printRoutes()
        server.start()
    }
    
    // HTML DSL функции
    fun html(block: () -> String): String = "<!DOCTYPE html><html>${block()}</html>"
    fun head(block: () -> String): String = "<head>${block()}</head>"
    fun body(block: () -> String): String = "<body>${block()}</body>"
    fun title(text: String): String = "<title>$text</title>"
    fun h1(text: String): String = "<h1>$text</h1>"
    fun p(text: String): String = "<p>$text</p>"
}

fun pulse(port: Int = 8080, block: Pulse.() -> Unit): Pulse {
    return Pulse(port).apply(block)
}