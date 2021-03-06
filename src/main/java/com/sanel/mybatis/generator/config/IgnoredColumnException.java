/**
 *    Copyright 2006-2017 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.sanel.mybatis.generator.config;

import com.sanel.mybatis.generator.api.dom.xml.Attribute;
import com.sanel.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;

import static com.sanel.mybatis.generator.internal.util.StringUtility.stringHasValue;
import static com.sanel.mybatis.generator.internal.util.messages.Messages.getString;

public class IgnoredColumnException extends IgnoredColumn {

    public IgnoredColumnException(String columnName) {
        super(columnName);
    }

    @Override
    public XmlElement toXmlElement() {
        XmlElement xmlElement = new XmlElement("except"); //$NON-NLS-1$
        xmlElement.addAttribute(new Attribute("column", columnName)); //$NON-NLS-1$

        if (stringHasValue(configuredDelimitedColumnName)) {
            xmlElement.addAttribute(new Attribute(
                    "delimitedColumnName", configuredDelimitedColumnName)); //$NON-NLS-1$
        }

        return xmlElement;
    }

    @Override
    public void validate(List<String> errors, String tableName) {
        if (!stringHasValue(columnName)) {
            errors.add(getString("ValidationError.26", //$NON-NLS-1$
                    tableName));
        }
    }
}
