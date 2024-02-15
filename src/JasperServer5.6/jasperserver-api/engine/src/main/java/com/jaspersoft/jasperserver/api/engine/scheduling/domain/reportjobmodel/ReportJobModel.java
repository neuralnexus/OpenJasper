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

/**
 * Definition model of a report execution job. Model is used in search/ update only.
 *
 * <p>
 * A report job definition specifies wich report to execute and when,
 * what output to generate and where to send the output.
 * </p>
 *
 * @author Ivan Chan (ichan@jaspersoft.com)
 * @version $Id: ReportJobModel.java 47331 2014-07-18 09:13:06Z kklein $
 * @since 4.7
 */

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.JasperServerAPI;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJob;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobAlert;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobMailNotification;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobRepositoryDestination;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobSimpleTrigger;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobSource;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobTrigger;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.jaxb.OutputFormatXmlAdapter;
import org.springframework.ui.Model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.transform.Source;
import java.sql.Timestamp;
import java.util.Set;
@XmlRootElement(name = "jobModel")
@JasperServerAPI
public class ReportJobModel extends ReportJob {

    public enum ReportJobSortType {
        NONE,
        SORTBY_JOBID,
        SORTBY_JOBNAME,
        SORTBY_REPORTURI,
        SORTBY_REPORTNAME,
        SORTBY_REPORTFOLDER,
        SORTBY_OWNER,
        SORTBY_STATUS,
        SORTBY_LASTRUN,
        SORTBY_NEXTRUN;
    }

    private boolean isCreationDateModified = false;
    private boolean isSourceModified = false;
    private boolean isTriggerModified = false;
    private boolean isMailNotificationModified = false;
    private boolean isAlertModified = false;
    private boolean isContentRespositoryDestinationModified = false;
    private boolean isDescriptionModified = false;
    private boolean isLabelModified = false;
    private boolean isBaseOutputFileNameModified = false;
    private boolean isOutputFormatsModified = false;
    private boolean isUsernameModified = false;
    private boolean isOutputLocaleModified = false;
    private boolean isRuntimeInformationModified = false;

    private ReportJobRuntimeInformationModel runtimeInformation = null;


    /**
	 * Returns the report execution source, consisting of a report and a set
	 * of input values for the report.
	 *
	 *
	 * @return the report execution source
	 * @see #setSourceModel(ReportJobSourceModel)
     * @deprecated use #getSourceModel() instead
	 */
    @XmlTransient
    @Override
	public ReportJobSource getSource() {
        return getSourceModel();
	}

    /**
	 * Returns the report execution source, consisting of a report and a set
	 * of input values for the report.
	 *
	 * @return the report execution source
	 * @see #setSourceModel(ReportJobSourceModel)
	 */
	public ReportJobSourceModel getSourceModel() {
        ReportJobSource model = super.getSource();
        if (model == null) return null;
        if (model instanceof ReportJobSourceModel) return (ReportJobSourceModel) model;
        throw new JSException("Please use ReportJobSourceModel instead of ReportJobSource in ReportJobModel class.");
	}

	/**
	 * Sets the report execution source for this job.
	 *
	 * <p>
	 * A report job needs to be set a source before being saved.
	 * </p>
	 *
	 * @param source the report job source
     * @deprecated use #getSourceMode() instead
	 */
	public void setSource(ReportJobSource source) {
        if (source == null) setSourceModel(null);
		else if (source instanceof ReportJobSourceModel) setSourceModel((ReportJobSourceModel)source);
        else throw new JSException("Please use ReportJobSourceModel instead of ReportJobSource in ReportJobModel class.");
	}

    /**
	 * Sets the report execution source for this job.
	 *
	 * <p>
	 * A report job needs to be set a source before being saved.
	 * </p>
	 *
	 * @param source the report job source
     * @see #getSourceModel()
	 */
	public void setSourceModel(ReportJobSourceModel source) {
       isSourceModified = true;
       super.setSource(source);
	}

