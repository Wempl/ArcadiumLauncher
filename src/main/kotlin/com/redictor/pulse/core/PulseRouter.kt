package com.redictor.pulse.core

// ОСТАВИТЬ ЭТУ СТРОКУ:
typealias PulseHandler = (PulseRequest) -> String

class PulseRouter {
    private val routes = mutableMapOf<String, MutableMap<String, PulseHandler>>()
    
    init {
        listOf("GET", "POST", "PUT", "DELETE").forEach { method ->
            routes[method] = mutableMapOf()
        }
    }
    
    fun get(path: String, handler: PulseHandler) {
        routes["GET"]!![path] = handler
        println("Pulse: Registered GET $path")
    }
    
    fun post(path: String, handler: PulseHandler) {
        routes["POST"]!![path] = handler
        println("Pulse: Registered POST $path")
    }
    
    fun put(path: String, handler: PulseHandler) {
        routes["PUT"]!![path] = handler
        println("Pulse: Registered PUT $path")
    }
    
    fun delete(path: String, handler: PulseHandler) {
        routes["DELETE"]!![path] = handler
        println("Pulse: Registered DELETE $path")
    }
    
    fun findHandler(method: String, path: String): PulseHandler? {
        val cleanPath = path.split("?").first()
        return routes[method]?.get(cleanPath)
    }
    
    fun printRoutes() {
        println("Pulse Routes:")
        routes.forEach { (method, paths) ->
            paths.keys.forEach { path ->
                println("   $method $path")
            }
        }
    }
}
