package org.decembrist.mojos

import org.apache.maven.archiver.MavenArchiveConfiguration
import org.apache.maven.archiver.MavenArchiver
import org.apache.maven.execution.MavenSession
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugin.MojoExecutionException
import org.apache.maven.plugins.annotations.*
import org.apache.maven.project.MavenProject
import org.apache.maven.project.MavenProjectHelper
import org.codehaus.plexus.archiver.Archiver
import org.codehaus.plexus.archiver.jar.JarArchiver
import org.codehaus.plexus.archiver.util.DefaultFileSet.fileSet
import org.decembrist.dto.SourceConfig
import org.decembrist.enums.CompileTarget
import org.decembrist.enums.CompileTarget.*
import java.io.File


@Mojo(name = "jar",
        defaultPhase = LifecyclePhase.PACKAGE,
        requiresProject = true,
        threadSafe = true,
        requiresDependencyResolution = ResolutionScope.RUNTIME)
class JarMojo : AbstractMojo() {

    /**
     * Targeting platforms (JS, JVM)
     * Target or [sourceConfigs] should be specified
     */
    @Parameter
    private var target: CompileTarget? = null

    /**
     * Only for JS target
     * Directory containing js files that should be packaged into the JAR.
     *
     * default /target/js/${project.name}
     */
    @Parameter(defaultValue = "\${project.build.directory}/js/\${project.name}", required = true)
    private lateinit var jsDefaultKjsmDirectory: File

    /**
     * Array of directories with include/exclude for every directory or multiple ones
     * include/exclude is fileset patterns which are relative to the input directory whose contents
     * is being packaged into the JAR.
     *
     * Target or [sourceConfigs] should be specified
     *
     * For each target will be added default sourceConfigs
     * sourceConfigs will be added to default if specified
     * JS:
     * (
     *
     * directories = arrayOf([jsDefaultKjsmDirectory])
     *
     * includes = arrayOf(&#42&#42&#47;&#42.kjsm)
     *
     * excludes = arrayOf(&#42&#42&#47;&#42.class, &#42&#42&#47;&#42.js))
     *
     * ),
     *
     * (
     *
     * directories = arrayOf([jsDefaultKjsmDirectory].parentFile),
     *
     * includes = arrayOf(&#42&#42&#47;&#42.js),
     *
     * excludes = arrayOf(&#42&#42&#47;&#42.kjsm, &#42&#42&#47;&#42.class)),
     *
     * ),
     *
     * (
     *
     * directories = arrayOf(${project.build.outputDirectory}),
     *
     * includes = arrayOf(&#42&#42&#47;&#42&#42),
     *
     * excludes = arrayOf(&#42&#42&#47;&#42.kjsm, &#42&#42&#47;&#42.class)
     *
     * )
     *
     * JVM:
     *
     * directories = arrayOf(${project.build.outputDirectory}),
     *
     * includes = arrayOf(&#42&#42&#47;&#42&#42),
     *
     * excludes = arrayOf("&#42&#42&#47package.html")
     */
    @Parameter
    private var sourceConfigs: Array<SourceConfig>? = null

    /**
     * Directory containing the generated JAR.
     */
    @Parameter(defaultValue = "\${project.build.directory}", required = true)
    private lateinit var outputDirectory: File

    /**
     * Classifier to add to the artifact generated. If given, the artifact will be attached
     * as a supplemental artifact.
     * If not given this will create the main artifact which is the default behavior.
     * If you try to do that a second time without using a classifier the build will fail.
     */
    @Parameter
    private val classifier: String? = null

    /**
     * The archive configuration to use. See [Maven
     * Archiver Reference](http://maven.apache.org/shared/maven-archiver/index.html).
     */
    @Parameter
    private val archive = MavenArchiveConfiguration()

