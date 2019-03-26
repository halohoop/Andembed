package com.halohoop.andembed

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.ProjectConfigurationException
import org.gradle.api.artifacts.ResolvedArtifact
import org.gradle.api.tasks.bundling.Jar

class EmbedGroupPlugin implements Plugin<Project>{
    def project
    def packageName
    def artifactVersion
    def includeClass
    def includePackage
    def excludeJar
    def excludeAar
    def excludeClass
    def excludePackage
    def classesBasePath

    def resolvedAar = new HashSet<ResolvedArtifact>(4)
    def resolvedJar = new HashSet<ResolvedArtifact>(4)

    def classesReleasePath = "${File.separator}intermediates${File.separator}classes${File.separator}release"
    def classesReleasePath2 = "${File.separator}intermediates${File.separator}javac${File.separator}release${File.separator}compileReleaseJavaWithJavac${File.separator}classes"

    //判断是否是空的文件夹
    def isEmptyDir(def file) {
        def isEmpty = true
        if (file.isDirectory()) {
            def files = file.listFiles()
            if(files != null && files.length > 0) {
                def listResult = new ArrayList()
                for (def f : files) {
                    listResult.add(isEmptyDir(f))
                }
                for(def result : listResult) {
                    if(!result) {
                        isEmpty = false
                        break
                    }
                }
            } else {
                isEmpty = true
            }
        } else {
            isEmpty = false
        }
        return isEmpty
    }

    @Override
    void apply(Project project) {
        this.project = project
        def extension = project.extensions.create("xEmbedGroup", EmbedGroup.class, project)

        if (!project.pluginManager.hasPlugin("com.android.library")) {
//        if (!project.pluginManager.hasPlugin("com.android.application")) {
            throw new ProjectConfigurationException("EmbedArtifactPlugin must applied in android library project.", null)
        }
        project.configurations.create('embed')
                .setVisible(false)
                .setDescription("Merge libraries for this plugin.")
//        Set<ResolvedArtifact> artifactSet = new HashSet<>()
//        def extensions = project.getExtensions()
//        extensions.add("x_EmbedGroup", artifactSet)

        project.afterEvaluate {
            try {
                packageName = extension.getPackageName().get()
            } catch (IllegalStateException e) {
                throw new ProjectConfigurationException('Plz add extension with "xEmbedGroup{packageName = "com.halohoop.abc" versionName = ""}"', null)
            }
            if ("".equals(packageName) || packageName == null) {
                throw new ProjectConfigurationException('Plz add extension with "xEmbedGroup{packageName = "com.halohoop.abc" versionName = ""}"', null)
            }
            try {
                artifactVersion = extension.getVersionName().get()
            } catch (IllegalStateException e) {
                throw new ProjectConfigurationException('Plz add extension with "xEmbedGroup{packageName = "com.halohoop.abc" versionName = ""}"', null)
            }
            if ("".equals(artifactVersion) || artifactVersion == null) {
                throw new ProjectConfigurationException('Plz add extension with "xEmbedGroup{packageName = "com.halohoop.abc" versionName = ""}"', null)
            }

            includeClass = extension.getIncludeClass().get()
            includePackage = extension.getIncludePackage().get()
            excludeJar = extension.getExcludeJar().get()
            excludeAar = extension.getExcludeJar().get()
            excludeClass = extension.getExcludeClass().get()
            excludePackage = extension.getExcludePackage().get()

            classesBasePath = packageName.replaceAll("\\.", '\\\\')

            //添加自定义构建任务
            def make2JarReleaseAssembleReleaseTask = project.task("make2JarReleaseAssembleRelease", {
                doFirst {
                    println("${it.name} start")
                }
                doLast {
                    println("${it.name} finish")
                }
            })
            make2JarReleaseAssembleReleaseTask.dependsOn(project.tasks.findByPath(":${project.name}:assembleRelease"))

            //添加自定义构建任务
            def make2JarReleaseCleanTask = project.task("make2JarReleaseClean", {
                doFirst {
                    println("${it.name} start")
                }
                doLast {
                    println("${it.name} finish")
                }
            })
            make2JarReleaseCleanTask.dependsOn(project.tasks.findByPath(":${project.name}:clean"))

            def embedJarCopyProcessTask = project.task("embedJarCopyProcess", {
                doFirst {
                    println("${it.name} start")
                    copyProcess()
                }
                doLast {
                    println("${it.name} finish")
                }
            })

            //添加打jar包task
            def make2JarReleaseTask = project.task(type: Jar, "make2JarRelease",{
                def srcClassDir = ["${project.buildDir.absolutePath}${classesReleasePath}",
                                   "${project.buildDir.absolutePath}${classesReleasePath2}"]
//                baseName = "${project.name}"
//                version = "${artifactVersion}"
//                classifier = "release"
//                extension = "jar"

                archiveName = "tmp.jar"

                from srcClassDir
                destinationDir = new File("${project.buildDir}${File.separator}outputs${File.separator}jar")
                //根据aar或者jar配置这里需要的文件
                //exclude "${classesBasePath}/BuildConfig.class"
                //exclude "${classesBasePath}/BuildConfig\$*.class"
//                exclude "${classesBasePath}/*R.class"
//                exclude "${classesBasePath}/*R\$*.class"
                exclude 'META-INF'
                exclude {
                    println("handling file :" + it.name)
                    it.name.equals("R.class") || it.name.startsWith("R\$")
                }

                if (excludeClass != null && excludeClass.size() > 0) {
                    excludeClass.each {
                        println("excludeClass -> ${it}")
                        //排除指定class
                        exclude(it)
                    }
                }
                if (excludePackage != null && excludePackage.size() > 0) {
                    excludePackage.each {
                        println("excludePackage -> ${it}")
                        //过滤指定包名下class
                        exclude("${it}/**/*.class")
                    }
                }
                if (includeClass != null && includeClass.size() > 0) {
                    includeClass.each {
                        println("includeClass -> ${it}")
                        //打包指定的class
                        include(it)
                    }
                }
                if (includePackage != null && includePackage.size() > 0) {
                    includePackage.each {
                        //仅仅打包指定包名下class
                        include("${it}/**/*.class")
                    }
                }

//                include "${srcClassDir}/**/*.class"
//                include "*.class"
                doFirst {
                    new File("${project.buildDir}${File.separator}outputs${File.separator}${extension}").mkdirs()
                    println("${it.name} start")
                    println("正在收集以下路径的类:" + srcClassDir)
                }
                doLast {
                    println("${it.name} finish")
//                    def jarFile = new File("${project.buildDir}\\outputs\\${extension}\\${baseName}-${version}-${classifier}.${extension}")
//                    jarFileMid = new File("${project.buildDir}\\outputs\\${extension}\\${baseName}-${classifier}.${extension}")
//                    jarFile.renameTo(jarFileMid)
//                    jarFileMid = jarFile
                }
            })


            def outputDirPath = extension.getOutputDirPath().get()
            if (outputDirPath == null || "".equals(outputDirPath)) {
                throw new ProjectConfigurationException("Plz set a non-empty outputDirPath for output jar.", null)
            }
            def cleanJarAgainTask = project.task(type: Jar,"cleanJarAgain", {
//                baseName = "${project.name}"
//                version = "${artifactVersion}"
//                classifier = "release"
//                extension = "jar"

                archiveName = "${project.name}-release.jar"

                from project.zipTree(new File("${project.buildDir}${File.separator}outputs${File.separator}jar${File.separator}tmp.jar"))
                destinationDir = new File(outputDirPath)
                exclude {
                    println("handling file :" + it.name)
                    if (isEmptyDir(it.file)) {
                        println("\\-----is a empty dir")
                        return true
                    } else {
                        println("\\-----is not a empty dir")
                        return false
                    }
                }
                doFirst {
                    println("${it.name} start")
                }
                doLast {
                    println("${it.name} finish")
                    def file = new File("${project.buildDir}${File.separator}outputs${File.separator}jar${File.separator}tmp.jar")
                    if (file.exists()) {
                        file.delete()
                    }
                }
            })

            make2JarReleaseTask.group = 'build'
            make2JarReleaseCleanTask.group = 'build'
            make2JarReleaseAssembleReleaseTask.group = 'build'
            embedJarCopyProcessTask.group = 'build'

//            make2JarReleaseTask.dependsOn(embedJarCopyProcessTask)
//            embedJarCopyProcessTask.dependsOn(make2JarReleaseAssembleReleaseTask)
//            make2JarReleaseAssembleReleaseTask.dependsOn(make2JarReleaseCleanTask)

            make2JarReleaseTask.dependsOn(make2JarReleaseCleanTask)
            make2JarReleaseTask.dependsOn(make2JarReleaseAssembleReleaseTask)
            make2JarReleaseTask.dependsOn(embedJarCopyProcessTask)
            make2JarReleaseTask.finalizedBy(cleanJarAgainTask)

            make2JarReleaseAssembleReleaseTask.mustRunAfter(make2JarReleaseCleanTask)
            embedJarCopyProcessTask.mustRunAfter(make2JarReleaseAssembleReleaseTask)

//            copyProcess()
        }
    }

