package com.redictor.pulse.server

import com.redictor.pulse.core.PulseRequest
import com.redictor.pulse.core.PulseRouter
import java.io.BufferedReader
import java.net.ServerSocket
import java.net.Socket

class PulseServer(private val port: Int = 8080) {
    private var running = false
    private lateinit var serverSocket: ServerSocket
    private val routers = mutableListOf<PulseRouter>()
    
    fun addRouter(router: PulseRouter) {
        routers.add(router)
    }
    
    fun start() {
        serverSocket = ServerSocket(port)
        running = true
        
        println("PULSE FRAMEWORK v0.1.0")
        println("Created by redictor")
        println(" ")
        println("Server started on http://localhost:$port")
        println("Listening for connections...")
        println(" ")
        
        while (running) {
            try {
                val clientSocket = serverSocket.accept()
                Thread { 
                    handlePulseClient(clientSocket) 
                }.start()
            } catch (e: Exception) {
                if (running) {
                    println("Pulse Server error: ${e.message}")
                }
            }
        }
    }
    
    fun stop() {
        running = false
        serverSocket.close()
        println("Pulse Server stopped")
    }
    
    private fun handlePulseClient(client: Socket) {
        try {
            val input = client.getInputStream().bufferedReader()
            val output = client.getOutputStream()
            
            val request = parsePulseRequest(input)
            println("${request.method} ${request.path}")
            
            val response = findPulseHandler(request)
            
            output.write(response.toByteArray())
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
        
        if (parts.size < 2) {
            return PulseRequest("GET", "/")
        }
        
        val method = parts[0]
        val path = parts[1]
        
        val headers = mutableMapOf<String, String>()
        var line: String
        while (input.readLine().also { line = it } != null && line.isNotEmpty()) {
            val headerParts = line.split(":", limit = 2)
            if (headerParts.size == 2) {
                headers[headerParts[0].trim()] = headerParts[1].trim()
            }
        }
        
        return PulseRequest(method, path, headers)
    }
    
    private fun findPulseHandler(request: PulseRequest): String {
        for (router in routers) {
            val handler = router.findHandler(request.method, request.path)
            if (handler != null) {
                return buildPulseResponse(handler.invoke(request))
            }
        }
        
        return buildPulseResponse("""
            <!DOCTYPE html>
            <html>
            <head>
                <title>404 - Pulse Framework</title>
            </head>
            <body>
                <h1>404 - Pulse Not Found</h1>
                <p>No handler for ${request.method} ${request.path}</p>
                <p><a href="/">Back to Pulse Home</a></p>
            </body>
            </html>
        """.trimIndent(), 404)
    }
    
    private fun buildPulseResponse(body: String, statusCode: Int = 200): String {
        val statusText = when (statusCode) {
            200 -> "OK"
            404 -> "Not Found"
            500 -> "Internal Server Error"
            else -> "Unknown"
        }
        
        return """
HTTP/1.1 $statusCode $statusText
Content-Type: text/html; charset=utf-8
Content-Length: ${body.length}
Connection: close
X-Powered-By: Pulse/0.1.0

$body
        """.trimIndent()
    }
}
