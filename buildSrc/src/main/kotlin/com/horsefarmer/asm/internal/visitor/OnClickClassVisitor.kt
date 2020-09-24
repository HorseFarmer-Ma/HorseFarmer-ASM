package com.horsefarmer.asm.internal.visitor

import com.horsefarmer.asm.internal.util.log
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

internal class OnClickClassVisitor(cv: ClassVisitor) : ClassVisitor(Opcodes.ASM5, cv){
    private var isNeedInsert = false

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        isNeedInsert = name?.contains("TestPlugin") == true
        super.visit(version, access, name, signature, superName, interfaces)
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val mv = super.visitMethod(access, name, descriptor, signature, exceptions)
        if (isNeedInsert) {
            if (name.equals("showLog")) {
                log("visitMethod of showLog")
                mv.visitLdcInsn("maxueming")
                mv.visitLdcInsn("maxueming haoshuai")
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/util/Log", "d", "(Ljava/lang/String;Ljava/lang/String;)I", false)
                mv.visitInsn(Opcodes.POP)
            }
        }
        return mv
    }
}
