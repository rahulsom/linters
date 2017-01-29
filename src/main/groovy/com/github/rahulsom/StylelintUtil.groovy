package com.github.rahulsom

import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import groovy.xml.MarkupBuilder
import org.gradle.api.Project

import static com.github.rahulsom.SharedUtil.ensureFile
import static com.github.rahulsom.SharedUtil.nodeTask
import static com.github.rahulsom.SharedUtil.wrap

@CompileStatic
class StylelintUtil {
    def static void createStylelintTask(Project project, LintersModuleExtension lintersConfig) {
        nodeTask(project, 'stylelint', dependsOn: 'installLinters', group: 'linters',
                description: 'Checks styles for styles using stylelint') {

            onlyIf { lintersConfig.stylelint }

            doFirst { ensureFile project, '.stylelintrc' }

            def list = new FileNameFinder().getFileNames(
                    project.projectDir.absolutePath,
                    lintersConfig.stylesIncludes?.join(' '),
                    lintersConfig.stylesExcludes?.join(' ')
            )
            if (lintersConfig?.format == LintersModuleExtension.FORMAT_CHECKSTYLE) {
                def baos = new ByteArrayOutputStream()
                execOverrides = wrap(baos)
                args = list + ['-f', 'json']
                doFirst {
                    project.buildDir.mkdirs()
                }
                doLast {
                    writeStylelintXmlReport(baos)
                }
            } else {
                args = list
            }
            ignoreExitValue = true
            script = project.file('node_modules/stylelint/bin/stylelint.js')
        }
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    private static void writeStylelintXmlReport(ByteArrayOutputStream baos) {
        def json = new JsonSlurper().parseText(new String(baos.toByteArray()))
        def checkstyle = new MarkupBuilder(new FileWriter('build/checkstyle.stylelint.xml'))
        checkstyle.checkstyle {
            json.each { j ->
                file(name: j.source) {
                    j.warnings.each { w ->
                        error(line: w.line, column: w.column, message: w.text, source: w.rule, severity: w.severity)
                    }
                }
            }
        }
    }
}
