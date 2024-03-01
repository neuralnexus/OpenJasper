/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.jaspersoft.jasperserver.jaxrs.keys;

import com.jaspersoft.jasperserver.crypto.JrsKeystore;
import com.jaspersoft.jasperserver.crypto.KeyProperties;
import com.jaspersoft.jasperserver.crypto.KeystoreManager;
import com.jaspersoft.jasperserver.crypto.properties.KeyAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Path("/keys")
public class KeysJaxrsService {

    @Qualifier("keystoreManager")
    @Autowired
    KeystoreManager keystoreManager;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<KeyProps> getKeyAliases() {
        final JrsKeystore keystore = keystoreManager.getKeystore(null);
        List<KeyProperties> keyPropertiesList = keystore.getKeyProperties();

        List<KeyProps> keyPropsList = keyPropertiesList.stream()
                .filter(kp -> kp.isKeyVisible() && Objects.equals(kp.getKeyAlg(), KeyAlgorithm.AES))
                .map(KeyProps::new)
                .collect(Collectors.toList());
        if (keyPropsList.isEmpty())
        {
            // jersey maps null result to 204 No Content
            return null;
        } else {
            return keyPropsList;
        }
    }
    
    static class KeyProps {
        String alias;
        String algorithm;
        String label;

        public KeyProps() {}

        public KeyProps(KeyProperties kp) {
            super();
            this.setAlgorithm(kp.getKeyAlg());
            this.setAlias(kp.getKeyAlias());
            this.setLabel(kp.getKeyLabel());
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public String getAlgorithm() {
            return algorithm;
        }

        public void setAlgorithm(String algorithm) {
            this.algorithm = algorithm;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }

}
