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

package com.jaspersoft.jasperserver.ws.axis2.scheduling;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.common.util.TimeZonesList;
import com.jaspersoft.jasperserver.api.engine.common.service.EngineService;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJob;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobCalendarTrigger;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobMailNotification;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobRepositoryDestination;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobRuntimeInformation;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobSimpleTrigger;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobSource;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobSummary;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobTrigger;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.jaxb.OutputFormatConversionHelper;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.jaxb.RelativeDateRangeWrapper;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.jaxb.ReportJobSendTypeXmlAdapter;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.jaxb.ReportJobStateXmlAdapter;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.jaxb.ReportJobTriggerCalendarDaysXmlAdapter;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.jaxb.ReportJobTriggerIntervalUnitXmlAdapter;
import com.jaspersoft.jasperserver.ws.scheduling.CalendarDaysType;
import com.jaspersoft.jasperserver.ws.scheduling.IntervalUnit;
import com.jaspersoft.jasperserver.ws.scheduling.Job;
import com.jaspersoft.jasperserver.ws.scheduling.JobCalendarTrigger;
import com.jaspersoft.jasperserver.ws.scheduling.JobMailNotification;
import com.jaspersoft.jasperserver.ws.scheduling.JobParameter;
import com.jaspersoft.jasperserver.ws.scheduling.JobRepositoryDestination;
import com.jaspersoft.jasperserver.ws.scheduling.JobSimpleTrigger;
import com.jaspersoft.jasperserver.ws.scheduling.JobSummary;
import com.jaspersoft.jasperserver.ws.scheduling.JobTrigger;
import com.jaspersoft.jasperserver.ws.scheduling.ResultSendType;
import com.jaspersoft.jasperserver.ws.scheduling.RuntimeJobState;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.types.date.RelativeDateRange;
import org.apache.commons.collections.set.ListOrderedSet;

