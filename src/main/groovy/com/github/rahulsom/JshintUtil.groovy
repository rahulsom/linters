package com.github.rahulsom

import groovy.transform.CompileStatic
import org.gradle.api.Project

import static com.github.rahulsom.SharedUtil.ensureFile
import static com.github.rahulsom.SharedUtil.nodeTask
import static com.github.rahulsom.SharedUtil.toFile

@CompileStatic
class JshintUtil {
    def static void createJshintTask(Project project, LintersModuleExtension lintersConfig) {
        nodeTask(project, 'jshint', dependsOn: 'installLinters', group: 'linters',
                description: 'Checks styles for js code using jshint') {
            onlyIf { lintersConfig.jshint }

            doFirst { ensureFile project, '.jshintrc' }

            def list = new FileNameFinder().getFileNames(
                    project.projectDir.absolutePath,
                    lintersConfig.jsIncludes?.join(' '),
                    lintersConfig.jsExcludes?.join(' ')
            )

            if (lintersConfig?.format == LintersModuleExtension.FORMAT_CHECKSTYLE) {
                doFirst {
                    project.buildDir.mkdirs()
                }
                args = list + ['--reporter', 'checkstyle']
                execOverrides = toFile('build/checkstyle.jshint.xml')
            } else {
                args = list
            }

            ignoreExitValue = true
            script = project.file('node_modules/jshint/bin/jshint')

        }
    }

}
