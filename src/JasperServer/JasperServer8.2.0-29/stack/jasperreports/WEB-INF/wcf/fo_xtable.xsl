<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.1"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:fo="http://www.w3.org/1999/XSL/Format"
exclude-result-prefixes="fo">

<xsl:param name="context"/>
<xsl:param name="renderId"/>
<xsl:param name="token"/>
<xsl:param name="imgpath" select="'jpivot/table'"/>

<xsl:param name="tableWidth"/>
<xsl:param name="reportTitle"/>

<xsl:param name="pageHeight"/>
<xsl:param name="pageWidth"/>
<xsl:param name="pageOrientation"/>
<xsl:param name="paper.type" select="'A4'"></xsl:param>

<xsl:output method="xml" version="1.0" omit-xml-declaration="no" indent="yes" encoding="UTF-8"/>

<xsl:template match="xtable-component">

	<!-- make a try at calculating the page size -->
	<xsl:variable name="page.width.calculated.value">
		<xsl:for-each select="/xtable-component/xtr"> <!-- /xtable-component/body/row -->
			<xsl:sort select="count(child::*)" data-type="number" order="descending" />
			<xsl:if test="position() = 1">
				<!-- <xsl:value-of select="concat(count(child::*)*3.8,'cm')"/> -->
				<xsl:value-of select="count(child::*)*3.8"/>
			</xsl:if>
		</xsl:for-each>
	</xsl:variable>

	<xsl:variable name="page.width.calculated">
		<xsl:value-of select="concat($page.width.calculated.value,'cm')"/>
	</xsl:variable>

	<!-- set page width -->
	<xsl:variable name="page.width.portrait.value">
	  <xsl:choose>
		<xsl:when test="$paper.type = 'USletter'">8.5</xsl:when>
		<xsl:when test="$paper.type = '4A0'">1682</xsl:when>
		<xsl:when test="$paper.type = '2A0'">1189</xsl:when>
		<xsl:when test="$paper.type = 'A0'">841</xsl:when>
		<xsl:when test="$paper.type = 'A1'">594</xsl:when>
		<xsl:when test="$paper.type = 'A2'">420</xsl:when>
		<xsl:when test="$paper.type = 'A3'">297</xsl:when>
		<xsl:when test="$paper.type = 'A4'">210</xsl:when>
		<xsl:when test="$paper.type = 'A5'">148</xsl:when>
		<xsl:when test="$paper.type = 'A6'">105</xsl:when>
		<xsl:when test="$paper.type = 'A7'">74</xsl:when>
		<xsl:when test="$paper.type = 'A8'">52</xsl:when>
		<xsl:when test="$paper.type = 'A9'">37</xsl:when>
		<xsl:when test="$paper.type = 'A10'">26</xsl:when>
		<xsl:when test="$paper.type = 'B0'">1000</xsl:when>
		<xsl:when test="$paper.type = 'B1'">707</xsl:when>
		<xsl:when test="$paper.type = 'B2'">500</xsl:when>
		<xsl:when test="$paper.type = 'B3'">353</xsl:when>
		<xsl:when test="$paper.type = 'B4'">250</xsl:when>
		<xsl:when test="$paper.type = 'B5'">176</xsl:when>
		<xsl:when test="$paper.type = 'B6'">125</xsl:when>
		<xsl:when test="$paper.type = 'B7'">88</xsl:when>
		<xsl:when test="$paper.type = 'B8'">62</xsl:when>
		<xsl:when test="$paper.type = 'B9'">44</xsl:when>
		<xsl:when test="$paper.type = 'B10'">31</xsl:when>
		<xsl:when test="$paper.type = 'C0'">917</xsl:when>
		<xsl:when test="$paper.type = 'C1'">648</xsl:when>
		<xsl:when test="$paper.type = 'C2'">458</xsl:when>
		<xsl:when test="$paper.type = 'C3'">324</xsl:when>
		<xsl:when test="$paper.type = 'C4'">229</xsl:when>
		<xsl:when test="$paper.type = 'C5'">162</xsl:when>
		<xsl:when test="$paper.type = 'C6'">114</xsl:when>
		<xsl:when test="$paper.type = 'C7'">81</xsl:when>
		<xsl:when test="$paper.type = 'C8'">57</xsl:when>
		<xsl:when test="$paper.type = 'C9'">40</xsl:when>
		<xsl:when test="$paper.type = 'C10'">28</xsl:when>
		<xsl:when test="$paper.type = 'custom'">
			<!-- <xsl:value-of select="concat($pageWidth,'cm')"/> -->
			<xsl:value-of select="$pageWidth * 10"/>
		</xsl:when>
		<xsl:when test="$paper.type = 'auto'">
			 <xsl:value-of select="$page.width.calculated.value"/> <!-- $page.width.calculated -->
		</xsl:when>
		<xsl:otherwise>8.5in</xsl:otherwise>
	  </xsl:choose>
	</xsl:variable>

	<xsl:variable name="page.width.portrait">
	  <xsl:choose>
		<xsl:when test="$page.width.portrait.value &gt; 10">
			 <xsl:value-of select="concat($page.width.portrait.value, 'mm')"/>
		</xsl:when>
		<xsl:otherwise>
			 <xsl:value-of select="concat($page.width.portrait.value, 'in')"/>
		</xsl:otherwise>
	  </xsl:choose>
	</xsl:variable>

	<!-- set page height -->
	<xsl:variable name="page.height.portrait">
	  <xsl:choose>
		<xsl:when test="$paper.type = 'A4landscape'">210mm</xsl:when>
		<xsl:when test="$paper.type = 'USletter'">11in</xsl:when>
		<xsl:when test="$paper.type = 'USlandscape'">8.5in</xsl:when>
		<xsl:when test="$paper.type = '4A0'">2378mm</xsl:when>
		<xsl:when test="$paper.type = '2A0'">1682mm</xsl:when>
		<xsl:when test="$paper.type = 'A0'">1189mm</xsl:when>
		<xsl:when test="$paper.type = 'A1'">841mm</xsl:when>
		<xsl:when test="$paper.type = 'A2'">594mm</xsl:when>
		<xsl:when test="$paper.type = 'A3'">420mm</xsl:when>
		<xsl:when test="$paper.type = 'A4'">297mm</xsl:when>
		<xsl:when test="$paper.type = 'A5'">210mm</xsl:when>
		<xsl:when test="$paper.type = 'A6'">148mm</xsl:when>
		<xsl:when test="$paper.type = 'A7'">105mm</xsl:when>
		<xsl:when test="$paper.type = 'A8'">74mm</xsl:when>
		<xsl:when test="$paper.type = 'A9'">52mm</xsl:when>
		<xsl:when test="$paper.type = 'A10'">37mm</xsl:when>
		<xsl:when test="$paper.type = 'B0'">1414mm</xsl:when>
		<xsl:when test="$paper.type = 'B1'">1000mm</xsl:when>
		<xsl:when test="$paper.type = 'B2'">707mm</xsl:when>
		<xsl:when test="$paper.type = 'B3'">500mm</xsl:when>
		<xsl:when test="$paper.type = 'B4'">353mm</xsl:when>
		<xsl:when test="$paper.type = 'B5'">250mm</xsl:when>
		<xsl:when test="$paper.type = 'B6'">176mm</xsl:when>
		<xsl:when test="$paper.type = 'B7'">125mm</xsl:when>
		<xsl:when test="$paper.type = 'B8'">88mm</xsl:when>
		<xsl:when test="$paper.type = 'B9'">62mm</xsl:when>
		<xsl:when test="$paper.type = 'B10'">44mm</xsl:when>
		<xsl:when test="$paper.type = 'C0'">1297mm</xsl:when>
		<xsl:when test="$paper.type = 'C1'">917mm</xsl:when>
		<xsl:when test="$paper.type = 'C2'">648mm</xsl:when>
		<xsl:when test="$paper.type = 'C3'">458mm</xsl:when>
		<xsl:when test="$paper.type = 'C4'">324mm</xsl:when>
		<xsl:when test="$paper.type = 'C5'">229mm</xsl:when>
		<xsl:when test="$paper.type = 'C6'">162mm</xsl:when>
		<xsl:when test="$paper.type = 'C7'">114mm</xsl:when>
		<xsl:when test="$paper.type = 'C8'">81mm</xsl:when>
		<xsl:when test="$paper.type = 'C9'">57mm</xsl:when>
		<xsl:when test="$paper.type = 'C10'">40mm</xsl:when>
		<xsl:when test="$paper.type = 'custom'">
			<xsl:value-of select="concat($pageHeight,'cm')"/>
		</xsl:when>
		<xsl:otherwise>11in</xsl:otherwise>
	  </xsl:choose>
	</xsl:variable>

	<xsl:variable name="max-column">
	  <xsl:choose>
		<xsl:when test="$pageOrientation = 'portrait'">
		  <xsl:value-of select="10"/>
		</xsl:when>
		<xsl:otherwise>
		  <xsl:value-of select="15"/>
		</xsl:otherwise>
	  </xsl:choose>
	</xsl:variable>

	<xsl:variable name="column-size"> 
		<xsl:value-of select="count(xtr[1]/xth)"/>
	</xsl:variable>

	<xsl:variable name="page-size"> 
		<xsl:value-of select="ceiling($column-size div $max-column)"/>
	</xsl:variable>

	<xsl:variable name="page-column-width"> 
		<xsl:value-of select="concat(($page.width.portrait.value - 20) div $max-column, 'mm')"/>
	</xsl:variable>

	<xsl:processing-instruction name="cocoon-format">type="text/xslfo"</xsl:processing-instruction>
	<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
		<fo:layout-master-set>
			<fo:simple-page-master
				master-name="simple"
				margin="0.5cm">

				<!-- swap height/width for landscape -->
				<xsl:attribute name="page-height">
				  <xsl:choose>
					<xsl:when test="$pageOrientation = 'portrait'">
					  <xsl:value-of select="$page.height.portrait"></xsl:value-of>
					</xsl:when>
					<xsl:otherwise>
					  <xsl:value-of select="$page.width.portrait"></xsl:value-of>
					</xsl:otherwise>
				  </xsl:choose>
				</xsl:attribute>
				<xsl:attribute name="page-width">
				  <xsl:choose>
					<xsl:when test="$pageOrientation = 'portrait'">
					  <xsl:value-of select="$page.width.portrait"></xsl:value-of>
					</xsl:when>
					<xsl:otherwise>
					  <xsl:value-of select="$page.height.portrait"></xsl:value-of>
					</xsl:otherwise>
				  </xsl:choose>
				</xsl:attribute>

				<fo:region-before extent="1cm"/>
				<fo:region-after extent="1cm"/>
				<fo:region-body margin-bottom="0.5cm" margin-top="0.5cm"/>
			</fo:simple-page-master>
		</fo:layout-master-set>

		<fo:page-sequence master-reference="simple">
			<!-- Page header -->
			<fo:static-content flow-name="xsl-region-before">
			  <fo:block border-bottom-width="1pt"
						border-bottom-style="solid"
						border-bottom-color="black"
						font-weight="bold"
						font-size="8pt">
				<!-- retrieve current page title -->
				<fo:retrieve-marker retrieve-class-name="page-head"/>
			  </fo:block>
			</fo:static-content>

			<!-- Page footer -->
			<fo:static-content flow-name="xsl-region-after">
			  <fo:block padding-top="3pt"
						font-weight="bold"
						font-size="8pt"
						text-align="center">
				<fo:inline font-style="italic"> <!-- INSERT FOOTER TEXT HERE -->
					Powered by JasperServerPro
				</fo:inline>
			  </fo:block>
			</fo:static-content>

			<!-- Page content -->
			<fo:flow flow-name="xsl-region-body">
				<fo:block font-size="8pt"
					font-family="simhei, Verdana, Geneva, Arial, Helvetica, sans-serif">

					<!-- page header -->
					<fo:block>
					  <fo:marker marker-class-name="page-head">
						<!-- FOP compliant implementation, should use fo:leader with later versions -->
						<fo:table table-layout="fixed" width="100%">
						  <fo:table-column column-width="proportional-column-width(4)"/>
						  <fo:table-column column-width="proportional-column-width(1)"/>
						  <fo:table-body>
							<fo:table-row>
							  <fo:table-cell>
								<fo:block text-align="left"> <!-- HEADER TEXT (e.g Company Name) -->
								   JasperServerPro Drill-through Table
								</fo:block>
							  </fo:table-cell>
							  <fo:table-cell>
								<fo:block text-align="right">
								  Page
								  <fo:page-number/>
								  of
								  <fo:page-number-citation ref-id="EndOfDocument"/>
								</fo:block>
							  </fo:table-cell>
							</fo:table-row>
						  </fo:table-body>
						</fo:table>
					  </fo:marker>
					</fo:block>

					<!-- report title -->
					<fo:block font-size="12pt" font-weight="bold" text-align="center" space-after="1em">
					  <xsl:value-of select="$reportTitle"/>
					</fo:block>

					<!-- report content -->
					<fo:block font-size="10pt">
						<xsl:call-template name="xtable-title">
							<xsl:with-param name="title" select="@title"/>
						</xsl:call-template>

						<xsl:call-template name="create-table">
							<xsl:with-param name="page-index" select="0"/>
							<xsl:with-param name="page-size" select="$page-size"/>
							<xsl:with-param name="max-column" select="$max-column"/>
							<xsl:with-param name="page-column-width" select="$page-column-width"/>
						</xsl:call-template>

					</fo:block>
					<!--<fo:block text-align="center" id="EndOfDocument"/>-->
				</fo:block>
				<fo:block text-align="center" id="EndOfDocument"/>
			 </fo:flow>
		</fo:page-sequence>
	</fo:root>
