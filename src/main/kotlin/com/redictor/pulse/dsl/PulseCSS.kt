package com.redictor.pulse.dsl

class PulseCSSBuilder {
    private val rules = mutableListOf<String>()
    
    fun rule(selector: String, block: CSSRuleBuilder.() -> Unit) {
        val ruleBuilder = CSSRuleBuilder().apply(block)
        rules.add("$selector { ${ruleBuilder.properties.joinToString(" ")} }")
    }
    
    fun build(): String = rules.joinToString("\n")
}

class CSSRuleBuilder {
    val properties = mutableListOf<String>()
    
    infix fun String.to(value: String) {
        properties.add("$this: $value;")
    }
    
    // Shortcut methods
    fun margin(value: String) = properties.add("margin: $value;")
    fun padding(value: String) = properties.add("padding: $value;")
    fun color(value: String) = properties.add("color: $value;")
    fun background(value: String) = properties.add("background: $value;")
    fun backgroundColor(value: String) = properties.add("background-color: $value;")
    fun fontSize(value: String) = properties.add("font-size: $value;")
    fun fontWeight(value: String) = properties.add("font-weight: $value;")
    fun display(value: String) = properties.add("display: $value;")
    fun width(value: String) = properties.add("width: $value;")
    fun height(value: String) = properties.add("height: $value;")
    fun border(value: String) = properties.add("border: $value;")
    fun borderRadius(value: String) = properties.add("border-radius: $value;")
    fun boxShadow(value: String) = properties.add("box-shadow: $value;")
    fun textAlign(value: String) = properties.add("text-align: $value;")
    fun textDecoration(value: String) = properties.add("text-decoration: $value;")
    fun position(value: String) = properties.add("position: $value;")
    fun top(value: String) = properties.add("top: $value;")
    fun left(value: String) = properties.add("left: $value;")
    fun right(value: String) = properties.add("right: $value;")
    fun bottom(value: String) = properties.add("bottom: $value;")
    fun zIndex(value: String) = properties.add("z-index: $value;")
    fun flex(value: String) = properties.add("flex: $value;")
    fun justifyContent(value: String) = properties.add("justify-content: $value;")
    fun alignItems(value: String) = properties.add("align-items: $value;")
    fun flexDirection(value: String) = properties.add("flex-direction: $value;")
    fun gridTemplateColumns(value: String) = properties.add("grid-template-columns: $value;")
    fun gap(value: String) = properties.add("gap: $value;")
    fun maxWidth(value: String) = properties.add("max-width: $value;")
    fun minHeight(value: String) = properties.add("min-height: $value;")
    fun overflow(value: String) = properties.add("overflow: $value;")
    fun cursor(value: String) = properties.add("cursor: $value;")
    fun transition(value: String) = properties.add("transition: $value;")
    fun transform(value: String) = properties.add("transform: $value;")
    fun opacity(value: String) = properties.add("opacity: $value;")
    fun lineHeight(value: String) = properties.add("line-height: $value;")
    fun fontFamily(value: String) = properties.add("font-family: $value;")
    fun listStyle(value: String) = properties.add("list-style: $value;")
    fun objectFit(value: String) = properties.add("object-fit: $value;")
}

// DSL function
fun pulseCss(block: PulseCSSBuilder.() -> Unit): String {
    return PulseCSSBuilder().apply(block).build()
}