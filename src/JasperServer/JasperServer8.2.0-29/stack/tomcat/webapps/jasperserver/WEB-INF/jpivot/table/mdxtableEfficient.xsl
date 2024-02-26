<?xml version="1.0" encoding="utf-8"?>

<!-- renders the JPivot Table -->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<!-- the id of the table for httpUnit -->
<xsl:output method="html" indent="no" encoding="utf-8"/>
<xsl:param name="context"/>
<xsl:param name="renderId"/>
<xsl:param name="token"/>
<xsl:param name="imgpath" select="'j'"/>

<!-- Tabelle:  -->
<xsl:param name="maxColHdrLen" select="20"/>

<!-- ja-pro -->
<xsl:param name="linkParameters"/>
<xsl:param name="viewUri"/> <!-- i.e., linkParameters -->
<xsl:param name="olapPage"/>

<xsl:template match="mdxtable">
  <xsl:if test="@message">
    <div class="table-message"><xsl:value-of select="@message"/></div>
  </xsl:if>
  <table border="1" cellspacing="0" cellpadding="2" id="{$renderId}">
    <!--<xsl:apply-templates select="breadcrumbs"/>-->
    <xsl:apply-templates select="head"/>
    <xsl:apply-templates select="body"/>
  </table>
</xsl:template>

<xsl:template match="breadcrumbs | head | body">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="row">
  <tr>
    <xsl:apply-templates/>
  </tr>
</xsl:template>

<xsl:template match="corner">
  <th nowrap="nowrap" class="corner-heading" colspan="{@colspan}" rowspan="{@rowspan}">
    <!--<xsl:apply-templates/>-->
    <!-- &#160; == &nbsp; -->
    <!--<xsl:text>&#160;</xsl:text>-->
	<!-- ja-pro -->
	<table border="0" cellpadding="0" cellspacing="0" width="100%">
		<tr>
			<th nowrap="nowrap" class="corner-heading">&#160;<xsl:value-of select="@dimensions"/></th>
			<td align="right">
				<!-- expand all -->
				<xsl:choose>
					<xsl:when test="@exceeded = 'true'">
						<xsl:choose>
							<!--
								<a href="JavaScript:alert('{@exceededMessage}')">
									<img name="{@id-expand-all}" title="{@title-expand-all}" alt="{@title-expand-all}" class="corner" src="../{$imgpath}/{@img-expand-all}-{@expand-all-mode}.png" disabled="{@expandAllDisabled}" />
								</a>
							-->
							<xsl:when test="not(@expandAllDisabled)">
								<a href="JavaScript:alert('{@exceededMessage}')">
									<img name="{@id-expand-all}" title="{@title-expand-all}" alt="{@title-expand-all}" class="corner" src="../{$imgpath}/{@img-expand-all}-{@expand-all-mode}.png"/>
								</a>
							</xsl:when>
							<!--
							<xsl:otherwise>
							</xsl:otherwise>
							-->
						</xsl:choose>
					</xsl:when>
					<xsl:otherwise>
						<xsl:choose>
							<!--
								<input name="{@id-expand-all}" title="{@title-expand-all}" type="image" class="corner" src="../{$imgpath}/{@img-expand-all}-{@expand-all-mode}.png" disabled="{@expandAllDisabled}" />
							-->
							<xsl:when test="not(@expandAllDisabled)">
								<input id="{@id-expand-all}" name="{@id-expand-all}" title="{@title-expand-all}" type="image" class="corner" src="../{$imgpath}/{@img-expand-all}-{@expand-all-mode}.png"/>
							</xsl:when>
							<!--
							<xsl:otherwise>
							</xsl:otherwise>
							-->
						</xsl:choose>
					</xsl:otherwise>
				</xsl:choose>
				<!-- collapse all -->
				<xsl:choose>
					<!--
						<input name="{@id-collapse-all}" title="{@title-collapse-all}" type="image" class="corner" src="../{$imgpath}/{@img-collapse-all}-{@collapse-all-mode}.png" disabled="{@collapseAllDisabled}"/>
					-->
					<xsl:when test="not(@collapseAllDisabled)">
						<input id="{@id-collapse-all}" name="{@id-collapse-all}" title="{@title-collapse-all}" type="image" class="corner" src="../{$imgpath}/{@img-collapse-all}-{@collapse-all-mode}.png"/>
					</xsl:when>
					<!--
					<xsl:otherwise>
					</xsl:otherwise>
					-->
				</xsl:choose>
				<!-- zoom out all -->
				<xsl:choose>
					<!--
						<input name="{@id-zoom-out-all}" title="{@title-zoom-out-all}" type="image" class="corner" src="../{$imgpath}/{@img-zoom-out-all}-{@zoom-out-all-mode}.png" disabled="{@zoomOutAllDisabled}" />&#160;
					-->
					<xsl:when test="not(@zoomOutAllDisabled)">
						<input id="{@id-zoom-out-all}" name="{@id-zoom-out-all}" title="{@title-zoom-out-all}" type="image" class="corner" src="../{$imgpath}/{@img-zoom-out-all}-{@zoom-out-all-mode}.png" />&#160;
					</xsl:when>
					<!--
					<xsl:otherwise>
					</xsl:otherwise>
					-->
				</xsl:choose>
			</td>
		</tr>
	</table>
  </th>
