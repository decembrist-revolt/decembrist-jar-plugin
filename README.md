# Decembrist Jar Plugin 
[![Build Status](https://travis-ci.org/decembrist-revolt/decembrist-jar-plugin.svg?branch=master)](https://travis-ci.org/decembrist-revolt/decembrist-jar-plugin)  
Flexible jar plugin for Maven 

Plugin substitutes maven-jar-plugin to create jar for java, kotlin or kotlin2js
You should specify _target_ in configuration block  
 - JVM
 - JS

```xml
<plugin>
    <groupId>org.decembrist</groupId>
    <artifactId>decembrist-jar-plugin</artifactId>
    <version>0.9.0</version>
    <extensions>true</extensions>
    <configuration>
        <target>JVM</target>
    </configuration>
</plugin>
```  
You can ignore _target_ propery and specify sources for packing manually with _sourceConfigs_
 - sourceConfigs - array of sources configs
 - directories - array of directories to include
 - includes - array of file patterns to include
 - excludes - array of files patterns to exclude

Patterns examples 
 - _\*\*/*.class_ - for any .class files
 - _\*\*/**_ - for any files
 - _\*\*/*.js_ - for .js files
```xml
...
<configuration>
    <sourceConfigs>
        <config>
            <directories>
                <directory>${project.build.outputDirectory}</directory>
            </directories>
            <includes>
                <include>**/*.class</include>
            </includes>
            <excludes>
                <exclude>**/*.js</exclude>
            </excludes>
        </config>
    </sourceConfigs>
</configuration>
...
```

You can combine your _sourceConfigs_ with _target_.

_target_ for both JVM and JS presents hardcoded source configs:
 - JVM  
```xml
<sourceConfigs>
    <config>
        <directories>
            <directory>${project.build.outputDirectory}</directory>
        </directories>
        <includes>
            <include>**/**</include>
        </includes>
        <excludes>
            <exclude>**/package.html</exclude>
        </excludes>
    </config>
</sourceConfigs>
```
 - JS  
```xml
<sourceConfigs>
    <config>
        <directories>
            <directory>${project.build.directory}/js/${project.name}</directory>
        </directories>
        <includes>
            <include>**/*.kjsm</include>
        </includes>
        <excludes>
            <exclude>**/*.class</exclude>
            <exclude>**/*.js</exclude>
        </excludes>
    </config>
    <config>
        <directories>
            <directory>(${project.build.directory}/js/${project.name}).parentFile</directory>
        </directories>
        <includes>
            <include>**/*.js</include>
        </includes>
        <excludes>
            <exclude>**/*.kjsm</exclude>
            <exclude>**/*.class</exclude>
        </excludes>
    </config>
    <config>
        <directories>
            <directory>${project.build.outputDirectory}</directory>
        </directories>
        <includes>
            <include>**/**</include>
        </includes>
        <excludes>
            <exclude>**/*.kjsm</exclude>
            <exclude>**/*.class</exclude>
        </excludes>
    </config>
</sourceConfigs>
```

```One of _<target>_ or _<sourceConfigs>_ have to be specified```

_Other available options:_
 - _jsDefaultKjsmDirectory_ - directory where are .kjsm files placed. Used only for JS - target. 
Default:
```maven
    ${project.build.directory}/js/${project.name}
```
 - _outputDirectory_ - directory containing the generated JAR
Default:
```maven
    ${project.build.directory}
```
 - _skipNonExistingDirs_ - If true - don't process non-existing sources (it can throw exception) Default: true
 - _classifier_ - @see [maven-jar-plugin](https://maven.apache.org/plugins/maven-jar-plugin/jar-mojo.html)
 - _archive_ - @see [maven-jar-plugin](https://maven.apache.org/plugins/maven-jar-plugin/jar-mojo.html)
 - _forceCreation_ -@see [maven-jar-plugin](https://maven.apache.org/plugins/maven-jar-plugin/jar-mojo.html)
 - _skipIfEmpty_ - @see [maven-jar-plugin](https://maven.apache.org/plugins/maven-jar-plugin/jar-mojo.html)
