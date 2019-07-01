package net.unit8.maven.plugins;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.github.eirslett.maven.plugins.frontend.lib.ElmPluginFactory;
import com.github.eirslett.maven.plugins.frontend.lib.ElmRunner;
import com.github.eirslett.maven.plugins.frontend.lib.FrontendException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.RepositorySystemSession;

import java.io.File;

/**
 * Running Elm.
 */
@Mojo(name = "run", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class ElmRunMojo
    extends AbstractElmMojo
{
    /**
     * npm arguments. Default is "install".
     */
    @Parameter(defaultValue = "", property = "frontend.elm.arguments", required = false)
    private String arguments;

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    @Parameter(defaultValue = "${repositorySystemSession}", readonly = true)
    private RepositorySystemSession repositorySystemSession;

    /**
     * Skips execution of this mojo.
     */
    @Parameter(property = "skip.elm", defaultValue = "${skip.elm}")
    private boolean skip;

    protected boolean skipExecution() {
        return this.skip;
    }
    /**
     * Location of the file.
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private File outputDirectory;

    public void execute(ElmPluginFactory factory) throws FrontendException {
        final ElmRunner elmRunner = factory.getElmRunner();
        elmRunner.execute(this.arguments, this.environmentVariables);
     }

 }
