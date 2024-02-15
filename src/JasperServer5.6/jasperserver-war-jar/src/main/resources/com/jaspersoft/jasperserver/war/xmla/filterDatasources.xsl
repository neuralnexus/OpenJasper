<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fn="http://www.w3.org/2005/02/xpath-functions" xmlns:uu="urn:schemas-microsoft-com:xml-analysis:rowset">

	<!-- This transformation is used on XML/A SOAP DISCOVER results,
	it will first filter the datasources keeping only public or the selected tenant.
	Then it will clean out the TenantID=...; postfix -->

	<xsl:output method="xml" indent="yes" />

	<!--  the 'organization_unknown_placeholder' is replaced to the real
	organization name before the transformation. -->

	<xsl:variable name="organization" select="'organization_unknown_placeholder'" />
	<xsl:variable name="key" select="'TenantID='" />
	<xsl:variable name="value" select="concat($key,$organization,';')" />

	<!-- This template applies to <Row/> element.
	it will only copy <DataSourcesName/>s that does not contain the tenant key at all (pubic)
	or those who belong to the specific tenant value -->

	<xsl:template match="uu:row">
		<xsl:if
			test="not(contains(uu:DataSourceName,$key)) or contains(uu:DataSourceName,$value)">
			<xsl:copy>
				<xsl:apply-templates />
			</xsl:copy>
		</xsl:if>
	</xsl:template>

	<!-- when coping the text of the <DataSourceName/>s and <DataSourceInfo/>
	remove the TenantID postfix if it exists -->
	
	<xsl:template match="uu:DataSourceName/text()|uu:DataSourceInfo/text()">
		<xsl:choose>
			<xsl:when test="contains(., $value)">
				<xsl:call-template name="replace">
					<xsl:with-param name="text" select="." />
					<xsl:with-param name="replace" select="$value" />
					<xsl:with-param name="by" select="''" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:copy>
					<xsl:apply-templates />
				</xsl:copy>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>


	<xsl:template match="@* | node()">
		<xsl:copy>
			<xsl:apply-templates />
		</xsl:copy>
	</xsl:template>

	<!-- this template just copies text after replacing one sting by another string -->
	
	<xsl:template name="replace">
		<xsl:param name="text" />
		<xsl:param name="replace" />
		<xsl:param name="by" />
		<xsl:choose>
			<xsl:when test="contains($text, $replace)">
				<xsl:value-of select="substring-before($text,$replace)" />
				<xsl:value-of select="$by" />
				<xsl:call-template name="replace">
					<xsl:with-param name="text"
						select="substring-after($text,$replace)" />
					<xsl:with-param name="replace" select="$replace" />
					<xsl:with-param name="by" select="$by" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$text" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet> 