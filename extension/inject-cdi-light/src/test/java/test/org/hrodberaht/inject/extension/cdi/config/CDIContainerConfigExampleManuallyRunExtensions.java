package test.org.hrodberaht.inject.extension.cdi.config;

import org.hrodberaht.inject.InjectContainer;
import org.hrodberaht.inject.extension.cdi.inner.InjectionRegisterScanCDI;

import javax.sql.DataSource;

/**
 * Unit Test JUnit (using @Inject)
 *
 * @author Robert Alexandersson
 *         2010-okt-11 19:37:42
 * @version 1.0
 * @since 1.0
 */
public class CDIContainerConfigExampleManuallyRunExtensions extends TDDCDIContainerConfigBase {

    public CDIContainerConfigExampleManuallyRunExtensions() {

        // super(new JSEResourceCreator());

        System.setProperty("MyDataSource.driver", "org.hsqldb.jdbcDriver");
        System.setProperty("MyDataSource.url", "jdbc:hsqldb:mem:MyDataSource");
        System.setProperty("MyDataSource.username", "sa");
        System.setProperty("MyDataSource.password", "");

        String dataSourceName = "MyDataSource";
        DataSource dataSource = createDataSource(dataSourceName);
        // Named resource
        addResource(dataSourceName, dataSource);
        addSQLSchemas(dataSourceName, "test");

    }




    @Override
    public InjectContainer createContainer() {
        InjectionRegisterScanCDI serviceModule = new InjectionRegisterScanCDI();
        serviceModule.scanPackage("test.org.hrodberaht.inject.extension.cdi.service");

        /*InjectContainer injectContainer = createAutoScanContainerManuallyRunAfterBeanDiscovery(
                new RegistrationModuleAnnotation[]{serviceModule}, "test.org.hrodberaht.inject.extension.cdi.service2");
        return injectContainer;
                */

        return null;
    }


}
