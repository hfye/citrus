/*
 * Copyright 2006-2012 the original author or authors.
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

package com.consol.citrus.ssh.config;

import com.consol.citrus.config.util.BeanDefinitionParserUtils;
import com.consol.citrus.config.xml.AbstractServerParser;
import com.consol.citrus.server.AbstractServer;
import com.consol.citrus.ssh.server.SshServer;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Parser for the configuration of an SSH server
 * 
 * @author Roland Huss, Christoph Deppisch
 */
public class SshServerParser extends AbstractServerParser {

    @Override
    protected void parseServer(BeanDefinitionBuilder builder, Element element, ParserContext parserContext) {
        BeanDefinitionParserUtils.setPropertyValue(builder, element.getAttribute("port"), "port");
        BeanDefinitionParserUtils.setPropertyValue(builder, element.getAttribute("host-key-path"), "hostKeyPath");
        BeanDefinitionParserUtils.setPropertyValue(builder, element.getAttribute("user"), "user");
        BeanDefinitionParserUtils.setPropertyValue(builder, element.getAttribute("password"), "password");
        BeanDefinitionParserUtils.setPropertyValue(builder, element.getAttribute("allowed-key-path"), "allowedKeyPath");
    }

    @Override
    protected Class<? extends AbstractServer> getServerClass() {
        return SshServer.class;
    }
}
