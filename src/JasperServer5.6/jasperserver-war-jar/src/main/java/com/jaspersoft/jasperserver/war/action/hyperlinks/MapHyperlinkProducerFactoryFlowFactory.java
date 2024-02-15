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
package com.jaspersoft.jasperserver.war.action.hyperlinks;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.export.JRHyperlinkProducer;
import net.sf.jasperreports.engine.export.JRHyperlinkProducerFactory;
import net.sf.jasperreports.engine.export.JRHyperlinkProducerMapFactory;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: MapHyperlinkProducerFactoryFlowFactory.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class MapHyperlinkProducerFactoryFlowFactory implements HyperlinkProducerFactoryFlowFactory, Serializable {

	private static final long serialVersionUID = 1L;
	
	private Map flowHyperlinkProducers;
	
	public JRHyperlinkProducerFactory getHyperlinkProducerFactory(HttpServletRequest request, HttpServletResponse response) {
		if (flowHyperlinkProducers == null) {
			return null;
		}
		
		JRHyperlinkProducerMapFactory hyperlinkProducerMapFactory = new JRHyperlinkProducerMapFactory();
		for (Iterator it = flowHyperlinkProducers.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			String type = (String) entry.getKey();
			HyperlinkProducerFlowFactory flowProducer = (HyperlinkProducerFlowFactory) entry.getValue();
			
			JRHyperlinkProducer producer = flowProducer.getHyperlinkProducer(request, response);
			hyperlinkProducerMapFactory.addProducer(type, producer);
		}

		return hyperlinkProducerMapFactory;
	}

	public Map getFlowHyperlinkProducers() {
		return flowHyperlinkProducers;
	}

	public void setFlowHyperlinkProducers(Map producers) {
		this.flowHyperlinkProducers = producers;
	}

}
