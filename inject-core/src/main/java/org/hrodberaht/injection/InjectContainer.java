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

package org.hrodberaht.injection;

import java.lang.annotation.Annotation;

/**
 * Simple Java Utils - Container
 *
 * @author Robert Alexandersson
 * 2010-jun-06 02:06:55
 * @version 1.0
 * @since 1.0
 */
public interface InjectContainer {


    /**
     * Will inject only inner container resources (no extended injections and no post-construct)
     *
     * @param service
     */
    void injectDependencies(Object service);

    /**
     * Will inject all dependencies (inner and extended) as well as post-construct
     *
     * @param service
     */
    void autowireAndPostConstruct(Object service);

    <T, K> T get(Class<T> service, K variable);


    /**
     * @param service
     * @param <T>
     * @return an instance of the service class with qualifier
     */
    <T> T get(Class<T> service);

    /**
     * @param service
     * @param qualifier
     * @param <T>
     * @return an instance of the service class with qualifier
     */
    <T> T get(Class<T> service, String qualifier);

    <T> T get(Class<T> service, Class<? extends Annotation> qualifier);


}