	/**
	 * Returns the job trigger, which specifies when the report should be
	 * executed.
	 *
	 * @return the job trigger
     * @deprecated use #getTriggerModel() instead
	 */
    @Override
    @XmlTransient
	public ReportJobTrigger getTrigger() {
		return getTriggerModel();
	}

	/**
	 * Returns the job trigger, which specifies when the report should be
	 * executed.
	 *
	 * @return the job trigger
	 */
    @XmlElements({
            @XmlElement(name = "simpleTriggerModel", type = ReportJobSimpleTriggerModel.class),
            @XmlElement(name = "calendarTriggerModel", type = ReportJobCalendarTriggerModel.class)})
	public ReportJobTrigger getTriggerModel() {
		ReportJobTrigger model = super.getTrigger();
        if (model == null) return null;
        if ((model instanceof ReportJobSimpleTriggerModel) || (model instanceof ReportJobCalendarTriggerModel)) return model;
        else throw new JSException("Please useReportJobTriggerModel instead of ReportJobTrigger in ReportJobModel class.");
	}

	/**
	 * Sets the report job trigger.
	 *
	 * <p>
	 * The trigger defines when the report jobs is to be executed.
	 * A job can be executed only once or can recur either at fixed intervals
	 * or at specific calendar moments.
	 * </p>
	 *
	 * @param trigger the job trigger
	 * @see ReportJobSimpleTriggerModel
	 * @see ReportJobCalendarTriggerModel
     * @deprecated use #setTriggerModel(ReportJobTriggerModel) instead
	 */
	public void setTrigger(ReportJobTrigger trigger) {
        this.setTriggerModel(trigger);
	}

    /**
	 * Sets the report job trigger.
	 *
	 * <p>
	 * The trigger defines when the report jobs is to be executed.
	 * A job can be executed only once or can recur either at fixed intervals
	 * or at specific calendar moments.
	 * </p>
	 *
	 * @param trigger the job trigger
	 * @see ReportJobSimpleTriggerModel
	 * @see ReportJobCalendarTriggerModel
	 */
	public void setTriggerModel(ReportJobTrigger trigger) {
        isTriggerModified = true;
        if (trigger == null) super.setTrigger(null);
		else if ((trigger instanceof ReportJobSimpleTriggerModel) || (trigger instanceof ReportJobCalendarTriggerModel))
            super.setTrigger(trigger);
        else {
            if ((trigger instanceof ReportJobSimpleTrigger))
                throw new JSException("Please use ReportJobSimpleTriggerModel or  instead of ReportJobSimpleTrigger in ReportJobModel class.");
            else
                throw new JSException("Please use ReportJobCalendarTriggerModel or  instead of ReportJobCalendarTrigger in ReportJobModel class.");
        }
	}


	/**
	 * Returns the information related to the email alert which is to be
	 * sent at job execution time.
	 *
	 * @return email alert information associated with the job, or
	 * <code>null</code> if the job is not to send any notification
	 * @see #setAlert(ReportJobAlertModel)
     * @deprecated use #getAlertModel() instead
	 */
    public ReportJobAlert getAlert() {
        return getAlertModel();
    }

    /**
	 * Returns the information related to the email alert which is to be
	 * sent at job execution time.
	 *
	 * @return email alert information associated with the job, or
	 * <code>null</code> if the job is not to send any alert
	 * @see #setAlert(ReportJobAlertModel)
	 */
    public ReportJobAlertModel getAlertModel() {
        ReportJobAlert model = super.getAlert();
        if (model == null) return null;
        if (model instanceof ReportJobAlertModel) return (ReportJobAlertModel) model;
        throw new JSException("Please use ReportJobAlertModel instead of ReportJobAlert in ReportJobModel class.");
    }

