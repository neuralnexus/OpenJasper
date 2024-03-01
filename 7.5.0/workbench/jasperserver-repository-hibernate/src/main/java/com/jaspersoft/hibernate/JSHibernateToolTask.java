package com.jaspersoft.hibernate;

import javax.xml.bind.annotation.XmlSchema;


import org.hibernate.tool.ant.HibernateToolTask;
import java.text.MessageFormat;

public class JSHibernateToolTask extends HibernateToolTask {

	public JSHibernateToolTask() {
		super();
        setupBindInfoPackage();
	}

	void setupBindInfoPackage() {
	    String nsuri = "http://www.hibernate.org/xsd/orm/cfg";
	    String[] packageInfoClassName = {"org.hibernate.boot.jaxb.hbm.spi.package-info","org.hibernate.boot.jaxb.cfg.spi.package-info"};
	    StringBuilder msg = new StringBuilder();
	    boolean found = false;
            for(String packageName: packageInfoClassName){
		    try {
		    	Class<?> packageInfoClass = Class.forName(packageName);
		        final XmlSchema xmlSchema = packageInfoClass.getAnnotation(XmlSchema.class);
		        if (xmlSchema == null) {
		        	msg.append(MessageFormat.format(
		                    "Class [{0}] is missing the [{1}] annotation. Processing bindings will probably fail.",
		                    packageName, XmlSchema.class.getName()));
		        	msg.append('\n');
		        } else {
		            final String namespace = xmlSchema.namespace();
		            if (!nsuri.equals(namespace)) {
		            	msg.append(MessageFormat.format(
			            "Namespace [{0}] of the [{1}] annotation does not match [{2}]. Processing bindings will probably fail.",
		                    namespace, XmlSchema.class.getName(), nsuri));
		                msg.append('\n');
		            } else {
		            	found = true;
		            	break;
		            }
		        }
		    } catch (ClassNotFoundException cnfex) {
		    	msg.append(MessageFormat.format(
	                    "Class [{0}] could not be found. Processing bindings will probably fail.",
		                packageName));
		    	msg.append('\n');
		    }
             }     
             if(!found){
        	System.out.println(msg.toString());
             }
	}
}
