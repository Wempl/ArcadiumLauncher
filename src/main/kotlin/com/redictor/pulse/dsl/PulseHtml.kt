package com.redictor.pulse.dsl

sealed class PulseElement {
    abstract fun render(): String
}

class PulseText(private val content: String) : PulseElement() {
    override fun render(): String = content
}

class PulseDiv(private val className: String, private val children: List<PulseElement>) : PulseElement() {
    override fun render(): String {
        val classAttr = if (className.isNotEmpty()) " class=\"$className\"" else ""
        val childrenHtml = children.joinToString("") { it.render() }
        return "<div$classAttr>$childrenHtml</div>"
    }
}

class PulseHeading(private val level: Int, private val text: String, private val className: String) : PulseElement() {
    override fun render(): String {
        val classAttr = if (className.isNotEmpty()) " class=\"$className\"" else ""
        return "<h${level}$classAttr>$text</h${level}>"
    }
}

class PulseParagraph(private val text: String, private val className: String) : PulseElement() {
    override fun render(): String {
        val classAttr = if (className.isNotEmpty()) " class=\"$className\"" else ""
        return "<p$classAttr>$text</p>"
    }
}

class PulseButton(private val text: String, private val className: String, private val onClick: () -> Unit) : PulseElement() {
    override fun render(): String {
        val classAttr = if (className.isNotEmpty()) " class=\"$className\"" else ""
        return "<button$classAttr>$text</button>"
    }
}

class PulseLink(private val href: String, private val text: String, private val className: String) : PulseElement() {
    override fun render(): String {
        val classAttr = if (className.isNotEmpty()) " class=\"$className\"" else ""
        return "<a href=\"$href\"$classAttr>$text</a>"
    }
}

class PulseDivBuilder {
    val children = mutableListOf<PulseElement>()
    
    fun text(content: String) {
        children.add(PulseText(content))
    }
    
    fun p(text: String, className: String = "") {
        children.add(PulseParagraph(text, className))
    }
    
    fun h1(text: String, className: String = "") {
        children.add(PulseHeading(1, text, className))
    }
    
    fun button(text: String, className: String = "", onClick: () -> Unit = {}) {
        children.add(PulseButton(text, className, onClick))
    }
    
    fun a(href: String, text: String, className: String = "") {
        children.add(PulseLink(href, text, className))
    }
}

class PulseHtmlBuilder {
    private val elements = mutableListOf<PulseElement>()
    
    fun div(className: String = "", block: PulseDivBuilder.() -> Unit = {}) {
        val divBuilder = PulseDivBuilder().apply(block)
        elements.add(PulseDiv(className, divBuilder.children))
    }
    
    fun h1(text: String, className: String = "") {
        elements.add(PulseHeading(1, text, className))
    }
    
    fun h2(text: String, className: String = "") {
        elements.add(PulseHeading(2, text, className))
    }
    
    fun p(text: String, className: String = "") {
        elements.add(PulseParagraph(text, className))
    }
    
    fun button(text: String, className: String = "", onClick: () -> Unit = {}) {
        elements.add(PulseButton(text, className, onClick))
    }
    
    fun a(href: String, text: String, className: String = "") {
        elements.add(PulseLink(href, text, className))
    }
    
    fun text(content: String) {
        elements.add(PulseText(content))
    }
    
    fun build(): String {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Pulse Framework</title>
            </head>
            <body>
                ${elements.joinToString("") { it.render() }}
            </body>
            </html>
        """.trimIndent()
    }
}

fun pulseHtml(block: PulseHtmlBuilder.() -> Unit): String {
    return PulseHtmlBuilder().apply(block).build()
}