</xsl:template>

<xsl:template name="xtable-title">
<xsl:param name="title"/>
<fo:table table-layout="fixed">
	<xsl:attribute name="width">
		<xsl:value-of select="'100%'"/>
	</xsl:attribute>

  <!-- <xsl:if test="@title"> -->
  <fo:table-column>
	<xsl:attribute name="column-width">
		<xsl:value-of select="'100%'"/>
	</xsl:attribute>
  </fo:table-column>
  
  <fo:table-body>
    <!-- <tr> -->
	<fo:table-row>
		<!-- <th colspan="{@visibleColumns}" class="xtable-title"> -->
		<fo:table-cell border-style="solid" border-width="0.2mm" background-color="#D0D6D9">
			<fo:block text-align="left" padding="2pt" font-size="10pt" font-family="simhei, serif" line-height="14pt" space-before="0.2mm" space-after="0.2mm" text-indent="1mm">
                <xsl:value-of select="$title"/>
			</fo:block>
		<!-- </th> -->
		</fo:table-cell>
    <!-- </tr> -->
	</fo:table-row>
  <!-- </xsl:if> -->
  </fo:table-body>
</fo:table>
</xsl:template>

<xsl:template name="create-table">
   <xsl:param name="page-index"/>
   <xsl:param name="page-size"/>
   <xsl:param name="max-column"/>
   <xsl:param name="page-column-width"/>

   <xsl:if test="$page-index &lt; $page-size">
	   <xsl:call-template name="table-page">
			<xsl:with-param name="page-index" select="$page-index"/>
			<xsl:with-param name="max-column" select="$max-column"/>
			<xsl:with-param name="page-column-width" select="$page-column-width"/>
	   </xsl:call-template>
	   <xsl:call-template name="create-table">
		   <xsl:with-param name="page-index" select="$page-index + 1"/>
		   <xsl:with-param name="page-size" select="$page-size"/>
		   <xsl:with-param name="max-column" select="$max-column"/>
		   <xsl:with-param name="page-column-width" select="$page-column-width"/>
	   </xsl:call-template>
   </xsl:if>
