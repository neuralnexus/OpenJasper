<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>

<xsl:output method="html" indent="no" encoding="utf-8"/>
<xsl:param name="context"/>
<xsl:param name="renderId"/>
<xsl:param name="token"/>
<xsl:param name="imgpath" select="'bii/table'"/>

<xsl:template match="/mdxtable">
  <xsl:apply-templates select="slicer/member"/>
</xsl:template>

<xsl:template match="member[@href]">
	<xsl:choose>
		<xsl:when test="position() = 1">
			<xsl:text> </xsl:text>
		</xsl:when>
		<xsl:otherwise>
			<xsl:text>, </xsl:text>
		</xsl:otherwise>
	</xsl:choose>
  <span class="slicer-{@style}">
    <a href="{@href}">
      <xsl:value-of select="@level"/>
      <xsl:text>=</xsl:text>
      <xsl:value-of select="@caption"/>
    </a>
  </span>
</xsl:template>

<xsl:template match="member">
	<xsl:choose>
		<xsl:when test="position() = 1">
			<xsl:text> </xsl:text>
		</xsl:when>
		<xsl:otherwise>
			<xsl:text>, </xsl:text>
		</xsl:otherwise>
	</xsl:choose>
  <span class="slicer-{@style}">
    <xsl:value-of select="@level"/>
    <xsl:text>=</xsl:text>
    <xsl:value-of select="@caption"/>
  </span>
  <xsl:apply-templates select="property"/>
</xsl:template>

<xsl:template match="property">
  <xsl:text>, </xsl:text>
  <xsl:value-of select="@name"/>
  <xsl:text>=</xsl:text>
  <xsl:value-of select="@value"/>
</xsl:template>

</xsl:stylesheet>
