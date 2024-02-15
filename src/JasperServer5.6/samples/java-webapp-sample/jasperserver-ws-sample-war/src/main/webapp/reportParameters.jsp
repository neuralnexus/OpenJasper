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

<%@page import="com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.*;" %>

<table border=0>
    <%
        com.jaspersoft.jasperserver.sample.WSClient client = (com.jaspersoft.jasperserver.sample.WSClient) session.getAttribute("client");
        ResourceDescriptor reportUnit = (ResourceDescriptor) request.getAttribute("reportUnit");

        com.jaspersoft.jasperserver.sample.jsp.JspUtils jspUtils = new com.jaspersoft.jasperserver.sample.jsp.JspUtils();

        int parametersCount = 0;
        java.util.List list = reportUnit.getChildren();
        java.util.List icItems = new java.util.ArrayList();

        final String IC_MULTI_PREFIX = "ic_multi_";
        java.util.Enumeration enum_params = request.getParameterNames();
        java.util.Map hashMap = new java.util.HashMap();
        while (enum_params.hasMoreElements()) {
            String key = "" + enum_params.nextElement();
            String[] values = request.getParameterValues(key);

            key = key.startsWith(IC_MULTI_PREFIX) ? key.substring(IC_MULTI_PREFIX.length()) : key;
            for (int i=0; i< values.length; i++) {
                ListItem li1 = new ListItem(key, values[i]);
                li1.setIsListItem(true);
                icItems.add(li1);
            }
        }

        // Find the datasource uri...
        String dsUri = null;
        for (int i = 0; i < list.size(); ++i) {
            ResourceDescriptor rd =
                    (ResourceDescriptor) list.get(i);

            if (rd.getWsType().equals(ResourceDescriptor.TYPE_DATASOURCE)) {
                dsUri = rd.getReferenceUri();
            } else if (rd.getWsType().equals(ResourceDescriptor.TYPE_DATASOURCE) ||
                    rd.getWsType().equals(ResourceDescriptor.TYPE_DATASOURCE_JDBC) ||
                    rd.getWsType().equals(ResourceDescriptor.TYPE_DATASOURCE_JNDI) ||
                    rd.getWsType().equals(ResourceDescriptor.TYPE_DATASOURCE_BEAN)) {
                dsUri = rd.getUriString();
            }
        }


        // Show all input controls
        for (int i = 0; i < list.size(); ++i) {
            ResourceDescriptor rd =
                    (ResourceDescriptor) list.get(i);

            java.util.Set<String> selectedValues = jspUtils.getValuesSetForParameter(rd.getName(), request);
            if (selectedValues.isEmpty()) {
                selectedValues = jspUtils.getValuesSetForParameter(IC_MULTI_PREFIX + rd.getName(), request);
            }

            if (rd.getWsType().equals(ResourceDescriptor.TYPE_INPUT_CONTROL)) {
                parametersCount++;
    %>
    <tr>
        <td><%=rd.getLabel()%>
        </td>
        <td>
            <%
                if (rd.getControlType() == ResourceDescriptor.IC_TYPE_BOOLEAN) {
            %>
            <input type="checkbox" name="<%=rd.getName()%>" value="true" <% if (selectedValues.contains("true")){%>checked<%}%>>
            <%
            } else if (rd.getControlType() == ResourceDescriptor.IC_TYPE_SINGLE_VALUE) {
            %>
            <input type="text" name="<%=rd.getName()%>" <% if (request.getParameter(rd.getName()) != null) {%>value="<%=request.getParameter(rd.getName())%>"<%}%>>
            <%
            } else if (rd.getControlType() == ResourceDescriptor.IC_TYPE_SINGLE_SELECT_LIST_OF_VALUES) {
            %>
            <select name="<%=rd.getName()%>">
                <%
                    // Get the child with the list...
                    java.util.List rdChildren = rd.getChildren();
                    ResourceDescriptor listOfValuesRd = null;

                    for (int j = 0; j < rdChildren.size(); ++j) {
                        listOfValuesRd = (ResourceDescriptor) rdChildren.get(j);
                        if (listOfValuesRd.getWsType().equals(listOfValuesRd.TYPE_LOV)) {
                            break;
                        } else listOfValuesRd = null;
                    }

                    if (listOfValuesRd != null) {
                        java.util.List listOfValues = listOfValuesRd.getListOfValues();
                        for (int j = 0; j < listOfValues.size(); ++j) {
                            ListItem item =
                                    (ListItem) listOfValues.get(j);
                %>
                <option value="<%=item.getValue()%>"<% if (selectedValues.contains(item.getValue())){%>selected<%}%>><%=item.getLabel()%>
                </option>
                <%
                        }
                    }
                %>
            </select>
            <%
            } else if (rd.getControlType() == ResourceDescriptor.IC_TYPE_MULTI_SELECT_LIST_OF_VALUES) {
            %>
            <select name="<%=IC_MULTI_PREFIX + rd.getName()%>" multiple>
                <%
                    // Get the child with the list...
                    java.util.List rdChildren = rd.getChildren();
                    ResourceDescriptor listOfValuesRd = null;

                    for (int j = 0; j < rdChildren.size(); ++j) {
                        listOfValuesRd = (ResourceDescriptor) rdChildren.get(j);
                        if (listOfValuesRd.getWsType().equals(listOfValuesRd.TYPE_LOV)) {
                            break;
                        } else listOfValuesRd = null;
                    }

                    if (listOfValuesRd != null) {
                        java.util.List listOfValues = listOfValuesRd.getListOfValues();
                        for (int j = 0; j < listOfValues.size(); ++j) {
                            ListItem item =
                                    (ListItem) listOfValues.get(j);
                %>
                <option value="<%=item.getValue()%>"<% if (selectedValues.contains(item.getValue())){%>selected<%}%>><%=item.getLabel()%>
                </option>
                <%
                        }
                    }
                %>
            </select>
            <%
            } else if (rd.getControlType() == ResourceDescriptor.IC_TYPE_SINGLE_SELECT_LIST_OF_VALUES_RADIO || rd.getControlType() == ResourceDescriptor.IC_TYPE_MULTI_SELECT_LIST_OF_VALUES_CHECKBOX) {
                    // Get the child with the list...
                    java.util.List rdChildren = rd.getChildren();
                    ResourceDescriptor listOfValuesRd = null;

                    for (int j = 0; j < rdChildren.size(); ++j) {
                        listOfValuesRd = (ResourceDescriptor) rdChildren.get(j);
                        if (listOfValuesRd.getWsType().equals(listOfValuesRd.TYPE_LOV)) {
                            break;
                        } else listOfValuesRd = null;
                    }

                    if (listOfValuesRd != null) {
                        java.util.List listOfValues = listOfValuesRd.getListOfValues();
                        for (int j = 0; j < listOfValues.size(); ++j) {
                            ListItem item =
                                    (ListItem) listOfValues.get(j);
                %>
                <input <% if (selectedValues.contains(item.getValue())){%>checked<%}%> <%
                if (rd.getControlType() == ResourceDescriptor.IC_TYPE_SINGLE_SELECT_LIST_OF_VALUES_RADIO) {
                    %>type="radio"<%
                } else if (rd.getControlType() == ResourceDescriptor.IC_TYPE_MULTI_SELECT_LIST_OF_VALUES_CHECKBOX) {
                    %>type="checkbox"<%
                }
                %> name="<%if(rd.getControlType() == ResourceDescriptor.IC_TYPE_MULTI_SELECT_LIST_OF_VALUES_CHECKBOX){%>ic_multi_<%}%><%=rd.getName()%>" value="<%=item.getValue()%>"/><%=item.getLabel()%>
                <%
                        }
                    }
            } else if (rd.getControlType() == ResourceDescriptor.IC_TYPE_SINGLE_SELECT_QUERY) {
            %>
            <select name="<%=rd.getName()%>">
                <%
                    // Get the list of entries....
                    ResourceDescriptor liRd = new ResourceDescriptor();
                    liRd.setUriString(rd.getUriString());
                    liRd.setResourceProperty(rd.PROP_QUERY_DATA, null);

                    liRd.getParameters().addAll(icItems);

                    java.util.List args = new java.util.ArrayList();
                    args.add(new Argument( Argument.IC_GET_QUERY_DATA, dsUri));
                    args.add(new Argument( Argument.RU_REF_URI, reportUnit.getUriString()));
                    ResourceDescriptor qdRd = client.get(liRd, null, args);

                    if (qdRd.getQueryData() != null) {
                        java.util.List rows = qdRd.getQueryData();
                        for (int j = 0; j < rows.size(); ++j) {
                            InputControlQueryDataRow item =
                                    (InputControlQueryDataRow) rows.get(j);

                            StringBuffer label = new StringBuffer();
                            for (int k = 0; k < item.getColumnValues().size(); ++k) {
                                label.append(((k > 0) ? " | " : ""));
                                label.append(item.getColumnValues().get(k));
                            }

                %>

                <option value="<%=item.getValue()%>"<% if (selectedValues.contains(item.getValue())){%>selected<%}%>><%=label%>
                </option>
                <%
                        }
                    }
                %>
            </select>
            <%
                } else if (rd.getControlType() == ResourceDescriptor.IC_TYPE_SINGLE_SELECT_QUERY_RADIO) {
                    // Get the list of entries....
                    ResourceDescriptor liRd = new ResourceDescriptor();
                    liRd.setUriString(rd.getUriString());
                    liRd.setResourceProperty(rd.PROP_QUERY_DATA, null);

                    liRd.getParameters().addAll(icItems);

                    java.util.List args = new java.util.ArrayList();
                    args.add(new Argument( Argument.IC_GET_QUERY_DATA, dsUri));
                    args.add(new Argument( Argument.RU_REF_URI, reportUnit.getUriString()));
                    ResourceDescriptor qdRd = client.get(liRd, null, args);

                    if (qdRd.getQueryData() != null) {
                        java.util.List rows = qdRd.getQueryData();
                        for (int j = 0; j < rows.size(); ++j) {
                            InputControlQueryDataRow item =
                                    (InputControlQueryDataRow) rows.get(j);

                            StringBuffer label = new StringBuffer();
                            for (int k = 0; k < item.getColumnValues().size(); ++k) {
                                label.append(((k > 0) ? " | " : ""));
                                label.append(item.getColumnValues().get(k));
                            }
                %>

                <input <% if (selectedValues.contains(item.getValue())){%>checked<%}%> type="radio" name="<%=rd.getName()%>" value="<%=item.getValue()%>" /><%=label%>
                <%
                        }
                    }
                %>
            </select>
            <%
                } else if (rd.getControlType() == ResourceDescriptor.IC_TYPE_MULTI_SELECT_QUERY) {
            %>
            <select name="ic_multi_<%=rd.getName()%>" multiple size="3">
                <%
                    // Get the list of entries....
                    ResourceDescriptor liRd = new ResourceDescriptor();
                    liRd.setUriString(rd.getUriString());
                    liRd.setResourceProperty(rd.PROP_QUERY_DATA, null);

                    liRd.getParameters().addAll(icItems);

                    java.util.List args = new java.util.ArrayList();
                    args.add(new Argument( Argument.IC_GET_QUERY_DATA, dsUri));
                    args.add(new Argument( Argument.RU_REF_URI, reportUnit.getUriString()));
                    ResourceDescriptor qdRd = client.get(liRd, null, args);


                    if (qdRd.getQueryData() != null) {
                        java.util.List rows = qdRd.getQueryData();
                        for (int j = 0; j < rows.size(); ++j) {
                            InputControlQueryDataRow item =
                                    (InputControlQueryDataRow) rows.get(j);

                            StringBuffer label = new StringBuffer();
                            for (int k = 0; k < item.getColumnValues().size(); ++k) {
                                label.append(((k > 0) ? " | " : ""));
                                label.append(item.getColumnValues().get(k));
                            }

                            %>

                            <option value="<%=item.getValue()%>"<% if (selectedValues.contains(item.getValue())){%>selected<%}%>><%=label%>
                            </option>
                            <%
                        }
                    }
                %>
            </select>
            <%
                } else if (rd.getControlType() == ResourceDescriptor.IC_TYPE_MULTI_SELECT_QUERY_CHECKBOX) {
                    // Get the list of entries....
                    ResourceDescriptor liRd = new ResourceDescriptor();
                    liRd.setUriString(rd.getUriString());
                    liRd.setResourceProperty(rd.PROP_QUERY_DATA, null);

                    liRd.getParameters().addAll(icItems);

                    java.util.List args = new java.util.ArrayList();
                    args.add(new Argument( Argument.IC_GET_QUERY_DATA, dsUri));
                    args.add(new Argument( Argument.RU_REF_URI, reportUnit.getUriString()));
                    ResourceDescriptor qdRd = client.get(liRd, null, args);


                    if (qdRd.getQueryData() != null) {
                        java.util.List rows = qdRd.getQueryData();
                        for (int j = 0; j < rows.size(); ++j) {
                            InputControlQueryDataRow item =
                                    (InputControlQueryDataRow) rows.get(j);

                            StringBuffer label = new StringBuffer();
                            for (int k = 0; k < item.getColumnValues().size(); ++k) {
                                label.append(((k > 0) ? " | " : ""));
                                label.append(item.getColumnValues().get(k));
                            }

                            %>

                            <input type="checkbox" <% if (selectedValues.contains(item.getValue())){%>checked<%}%> name="ic_multi_<%=rd.getName()%>" value="<%=item.getValue()%>"/><%=label%>
                            <%
                        }
                    }
                }
            %>
        </td>
        <td><% if (rd.getDescription() != null) rd.getDescription();%></td>
    </tr>
    <%
            }
        }

        if (parametersCount > 0) {
            request.setAttribute("hasParameters", Boolean.TRUE);
        }
    %>
</table>
