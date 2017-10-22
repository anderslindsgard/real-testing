package org.hrodberaht.injection.plugin.junit.spring.config;


import org.hrodberaht.injection.plugin.junit.ContainerContextConfigBase;
import org.hrodberaht.injection.plugin.junit.plugins.DataSourcePlugin;
import org.hrodberaht.injection.plugin.junit.plugins.SpringExtensionPlugin;
import org.hrodberaht.injection.stream.InjectionRegistryBuilder;

import javax.sql.DataSource;

/**
 * Unit Test JUnit (using @Inject)
 *
 * @author Robert Alexandersson
 * 2010-okt-11 19:37:42
 * @version 2.0
 * @since 2.0
 */
public class SpringJavaConfigExample2 extends ContainerContextConfigBase {

    public SpringJavaConfigExample2() {
        final String dataSourceName = "jdbc/MyDataSource";

        // Prepare the datasource
/*
        DataSource dataSource = createDataSource(dataSourceName);
        addResourceCrator(dataSourceName, dataSource);

        // Load reusable test data to the datasource

        junitSQLContainerService.addSQLSchemas(dataSourceName, "sql");
        junitSQLContainerService.addSQLSchemas(dataSourceName, "sql/insert");

        // Adding the spring config, will combine the IoC of the tests and the spring config
        loadJavaSpringConfig(SpringConfigJavaSample2.class);
*/
    }


    @Override
    protected void register(InjectionRegistryBuilder registryBuilder) {
        String dataSourceName = "MyDataSource";
        DataSourcePlugin dataSourcePlugin = activatePlugin(DataSourcePlugin.class);
        DataSource dataSource = dataSourcePlugin.getCreator(DataSource.class).create(dataSourceName);

        dataSourcePlugin.loadSchema(dataSource, "inject-spring-plugin/src/test/resources/sql");
        dataSourcePlugin.loadSchema(dataSource, "inject-spring-plugin/src/test/resources/sql/insert");

        activatePlugin(SpringExtensionPlugin.class).loadConfig(SpringConfigJavaSample2.class);

    }
}
