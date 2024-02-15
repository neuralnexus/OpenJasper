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
package com.jaspersoft.jasperserver.api.engine.scheduling.domain;

import com.jaspersoft.jasperserver.api.JasperServerAPI;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.jaxb.RelativeDateRangeWrapper;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.jaxb.ReportJobSourceParametersXmlAdapter;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.jaxb.ValuesCollection;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * The source of a report job, consisting of a report to execute and a set of
 * report input values.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ReportJobSource.java 47331 2014-07-18 09:13:06Z kklein $
 * @since 1.0
 * @see ReportJob#getSource()
 */
@JasperServerAPI
@XmlRootElement
@XmlSeeAlso({ValuesCollection.class, RelativeDateRangeWrapper.class})
public class ReportJobSource implements Serializable {
	private static final long serialVersionUID = 1L;

	private String reportUnitURI;
	private Map<String, Object> parameters;
	
	/**
	 * Creates an empty job source.
	 */
	public ReportJobSource() {
	}

	/**
	 * Returns the repository URI/path of the report that the job will execute.
	 * 
	 * @return the report that the job will execute
	 */
	public String getReportUnitURI() {
		return reportUnitURI;
	}

	/**
	 * Defines the report which should be executed by the job.
	 * 
	 * @param reportUnitURI the repository URI/path of the report that the job 
	 * should execute
	 */
	public void setReportUnitURI(String reportUnitURI) {
		this.reportUnitURI = reportUnitURI;
	}

	/**
	 * Returns the set of input values which will be used when running the
	 * job report.
	 * 
	 * <p>
	 * The input values are associated to report input controls.
	 * </p>
	 * 
	 * @return the input values used for the report, indexed by input control
	 * names
	 * @see ReportUnit#getInputControls()
     * @deprecated use ReportJobSource.getParameters() instead
	 */
    @XmlTransient
	public Map getParametersMap() {
		return getParameters();
	}

    /**
     * Returns the set of input values which will be used when running the
     * job report.
     *
     * <p>
     * The input values are associated to report input controls.
     * </p>
     *
     * @return the input values used for the report, indexed by input control
     * names
     * @see ReportUnit#getInputControls()
     * @since 4.7
     */
    @XmlJavaTypeAdapter(ReportJobSourceParametersXmlAdapter.class)
    public Map<String, Object> getParameters(){
        return parameters;
    }

	/**
	 * Sets the set of input values to be used when running the job report.
	 * 
	 * <p>
	 * The values are passed in a map indexed by report input control/parameter
	 * names.
	 * </p>
	 * 
	 * @param parameters the report input values
     * @deprecated use ReportJobSource.setParameters() instead
	 */
	public void setParametersMap(Map parameters) {
		setParameters(parameters);
	}

    /**
     * Sets the set of input values to be used when running the job report.
     *
     * <p>
     * The values are passed in a map indexed by report input control/parameter
     * names.
     * </p>
     *
     * @param parameters the report input values
     * @since 4.7
     */
    public void setParameters(Map<String, Object> parameters){
        this.parameters = parameters;
    }

  /**
   * Convenience constructor that returns a distinct copy of the input ReportJobSource.
   * All of the copy's Object members are themselves copies as well.
   *
   * We're deliberately avoiding using clone()
   */
  public ReportJobSource(ReportJobSource jobSource) {
      this.setReportUnitURI(jobSource.getReportUnitURI());

      // Here we assume that copying any Map Object references is safe
      //   That it is OK if any of these are shared.
      if (jobSource.getParametersMap() != null) {
          this.setParametersMap(new HashMap(jobSource.getParametersMap()));
      }
  }
}
