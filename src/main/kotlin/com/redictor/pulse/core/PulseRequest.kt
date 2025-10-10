package com.redictor.pulse.core

class PulseRequest(
    val method: String,
    val path: String, 
    val headers: Map<String, String> = emptyMap(),
    val body: String = ""
) {
    val pathParams = mutableMapOf<String, String>()
    val queryParams = mutableMapOf<String, String>()
    
    // Path parameter methods
    fun pathParam(key: String): String? = pathParams[key]
    fun pathParamInt(key: String): Int? = pathParams[key]?.toIntOrNull()
    fun pathParamLong(key: String): Long? = pathParams[key]?.toLongOrNull()
    
    // Query parameter methods  
    fun queryParam(key: String): String? = queryParams[key]
    fun queryParamInt(key: String): Int? = queryParams[key]?.toIntOrNull()
    fun queryParamLong(key: String): Long? = queryParams[key]?.toLongOrNull()
    
    // Required parameters
    fun requirePathParam(key: String): String = pathParam(key) 
        ?: throw IllegalArgumentException("Missing required path parameter: $key")
        
    fun requirePathParamInt(key: String): Int = pathParamInt(key) 
        ?: throw IllegalArgumentException("Invalid or missing path parameter: $key")
}