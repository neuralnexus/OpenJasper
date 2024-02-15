
<div class="neverFire">
    <input type="text" name="triggerWillNeverFire" class="hidden"/>
    <div data-field="triggerWillNeverFire" class="warning message"></div>
</div>

<!-- *START, SCHEDULE START SECTION -->
<div id="scheduleStart" class="section">
    <h4 class="title"><spring:message code="report.scheduling.job.edit.schedulestart"/></h4>
    <!-- *START JOB GROUP -->
    <fieldset id="jobCommon" class="group first">
        <legend><span><spring:message code="report.scheduling.job.edit.startdate"/>:</span></legend>
        <ul class="list inputSet">
            <li class="leaf">
                <div class="control radio">
                    <label for="startImmediately" class="wrap"><spring:message code="report.scheduling.job.edit.trigger.start.immediately"/></label>
                    <input id="startImmediately" name="startType" type="radio" value="1">
                </div>
            </li>
            <li class="leaf last">
                <div id="startDateLabel" class="control radio twoLine">
                    <label for="startDate" class="wrap"><spring:message code="report.scheduling.job.edit.onspecificdate"/>:</label>
                    <input id="startDate" name="startType" type="radio" value="2">
                    <div id="startOn" class="control picker inline">
                        <input type="text" name="startDate" class="datepicker">
                        <div data-field="startDate" class="warning message"></div>
                    </div>
                </div>
            </li>
        </ul>
    </fieldset>
    <!-- TIME ZONE GROUP -->
    <fieldset id="timeZone" class="group">
        <label class="control select inline twoLine">
            <span class="wrap"><spring:message code="report.scheduling.job.edit.output.label.timezone"/>:</span>
            <select name="schedulerTimeZone">
                <c:forEach items="${userTimezones}" var="timezone">
                    <option value="${timezone.code}">
                        <spring:message code="timezone.option" arguments='${timezone.code},${timezone.description}'/>
                    </option>
                </c:forEach>
            </select>
        </label>
    </fieldset>
</div>
<!-- *END, SCHEDULE START SECTION -->



<!-- *START, RECURRENCE SECTION -->
<div id="scheduleRecurrence" class="section">
    <h4 class="title"><spring:message code="report.scheduling.job.edit.trigger.recurrence"/></h4>

    <!-- RECURRENCE TYPE GROUP -->
    <fieldset id="recurrenceSelector" class="group first">
        <label class="control select inline twoLine">
            <span class="wrap"><spring:message code="report.scheduling.job.edit.recurrencetype"/>:</span>
            <select name="recurrenceType">
                <option value="none"><spring:message code="report.scheduling.job.edit.trigger.recurrence.type.none"/></option>
                <option value="simple"><spring:message code="report.scheduling.job.edit.trigger.recurrence.type.simple"/></option>
                <option value="calendar"><spring:message code="report.scheduling.job.edit.trigger.recurrence.type.calendar"/></option>
            </select>
        </label>
    </fieldset>

    <!-- *START, simple recurrence repetition -->
    <fieldset id="simpleRecurrenceInterval" data-recurrence="simple" class="group">
        <label class="control select inline">
            <span class="wrap"><spring:message code="report.scheduling.job.edit.repeatevery"/>:</span>
            <div class="control inline">
                <input name="recurrenceInterval" type="text">
                <div data-field="recurrenceInterval" class="warning message"></div>
            </div>
        </label>
        <select name="recurrenceIntervalUnit">
            <option value="MINUTE"><spring:message code="job.interval.unit.minute.label"/></option>
            <option value="HOUR"><spring:message code="job.interval.unit.hour.label"/></option>
            <option value="DAY"><spring:message code="job.interval.unit.day.label"/></option>
            <option value="WEEK"><spring:message code="job.interval.unit.week.label"/></option>
        </select>
	</fieldset>
	<!-- *END, simple recurrence repetition -->
</div>

