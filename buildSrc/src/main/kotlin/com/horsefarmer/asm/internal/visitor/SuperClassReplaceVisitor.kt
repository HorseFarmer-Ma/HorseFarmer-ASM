package com.horsefarmer.asm.internal.visitor

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes

internal class SuperClassReplaceVisitor(cv: ClassVisitor) : ClassVisitor(Opcodes.ASM5, cv) {

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        if ("com/horsefarmer/asm/MainActivity" == name) {
            super.visit(version, access, name, signature, "com/horsefarmer/asm/BaseActivity", interfaces)
        } else {
            super.visit(version, access, name, signature, superName, interfaces)
        }
    }
}
