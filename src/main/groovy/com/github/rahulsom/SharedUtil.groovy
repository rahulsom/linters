package com.github.rahulsom

import com.moowork.gradle.node.task.NodeTask
import com.moowork.gradle.node.task.NpmTask
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import org.gradle.api.Project

@CompileStatic
class SharedUtil {
    def static void npmTask(
            Map<String, ?> options, Project project, String name, @DelegatesTo(NpmTask) Closure closure) {
        def task = project.task (options + [type: NpmTask], name)
        project.afterEvaluate {
            task.configure closure
        }
    }

    def static void nodeTask(
            Map<String, ?> options, Project project, String name, @DelegatesTo(NodeTask) Closure closure) {
        def task = project.task (options + [type: NodeTask], name)
        project.afterEvaluate {
            task.configure closure
        }
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    def static Closure toFile(String name) {
        return {
            it.standardOutput = new FileOutputStream(name)
        }
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    def static Closure toBAOS(boolean lintersDebug) {
        return {
            if (!lintersDebug) {
                it.standardOutput = new ByteArrayOutputStream()
            }
        }
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    def static Closure wrap(ByteArrayOutputStream baos) {
        return {
            it.standardOutput = baos
        }
    }

    def static void ensureFile(Project project, String name) {
        if (!project.file(name).exists()) {
            project.file(name).text = SharedUtil.class.classLoader.getResource(name).text
        }

    }

}