	/**
	 * Defines an email alert for the report job.
	 *
	 * <p>
	 * An email alert will be send each time the report job executes.
	 * </p>
	 *
	 * @param alert the job email alert
     * @deprecated use #setAlertModel() instead
	 */
    public void setAlert(ReportJobAlertModel alert) {
	    if (alert == null) setAlertModel(null);
		else if (alert instanceof ReportJobAlertModel) setAlertModel((ReportJobAlertModel) alert);
        else throw new JSException("Please use ReportJobAlertModel instead of ReportJobAlert in ReportJobModel class.");
	}

    /**
	 * Defines an email alert for the report job.
	 *
	 * <p>
	 * An email alert will be send each time the report job executes.
	 * </p>
	 *
	 * @param alert the job email alert information
	 */
	public void setAlertModel(ReportJobAlertModel alert) {
		super.setAlert(alert);
        isAlertModified = true;
	}


	/**
	 * Returns the information related to the email notification which is to be
	 * sent at job execution time.
	 *
	 * @return email notification information associated with the job, or
	 * <code>null</code> if the job is not to send any notification
	 * @see #setMailNotification(ReportJobMailNotificationModel)
     * @deprecated use #getMailNotificationModel() instead
	 */
    @XmlTransient
    @Override
	public ReportJobMailNotification getMailNotification() {
		return getMailNotificationModel();
	}

	/**
	 * Returns the information related to the email notification which is to be
	 * sent at job execution time.
	 *
	 * @return email notification information associated with the job, or
	 * <code>null</code> if the job is not to send any notification
	 * @see #setMailNotification(ReportJobMailNotificationModel)
	 */
	public ReportJobMailNotificationModel getMailNotificationModel() {
		ReportJobMailNotification model = super.getMailNotification();
        if (model == null) return null;
        if (model instanceof ReportJobMailNotificationModel) return (ReportJobMailNotificationModel) model;
        throw new JSException("Please use ReportJobMailNotificationModel instead of ReportJobMailNotification in ReportJobModel class.");
	}

	/**
	 * Defines an email notification for the report job.
	 *
	 * <p>
	 * An email notification will be send each time the report job executes.
	 * </p>
	 *
	 * @param mailNotification the job email notification information
     * @deprecated use #setMailNotificationModel() instead
	 */
	public void setMailNotification(ReportJobMailNotificationModel mailNotification) {
	    if (mailNotification == null) setMailNotificationModel(null);
		else if (mailNotification instanceof ReportJobMailNotificationModel) setMailNotificationModel((ReportJobMailNotificationModel) mailNotification);
        else throw new JSException("Please use ReportJobMailNotificationModel instead of ReportJobMailNotification in ReportJobModel class.");
	}

    /**
	 * Defines an email notification for the report job.
	 *
	 * <p>
	 * An email notification will be send each time the report job executes.
	 * </p>
	 *
	 * @param mailNotification the job email notification information
	 */
	public void setMailNotificationModel(ReportJobMailNotificationModel mailNotification) {
		super.setMailNotification(mailNotification);
        isMailNotificationModified = true;
	}


	/**
	 * Returns information related to the repository output of the report job.
	 *
	 * <p>
	 * The output files created by the job are saved in the repository as
	 * content resources.
	 * This method provides attributes related to way the repository resources
	 * should be generated.
	 * </p>
	 *
	 * @return repository output information
	 * @see com.jaspersoft.jasperserver.api.metadata.common.domain.ContentResource
     * @deprecated use #getContentRepositoryDestinationModel()instead
	 */
    @XmlTransient
	public ReportJobRepositoryDestination getContentRepositoryDestination() {
		return getContentRepositoryDestinationModel();
	}

