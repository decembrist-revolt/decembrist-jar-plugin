package org.decembrist.mojo

import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.shared.invoker.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.io.File
import java.util.*
import java.util.jar.JarFile
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.AfterTestExecutionCallback
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(JarMojoTest.FailedTestLogWriter::class)
class JarMojoTest {

    private lateinit var logLines: MutableList<String>
    private var currentProject: Project? = null

    @BeforeEach
    fun setUp() {
        logLines = mutableListOf()
    }

    @AfterEach
    fun tearDown() {
        executeMavenGoal(LifecyclePhase.CLEAN)
        currentProject = null
    }

    @DisplayName("Should create jar with <target>JS</target>")
    @Test
    fun testJsJar() {
        currentProject = jsJarTestProj
        val result = executeMavenGoal(LifecyclePhase.PACKAGE)
        assertEquals(MAVEN_SUCCESS_EXIT_CODE, result.exitCode, MAVEN_WRONG_EXIT_CODE_MESSAGE)
        val jarFile = fileFromResource(currentProject!!.jar)
        assertTrue(jarFile.exists(), JARFILE_NOT_FOUND)
        JarFile(jarFile).use { jar ->
            val orgDecembrist = buildDirectory(ORG, DECEMBRIST)
            jar.checkFileExistence(buildDirectory(orgDecembrist, DECEMBRIST.kjsm()))
            jar.checkFileExistence("properties.props")
            jar.checkFileExistence(
                    buildDirectory(orgDecembrist, "testpackage", "testpackage".kjsm()))
            jar.checkFileExistence(currentProject!!.projectName.js())
            jar.checkFileExistence(currentProject!!.projectName.metaJs())
            jar.checkFileExistence(currentProject!!.projectName.jsMap())
            jar.checkFileExistence(buildDirectory(META_INF_FOLDER, MANIFEST_MF_FILE))
            jar.checkFileNonExistence(buildDirectory(orgDecembrist, "SomeTest"))
            jar.checkFileNonExistence("testproperties.props")
        }
    }

    @DisplayName("Should create jar with <target>JS</target> with <sourceConfigs>")
    @Test
    fun testJsJarWithSourceConfigs() {
        currentProject = jsJarWithSourceSonfigsTestProj
        val result = executeMavenGoal(LifecyclePhase.PACKAGE)
        assertEquals(MAVEN_SUCCESS_EXIT_CODE, result.exitCode, MAVEN_WRONG_EXIT_CODE_MESSAGE)
        val jarFile = fileFromResource(currentProject!!.jar)
        assertTrue(jarFile.exists(), JARFILE_NOT_FOUND)
        JarFile(jarFile).use { jar ->
            val orgDecembrist = buildDirectory(ORG, DECEMBRIST)
            jar.checkFileExistence(buildDirectory(orgDecembrist, DECEMBRIST.kjsm()))
            jar.checkFileExistence(buildDirectory(orgDecembrist, "SomeTest".`class`()))
            jar.checkFileExistence("properties.props")
            jar.checkFileExistence(
                    buildDirectory(orgDecembrist, "testpackage", "testpackage".kjsm()))
            jar.checkFileExistence(currentProject!!.projectName.js())
            jar.checkFileExistence(currentProject!!.projectName.metaJs())
            jar.checkFileExistence(currentProject!!.projectName.jsMap())
            jar.checkFileExistence(buildDirectory(META_INF_FOLDER, MANIFEST_MF_FILE))
            jar.checkFileNonExistence("testproperties.props")
        }
    }

