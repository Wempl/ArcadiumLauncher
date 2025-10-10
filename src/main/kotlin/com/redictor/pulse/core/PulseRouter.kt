package com.redictor.pulse.core

typealias PulseHandler = (PulseRequest) -> String

data class RoutePattern(
    val originalPattern: String,
    val regex: Regex,
    val parameterNames: List<String>
)

class PulseRouter {
    private val routes = mutableMapOf<String, MutableList<Pair<RoutePattern, PulseHandler>>>()
    
    init {
        listOf("GET", "POST", "PUT", "DELETE").forEach { method ->
            routes[method] = mutableListOf()
        }
    }
    
    private fun compilePathPattern(pattern: String): RoutePattern {
        val parameterNames = mutableListOf<String>()
        val regexPattern = StringBuilder()
        regexPattern.append("^")
        
        if (pattern == "/" || pattern.isEmpty()) {
            return RoutePattern(pattern, Regex("^/$"), emptyList())
        }
        
        val segments = pattern.split('/').filter { it.isNotEmpty() }
        
        if (segments.isEmpty()) {
            return RoutePattern(pattern, Regex("^/$"), emptyList())
        }
        
        regexPattern.append("/")
        
        segments.forEachIndexed { index, segment ->
            if (segment.startsWith('{') && segment.endsWith('}')) {
                val paramName = segment.removeSurrounding("{", "}")
                parameterNames.add(paramName)
                regexPattern.append("([^/]+)")
            } else {
                regexPattern.append(Regex.escape(segment))
            }
            
            if (index < segments.size - 1) {
                regexPattern.append("/")
            }
        }
        
        regexPattern.append("/*$")
        
        return RoutePattern(
            originalPattern = pattern,
            regex = Regex(regexPattern.toString()),
            parameterNames = parameterNames
        )
    }
    
    private fun addRoute(method: String, path: String, handler: PulseHandler) {
        val routePattern = compilePathPattern(path)
        routes[method]!!.add(routePattern to handler)
        println("Pulse: Registered $method $path")
    }
    
    fun get(path: String, handler: PulseHandler) = addRoute("GET", path, handler)
    fun post(path: String, handler: PulseHandler) = addRoute("POST", path, handler)
    fun put(path: String, handler: PulseHandler) = addRoute("PUT", path, handler)
    fun delete(path: String, handler: PulseHandler) = addRoute("DELETE", path, handler)
    
    fun findHandler(method: String, path: String): Pair<PulseHandler, Map<String, String>>? {
        val cleanPath = path.split('?').first()
        val methodRoutes = routes[method] ?: return null
        
        for ((routePattern, handler) in methodRoutes) {
            val matchResult = routePattern.regex.find(cleanPath)
            if (matchResult != null) {
                val parameters = extractParameters(matchResult, routePattern.parameterNames)
                return handler to parameters
            }
        }
        
        return null
    }
    
    private fun extractParameters(matchResult: MatchResult, parameterNames: List<String>): Map<String, String> {
        val parameters = mutableMapOf<String, String>()
        
        parameterNames.forEachIndexed { index, name ->
            val value = matchResult.groups[index + 1]?.value ?: ""
            parameters[name] = value
        }
        
        return parameters
    }
    
    fun printRoutes() {
        println("Pulse Routes:")
        routes.forEach { (method, patterns) ->
            patterns.forEach { (routePattern, _) ->
                println("   $method ${routePattern.originalPattern}")
            }
        }
        println(" ")
    }
}