    /**
	 * Returns information related to the repository output of the report job.
	 *
	 * <p>
	 * The output files created by the job are saved in the repository as
	 * content resources.
	 * This method provides attributes related to way the repository resources
	 * should be generated.
	 * </p>
	 *
	 * @return repository output information
	 * @see com.jaspersoft.jasperserver.api.metadata.common.domain.ContentResource
	 */
    @XmlElement(name = "repositoryDestinationModel")
	public ReportJobRepositoryDestinationModel getContentRepositoryDestinationModel() {
        ReportJobRepositoryDestination model = super.getContentRepositoryDestination();
        if (model == null) return null;
        if (model instanceof ReportJobRepositoryDestinationModel) return (ReportJobRepositoryDestinationModel) model;
        throw new JSException("Please use ReportJobRepositoryDestinationModel instead of ReportJobRepositoryDestination in ReportJobModel class.");
	}

	/**
	 * Sets attributes that specify how the job output is to be saved in the
	 * repository.
	 *
	 * <p>
	 * These attributes need to be set prior to scheduling the job as the
	 * repository output is mandatory.
	 * </p>
	 *
	 * @param contentRepositoryDestination repository output attributes
     * @deprecated use #setContentRepositoryDestinationModel()instead
	 */
	public void setContentRepositoryDestination(
			ReportJobRepositoryDestinationModel contentRepositoryDestination) {
		if (contentRepositoryDestination == null) setContentRepositoryDestinationModel(null);
		else if (contentRepositoryDestination instanceof ReportJobRepositoryDestinationModel)
            setContentRepositoryDestinationModel((ReportJobRepositoryDestinationModel) contentRepositoryDestination);
        else throw new JSException("Please use ReportJobRepositoryDestinationModel instead of ReportJobRepositoryDestinationModel in ReportJobModel class.");
	}

    /**
	 * Sets attributes that specify how the job output is to be saved in the
	 * repository.
	 *
	 * <p>
	 * These attributes need to be set prior to scheduling the job as the
	 * repository output is mandatory.
	 * </p>
	 *
	 * @param contentRepositoryDestination repository output attributes
	 */
	public void setContentRepositoryDestinationModel(ReportJobRepositoryDestinationModel contentRepositoryDestination) {
		super.setContentRepositoryDestination(contentRepositoryDestination);
        isContentRespositoryDestinationModified = true;
	}

    /**
	 * get runtime information attributes for searching
	 *
	 * @return runtime Information Model for searhing
	 */
	public ReportJobRuntimeInformationModel getRuntimeInformationModel() {
		return runtimeInformation;
	}