</xsl:template>


<xsl:template match="column-heading[@indent]">
  <th class="column-heading-{@style}" colspan="{@colspan}" rowspan="{@rowspan}">
    <xsl:call-template name="nowrap"/>
    <div style="margin-top: {@indent}em">
      <xsl:apply-templates/>
    </div>
  </th>
</xsl:template>


<xsl:template match="row-heading[@indent]">
  <th nowrap="nowrap" class="row-heading-{@style}" colspan="{@colspan}" rowspan="{@rowspan}">
    <div style="margin-left: {@indent}em">
      <xsl:apply-templates/>
    </div>
  </th>
</xsl:template>


<xsl:template match="column-heading">
  <th class="column-heading-{@style}" colspan="{@colspan}" rowspan="{@rowspan}">
    <xsl:call-template name="nowrap"/>
    <xsl:apply-templates/>
  </th>
</xsl:template>


<xsl:template match="row-heading">
  <th class="row-heading-{@style}" colspan="{@colspan}" rowspan="{@rowspan}">
    <xsl:call-template name="nowrap"/>
    <xsl:apply-templates/>
  </th>
</xsl:template>


<xsl:template match="heading-heading">
  <th class="heading-heading" colspan="{@colspan}" rowspan="{@rowspan}">
    <xsl:call-template name="nowrap"/>
    <xsl:apply-templates/>
  </th>
</xsl:template>



<xsl:template match="caption">
  <span class="zoom"> <!-- ja-pro:for matching drill-replace span -->
  <xsl:call-template name="render-label">
    <xsl:with-param name="label">
      <xsl:value-of select="@caption"/>
    </xsl:with-param>
  </xsl:call-template>
  </span>
</xsl:template>


<!-- navigation: expand / collapse / leaf node  -->
<xsl:template match="drill-expand | drill-collapse">
  <input type="image" id="{@id}" name="{@id}" title="{@title}" src="../{$imgpath}/{@img}.gif" class="nav"/>
</xsl:template>

<!-- ja-pro   -->
<xsl:template match="drill-position-expand | drill-position-collapse">
  <input type="image" id="{@id}" name="{@id}" title="{@title}" src="../{$imgpath}/{@img}.gif" class="nav"/>
</xsl:template>

<xsl:template match="drill-member-expand | drill-member-collapse">
  <!-- Its disabled - don't output: <input type="hidden" name="{@id}" disabled="true"/> -->
</xsl:template>

