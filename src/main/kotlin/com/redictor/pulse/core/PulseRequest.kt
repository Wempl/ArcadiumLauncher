package com.redictor.pulse.core

data class PulseRequest(
    val method: String,
    val path: String,
    val headers: Map<String, String> = emptyMap(),
    val body: String = ""
)   
