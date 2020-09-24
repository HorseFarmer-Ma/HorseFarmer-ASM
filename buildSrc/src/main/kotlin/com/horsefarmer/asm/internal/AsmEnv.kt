package com.horsefarmer.asm.internal

import com.android.build.gradle.AppExtension
import org.gradle.api.Project

internal object AsmEnv {
    fun isAppProject(project: Project) = project.extensions.findByType(AppExtension::class.java) != null
}
