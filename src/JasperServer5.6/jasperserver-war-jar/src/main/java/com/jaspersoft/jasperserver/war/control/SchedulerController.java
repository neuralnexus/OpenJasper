/*
 * Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.jaspersoft.jasperserver.war.control;

import com.jaspersoft.jasperserver.api.common.util.TimeZoneContextHolder;
import com.jaspersoft.jasperserver.api.common.util.TimeZonesList;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJob;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobSummary;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.ReportSchedulingService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author Chaim Arbiv
 * @version $id$
 * Controller to handle all the scheduling interaction.
 * It should not depend to Web-Flow and use only the REST v2 end points.
 */
public class SchedulerController extends MultiActionController {

    private static final Log log = LogFactory.getLog(SchedulerController.class);

    private TimeZonesList timezones;
    private ReportSchedulingService scheduler;

    @Autowired(required=true)
    private HttpServletRequest request;

    private boolean enableSaveToHostFS;

    private static final String LICENSE_MANAGER = "com.jaspersoft.ji.license.LicenseManager";
    private String enableDataSnapshot;


    public ModelAndView main(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("modules/reportScheduling/main");
        mav.addObject("timezone", TimeZoneContextHolder.getTimeZone().getID());
        mav.addObject("isPro", isProVersion());
        mav.addObject("userTimezones", timezones.getTimeZones(request.getLocale()));

        mav.addObject("enableSaveToHostFS", getEnableSaveToHostFS());
        mav.addObject("enableDataSnapshot", getEnableDataSnapshot());

        return mav;
    }

    public ModelAndView jobsummary(HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info("scheduler req with uri: "+ getReportUri(request));

        ModelAndView mav = new ModelAndView();
        mav.setViewName("modules/reportScheduling/jobsSummary");

        List<ReportJobSummary> jobsSummaryList = getScheduler().getScheduledJobSummaries(null, getReportUri(request));
        mav.addObject("jobsSummaryList", jobsSummaryList);
        log.debug("returned jobs: " + jobsSummaryList.size());

        if (jobsSummaryList.size()>0){
            mav.addObject("reportUnitURI", jobsSummaryList.get(0).getReportUnitURI());
        }
        mav.addObject("timezone", TimeZoneContextHolder.getTimeZone().getID());


        return mav;
    }

    public ModelAndView jobdetails(HttpServletRequest request, HttpServletResponse response) throws Exception {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("modules/reportScheduling/jobDetails");

        if (request.getParameter("id")!=null){
            long jobId = Long.parseLong(request.getParameter("id"));
            log.info("jobdetails for job "+jobId);

            ReportJob job = getScheduler().getScheduledJob(null, jobId);
            mav.addObject("job", job);

        }
        return mav;
    }


    private String getReportUri(HttpServletRequest request){
        String reportUnitURI = request.getParameter("reportUnitURI");
        if (reportUnitURI!=null && !reportUnitURI.isEmpty())
            return request.getParameter("reportUnitURI");
        else
            throw new IllegalArgumentException("Could not retrieve report uri");
    }

    public ReportSchedulingService getScheduler() {
        return scheduler;
    }

    public void setScheduler(ReportSchedulingService scheduler) {
        this.scheduler = scheduler;
    }

    /**
     * Helper method to determine if we are running the Pro or Enterprise edition
     * @return boolean indicating success..
     */
    public boolean isProVersion(){
        boolean isPro = false;
        try {
            Class clazz = Class.forName(LICENSE_MANAGER);
            if(clazz != null){
                isPro = true;
            }
        } catch (ClassNotFoundException e) {
            if(log.isDebugEnabled()){
                log.info("This is not a pro version. Access is denied");
            }
        }
        return isPro;
    }

    public TimeZonesList getTimezones()
    {
        return timezones;
    }

    public void setTimezones(TimeZonesList timezones)
    {
        this.timezones = timezones;
    }

    public String getEnableSaveToHostFS() {
        return Boolean.toString(isEnableSaveToHostFS());
    }

    public boolean isEnableSaveToHostFS() {
        return enableSaveToHostFS;
    }

    public void setEnableSaveToHostFS(boolean enableSaveToHostFS) {
        this.enableSaveToHostFS = enableSaveToHostFS;
    }

    public void setEnableDataSnapshot(String enableDataSnapshot) {
        this.enableDataSnapshot = enableDataSnapshot;
    }

    public String getEnableDataSnapshot() {
        return enableDataSnapshot;
    }
}
