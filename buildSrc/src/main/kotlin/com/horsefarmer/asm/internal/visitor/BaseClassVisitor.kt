package com.horsefarmer.asm.internal.visitor

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes

internal abstract class BaseClassVisitor(cv: ClassVisitor) : ClassVisitor(Opcodes.ASM5, cv) {

}
