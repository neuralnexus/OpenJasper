/*
 * Copyright © 2005 - 2018 TIBCO Software Inc.
 * http://www.jaspersoft.com.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.jaspersoft.jasperserver.api.engine.scheduling.domain;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.JasperServerAPI;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.reportjobmodel.ReportJobAlertModel;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.reportjobmodel.ReportJobCalendarTriggerModel;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.reportjobmodel.ReportJobMailNotificationModel;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.reportjobmodel.ReportJobModel;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.reportjobmodel.ReportJobRepositoryDestinationModel;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.reportjobmodel.ReportJobSimpleTriggerModel;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.reportjobmodel.ReportJobSourceModel;
import com.jaspersoft.jasperserver.api.engine.scheduling.service.ReportSchedulingService;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ContentResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import javax.xml.bind.annotation.XmlTransient;
import net.sf.jasperreports.engine.JRParameter;
import org.apache.commons.lang.StringUtils;


/**
 * Definition of a report execution job.
 *
 * <p>
 * A report job definition specifies wich report to execute and when,
 * what output to generate and where to send the output.
 * </p>
 *
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 * @since 1.0
 * @see ReportSchedulingService#scheduleJob(com.jaspersoft.jasperserver.api.common.domain.ExecutionContext, ReportJob)
 */