    def copyProcess() {
        project.configurations.embed.resolvedConfiguration.resolvedArtifacts.each { artifact ->
            if ('aar' == artifact.type) {
                if (excludeAar != null && excludeAar.size() > 0) {
                    if (!excludeAar.contains(artifact.file.name)) {
//                        println("res -> " + artifact.file.name)
                        resolvedAar.add(artifact)
                    }
                }
            } else if ('jar' == artifact.type) {
                if (excludeJar != null && excludeJar.size() > 0) {
                    if (!excludeJar.contains(artifact.file.name)) {
//                        println("res -> " + artifact.file.name)
                        resolvedJar.add(artifact)
                    }
                }
            } else {
                throw new ProjectConfigurationException('Only support embed aar and jar dependencies!', null)
            }
        }

        def srcClassDir = project.buildDir.absolutePath + classesReleasePath
        Set<File> jarFiles = new HashSet<>()
        resolvedAar.each { artifact ->
            def aarFile = artifact.file
            def clzFile = new File(srcClassDir + File.separator + "${artifact.name}.jar")
            jarFiles.add(clzFile)
            project.copy {
                from project.zipTree(aarFile)
                into srcClassDir
                exclude 'aidl'
                exclude "android${File.separator}arch"
                exclude "android${File.separator}support"
                exclude 'com'
                exclude 'res'
                exclude 'values'
                exclude 'META-INF'
                exclude 'AndroidManifest.xml'
                include 'classes.jar'
                rename('classes.jar', "${artifact.name}.jar")
            }
        }
        jarFiles.each { jarFile ->
            project.copy {
                from project.zipTree(jarFile)
                into srcClassDir
                exclude 'META-INF'
            }
            jarFile.delete()
        }
        resolvedJar.each { artifact ->
            project.copy {
                from project.zipTree(artifact.file)
                into srcClassDir
                exclude 'META-INF'
            }
        }
        def metaFile = new File(srcClassDir + File.separator + "META-INF")
        project.fileTree(metaFile).each { file ->
            file.delete()
        }
        metaFile.delete()
    }
}