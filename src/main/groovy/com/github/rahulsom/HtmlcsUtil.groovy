package com.github.rahulsom

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import groovy.xml.MarkupBuilder
import org.gradle.api.Project

import static com.github.rahulsom.SharedUtil.ensureFile
import static com.github.rahulsom.SharedUtil.nodeTask
import static com.github.rahulsom.SharedUtil.wrap

@CompileStatic
class HtmlcsUtil {

    private static Map<String, String> parseHtmlCs(String line) {
        // [WARN] line 24, column 13: Attribute "alt" of <img> is recommended to be set. (img-alt, 012)
        def m = line =~ /\[(.+)\] line (\d+), column (\d+): (.+) \((.+)\)/
        def m0 = m[0] as List<String>
        [severity: m0[1], line: m0[2], column: m0[3], message: m0[4], source: m0[5]]
    }

    def static void createHtmlcsTask(Project project, LintersModuleExtension lintersConfig) {
        nodeTask(project, 'htmlcs', dependsOn: 'installLinters', group: 'linters',
                description: 'Check styles for html code using htmlcs') {

            onlyIf { lintersConfig.htmlcs }

            doFirst { ensureFile project, '.htmlcsrc' }

            def list = new FileNameFinder().getFileNames(
                    project.projectDir.absolutePath,
                    lintersConfig.htmlIncludes?.join(' '),
                    lintersConfig.htmlExcludes?.join(' ')
            )

            if (lintersConfig?.format == LintersModuleExtension.FORMAT_CHECKSTYLE) {
                def baos = new ByteArrayOutputStream()
                execOverrides = wrap(baos)
                doFirst {
                    project.buildDir.mkdirs()
                }
                doLast {
                    def report = new String(baos.toByteArray())
                    def files = report.split('\n').inject([[]] as List<List<String>>) { allFiles, line ->
                        if (line != '') {
                            allFiles.last().add(line)
                        } else {
                            allFiles.add([])
                        }
                        allFiles
                    }
                    writeHtmlcsReport(files as List<List<String>>)
                }
            }

            args = ['hint'] + list
            ignoreExitValue = true
            script = project.file('node_modules/htmlcs/bin/htmlcs')
        }
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    private static void writeHtmlcsReport(List<List<String>> files) {
        def checkstyle = new MarkupBuilder(new FileWriter('build/checkstyle.htmlcs.xml'))
        checkstyle.checkstyle {
            files.each { List<String> theFile ->
                if (theFile[1] != 'No hint result.') {
                    file(name: (theFile[0][0..-2])) {
                        theFile[1..-1].each { String w -> error(parseHtmlCs(w)) }
                    }
                }
            }
        }
    }

    def static void createHtmlcsFixTask(Project project, LintersModuleExtension lintersConfig) {
        nodeTask(project, 'htmlcsFix', dependsOn: 'installLinters', group: 'linters',
                description: 'Fix style for html code using htmlcs') {

            onlyIf { lintersConfig.htmlcs }

            doFirst { ensureFile project, '.htmlcsrc' }

            def list = new FileNameFinder().getFileNames(
                    project.projectDir.absolutePath,
                    lintersConfig.htmlIncludes?.join(' '),
                    lintersConfig.htmlExcludes?.join(' ')
            )

            args = ['format', '-i'] + list
            ignoreExitValue = true
            script = project.file('node_modules/htmlcs/bin/htmlcs')
        }
    }

}