<xsl:template match="drill-replace-expand">
	<xsl:choose>
	  <xsl:when test="@id">
		<!-- <a href="?{$token}&amp;{@id}=x&amp;name={$viewUri}&amp;d=z" -->
		<a href="javascript:z('{@id}')" class="zoom" title="{@title}">
		 <span class="zoom_in">
			<xsl:value-of select="@name"/>
		 </span>
		</a>
	  </xsl:when>
	</xsl:choose>
</xsl:template>

<xsl:template match="drill-replace-collapse">
	<xsl:choose>
	  <xsl:when test="@id">
		<!-- <a href="?{$token}&amp;{@id}=x&amp;name={$viewUri}&amp;d=z" class="zoom" title="{@title}"> -->
		<a href="javascript:z('{@id}')" class="zoom" title="{@title}">
		 <span class="zoom_out">
			<xsl:value-of select="@name"/>
		 </span>
		</a>
	  </xsl:when>
	</xsl:choose>
</xsl:template>

<xsl:template match="drill-other">
  <img src="../{$imgpath}/{@img}.gif" class="drill"/>
</xsl:template>

<!-- ja-pro -->
<xsl:template match="drill-member-other">
  <img src="../{$imgpath}/{@img}.gif" class="drill"/>
</xsl:template>

<xsl:template match="drill-position-other">
  <img src="../{$imgpath}/{@img}.gif" class="drill"/>
</xsl:template>

<xsl:template match="drill-replace-other">
  <!--keep consistent with drill replace hyperlink enhancement -->
  <!--<img src="../{$imgpath}/{@img}.gif" border="0" width="14" height="14"/>-->
</xsl:template>

<!-- navigation: sort  name="{@id}" -->
<xsl:template match="sort">
  <input id="{@id}" name="{@id}" title="{@title}" type="image" src="../{$imgpath}/{@mode}.gif" border="0" width="12" height="12"/>
</xsl:template>

<xsl:template match="drill-through">
	<!--<input name="{@id}" title="{@title}" type="image" src="../{$imgpath}/drill-through.gif" border="0" width="14" height="14"/>-->
	<xsl:choose>
	  <xsl:when test="@id">
		<xsl:choose>
			<xsl:when test="@hideLinks = ''">
				<!-- hideLinks is false -->
				<xsl:choose>
					<xsl:when test="@useBrowser = ''">
						<!-- useBrowser is false
						<a href="?{$token}&amp;{@id}=x&amp;name={$viewUri}&amp;d=y" title="{@title}">  -->
						<a href="javascript:lc('{@id}')" title="{@title}">
						 <span>
						  <xsl:value-of select="@value"/>
						 </span>
						</a>
					</xsl:when>
					<xsl:otherwise>
						<!-- enable drill through dialog window
						<a href="javascript:launch('../{$olapPage}?{$linkParameters}&amp;{$token}&amp;{@id}=x&amp;d=x')" title="{@title}">  -->
						<a href="javascript:ld('{@id}')" title="{@title}">
						 <span>
						  <xsl:value-of select="@value"/>
						 </span>
						</a>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
			  <!-- hideLinks is true -->
			  <xsl:value-of select="@value"/>&#160;
			</xsl:otherwise>
		</xsl:choose>
	  </xsl:when>
	  <xsl:otherwise>
		<xsl:value-of select="@value"/>
	  </xsl:otherwise>
	</xsl:choose>
</xsl:template>

<!--
 ja-pro: 1.1.10   DrillThrough Option
 blank cell value by default, cell value is replaced by hyperlink
-->
<xsl:template match="cell">
  <td nowrap="nowrap" class="cell-{@style}">
    <xsl:apply-templates select="drill-through"/>
    <xsl:call-template name="render-label">
      <xsl:with-param name="label">
<!--
        <xsl:value-of select="@value"/>
-->
      </xsl:with-param>
    </xsl:call-template>&#160; <!-- for cell border with no cell value -->
  </td>
</xsl:template>


