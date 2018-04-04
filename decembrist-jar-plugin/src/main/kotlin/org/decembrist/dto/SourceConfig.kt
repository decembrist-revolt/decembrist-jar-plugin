package org.decembrist.dto

import java.io.File

class SourceConfig(var directories: Array<File> = emptyArray(),
                   var includes: Array<String> = emptyArray(),
                   var excludes: Array<String> = emptyArray())

