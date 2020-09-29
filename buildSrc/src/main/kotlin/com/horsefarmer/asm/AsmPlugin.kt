package com.horsefarmer.asm

import com.android.build.gradle.AppExtension
import com.horsefarmer.asm.internal.AsmEnv
import com.horsefarmer.asm.internal.extension.AsmExtension
import com.horsefarmer.asm.internal.transform.AsmTransformImpl
import com.horsefarmer.asm.internal.util.log
import org.gradle.api.Plugin
import org.gradle.api.Project

class AsmPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        log("AsmPlugin apply start")
        // 创建配置数据
        project.extensions.create("AsmExtension", AsmExtension::class.java)

        // Android主工程，添加字节码处理器
        project.extensions.findByType(AppExtension::class.java)?.run {
            log("AsmPlugin registerTransform AsmTransformImpl")
            registerTransform(AsmTransformImpl(project))
        }

        // 读取AsmExtension配置成功完毕
        project.afterEvaluate {
            val asmExtension = AsmExtension.get(it)

        }
        log("AsmPlugin apply end")
    }
}
