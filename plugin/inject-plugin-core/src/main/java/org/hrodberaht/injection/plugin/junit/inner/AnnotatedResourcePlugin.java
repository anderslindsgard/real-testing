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

package org.hrodberaht.injection.plugin.junit.inner;

import org.hrodberaht.injection.core.internal.annotation.InjectionFinder;
import org.hrodberaht.injection.core.internal.annotation.ReflectionUtils;
import org.hrodberaht.injection.core.internal.exception.InjectRuntimeException;
import org.hrodberaht.injection.core.spi.ResourceFactory;
import org.hrodberaht.injection.plugin.junit.api.Plugin;
import org.hrodberaht.injection.plugin.junit.api.PluginContext;
import org.hrodberaht.injection.plugin.junit.api.annotation.ResourcePluginChainableInjectionProvider;
import org.hrodberaht.injection.plugin.junit.api.annotation.ResourcePluginFactory;
import org.hrodberaht.injection.plugin.junit.resources.ChainableInjectionPointProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class AnnotatedResourcePlugin {
    private static final Logger LOG = LoggerFactory.getLogger(AnnotatedResourcePlugin.class);

    private static Map<Class, List<Method>> classResourcePluginFactoryMethodMap = new ConcurrentHashMap<>();
    private static Map<Class, Method> classChainableInjectionProviderMethodMap = new ConcurrentHashMap<>();


    private static Set<Class> supporedAnnotations = new HashSet<>(Arrays.asList(
            ResourcePluginFactory.class,
            ResourcePluginChainableInjectionProvider.class
    ));

    public static boolean containsAnnotations(Plugin plugin) {
        return AnnotatedRunnerBase.containsRunnerAnnotations(plugin, supporedAnnotations);
    }

    public static <T extends Plugin> void inject(PluginContext context, ResourceFactory resourceFactory, T plugin) {
        List<Method> methodList = classResourcePluginFactoryMethodMap.computeIfAbsent(plugin.getClass(), aClass -> {
            List<Method> methodListInner = new ArrayList<>();
            for (Method method : ReflectionUtils.findMethods(aClass)) {
                if (method.getAnnotation(ResourcePluginFactory.class) != null) {
                    LOG.info("found ResourcePluginFactory at {} in {}", method.getName(), aClass.getName());
                    if (!method.isAccessible()) {
                        method.setAccessible(true);
                    }
                    methodListInner.add(method);
                }
            }
            return methodListInner;
        });

        methodList.forEach(method -> {
            try {
                if (method.getParameterCount() == 2
                        && method.getParameterTypes()[0].isAssignableFrom(PluginContext.class)
                        && method.getParameterTypes()[1].isAssignableFrom(ResourceFactory.class)) {
                    method.invoke(plugin, context, resourceFactory);
                } else {
                    throw new InjectRuntimeException("method with ResourcePluginFactory must have a parameter of type ResourceFactory");
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new InjectRuntimeException(e);
            }
        });


    }

    public static <T extends Plugin> ChainableInjectionPointProvider getChainableInjectionPointProvider(T plugin, InjectionFinder injectionFinder) {
        Method methodToReturn = classChainableInjectionProviderMethodMap.computeIfAbsent(plugin.getClass(), aClass -> {
            for (Method method : ReflectionUtils.findMethods(plugin.getClass())) {
                if (method.getAnnotation(ResourcePluginChainableInjectionProvider.class) != null) {
                    return evaluateInjectionProviderMethod(aClass, method);
                }
            }
            return null;
        });
        try {
            if (methodToReturn != null) {
                return (ChainableInjectionPointProvider) methodToReturn.invoke(plugin, injectionFinder);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new InjectRuntimeException(e);
        }
        throw new InjectRuntimeException("Could not find method annotated with ResourcePluginChainableInjectionProvider");
    }

    private static Method evaluateInjectionProviderMethod(Class aClass, Method method) {
        if (!ChainableInjectionPointProvider.class.isAssignableFrom(method.getReturnType())) {
            throw new InjectRuntimeException("method with ResourcePluginChainableInjectionProvider must return a ChainableInjectionPointProvider class");
        } else if (method.getParameterCount() != 1 || !InjectionFinder.class.isAssignableFrom(method.getParameterTypes()[0])) {
            throw new InjectRuntimeException("method with ResourcePluginChainableInjectionProvider must have one parameter of type InjectionFinder");
        } else {
            LOG.info("found ResourcePluginChainableInjectionProvider at {} in {}", method.getName(), aClass.getName());
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            return method;
        }
    }

    public static <T extends Plugin> boolean hasChainableAnnotaion(T plugin) {
        return AnnotatedRunnerBase.containsRunnerAnnotations(plugin,
                new HashSet<>(Arrays.asList(ResourcePluginChainableInjectionProvider.class)));
    }
}