<xsl:template name="render-label">
  <xsl:param name="label"/>
  <xsl:choose>

    <!-- popup menu -->
    <xsl:when test="popup-menu">
      <xsl:apply-templates select="popup-menu"/>
      <xsl:apply-templates select="property"/>
    </xsl:when>

    <!-- clickable member -->
    <xsl:when test="@href">
      <a>
        <xsl:call-template name="make-href">
          <xsl:with-param name="href" select="@href"/>
        </xsl:call-template>
        <xsl:value-of select="$label"/>
        <xsl:apply-templates select="property"/>
      </a>
    </xsl:when>

    <!-- member property -->
    <xsl:when test="property[@name='link']">
      <!--
        target="_blank" was removed because it makes no sense: you have no chance to close
        the new window if the url points to the current context because of the wcf:token
        mechanism
      -->
      <a>
        <xsl:call-template name="make-href">
          <xsl:with-param name="href" select="property[@name='link']/@value"/>
        </xsl:call-template>
        <xsl:value-of select="$label"/>
        <xsl:apply-templates select="property"/>
      </a>
    </xsl:when>
    <!-- default -->
    <xsl:otherwise>
      <xsl:value-of select="$label"/>
      <xsl:apply-templates select="property"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>


<xsl:template name="make-href">
  <xsl:param name="href"/>
  <xsl:choose>
    <xsl:when test="starts-with($href, '/')">
      <xsl:attribute name="href">
        <xsl:value-of select="concat($context, $href)"/>
      </xsl:attribute>
    </xsl:when>
    <xsl:otherwise>
      <xsl:attribute name="href">
        <xsl:value-of select="$href"/>
      </xsl:attribute>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="nowrap">
  <xsl:if test="string-length(string(caption/@caption))&lt;$maxColHdrLen">
    <xsl:attribute name="nowrap">nowrap</xsl:attribute>
  </xsl:if>
</xsl:template>


<xsl:template match="property[@name='arrow']">
  <span style="margin-left: 0.5ex"> <!-- 0em -->
    <img border="0" src="../{$imgpath}/arrow-{@value}.gif" width="10" height="10"/>
  </span>
</xsl:template>

<xsl:template match="property[@name='image']">
  <span style="margin-left: 0.5ex"> <!-- 0em -->
    <xsl:choose>
      <xsl:when test="starts-with(@value, '/')">
        <img border="0" src="..{@value}"/>
      </xsl:when>
      <xsl:otherwise>
        <img border="0" src="{@value}"/>
      </xsl:otherwise>
    </xsl:choose>
  </span>
</xsl:template>

<xsl:template match="property[@name='cyberfilter']">
  <span style="margin-left: 0.5ex"> <!-- 0em -->
    <img align="middle" src="../{$imgpath}/filter-{@value}.gif" width="53" height="14"/>
  </span>
</xsl:template>

<!-- ignore other properties (e.g. "link") -->
<xsl:template match="property"/>


<!-- begin popup menu  -->
<xsl:template match="popup-menu">
  <a href="#" onMouseover="cssdropdown.dropit(this, event, '{@id}')">
    <xsl:value-of select="@label" />
  </a>
  <div id="{@id}" class="dropmenudiv">
    <strong style="padding-left: {@level}em">
      <xsl:value-of select="@label" />
    </strong>
    <xsl:apply-templates />
  </div>
</xsl:template>

<xsl:template match="popup-group">
  <strong style="padding-left: {@level}em">
    <xsl:value-of select="@label" />
  </strong>
  <xsl:apply-templates />
</xsl:template>

<xsl:template match="popup-item">
  <a href="{@href}" style="padding-left: {@level}em">
    <xsl:value-of select="@label" />
  </a>
</xsl:template>
<!-- end popup menu  -->
<xsl:template match="*|@*|node()">
  <xsl:copy>
    <xsl:apply-templates select="*|@*|node()"/>
  </xsl:copy>
</xsl:template>

</xsl:stylesheet>