    /**
     * Require the jar plugin to build a new JAR even if none of the contents appear to have changed. By default, this
     * plugin looks to see if the output jar exists and inputs have not changed. If these conditions are true, the
     * plugin skips creation of the jar. This does not work when other plugins, like the maven-shade-plugin, are
     * configured to post-process the jar. This plugin can not detect the post-processing, and so leaves the
     * post-processed jar in place. This can lead to failures when those plugins do not expect to find their own output
     * as an input. Set this parameter to <tt>true</tt> to avoid these problems by forcing this plugin to recreate the
     * jar every time.<br></br>
     * Starting with **3.0.0** the property has been renamed from `jar.forceCreation` to
     * `maven.jar.forceCreation`.
     *
     * default false
     */
    @Parameter(property = "maven.jar.forceCreation")
    private var forceCreation: Boolean = false

    /**
     * Skip creating empty archives.
     *
     * default false
     */
    @Parameter
    private var skipIfEmpty: Boolean = false

    /**
     * If true - don't process non-existing sources (it can throw exception)
     *
     * default true
     */
    @Parameter(defaultValue = "true")
    private var skipNonExistingDirs: Boolean = true

    /**
     * Directory containing the classes and resource files that should be packaged into the JAR.
     *
     * default /target/classes
     */
    @Parameter(defaultValue = "\${project.build.outputDirectory}", required = true, readonly = true)
    private lateinit var buildClasses: File

    /**
     * Name of the generated JAR.
     */
    @Parameter(defaultValue = "\${project.build.finalName}", readonly = true)
    private lateinit var finalName: String

    @Parameter(defaultValue = "\${project}", readonly = true, required = true)
    private lateinit var project: MavenProject

    @Parameter(defaultValue = "\${session}", readonly = true, required = true)
    private lateinit var session: MavenSession

    @Component
    private lateinit var projectHelper: MavenProjectHelper

    /**
     * The Jar archiver.
     */
    @Component(role = Archiver::class, hint = "jar")
    private lateinit var jarArchiver: JarArchiver

    /**
     * List of files to include/exclude for JVM target <buildClasses>
     * Specified as fileset patterns which are relative to the input directory whose contents
     * is being packaged into the JAR.
     *
     * default includes "&#42&#42&#47;&#42*"
     *
     * default excludes "&#42&#42&#47package.html", "&#42&#42&#47;&#42.class"
     */
    private val jvmSourceConfig by lazy {
        arrayOf(SourceConfig(
                directories = arrayOf(buildClasses),
                includes = arrayOf(ANY_FILE),
                excludes = arrayOf("**/package.html")
        ))
    }

    /**
     * List of files to include/exclude for JS target <jsDefaultDirectory>
     * Specified as fileset patterns which are relative to the input directory whose contents
     * is being packaged into the JAR.
     *
     * default includes "&#42&#42&#47;&#42.kjsm"
     *
     * default excludes is empty
     */
    private val jsSourceConfig by lazy {
        arrayOf(
                SourceConfig(
                        directories = arrayOf(jsDefaultKjsmDirectory),
                        includes = arrayOf(KJSM_FILES),
                        excludes = arrayOf(CLASS_FILES, JS_FILES)),
                SourceConfig(
                        directories = arrayOf(jsDefaultKjsmDirectory.parentFile),
                        includes = arrayOf(JS_FILES, JS_MAP_FILES),
                        excludes = arrayOf(KJSM_FILES, CLASS_FILES)),
                SourceConfig(
                        directories = arrayOf(buildClasses),
                        includes = arrayOf(ANY_FILE),
                        excludes = arrayOf(KJSM_FILES, CLASS_FILES))
        )
    }

    private val defaultSourceConfig
        get() = if (target == JVM) jvmSourceConfig else jsSourceConfig

    /**
     * Overload this to produce a test-jar, for example.
     * @return return the type.
     */

    /**
     * Generates the JAR.
     * @throws [MojoExecutionException] in case of an error.
     */
    override fun execute() {
        assertParams()
        val hasSources = target.dirCheck()
        if (skipIfEmpty && hasSources) {
            log.info("Skipping packaging of the $TYPE")
        } else {
            val jarArchive = createArchive()

            if (hasClassifier()) {
                projectHelper.attachArtifact(project, TYPE, classifier, jarArchive)
            } else {
                if (projectHasAlreadySetAnArtifact()) {
                    throw MojoExecutionException("You have to use a classifier for jvm to attach" +
                            " supplemental artifacts to the project instead of replacing them.")
                }
                project.artifact.file = jarArchive
            }
        }
    }

