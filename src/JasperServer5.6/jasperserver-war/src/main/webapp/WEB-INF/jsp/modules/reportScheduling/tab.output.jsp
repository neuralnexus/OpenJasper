<!-- START OUTPUT FILE OPTIONS SECTION -->
<div id="outputFileOptions" class="section">

	<!-- not sure what the fieldset below is for -->
	<!-- <fieldset class="row instructions">
	    <legend class="offLeft"><span><spring:message code="report.scheduling.job.edit.output.instructions"/></span></legend>
	</fieldset> --> 
	
	<h4 class="title"><spring:message code="report.scheduling.job.edit.output.label"/></h4>
	
	<!-- start name and description -->
    <fieldset id="outputFileName" class="group first" title="<spring:message code="report.scheduling.job.edit.parameters.tooltip.baseFileName"/>">
        <label class="control input text">
        	<span class="wrap"><spring:message code="report.scheduling.job.edit.output.filename"/>:</span>
			<input name="baseOutputFilename" type="text" value="" maxlength="200"/>
			<span data-field="baseOutputFilename" class="message warning"></span>
        </label>
    </fieldset>
    <fieldset id="outputDescription" class="group">
        <label class="control textArea">
        	<span class="wrap"><spring:message code="report.scheduling.job.edit.output.description"/>:</span>
			<textarea name="outputDescription"></textarea>
			<span data-field="outputDescription" class="message warning"></span>
		</label>
    </fieldset>
	<!-- end name and description -->
		
	<!-- start time zone -->
    <fieldset id="timeZone" class="group">
        <label class="control select inline twoLine">
        	<span class="wrap"><spring:message code="report.scheduling.job.edit.output.timezone"/>:</span>
	        <select name="timeZone">
	            <c:forEach items="${userTimezones}" var="timezone">
	                <option value="${timezone.code}">
	                    <spring:message code="timezone.option" arguments='${timezone.code},${timezone.description}'/>
	                </option>
	            </c:forEach>
	        </select>
		</label>
    </fieldset>
    <!-- end time zone -->
		
	<!-- start output locale -->
    <fieldset id="outputLocale" class="group">
        <label class="control select inline twoLine">
        	<span class="wrap"><spring:message code="report.scheduling.job.edit.output.locale"/>:</span>
	        <select name="outputLocale">
	            <option value="" selected>(Default)</option>
	            <option value="en">en - English</option>
	            <option value="fr">fr - French</option>
	            <option value="it">it - Italian</option>
	            <option value="es">es - Spanish</option>
	            <option value="de">de - German</option>
	            <option value="ro">ro - Romanian</option>
	            <option value="ja">ja - Japanese</option>
	            <option value="zh_TW">zh_TW - Chinese (Taiwan)</option>
	            <option value="zh_CN">zh_CN - Chinese (China)</option>
	       </select>
		</label>
    </fieldset>
	<!-- end output locale -->
	
	<!-- start formats -->
    <fieldset id="outputFormat" class="group">
        <input id="outputFormats" type="hidden" />
        <legend>
        	<span class="wrap"><spring:message code="report.scheduling.job.edit.output.label.outputFormat"/>:</span>
        </legend>
        <ul class="list inputSet">
            <li class="leaf">
                <div class="control checkBox">
                     <label class="wrap" for="scheduler_box_5" title="<spring:message code="report.output.csv.label.tooltip"/>">CSV</label>
                     <input id="scheduler_box_5" name="outputFormats" value="CSV" type="checkbox">
                </div>
            </li>

            <li class="leaf">
                <div class="control checkBox">
                     <label class="wrap" for="scheduler_box_2" title="<spring:message code="report.output.html.label.tooltip"/>">HTML</label>
                     <input id="scheduler_box_2" name="outputFormats" value="HTML" type="checkbox">
                </div>
            </li>

            <li class="leaf">
                <div class="control checkBox">
                     <label class="wrap" for="scheduler_box_4" title="<spring:message code="report.output.rtf.label.tooltip"/>">RTF</label>
                     <input id="scheduler_box_4" name="outputFormats" value="RTF" type="checkbox">
                </div>
            </li>

            <li class="leaf">
                <div class="control checkBox">
                     <label class="wrap" for="scheduler_box_8" title="<spring:message code="report.output.docx.label.tooltip"/>">DOCX</label>
                     <input id="scheduler_box_8" name="outputFormats" value="DOCX" type="checkbox">
                </div>
            </li>

            <li class="leaf">
                <div class="control checkBox">
                     <label class="wrap" for="scheduler_box_9" title="<spring:message code="report.output.ods.label.tooltip"/>">ODS</label>
                     <input id="scheduler_box_9" name="outputFormats" value="ODS" type="checkbox">
                </div>
            </li>

            <li class="leaf">
                <div class="control checkBox">
                     <label class="wrap" for="scheduler_box_12" title="<spring:message code="report.output.xlsx.nopag.label.tooltip"/>">XLSX</label>
                     <input id="scheduler_box_12" name="outputFormats" value="XLSX_NOPAG" type="checkbox">
                </div>
            </li>

            <li class="leaf">
                <div class="control checkBox">
                     <label class="wrap" for="scheduler_box_11" title="<spring:message code="report.output.xls.nopag.label.tooltip"/>">Excel</label>
                     <input id="scheduler_box_11" name="outputFormats" value="XLS_NOPAG" type="checkbox">
                </div>
            </li>

            <li class="leaf">
                <div class="control checkBox">
                     <label class="wrap" for="scheduler_box_6" title="<spring:message code="report.output.odt.label.tooltip"/>">ODT</label>
                     <input id="scheduler_box_6" name="outputFormats" value="ODT" type="checkbox">
                </div>
            </li>

            <li class="leaf">
                <div class="control checkBox">
                     <label class="wrap" for="scheduler_box_10" title="<spring:message code="report.output.xlsx.label.tooltip"/>"><spring:message code="report.output.xlsx.label"/></label>
                     <input id="scheduler_box_10" name="outputFormats" value="XLSX" type="checkbox">
                </div>
            </li>

            <li class="leaf">
                <div class="control checkBox">
                     <label class="wrap" for="scheduler_box_3" title="<spring:message code="report.output.xls.label.tooltip"/>"><spring:message code="report.output.xls.label"/></label>
                     <input id="scheduler_box_3" name="outputFormats" value="XLS" type="checkbox">
                </div>
            </li>

            <li class="leaf">
                <div class="control checkBox">
                     <label class="wrap" for="scheduler_box_1" title="<spring:message code="report.output.pdf.label.tooltip"/>">PDF</label>
                     <input id="scheduler_box_1" name="outputFormats" value="PDF" type="checkbox">
                </div>
            </li>


            <li class="leaf">
                <div class="control checkBox">
                     <label class="wrap" for="scheduler_box_14" title="<spring:message code="report.output.pptx.label.tooltip"/>"><spring:message code="report.output.pptx.label"/></label>
                     <input id="scheduler_box_14" name="outputFormats" value="PPTX" type="checkbox">
                </div>
            </li>


            <c:if test="${enableDataSnapshot}">
                <li class="leaf">
                    <div class="control checkBox">
                        <label class="wrap" for="scheduler_box_13" title="<spring:message code="report.output.data.snapshot.label.tooltip"/>"><spring:message code="report.output.data.snapshot.label"/></label>
                        <input id="scheduler_box_13" name="outputFormats" value="DATA_SNAPSHOT" type="checkbox"/>
                    </div>
                </li>
            </c:if>
        </ul>

        <span data-field="outputFormats" class="message warning"></span>
    </fieldset>
    <!-- end formats -->

	<!-- start file handling -->
    <fieldset id="fileHandling" class="group">
        <legend class="">
        	<span class="wrap"><spring:message code="report.scheduling.job.edit.file.handling"/>:</span>
        </legend>
		<ul class="list inputSet">
			<li class="leaf">
				<div class="control checkBox">
					<label class="wrap" for="scheduler_overwriteFiles"><spring:message code="report.scheduling.job.edit.file.handling.overwrite"/></label>
					<input id="scheduler_overwriteFiles" type="checkbox" name="overwriteFiles" value="overwrite" />
				</div>
			</li>
			<li class="leaf">
                <div class="control checkBox twoLine">
                    <label class="wrap" for="scheduler_sequentialFilenames"><spring:message code="report.scheduling.job.edit.file.handling.sequential"/></label>
                    <span class="wrap"><spring:message code="report.scheduling.job.edit.file.handling.timestamp"/>:</span>
                    <input id="scheduler_sequentialFilenames" type="checkbox" name="sequentialFilenames" value="sequential" />
                    
                    <label class="control inline twoLine" title="<spring:message code="report.scheduling.job.edit.parameters.tooltip.timestamp.pattern.sequential"/>"> 
				        <input name="timestampPattern" type="text" maxlength="100" title="<spring:message code="report.scheduling.job.edit.parameters.tooltip.timestamp.pattern.simple.data"/>">
			            <span data-field="timestampPattern" class="message warning"></span>
                   </label>
                </div>
            </li>
		</ul>
    </fieldset>
	<!-- end file handling -->
