package com.horsefarmer.asm.internal.transform

import com.horsefarmer.asm.internal.extension.AsmExtension
import com.horsefarmer.asm.internal.visitor.ClassVisitorChain
import com.horsefarmer.asm.internal.visitor.OnTestClassVisitor
import com.horsefarmer.asm.internal.visitor.SuperClassReplaceVisitor
import org.gradle.api.Project

internal class AsmTransformImpl(project: Project) : BaseAsmTransform(project) {

    private val excludeClassArray: Array<String> by lazy {
        AsmExtension.get(project).excludeClassArray ?: emptyArray()
    }

    override fun filterVisitClass(fileName: String): Boolean {
        // 非class文件，不处理，直接过滤
        if (!fileName.contains(".class")) {
            return true
        }

        // 黑名单的class文件，直接过滤
        excludeClassArray.forEach { excludeClass ->
            if (fileName.contains(excludeClass)) {
                return true
            }
        }
        return false
    }

    override fun dealClassChain(chain: ClassVisitorChain) {
        chain.addVisitor {
            OnTestClassVisitor(it)
            SuperClassReplaceVisitor(it)
        }
    }

    override fun getName(): String = "ASMTransformImpl"
}
