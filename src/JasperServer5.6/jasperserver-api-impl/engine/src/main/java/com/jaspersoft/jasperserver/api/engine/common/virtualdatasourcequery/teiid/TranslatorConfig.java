/*
 * Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery.teiid;

import com.jaspersoft.jasperserver.api.common.virtualdatasourcequery.teiid.TranslatorConfiguration;
import com.jaspersoft.jasperserver.api.engine.common.virtualdatasourcequery.impl.TeiidVirtualDataSourceQueryServiceImpl;
import org.teiid.translator.ExecutionFactory;
import org.teiid.translator.TranslatorException;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Ivan Chan (ichan@jaspersoft.com)
 * @version $Id: TranslatorConfig.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class TranslatorConfig implements TranslatorConfiguration {

    private String productName;
    private String productVersion;
    private String translatorName;
    private String translatorFactoryClass;
    private ExecutionFactory translatorFactory;

    public String getProductVersion() {
        return productVersion;
    }

    public void setProductVersion(String productVersion) {
        this.productVersion = productVersion;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public ExecutionFactory getTranslatorFactory() throws TranslatorException {
        return translatorFactory;
    }

    public void setTranslatorFactory(ExecutionFactory translatorFactory) {
        this.translatorFactory = translatorFactory;
    }

    public String getTranslatorName() {
        return translatorName;
    }

    public void setTranslatorName(String translatorName) {
        this.translatorName = translatorName;
    }

    public String getTranslatorFactoryClass() {
        return translatorFactoryClass;
    }

    public void setTranslatorFactoryClass(String translatorFactoryClass) {
        this.translatorFactoryClass = translatorFactoryClass;
    }

    public void setupTranslator() throws TranslatorException, ClassNotFoundException, NoSuchMethodException, InstantiationException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (translatorFactory != null) return;
        translatorFactory = (ExecutionFactory) Class.forName(translatorFactoryClass).getConstructor().newInstance();
        TeiidVirtualDataSourceQueryServiceImpl.getServer().addTranslator(translatorFactory);
        translatorFactory.start();
    }
}
