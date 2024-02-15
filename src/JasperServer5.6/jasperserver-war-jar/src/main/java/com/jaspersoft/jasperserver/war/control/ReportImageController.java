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

import java.awt.Dimension;
import java.util.Arrays;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRImage;
import net.sf.jasperreports.engine.JRImageRenderer;
import net.sf.jasperreports.engine.JRPrintImage;
import net.sf.jasperreports.engine.JRRenderable;
import net.sf.jasperreports.engine.JRWrappingSvgRenderer;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.util.JRTypeSniffer;
import net.sf.jasperreports.engine.type.OnErrorTypeEnum;
import net.sf.jasperreports.engine.type.ModeEnum;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.AbstractView;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl.ReportUnitResult;
import com.jaspersoft.jasperserver.war.util.SessionObjectSerieAccessor;

/**
 * Controller for JasperServer report images.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ReportImageController.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class ReportImageController  implements Controller
{

	private SessionObjectSerieAccessor jasperPrintAccessor;
	private String jasperPrintNameParameter;
	private String imageNameParameter;

	protected static class ImageView extends AbstractView {

		private final String imageMimeType;
		private final byte[] imageData;

		public ImageView(final String imageMimeType, final byte[] imageData) {
			this.imageMimeType = imageMimeType;
			this.imageData = imageData;
		}

		protected void renderMergedOutputModel(Map model, HttpServletRequest request, HttpServletResponse response) throws Exception {
			if (imageData != null && imageData.length > 0) {
				if (imageMimeType != null) {
					response.setHeader("Content-Type", imageMimeType);
				}
				response.setContentLength(imageData.length);
				ServletOutputStream ouputStream = response.getOutputStream();
				ouputStream.write(imageData, 0, imageData.length);
				ouputStream.flush();
				ouputStream.close();
			} else {
				response.getOutputStream().close();
			}
		}
		
	}
	
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws JRException
	{
		String jasperPrintName = request.getParameter(getJasperPrintNameParameter());
		ReportUnitResult result = (ReportUnitResult) getJasperPrintAccessor().getObject(request, jasperPrintName);
		JasperPrint jasperPrint = (result == null ? null : result.getJasperPrintAccessor().getJasperPrint());
		if (jasperPrint == null) {
			throw new JSException("jsexception.jasperprint.not.found", new Object[] {jasperPrintName});
		}
		
		byte[] imageData = null;
		String imageMimeType = null;

		String imageName = request.getParameter(getImageNameParameter());
		if ("px".equals(imageName))
		{
			JRRenderable pxRenderer = 
				JRImageRenderer.getInstance(
					"net/sf/jasperreports/engine/images/pixel.GIF",
					OnErrorTypeEnum.ERROR
					);
			imageMimeType = JRRenderable.MIME_TYPE_GIF;
			imageData = pxRenderer.getImageData();
		}
		else
		{
			JRPrintImage image = JRHtmlExporter.getImage(Arrays.asList(new JasperPrint[]{jasperPrint}), imageName);
			
			JRRenderable renderer = image.getRenderer();
			if (renderer.getType() == JRRenderable.TYPE_SVG)
			{
				renderer = 
					new JRWrappingSvgRenderer(
						renderer, 
						new Dimension(image.getWidth(), image.getHeight()),
						ModeEnum.OPAQUE == image.getModeValue() ? image.getBackcolor() : null
						);
			}

			imageMimeType = JRTypeSniffer.getImageMimeType(renderer.getImageType());
			imageData = renderer.getImageData();
		}

		View view = new ImageView(imageMimeType, imageData);
		return new ModelAndView(view);
	}

	public SessionObjectSerieAccessor getJasperPrintAccessor() {
		return jasperPrintAccessor;
	}

	public void setJasperPrintAccessor(
			SessionObjectSerieAccessor jasperPrintAccessor) {
		this.jasperPrintAccessor = jasperPrintAccessor;
	}

	public String getJasperPrintNameParameter() {
		return jasperPrintNameParameter;
	}

	public void setJasperPrintNameParameter(String jasperPrintNameAttribute) {
		this.jasperPrintNameParameter = jasperPrintNameAttribute;
	}

	public String getImageNameParameter() {
		return imageNameParameter;
	}

	public void setImageNameParameter(String imageNameParameter) {
		this.imageNameParameter = imageNameParameter;
	}

}
