<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:param name="viewUri"/> <!-- ja-pro:model name, originally linkParameters -->

<xsl:template match="cat-edit">
  <table cellpadding="1" cellspacing="0" border="1" id="{$renderId}">
    <xsl:apply-templates select="cat-category"/>
    <!-- buttons -->
    <tr>
      <td align="right">
        <div align="right">
          <input type="submit" value="{@ok-title}" name="{@ok-id}" onclick="pageAlert=false"/>
          <xsl:text> </xsl:text>
          <input type="submit" value="{@cancel-title}" name="{@cancel-id}" onclick="pageAlert=false"/>
        </div>
      </td>
    </tr>
  </table>
</xsl:template>

<xsl:template match="cat-category">
  <tr>
    <th align="left" class="xform-title"> <!-- ja-pro:navi-axis -->
      <xsl:choose>
        <!-- the first category gets the close button -->
        <xsl:when test="position() = 1">

          <table border="0" cellspacing="0" cellpadding="0" width="100%">
            <tr>
              <th align="left" class="xform-title"> <!-- ja-pro:navi-axis -->
                <img src="{$context}/jpivot/navi/{@icon}" onclick="pageAlert=false" width="9" height="9"/>
                <xsl:text> </xsl:text>
                <xsl:value-of select="@name"/>
              </th>
              <td align="right" class="xform-close-button">
                <input type="image" src="{$context}/wcf/form/cancel.png" value="{../@cancel-title}" name="{../@cancel-id}" title="{@close-title}" onclick="pageAlert=false" width="16" height="16"/>
              </td>
            </tr>
          </table>
        </xsl:when>
        <xsl:otherwise>
          <img src="{$context}/jpivot/navi/{@icon}" onclick="pageAlert=false" width="9" height="9"/>
          <xsl:text> </xsl:text>
          <xsl:value-of select="@name"/>
        </xsl:otherwise>
      </xsl:choose>
    </th>
  </tr>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="cat-item">
  <tr>
    <td class="navi-hier">
      <div style="margin-left: 1em">
        <xsl:apply-templates select="cat-button"/>
        <xsl:apply-templates select="move-button"/>
        <xsl:text> </xsl:text>
        <xsl:choose>
          <xsl:when test="@id">
            <a href="?{$linkParameters}&amp;{$token}&amp;{@id}=x&amp;d=z"> <!-- ja-pro: add model name, and support drill-through in separate browser window -->
              <xsl:value-of select="@name"/>
            </a>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="@name"/>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:text> </xsl:text>
        <xsl:apply-templates select="property-button"/>
        <xsl:apply-templates select="function-button"/>
        <xsl:apply-templates select="slicer-value"/>
      </div>
    </td>
  </tr>
</xsl:template>

<xsl:template match="slicer-value">
  <xsl:text> (</xsl:text>
  <xsl:value-of select="@level"/>
  <xsl:text>=</xsl:text>
  <xsl:value-of select="@label"/>
  <xsl:text>)</xsl:text>
</xsl:template>

<xsl:template match="cat-button[@icon]">
  <input border="0" type="image" src="{$context}/jpivot/navi/{@icon}" name="{@id}" title="@{title}" onclick="pageAlert=false" width="9" height="9"/>
  <xsl:text> </xsl:text>
</xsl:template>

<xsl:template match="cat-button">
  <img src="{$context}/jpivot/navi/empty.png" onclick="pageAlert=false" width="9" height="9"/>
  <xsl:text> </xsl:text>
</xsl:template>


<xsl:template match="property-button">
  <input border="0" type="image" src="{$context}/jpivot/navi/properties.png" name="{@id}" onclick="pageAlert=false"/>
  <xsl:text> </xsl:text>
</xsl:template>

<xsl:template match="function-button">
  <input border="0" type="image" src="{$context}/jpivot/navi/functions.png" name="{@id}" onclick="pageAlert=false"/>
  <xsl:text> </xsl:text>
</xsl:template>


</xsl:stylesheet>
