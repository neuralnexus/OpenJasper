package com.jaspersoft.buildomatic.codecoverage.validate;

import com.fasterxml.jackson.annotation.JsonInclude;

public class ModuleBean {

	private String moduleName;
	private String jrsVersion;
	private double instructionCoveredRatio;
	private double branchCoveredRatio;
	private boolean isSkip;
	
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	public String getJrsVersion() {
		return jrsVersion;
	}
	public void setJrsVersion(String jrsVersion) {
		this.jrsVersion = jrsVersion;
	}
	public double getInstructionCoveredRatio() {
		return instructionCoveredRatio;
	}
	public void setInstructionCoveredRatio(double instructionCoveredRatio) {
		this.instructionCoveredRatio = instructionCoveredRatio;
	}
	public double getBranchCoveredRatio() {
		return branchCoveredRatio;
	}
	public void setBranchCoveredRatio(double branchCoveredRatio) {
		this.branchCoveredRatio = branchCoveredRatio;
	}
	public boolean isSkip() {
		return isSkip;
	}
	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	public void setSkip(boolean isSkip) {
		this.isSkip = isSkip;
	}



}
