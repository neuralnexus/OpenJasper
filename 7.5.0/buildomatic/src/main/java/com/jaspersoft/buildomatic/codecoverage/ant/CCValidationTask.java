package com.jaspersoft.buildomatic.codecoverage.ant;

import org.apache.tools.ant.Task;

import com.jaspersoft.buildomatic.codecoverage.validate.CCValidator;

public class CCValidationTask extends Task{
	
	String jsPath ;
	String jsProPath;
	String baseFile ;
	public String getJsPath() {
		return jsPath;
	}

	public void setJsPath(String jsPath) {
		this.jsPath = jsPath;
	}

	public String getJsProPath() {
		return jsProPath;
	}

	public void setJsProPath(String jsProPath) {
		this.jsProPath = jsProPath;
	}

	public String getBaseFile() {
		return baseFile;
	}

	public void setBaseFile(String baseFile) {
		this.baseFile = baseFile;
	}

	public void execute(){
		
		CCValidator validdator = new CCValidator();
		validdator.execute(jsPath , jsProPath , baseFile);
		
	}

}
