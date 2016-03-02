package com.github.rahulsom

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification
import spock.lang.Unroll

class LintersPluginSpec extends Specification {

    @Unroll
    def "#taskName added to project when applied"() {
        given:
        Project project = ProjectBuilder.builder().build()

        when:
        project.pluginManager.apply('com.github.rahulsom.linters')

        then:
        project.tasks[taskName]

        where:
        taskName << ['installLinters', 'lintersInit', 'jscs', 'jscsFix', 'htmlcs', 'htmlcsFix', 'jshint', 'stylelint']
    }

    @Unroll
    def "#taskName is created with correct dependsOn"() {
        given:
        Project project = ProjectBuilder.builder().build()

        when:
        project.pluginManager.apply('com.github.rahulsom.linters')

        then:
        project.tasks[taskName].dependsOn.contains(dependencies)

        where:
        taskName         || dependencies
        'installLinters' || 'lintersInit'
        'jscs'           || 'installLinters'
        'jscsFix'        || 'installLinters'
        'htmlcs'         || 'installLinters'
        'htmlcsFix'      || 'installLinters'
        'jshint'         || 'installLinters'
        'stylelint'      || 'installLinters'
    }

    def "linters module extension is added"() {
        given:
        Project project = ProjectBuilder.builder().build()

        when:
        project.pluginManager.apply('com.github.rahulsom.linters')

        then:
        project.extensions.findByName('linters')
    }
}