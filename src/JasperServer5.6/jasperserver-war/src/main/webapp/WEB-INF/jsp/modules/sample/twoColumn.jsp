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
		<title>two column</title>
		
        <link rel="stylesheet" href="${pageContext.request.contextPath}/<spring:theme code='samples.css'/>" type="text/css" />

		<style type="text/css"></style>
	</head>
	<body id="test" class="twoColumn">
		
		<div class="primary column decorated">
	    <!-- cosmetic -->
			<div class="before IE7 cosmetic"></div>
			<div class="background cosmetic"></div>
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
						<div class="background cosmetic"></div>
						<div class="after IE7 cosmetic"></div>
					<!-- end cosmetic -->
						<div class="content rndCorners-all">			
							<div class="header"> 
								<div class="icon"></div>
								<div class="title">Primary Column</div>
								<div class="buttonSet"></div>       
							</div><!-- header -->				
							<div class="body">	
							</div><!-- body -->				
							<div class="footer rndCorners-bottom">
								<div class="buttonSet"></div> 
							</div><!-- footer -->
						</div><!-- content -->	   	 
					</div><!-- column -->
		
					<div class="secondary column decorated">
						<div class="sizer horizontal layout"></div>
						<button class="minimize"></button>
				    <!-- cosmetic -->
						<div class="before IE7 cosmetic"></div>
						<div class="background cosmetic"></div>
						<div class="after IE7 cosmetic"></div>
					<!-- end cosmetic -->
						<div class="content rndCorners-all">			
							<div class="header rndCorners-top"> 
								<div class="icon"></div>
								<div class="title">Secondary Column</div>
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
		
		<div class="secondary column decorated">
			<div class="sizer horizontal layout"></div>
			<button class="minimize"></button>
	    <!-- cosmetic -->
			<div class="before IE7 cosmetic"></div>
			<div class="background cosmetic"></div>
			<div class="after IE7 cosmetic"></div>
		<!-- end cosmetic -->
			<div class="content rndCorners-all">			
				<div class="header rndCorners-top"> 
					<div class="icon"></div>
					<div class="title">Secondary Column</div>
					<div class="buttonSet"></div>       
				</div><!-- header -->				
				<div class="body twoRow">
					
					<div class="panel pane primary">
				    	<div class="sizer vertical layout"></div>
				    	<!-- cosmetic -->
						<div class="before IE7 cosmetic"></div>
						<div class="background cosmetic"></div>
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
														     	<ul class="responsive collapsible folders">
									<li class="node open"><a href="twoColumn.jsp#"><b></b>item 1</a>
										<ul class="responsive">
											<li><a href="twoColumn.jsp#"><b></b>item 1.1</a></li>
											<li class="node open"><a href="twoColumn.jsp#"><b></b>item 1.2</a>
												<ul class="responsive">
													<li><a href="twoColumn.jsp#"><b></b>item 1.2.1</a></li>
													<li class="node open"><a href="twoColumn.jsp#"><b></b>item 1.2.2</a>
														<ul class="responsive">
															<li><a href="twoColumn.jsp#"><b></b>item 1.2.2.1</a></li>
															<li><a href="twoColumn.jsp#"><b></b>item 1.2.2.2</a></li>
															<li><a href="twoColumn.jsp#"><b></b>item 1.2.2.3</a></li>
														</ul>	
													</li>
													<li><a href="twoColumn.jsp#"><b></b>item 1.2.3</a></li>
												</ul>	
											</li>
											<li><a href="twoColumn.jsp#"><b></b>item 1.3</a></li>
										</ul>	
									</li>
									<li class="node loading" ><a href="twoColumn.jsp#"><b></b>item 2</a>
										<ul class="responsive">
											<li><a href="twoColumn.jsp#"><b></b>item 2.1</a></li>
											<li><a href="twoColumn.jsp#"><b></b>item 2.2</a></li>
											<li><a href="twoColumn.jsp#"><b></b>item 2.3</a></li>
										</ul>	
									</li>
									<li class="node closed" ><a href="twoColumn.jsp#"><b></b>item 3</a>
										<ul class="responsive">
											<li><a href="twoColumn.jsp#"><b></b>item 3.1</a></li>
											<li><a href="twoColumn.jsp#"><b></b>item 3.2</a></li>
											<li><a href="twoColumn.jsp#"><b></b>item 3.3</a></li>
										</ul>	
									</li>
								</ul>			        			

							</div><!-- body -->				
							<div class="footer">
								<div class="buttonSet"></div> 
							</div><!-- footer -->
						</div><!-- content -->	   	 
					</div><!-- panel -->
					
					<div class="panel pane lower">
				    	<div class="sizer vertical layout"></div>
				    	<!-- cosmetic -->
						<div class="before IE7 cosmetic"></div>
						<div class="background cosmetic"></div>
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

				</div><!-- body -->				
				<div class="footer rndCorners-bottom">
					<div class="buttonSet"></div> 
				</div><!-- footer -->
			</div><!-- content -->	   	 
		</div><!-- column -->
		
	</body>

</html>