    @DisplayName("Should create jar with <target>JVM</target> manifest main class should exist")
    @Test
    fun testJvmJarAndManifest() {
        currentProject = jvmJarTestProj
        val result = executeMavenGoal(LifecyclePhase.PACKAGE)
        assertEquals(MAVEN_SUCCESS_EXIT_CODE, result.exitCode, MAVEN_WRONG_EXIT_CODE_MESSAGE)
        val jarFile = fileFromResource(currentProject!!.jar)
        assertTrue(jarFile.exists(), JARFILE_NOT_FOUND)
        JarFile(jarFile).use { jar ->
            val orgDecembrist = buildDirectory(ORG, DECEMBRIST)
            jar.checkFileExistence(buildDirectory(orgDecembrist, "MainKt".`class`()))
            jar.checkFileExistence("properties.props")
            jar.checkFileExistence(
                    buildDirectory(orgDecembrist, "testpackage", "SomeClass".`class`()))
            jar.checkFileExistence(buildDirectory(META_INF_FOLDER, MANIFEST_MF_FILE))
            jar.checkFileNonExistence(buildDirectory(orgDecembrist, "SomeTest"))
            jar.checkFileNonExistence("testproperties.props")
            val manifestContent = jar.getJarFileContent(buildDirectory(META_INF_FOLDER, MANIFEST_MF_FILE))
            assertTrue(manifestContent.contains("Main-Class: org.decembrist.MainKt"))
        }
    }

    @DisplayName("Should create jar with <target>JVM</target> with <sourceConfigs>")
    @Test
    fun testJvmJarWithSourceConfigs() {
        currentProject = jvmJarWithSourceSonfigsTestProj
        val result = executeMavenGoal(LifecyclePhase.PACKAGE)
        assertEquals(MAVEN_SUCCESS_EXIT_CODE, result.exitCode, MAVEN_WRONG_EXIT_CODE_MESSAGE)
        val jarFile = fileFromResource(currentProject!!.jar)
        assertTrue(jarFile.exists(), JARFILE_NOT_FOUND)
        JarFile(jarFile).use { jar ->
            val orgDecembrist = buildDirectory(ORG, DECEMBRIST)
            jar.checkFileExistence(buildDirectory(orgDecembrist, "MainKt".`class`()))
            jar.checkFileExistence(buildDirectory(orgDecembrist, "SomeTest".`class`()))
            jar.checkFileExistence("properties.props")
            jar.checkFileExistence(
                    buildDirectory(orgDecembrist, "testpackage", "SomeClass".`class`()))
            jar.checkFileExistence(buildDirectory(META_INF_FOLDER, MANIFEST_MF_FILE))
            jar.checkFileNonExistence("testproperties.props")
        }
    }

    @DisplayName("Should create jar with <sourceConfigs>")
    @Test
    fun testJarWithSourceConfigs() {
        currentProject = jarWithSourceSonfigsTestProj
        val result = executeMavenGoal(LifecyclePhase.PACKAGE)
        assertEquals(MAVEN_SUCCESS_EXIT_CODE, result.exitCode, MAVEN_WRONG_EXIT_CODE_MESSAGE)
        val jarFile = fileFromResource(currentProject!!.jar)
        assertTrue(jarFile.exists(), JARFILE_NOT_FOUND)
        JarFile(jarFile).use { jar ->
            val orgDecembrist = buildDirectory(ORG, DECEMBRIST)
            jar.checkFileNonExistence(buildDirectory(orgDecembrist, "MainKt".`class`()))
            jar.checkFileExistence(buildDirectory(orgDecembrist, "SomeTest".`class`()))
            jar.checkFileNonExistence("properties.props")
            jar.checkFileNonExistence(
                    buildDirectory(orgDecembrist, "testpackage", "SomeClass".`class`()))
            jar.checkFileExistence(buildDirectory(META_INF_FOLDER, MANIFEST_MF_FILE))
            jar.checkFileNonExistence("testproperties.props")
        }
    }

    @DisplayName("Should throw error \"Target should be specified\"")
    @Test
    fun testTargetError() {
        currentProject = targetErrorTestProj
        val result = executeMavenGoal(LifecyclePhase.PACKAGE)
        assertEquals(MAVEN_ERROR_EXIT_CODE, result.exitCode, MAVEN_WRONG_EXIT_CODE_MESSAGE)
        val errorExists = logLines
                .any { it.contains("You should specify target(JVM, JS) or <sourceConfigs>") }
        assertTrue(errorExists, ERROR_STRING_NOT_FOUND)
    }

