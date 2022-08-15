package com.horsefarmer.asm.internal.extension

import org.gradle.api.Project

open class AsmExtension {
    var incremental: Boolean = true
    var whiteClassArray: Array<String>? = null
    var excludeClassArray: Array<String>? = null
    var replaceSuperProps: Array<String>? = null

    fun incremental(incremental: Boolean) {
        this.incremental = incremental
    }

    fun whiteClassArray(whiteClassArray: Array<String>) {
        this.whiteClassArray = whiteClassArray
    }

    fun excludeClassArray(excludeClassArray: Array<String>) {
        this.excludeClassArray = excludeClassArray
    }

    fun replaceSuperProps(replaceSuperProps: Array<String>) {
        this.replaceSuperProps = replaceSuperProps;
    }

    companion object {
        fun get(project: Project): AsmExtension = project.extensions.getByType(AsmExtension::class.java)

        fun obtainReplaceSuperProps(project: Project): Map<String, String> {
            val replaceSuperProps = get(project).replaceSuperProps
            return mutableMapOf<String, String>().apply {
                replaceSuperProps?.forEach { propValue ->
                    val propList = propValue.split("/")
                    if (propList.size >= 2) {
                        this[propList[0].replace(".", "/")] = propList[1].replace(".", "/")
                    }
                }
            }
        }
    }
}
