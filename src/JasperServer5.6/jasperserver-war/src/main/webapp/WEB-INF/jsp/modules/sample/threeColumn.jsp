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

<html>
    <head>
        <title>three column</title>

        <link rel="stylesheet" href="${pageContext.request.contextPath}/<spring:theme code='samples.css'/>" type="text/css" />

        <style type="text/css"></style>
    </head>
    <body id="threeColumn" class="threeColumn">

        <div class="primary column decorated">
        <!-- cosmetic -->
            <div class="before IE7 cosmetic"></div>
            <div class="background"></div>
            <div class="after IE7 cosmetic"></div>
        <!-- end cosmetic -->
            <div class="content rndCorners-all">
                <div class="header">
                    <div class="icon"></div>
                    <div class="title">Primary Column</div>
                    <div class="buttonSet"></div>
                </div><!-- header -->
                <div class="body twoColumn">

                    <div class="primary column decorated">
                    <!-- cosmetic -->
                        <div class="before IE7 cosmetic"></div>
                        <div class="background"></div>
                        <div class="after IE7 cosmetic"></div>
                    <!-- end cosmetic -->
                        <div class="content rndCorners-all">
                            <div class="header">
                                <div class="icon"></div>
                                <div class="title">Nested Primary Column</div>
                                <div class="buttonSet"></div>
                            </div><!-- header -->
                            <div class="body">
                            </div><!-- body -->
                            <div class="footer rndCorners-bottom">
                                <div class="buttonSet"></div>
                            </div><!-- footer -->
                        </div><!-- content -->
                    </div><!-- column -->

                    <div class="secondary column decorated sizeable">
                        <div class="sizer horizontal"></div>
                        <button class="button minimize"></button>
                    <!-- cosmetic -->
                        <div class="before IE7 cosmetic"></div>
                        <div class="background"></div>
                        <div class="after IE7 cosmetic"></div>
                    <!-- end cosmetic -->
                        <div class="content rndCorners-all">
                            <div class="header rndCorners-top">
                                <div class="icon"></div>
                                <div class="title">Nested Secondary Column</div>
                                <div class="buttonSet"></div>
                            </div><!-- header -->
                            <div class="body">
                            </div><!-- body -->
                            <div class="footer rndCorners-bottom">
                                <div class="buttonSet"></div>
                            </div><!-- footer -->
                        </div><!-- content -->
                    </div><!-- column -->

                </div><!-- body -->
                <div class="footer rndCorners-bottom">
                    <div class="buttonSet"></div>
                </div><!-- footer -->
            </div><!-- content -->
        </div><!-- column -->

        <div class="secondary column decorated sizeable">
            <div class="sizer horizontal"></div>
            <button class="button minimize"></button>
        <!-- cosmetic -->
            <div class="before IE7 cosmetic"></div>
            <div class="background"></div>
            <div class="after IE7 cosmetic"></div>
        <!-- end cosmetic -->
            <div class="content rndCorners-all">
                <div class="header rndCorners-top">
                    <div class="icon"></div>
                    <div class="title">Secondary Column</div>
                    <div class="buttonSet"></div>
                </div><!-- header -->
                <div class="body">

                    <div class="panel pane sizeable">
                        <div class="sizer vertical"></div>
                        <!-- cosmetic -->
                        <div class="before IE7 cosmetic"></div>
                        <div class="background"></div>
                        <div class="after IE7 cosmetic"></div>
                        <!-- end cosmetic -->
                        <div class="content">
                            <div class="header">
                                <span class="cosmetic"></span>
                                <div class="icon"></div>
                                <div class="title">Stack Pane</div>
                                <div class="buttonSet"></div>
                            </div><!-- header -->
                            <div class="body">
                                <!-- <div class="temp b"></div> -->
                            </div><!-- body -->
                            <div class="footer">
                                <div class="buttonSet"></div>
                            </div><!-- footer -->
                        </div><!-- content -->
                    </div><!-- panel -->

                    <div class="panel pane sizeable">
                        <div class="sizer vertical"></div>
                        <!-- cosmetic -->
                        <div class="before IE7 cosmetic"></div>
                        <div class="background"></div>
                        <div class="after IE7 cosmetic"></div>
                        <!-- end cosmetic -->
                        <div class="content">
                            <div class="header">
                                <span class="cosmetic"></span>
                                <div class="icon"></div>
                                <div class="title">Stack Pane</div>
                                <div class="buttonSet"></div>
                            </div><!-- header -->
                            <div class="body">
                                <!-- <div class="temp d"></div> -->
                            </div><!-- body -->
                            <div class="footer">
                                <div class="buttonSet"></div>
                            </div><!-- footer -->
                        </div><!-- content -->
                    </div><!-- panel -->

                    <div class="panel pane sizeable">
                        <div class="sizer vertical"></div>
                        <!-- cosmetic -->
                        <div class="before IE7 cosmetic"></div>
                        <div class="background"></div>
                        <div class="after IE7 cosmetic"></div>
                        <!-- end cosmetic -->
                        <div class="content">
                            <div class="header">
                                <span class="cosmetic"></span>
                                <div class="icon"></div>
                                <div class="title">Stack Pane</div>
                                <div class="buttonSet"></div>
                            </div><!-- header -->
                            <div class="body">
                                <!-- <div class="temp c"></div> -->
                            </div><!-- body -->
                            <div class="footer">
                                <div class="buttonSet"></div>
                            </div><!-- footer -->
                        </div><!-- content -->
                    </div><!-- panel -->

                </div><!-- body -->
                <div class="footer rndCorners-bottom">
                    <div class="buttonSet"></div>
                </div><!-- footer -->
            </div><!-- content -->
        </div><!-- column -->

        <div class="tertiary column decorated sizeable">
            <div class="sizer horizontal"></div>
            <button class="button minimize"></button>
        <!-- cosmetic -->
            <div class="before IE7 cosmetic"></div>
            <div class="background"></div>
            <div class="after IE7 cosmetic"></div>
        <!-- end cosmetic -->
            <div class="content rndCorners-all">
                <div class="header rndCorners-top">
                    <div class="icon"></div>
                    <div class="title">Tertiary Column</div>
                    <div class="buttonSet"></div>
                </div><!-- header -->
                <div class="body threeRow">

                    <div class="panel pane primary sizeable">
                        <div class="sizer vertical"></div>
                        <!-- cosmetic -->
                        <div class="before IE7 cosmetic"></div>
                        <div class="background"></div>
                        <div class="after IE7 cosmetic"></div>
                        <!-- end cosmetic -->
                        <div class="content">
                            <div class="header">
                                <span class="cosmetic"></span>
                                <div class="icon"></div>
                                <div class="title">Primary Pane</div>
                                <div class="buttonSet"></div>
                            </div><!-- header -->
                            <div class="body">
                                <div class="temp b"></div>
                            </div><!-- body -->
                            <div class="footer">
                                <div class="buttonSet"></div>
                            </div><!-- footer -->
                        </div><!-- content -->
                    </div><!-- panel -->

                    <div class="panel pane lower sizeable">
                        <div class="sizer vertical"></div>
                        <!-- cosmetic -->
                        <div class="before IE7 cosmetic"></div>
                        <div class="background"></div>
                        <div class="after IE7 cosmetic"></div>
                        <!-- end cosmetic -->
                        <div class="content">
                            <div class="header">
                                <span class="cosmetic"></span>
                                <div class="icon"></div>
                                <div class="title">Lower Pane</div>
                                <div class="buttonSet"></div>
                            </div><!-- header -->
                            <div class="body">
                                <div class="temp c"></div>
                            </div><!-- body -->
                            <div class="footer">
                                <div class="buttonSet"></div>
                            </div><!-- footer -->
                        </div><!-- content -->
                    </div><!-- panel -->

                    <div class="panel pane upper sizeable">
                        <div class="sizer vertical"></div>
                        <!-- cosmetic -->
                        <div class="before IE7 cosmetic"></div>
                        <div class="background"></div>
                        <div class="after IE7 cosmetic"></div>
                        <!-- end cosmetic -->
                        <div class="content">
                            <div class="header">
                                <span class="cosmetic"></span>
                                <div class="icon"></div>
                                <div class="title">Upper Pane</div>
                                <div class="buttonSet"></div>
                            </div><!-- header -->
                            <div class="body">
                                <div class="temp c"></div>
                            </div><!-- body -->
                            <div class="footer">
                                <div class="buttonSet"></div>
                            </div><!-- footer -->
                        </div><!-- content -->
                    </div><!-- panel -->

                </div><!-- body -->
                <div class="footer rndCorners-bottom">
                    <div class="buttonSet"></div>
                </div><!-- footer -->
            </div><!-- content -->
        </div><!-- column -->


    </body>
</html>