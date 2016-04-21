/*
 * Copyright 2006-2016 the original author or authors.
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

package com.consol.citrus.restdocs.config.handler;

import com.consol.citrus.restdocs.config.xml.*;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * @author Christoph Deppisch
 * @since 2.6
 */
public class RestDocConfigNamespaceHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser("documentation", new RestDocDocumentationParser());
        registerBeanDefinitionParser("configurer", new RestDocConfigurerParser());
        registerBeanDefinitionParser("client-interceptor", new RestDocClientInterceptorParser());
    }
}