</xsl:template>

<xsl:template name="table-page">
	<xsl:param name="page-index"/>
	<xsl:param name="max-column"/>
	<xsl:param name="page-column-width"/>

	<!-- if the following fo:table is changed, the reference string in PrintServlet::appendDrillThroughTablePdf() also needs to be changed -->
	<fo:table table-layout="fixed">
		<xsl:attribute name="width">
				<xsl:choose>
				<xsl:when test="$tableWidth">
					<xsl:value-of select="concat($tableWidth,'cm')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="'100%'"/>
				</xsl:otherwise>
				</xsl:choose>
		</xsl:attribute>

		<!-- don't add page break if this is the first page -->
		<xsl:if test="$page-index &gt; 0">
			<xsl:attribute name="break-before">
				<xsl:value-of select="'page'"/>
			</xsl:attribute>
		</xsl:if>

		<!-- for all rows ... -->
		<xsl:for-each select="xtr[1]/xth|xtr[1]/xtd">
			<xsl:if test="(position() &gt; ($max-column * $page-index))">
			<xsl:if test="(position() &lt; ($max-column * ($page-index + 1)) + 1)">
				<fo:table-column>
					<xsl:attribute name="column-width">
						<xsl:value-of select="$page-column-width"/>
					</xsl:attribute>
				</fo:table-column>
			</xsl:if>
			</xsl:if>
		</xsl:for-each>

		<fo:table-body>
			<xsl:apply-templates select="xtr">
				<xsl:with-param name="from-index" select="($max-column * $page-index)" />
				<xsl:with-param name="to-index" select="($max-column * ($page-index + 1))" />
			</xsl:apply-templates>
		</fo:table-body>
	</fo:table>
