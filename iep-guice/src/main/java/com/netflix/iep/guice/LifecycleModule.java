/*
 * Copyright 2014-2016 Netflix, Inc.
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
package com.netflix.iep.guice;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.ProvisionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Provider;
import java.lang.reflect.Method;

/**
 * Helper for listening to injection events and invoking the PostConstruct and PreDestroy
 * annotated methods.
 */
public class LifecycleModule extends AbstractModule {

  private static final Logger LOGGER = LoggerFactory.getLogger(LifecycleModule.class);

  private static class BindingListener implements ProvisionListener {
    private PreDestroyList preDestroyList;

    BindingListener(PreDestroyList preDestroyList) {
      this.preDestroyList = preDestroyList;
    }

    @Override public <T> void onProvision(ProvisionInvocation<T> provisionInvocation) {
      T value = provisionInvocation.provision();
      AnnotationUtils.invokePostConstruct(LOGGER, value, preDestroyList);
    }
  }

  @Override protected void configure() {
    PreDestroyList list = new PreDestroyList();
    bindListener(Matchers.any(), new BindingListener(list));
    bind(PreDestroyList.class).toInstance(list);
  }

  @Override public boolean equals(Object obj) {
    return obj != null && getClass().equals(obj.getClass());
  }

  @Override public int hashCode() {
    return getClass().hashCode();
  }
}
