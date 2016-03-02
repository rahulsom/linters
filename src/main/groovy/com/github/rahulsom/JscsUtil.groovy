package com.github.rahulsom

import groovy.transform.CompileStatic
import org.gradle.api.Project

import static com.github.rahulsom.SharedUtil.ensureFile
import static com.github.rahulsom.SharedUtil.nodeTask
import static com.github.rahulsom.SharedUtil.toFile

@CompileStatic
class JscsUtil {
    def static void createJscsTask(Project project, LintersModuleExtension lintersConfig) {
        nodeTask(project, 'jscs', dependsOn: 'installLinters', group: 'linters',
                description: 'Check styles for js code using jscs') {

            onlyIf { lintersConfig.jscs }

            doFirst { ensureFile project, '.jscsrc' }

            def list = new FileNameFinder().getFileNames(
                    project.projectDir.absolutePath,
                    lintersConfig.jsIncludes?.join(' '),
                    lintersConfig.jsExcludes?.join(' ')
            )

            if (lintersConfig?.format == LintersModuleExtension.FORMAT_CHECKSTYLE) {
                doFirst {
                    project.buildDir.mkdirs()
                }
                args = list + ['-r', 'checkstyle']
                execOverrides = toFile('build/checkstyle.jscs.xml')
            } else {
                args = list
            }

            ignoreExitValue = true
            script = project.file('node_modules/jscs/bin/jscs')
        }
    }

    def static void createJscsFixTask(Project project, LintersModuleExtension lintersConfig) {
        nodeTask(project, 'jscsFix', dependsOn: 'installLinters', group: 'linters',
                description: 'Fix style for js code using jscs') {

            onlyIf { lintersConfig.jscs }

            doFirst { ensureFile project, '.jscsrc' }

            def list = new FileNameFinder().getFileNames(
                    project.projectDir.absolutePath,
                    lintersConfig.jsIncludes?.join(' '),
                    lintersConfig.jsExcludes?.join(' ')
            )

            args = ['-x'] + list
            ignoreExitValue = true
            script = project.file('node_modules/jscs/bin/jscs')
        }
    }

}
