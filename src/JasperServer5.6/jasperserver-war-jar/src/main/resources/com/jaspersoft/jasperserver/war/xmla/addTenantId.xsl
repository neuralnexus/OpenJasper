<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fn="http://www.w3.org/2005/02/xpath-functions" 
 xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/"
    SOAP-ENV:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/"
   xmlns:uu="urn:schemas-microsoft-com:xml-analysis"
>

		<!-- This transformation is used on XML/A SOAP requests,
		it will find the <DataSourceInfo/> element 
		and will append the TenentID=...; postfix to it. -->
		
        <xsl:output method="xml" indent="yes" />

		<!--  the 'organization_unknown_placeholder' is replaced to the real
		organization name before the transformation.
		the value variable holds the postfix we want to add to the datasource info-->
		
        <xsl:variable name="organization" select="'organization_unknown_placeholder'" />
        <xsl:variable name="key" select="'TenantID='" />
        <xsl:variable name="value" select="concat($key,$organization,';')" />

		<!-- This template applies to the text within the <DataSourceInfo/> element.
		It will first apply the default template which is to copy the content
		and then it adds the value of the value variable -->
        
        <xsl:template match="uu:DataSourceInfo/text()">
                <xsl:copy>
                        <xsl:apply-templates />
                </xsl:copy>
                <xsl:value-of select="$value" />
        </xsl:template>

		<!-- by default all nodes are just copied from input to output -->
        
        <xsl:template match="@* | node()">
                <xsl:copy>
                        <xsl:apply-templates />
                </xsl:copy>
        </xsl:template>

</xsl:stylesheet>
