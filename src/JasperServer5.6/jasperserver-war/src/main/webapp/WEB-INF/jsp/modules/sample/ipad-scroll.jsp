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

<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<t:insertTemplate template="/WEB-INF/jsp/templates/page.jsp">
    <t:putAttribute name="pageTitle" value="One Column Layout"/>
    <t:putAttribute name="bodyID" value="changeMe"/>
    <t:putAttribute name="bodyClass" value="oneColumn"/>

    <t:putAttribute name="bodyContent">

        <t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
            <t:putAttribute name="containerClass" value="column decorated primary"/>
            <t:putAttribute name="containerTitle"><spring:message code="JIF.titleprimary"/></t:putAttribute>
            <t:putAttribute name="headerContent">
                <script type="text/javascript">

                    document.observe('touchmove', function(e) {
                        e.preventDefault();
                    });
                    document.observe('dom:loaded', function() {
                        $A($$(".swipeScroll")).each(function(scroll) {
                            var myScroll = new iScroll(scroll.up().identify());
                        });
                    });

                </script>

            </t:putAttribute>
            <t:putAttribute name="bodyID" value="itemListToScroll"/>
            <t:putAttribute name="bodySwipeScroll" value="${true}"/>
            <t:putAttribute name="bodyContent">
                <%--<div id="wrapper">--%>
                    <%--<div id="scroller">--%>
                        <ul id="thelist">
                            <li>Pretty row 1</li>
                            <li>Pretty row 2</li>
                            <li>Pretty row 3</li>
                            <li>Pretty row 4</li>
                            <li>Pretty row 5</li>
                            <li>Pretty row 6</li>
                            <li>Pretty row 7</li>
                            <li>Pretty row 8</li>
                            <li>Pretty row 9</li>
                            <li>Pretty row 10</li>
                            <li>Pretty row 11</li>
                            <li>Pretty row 12</li>
                            <li>Pretty row 13</li>
                            <li>Pretty row 14</li>
                            <li>Pretty row 15</li>
                            <li>Pretty row 16</li>
                            <li>Pretty row 17</li>
                            <li>Pretty row 18</li>
                            <li>Pretty row 19</li>
                            <li>Pretty row 20</li>
                            <li>Pretty row 21</li>
                            <li>Pretty row 22</li>
                            <li>Pretty row 23</li>
                            <li>Pretty row 24</li>
                            <li>Pretty row 25</li>
                            <li>Pretty row 26</li>
                            <li>Pretty row 27</li>
                            <li>Pretty row 28</li>
                            <li>Pretty row 29</li>
                            <li>Pretty row 30</li>
                            <li>Pretty row 31</li>
                            <li>Pretty row 32</li>
                            <li>Pretty row 33</li>
                            <li>Pretty row 34</li>
                            <li>Pretty row 35</li>
                            <li>Pretty row 36</li>
                            <li>Pretty row 37</li>
                            <li>Pretty row 38</li>
                            <li>Pretty row 39</li>
                            <li>Pretty row 40</li>
                            <li>Pretty row 1</li>
                            <li>Pretty row 2</li>
                            <li>Pretty row 3</li>
                            <li>Pretty row 4</li>
                            <li>Pretty row 5</li>
                            <li>Pretty row 6</li>
                            <li>Pretty row 7</li>
                            <li>Pretty row 8</li>
                            <li>Pretty row 9</li>
                            <li>Pretty row 10</li>
                            <li>Pretty row 11</li>
                            <li>Pretty row 12</li>
                            <li>Pretty row 13</li>
                            <li>Pretty row 14</li>
                            <li>Pretty row 15</li>
                            <li>Pretty row 16</li>
                            <li>Pretty row 17</li>
                            <li>Pretty row 18</li>
                            <li>Pretty row 19</li>
                            <li>Pretty row 20</li>
                            <li>Pretty row 21</li>
                            <li>Pretty row 22</li>
                            <li>Pretty row 23</li>
                            <li>Pretty row 24</li>
                            <li>Pretty row 25</li>
                            <li>Pretty row 26</li>
                            <li>Pretty row 27</li>
                            <li>Pretty row 28</li>
                            <li>Pretty row 29</li>
                            <li>Pretty row 30</li>
                            <li>Pretty row 31</li>
                            <li>Pretty row 32</li>
                            <li>Pretty row 33</li>
                            <li>Pretty row 34</li>
                            <li>Pretty row 35</li>
                            <li>Pretty row 36</li>
                            <li>Pretty row 37</li>
                            <li>Pretty row 38</li>
                            <li>Pretty row 39</li>
                            <li>Pretty row 40</li>
                            <li>Pretty row 1</li>
                            <li>Pretty row 2</li>
                            <li>Pretty row 3</li>
                            <li>Pretty row 4</li>
                            <li>Pretty row 5</li>
                            <li>Pretty row 6</li>
                            <li>Pretty row 7</li>
                            <li>Pretty row 8</li>
                            <li>Pretty row 9</li>
                            <li>Pretty row 10</li>
                            <li>Pretty row 11</li>
                            <li>Pretty row 12</li>
                            <li>Pretty row 13</li>
                            <li>Pretty row 14</li>
                            <li>Pretty row 15</li>
                            <li>Pretty row 16</li>
                            <li>Pretty row 17</li>
                            <li>Pretty row 18</li>
                            <li>Pretty row 19</li>
                            <li>Pretty row 20</li>
                            <li>Pretty row 21</li>
                            <li>Pretty row 22</li>
                            <li>Pretty row 23</li>
                            <li>Pretty row 24</li>
                            <li>Pretty row 25</li>
                            <li>Pretty row 26</li>
                            <li>Pretty row 27</li>
                            <li>Pretty row 28</li>
                            <li>Pretty row 29</li>
                            <li>Pretty row 30</li>
                            <li>Pretty row 31</li>
                            <li>Pretty row 32</li>
                            <li>Pretty row 33</li>
                            <li>Pretty row 34</li>
                            <li>Pretty row 35</li>
                            <li>Pretty row 36</li>
                            <li>Pretty row 37</li>
                            <li>Pretty row 38</li>
                            <li>Pretty row 39</li>
                            <li>Pretty row 40</li>
                        </ul>
                    <%--</div>--%>
                <%--</div>--%>
            </t:putAttribute>
        </t:insertTemplate>

    </t:putAttribute>

</t:insertTemplate>
