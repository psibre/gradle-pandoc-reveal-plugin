package org.m2ci.msp.pandocreveal

import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class PandocRevealPluginTest {

    GradleRunner provideGradle() {
        def projectDir = File.createTempDir()
        new File(projectDir, 'settings.gradle').createNewFile()
        new File(projectDir, 'assets').mkdirs()
        ['build.gradle', 'slides.md', 'header.yaml', 'refs.bib', 'expected.html', 'assets/asset.txt'].each { resourceName ->
            new File(projectDir, resourceName).withWriter {
                it << this.class.getResourceAsStream(resourceName)
            }
        }
        GradleRunner.create().withPluginClasspath().withProjectDir(projectDir).forwardOutput()
    }

    @Test
    void testPlugin() {
        def gradle = provideGradle()
        def result = gradle.build()
        assert result
    }

    @ParameterizedTest
    @ValueSource(strings = [
            'testPandoc',
            'testCompileReveal',
            'assemble',
            'testDate'
    ])
    void testTasks(String taskName) {
        def gradle = provideGradle()
        def result = gradle.withArguments('--warning-mode', 'all', '--stacktrace', taskName).build()
        assert result.task(":$taskName").outcome == SUCCESS
    }
}
