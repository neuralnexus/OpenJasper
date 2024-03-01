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

package com.jaspersoft.jasperserver.dto.common;

import com.jaspersoft.jasperserver.dto.resources.ClientProperty;
import java.util.Arrays;

/**
 * This class could be used to filter out duplicated errorDescriptors by errorCode and parameters
 * but ignoring message differences by adding such instances to Set.
 *
 * @author tetiana.iefimenko
 * @version $Id$
 * @see
 */
public class MessageAgnosticErrorDescriptor extends ErrorDescriptor {

    public MessageAgnosticErrorDescriptor(ErrorDescriptor errorDescriptor) {
        super(errorDescriptor);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof MessageAgnosticErrorDescriptor)) return false;

        MessageAgnosticErrorDescriptor that = (MessageAgnosticErrorDescriptor) other;

        if (getErrorCode() != null ? !getErrorCode().equals(that.getErrorCode()) : that.getErrorCode() != null) return false;
        if (!Arrays.equals(getParameters(), that.getParameters())) return false;
        if ((getProperties() == null & that.getProperties() != null) || (getProperties()!= null & that.getProperties() == null)) return false;
        if (getProperties() != null && that.getProperties() != null) {
            if (!(getProperties().containsAll(that.getProperties()) && that.getProperties().containsAll(getProperties()))) return false;
        }
        if (getDetails() != null ? !getDetails().equals(that.getDetails()) : that.getDetails()!= null) return false;
        if (getErrorUid() != null ? !getErrorUid().equals(that.getErrorUid()) : that.getErrorUid() != null) return false;
        return getException() != null ? getException().equals(that.getException()) : that.getException() == null;
    }

    @Override
    public int hashCode() {
        int result = 0;
        result = 31 * result + (getErrorCode()!= null ? getErrorCode().hashCode() : 0);
        result = 31 * result + Arrays.hashCode(getParameters());
        int propertiesHashCode = 0;
        if (getProperties() != null) {
            for (ClientProperty property : getProperties()) {
                propertiesHashCode += property.hashCode();
            }
        }
        result = 31 * result + propertiesHashCode;
        result = 31 * result + (getDetails() != null ? getDetails().hashCode() : 0);
        result = 31 * result + (getErrorUid() != null ? getErrorUid().hashCode() : 0);
        result = 31 * result + (getException() != null ? getException().hashCode() : 0);
        return result;
    }
}