<!-- *START simple recurrence end date -->
<fieldset id="scheduleEndSimple" class="section" data-recurrence="simple">
	<h4 class="title"><spring:message code="report.scheduling.job.edit.scheduleend"/></h4>
    <fieldset data-recurrence="simple" class="group first">
        <legend class="">
        	<span><spring:message code="report.scheduling.job.edit.enddate"/>:</span>
        </legend>
        <ul class="list inputSet">
            <li class="leaf">
                <div class="control radio twoLine">
                    <label for="fixedRepeat" class="wrap"><spring:message code="report.scheduling.job.edit.runasetnumberoftimes"/></label>
                    <span class="wrap"><spring:message code="report.scheduling.job.edit.numberofruns"/>:</span>
                    <input id="fixedRepeat" type="radio" name="endat" value="numberOfTimes">
                    <span class="control inline twoLine">
                        <input type="text" id="maxOccurrences" name="occurrenceCount">
                        <div data-field="occurrenceCount" class="warning message"></div>
                    </span>
                </div>
            </li>
            <li class="leaf">
                <div id="calendarRepeatSet" class="control radio twoLine">
                    <label for="calendarRepeat" class="wrap"><spring:message code="report.scheduling.job.edit.rununtilaspecifieddate"/>:</label>
                    <input id="calendarRepeat" type="radio" name="endat" value="specificDate">
                    <span id="repeatEndDate" class="control picker inline twoline">
                        <input type="text" class="datepicker" name="simpleEndDate">
                        <div data-field="simpleEndDate" class="warning message"></div>
                    </span>
                </div>
            </li>
            <li class="leaf last">
                <div class="control radio">
                    <label for="indefiniteRepeat" class="wrap"><spring:message code="report.scheduling.job.edit.runindefinitely"/></label>
                    <input id="indefiniteRepeat" type="radio" name="endat" value="indefinitely">
                </div>
            </li>
        </ul>
    </fieldset>
</fieldset>
<!-- *END simple recurrence end date -->
<!-- *END, SIMPLE RECURRENCE -->


