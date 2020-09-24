package com.horsefarmer.asm.internal.extension

import org.gradle.api.Project

open class AsmExtension {
    var incremental: Boolean = true
    var excludeClassArray: Array<String>? = null

    fun incremental(incremental: Boolean) {
        this.incremental = incremental
    }

    fun excludeClassArray(excludeClassArray: Array<String>) {
        this.excludeClassArray = excludeClassArray
    }

    companion object {
        fun get(project: Project): AsmExtension = project.extensions.getByType(AsmExtension::class.java)
    }
}
