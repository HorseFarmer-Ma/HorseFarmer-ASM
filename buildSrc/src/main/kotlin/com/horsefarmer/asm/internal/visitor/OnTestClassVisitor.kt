package com.horsefarmer.asm.internal.visitor

import com.horsefarmer.asm.internal.util.log
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

internal class OnTestClassVisitor(cv: ClassVisitor) : ClassVisitor(Opcodes.ASM5, cv) {
    private var isNeedInsert = false

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        super.visit(version, access, name, signature, superName, interfaces)
        log("name = $name, interfaces = ${interfaces?.contentToString()}")
        isNeedInsert = interfaces?.contains("android/view/View\$OnClickListener") == true
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val mv = super.visitMethod(access, name, descriptor, signature, exceptions)
        if (isNeedInsert && "onClick" == name) {
            log("visitMethod onClick")
            mv.visitFieldInsn(Opcodes.GETSTATIC, "com/horsefarmer/asm/AsmApplication", "context", "Landroid/content/Context;")
            mv.visitLdcInsn("ASM\u63d2\u6869")
            mv.visitInsn(Opcodes.ICONST_0)
            mv.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "android/widget/Toast",
                "makeText",
                "(Landroid/content/Context;Ljava/lang/CharSequence;I)Landroid/widget/Toast;",
                false
            )
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "android/widget/Toast", "show", "()V", false)
        }
        return mv
    }
}
