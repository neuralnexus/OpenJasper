<?xml version="1.0" encoding="iso-8859-1"?>

<!-- renders the JPivot Table -->

<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns="http://www.w3.org/TR/REC-html40"
xmlns:x="urn:schemas-microsoft-com:office:excel" >

<!-- the id of the table for httpUnit -->
<xsl:param name="renderId"/>
<xsl:param name="context"/>
<xsl:param name="imgpath" select="'jpivot/table'"/>
<xsl:param name="chartimage"/>
<xsl:param name="chartheight" select="'50'"/>
<xsl:param name="chartwidth" select="'150'"/>
<xsl:param name="reportTitle"/>
<xsl:param name="table-border" select="'1'"/>

<xsl:output method="html" indent="yes" encoding="utf-8"/>

<xsl:template match="xtable-component">
    <html xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:x="urn:schemas-microsoft-com:office:excel" xmlns="http://www.w3.org/TR/REC-html40">
        <head>
          <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
          <meta name="ProgId content=Excel.Sheet"/>
          <style>
            table
            {mso-displayed-decimal-separator:"\.";
            mso-displayed-thousand-separator:"\,";}

            @page
                    {margin:1.0in .75in 1.0in .75in;
                    mso-header-margin:.5in;
                    mso-footer-margin:.5in;}
            tr
                    {mso-height-source:auto;}
            col
                    {mso-width-source:auto;}
            br
                    {mso-data-placement:same-cell;}
            .basestyle
                    {mso-number-format:General;
                    text-align:general;
                    vertical-align:bottom;
                    white-space:nowrap;
                    mso-rotate:0;
                    mso-background-source:auto;
                    mso-pattern:auto;
                    color:windowtext;
                    font-size:10.0pt;
                    font-weight:400;
                    font-style:normal;
                    text-decoration:none;
                    font-family:Arial;
                    mso-generic-font-family:auto;
                    mso-font-charset:0;
                    border:none;
                    mso-protection:locked visible;
                    mso-style-name:Normal;
                    mso-style-id:0;}

              .col-heading
                    {mso-style-parent:basestyle;
                    font-weight:700;
                    font-family:"Arial Unicode MS";
                    mso-generic-font-family:auto;
                    mso-font-charset:0;
                    text-align:left;
                    vertical-align:middle;
                    border:.5pt solid black;
                    mso-pattern:auto none;}

              .row-heading
                    {mso-style-parent:basestyle;
                    font-weight:700;
                    font-family:"Arial Unicode MS";
                    mso-generic-font-family:auto;
                    mso-font-charset:0;
                    text-align:left;
                    vertical-align:top;
                    border-top:.5pt solid black;
                    border-right:.5pt solid black;
                    border-bottom:none;
                    border-left:.5pt solid black;
                    mso-pattern:auto none;}

              .dataitem
                    {mso-style-parent:basestyle;
                    font-family:"Arial Unicode MS";
                    mso-generic-font-family:auto;
                    mso-font-charset:0;
                    <!--mso-number-format:"\#\,\#\#0";-->
                    <!-- mso-number-format:Standard;-->
                    text-align:right;
                    vertical-align:top;
                    border:.5pt solid black;
                    background:white;
                    mso-pattern:auto none;}
            </style>

        </head>
        <body>
              <!-- Title -->
              <xsl:if test="$reportTitle">
                <h2><xsl:value-of select="$reportTitle"/></h2>
              </xsl:if>
                <!-- Table -->
              <table xmlns:x="urn:schemas-microsoft-com:office:excel" border="1">

				<xsl:if test="@width">
				  <xsl:attribute name="width">
					<xsl:value-of select="@width"/>
				  </xsl:attribute>
				</xsl:if>

				<xsl:attribute name="id">
				  <xsl:call-template name="xtable-renderId"/>
				</xsl:attribute>

				<xsl:attribute name="border">
				  <xsl:call-template name="xtable-border"/>
				</xsl:attribute>

				<xsl:call-template name="xtable-title"/>
				<xsl:call-template name="xtable-error"/>
				<!--<xsl:apply-templates/>-->
				<xsl:apply-templates/>

              </table>
        </body>
    </html>
</xsl:template>

<xsl:template name="xtable-renderId">
  <xsl:choose>
    <xsl:when test="@renderId">
      <xsl:value-of select="@renderId"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$renderId"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="xtable-border">
  <xsl:choose>
    <xsl:when test="@border">
      <xsl:value-of select="@border"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$table-border"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="xtable-title">
  <xsl:if test="@title or @closeId or @editId">
    <tr>
      <th colspan="{@visibleColumns}" class="xtable-title">
        <table border="0" cellspacing="0" cellpadding="0" width="100%">
          <tr>
            <xsl:if test="@editId">
              <th align="left" class="xtable-title">
                <!--<input type="image" src="{$context}/wcf/table/edit.png" name="{@editId}" title="Edit Properties" onclick="pageAlert=false" width="16" height="16"/>-->
              </th>
            </xsl:if>
            <xsl:if test="@title">
              <th align="left" class="xtable-title" width="100%">
                <xsl:text>&#160;</xsl:text>
                <xsl:value-of select="@title"/>
              </th>
            </xsl:if>
            <xsl:if test="@closeId">
              <td align="right" class="xtable-title">
				<!--<input type="image" src="{$context}/wcf/form/cancel.png" name="{@closeId}" onclick="pageAlert=false" width="16" height="16"/>-->
              </td>
            </xsl:if>
            <xsl:if test="@closeWinId">
              <td align="right" class="xtable-title">
				<!--<input type="image" src="{$context}/wcf/form/cancel.png" name="{@closeId}" onclick="pageAlert=false;clickClose()" width="16" height="16"/>-->
              </td>
            </xsl:if>
          </tr>
        </table>
      </th>
    </tr>
  </xsl:if>
