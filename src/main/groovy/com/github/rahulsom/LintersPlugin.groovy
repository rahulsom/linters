package com.github.rahulsom

import com.moowork.gradle.node.NodeExtension
import com.moowork.gradle.node.NodePlugin
import groovy.transform.CompileStatic
import org.gradle.api.Plugin
import org.gradle.api.Project

import static com.github.rahulsom.HtmlcsUtil.createHtmlcsFixTask
import static com.github.rahulsom.HtmlcsUtil.createHtmlcsTask
import static com.github.rahulsom.JscsUtil.createJscsFixTask
import static com.github.rahulsom.JscsUtil.createJscsTask
import static com.github.rahulsom.JshintUtil.createJshintTask
import static com.github.rahulsom.SharedUtil.npmTask
import static com.github.rahulsom.SharedUtil.toBAOS
import static com.github.rahulsom.StylelintUtil.createStylelintTask

@CompileStatic
class LintersPlugin implements Plugin<Project> {

    static final String NPM_OUTPUT_PATH = 'node_modules'
    static final String DEFAULT_NODE_VERSION = '4.2.3'

    void apply(Project project) {

        setupNode project

        LintersModuleExtension lintersConfig =
                project.extensions.create('linters', LintersModuleExtension) as LintersModuleExtension

        boolean lintersDebug = project.hasProperty('lintersDebug') ? project.property('lintersDebug') : false

        def nodeExecOverrides = toBAOS(lintersDebug)

        createInitTask project
        createInstallTask project, nodeExecOverrides

        createJscsTask project, lintersConfig
        createJscsFixTask project, lintersConfig

        createHtmlcsTask project, lintersConfig
        createHtmlcsFixTask project, lintersConfig

        createJshintTask project, lintersConfig

        createStylelintTask project, lintersConfig

        project.tasks.findByName('check')?.
                dependsOn('jscs', 'htmlcs', 'jshint', 'stylelint')
    }

    private static void createInstallTask(Project project, Closure nodeExecOverrides) {
        npmTask(project, 'installLinters', dependsOn: 'lintersInit', group: null,
                description: 'Installs dependencies needed for the jscs.') {
            args = ['install', 'jscs@3.0.7', 'stylelint@5.4.0', 'htmlcs@0.2.9', 'jshint@2.9.4']
            outputs.dir project.file(NPM_OUTPUT_PATH)
            execOverrides = nodeExecOverrides
        }
    }

    private static void createInitTask(Project project) {
        project.task('lintersInit', group: null,
                description: 'Sets up folder structure needed for the linters plugin') {
            project.file(NPM_OUTPUT_PATH).mkdirs()
        }
    }

    private static void setupNode(Project project) {
        project.plugins.apply NodePlugin
        NodeExtension nodeConfig = project.extensions.findByName('node') as NodeExtension
        nodeConfig.download = true
        nodeConfig.version = DEFAULT_NODE_VERSION
    }

}
