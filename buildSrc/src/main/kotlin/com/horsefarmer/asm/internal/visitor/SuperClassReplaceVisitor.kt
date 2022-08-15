package com.horsefarmer.asm.internal.visitor

import com.horsefarmer.asm.internal.util.log
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes

internal class SuperClassReplaceVisitor(cv: ClassVisitor, private val replaceSuperProps: Map<String, String>) :
    ClassVisitor(Opcodes.ASM5, cv) {

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {

        if (replaceSuperProps.containsKey(name)) {
            log(" ================= SuperClassReplaceVisitor: find ================\nname=$name\noriginSuperClass=$superName\ndestSuperClass=${replaceSuperProps[name]}")
            super.visit(
                version,
                access,
                name,
                signature,
                replaceSuperProps[name],
                interfaces
            )
        } else {
            super.visit(version, access, name, signature, superName, interfaces)
        }
    }
}
