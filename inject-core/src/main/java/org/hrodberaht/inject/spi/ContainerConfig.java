package org.hrodberaht.inject.spi;

import org.hrodberaht.inject.InjectContainer;
import org.hrodberaht.inject.InjectionRegisterModule;


/**
 * Created with IntelliJ IDEA.
 * User: alexbrob
 * Date: 2012-11-28
 * Time: 11:03
 * To change this template use File | Settings | File Templates.
 */
public interface ContainerConfig {
    void cleanActiveContainer();

    InjectionRegisterModule getActiveRegister();

    InjectContainer getActiveContainer();

    void addSingletonActiveRegistry();

    ResourceCreator getResourceCreator();

    InjectContainer createContainer();


}
