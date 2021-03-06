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

package org.hrodberaht.injection.plugin.junit.spring.config;

import org.hrodberaht.injection.core.stream.InjectionRegistryBuilder;
import org.hrodberaht.injection.plugin.junit.ContainerContextConfigBase;
import org.hrodberaht.injection.plugin.junit.plugins.DataSourcePlugin;
import org.hrodberaht.injection.plugin.junit.plugins.SpringExtensionPlugin;
import org.hrodberaht.injection.plugin.junit.spring.beans.incubator.SpringBeanReplacementProxy;

import javax.sql.DataSource;

import static org.hrodberaht.injection.plugin.junit.spring.config.JUnitConfigExample._package;

public class JUnitJavaConfigComboExample extends ContainerContextConfigBase {


    @Override
    public void register(InjectionRegistryBuilder registryBuilder) {
        String dataSourceName = "MyDataSource";
        DataSourcePlugin dataSourcePlugin = activatePlugin(
                new DataSourcePlugin.DataSourcePluginBuilder().usingJavaContext().build()
        );
        DataSource dataSource = dataSourcePlugin.createDataSource(dataSourceName);

        dataSourcePlugin.loadSchema(dataSource, "sql");
        dataSourcePlugin.loadSchema(dataSource, "sql/insert");

        activatePlugin(SpringExtensionPlugin.class)
                .springConfig(
                        SpringConfigJavaComboSample.class,
                        SpringBeanReplacementProxy.class);

        registryBuilder.scan(() -> _package);
    }

}