</div>
	
<!-- start output destinations -->
<div id="outputDestination" class="section">
    <h4 class="title"><spring:message code="report.scheduling.job.edit.destination"/></h4>
	<ul class="list group first inputSet">
		<li id="repositoryOutput" class="leaf">
			<div class="control checkBox">
				<label class="control browser" for="scheduler_outputToRepository">
					<span class="wrap"><spring:message code="report.scheduling.job.edit.destination.repository"/></span>
				</label>
				<input id="scheduler_outputToRepository" type="checkbox" name="outputToRepository" />
			</div>
			<div class="control browser">
				<input type="text" id="outputRepository" name="outputRepository">
				<button name="outputRepositoryButton" class="button action" id="browser_button" type="button">
					<span class="wrap">
						<spring:message code="report.scheduling.job.edit.destination.browse"/>
						<span class="icon"></span>
					</span>
				</button>
				<span data-field="outputRepository" class="message warning"></span>
			</div>	
		</li>	

        <li id="fileSystemOutput" class="leaf">
            <div class="control checkBox">
                <label class="control browser" for="scheduler_outputToHostFileSystem">
                    <span class="wrap"><spring:message code="report.scheduling.job.edit.destination.fs"/></span>
                </label>
                <input id="scheduler_outputToHostFileSystem" type="checkbox" name="outputToHostFileSystem" />
            </div>
            <div class="control browser">
                <input type="text" name="outputHostFileSystem">
                <span data-field="outputHostFileSystem" disabled class="message warning"></span>
            </div>
        </li>

   		<li id="ftpServerOutput" class="leaf">
   			<ul class="list">
   				<li class="leaf">
					<div class="control checkBox">
			            <label class="control browser" for="scheduler_outputToFTPServer">
			            	<span class="wrap"><spring:message code="report.scheduling.job.edit.destination.ftp"/></span>
			            </label>
			            <input id="scheduler_outputToFTPServer" type="checkbox" name="outputToFTPServer" />
					</div>
					<div class="control text">
	                    <label class="control input text" for="ftpAddress">
	                    	<span class="wrap"><spring:message code="report.scheduling.job.edit.destination.ftp.server"/></span>
	                   	    <input id="ftpAddress" type="text" name="ftpAddress" />
	                        <span data-field="ftpAddress" class="message warning"></span>
	                    </label>		
					</div>
					<div class="control text">
	                    <label class="control input text" for="ftpDirectory">
	                    	<span class="wrap"><spring:message code="report.scheduling.job.edit.destination.ftp.dir"/></span>
	                        <input id="ftpDirectory" type="text" name="ftpDirectory" />
	                        <span data-field="ftpDirectory" class="message warning"></span>
	                    </label>				
					</div>
					<div class="control text">
	                    <label class="control input text" for="ftpUsername">
	                    	<span class="wrap"></span><spring:message code="report.scheduling.job.edit.destination.ftp.user"/>
	                    	<input id="ftpUsername" type="text" name="ftpUsername" />
	                    	<span data-field="ftpUsername" class="message warning"></span>	
	                    </label>			
					</div>
					<div class="control text">
	                    <label class="control input text" for="ftpPassword"><spring:message code="report.scheduling.job.edit.destination.ftp.password"/>
	                    	<span class="wrap"><input type="password" id="ftpPassword" name="ftpPassword" /></span>
	                   	 	<span data-field="ftpPassword" class="message warning"></span>
	                   	 </label>
					</div>
				</li>
				<li class="leaf">
					<div class="control checkBox">
	                    <label for="scheduler_useFTPS">
	                    	<span class="wrap"><spring:message code="report.scheduling.job.edit.destination.ftp.useftps"/></span>
	                    </label>
			            <input id="scheduler_useFTPS" type="checkbox" name="useFTPS" />
					</div>
					<div class="control text">
                        <label class="control input text">
                        	<span class="wrap"><spring:message code="report.scheduling.job.edit.destination.ftp.port"/></span>
                            <input type="text" name="ftpPort" />
                            <span data-field="ftpPort" class="message warning"></span>
                        </label>		
					</div>
				</li>
			</ul>
   		</li>
	</ul>

	<fieldset id="testConnection" class="group inputSet">
        <button name="ftpTestButton" id="ftpTestButton" class="button action ftp-test" type="button">
        	<span class="wrap"><spring:message code="report.scheduling.job.edit.destination.ftp.test"/>
        		<span class="icon"></span>
        	</span>
        </button>
            <span data-field="ftpTest" class="message warning"></span>
                                    
    </fieldset>
</div>
<!-- end output destinations -->

