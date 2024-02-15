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
package com.jaspersoft.jasperserver.api.engine.scheduling.domain.reportjobmodel;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.JasperServerAPI;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobMailNotification;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.List;

/**
 * Email notification model that can be defined for a report job.  Model is used in search/ update only.
 *
 * <p>
 * A notification model will result in an email being send to the specified recipients
 * at each job execution.
 * </p>
 *
 * @author Ivan Chan (ichan@jaspersoft.com)
 * @version $Id: ReportJobMailNotificationModel.java 47331 2014-07-18 09:13:06Z kklein $
 * @since 4.7
 */
@JasperServerAPI
public class ReportJobMailNotificationModel extends ReportJobMailNotification {

    private boolean isMessageTextModified = false;
    private boolean isResultSendTypeModified = false;
    private boolean isSubjectModified = false;
    private boolean isBccAddressesModified = false;
    private boolean isCcAddressesModified = false;
    private boolean isToAddressesModified = false;
    private boolean isSkipEmptyReportsModified = false;
    private boolean isMessageTextWhenJobFailsModified = false;
    private boolean isIncludingStackTraceWhenJobFailsModified = false;
    private boolean isSkipNotificationWhenJobFailsModified = false;

    /**
	 * Creates an empty job email notification.
	 */
	public ReportJobMailNotificationModel() {
		super();
	}

    /**
     * @deprecated ID is not supported in ReportJobModel
     */
    @Override
    @XmlTransient
	public long getId() {
        return super.getId();
	}
	/**
     * @deprecated ID is not supported in ReportJobModel
     */
    @Override
	public void setId(long id) {
        super.setId(id);
	}

    /**
     * @deprecated version is not supported in ReportJobModel
     */
    @Override
    @XmlTransient
	public int getVersion() {
        return super.getVersion();
	}
    /**
     * @deprecated version is not supported in ReportJobModel
     */
    @Override
	public void setVersion(int version) {
        super.setVersion(version);
	}

	/**
	 * Sets the message text to be used for the email notification.
	 *
	 * @param messageText the notification message text
	 */
	public void setMessageText(String messageText) {
        isMessageTextModified = true;
		super.setMessageText(messageText);
	}

	/**
	 * Specifies whether the notification would include the job output as
	 * attachments, or include links to the output in the repository.
	 *
	 * @param resultSendType one of {@link #RESULT_SEND} and
	 * {@link #RESULT_SEND_ATTACHMENT}
	 */
	public void setResultSendTypeCode(Byte resultSendType) {
        isResultSendTypeModified = true;
		super.setResultSendTypeCode(resultSendType);
	}

	/**
	 * Sets the subject to be used for the email notification.
	 *
	 * @param subject the email notification subject
	 */
	public void setSubject(String subject) {
		isSubjectModified = true;
        super.setSubject(subject);
	}

	/**
	 * Sets the email addresses that should be used as Bcc (Blind carbon copy)
	 * recipients for the email notification.
	 *
	 * @param bccAddresses the list of Bcc recipients as
	 * <code>java.lang.String</code> email addresses
	 */
	public void setBccAddresses(List bccAddresses) {
        isBccAddressesModified = true;
        super.setBccAddresses(bccAddresses);
	}

	/**
	 * Adds a Bcc (Blind carbon copy) recipient for the email notification.
	 *
	 * @param address the email address of the recipient
	 */
	public void addBcc(String address) {
        isBccAddressesModified = true;
		super.addBcc(address);
	}

	/**
	 * Sets the email addresses that should be used as CC (Carbon copy)
	 * recipients for the email notification.
	 *
	 * @param ccAddresses the list of CC recipients as
	 * <code>java.lang.String</code> email addresses
	 */
	public void setCcAddresses(List ccAddresses) {
        isCcAddressesModified = true;
		super.setCcAddresses(ccAddresses);
	}

	/**
	 * Adds a CC (Carbon copy) recipient for the email notification.
	 *
	 * @param address the email address of the recipient
	 */
	public void addCc(String address) {
        isCcAddressesModified = true;
		super.addCc(address);
	}

