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

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN">

		<div class="calendar" style="display:block; height:170px;width:227px;position:relative;">
			<table cellspacing="0" cellpadding="0" style="visibility: visible;">
				<thead>
					<tr>
						<td colspan="1" class="button">
							<div unselectable="on">
								?
							</div>
						</td>
						<td colspan="6" class="title" style="cursor: move;">
							November, 2010
						</td>
						<td colspan="1" class="button">
							<div unselectable="on">
								x
							</div>
						</td>
					</tr>
					<tr class="headrow">
						<td colspan="1" class="button nav">
							<div unselectable="on">
								&#171;
							</div>
						</td>
						<td colspan="1" class="nav button">
							<div unselectable="on">
								&#8249;
							</div>
						</td>
						<td colspan="4" class="button">
							<div unselectable="on">
								Today
							</div>
						</td>
						<td colspan="1" class="button nav">
							<div unselectable="on">
								&#8250;
							</div>
						</td>
						<td colspan="1" class="button nav">
							<div unselectable="on">
								&#187;
							</div>
						</td>
					</tr>
					<tr class="daynames">
						<td class="name wn">
							wk
						</td>
						<td class="name day weekend">
							Sun
						</td>
						<td class="day name">
							Wed
						</td>
						<td class="name day weekend">
							Sat
						</td>
						<td class="day name">
							Tue
						</td>
						<td class="day name">
							Fri
						</td>
						<td class="day name">
							Mon
						</td>
						<td class="day name">
							Thu
						</td>
					</tr>
				</thead>
				<tbody>
					<tr class="daysrow">
						<td class="day wn">
							43
						</td>
						<td class="emptycell">
							&nbsp;
						</td>
						<td class="day">
							1
						</td>
						<td class="day">
							2
						</td>
						<td class="day">
							3
						</td>
						<td class="day">
							4
						</td>
						<td class="day">
							5
						</td>
						<td class="day weekend">
							6
						</td>
					</tr>
					<tr class="daysrow">
						<td class="day wn">
							44
						</td>
						<td class="day weekend">
							7
						</td>
						<td class="day">
							8
						</td>
						<td class="day">
							9
						</td>
						<td class="day">
							10
						</td>
						<td class="day">
							11
						</td>
						<td class="day">
							12
						</td>
						<td class="day weekend">
							13
						</td>
					</tr>
					<tr class="daysrow">
						<td class="day wn">
							45
						</td>
						<td class="weekend day">
							14
						</td>
						<td class="day today">
							15
						</td>
						<td class="day selected">
							16
						</td>
						<td class="day active">
							17
						</td>
						<td class="day hilite">
							18
						</td>
						<td class="day">
							19
						</td>
						<td class="day weekend">
							20
						</td>
					</tr>
					<tr class="daysrow">
						<td class="day wn">
							46
						</td>
						<td class="weekend day">
							21
						</td>
						<td class="day">
							22
						</td>
						<td class="day">
							23
						</td>
						<td class="day">
							24
						</td>
						<td class="day">
							25
						</td>
						<td class="day">
							26
						</td>
						<td class="day weekend">
							27
						</td>
					</tr>
					<tr class="daysrow">
						<td class="day wn">
							47
						</td>
						<td class="day weekend">
							28
						</td>
						<td class="day">
							29
						</td>
						<td class="day">
							30
						</td>
						<td class="emptycell">
							&nbsp;
						</td>
						<td class="emptycell">
							&nbsp;
						</td>
						<td class="emptycell">
							&nbsp;
						</td>
						<td class="emptycell">
							&nbsp;
						</td>
					</tr>
					<tr class="emptyrow">
						<td class="day wn">
							48
						</td>
						<td class="emptycell">
							&nbsp;
						</td>
						<td class="emptycell">
							&nbsp;
						</td>
						<td class="emptycell">
							&nbsp;
						</td>
						<td class="emptycell">
							&nbsp;
						</td>
						<td class="emptycell">
							&nbsp;
						</td>
						<td class="emptycell">
							&nbsp;
						</td>
						<td class="emptycell">
							&nbsp;
						</td>
					</tr>
					<tr class="time">
						<td class="time" colspan="2">
							Time:
						</td>
						<td class="time" colspan="4">
							<span class="hour">15</span><span class="colon">:</span><span class="minute">15</span>
						</td>
						<td class="time" colspan="2">
							&nbsp;
						</td>
					</tr>
				</tbody>
				<tfoot>
					<tr class="footrow">
						<td colspan="8" class="ttip" style="cursor: move;">
							Select date
						</td>
					</tr>
				</tfoot>
			</table>
			<div class="combo" style="display: none;">
				<div class="label">
					Jan
				</div>
				<div class="label">
					Feb
				</div>
				<div class="label">
					Mar
				</div>
				<div class="label">
					Apr
				</div>
				<div class="label">
					May
				</div>
				<div class="label">
					Jun
				</div>
				<div class="label">
					Jul
				</div>
				<div class="label">
					Aug
				</div>
				<div class="label">
					Sep
				</div>
				<div class="label">
					Oct
				</div>
				<div class="label">
					Nov
				</div>
				<div class="label">
					Dec
				</div>
			</div>
			<div class="combo">
				<div class="label"></div>
				<div class="label"></div>
				<div class="label"></div>
				<div class="label"></div>
				<div class="label"></div>
				<div class="label"></div>
				<div class="label"></div>
				<div class="label"></div>
				<div class="label"></div>
				<div class="label"></div>
				<div class="label"></div>
				<div class="label"></div>
			</div>
		</div>