import javax.xml.datatype.XMLGregorianCalendar;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ReportJobBeanTraslator.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class ReportJobBeanTraslator {
	private EngineService engine;
	private TimeZonesList timezones;

	public EngineService getEngine() {
		return engine;
	}

	public void setEngine(EngineService engine) {
		this.engine = engine;
	}

	public TimeZonesList getTimezones() {
		return timezones;
	}

	public void setTimezones(TimeZonesList timezones) {
		this.timezones = timezones;
	}

	protected String toStringConstant(String constant, byte value) {
		return toStringConstant(constant, new Byte(value));
	}

	public Job toServiceBean(ReportJob reportJob) {
		Job job = new Job();
		job.setId(reportJob.getId());
		job.setVersion(reportJob.getVersion());
		job.setUsername(reportJob.getUsername());
		job.setLabel(reportJob.getLabel());
		job.setDescription(reportJob.getDescription());
		copyTrigger(job, reportJob);
		job.setReportUnitURI(reportJob.getSource().getReportUnitURI());
		copyParameters(job, reportJob);
		job.setBaseOutputFilename(reportJob.getBaseOutputFilename());
		copyOutputFormats(job, reportJob);
		job.setOutputLocale(reportJob.getOutputLocale());
		copyRepositoryDestination(job, reportJob);
		copyMailNotification(job, reportJob);
		return job;
	}

	protected void copyTrigger(Job job, ReportJob reportJob) {
		ReportJobTrigger reportTrigger = reportJob.getTrigger();
		if (reportTrigger == null) {
			job.setSimpleTrigger(null);
			job.setCalendarTrigger(null);
		} else if (reportTrigger instanceof ReportJobSimpleTrigger) {
			copySimpleTrigger(job, (ReportJobSimpleTrigger) reportTrigger);
		} else if (reportTrigger instanceof ReportJobCalendarTrigger) {
			copyCalendarTrigger(job, (ReportJobCalendarTrigger) reportTrigger);
		} else {
			throw new JSException("report.scheduling.ws.unknown.trigger.type",
					new Object[]{reportTrigger.getClass().getName()});
		}
	}

	protected void copySimpleTrigger(Job job, ReportJobSimpleTrigger reportTrigger) {
		JobSimpleTrigger trigger = new JobSimpleTrigger();
		copyBaseTrigger(trigger, reportTrigger);
		trigger.setOccurrenceCount(reportTrigger.getOccurrenceCount());
		trigger.setRecurrenceInterval(reportTrigger.getRecurrenceInterval());
		trigger.setRecurrenceIntervalUnit(toIntervalUnit(reportTrigger.getRecurrenceIntervalUnit()));

		job.setSimpleTrigger(trigger);
		job.setCalendarTrigger(null);
	}

	protected void copyCalendarTrigger(Job job, ReportJobCalendarTrigger reportTrigger) {
		JobCalendarTrigger trigger = new JobCalendarTrigger();
		copyBaseTrigger(trigger, reportTrigger);
		trigger.setMinutes(reportTrigger.getMinutes());
		trigger.setHours(reportTrigger.getHours());
		trigger.setDaysType(toDaysType(reportTrigger.getDaysType()));
		trigger.setWeekDays(toIntArray(reportTrigger.getWeekDays()));
		trigger.setMonthDays(reportTrigger.getMonthDays());
		trigger.setMonths(toIntArray(reportTrigger.getMonths()));

		job.setSimpleTrigger(null);
		job.setCalendarTrigger(trigger);
	}

	protected int[] toIntArray(Set values) {
		int[] vals;
		if (values == null || values.isEmpty()) {
			vals = null;
		} else {
			vals = new int[values.size()];
			int idx = 0;
			for (Iterator it = values.iterator(); it.hasNext(); ++idx) {
				Number value = (Number) it.next();
				vals[idx] = value.intValue();
			}
		}
		return vals;
	}
	
	protected SortedSet toByteSet(int[] values) {
		SortedSet set;
		if (values == null || values.length == 0) {
			set = null;
		} else {
			set = new TreeSet();
			for (int i = 0; i < values.length; i++) {
				set.add(new Byte((byte) values[i]));
			}
		}
		return set;
	}
	
	protected void copyBaseTrigger(JobTrigger trigger, ReportJobTrigger reportTrigger) {
		trigger.setId(reportTrigger.getId());
		trigger.setVersion(reportTrigger.getVersion());
		trigger.setTimezone(reportTrigger.getTimezone());
		trigger.setStartDate(toCalendar(reportTrigger.getStartDate()));
		trigger.setEndDate(toCalendar(reportTrigger.getEndDate()));
	}

	protected void copyParameters(Job job, ReportJob reportJob) {
		Map parametersMap = reportJob.getSource().getParametersMap();
		JobParameter[] params;
		if (parametersMap == null || parametersMap.isEmpty()) {
			params = null;
		} else {
			params = new JobParameter[parametersMap.size()];
			int idx = 0;
			for (Iterator it = parametersMap.entrySet().iterator(); it.hasNext(); ++idx) {
				Map.Entry entry = (Map.Entry) it.next();
				String name = (String) entry.getKey();
				Object value = entry.getValue();
                if(value instanceof RelativeDateRange){
                    value = new RelativeDateRangeWrapper((RelativeDateRange)value);
                }
				params[idx] = toServiceParameter(name, value);
			}
		}
		job.setParameters(params);
	}

	protected JobParameter toServiceParameter(String name, Object value) {
		JobParameter jobParameter = new JobParameter();
		jobParameter.setName(name);
		jobParameter.setValue(value);
		return jobParameter;
	}

	protected void copyOutputFormats(Job job, ReportJob reportJob) {
        try {
            Set<String> strings = OutputFormatConversionHelper.toStrings(reportJob.getOutputFormats());
            job.setOutputFormats(strings.toArray(new String[strings.size()]));
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to convert output formats of input ReportJob object from Set<Byte> to Set<Strings>", e);
        }
    }

	protected void copyRepositoryDestination(Job job, ReportJob reportJob) {
		ReportJobRepositoryDestination reportDest = reportJob.getContentRepositoryDestination();
		JobRepositoryDestination dest = new JobRepositoryDestination();
		dest.setId(reportDest.getId());
		dest.setVersion(reportDest.getVersion());
		dest.setFolderURI(reportDest.getFolderURI());
		dest.setSequentialFilenames(reportDest.isSequentialFilenames());
		dest.setOverwriteFiles(reportDest.isOverwriteFiles());
		dest.setOutputDescription(reportDest.getOutputDescription());
		dest.setTimestampPattern(reportDest.getTimestampPattern());
		job.setRepositoryDestination(dest);
	}
	
	protected void copyMailNotification(Job job, ReportJob reportJob) {
		ReportJobMailNotification reportNotification = reportJob.getMailNotification();
		JobMailNotification notification;
		if (reportNotification == null) {
			notification = null;
		} else {
			notification = new JobMailNotification();
			notification.setId(reportNotification.getId());
			notification.setVersion(reportNotification.getVersion());
			
			List reportAddresses = reportNotification.getToAddresses();
			String[] addresses;
			if (reportAddresses == null || reportAddresses.isEmpty()) {
				addresses = null;
			} else {
				addresses = (String[]) reportAddresses.toArray(new String[reportAddresses.size()]);
			}
			notification.setToAddresses(addresses);
			
			notification.setSubject(reportNotification.getSubject());
			notification.setMessageText(reportNotification.getMessageText());
			notification.setResultSendType(toSendType(reportNotification.getResultSendType()));
			notification.setSkipEmptyReports(reportNotification.isSkipEmptyReports());
		}
		job.setMailNotification(notification);
	}

	public void copy(ReportJob reportJob, Job job) {
		reportJob.setId(job.getId());
		reportJob.setVersion(job.getVersion());
		reportJob.setUsername(job.getUsername());
		reportJob.setLabel(job.getLabel());
		reportJob.setDescription(job.getDescription());
		copyTrigger(reportJob, job);
		
		ReportJobSource reportJobSource = reportJob.getSource();
		if (reportJobSource == null) {
			reportJobSource = new ReportJobSource();
			reportJob.setSource(reportJobSource);
		}
		reportJobSource.setReportUnitURI(job.getReportUnitURI());
		copyParameters(reportJob, job);
		
		reportJob.setBaseOutputFilename(job.getBaseOutputFilename());
		copyOutputFormats(reportJob, job);
		reportJob.setOutputLocale(job.getOutputLocale());
		copyRepositoryDestination(reportJob, job);
		copyMailNotification(reportJob, job);
	}

	protected void copyParameters(ReportJob reportJob, Job job) {
		JobParameter[] jobParams = job.getParameters();
		Map reportParams;
		if (jobParams == null || jobParams.length == 0) {
			reportParams = null;
		} else {
            JasperReport jasperReport = getJobReport(job);
            reportParams = new HashMap();
            for (int i = 0; i < jobParams.length; i++) {
                JobParameter jobParameter = jobParams[i];
                String paramName = jobParameter.getName();
                final Object value = jobParameter.getValue();
                if (value instanceof RelativeDateRangeWrapper) {
                    reportParams.put(paramName, ((RelativeDateRangeWrapper) value).getRelativeDateRange());
                } else {
                    JRParameter reportParameter = findParameter(jasperReport, paramName);
                    if (reportParameter != null) {
                        reportParams.put(paramName, toParameterValue(reportParameter, value));
                    }
                }
            }
        }
        reportJob.getSource().setParametersMap(reportParams);
	}

	protected JasperReport getJobReport(Job job) {
		return getEngine().getMainJasperReport(null, job.getReportUnitURI());
	}

	protected JRParameter findParameter(JasperReport jasperReport, String paramName) {
		JRParameter[] params = jasperReport.getParameters();
		JRParameter param = null;
		for (int i = 0; i < params.length; i++) {
			if (paramName.equals(params[i].getName())) {
				param = params[i];
				break;
			}
		}
		return param;
	}

	protected Object toParameterValue(JRParameter parameter, Object value) {
		Class paramType = parameter.getValueClass();
		Object reportValue = value;
		if (value != null) {
			if (value.getClass().isArray()) {
				reportValue = toCollectionValue(paramType, value);
			} else if (!paramType.isInstance(value)) {
				if (value instanceof String && ((String) value).length() == 0) {
					reportValue = null;
				} else {
					reportValue = convertToParameter(paramType, value);
				}
			}
		}
		return reportValue;
	}

	protected Object toCollectionValue(Class parameterType, Object valueArray) {
		Object reportValue;
		int valueCount = Array.getLength(valueArray);
		if (parameterType.equals(Object.class)
				|| parameterType.equals(Collection.class)
				|| parameterType.equals(Set.class)) {
			Collection values = new ListOrderedSet();
			for (int i = 0; i < valueCount; ++i) {
				values.add(Array.get(valueArray, i));
			}
			reportValue = values;
		} else if (parameterType.equals(List.class)) {
			Collection values = new ArrayList(valueCount);
			for (int i = 0; i < valueCount; ++i) {
				values.add(Array.get(valueArray, i));
			}
			reportValue = values;
		} else if (parameterType.isArray()) {
			Class componentType = parameterType.getComponentType();
			if (componentType.equals(valueArray.getClass().getComponentType())) {
				reportValue = valueArray;
			} else {
				reportValue = Array.newInstance(componentType, valueCount);
				for (int i = 0; i < valueCount; ++i) {
					Array.set(reportValue, i, Array.get(valueArray, i));
				}
			}
		} else {
			throw new JSException(
					"report.scheduling.ws.collection.parameter.type.not.supported",
					new Object[] {parameterType.getName()});
		}
		return reportValue;
	}

	private Object convertToParameter(Class paramType, Object value) {
		Object parameterValue = null;

		try {
			if (String.class.equals(paramType)) {
				parameterValue = value.toString();
			} else if (Boolean.class.equals(paramType)) {
				if (value instanceof String) {
					parameterValue = Boolean.valueOf((String) value);
				} else if (value instanceof Number) {
					//0 = false
					parameterValue = Boolean.valueOf(((Number) value).intValue() != 0);
				}
			} else if (Date.class.equals(paramType)) {
				if (value instanceof Calendar) {
					parameterValue = ((Calendar) value).getTime();
				} else if (value instanceof Number) {
					// the number is interpreted as milliseconds
					parameterValue = new Date(((Number) value).longValue());
				} else if (value instanceof String) {
					// the string is interpreted as milliseconds
					parameterValue = new Date(Long.parseLong((String) value));
				}else if(value instanceof XMLGregorianCalendar){
                    parameterValue = ((XMLGregorianCalendar)value).toGregorianCalendar().getTime();
                }
			} else if (java.sql.Date.class.equals(paramType)) {
				if (value instanceof Calendar) {
					parameterValue = ((Calendar) value).getTime();
				} else if (value instanceof Number) {
					// the number is interpreted as milliseconds
					parameterValue = new java.sql.Date(((Number) value).longValue());
				} else if (value instanceof String) {
					// the string is interpreted as milliseconds
					parameterValue = new java.sql.Date(Long.parseLong((String) value));
				}else if(value instanceof XMLGregorianCalendar){
                    parameterValue = new java.sql.Date(((XMLGregorianCalendar)value).toGregorianCalendar().getTimeInMillis());
                }
			} else if (Timestamp.class.equals(paramType)) {
				if (value instanceof Date) {
					parameterValue = new Timestamp(((Date) value).getTime());
				}
				else if (value instanceof Calendar) {
					parameterValue = new Timestamp(((Calendar) value).getTimeInMillis());
				} else if (value instanceof Number) {
					// the number is interpreted as milliseconds
					parameterValue = new Timestamp(((Number) value).longValue());
				} else if (value instanceof String) {
					// the string is interpreted as milliseconds
					parameterValue = new Timestamp(Long.parseLong((String) value));
				}else if(value instanceof XMLGregorianCalendar){
                    parameterValue = new Timestamp(((XMLGregorianCalendar)value).toGregorianCalendar().getTimeInMillis());
                }
			} else if (Byte.class.equals(paramType)) {
				if (value instanceof Number) {
					parameterValue = new Byte(((Number) value).byteValue());
				} else if (value instanceof String) {
					parameterValue = new Byte((String) value);
				}
			} else if (Short.class.equals(paramType)) {
				if (value instanceof Number) {
					parameterValue = new Short(((Number) value).shortValue());
				} else if (value instanceof String) {
					parameterValue = new Short((String) value);
				}
			} else if (Integer.class.equals(paramType)) {
				if (value instanceof Number) {
					parameterValue = new Integer(((Number) value).intValue());
				} else if (value instanceof String) {
					parameterValue = new Integer((String) value);
				}
			} else if (Long.class.equals(paramType)) {
				if (value instanceof Number) {
					parameterValue = new Long(((Number) value).longValue());
				} else if (value instanceof String) {
					parameterValue = new Long((String) value);
				}
			} else if (Float.class.equals(paramType)) {
				if (value instanceof Number) {
					parameterValue = new Float(((Number) value).floatValue());
				} else if (value instanceof String) {
					parameterValue = new Float((String) value);
				}
			} else if (Double.class.equals(paramType)) {
				if (value instanceof Number) {
					parameterValue = new Double(((Number) value).doubleValue());
				} else if (value instanceof String) {
					parameterValue = new Double((String) value);
				}
			} else if (BigInteger.class.equals(paramType)) {
				if (value instanceof BigDecimal) {
					parameterValue = ((BigDecimal) value).toBigInteger();
				} else if (value instanceof Number) {
					parameterValue = BigDecimal.valueOf(((Number) value)
							.longValue());
				} else if (value instanceof String) {
					parameterValue = new BigDecimal((String) value);
				}
			} else if (BigDecimal.class.equals(paramType)) {
				if (value instanceof BigInteger) {
					parameterValue = new BigDecimal((BigInteger) value);
				} else if (value instanceof Number) {
					parameterValue = new BigDecimal(Double
							.toString(((Number) value).doubleValue()));
				} else if (value instanceof String) {
					parameterValue = new BigDecimal((String) value);
				}
			}
		} catch (NumberFormatException e) {
			// ignore, exception will be thrown bellow
		}

		if (parameterValue == null) {
			throw new JSException(
					"report.scheduling.ws.value.conversion.not.supported",
					new Object[] { value, value.getClass().getName(),
							paramType.getName() });
		}

		return parameterValue;
	}

	protected void copyTrigger(ReportJob reportJob, Job job) {
		if (job.getSimpleTrigger() == null) {
			if (job.getCalendarTrigger() == null) {
				reportJob.setTrigger(null);
			} else {
				copyCalendarTrigger(reportJob, job.getCalendarTrigger());
			}
		} else {
			if (job.getCalendarTrigger() == null) {
				copySimpleTrigger(reportJob, job.getSimpleTrigger());
			} else {
				//TODO
				throw new JSException("Only a single trigger can be set");
			}
		}
	}

	protected void copySimpleTrigger(ReportJob reportJob, JobSimpleTrigger trigger) {
		ReportJobSimpleTrigger reportTrigger = new ReportJobSimpleTrigger();
		copyBaseTrigger(reportTrigger, trigger);
		reportTrigger.setOccurrenceCount(trigger.getOccurrenceCount());
		reportTrigger.setRecurrenceInterval(trigger.getRecurrenceInterval());
		reportTrigger.setRecurrenceIntervalUnit(toIntervalUnit(trigger.getRecurrenceIntervalUnit()));

		reportJob.setTrigger(reportTrigger);
	}

	protected void copyCalendarTrigger(ReportJob reportJob, JobCalendarTrigger trigger) {
		ReportJobCalendarTrigger reportTrigger = new ReportJobCalendarTrigger();
		copyBaseTrigger(reportTrigger, trigger);
		reportTrigger.setMinutes(trigger.getMinutes());
		reportTrigger.setHours(trigger.getHours());
		reportTrigger.setDaysType(toDaysType(trigger.getDaysType()));
		reportTrigger.setWeekDays(toByteSet(trigger.getWeekDays()));
		reportTrigger.setMonthDays(trigger.getMonthDays());
		reportTrigger.setMonths(toByteSet(trigger.getMonths()));

		reportJob.setTrigger(reportTrigger);
	}
	
	protected void copyBaseTrigger(ReportJobTrigger reportTrigger, JobTrigger trigger) {
		reportTrigger.setId(trigger.getId());
		reportTrigger.setVersion(trigger.getVersion());
		
		String timezone = trigger.getTimezone();
		if (timezone == null || timezone.length() == 0) {
			timezone = timezones.getDefaultTimeZoneID();
		}
		reportTrigger.setTimezone(timezone);
		
		reportTrigger.setStartDate(toDate(trigger.getStartDate()));
		reportTrigger.setStartType(trigger.getStartDate() == null ? ReportJobTrigger.START_TYPE_NOW : ReportJobTrigger.START_TYPE_SCHEDULE);
		reportTrigger.setEndDate(toDate(trigger.getEndDate()));
	}

	protected void copyOutputFormats(ReportJob reportJob, Job job) {
        Set<String> strings = new HashSet<String>();
        strings.addAll(Arrays.asList(job.getOutputFormats()));
        try {
            reportJob.setOutputFormats(OutputFormatConversionHelper.toBytes(strings));
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to convert output formats of input Job object from Set<Strings> to Set<Byte>", e);
        }
    }

	protected void copyRepositoryDestination(ReportJob reportJob, Job job) {
		JobRepositoryDestination dest = job.getRepositoryDestination();
		ReportJobRepositoryDestination reportDest;
		if (dest == null) {
			reportDest = null;
		} else {
			reportDest = new ReportJobRepositoryDestination();
			reportDest.setId(dest.getId());
			reportDest.setVersion(dest.getVersion());
			reportDest.setFolderURI(dest.getFolderURI());
			reportDest.setSequentialFilenames(dest.isSequentialFilenames());
			reportDest.setOverwriteFiles(dest.isOverwriteFiles());
			reportDest.setOutputDescription(dest.getOutputDescription());
			reportDest.setTimestampPattern(dest.getTimestampPattern());
		}
		reportJob.setContentRepositoryDestination(reportDest);
	}
	
	protected void copyMailNotification(ReportJob reportJob, Job job) {
		JobMailNotification notification = job.getMailNotification();
		ReportJobMailNotification reportNotification;
		if (notification == null) {
			reportNotification = null;
		} else {
			reportNotification = new ReportJobMailNotification();
			
			reportNotification.setId(notification.getId());
			reportNotification.setVersion(notification.getVersion());
			
			String[] addresses = notification.getToAddresses();
			List reportAdresses = addresses == null ? null : Arrays.asList(addresses);
			reportNotification.setToAddresses(reportAdresses);
			
			reportNotification.setSubject(notification.getSubject());
			reportNotification.setMessageText(notification.getMessageText());
			reportNotification.setResultSendType(toSendType(notification.getResultSendType()));
			reportNotification.setSkipEmptyReports(notification.isSkipEmptyReports());
		}
		reportJob.setMailNotification(reportNotification);
	}
	
	public JobSummary toServiceSummary(ReportJobSummary reportJob) {
		JobSummary summary = new JobSummary();
		summary.setId(reportJob.getId());
		summary.setVersion(reportJob.getVersion());
		summary.setReportUnitURI(reportJob.getReportUnitURI());
		summary.setLabel(reportJob.getLabel());
		summary.setUsername(reportJob.getUsername());
		ReportJobRuntimeInformation runtimeInfo = reportJob.getRuntimeInformation();
		if (runtimeInfo != null) {
			summary.setState(toJobState(runtimeInfo.getState()));
			summary.setPreviousFireTime(toCalendar(runtimeInfo.getPreviousFireTime()));
			summary.setNextFireTime(toCalendar(runtimeInfo.getNextFireTime()));
		}
		return summary;
	}
	
	protected Date toDate(Calendar calendar) {
		Date date;
		if (calendar == null) {
			date = null;
		} else {
			date = calendar.getTime();
		}
		return date;
	}
	
	protected Calendar toCalendar(Date date) {
		Calendar calendar;
		if (date == null) {
			calendar = null;
		} else {
			calendar = Calendar.getInstance();
			calendar.setTime(date);
		}
		return calendar;
	}

    protected IntervalUnit toIntervalUnit(Byte intervalUnit) {
        IntervalUnit result = null;
        if (intervalUnit != null)
            try {
                result = IntervalUnit.fromValue(new ReportJobTriggerIntervalUnitXmlAdapter().marshal(intervalUnit));
            } catch (Exception e) {
                //do nothing, let be null
            }
        return result;
    }

    protected Byte toIntervalUnit(IntervalUnit intervalUnit) {
        Byte result = null;
        if (intervalUnit != null)
            try {
                result = new ReportJobTriggerIntervalUnitXmlAdapter().unmarshal(intervalUnit.getValue());
            } catch (Exception e) {
                // do nothing, let be null
            }
        return result;
    }

	protected CalendarDaysType toDaysType(byte daysType) {
        try {
            return CalendarDaysType.fromValue(new ReportJobTriggerCalendarDaysXmlAdapter().marshal(daysType));
        } catch (Exception e) {
            throw new IllegalArgumentException("Days type '" + daysType + "' isn't supported", e);
        }
    }

	protected byte toDaysType(CalendarDaysType daysType) {
        byte result = ReportJobCalendarTrigger.DAYS_TYPE_ALL;
        try {
            result = new ReportJobTriggerCalendarDaysXmlAdapter().unmarshal(daysType.getValue());
        } catch (Exception e) {
            // do nothing. Default value should be used
        }
        return result;
    }

	protected ResultSendType toSendType(byte sendType) {
        try {
            return ResultSendType.fromValue(new ReportJobSendTypeXmlAdapter().marshal(sendType));
        } catch (Exception e) {
            throw new IllegalArgumentException("Send type '" + sendType + "' isn't supported", e);
        }
    }

	protected byte toSendType(ResultSendType sendType) {
        byte result = ReportJobMailNotification.RESULT_SEND;
        if(sendType != null)
            try {
                result = new ReportJobSendTypeXmlAdapter().unmarshal(sendType.getValue());
            } catch (Exception e) {
                //do nothing, use default value
            }
        return result;
	}

	protected RuntimeJobState toJobState(byte state) {
        String stringValue = null;
        try {
            stringValue = new ReportJobStateXmlAdapter().marshal(state);
        } catch (Exception e) {
            throw new IllegalArgumentException("State value '" + state + "' isn't supported", e);
        }
        return RuntimeJobState.fromValue(stringValue);
	}
}
