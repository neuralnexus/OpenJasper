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
package com.jaspersoft.jasperserver.api.engine.scheduling.quartz;

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.engine.common.service.impl.ContentResourceURIResolver;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJob;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobMailNotification;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ContentResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.DataContainer;
import com.jaspersoft.jasperserver.api.metadata.common.domain.MemoryDataContainer;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.DataContainerResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.DataContainerStreamUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Ivan Chan (ichan@jaspersoft.com)
 * @version $Id: ReportExecutionJobMailNotificationImpl.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class ReportExecutionJobMailNotificationImpl implements ReportExecutionJobMailNotification {

    private static final Log log = LogFactory.getLog(ReportExecutionJobMailNotificationImpl.class);

    public void sendMailNotification(ReportExecutionJob job, ReportJob jobDetails, List reportOutputs) throws JobExecutionException {
    	ReportJobMailNotification mailNotification = jobDetails.getMailNotification();

		if (mailNotification != null) {
			try {
                // skip mail notification when job fails
                if (mailNotification.isSkipNotificationWhenJobFails() && (!job.exceptions.isEmpty())) {
                    return;
                }
                JavaMailSender mailSender = job.getMailSender();
	            String fromAddress = job.getFromAddress();
				MimeMessage message = mailSender.createMimeMessage();
				MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, job.getCharacterEncoding());
				messageHelper.setFrom(fromAddress);
				messageHelper.setSubject(mailNotification.getSubject());

				StringBuffer messageText = new StringBuffer();
				addMailRecipients(mailNotification, messageHelper);

                boolean isEmailBodyOccupied = false;

				if (reportOutputs != null && !reportOutputs.isEmpty()) {
                    byte resultSendType = jobDetails.getMailNotification().getResultSendType();

					if ((resultSendType == ReportJobMailNotification.RESULT_SEND_EMBED) || (resultSendType == ReportJobMailNotification.RESULT_SEND_EMBED_ZIP_ALL_OTHERS)) {
                        List attachmentReportList = new ArrayList();
                        for (Iterator it = reportOutputs.iterator(); it.hasNext();) {
							ReportOutput output = (ReportOutput) it.next();
                            if ((!isEmailBodyOccupied) && output.getFileType().equals(ContentResource.TYPE_HTML) && (job.exceptions.isEmpty())) {
                                // only embed html output
                                embedOutput(messageHelper, output);
                                isEmailBodyOccupied = true;
                            } else if (resultSendType == ReportJobMailNotification.RESULT_SEND_EMBED) {
                                // save the rest of the output as attachments
							    attachOutput(job, messageHelper, output, (resultSendType == ReportJobMailNotification.RESULT_SEND_ATTACHMENT));
                            } else {  // RESULT_SEND_EMBED_ZIP_ALL_OTHERS
                                attachmentReportList.add(output);
                            }
						}
                        if (attachmentReportList.size() > 0) {
                            // put the rest of attachments in 1 zip file
                            attachOutputs(job, messageHelper, attachmentReportList);

                        }
                    } else if (resultSendType == ReportJobMailNotification.RESULT_SEND_ATTACHMENT ||
                        resultSendType == ReportJobMailNotification.RESULT_SEND_ATTACHMENT_NOZIP) {
						for (Iterator it = reportOutputs.iterator(); it.hasNext();) {
							ReportOutput output = (ReportOutput) it.next();
							attachOutput(job, messageHelper, output, (resultSendType == ReportJobMailNotification.RESULT_SEND_ATTACHMENT));
						}
					} else if (resultSendType == ReportJobMailNotification.RESULT_SEND_ATTACHMENT_ZIP_ALL) {
                           // put all the attachments in 1 zip file
                            attachOutputs(job, messageHelper, reportOutputs);
                    } else {
						appendRepositoryLinks(job, messageText, reportOutputs);
					}
				}
                if (mailNotification.isIncludingStackTraceWhenJobFails()) {
                    if (!job.exceptions.isEmpty()) {
                        for (Iterator it = job.exceptions.iterator(); it.hasNext();) {
                            ExceptionInfo exception = (ExceptionInfo) it.next();

                            messageText.append("\n");
                            messageText.append(exception.getMessage());

                            attachException(messageHelper, exception);
                        }
                    }
                }
                String text = mailNotification.getMessageText();
                if (!job.exceptions.isEmpty()) {
                    if (mailNotification.getMessageTextWhenJobFails() != null) text = mailNotification.getMessageTextWhenJobFails();
                    else text = job.getMessage("report.scheduling.job.default.mail.notification.message.on.fail", null);
                }
				if (!isEmailBodyOccupied) messageHelper.setText(text + "\n" + messageText.toString());
				mailSender.send(message);
			} catch (MessagingException e) {
				log.error("Error while sending report job result mail", e);
				throw new JSExceptionWrapper(e);
			}
		}
	}

    protected void appendRepositoryLinks(ReportExecutionJob job, StringBuffer notificationMessage, List reportOutputs) {
		if (reportOutputs != null && !reportOutputs.isEmpty()) {
			String preamble = job.getMessage("report.scheduling.notification.repository.links.preamble", null);
			notificationMessage.append(preamble);
			String linkDescription = job.getRepositoryLinkDescription();
			ContentResourceURIResolver contentResourceURIResolver = job.getContentResourceURIResolver();
			for (Iterator it = reportOutputs.iterator(); it.hasNext();) {
				ReportOutput output = (ReportOutput) it.next();
				String resourcePath = output.getRepositoryPath();
				if (resourcePath != null) {
					String resourceURI = contentResourceURIResolver.resolveURI(resourcePath);
					String line = job.getMessage("report.scheduling.notification.repository.link.line",
                            new Object[]{linkDescription, resourceURI});
					notificationMessage.append(line);
				}
			}
		}
	}

    protected void embedOutput(MimeMessageHelper messageHelper, ReportOutput output) throws MessagingException, JobExecutionException {
        /***
         try {
         BufferedReader in = new BufferedReader(new FileReader("C:/Users/ichan/Desktop/employee.html"));
         StringBuffer strBuffer = new StringBuffer();
         String strLine;
         while ((strLine = in.readLine()) != null) {
         strBuffer = strBuffer.append(strLine + "\n");
         }
         content = strBuffer.toString();
         } catch (Exception ex) {
         ex.printStackTrace();
         }
         System.out.println("CONTENT = " + content);
         ****/

        // modify content to use inline images
        StringBuffer primaryPage = new StringBuffer(new String(output.getData().getData()));
        for (Iterator it = output.getChildren().iterator(); it.hasNext();) {
            ReportOutput child = (ReportOutput) it.next();
            String childName = child.getFilename();

            // NOTE:  add the ".dat" extension to all image resources
            // email client will automatically append ".dat" extension to all the files with no extension
            // should do it in JasperReport side
            if (output.getFileType().equals(ContentResource.TYPE_HTML)) {
                if (primaryPage == null) primaryPage = new StringBuffer(new String(output.getData().getData()));
                int fromIndex = 0;
                while ((fromIndex = primaryPage.indexOf("src=\""+ childName + "\"", fromIndex)) > 0) {
                    primaryPage.insert(fromIndex + 5, "cid:");
                }
            }
        }

        messageHelper.setText(primaryPage.toString(), true);
        // add inline images after setting the text in the main body
        for (Iterator it = output.getChildren().iterator(); it.hasNext();) {
            ReportOutput child = (ReportOutput) it.next();
            String childName = child.getFilename();
            DataContainerResource dataContainerResource = new DataContainerResource(child.getData());
            dataContainerResource.setFilename(childName);
            messageHelper.addInline(childName, dataContainerResource);

        }
    }

    protected void attachOutputs(ReportExecutionJob job, MimeMessageHelper messageHelper, List reportOutputs) throws MessagingException, JobExecutionException {
        String attachmentName = null;
        DataContainer attachmentData = job.createDataContainer();
        boolean close = true;
        ZipOutputStream zipOut = new ZipOutputStream(attachmentData.getOutputStream());
        try {
            for (Iterator it = reportOutputs.iterator(); it.hasNext();) {
                ReportOutput output = (ReportOutput) it.next();
                if (attachmentName == null) attachmentName = removeExtension(output.getFilename()) + ".zip";
                zipOutput(job, output, zipOut);
            }
            zipOut.finish();
            zipOut.flush();
            close = false;
            zipOut.close();
        } catch (IOException e) {
            throw new JSExceptionWrapper(e);
        } finally {
            if (close) {
                try {
                    zipOut.close();
                } catch (IOException e) {
                    log.error("Error closing stream", e);
                }
            }
        }
		try {
			attachmentName = MimeUtility.encodeWord(attachmentName, job.getCharacterEncoding(), null);
		} catch (UnsupportedEncodingException e) {
			throw new JSExceptionWrapper(e);
		}
		messageHelper.addAttachment(attachmentName, new DataContainerResource(attachmentData));
    }

    protected void zipOutput(ReportExecutionJob job, ReportOutput output, ZipOutputStream zipOut) throws IOException {
        zipOut.putNextEntry(new ZipEntry(output.getFilename()));
        DataContainerStreamUtil.pipeDataAndCloseInput(output.getData().getInputStream(), zipOut);
        zipOut.closeEntry();

        for (Iterator it = output.getChildren().iterator(); it.hasNext();) {
            ReportOutput child = (ReportOutput) it.next();
            String childName = getChildrenFolderName(job, output.getFilename()) + '/' + child.getFilename();
            zipOut.putNextEntry(new ZipEntry(childName));
            DataContainerStreamUtil.pipeDataAndCloseInput(child.getData().getInputStream(), zipOut);
            zipOut.closeEntry();
        }
    }


    protected void attachOutput(ReportExecutionJob job, MimeMessageHelper messageHelper, ReportOutput output, boolean useZipFormat) throws MessagingException, JobExecutionException {
		String attachmentName;
		DataContainer attachmentData;
		if (output.getChildren().isEmpty()) {
			attachmentName = output.getFilename();
			attachmentData = output.getData();
		} else if (useZipFormat) {  // use zip format
			attachmentData = job.createDataContainer();
			boolean close = true;
			ZipOutputStream zipOut = new ZipOutputStream(attachmentData.getOutputStream());
			try {
                zipOutput(job, output, zipOut);
   				zipOut.finish();
				zipOut.flush();
				close = false;
				zipOut.close();
			} catch (IOException e) {
				throw new JSExceptionWrapper(e);
			} finally {
				if (close) {
					try {
						zipOut.close();
					} catch (IOException e) {
						log.error("Error closing stream", e);
					}
				}
			}

			attachmentName = output.getFilename() + ".zip";
		} else {         // NO ZIP FORMAT
            attachmentName = output.getFilename();
            try {
			    attachmentName = MimeUtility.encodeWord(attachmentName, job.getCharacterEncoding(), null);
		    } catch (UnsupportedEncodingException e) {
			    throw new JSExceptionWrapper(e);
		    }
            StringBuffer primaryPage = null;
            for (Iterator it = output.getChildren().iterator(); it.hasNext();) {
					ReportOutput child = (ReportOutput) it.next();
                    String childName = child.getFilename();

                    // NOTE:  add the ".dat" extension to all image resources
                    // email client will automatically append ".dat" extension to all the files with no extension
                    // should do it in JasperReport side
                    if (output.getFileType().equals(ContentResource.TYPE_HTML)) {
                        if (primaryPage == null) primaryPage = new StringBuffer(new String(output.getData().getData()));
                        int fromIndex = 0;
                        while ((fromIndex = primaryPage.indexOf("src=\""+ childName + "\"", fromIndex)) > 0) {
                            primaryPage.insert(fromIndex + 5 + childName.length(), ".dat");
                        }
                        childName = childName + ".dat";
                    }

                    try {
			            childName = MimeUtility.encodeWord(childName, job.getCharacterEncoding(), null);
		            } catch (UnsupportedEncodingException e) {
			            throw new JSExceptionWrapper(e);
		            }
		            messageHelper.addAttachment(childName, new DataContainerResource(child.getData()));
		    }
            if (primaryPage == null) {
                messageHelper.addAttachment(attachmentName, new DataContainerResource(output.getData()));
            } else {
                messageHelper.addAttachment(attachmentName, new DataContainerResource(new MemoryDataContainer(primaryPage.toString().getBytes())));
            }
            return;
        }
		try {
			attachmentName = MimeUtility.encodeWord(attachmentName, job.getCharacterEncoding(), null);
		} catch (UnsupportedEncodingException e) {
			throw new JSExceptionWrapper(e);
		}
		messageHelper.addAttachment(attachmentName, new DataContainerResource(attachmentData));
	}


    protected static void attachException(MimeMessageHelper messageHelper, ExceptionInfo exceptionInfo) throws MessagingException {
		Throwable exception = exceptionInfo.getException();
		if (exception == null) {
			return;
		}

		ByteArrayOutputStream bufOut = new ByteArrayOutputStream();
		PrintStream printOut = new PrintStream(bufOut);
		exception.printStackTrace(printOut);
		printOut.flush();

		String attachmentName = "exception_" + System.identityHashCode(exception) + ".txt";
		messageHelper.addAttachment(attachmentName, new ByteArrayResource(bufOut.toByteArray()));
	}

    protected String getChildrenFolderName(ReportExecutionJob job, String resourceName) {
		return job.getRepository().getChildrenFolderName(resourceName);
	}

    protected void addMailRecipients(ReportJobMailNotification mailNotification, MimeMessageHelper messageHelper) throws MessagingException {
		List toAddresses = mailNotification.getToAddresses();
		if (toAddresses != null && !toAddresses.isEmpty()) {
			String[] addressArray = new String[toAddresses.size()];
			toAddresses.toArray(addressArray);
			messageHelper.setTo(addressArray);
		}

		List ccAddresses = mailNotification.getCcAddresses();
		if (ccAddresses != null && !ccAddresses.isEmpty()) {
			String[] addressArray = new String[ccAddresses.size()];
			ccAddresses.toArray(addressArray);
			messageHelper.setCc(addressArray);
        }
		List bccAddresses = mailNotification.getBccAddresses();
		if (bccAddresses != null && !bccAddresses.isEmpty()) {
			String[] addressArray = new String[bccAddresses.size()];
			bccAddresses.toArray(addressArray);
			messageHelper.setBcc(addressArray);
		}
	}

    private String removeExtension(String fileName) {
        int lastIndex = fileName.lastIndexOf(".");
        if (lastIndex > 0) return fileName.substring(0, lastIndex);
        else return fileName;
    }
}
