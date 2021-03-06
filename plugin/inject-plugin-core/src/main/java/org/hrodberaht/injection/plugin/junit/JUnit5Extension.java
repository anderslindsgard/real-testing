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

package org.hrodberaht.injection.plugin.junit;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Optional;

import static org.junit.platform.commons.util.Preconditions.notNull;

public class JUnit5Extension implements BeforeAllCallback, AfterAllCallback, TestInstancePostProcessor,
        BeforeEachCallback, AfterEachCallback, ParameterResolver {

    private static final Logger LOG = LoggerFactory.getLogger(JUnit5Extension.class);

    private JUnitContext jUnitContext;

    private static Class<?> getRequiredTestClass(ExtensionContext context) {
        notNull(context, "ExtensionContext must not be null");
        return context.getTestClass().orElseThrow(
                () -> new IllegalStateException("JUnit failed to supply the test class in the ExtensionContext"));
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        Class clazz = getRequiredTestClass(extensionContext);
        LOG.info("Starting tests for {}", clazz.getName());
        jUnitContext = new JUnitContext(clazz);
        jUnitContext.runBeforeClass();
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        jUnitContext.runAfterClass();
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) throws Exception {
        jUnitContext.runAfterTest(getName(extensionContext));
    }

    private String getName(ExtensionContext extensionContext) {
        Optional<Method> testMethod = extensionContext.getTestMethod();
        if (testMethod.isPresent()) {
            return testMethod.get().getName();
        }
        return "noName";
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        jUnitContext.runBeforeTest(false, getName(extensionContext));
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        //Parameter parameter = parameterContext.getParameter();
        //Executable executable = parameter.getDeclaringExecutable();
        //return (executable instanceof Constructor && AnnotationUtil.hasAnnotation(executable, Inject.class));
        return false;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        //Class<?> testClass = getRequiredTestClass(extensionContext);
        //return jUnitContext.get(testClass);
        return null;
    }

    @Override
    public void postProcessTestInstance(Object test, ExtensionContext extensionContext) throws Exception {
        jUnitContext.activateContainer();
        jUnitContext.createTest(() -> test);
    }
}
