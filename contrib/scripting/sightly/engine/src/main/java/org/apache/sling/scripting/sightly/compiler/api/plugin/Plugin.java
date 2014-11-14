/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 ******************************************************************************/
package org.apache.sling.scripting.sightly.compiler.api.plugin;

import org.apache.sling.scripting.sightly.compiler.api.expression.Expression;

/**
 * Common interface for plugins
 */
public interface Plugin extends Comparable<Plugin> {

    String SCR_PROP_NAME_BLOCK_NAME = "org.apache.sling.scripting.sightly.plugin.name";
    String SCR_PROP_NAME_PRIORITY = "org.apache.sling.scripting.sightly.plugin.priority";

    /**
     * Given the plugin invocation provide an invoke object which will influence the rendering command
     * stream
     * @param expression the expression used at plugin invocation
     * @param callInfo the parameters given to the plugin
     * @param compilerContext a compiler context providing utility methods to plugins
     * @return an invocation
     * @see PluginInvoke
     */
    PluginInvoke invoke(Expression expression, PluginCallInfo callInfo, CompilerContext compilerContext);

    /**
     * The priority of the plugin
     * @return a numeric value which controls when, relative to other plugins, should
     * this plugin be applied
     */
    int priority();

    /**
     * The name of the plugin
     * @return the plugin name
     */
    String name();
}
