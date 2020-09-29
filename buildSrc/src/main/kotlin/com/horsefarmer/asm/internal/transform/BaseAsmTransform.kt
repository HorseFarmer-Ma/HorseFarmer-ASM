package com.horsefarmer.asm.internal.transform

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import com.horsefarmer.asm.internal.extension.AsmExtension
import com.horsefarmer.asm.internal.util.log
import com.horsefarmer.asm.internal.visitor.ClassVisitorChain
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

/**
 * BaseAsmTransform
 */
internal abstract class BaseAsmTransform(protected val project: Project) : Transform() {

    override fun isIncremental(): Boolean = AsmExtension.get(project).incremental

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
        return TransformManager.CONTENT_CLASS
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    override fun transform(transformInvocation: TransformInvocation) {
        val outputProvider = transformInvocation.outputProvider
        val incremental = transformInvocation.isIncremental
        log("isIncremental=$isIncremental")
        if (!incremental) {
            transformInvocation.outputProvider.deleteAll()
        }
        transformInvocation.inputs.forEach { input ->
            input.directoryInputs.forEach { directoryInput ->
                transformDirectory(directoryInput, outputProvider, incremental)
            }

            input.jarInputs.forEach { jarInput ->
                transformJar(jarInput, outputProvider, incremental)
            }
        }
    }

    private fun transformDirectory(
        directoryInput: DirectoryInput,
        outputProvider: TransformOutputProvider,
        isIncremental: Boolean
    ) {
        val inputDir = directoryInput.file
        val outputDir = outputProvider.getContentLocation(
            directoryInput.name,
            directoryInput.contentTypes,
            directoryInput.scopes,
            Format.DIRECTORY
        )
        if (isIncremental) {
            directoryInput.changedFiles.entries.forEach { entry ->
                val inputFile = entry.key
                val outputFile = inputFile.obtainNewOutputFile(outputDir, inputDir)
                when (entry.value) {
                    // No changed, transfer
                    Status.NOTCHANGED -> FileUtils.copyFile(inputFile, outputFile)
                    Status.ADDED, Status.CHANGED -> modifiedDirectorFile(inputFile, outputFile)
                    else -> FileUtils.deletePath(outputFile)
                }
            }
        } else {
            FileUtils.getAllFiles(directoryInput.file).forEach { inputFile ->
                val outputFile = inputFile.obtainNewOutputFile(outputDir, inputDir)
                modifiedDirectorFile(inputFile, outputFile)
            }
        }
    }

    private fun transformJar(
        jarInput: JarInput,
        outputProvider: TransformOutputProvider,
        isIncremental: Boolean
    ) {
        val inputJar = jarInput.file
        val outputJar = outputProvider.getContentLocation(
            jarInput.file.absolutePath,
            jarInput.contentTypes,
            jarInput.scopes,
            Format.JAR
        )
        if (isIncremental) {
            when (jarInput.status) {
                // No changed, transfer
                Status.NOTCHANGED -> FileUtils.copyFile(inputJar, outputJar)
                Status.ADDED, Status.CHANGED -> modifyJarFile(inputJar, outputJar)
                else -> FileUtils.deletePath(outputJar)
            }
        } else {
            modifyJarFile(inputJar, outputJar)
        }
    }

    private fun modifiedDirectorFile(inputFile: File, outputFile: File) {
        FileOutputStream(outputFile).use { outputStream ->
            val byteArray = inputFile.inputStream().use { inputStream ->
                if (filterVisitClass(inputFile.name))
                    inputStream.readBytes()
                else
                    inputStream.visitClass()
            }
            outputStream.write(byteArray)
        }
    }

    private fun modifyJarFile(inputJar: File, outputJar: File) {
        JarFile(inputJar).use { jarFile ->
            val enumeration = jarFile.entries()
            JarOutputStream(FileOutputStream(outputJar)).use { jarOutputStream ->
                while (enumeration.hasMoreElements()) {
                    val jarEntry = enumeration.nextElement()
                    val entryName = jarEntry.name
                    val outputByteArray = jarFile.getInputStream(jarEntry).use { inputStream ->
                        if (filterVisitClass(entryName)) {
                            inputStream.readBytes()
                        } else {
                            inputStream.visitClass()
                        }
                    }
                    val zipEntry = ZipEntry(entryName)
                    jarOutputStream.putNextEntry(zipEntry)
                    jarOutputStream.write(outputByteArray)
                    jarOutputStream.flush()
                    jarOutputStream.closeEntry()
                }
            }
        }
    }

    private fun File.obtainNewOutputFile(outputDir: File, inputDir: File): File {
        return File(
            outputDir,
            FileUtils.relativePossiblyNonExistingPath(this, inputDir)
        ).also { it.parentFile?.mkdirs() }
    }

    private fun InputStream.visitClass(): ByteArray {
        val cr = ClassReader(this)
        val cw = ClassWriter(ClassWriter.COMPUTE_MAXS)
        val cv = ClassVisitorChain(cw).also { dealClassChain(it) }.obtain()
        cr.accept(cv, ClassReader.EXPAND_FRAMES)
        return cw.toByteArray()
    }

    protected abstract fun filterVisitClass(fileName: String): Boolean

    protected abstract fun dealClassChain(chain: ClassVisitorChain)
}
