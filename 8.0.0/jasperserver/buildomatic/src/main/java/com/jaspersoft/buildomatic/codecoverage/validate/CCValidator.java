package com.jaspersoft.buildomatic.codecoverage.validate;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.tools.ExecFileLoader;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class CCValidator {

	private ExecFileLoader execFileLoader;
	private static final String TARGET_PATH = "target/classes";
	private static final String CC_DIR = "/code-coverage/cc-report";
	private static final String CC_EXE_FILE = "jacoco-merged.exec";
	private static final String PATH_PREFIX =".";
	private static final String JRS_VERSION ="ce";
	 List<ModuleBean> json_List ;
	 
	private void loadExecutionData(File executionDataFile) throws IOException {
		execFileLoader = new ExecFileLoader();
		execFileLoader.load(executionDataFile);
	}

	private void analyzeStructure(File classesDirectory, ModuleBean bean , StringBuffer errorList)
			throws IOException {
		
		double oldInstratio  = bean.getInstructionCoveredRatio();
		double oldBranchratio  = bean.getBranchCoveredRatio();
		 CoverageBuilder coverageBuilder = new CoverageBuilder();
		 Analyzer analyzer = new Analyzer(
				execFileLoader.getExecutionDataStore(), coverageBuilder);
		analyzer.analyzeAll(classesDirectory);
	
		double newInstRatio = coverageBuilder.getBundle(bean.getModuleName()).getInstructionCounter().getCoveredRatio();
		double newBranchRatio = coverageBuilder.getBundle(bean.getModuleName()).getBranchCounter().getCoveredRatio(); 
		
		newInstRatio =	newInstRatio > 0 ? new BigDecimal(newInstRatio, new MathContext(2, RoundingMode.DOWN)).doubleValue() : 0.0;
		newBranchRatio = newBranchRatio > 0 ? new BigDecimal(newBranchRatio, new MathContext(2, RoundingMode.DOWN)).doubleValue() : 0.0;
		
		if ((newInstRatio < 0.8 && bean.getInstructionCoveredRatio() > newInstRatio) || (newInstRatio < 0.8 && bean.getBranchCoveredRatio() > newBranchRatio)) {
			
			if (bean.getInstructionCoveredRatio() > newInstRatio) {
				errorList.append("Rule violated for bundle "
					+ bean.getModuleName()
					+ " missed instruction covered ratio "
					+ newInstRatio + " but expected minimum is "
					+ bean.getInstructionCoveredRatio()+" \n");
			}
			if (bean.getBranchCoveredRatio() > newBranchRatio) {
				errorList.append("Rule violated for bundle "
					+ bean.getModuleName()
					+ " missed branch covered ratio "
					+  newBranchRatio + " but expected minimum is "
					+ bean.getBranchCoveredRatio() +" \n");
			}
		}   
		else {
			System.out.println("code coverage ratios for module "+bean.getModuleName()+" - Previous and current instruction ratios are " + oldInstratio 
					+ ", "+newInstRatio +". Previous and current branch ratios are "+ oldBranchratio + ", "+newBranchRatio);
			bean.setInstructionCoveredRatio(newInstRatio);
			bean.setBranchCoveredRatio(newBranchRatio);
			json_List.add(bean);
		}
	}

	public  void execute(String jsPath, String jsProPath ,String baseLineValuesFile) {
		try {
			Path curentDirectory =  Paths.get(PATH_PREFIX).toAbsolutePath().normalize();
			json_List = new ArrayList<ModuleBean>();
			StringBuffer errorList = new StringBuffer();
			
			ObjectMapper mapper = new ObjectMapper();
			
			List<ModuleBean> beanList = mapper.readValue(new File(curentDirectory
					+ baseLineValuesFile),
					new TypeReference<List<ModuleBean>>() {
					});
			
			for (ModuleBean bean : beanList){
				Path path = null;
				if (!bean.isSkip()){
				if (bean.getJrsVersion().equals(JRS_VERSION)) {
					path = Paths.get(jsPath, bean.getModuleName(),TARGET_PATH);
				 } else {
					path = Paths.get(jsProPath, bean.getModuleName(),TARGET_PATH);
				}
				loadExecutionData(new File(jsProPath + CC_DIR,
						CC_EXE_FILE));
				analyzeStructure(path.toFile(),bean ,errorList);
				}else {
					json_List.add(bean);
			 }
			}
			if (errorList.length() > 0) {
				throw new BuildException(errorList.toString());
			}
			else {
				ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
				writer.writeValue(new File(curentDirectory
						+ baseLineValuesFile), json_List);
			}
		} catch (Exception e) {
			throw new BuildException(
					" Error occured during code coverage validation \n"
							+ e.getMessage());
		}
	}
}
