package com.redictor.pulse.server

import com.redictor.pulse.core.PulseRequest
import com.redictor.pulse.core.PulseRouter
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.Executors

class PulseServer(private val port: Int = 8080) {
    private var running = false
    private lateinit var serverSocket: ServerSocket
    private val routers = mutableListOf<PulseRouter>()
    private val threadPool = Executors.newFixedThreadPool(10)
    
    fun addRouter(router: PulseRouter) {
        routers.add(router)
    }
    
    fun start() {
        serverSocket = ServerSocket(port)
        running = true
        
        println("PULSE FRAMEWORK v0.2.0")
        println("Created by redictor")
        println()
        println("Server started on http://localhost:$port")
        println("Listening for connections...")
        println()
        
        while (running) {
            try {
                val clientSocket = serverSocket.accept()
                threadPool.submit {
                    handlePulseClient(clientSocket)
                }
            } catch (e: Exception) {
                if (running) println("Pulse Server error: ${e.message}")
            }
        }
    }
    
    fun stop() {
        running = false
        serverSocket.close()
        threadPool.shutdown()
        println("Pulse Server stopped")
    }
    
    private fun handlePulseClient(client: Socket) {
        try {
            val input = BufferedReader(InputStreamReader(client.getInputStream()))
            val output = client.getOutputStream()
            
            val request = parsePulseRequest(input)
            println("${request.method} ${request.path}")
            
            val response = findPulseHandler(request)
            
            output.write(response.toByteArray(Charsets.UTF_8))
            output.flush()
        } catch (e: Exception) {
            println("Pulse Client error: ${e.message}")
        } finally {
            client.close()
        }
    }
    
    private fun parsePulseRequest(input: BufferedReader): PulseRequest {
        val requestLine = input.readLine() ?: ""
        val parts = requestLine.split(" ")
        
        if (parts.size < 2) return PulseRequest("GET", "/")
        
        val method = parts[0]
        val fullPath = parts[1]
        
        val headers = mutableMapOf<String, String>()
        var line: String
        while (input.readLine().also { line = it } != null && line.isNotEmpty()) {
            val headerParts = line.split(":", limit = 2)
            if (headerParts.size == 2) headers[headerParts[0].trim()] = headerParts[1].trim()
        }
        
        val request = PulseRequest(method, fullPath, headers)
        parseQueryParameters(fullPath, request)
        return request
    }
    
    private fun parseQueryParameters(fullPath: String, request: PulseRequest) {
        val pathParts = fullPath.split("?")
        if (pathParts.size > 1) {
            val queryString = pathParts[1]
            queryString.split("&").forEach { param ->
                val keyValue = param.split("=")
                if (keyValue.size == 2) request.queryParams[keyValue[0]] = keyValue[1]
                else if (keyValue.size == 1) request.queryParams[keyValue[0]] = ""
            }
        }
    }
    
    private fun findPulseHandler(request: PulseRequest): String {
        for (router in routers) {
            val result = router.findHandler(request.method, request.path)
            if (result != null) {
                val (handler, pathParams) = result
                request.pathParams.putAll(pathParams)
                
                val responseBody = handler(request)
                val contentType = if (request.path == "/css") "text/css" else "text/html"
                
                return buildPulseResponse(responseBody, 200, contentType)
            }
        }
        
        return buildPulseResponse("""
            <!DOCTYPE html>
            <html>
            <head><title>404</title></head>
            <body><h1>404 Not Found</h1></body>
            </html>
        """.trimIndent(), 404, "text/html")
    }
    
    private fun buildPulseResponse(body: String, statusCode: Int = 200, contentType: String = "text/html"): String {
        val statusText = when (statusCode) {
            200 -> "OK"
            404 -> "Not Found"
            else -> "Unknown"
        }
        
        return """HTTP/1.1 $statusCode $statusText
Content-Type: $contentType
Content-Length: ${body.toByteArray(Charsets.UTF_8).size}
Connection: close

$body"""
    }
}
