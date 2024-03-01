<?xml version="1.0"?>
<xsl:stylesheet version="3.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:sun="http://java.sun.com/xml/ns/javaee"
                exclude-result-prefixes="sun">

   <xsl:output method="xml" encoding="utf-8" indent="yes" omit-xml-declaration="yes"/>
      <xsl:strip-space elements="*"/>
        <xsl:template match="@*|node()">
            <xsl:copy>
                <xsl:apply-templates select="@*|node()"/>
            </xsl:copy>
        </xsl:template>

    <xsl:template match="comment()[contains(., 'Start of servlets used for scalable-adhoc architecture')]" />

    <xsl:template match="comment()[contains(., 'Below servlets adhocQueryProxy, inputControlValuesProxy and domainQueryProxy')]" />

    <xsl:template match="comment()[contains(., 'End of servlets used for scalable-adhoc architecture')]" />

    <xsl:template match="sun:servlet[sun:servlet-name=('adhocQueryProxy')]"/>

    <xsl:template match="sun:servlet[sun:servlet-name=('inputControlValuesProxy')]"/>

    <xsl:template match="sun:servlet[sun:servlet-name=('domainQueryProxy')]"/>

    <xsl:template match="sun:servlet-mapping[sun:servlet-name=('adhocQueryProxy')]"/>

    <xsl:template match="sun:servlet-mapping[sun:servlet-name=('inputControlValuesProxy')]"/>

    <xsl:template match="sun:servlet-mapping[sun:servlet-name=('domainQueryProxy')]"/>

</xsl:stylesheet>