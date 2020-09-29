package com.horsefarmer.asm.internal.visitor

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter

internal class BaseMethodVisitor(
    methodVisitor: MethodVisitor,
    access: Int,
    name: String,
    descriptor: String
) : AdviceAdapter(Opcodes.ASM5, methodVisitor, access, name, descriptor) {

}
