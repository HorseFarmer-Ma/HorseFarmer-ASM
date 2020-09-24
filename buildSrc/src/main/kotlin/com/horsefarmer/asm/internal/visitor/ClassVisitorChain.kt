package com.horsefarmer.asm.internal.visitor

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter

/**
 * ClassVisitorChain 调用链
 */
internal class ClassVisitorChain(private var cw: ClassWriter) {

    private var cv: ClassVisitor? = null
    private var isFilter = false

    fun addVisitor(callback: (ClassVisitor) -> ClassVisitor): ClassVisitorChain {
        if (!isFilter) {
            this.cv = callback(this.cv ?: cw)
        }
        return this
    }

    fun filterWhen(filterFunction: () -> Boolean): ClassVisitorChain {
        isFilter = filterFunction.invoke()
        return this
    }

    fun obtain(): ClassVisitor = checkNotNull(this.cv) { "Please call addVisitor first!" }
}