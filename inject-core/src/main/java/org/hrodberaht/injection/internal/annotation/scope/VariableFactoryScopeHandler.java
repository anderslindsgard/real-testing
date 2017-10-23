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

package org.hrodberaht.injection.internal.annotation.scope;

import org.hrodberaht.injection.internal.ScopeContainer;
import org.hrodberaht.injection.register.VariableInjectionFactory;

/**
 * Created by IntelliJ IDEA.
 * User: Robert Work
 * Date: 2010-aug-18
 * Time: 15:51:41
 * To change this template use File | Settings | File Templates.
 */
public class VariableFactoryScopeHandler implements VariableScopeHandler {

    VariableInjectionFactory injectionFactory;

    public VariableFactoryScopeHandler(VariableInjectionFactory theFactory) {
        this.injectionFactory = theFactory;
    }

    @SuppressWarnings(value = "unchecked")
    public Class getInstanceClass(Object variable) {
        return injectionFactory.getInstanceClass(variable);
    }

    public void addInstance(Object instance) {
        throw new IllegalAccessError("Factory can not have instance set");
    }

    public Object getInstance() {
        return null;
    }

    public ScopeContainer.Scope getScope() {
        return null;
    }

    public boolean isInstanceCreated() {
        return true;
    }
}