<!-- *START, CALENDAR RECURRENCE -->
<!-- *START calendar recurrence repetition -->
<div class="section">
    <fieldset id="calendarRecurrence" data-recurrence="calendar" class="group first">
        <ul class="list">
            <li id="months" class="node">
                <fieldset class="group">
                    <legend>
                        <span><spring:message code="report.scheduling.job.edit.trigger.label.months"/>:</span>
                    </legend>
	                <ul class="list inputSet">
	                    <li class="leaf">
	                        <div title="Select to recur every month" class="control radio">
	                            <label for="everyMonth_radio" class="wrap"><spring:message code="report.scheduling.job.edit.trigger.label.months.all"/></label>
	                            <input type="radio" name="whichMonth" id="everyMonth_radio" value="everyMonth">
	                        </div>
	                    </li>
	                    <li class="leaf">
	                        <div id="selectedMonths" class="control radio twoLine">
	                            <label for="selectedMonths_radio" class="wrap"><spring:message code="report.scheduling.job.edit.trigger.label.months.selected"/></label>
	                            <input id="selectedMonths_radio" type="radio" name="whichMonth" value="selectedMonths">
                                <span class="control select multiple">
                                    <select name="monthSelector" size="12" class="monthSelector" multiple="multiple">
                                        <option value="1"><spring:message code="monts.label.jan"/></option>
                                        <option value="2"><spring:message code="monts.label.feb"/></option>
                                        <option value="3"><spring:message code="monts.label.mar"/></option>
                                        <option value="4"><spring:message code="monts.label.apr"/></option>
                                        <option value="5"><spring:message code="monts.label.may"/></option>
                                        <option value="6"><spring:message code="monts.label.jun"/></option>
                                        <option value="7"><spring:message code="monts.label.jul"/></option>
                                        <option value="8"><spring:message code="monts.label.aug"/></option>
                                        <option value="9"><spring:message code="monts.label.sep"/></option>
                                        <option value="10"><spring:message code="monts.label.oct"/></option>
                                        <option value="11"><spring:message code="monts.label.nov"/></option>
                                        <option value="12"><spring:message code="monts.label.dec"/></option>
                                    </select>
                                    <div data-field="monthSelector" class="warning message"></div>
                                </span>
	                        </div>
	                    </li>
	                </ul>
                </fieldset>
            </li>

            <li id="days" class="node">
                <fieldset class="group">
                    <legend>
                        <span><spring:message code="report.scheduling.job.edit.trigger.label.days"/>:</span>
                    </legend>
                    <ul class="list inputSet">
                        <li class="leaf">
                            <div class="control radio">
                                <label for="everyDay_radio" class="wrap"><spring:message code="report.scheduling.job.edit.trigger.label.days.all"/></label>
                                <input id="everyDay_radio" type="radio" name="whichDay" value="everyDay">
                            </div>
                        </li>
                        <li class="leaf">
                            <div id="weekDays" class="control radio twoLine">
                                <label for="weekDays_radio" class="wrap"><spring:message code="report.scheduling.job.edit.selecteddays"/>:</label>
                                <input id="weekDays_radio" type="radio" name="whichDay" value="selectedDays">
                                <span class="control select multiple">
                                    <select name="daySelector" size="7" class="daySelector" multiple="multiple">
                                        <option value="1"><spring:message code="week.days.label.sun"/></option>
                                        <option value="2"><spring:message code="week.days.label.mon"/></option>
                                        <option value="3"><spring:message code="week.days.label.tue"/></option>
                                        <option value="4"><spring:message code="week.days.label.wen"/></option>
                                        <option value="5"><spring:message code="week.days.label.thu"/></option>
                                        <option value="6"><spring:message code="week.days.label.fri"/></option>
                                        <option value="7"><spring:message code="week.days.label.sat"/></option>
                                    </select>
                                    <div data-field="daySelector" class="warning message"></div>
                                </span>
                            </div>
                        </li>
                        <li class="leaf">
                            <div id="monthDays" class="control radio twoLine">
                                <label for="monthDays_radio" class="wrap"><spring:message code="report.scheduling.job.edit.datesinmonth"/>:</label>
                                <input id="monthDays_radio" type="radio" name="whichDay" value="datesInMonth">
                                <div class="control input inline text">
                                    <input id="theMonthDays" name="datesInMonth" type="text">
                                    <p class="message hint"><spring:message code="report.scheduling.job.edit.enterdatesordateranges"/></p>
                                    <div data-field="datesInMonth" class="warning message"></div>
                                </div>
                            </div>
                        </li>
                    </ul>
                </fieldset>
            </li>

            <li id="repeatTimes" class="node">
                <fieldset class="group">
                    <legend>
                    	<span><spring:message code="report.scheduling.job.edit.trigger.label.times"/>:</span>
                   	</legend>
                    <ul class="list inputSet">
                        <li class="leaf">
                            <div class="control text twoLine">
                                <label class="control input text">
                                    <span class="wrap"><spring:message code="report.scheduling.job.edit.trigger.label.hours"/>:</span>
                                    <input type="text" name="hours">
                                    <p class="message hint"><spring:message code="report.scheduling.job.edit.trigger.label.hours.tip"/></p>
                                    <div data-field="hours" class="warning message"></div>
                                </label>
                            </div>
                        </li>
                        <li class="leaf">
                            <div class="control text twoLine">
                                <label class="control input text">
                                    <span class="wrap"><spring:message code="report.scheduling.job.edit.trigger.label.minutes"/>:</span>
                                    <input type="text" name="minutes">
                                    <p class="message hint"><spring:message code="report.scheduling.job.edit.trigger.label.minutes.tip"/></p>
                                    <div data-field="minutes" class="warning message"></div>
                                </label>
                            </div>
                        </li>
                    </ul>
                </fieldset>
            </li>
        </ul>
    </fieldset>
</div>
<!-- *END calendar recurrence repetition -->
<!-- *END, CALENDAR RECURRENCE -->
    
<!-- *START calendar recurrence end date -->
<fieldset id="calendarEnd" class="section" data-recurrence="calendar">
    <h4 class="title"><spring:message code="report.scheduling.job.edit.scheduleend"/></h4>
    <fieldset id="calendarEnd" class="group first">
    <label class="control twoLine">
        <span class="wrap"><spring:message code="report.scheduling.job.edit.enddate"/>:</span>
        <span class="control picker">
            <input id="trigger_endDate_calendar" name="calendarEndDate" type="text" class="datepicker">
            <div data-field="calendarEndDate" class="warning message"></div>
        </span>
    </label>
    </fieldset>
</fieldset>
<!-- *END calendar recurrence end date -->

<!-- *START holidays -->
<div class="section calendarBlock">
    <div id="holidayCalendar" class="section holidayCalendar">
        <h4 class="title"><spring:message code="report.scheduling.job.edit.holidays"/></h4>
        <fieldset class="group first">
            <label class="control twoLine">
                <span class="wrap"><spring:message code="report.scheduling.job.edit.calendar.ofdates.toexclude"/>:</span>
                <select name="calendarSelect"></select>
            </label>
        </fieldset>
    </div>
</div>
<!-- *END holidays -->