     /**
	 * Sets runtime information attributes for searching
	 *
	 * @param runtimeInformationModel search by runtime information
	 */
	public void setRuntimeInformationModel(ReportJobRuntimeInformationModel runtimeInformationModel) {
        isRuntimeInformationModified = true;
		this.runtimeInformation = runtimeInformationModel;
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
    public void setId(long id) {
        super.setId(id);
    }

    /**
     * @deprecated Version is not supported in ReportJobModel
     */
    @Override
    @XmlTransient
    public int getVersion() {
        return super.getVersion();
    }
    /**
     * @deprecated Version is not supported in ReportJobModel
     */
    public void setVersion(int version) {
        super.setVersion(version);
    }

    /**
     * Sets a description for the job
     *
     * @param description the job description
     */
    public void setDescription(String description) {
        isDescriptionModified = true;
        super.setDescription(description);
    }

    /**
     * Sets creation date for the job
     *
     * @param creationDate the job creation date
     * @since 4.7
     */
    public void setCreationDate(Timestamp creationDate) {
        isCreationDateModified = true;
        super.setCreationDate(creationDate);
    }

    /**
     * Sets a mandatory short description for the report job.
     *
     * @param label the job label
     */
    public void setLabel(String label) {
        isLabelModified = true;
        super.setLabel(label);
    }

    /**
     * Sets the base filename to be used for the report job output files.
     *
     * @param baseOutputFilename the job output base filename
     * @see #getBaseOutputFilename()
     */
    public void setBaseOutputFilename(String baseOutputFilename) {
        isBaseOutputFileNameModified = true;
        super.setBaseOutputFilename(baseOutputFilename);
    }

    /**
     * Sets the list of output formats that will be generated by the job.
     *
     * @param outputFormats the set of output formats as
     * <code>java.lang.Byte</code> keys
     * @see #getOutputFormatsSet()
     */
    public void setOutputFormatsSet(Set<Byte> outputFormats) {
        isOutputFormatsModified = true;
        super.setOutputFormatsSet(outputFormats);
    }

    /**
     * Adds an output format to the job.
     *
     * @param outputFormat the output format key
     * @return <code>true</code> if the ouput was not already present in the job
     * @see #setOutputFormats(Set)
     */
    public boolean addOutputFormat(byte outputFormat) {
        isOutputFormatsModified = true;
        return super.addOutputFormat(outputFormat);
    }

    /**
     * Removes an output format from the job.
     *
     * @param outputFormat the key of the output format to remoce
     * @return <code>true</code> if the output format was present in the job
     */
    public boolean removeOutputFormat(byte outputFormat) {
        isOutputFormatsModified = true;
        return super.removeOutputFormat(outputFormat);
    }

    /**
     * Sets the owner of this job.
     *
     * <p>
     * This method should not be called by code that schedules jobs as the job
     * owner is automatically set when the job is saved, overwriting any existing
     * value.
     * </p>
     *
     * @param username the job owner
     */
    public void setUsername(String username) {
        isUsernameModified = true;
        super.setUsername(username);
    }

    /**
     * Sets a locale to be used to execute the report.
     *
     * <p>
     * The report output will be localized according to the provided locale.
     * </p>
     *
     * @param outputLocale the locale code as in <code>java.util.Locale.toString()</code>
     */
    public void setOutputLocale(String outputLocale) {
        isOutputLocaleModified = true;
        super.setOutputLocale(outputLocale);
    }

    /**
     * returns whether CreationDate has been modified
     *
     * @return true if the attribute has been modified
     */
    public boolean isCreationDateModified() { return isCreationDateModified; }

    /**
     * returns whether source has been modified
     *
     * @return true if the attribute has been modified
     */
    public boolean isSourceModified() { return isSourceModified; }

    /**
     * returns whether trigger has been modified
     *
     * @return true if the attribute has been modified
     */
    public boolean isTriggerModified() { return isTriggerModified; }
    /**
     * returns whether mail notification has been modified
     *
     * @return true if the attribute has been modified
     */
    public boolean isMailNotificationModified() { return isMailNotificationModified; }
    /**
     * returns whether alert has been modified
     *
     * @return true if the attribute has been modified
     */
    public boolean isAlertModified() { return isAlertModified; }
    /**
     * returns whether ContentRespositoryDestination has been modified
     *
     * @return true if the attribute has been modified
     */
    public boolean isContentRespositoryDestinationModified() { return isContentRespositoryDestinationModified; }
    /**
     * returns whether description has been modified
     *
     * @return true if the attribute has been modified
     */
    public boolean isDescriptionModified() { return isDescriptionModified; }
    /**
     * returns whether label has been modified
     *
     * @return true if the attribute has been modified
     */
    public boolean isLabelModified() { return isLabelModified; }
    /**
     * returns whether base output file name has been modified
     *
     * @return true if the attribute has been modified
     */
    public boolean isBaseOutputFileNameModified() { return isBaseOutputFileNameModified; }
    /**
     * returns whether output formats has been modified
     *
     * @return true if the attribute has been modified
     */
    public boolean isOutputFormatsModified() { return isOutputFormatsModified; }
    /**
     * returns whether the user name has been modified
     *
     * @return true if the attribute has been modified
     */
    public boolean isUsernameModified() { return isUsernameModified; }
    /**
     * returns whether output locale has been modified
     *
     * @return true if the attribute has been modified
     */
    public boolean isOutputLocaleModified() { return isOutputLocaleModified; }
    /**
     * returns whether runtime information has been modified
     *
     * @return true if the attribute has been modified
     */
    public boolean isRuntimeInformationModified() { return isRuntimeInformationModified; }

}
