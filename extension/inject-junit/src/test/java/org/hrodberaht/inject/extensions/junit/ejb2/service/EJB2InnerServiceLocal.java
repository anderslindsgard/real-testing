package org.hrodberaht.inject.extensions.junit.ejb2.service;

import javax.ejb.EJBLocalObject;

/**
 * Unit Test JUnit (using @Inject)
 *
 * @author Robert Alexandersson
 *         2010-okt-11 21:57:59
 * @version 1.0
 * @since 1.0
 */
public interface EJB2InnerServiceLocal extends EJBLocalObject {
    public String getSomething(Long id);
}