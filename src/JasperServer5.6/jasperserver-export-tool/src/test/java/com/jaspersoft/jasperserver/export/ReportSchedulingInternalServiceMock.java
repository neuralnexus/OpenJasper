package com.jaspersoft.jasperserver.export;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.engine.scheduling.ReportSchedulingInternalService;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJob;

/**
 * Created with IntelliJ IDEA.
 * User: Zakhar.Tomchenco
 * Date: 7/25/12
 * Time: 4:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReportSchedulingInternalServiceMock implements ReportSchedulingInternalService {

    public void removeReportUnitJobs(String reportUnitURI) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void updateReportUnitURI(String oldURI, String newURI) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public ReportJob saveJob(ExecutionContext context, ReportJob job) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