@JasperServerAPI
public class ReportJob implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * The version number which should be used for new (unsaved) report jobs.
	 *
	 * @see #getVersion()
	 */
	public static final int VERSION_NEW = -1;

	/**
	 * PDF output constant.
	 *
	 * @see #getOutputFormats()
	 */
	public static final byte OUTPUT_FORMAT_PDF = 1;//FIXME consider removing these constants

	/**
	 * HTML output constant.
	 *
	 * @see #getOutputFormats()
	 */
	public static final byte OUTPUT_FORMAT_HTML = 2;

	/**
	 * XLS output constant.
	 *
	 * @see #getOutputFormats()
	 */
	public static final byte OUTPUT_FORMAT_XLS = 3;

	/**
	 * RTF output constant.
	 *
	 * @see #getOutputFormats()
	 */
	public static final byte OUTPUT_FORMAT_RTF = 4;

	/**
	 * CSV output constant.
	 *
	 * @see #getOutputFormats()
	 * @since 2.0.0
	 */
	public static final byte OUTPUT_FORMAT_CSV = 5;

	/**
	 * ODT output constant.
	 *
	 * @see #getOutputFormats()
	 * @since 3.7.0
	 */
	public static final byte OUTPUT_FORMAT_ODT = 6;

	/**
	 * TXT output constant.
	 *
	 * @see #getOutputFormats()
	 * @since 3.7.0
	 */
	public static final byte OUTPUT_FORMAT_TXT = 7;

	/**
	 * DOCX output constant.
	 *
	 * @see #getOutputFormats()
	 * @since 3.7.0
	 */
	public static final byte OUTPUT_FORMAT_DOCX = 8;

	/**
	 * ODS output constant.
	 *
	 * @see #getOutputFormats()
	 * @since 3.7.0
	 */
	public static final byte OUTPUT_FORMAT_ODS = 9;

	/**
	 * XLSX output constant.
	 *
	 * @see #getOutputFormats()
	 * @since 3.7.0
	 */
	public static final byte OUTPUT_FORMAT_XLSX = 10;

	/**
	 * Non paginated XLS output constant.
	 *
	 * @see #getOutputFormats()
	 */
	public static final byte OUTPUT_FORMAT_XLS_NOPAG = 11;

	/**
	 * Non paginated XLSX output constant.
	 *
	 * @see #getOutputFormats()
	 */
	public static final byte OUTPUT_FORMAT_XLSX_NOPAG = 12;

	/**
	 * Non paginated PPTX output constant.
	 *
	 * @see #getOutputFormats()
	 */
	public static final byte OUTPUT_FORMAT_PPTX = 14;

	/**
	 * Non paginated PPTX output constant.
	 *
	 * @see #getOutputFormats()
	 */
	public static final byte OUTPUT_FORMAT_JSON = 15;


	/**
	 * Non paginated PNG output constant.
	 *
	 * @see #getOutputFormats()
	 */

	public static final byte OUTPUT_FORMAT_PNG= 16;

	/**
	 * Report unit with data snapshot output format.
	 *
	 * @see #getOutputFormats()
	 */
	public static final byte OUTPUT_FORMAT_DATA_SNAPSHOT = 100;

	private long id;
	private int version = VERSION_NEW;
	private String username;
	private String label;
	private String description;
    private Timestamp creationDate;
	private ReportJobTrigger trigger;
	private ReportJobSource source;
	private ResourceReference scheduledResource;
	private String baseOutputFilename;
	private Set<Byte> outputFormats;
	private String outputLocale;
	private ReportJobRepositoryDestination contentRepositoryDestination;
	private ReportJobMailNotification mailNotification;
    private ReportJobAlert alert;

	/**
	 * Creates a new empty report job.
	 */
	public ReportJob() {
		outputFormats = new HashSet<Byte>();
        creationDate = new Timestamp(GregorianCalendar.getInstance().getTimeInMillis());
	}

	/**
	 * Returns the job ID.
	 *
	 * <p>
	 * Unsaved jobs will not an ID, as the ID is generated at save time.
	 * </p>
	 *
	 * @return the job ID, or <code>0</code> for unsaved jobs
	 */
	public long getId() {
		return id;
	}

	/**
	 * Sets the job ID.
	 *
	 * <p>
	 * A job ID needs to be set only when preparing a report job for an update
	 * operation.  The update operation will use the ID to locate the existing
	 * job and update its details.
	 * </p>
	 *
	 * @param id the job ID
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Returns the persistent version of this report job object.
	 *
	 * <p>
	 * When a report job is retrieved from the scheduling service, its version
	 * is automatically set.
	 * The version needs to be preserved if the report job details are going to
	 * be updated.
	 * </p>
	 *
	 * @return the version of this report job
	 * @see #VERSION_NEW
	 * @see #setVersion(int)
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * Sets the persistent version of this report job.
	 *
	 * <p>
	 * The version is set to <code>VERSION_NEW</code> by default when a new
	 * object is created.  It doesn't need to change when the report job is
	 * initially saved in the repository.
	 * </p>
	 *
	 * <p>
	 * When updating a report job, the version should preserve the same value
	 * as the one contained in the object returned from the repository.
	 * When the job details are updated, the service will verify that the version
	 * in the job object passed as argument is the same as the latest version
	 * of the persisted job.
	 * If the versions do not match, the update will fail.
	 * </p>
	 *
	 * @param version the report job persistent version
	 */
	public void setVersion(int version) {
		this.version = version;
	}

	/**
	 * Returns the report execution source, consisting of a report and a set
	 * of input values for the report.
	 *
	 *
	 * @return the report execution source
	 * @see #setSource(ReportJobSource)
	 */
	public ReportJobSource getSource() {
		return source;
	}

	/**
	 * Sets the report execution source for this job.
	 *
	 * <p>
	 * A report job needs to be set a source before being saved.
	 * </p>
	 *
	 * @param source the report job source
	 */
	public void setSource(ReportJobSource source) {
		this.source = source;
	}

    /**
     * Returns the job trigger, which specifies when the report should be
     * executed.
     *
     * @return the job trigger
     * @see #setTrigger(ReportJobTrigger)
     */
    public ReportJobTrigger getTrigger() {
        return trigger;
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
	 * @see ReportJobSimpleTrigger
	 * @see ReportJobCalendarTrigger
	 */
	public void setTrigger(ReportJobTrigger trigger) {
		this.trigger = trigger;
	}

	/**
	 * Returns the information related to the email notification which is to be
	 * sent at job execution time.
	 *
	 * @return email notification information associated with the job, or
	 * <code>null</code> if the job is not to send any notification
	 * @see #setMailNotification(ReportJobMailNotification)
	 */
	public ReportJobMailNotification getMailNotification() {
		return mailNotification;
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
	public void setMailNotification(ReportJobMailNotification mailNotification) {
        if ((contentRepositoryDestination != null && !contentRepositoryDestination.isSaveToRepository()) &&
            (mailNotification != null && mailNotification.getResultSendTypeCode() == mailNotification.RESULT_SEND)) {
            throw new JSException("Cannot send mail notifications contain links of the job output in repository when saving to repository function is disabled.");
        }
		this.mailNotification = mailNotification;
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
     * @see ContentResource
     */
    public ReportJobRepositoryDestination getContentRepositoryDestination() {
        return contentRepositoryDestination;
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
	public void setContentRepositoryDestination(
			ReportJobRepositoryDestination contentRepositoryDestination) {
        if ((contentRepositoryDestination != null && !contentRepositoryDestination.isSaveToRepository()) &&
            (mailNotification != null && mailNotification.getResultSendTypeCode() == mailNotification.RESULT_SEND)) {
            throw new JSException("Cannot send mail notifications contain links of the job output in repository when saving to repository function is disabled.");
        }
		this.contentRepositoryDestination = contentRepositoryDestination;
	}

	/**
	 * Returns the information related to the email alert which is to be
	 * sent at job execution time.
	 *
	 * @return email alert information associated with the job, or
	 * <code>null</code> if the job is not to send any alert
	 * @see #setAlert(ReportJobAlert)
	 */
    public ReportJobAlert getAlert() {
        return alert;
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
    public void setAlert(ReportJobAlert alert) {
        this.alert = alert;
    }

	/**
	 * Returns a long description of the report job.
	 *
	 * @return the job description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets a description for the job
	 *
	 * @param description the job description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Returns a creation date of the job.
	 *
	 * @return the job creation date
     * @since 4.7
     */
    public Timestamp getCreationDate() {
        return creationDate;
    }

	/**
	 * Sets creation date for the job
	 *
	 * @param creationDate the job creation date
     * @since 4.7
	 */
	public void setCreationDate(Timestamp creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * Returns a short description of the report job.
	 *
	 * @return the job label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Sets a mandatory short description for the report job.
	 *
	 * @param label the job label
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Returns the base name that will be used for job output file nanes.
	 *
	 * <p>
	 * The base output file name gets suffixed by a timestamp if the job is
	 * configured to generate sequential file names, and by an extension that
	 * depends on the output type.
	 * </p>
	 *
	 * @return the base output file name
	 * @see ReportJobRepositoryDestination#isSequentialFilenames()
	 */
	public String getBaseOutputFilename() {
		return baseOutputFilename;
	}

	/**
	 * Sets the base filename to be used for the report job output files.
	 *
	 * @param baseOutputFilename the job output base filename
	 * @see #getBaseOutputFilename()
	 */
	public void setBaseOutputFilename(String baseOutputFilename) {
		this.baseOutputFilename = baseOutputFilename;
	}

	/**
	 * Returns the set of output formats what will be generated by the job.
	 *
	 * <p>
	 * The output formats are returned as <code>java.lang.Byte</code> keys.
	 * A output format key can be one of the <code>OUTPUT_FORMAT_*</code>,
	 * or a different format for which support has been added as a customization.
	 * </p>
	 *
	 * @return a set of output formats as <code>java.lang.Byte</code> keys
     * @deprecated use #getOutputFormatsSet() with parametrized Set as output
     */
    public Set getOutputFormats() {
        return getOutputFormatsSet();
    }

    /**
     * Returns the set of output formats what will be generated by the job.
     *
     * <p>
     * The output formats are returned as <code>java.lang.Byte</code> keys.
     * A output format key can be one of the <code>OUTPUT_FORMAT_*</code>,
     * or a different format for which support has been added as a customization.
     * </p>
     *
     * @return a set of output formats as <code>java.lang.Byte</code> keys
     */
    public Set<Byte> getOutputFormatsSet() {
        return outputFormats;
    }

	/**
	 * Sets the list of output formats that will be generated by the job.
	 *
	 * @param outputFormats the set of output formats as
	 * <code>java.lang.Byte</code> keys
	 * @see #getOutputFormats()
     * @deprecated use #setOutputFormatsSet(Set<Byte> outputFormats) with parametrized Set as input
	 */
	public void setOutputFormats(Set outputFormats) {
		setOutputFormatsSet(outputFormats);
	}

	/**
	 * Sets the list of output formats that will be generated by the job.
	 *
	 * @param outputFormats the set of output formats as
	 * <code>java.lang.Byte</code> keys
	 * @see #getOutputFormatsSet()
	 */
	public void setOutputFormatsSet(Set<Byte> outputFormats) {
		this.outputFormats = outputFormats;
	}

	/**
	 * Adds an output format to the job.
	 *
	 * @param outputFormat the output format key
	 * @return <code>true</code> if the ouput was not already present in the job
	 * @see #setOutputFormats(Set)
	 */
	public boolean addOutputFormat(byte outputFormat) {
		return outputFormats.add(outputFormat);
	}

	/**
	 * Removes an output format from the job.
	 *
	 * @param outputFormat the key of the output format to remoce
	 * @return <code>true</code> if the output format was present in the job
	 */
	public boolean removeOutputFormat(byte outputFormat) {
		return outputFormats.remove(new Byte(outputFormat));
	}

	/**
	 * Returns the owner of this job.
	 *
	 * <p>
	 * The job owner is automatically determined from the context when the job
	 * is scheduled.
	 * For new/unsaved jobs, the owner will be <code>null</code>.
	 * </p>
	 *
	 * @return the username of the job owner
	 */
	public String getUsername() {
		return username;
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
		this.username = username;
	}

	/**
	 * Returns the code of the locale which will be used to execute the report.
	 *
	 * <p>
	 * </p>
	 *
	 * @return the report job locale, or <code>null</code> if none set
	 * @see #setOutputLocale(String)
	 */
	public String getOutputLocale() {
		return outputLocale;
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
		this.outputLocale = outputLocale;
	}

	/**
     * Looks for {@link  net.sf.jasperreports.engine.JRParameter#REPORT_TIME_ZONE REPORT_TIME_ZONE}
     * and returns ID of the {@link java.util.TimeZone Timezone}.
     * If Timezone parameters is not present, or it is null, return null.
     *
     * @return String Timezone ID
     */
    public String getOutputTimeZone() {
        ReportJobSource jobSource = this.getSource();
        if (jobSource != null) {

            Map<String, Object> parameters = jobSource.getParameters();
            if (parameters != null) {
                TimeZone tz = (TimeZone) parameters.get(JRParameter.REPORT_TIME_ZONE);
                return tz != null ? tz.getID() : null;
            }
        }

        return null;
    }

    /**
     * Adds {@link java.util.TimeZone Timezone} to the parameters map
     * under {@link  net.sf.jasperreports.engine.JRParameter#REPORT_TIME_ZONE REPORT_TIME_ZONE} key.
     * If parameter timeZoneID is null or empty string, parameters is not added.
     * If parameter map is null, creates new parameter map and add Timezone.
     *
     * @param timeZoneID Timezone ID
     */
    public void setOutputTimeZone(String timeZoneID) {
        ReportJobSource jobSource = this.getSource();
        if (jobSource != null && !StringUtils.isEmpty(timeZoneID)) {

            Map<String, Object> parameters = jobSource.getParameters();
            if (parameters == null) {
                parameters = new HashMap<String, Object>();
                jobSource.setParameters(parameters);
            }

            parameters.put(JRParameter.REPORT_TIME_ZONE, TimeZone.getTimeZone(timeZoneID));
        }
    }

    /**
     * Adds specified values to the {@link com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobSource ReportJobSource}
     * parameter map, values get overridden. If ReportJobSource parameter map is null set specified map into ReportJobSource.
     *
     * @param values Map&lt;String, Object&gt; to be added.
     */
    public void setReportParameters(Map<String, Object> values) {
        ReportJobSource jobSource = this.getSource();
        if (jobSource != null) {
            Map<String, Object> parameters = jobSource.getParameters();
            if (parameters != null) {
                parameters.putAll(values);
            } else {
                jobSource.setParameters(values);
            }
        }
    }


	/**
   * Convenience constructor that returns a distinct copy of the input ReportJob.
   * All of the copy's Object members are themselves copies as well.
   *
   * We're deliberately avoiding using clone()
   *
   * @param job
   * @return
   */
	public ReportJob(ReportJob job) {

		// getId() is deliberately omitted since this will be created upon
		// Persisting the new ReportJob

		// copying the version seems to be causing a problem
		// this.setVersion(job.getVersion());

		// this clone is first coming to life, so it cannot have been previously
		// saved
		// therefore we set the version to VERSION_NEW
		this.setVersion(VERSION_NEW);
		this.setSource(new ReportJobSource(job.getSource()));
		ReportJobTrigger trigger = job.getTrigger();
		if (trigger != null) {
			if (trigger instanceof ReportJobCalendarTrigger) {
				this.setTrigger(new ReportJobCalendarTrigger((ReportJobCalendarTrigger) trigger));
			}
			else if (trigger instanceof ReportJobSimpleTrigger) {
				this.setTrigger(new ReportJobSimpleTrigger((ReportJobSimpleTrigger) trigger));
			}
			else {
				throw new JSException("ERROR !  Unhandled Trigger type '" + trigger.getClass().getName() + "'");
			}
		}

		// note that mail notification is optional...
		if (job.getMailNotification() != null) {
			this.setMailNotification(new ReportJobMailNotification(job.getMailNotification()));
		} else {
			this.setMailNotification(null);
		}
		this.setContentRepositoryDestination(new ReportJobRepositoryDestination(job.getContentRepositoryDestination()));
		this.setDescription(job.getDescription());
		this.setCreationDate(job.getCreationDate());
		this.setLabel(job.getLabel());
		this.setBaseOutputFilename(job.getBaseOutputFilename());
		this.setOutputFormats(new HashSet(job.getOutputFormats()));
		this.setOutputFormatsSet(new HashSet(job.getOutputFormatsSet()));
		this.setUsername(job.getUsername());
		this.setOutputLocale(job.getOutputLocale());
	}

	@XmlTransient
	public ResourceReference getScheduledResource() {
		return scheduledResource;
	}

    public void setScheduledResource(ResourceReference scheduledResource) {
        this.scheduledResource = scheduledResource;
    }


    public ReportJob applyModel(ReportJobModel jobModel, boolean replaceTrigger) {
        if (jobModel.isUsernameModified()) setUsername(jobModel.getUsername());
        if (jobModel.isLabelModified()) setLabel(jobModel.getLabel());
        if (jobModel.isDescriptionModified()) setDescription(jobModel.getDescription());
        if (jobModel.isCreationDateModified() && jobModel.getCreationDate() != null)
            setCreationDate(jobModel.getCreationDate());
        if (jobModel.isTriggerModified() && jobModel.getTriggerModel() != null)
            copyTrigger(jobModel.getTriggerModel(), replaceTrigger);
        if (jobModel.isSourceModified()) copySource(jobModel.getSourceModel());
        if (jobModel.isBaseOutputFileNameModified()) setBaseOutputFilename(jobModel.getBaseOutputFilename());
        if (jobModel.isOutputFormatsModified()) {
            if (jobModel.getOutputFormatsSet() == null) setOutputFormats(new HashSet());
            else {
                HashSet<Byte> copiedFormats = new HashSet<Byte>(jobModel.getOutputFormatsSet());
                for (Byte outputFormat : outputFormats) {
                    copiedFormats.add(outputFormat);
                }
                setOutputFormatsSet(copiedFormats);
            }
        }
        if (jobModel.isOutputLocaleModified()) setOutputLocale(jobModel.getOutputLocale());
        if (jobModel.isContentRepositoryDestinationModified())
            copyContentRepositoryDestination(jobModel.getContentRepositoryDestinationModel());
        if (jobModel.isMailNotificationModified()) copyMailNotification(jobModel.getMailNotificationModel());
        if (jobModel.isAlertModified()) copyAlert(jobModel.getAlertModel());

        return this;
    }

    protected void copySource(ReportJobSourceModel source) {
        if (getSource() == null) {
            setSource(new ReportJobSource());
            if (source == null) return;
        }
        if (source != null) getSource().copyFromModel(source);
        else setSource(new ReportJobSource());
    }

    protected void copyTrigger(ReportJobTrigger jobTriggerModel, boolean replaceTrigger) {
        ReportJobTrigger originalTrigger = getTrigger();
        Class<? extends ReportJobTrigger> jobTriggerModelClass = jobTriggerModel.getClass();
        if (originalTrigger != null && !originalTrigger.supports(jobTriggerModelClass)) {
            if (!replaceTrigger) return;
            originalTrigger = null;
        }
        if (originalTrigger == null) {
            if (jobTriggerModel instanceof ReportJobSimpleTriggerModel) {
                originalTrigger = new ReportJobSimpleTrigger();
            } else if (jobTriggerModel instanceof ReportJobCalendarTriggerModel) {
                originalTrigger = new ReportJobCalendarTrigger();
            } else {
                String quotedJobTrigger = "\"" + jobTriggerModel.getClass().getName() + "\"";
                throw new JSException("jsexception.job.unknown.trigger.type", new Object[]{quotedJobTrigger});
            }
            setTrigger(originalTrigger);
        }
        // save existing information common for all types of triggers
        originalTrigger.copyFromModel(jobTriggerModel);
    }

    protected void copyContentRepositoryDestination(ReportJobRepositoryDestinationModel repositoryDestinationModel) {
        if (getContentRepositoryDestination() == null) {
            setContentRepositoryDestination(new ReportJobRepositoryDestination());
            if (repositoryDestinationModel == null) return;
        }
        if (repositoryDestinationModel != null)
            getContentRepositoryDestination().copyFromModel(repositoryDestinationModel);
        else setContentRepositoryDestination(new ReportJobRepositoryDestination());
    }

    protected void copyAlert(ReportJobAlertModel jobAlertModel) {
        ReportJobAlert alert = getAlert();
        if (alert == null) {
            alert = new ReportJobAlert();
            setAlert(alert);
        } else if (jobAlertModel == null) {
            return;
        }
        if (jobAlertModel != null) alert.copyFrom(jobAlertModel);
        else setAlert(new ReportJobAlert());
    }

    protected void copyMailNotification(ReportJobMailNotificationModel jobMail) {
        ReportJobMailNotification mailNotification = getMailNotification();
        if (mailNotification == null) {
            mailNotification = new ReportJobMailNotification();
            setMailNotification(mailNotification);
            if (jobMail == null) return;
        } else if (jobMail == null) {
            return;
        }
        if (jobMail != null) mailNotification.copyFromModel(jobMail);
        else setMailNotification(new ReportJobMailNotification());
    }

}
