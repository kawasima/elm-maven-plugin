# elm-maven-plugin

This plugin downloads/installs Elm locally for your project, runs elm . It's supposed to work on Windows, OS X and Linux.

## Requirements

- Maven 3 and Java 1.8

## Installation

Include the plugin as a dependency in your Maven project. Change `LATEST_VERSION` to the latest tagged version.

```xml
<plugins>
    <plugin>
        <groupId>net.unit8.maven.plugins</groupId>
        <artifactId>elm-maven-plugin</artifactId>
        <version>LATEST_VERSION</version>
        ...
    </plugin>
...
```

## Usage

### Installing elm

```xml
<plugin>
    ...
    <execution>
        <!-- optional: you don't really need execution ids, but it looks nice in your build log. -->
        <id>install-elm</id>
        <goals>
            <goal>install</goal>
        </goals>
        <!-- optional: default phase is "generate-resources" -->
        <phase>generate-resources</phase>
    </execution>
    <configuration>
        <elmVersion>0.19.0</elmVersion>
    </configuration>
</plugin>
```

### Running

```xml
<execution>
    <id>elm-make</id>
    <goals>
        <goal>run</goal>
    </goals>
    <configuration>
        <arguments>make src/Main.elm</arguments>
    </configuration>
</execution>
```

## License

Copyright 2019 kawasima

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