</xsl:template>

<xsl:template name="xtable-error">
  <xsl:if test="@error">
    <tr>
      <th colspan="{@visibleColumns}" class="xtable-error">
        <xsl:value-of select="@error"/>
      </th>
    </tr>
  </xsl:if>
</xsl:template>

<xsl:template match="xtr">
  <tr>
    <xsl:apply-templates/>
  </tr>
</xsl:template>

<xsl:template match="xtd">
  <td class="xtable-data">
    <xsl:apply-templates select="@align"/>
    <xsl:apply-templates select="@colspan"/>
    <xsl:apply-templates/>
  </td>
</xsl:template>

<xsl:template name="setcellcolor">
    <xsl:attribute name="bgcolor">
    <!-- for row headings -->
      <xsl:if test="count(../../preceding-sibling::*) mod 2 = 1">
        <xsl:choose>
         <xsl:when test="count(//head/row) mod 2 = 0">
                <xsl:choose>
                     <xsl:when test="count(../preceding-sibling::*) mod 2 = 1">
                        <xsl:value-of select = "'#C0C0C0'"/>
                     </xsl:when>
                    <xsl:otherwise>
                       <xsl:value-of select = "'#F0F0F0'"/>
                    </xsl:otherwise>
                  </xsl:choose>
           </xsl:when>
           <xsl:otherwise>
                <xsl:choose>
                    <xsl:when test="count(../preceding-sibling::*) mod 2 = 1">
                        <xsl:value-of select = "'#F0F0F0'"/>
                    </xsl:when>
                    <xsl:otherwise>
                       <xsl:value-of select = "'#C0C0C0'"/>
                    </xsl:otherwise>
                </xsl:choose>
           </xsl:otherwise>
        </xsl:choose>
       </xsl:if>
       <!-- for column headings -->
       <xsl:if test="count(../../preceding-sibling::*) mod 2 = 0">
        <xsl:choose>
           <xsl:when test="count(../preceding-sibling::*) mod 2 = 1">
             <xsl:value-of select = "'#C0C0C0'"/>
           </xsl:when>
           <xsl:otherwise>
               <xsl:value-of select = "'#F0F0F0'"/>
           </xsl:otherwise>
         </xsl:choose>
       </xsl:if>
      </xsl:attribute>
</xsl:template>

<!-- EXCEL cell format -->
<xsl:template match="cell">
  <td align="right" valign="top" nowrap="nowrap" bgcolor="#FFFFFF">

  <xsl:if test = "not(@value='&#160;')">
      <xsl:if test = "@mso-number-format">
            <xsl:attribute name="class">
                <xsl:value-of select = "'dataitem'"/>
            </xsl:attribute>

             <xsl:attribute name="style">
                <xsl:value-of select="concat('mso-number-format:',@mso-number-format)"/>
           </xsl:attribute>

      </xsl:if>
      <xsl:if test = "@rawvalue">
          <xsl:attribute name="x:num">
            <xsl:value-of select="@rawvalue"/>
          </xsl:attribute>
      </xsl:if>
      <xsl:apply-templates select="drill-through"/>
      <xsl:call-template name="render-label">
          <xsl:with-param name="label">
            <xsl:value-of select="@value"/>
          </xsl:with-param>
      </xsl:call-template>
    </xsl:if>
  </td>
</xsl:template>

<xsl:template name="render-label">
  <xsl:param name="label"/>
  <xsl:choose>
    <xsl:when test="property[@name='link']">
      <a href="{property[@name='link']/@value}" target="_blank">
        <xsl:value-of select="$label"/>
        <xsl:apply-templates select="property"/>
      </a>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$label"/>
      <xsl:apply-templates select="property"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="xtr">
  <tr>
    <xsl:apply-templates/>
  </tr>
</xsl:template>

<xsl:template match="xtd">
  <td class="xtable-data">
    <xsl:apply-templates select="@align"/>
    <xsl:apply-templates select="@colspan"/>
    <xsl:apply-templates/>
  </td>
</xsl:template>

<xsl:template match="xth[@selectId]">
  <th class='col-heading'>
    <xsl:apply-templates select="@colspan"/>
		<!--<input type="image" border="0" name="{@selectId}" src="{$context}/wcf/table/select.png" onclick="pageAlert=false" width="16" height="16"/>-->
    <xsl:apply-templates/>
  </th>
</xsl:template>

<xsl:template match="xth[@sort]">
  <th class='col-heading'>
    <!--<input type="image" border="0" name="{@id}" src="{$context}/wcf/table/sort-{@sort}.png" onclick="pageAlert=false" width="9" height="9"/>-->
    <xsl:text> </xsl:text>
    <xsl:apply-templates/>
  </th>
</xsl:template>

<xsl:template match="xth">
  <th class='col-heading'>
    <xsl:apply-templates select="@colspan"/>
    <xsl:apply-templates/>
  </th>
</xsl:template>

<xsl:template match="*|@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="*|@*|node()"/>
  </xsl:copy>
</xsl:template>

</xsl:stylesheet>