	/**
	 * Sets the email addresses that should be used as direct recipients for
	 * the email notification.
	 *
	 * @param toAddresses the list of CC recipients as
	 * <code>java.lang.String</code> email addresses
	 */
	public void setToAddresses(List toAddresses) {
        isToAddressesModified = true;
		super.setToAddresses(toAddresses);
	}

	/**
	 * Adds a CC (Carbon copy) recipient for the email notification.
	 *
	 * @param address the email address of the recipient
	 */
	public void addTo(String address) {
        isToAddressesModified = true;
		super.addTo(address);
	}

	/**
	 * Specifies whether the email notification should be skipped for job
	 * executions the produce empty reports.
	 *
	 * @param skipEmptyReports if <code>true</code>, no email notification will
	 * be sent if job executions that generate empty reports
	 * @since 2.0
	 */
	public void setSkipEmptyReports(boolean skipEmptyReports) {
        isSkipEmptyReportsModified = true;
        super.setSkipEmptyReports(skipEmptyReports);
	}

    /**
	 * Sets the message text to be used for the email notification when job fails.
	 *
	 * @param messageTextWhenErrorOccurs the notification message text
	 */
    public void setMessageTextWhenJobFails(String messageTextWhenErrorOccurs) {
        super.setMessageTextWhenJobFails(messageTextWhenErrorOccurs);
        isMessageTextWhenJobFailsModified = true;
    }

    /**
	 * Set whether the mail notification would include detail stack trace of exception
	 *
	 * @param  includeStackTraceWhenErrorOccurs including stack trace in mail notification
	 */
    public void setIncludingStackTraceWhenJobFails(boolean includeStackTraceWhenErrorOccurs) {
        super.setIncludingStackTraceWhenJobFails(includeStackTraceWhenErrorOccurs);
        isIncludingStackTraceWhenJobFailsModified = true;
    }

    /**
	 * Specifies whether the mail notification should send if job fails
	 *
	 * @param skipNotificationWhenJobFails skip mail notification when job fails
	 */
    public void setSkipNotificationWhenJobFails(boolean skipNotificationWhenJobFails) {
        isSkipNotificationWhenJobFailsModified = true;
        super.setSkipNotificationWhenJobFails(skipNotificationWhenJobFails);
    }

    /**
     * returns whether MessageText has been modified
     *
     * @return true if the attribute has been modified
     */
    public boolean isMessageTextModified() { return isMessageTextModified; }

    /**
     * returns whether ResultSendType has been modified
     *
     * @return true if the attribute has been modified
     */
    public boolean isResultSendTypeModified() { return isResultSendTypeModified; }

    /**
     * returns whether Subject has been modified
     *
     * @return true if the attribute has been modified
     */
    public boolean isSubjectModified() { return isSubjectModified; }

    /**
     * returns whether SkipEmptyReports has been modified
     *
     * @return true if the attribute has been modified
     */
    public boolean isSkipEmptyReportsModified() { return isSkipEmptyReportsModified; }

    /**
     * returns whether BccAddresses has been modified
     *
     * @return true if the attribute has been modified
     */
    public boolean isBccAddressesModified() { return isBccAddressesModified; }

    /**
     * returns whether CcAddresses has been modified
     *
     * @return true if the attribute has been modified
     */
    public boolean isCcAddressesModified() { return isCcAddressesModified; }

    /**
     * returns whether ToAddresses has been modified
     *
     * @return true if the attribute has been modified
     */
    public boolean isToAddressesModified() { return isToAddressesModified; }

    /**
     * returns whether CustomizeMessageToUserWhenErrorOccurs has been modified
     *
     * @return true if the attribute has been modified
     */
    public boolean isMessageTextWhenJobFailsModified() { return isMessageTextWhenJobFailsModified; }

    /**
     * returns whether IncludeStackTraceInMailWhenErrorOccurs has been modified
     *
     * @return true if the attribute has been modified
     */
    public boolean isIncludingStackTraceWhenJobFailsModified() { return isIncludingStackTraceWhenJobFailsModified; }

    /**
     * returns whether SkipNotificationWhenJobFails has been modified
     *
     * @return true if the attribute has been modified
     */
    public boolean isSkipNotificationWhenJobFailsModified() { return isSkipNotificationWhenJobFailsModified; }
}
