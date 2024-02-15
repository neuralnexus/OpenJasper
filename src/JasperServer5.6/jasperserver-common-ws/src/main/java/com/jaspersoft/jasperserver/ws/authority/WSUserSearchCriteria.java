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

/**
 * WSUserSearchCriteria.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package com.jaspersoft.jasperserver.ws.authority;

public class WSUserSearchCriteria  implements java.io.Serializable {
    private java.lang.String name;

    private java.lang.String tenantId;

    private java.lang.Boolean includeSubOrgs;

    private com.jaspersoft.jasperserver.ws.authority.WSRole[] requiredRoles;

    private int maxRecords;

    public WSUserSearchCriteria() {
    }

    public WSUserSearchCriteria(
           java.lang.String name,
           java.lang.String tenantId,
           java.lang.Boolean includeSubOrgs,
           com.jaspersoft.jasperserver.ws.authority.WSRole[] requiredRoles,
           int maxRecords) {
           this.name = name;
           this.tenantId = tenantId;
           this.includeSubOrgs = includeSubOrgs;
           this.requiredRoles = requiredRoles;
           this.maxRecords = maxRecords;
    }


    /**
     * Gets the name value for this WSUserSearchCriteria.
     *
     * @return name
     */
    public java.lang.String getName() {
        return name;
    }


    /**
     * Sets the name value for this WSUserSearchCriteria.
     *
     * @param name
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }


    /**
     * Gets the tenantId value for this WSUserSearchCriteria.
     *
     * @return tenantId
     */
    public java.lang.String getTenantId() {
        return tenantId;
    }


    /**
     * Sets the tenantId value for this WSUserSearchCriteria.
     *
     * @param tenantId
     */
    public void setTenantId(java.lang.String tenantId) {
        this.tenantId = tenantId;
    }


    /**
     * Gets the includeSubOrgs value for this WSUserSearchCriteria.
     *
     * @return includeSubOrgs
     */
    public java.lang.Boolean getIncludeSubOrgs() {
        return includeSubOrgs;
    }


    /**
     * Sets the includeSubOrgs value for this WSUserSearchCriteria.
     *
     * @param includeSubOrgs
     */
    public void setIncludeSubOrgs(java.lang.Boolean includeSubOrgs) {
        this.includeSubOrgs = includeSubOrgs;
    }


    /**
     * Gets the requiredRoles value for this WSUserSearchCriteria.
     *
     * @return requiredRoles
     */
    public com.jaspersoft.jasperserver.ws.authority.WSRole[] getRequiredRoles() {
        return requiredRoles;
    }


    /**
     * Sets the requiredRoles value for this WSUserSearchCriteria.
     *
     * @param requiredRoles
     */
    public void setRequiredRoles(com.jaspersoft.jasperserver.ws.authority.WSRole[] requiredRoles) {
        this.requiredRoles = requiredRoles;
    }


    /**
     * Gets the maxRecords value for this WSUserSearchCriteria.
     *
     * @return maxRecords
     */
    public int getMaxRecords() {
        return maxRecords;
    }


    /**
     * Sets the maxRecords value for this WSUserSearchCriteria.
     *
     * @param maxRecords
     */
    public void setMaxRecords(int maxRecords) {
        this.maxRecords = maxRecords;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof WSUserSearchCriteria)) return false;
        WSUserSearchCriteria other = (WSUserSearchCriteria) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true &&
            ((this.name==null && other.getName()==null) ||
             (this.name!=null &&
              this.name.equals(other.getName()))) &&
            ((this.tenantId==null && other.getTenantId()==null) ||
             (this.tenantId!=null &&
              this.tenantId.equals(other.getTenantId()))) &&
            ((this.includeSubOrgs==null && other.getIncludeSubOrgs()==null) ||
             (this.includeSubOrgs!=null &&
              this.includeSubOrgs.equals(other.getIncludeSubOrgs()))) &&
            ((this.requiredRoles==null && other.getRequiredRoles()==null) ||
             (this.requiredRoles!=null &&
              java.util.Arrays.equals(this.requiredRoles, other.getRequiredRoles()))) &&
            this.maxRecords == other.getMaxRecords();
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
        if (getTenantId() != null) {
            _hashCode += getTenantId().hashCode();
        }
        if (getIncludeSubOrgs() != null) {
            _hashCode += getIncludeSubOrgs().hashCode();
        }
        if (getRequiredRoles() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getRequiredRoles());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getRequiredRoles(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        _hashCode += getMaxRecords();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(WSUserSearchCriteria.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.jasperforge.org/jasperserver/ws", "WSUserSearchCriteria"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("name");
        elemField.setXmlName(new javax.xml.namespace.QName("", "name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("tenantId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "tenantId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("includeSubOrgs");
        elemField.setXmlName(new javax.xml.namespace.QName("", "includeSubOrgs"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("requiredRoles");
        elemField.setXmlName(new javax.xml.namespace.QName("", "requiredRoles"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.jasperforge.org/jasperserver/ws", "WSRole"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("maxRecords");
        elemField.setXmlName(new javax.xml.namespace.QName("", "maxRecords"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType,
           java.lang.Class _javaType,
           javax.xml.namespace.QName _xmlType) {
        return
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType,
           java.lang.Class _javaType,
           javax.xml.namespace.QName _xmlType) {
        return
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
