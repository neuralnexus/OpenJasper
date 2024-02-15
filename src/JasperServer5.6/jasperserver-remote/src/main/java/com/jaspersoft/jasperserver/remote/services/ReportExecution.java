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
package com.jaspersoft.jasperserver.remote.services;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.ReportUnitResult;
import com.jaspersoft.jasperserver.remote.exception.xml.ErrorDescriptor;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id: ReportExecution.java 26599 2012-12-10 13:04:23Z ykovalchyk $
 */
@XmlRootElement
public class ReportExecution {
    private ExecutionStatus status;
    private Integer totalPages;
    private Integer currentPage;
    private ErrorDescriptor errorDescriptor;
    private final ExportsContainer exports = new ExportsContainer();
    private ReportUnitResult reportUnitResult;
    private Map<String, String[]> rawParameters;
    private String requestId;
    private String reportURI;
    private ReportExecutionOptions options;
    private final Lock resultLock;
    private final Condition resultExist;

    public ReportExecution() {
        resultLock = new ReentrantLock();
        resultExist = resultLock.newCondition();
    }

    @XmlTransient
    public Map<String, String[]> getRawParameters() {
        return rawParameters;
    }

    public void setRawParameters(Map<String, String[]> rawParameters) {
        this.rawParameters = rawParameters;
    }

    @XmlTransient
    public ReportExecutionOptions getOptions() {
        return options;
    }

    public void setOptions(ReportExecutionOptions options) {
        this.options = options;
    }

    @XmlTransient
    public ReportUnitResult getFinalReportUnitResult() {
        resultLock.lock();
        try {
            while (reportUnitResult == null) {
                resultExist.await();
            }
        } catch (InterruptedException e) {
            throw new JSException(e);
        } finally {
            resultLock.unlock();
        }

        return reportUnitResult;
    }

    @XmlTransient
    public ReportUnitResult getReportUnitResult() {
        return reportUnitResult;
    }

    public void setReportUnitResult(ReportUnitResult reportUnitResult) {
        resultLock.lock();
        try {
            this.reportUnitResult = reportUnitResult;
            resultExist.signalAll();
        } finally {
            resultLock.unlock();
        }
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    @XmlElement(name = "export")
    @XmlElementWrapper(name = "exports")
    public Set<ExportExecution> getExportsSet() {
        return exports != null && !exports.isEmpty() ? new HashSet<ExportExecution>(exports.values()) : null;
    }

    public void setExportsSet(Set<ExportExecution> exportsSet) {
        exports.clear();
        if (exportsSet != null) {
            Map<ExportExecutionOptions, ExportExecution> exports = new HashMap<ExportExecutionOptions, ExportExecution>();
            for (ExportExecution currentExport : exportsSet) {
                exports.put(currentExport.getOptions(), currentExport);
            }
            exports.putAll(exports);


        }
    }

    @XmlTransient
    public ExportsContainer getExports() {
        return exports;
    }

    public ErrorDescriptor getErrorDescriptor() {
        return errorDescriptor;
    }

    public void setErrorDescriptor(ErrorDescriptor errorDescriptor) {
        this.errorDescriptor = errorDescriptor;
        this.status = ExecutionStatus.failed;
        synchronized (this){
            if(!exports.isEmpty()){
                for(ExportExecution exportExecution : exports.values()){
                    exportExecution.setErrorDescriptor(errorDescriptor);
                }
            }
        }
    }

    public String getReportURI() {
        return reportURI;
    }

    public void setReportURI(String reportURI) {
        this.reportURI = reportURI;
    }

    public ExecutionStatus getStatus() {
        return status;
    }

    public void setStatus(ExecutionStatus status) {
        if (this.status != status) {
            this.status = status;
            if (status == ExecutionStatus.cancelled || status == ExecutionStatus.ready) {
                synchronized (this) {
                    if (!exports.isEmpty()) {
                        for (ExportExecution exportExecution : exports.values()) {
                            switch (status) {
                                case ready:
                                    exportExecution.reset();
                                    break;
                                case cancelled:
                                    exportExecution.setStatus(ExecutionStatus.cancelled);
                                    break;
                            }
                        }
                    }
                }
            }
        }
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }
}
