package com.horsefarmer.asm.internal.transform

import com.horsefarmer.asm.internal.extension.AsmExtension
import com.horsefarmer.asm.internal.util.log
import com.horsefarmer.asm.internal.visitor.ClassVisitorChain
import com.horsefarmer.asm.internal.visitor.SuperClassReplaceVisitor
import org.gradle.api.Project

internal class AsmTransformImpl(project: Project) : BaseAsmTransform(project) {
    private val obtainReplaceSuperProps: Map<String, String> by lazy { AsmExtension.obtainReplaceSuperProps(project) }

    private val excludeClassArray: Array<String> by lazy {
        AsmExtension.get(project).excludeClassArray ?: emptyArray()
    }

    private val whiteClassArray: Array<String> by lazy {
        AsmExtension.get(project).whiteClassArray ?: emptyArray()
    }

    override fun filterVisitClass(fileName: String): Boolean {

        // 非class文件，不处理，直接过滤
        if (!fileName.contains(".class")) {
            return true
        }

        // 设置了白名单且当前类不在白名单中，直接过滤
        if (whiteClassArray.isNotEmpty() && !whiteClassArray.contains(fileName.replace("/", ".").split(".class")[0])) {
            return true;
        }

        // 黑名单的class文件，直接过滤
        excludeClassArray.forEach { excludeClass ->
            if (fileName.contains(excludeClass)) {
                return true
            }
        }

        log("white list=${whiteClassArray.contentToString()}, deal with class, $fileName")

        return false
    }

    override fun dealClassChain(chain: ClassVisitorChain) {
        chain.addVisitor {
            SuperClassReplaceVisitor(it, obtainReplaceSuperProps)
        }
    }

    override fun getName(): String = "ASMTransformImpl"
}