</xsl:template>

<xsl:template match="xtr">
	<xsl:param name="from-index"/>
	<xsl:param name="to-index"/>
	<fo:table-row>
		<!-- skip the last row which has only one column -->
		<xsl:if test="count(xth|xtd) &gt; 1">
			<xsl:for-each select="xth|xtd">
				<xsl:if test="position() &gt; ($from-index)">
				<xsl:if test="position() &lt; ($to-index + 1)">
					<fo:table-cell border-style="solid" border-width="0.2mm" background-color="{@background-color}">
						<!-- <fo:table-cell border="solid black 0.5pt" padding="2pt" border-collapse="collapse"> -->
						<fo:block hyphenate="true" text-align="{@align}" padding="2pt" font-size="10pt" font-family="simhei, serif" line-height="14pt" space-before="0.2mm" space-after="0.2mm" text-indent="1mm">
							<xsl:apply-templates/>
						</fo:block>
					</fo:table-cell>
				</xsl:if>
				</xsl:if>
			</xsl:for-each>
		</xsl:if>
	</fo:table-row>
</xsl:template>

<xsl:template match="*|@*|node()">
	<xsl:copy>
		<xsl:apply-templates select="*|@*|node()"/>
	</xsl:copy>
</xsl:template>

</xsl:stylesheet>
