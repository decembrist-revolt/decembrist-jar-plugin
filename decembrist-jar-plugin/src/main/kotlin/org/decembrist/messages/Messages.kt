package org.decembrist.messages

import java.io.File

//CompileSourcesMojo
const val SRC_DIR_NOT_EXISTS = "%s should exists and be directory"
const val EXCEPTION_WHILE_COMPILATION = "Compilation process throws exception %s"
const val SRC_DIRS_NOT_FOLDER_EXCEPTION = "One of <srcDirs> is not folder"
const val PER_FOLDER_SRC_DIRS_NOT_FOLDER_EXCEPTION_MESSAGE =
        "One of <perFolderSrcDirs> is not folder"
const val PER_FOLDER_SRC_SUB_DIR_NOT_FOLDER_EXCEPTION_MESSAGE =
        "One of <perFolderSrcDirs> sub file is not folder"
//InitProjectMojo
const val ARTIFACT_RESOLVED = "Artifact resolved successfully %s"
const val ARTIFACT_REPOSITORY = "Artifact repository - %s"
const val ARTIFACT_FILE = "Artifact file - %s"
const val SOURCE_JAR_IS_NOT_EXISTS = "Sources jar file is not exists for artifact %s"
//TASKS
const val COMPILE_TASK = "COMPILE TASK"

//TASK MESSAGES
const val KOTLIN_COMPILE_DIRS = "Kotlin compile dirs:"

const val KOTLIN_COMPILATION_MESSAGE = "Kotlin compilation"
const val KOTLINJS_COMPILATION_MESSAGE = "Kotlin2js compilation"
const val KOTLIN_COMPILATION_SUCCESS_MESSAGE = "Kotlin compiled successfully"
const val KOTLINJS_COMPILATION_SUCCESS_MESSAGE = "Kotlin2js compiled successfully"
const val PROCESSED_ARTIFACTS = "Processed artifacts %s"
const val PER_FOLDER_DIR_EMPTY_EXCEPTIONS = "perFolderSrcDir - must contain source folder"

fun kotlinCompileDirsMessage(srcDirs: Array<File>) =
        "$KOTLIN_COMPILE_DIRS ${srcDirs.joinToString(", ", "[", "]")}"

fun kotlinJsDirCompiledMessage(srcDir: File) = "$srcDir js compiled"

fun processedJsArtifactsMessage(artifacts: List<String>) = PROCESSED_ARTIFACTS.format(
        artifacts.joinToString(",", "[", "]"))

fun exceptionWhileCompilationMessage(message: String?) = EXCEPTION_WHILE_COMPILATION
        .format(message)

const val LOGO = """
________________________________________________________________________________
--------------------------------------------------------------------------------
--   __  __   ______  ______  __      __  __   __  ______  _____   ______     --
--  /\ \/ /  /\  __ \/\__  _\/\ \    /\ \/\ "-.\ \/\  __ \/\  __-./\  ___\    --
--  \ \  _"-.\ \ \/\ \/_/\ \/\ \ \___\ \ \ \ \-.  \ \ \/\ \ \ \/\ \ \  __\    --
--   \ \_\ \_\\ \_____\ \ \_\ \ \_____\ \_\ \_\\"\_\ \_____\ \____-\ \_____\  --
--    \/_/\/_/ \/_____/  \/_/  \/_____/\/_/\/_/ \/_/\/_____/\/____/ \/_____/  --
________________________________________________________________________________
--------------------------------------------------------------------------------
"""