    private fun executeMavenGoal(goal: LifecyclePhase): InvocationResult {
        val request = DefaultInvocationRequest()
        val file = fileFromResource(currentProject!!.pom)
        request.pomFile = file
        request.goals = Collections.singletonList(goal.id())

        return DefaultInvoker().apply {
            setOutputHandler { logLines.add(it) }
            logger = PrintStreamLogger(System.out, InvokerLogger.ERROR)
        }.execute(request)
    }

    private fun JarFile.getJarFileContent(file: String): String {
        val entry = getJarEntry(file)
        return getInputStream(entry).reader().readText()
    }

    private class Project(dir: String) {
        val pom = "$dir/pom.xml"
        val target = "$dir/target"
        val projectName = dir.split("/").last()
        val jar = "$target/$projectName-1.0-SNAPSHOT.jar"

    }

    private fun JarFile.checkFileExistence(file: String) {
        val fileEntry = getJarEntry(file)
        assertNotNull(fileEntry, "$file $FILE_NOT_FOUND")
    }

    private fun JarFile.checkFileNonExistence(startWith: String) {
        val entryExists = this.entries()
                .toList()
                .any { it.name.startsWith(startWith) }
        assertFalse(entryExists, "$startWith $FILE_MUST_NOT_EXIST")
    }

    private fun buildDirectory(vararg directory: String) = directory.joinToString("/")

    private fun String.kjsm() = "$this$KJSM_EXT"

    private fun String.js() = "$this$JS_EXT"

    private fun String.jsMap() = "$this$JS_EXT.map"

    private fun String.metaJs() = "$this$JS_EXT"

    private fun String.`class`() = "$this$CLASS_EXT"

    private fun fileFromResource(resource: String) = File(javaClass.getResource(resource).toURI())

    private companion object {
        const val ORG = "org"
        const val DECEMBRIST = "decembrist"
        const val KJSM_EXT = ".kjsm"
        const val JS_EXT = ".js"
        const val CLASS_EXT = ".class"
        const val META_JS_EXT = ".meta.js"
        const val META_INF_FOLDER = "META-INF"
        const val MANIFEST_MF_FILE = "MANIFEST.MF"
        const val MAVEN_SUCCESS_EXIT_CODE = 0
        const val MAVEN_ERROR_EXIT_CODE = 1

        //Messages
        const val MAVEN_WRONG_EXIT_CODE_MESSAGE = "Wrong maven exit code"
        const val JARFILE_NOT_FOUND = "Jarfile not found"
        const val ORGFOLDER_NOT_FOUND = "Org folder not found"
        const val DECEMBRISTFOLDER_NOT_FOUND = "Decembrist folder not found"
        const val FILE_NOT_FOUND = "file not found"
        const val FILE_MUST_NOT_EXIST = "file not found"
        const val ERROR_STRING_NOT_FOUND = "Error string not found"

        private val jsJarTestProj = Project("/test-sources/js-jar-test")
        private val jvmJarTestProj = Project("/test-sources/jvm-jar-test")
        private val targetErrorTestProj = Project("/test-sources/target-error-test")
        private val jsJarWithSourceSonfigsTestProj =
                Project("/test-sources/js-jar-with-source-configs-test")
        private val jvmJarWithSourceSonfigsTestProj =
                Project("/test-sources/jvm-jar-with-source-configs-test")
        private val jarWithSourceSonfigsTestProj =
                Project("/test-sources/jar-with-source-configs-test")
    }

    class FailedTestLogWriter : AfterTestExecutionCallback {

        override fun afterTestExecution(context: ExtensionContext) {
            if (context.executionException.isPresent) {
                context.testInstance
                        .map { it as JarMojoTest }
                        .ifPresent { test ->
                            test.logLines.forEach(::println)
                        }
            }
        }
    }
}

