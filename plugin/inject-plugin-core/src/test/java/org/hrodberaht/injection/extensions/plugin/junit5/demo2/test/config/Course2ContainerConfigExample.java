/*
 * Copyright (c) 2017 org.hrodberaht
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.hrodberaht.injection.extensions.plugin.junit5.demo2.test.config;

import org.hrodberaht.injection.extensions.plugin.junit5.demo2.service.MyResource;
import org.hrodberaht.injection.plugin.junit.ContainerContextConfigBase;
import org.hrodberaht.injection.plugin.junit.plugins.ResourcePlugin;
import org.hrodberaht.injection.core.stream.InjectionRegistryBuilder;

/**
 * Inject extension TDD
 *
 * @author Robert Alexandersson
 * 2011-05-03 20:31
 * @created 1.0
 * @since 1.0
 */
public class Course2ContainerConfigExample extends ContainerContextConfigBase {

    @Override
    public void register(InjectionRegistryBuilder registryBuilder) {
        activatePlugin(ResourcePlugin.class);
        registryBuilder
                .scan(() -> "org.hrodberaht.injection.extensions.plugin.junit5.demo2.service")
                .resource(builder ->
                        builder
                                .resource("myResource", new MyResource("named"))
                                .resource("myMappedResource", new MyResource("mapped"))
                                .resource(new MyResource("typed"))

                )
        ;
    }
}