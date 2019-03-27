package com.halohoop.andembed

import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty

class EmbedGroup {
    Property<String> packageName
    Property<String> versionName
    Property<String> outputDirPath
    SetProperty<String> includeClass
    SetProperty<String> includePackage
    SetProperty<String> excludeJar
    SetProperty<String> excludeAar
    SetProperty<String> excludeClass
    SetProperty<String> excludePackage

    EmbedGroup(Project project) {
        packageName = project.getObjects().property(String.class)
        versionName = project.getObjects().property(String.class)
        outputDirPath = project.getObjects().property(String.class)
        includeClass = project.getObjects().setProperty(String.class)
        includePackage = project.getObjects().setProperty(String.class)
        excludeJar = project.getObjects().setProperty(String.class)
        excludeAar = project.getObjects().setProperty(String.class)
        excludeClass = project.getObjects().setProperty(String.class)
        excludePackage = project.getObjects().setProperty(String.class)
        outputDirPath.set(project.buildDir.getAbsolutePath() + File.separator + "outputs" + File.separator + "jar")
    }

    Property<String> getPackageName() {
        return packageName
    }

    Property<String> getVersionName() {
        return versionName
    }

    Property<String> getOutputDirPath() {
        return outputDirPath
    }

    SetProperty<String> getIncludeClass() {
        return includeClass
    }

    SetProperty<String> getIncludePackage() {
        return includePackage
    }

    SetProperty<String> getExcludeJar() {
        return excludeJar
    }

    SetProperty<String> getExcludeAar() {
        return excludeAar
    }

    SetProperty<String> getExcludeClass() {
        return excludeClass
    }

    SetProperty<String> getExcludePackage() {
        return excludePackage
    }
}
