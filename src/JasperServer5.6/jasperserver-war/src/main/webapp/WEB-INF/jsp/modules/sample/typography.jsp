<%--
  ~ Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved.
  ~ http://www.jaspersoft.com.
  ~
  ~ Unless you have purchased  a commercial license agreement from Jaspersoft,
  ~ the following license terms  apply:
  ~
  ~ This program is free software: you can redistribute it and/or  modify
  ~ it under the terms of the GNU Affero General Public License  as
  ~ published by the Free Software Foundation, either version 3 of  the
  ~ License, or (at your option) any later version.
  ~
  ~ This program is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
  ~ GNU Affero  General Public License for more details.
  ~
  ~ You should have received a copy of the GNU Affero General Public  License
  ~ along with this program. If not, see <http://www.gnu.org/licenses/>.
  --%>

<%@ taglib uri="/spring" prefix="spring"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>

<t:insertTemplate template="/WEB-INF/jsp/templates/page.jsp">
    <t:putAttribute name="pageTitle" value="Typography Samples"/>
    <t:putAttribute name="bodyID" value="typography"/>
    <t:putAttribute name="bodyClass" value="twoColumn"/>
    <t:putAttribute name="moduleName" value="commons.main"/>
    <t:putAttribute name="headerContent" >
        <link rel="stylesheet" href="${pageContext.request.contextPath}/<spring:theme code='samples.css'/>" type="text/css" />
		<style type="text/css">

		</style>
    </t:putAttribute>
    <t:putAttribute name="bodyContent" >
	
		<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
		    <t:putAttribute name="containerClass" value="column decorated primary"/>
		    <t:putAttribute name="containerTitle">Swatches</t:putAttribute>
		    <t:putAttribute name="bodyClass" value="oneColumn"/>
		    <t:putAttribute name="bodyContent" >
	        
			<table id="sampleGrid" class="sample">
					        		<thead>
					        		<tr>
					        			<th><spring:message code="JIF.titleclass"/></th>
					        			<th colspan="2"><spring:message code="JIF.titlesample"/></th>
					        		</tr>
					        		</thead>
					        		<tbody>
						        		<tr>
						        			<th class="rowHeader"><spring:message code="JIF.titlecolors"/></th>
						        			<td colspan="2"><spring:message code="JIF.descriptorcolorssample"/></td>
						        		</tr>
						        		<tr>
						        			<td>.palette01</td>
						        			<td class="chip palette01_bkgd"></td>
						        			<td></td>
						        		</tr>
						        		<tr>
						        			<td>.palette02</td>
						        			<td class="chip palette02_bkgd"></td>
						        			<td></td>
						        		</tr>
						        		<tr>
						        			<td>.palette03</td>
						        			<td class="chip palette03_bkgd"></td>
						        			<td></td>
						        		</tr>
						        		<tr>
						        			<td>.palette04</td>
						        			<td class="chip palette04_bkgd"></td>
						        			<td></td>
						        		</tr>
										<tr>
						        			<td>.palette05</td>
						        			<td class="chip palette05_bkgd"></td>
						        			<td></td>
						        		</tr>
						        		<tr>
						        			<td>.palette06</td>
						        			<td class="chip palette06_bkgd"></td>
						        			<td></td>
						        		</tr>
						        		<tr>
						        			<td>.palette07</td>
						        			<td class="chip palette07_bkgd"></td>
						        			<td></td>
						        		</tr>
						        		<tr>
						        			<td>.palette08</td>
						        			<td class="chip palette08_bkgd"></td>
						        			<td></td>
						        		</tr>
						        		<tr>
						        			<td>.palette10</td>
						        			<td class="chip palette10_bkgd"></td>
						        			<td></td>
						        		</tr>
						        		<tr>
						        			<td>.palette09</td>
						        			<td class="chip palette09_bkgd"></td>
						        			<td></td>
						        		</tr>
						        		<tr>
						        			<td>.palette11</td>
						        			<td class="chip palette11_bkgd"></td>
						        			<td></td>
						        		</tr>
						        		<tr>
						        			<td>.palette12</td>
						        			<td class="chip palette12_bkgd"></td>
						        			<td></td>
						        		</tr>
						        		<tr>
						        			<td>.palette13</td>
						        			<td class="chip palette13_bkgd"></td>
						        			<td></td>
						        		</tr>
						        		<tr>
						        			<td>.palette14</td>
						        			<td class="chip palette14_bkgd"></td>
						        			<td></td>
						        		</tr>
						        		<tr>
						        			<td>.palette16</td>
						        			<td class="chip palette16_bkgd"></td>
						        			<td></td>
						        		</tr>
						        		<tr>
						        			<td>.palette15</td>
						        			<td class="chip palette15_bkgd"></td>
						        			<td></td>
						        		</tr>
						        		<tr>
						        			<td>.palette17</td>
						        			<td class="chip palette17_bkgd"></td>
						        			<td></td>
						        		</tr>
					        		</tbody>
					        		<tbody>
						        		<tr>
						        			<th><spring:message code="JIF.titlefonts"/></th>
						        			<td colspan="2"><spring:message code="JIF.descriptorfontssample"/></td>
						        		</tr>
						        		<tr>
						        			<th></th>
						        			<th></th>
						        			<td>.emphasis</td>
						        		</tr>
						        		<tr>
						        			<td>.jumbo</td>
						        			<td class="jumbo"><spring:message code="JIF.samplefont"/></td>
						        			<td class="jumbo emphasis"><spring:message code="JIF.samplefont"/></td>
						        		</tr>
						        		<tr>
						        			<td>.display</td>
						        			<td class="display"><spring:message code="JIF.samplefont"/></td>
						        			<td class="display emphasis"><spring:message code="JIF.samplefont"/></td>
						        		</tr>
						        		<tr>
						        			<td>.large</td>
						        			<td class="large"><spring:message code="JIF.samplefont"/></td>
						        			<td class="large emphasis"><spring:message code="JIF.samplefont"/></td>
						        		</tr>
						        		<tr>
						        			<td class="">body</td>
						        			<td class=""><spring:message code="JIF.samplefont"/></td>
						        			<td class="emphasis"><spring:message code="JIF.samplefont"/></td>
						        		</tr>
						        		<tr>
						        			<td>.small</td>
						        			<td class="small"><spring:message code="JIF.samplefont"/></td>
						        			<td class="small emphasis"><spring:message code="JIF.samplefont"/></td>
						        		</tr>
						        		<tr>
						        			<td>.mini</td>
						        			<td class="mini"><spring:message code="JIF.samplefont"/></td>
						        			<td class="mini emphasis"><spring:message code="JIF.samplefont"/></td>
						        		</tr>
						        		<tr>
						        			<td>.micro</td>
						        			<td class="micro"><spring:message code="JIF.samplefont"/></td>
						        			<td class="micro emphasis"><spring:message code="JIF.samplefont"/></td>
						        		</tr>
					        		</tbody>
					        		<tbody>
						        		<tr>
						        			<th><spring:message code="JIF.titletextaccents"/></th>
						        			<td colspan="2"><spring:message code="JIF.descriptortextaccents"/></td>
						        		</tr>
						        		<tr>
						        			<th></th>
						        			<th></th>
						        			<td>.emphasis</td>
						        		</tr>
						        		<tr>
						        			<td></td>
						        			<td>
						        				<p class="textAccent"><spring:message code="JIF.sampletextaccents"/>="textAccent"</p>
						        				<p class="textAccent02"><spring:message code="JIF.sampletextaccents"/>="textAccent02"</p>
						        				<p class="warning"><spring:message code="JIF.sampletextaccents"/>="warning"</p>
						        			</td>
						        			<td class="emphasis">
						        				<p class="textAccent"><spring:message code="JIF.sampletextaccents"/>="textAccent"</p>
						        				<p class="textAccent02"><spring:message code="JIF.sampletextaccents"/>="textAccent02"</p>
						        				<p class="warning"><spring:message code="JIF.sampletextaccents"/>="warning"</p>
						        			</td>
						        		</tr>
					        		</tbody>
					        		<tbody>
						        		<tr>
						        			<th><spring:message code="JIF.titlelists"/></th>
						        			<td colspan="2"><spring:message code="JIF.descriptorlistssample"/></td>
						        		</tr>
						        		<tr>
						        			<th></th>
						        			<td>[default ul]</td>
						        			<td>.decorated</td>
						        		</tr>
						        		<tr>
						        			<td></td>
						        			<td>
						        				<ul>
						        					<li><spring:message code="JIF.samplelists"/> 1</li>
						        					<li><spring:message code="JIF.samplelists"/> 2</li>
						        					<li><spring:message code="JIF.samplelists"/> 3</li>
						        				</ul>
						        			</td>
						        			<td>
						        				<ul class="decorated">
						        					<li><spring:message code="JIF.samplelists"/> 1</li>
						        					<li><spring:message code="JIF.samplelists"/> 2</li>
						        					<li><spring:message code="JIF.samplelists"/> 3</li>
						        				</ul>
						        			</td>
						        		</tr>
					        		</tbody>
					        	</table>
	        
			</t:putAttribute>
		    <t:putAttribute name="footerContent">
		    	<!-- custom content here; remove this comment -->
		    </t:putAttribute>
		</t:insertTemplate>		
		
		<t:insertTemplate template="/WEB-INF/jsp/modules/sample/sampleIndex.jsp"/>

    </t:putAttribute>
</t:insertTemplate>