    /**
     * Check that sourceConfigs or target is not null
     */
    private fun assertParams() {
        if (sourceConfigs == null) {
            target ?: throw MojoExecutionException(
                    "You should specify target(JVM, JS) or <sourceConfigs>")
        }
    }

    /**
     * Returns the Jar file to generate, based on an optional classifier.
     *
     * @param basedir the output directory
     * @param resultFinalName the name of the ear file
     * @param classifier an optional classifier
     * @return the file to generate
     */
    private fun getJarFile(basedir: File?, resultFinalName: String?, classifier: String?): File {
        basedir ?: throw IllegalArgumentException("basedir is not allowed to be null")
        resultFinalName ?: throw IllegalArgumentException("finalName is not allowed to be null")

        val classifierString = if (hasClassifier()) "-$classifier" else ""
        val fileName = "$resultFinalName$classifierString.jar"
        return File(basedir, fileName)
    }

    /**
     * Generates the JAR.
     * @return The instance of File for the created archive file.
     * @throws MojoExecutionException in case of an error.
     */
    private fun createArchive(): File {
        val jarFile = getJarFile(outputDirectory, finalName, classifier)

        val archiver = MavenArchiver()
        archiver.archiver = jarArchiver
        archiver.setOutputFile(jarFile)
        archive.isForced = forceCreation

        try {
            val actualSourceConfigs = getActualSourceConfigs()
            val someDirsExists = actualSourceConfigs
                    .map(SourceConfig::directories)
                    .flatMap { it.asIterable() }
                    .none(File::exists)
            if (someDirsExists) {
                log.warn("JAR will be empty - no content was marked for inclusion($target)!")
            } else {
                for (actualSourceConfig in actualSourceConfigs) {
                    val directories = actualSourceConfig.directories
                    for (directory in directories) {
                        if (!skipNonExistingDirs || directory.exists()) {
                            archiver.archiver.addDir(
                                    directory,
                                    actualSourceConfig.includes,
                                    actualSourceConfig.excludes)
                        }
                    }
                }
            }
            archiver.createArchive(session, project, archive)
            return jarFile
        } catch (ex: Exception) {
            throw MojoExecutionException("Error assembling JAR", ex)
        }
    }

    private fun getActualSourceConfigs(): Array<SourceConfig> {
        return if (target == null) {
            sourceConfigs!!
        } else {
            sourceConfigs?.plus(defaultSourceConfig) ?: defaultSourceConfig
        }
    }

    private fun projectHasAlreadySetAnArtifact() = project.artifact.file?.isFile == true

    private fun CompileTarget?.dirCheck() = when (this) {
        JVM -> dirCheck(*jvmSourceConfig, *sourceConfigs.orEmpty())
        JS -> dirCheck(*jsSourceConfig, *sourceConfigs.orEmpty())
        null -> dirCheck(*sourceConfigs.orEmpty())
    }

    private fun dirCheck(vararg sourceConfig: SourceConfig) = sourceConfig
            .flatMap { it.directories.asIterable() }
            .any { it.exists() and it.isDirectoryNotEmpty() }

    /**
     * @return true in case where the classifier is not `null`
     * and contains something else than white spaces.
     */
    private fun hasClassifier() = classifier?.isNotBlank() == true

    private fun File.isDirectoryNotEmpty() = this.list()?.isNotEmpty() ?: false

    private fun JarArchiver.addDir(dir: File, includes: Array<String>, excludes: Array<String>) {
        val fileSet = fileSet(dir)
                .prefixed("")
                .includeExclude(includes, excludes)
                .includeEmptyDirs(true)
        addFileSet(fileSet)
    }

    companion object {
        const val TYPE = "jar"
        const val KJSM_FILES = "**/*.kjsm"
        const val JS_FILES = "**/*.js"
        const val JS_MAP_FILES = "**/*.js.map"
        const val CLASS_FILES = "**/*.class"
        const val ANY_FILE = "**/**"
    }
}