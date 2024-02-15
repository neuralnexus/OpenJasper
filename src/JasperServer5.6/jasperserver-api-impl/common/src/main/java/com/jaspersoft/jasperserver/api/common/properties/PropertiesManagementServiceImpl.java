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
package com.jaspersoft.jasperserver.api.common.properties;

import java.lang.reflect.Array;
import java.util.*;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.FolderImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValues;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValuesItem;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ListOfValuesItemImpl;

import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;

/**
 * PropertiesManagementServiceImpl
 *
 * This service manages setting, retrieving and storing of
 * configurable properties.  by convention, the property keys
 * should be qualified with dots to avoid namespace conflict.
 *   for example:  mondrian.query.limit
 *
 * @author sbirney (sbirney@users.sourceforge.net)
 * @author udavidovich
 *
 * TODO: mulitple VMs concurrent updates
 * TODO: mulitple namespaces
 */
public class PropertiesManagementServiceImpl 
implements PropertiesManagementService, ApplicationContextAware, ApplicationListener<ContextRefreshedEvent> {

    protected static final Log log = LogFactory
	    .getLog(PropertiesManagementServiceImpl.class);

    public static final String CONTENT_FOLDER = "properties";
    public static final String CONTENT_FOLDER_DESC = "System Properties";
    public static final String RESOURCE_NAME = "GlobalPropertiesList";
    public static final String RESOURCE_FULL_NAME = Folder.SEPARATOR +
													   CONTENT_FOLDER +
													   Folder.SEPARATOR +
													   RESOURCE_NAME;    

    protected ApplicationContext context;
//    protected SessionFactoryImpl sessionFactory;
    
    protected Map<String,String> changers;
    protected Map<String,PropertyChanger> changerObjects;
  
    protected Map<String,String> mov;

    // Using this map to find removed properties on reload
    protected Map<String,String> previousMov;

    protected RepositoryService mRepository;
    
    /*
     * c'tor
     */
    public PropertiesManagementServiceImpl() {
    }

    // call initialize before using   
    public void initialize() {
    	log.info("Initializing porperty management service");
		//parse changers for the first time
		parseChangers();
		loadProperties();
		applyProperties();
    }


    /*
     * Use setProperty method to set and apply a configuration key to a value.
     * The key prefix must correspond to the changer being configured.
     * @param key should be fully qualified, must not be null
     * @param val the value as a string
     */
    public void setProperty(String key, String val) {
    	log.debug("property "+key+" is going to be set to "+val);
    	mov.put(key, val);
    	saveProperties();
    }
    
    protected PropertyChanger getChanger(String key) {
        if(key.isEmpty()) {
            return null;
        }

        int semicolonPos = key.indexOf(":") > 0 ? key.indexOf(":") : Integer.MAX_VALUE;
        int pointPos = key.indexOf(".") > 0 ? key.indexOf(".") : Integer.MAX_VALUE;
        int changerKeyLength = Math.min(key.length(), Math.min(semicolonPos, pointPos));

    	String changerKey = key.substring(0, changerKeyLength);
    	return changerObjects.get(changerKey);    	
    }

    protected void applyProperty(String key, String val) {
    	log.debug("Applying configuration property "+key+" = "+val);
      PropertyChanger changer = null;
    	try {
    		changer = getChanger(key);
        if(changer == null) {
            log.error("PropertyChanger not found for property "+key);
        } else {
            changer.setProperty(key, val);
        }
    	} catch (Exception e) {
        	log.error("PropertyChanger "+changer+" failed to apply configuration property "+key+" = "+val, e);
    	}
    }

    protected void removeProperty(String key, String val) {
        if (log.isDebugEnabled()) { log.debug("Removing configuration property " + key + " = " + val); }
        try {
            getChanger(key).removeProperty(key, val);
        } catch (Exception e) {
            log.error("PropertyChanger " + getChanger(key) + "failed to remove configuration property " +
                    key + " = " + val, e);
        }
    }

    /*
     * Use getProperty to retrieve the state of a configuration property.
     * The value is queried from the relevant changer
     * @param key must not be null
     * @return associated value or null
     */
    public String getProperty(String key) {
    	try {
    		String val = getChanger(key).getProperty(key);
        	log.debug("Read configuration property "+key+" = "+val);
    		return val;    			
    	} catch (Exception e) {
        	log.error("PropertyChanger "+getChanger(key)+"failed to read configuration property "+key);
        	return null;
    	}
    }
    
    protected void loadProperties() {
    	log.info("Loading configuration properties from "+RESOURCE_FULL_NAME);
		RepositoryService rep = getRepository();
		ListOfValues lov =
		    (ListOfValues)
		    (rep).getResource( null, RESOURCE_FULL_NAME );
		if (lov == null) {
	    	log.debug(RESOURCE_FULL_NAME+" not found, creating it");
		    lov = (ListOfValues) rep.newResource( null, ListOfValues.class );
		    lov.setName( RESOURCE_NAME );
		    lov.setLabel( RESOURCE_NAME );
		    lov.setDescription( RESOURCE_NAME );
	
		    Folder folder = rep.getFolder( null, Folder.SEPARATOR + CONTENT_FOLDER );
		    if (folder == null) {
		    	log.debug(Folder.SEPARATOR + CONTENT_FOLDER+" not found, creating it");
				folder = new FolderImpl();
				folder.setName( CONTENT_FOLDER );
				folder.setLabel( CONTENT_FOLDER_DESC );
				folder.setDescription( CONTENT_FOLDER_DESC );
				folder.setParentFolder( rep.getFolder(null, Folder.SEPARATOR) );
				rep.saveFolder( null, folder );
		    }
		    lov.setParentFolder( folder );
		    rep.saveResource( null, lov );
		}

        if (this.mov != null) {
            // Storing previous values to be able find removed properties
            this.previousMov = Collections.unmodifiableMap(new HashMap<String, String>(this.mov));
        }

		mov = new HashMap<String, String>();
		for (ListOfValuesItem i : lov.getValues()) {
			mov.put(i.getLabel(), (String)i.getValue());
		}
	}

    /*
     * saveProperties
     * call saveProperties after setting one or more properties
     */
    public void saveProperties() {
    	log.info("Saving configuration properties to "+RESOURCE_FULL_NAME);
    	RepositoryService rep = getRepository();
    	ListOfValues lov = (ListOfValues)rep.getResource( null, RESOURCE_FULL_NAME );

    	// this is not so elegant to have to copy the new one over the old
	    ListOfValuesItem[] oldItems = lov.getValues();
	    for (int i=0; i<oldItems.length; i++) {
	    	lov.removeValue(oldItems[i]);
	    }

	    for (Entry<String, String> e: mov.entrySet()) {
	    	ListOfValuesItemImpl i = new ListOfValuesItemImpl();
	    	i.setLabel(e.getKey());
	    	i.setValue(e.getValue());
	    	lov.addValue(i);
	    }

	    rep.saveResource( null, lov );
    }
    
    public void applyProperties() {
    	log.info("Appling all configuration properties");

	    for (Entry<String, String> e: mov.entrySet()) {
	    	applyProperty(e.getKey(),e.getValue());
	    }
    }

    public void removeProperties() {
    	log.debug("Removing deleted configuration properties");

        if (this.previousMov != null) {
            for (Entry<String, String> e: this.previousMov.entrySet()) {

                if (!this.mov.containsKey(e.getKey())) {
                    removeProperty(e.getKey(), e.getValue());
                }
            }
            // After changers was notified we don't need it anymore
            this.previousMov = null;
        }
    }


    public int size() {
    	return mov.size();
    }

    public Set entrySet() {
    	return mov.entrySet();
    }

    public String remove(String key) {
    	return mov.remove(key);
    }

    public Map<String, String> removeByValue(String val) {
        Map<String, String> removedKeys = new HashMap<String, String>();

        if (this.mov != null) {
            for(Iterator<Map.Entry<String, String>> it = this.mov.entrySet().iterator(); it.hasNext();) {
                Map.Entry<String, String> e = it.next();

                if (e.getValue().equals(val)) {
                    removedKeys.put(e.getKey(), e.getValue());
                    it.remove();
                }
            }

            if(removedKeys.size() > 0) {
                saveProperties();
                for(Map.Entry<String, String> e : removedKeys.entrySet()) {
                    this.getChanger(e.getKey()).removeProperty(e.getKey(), e.getValue());
                }
            }
        }

        return removedKeys;
    }

    private RepositoryService getRepository() {
    	return mRepository;
    }
    public void setRepository(RepositoryService repository) {
    	mRepository = repository;
    }

	public Map<String, String> getChangers() {
		return changers;
	}

	public void setChangers(Map<String, String> changers) {
		this.changers = changers;
		//if changerObject is null, the this object had not been initialized yet
		//so the changers might not exist yet and we should not parse them
		if (changerObjects!=null)
			parseChangers();
	}

	//This method looks up spring context for changer beans based on this name
	//We are wiring the service with changer names and not changer beans to eliminate circular references.
	private void parseChangers() {
    	log.debug("Collecting all changers from Spring context");
		changerObjects = new HashMap<String, PropertyChanger>();
		for (Entry<String,String> e: changers.entrySet()) {
			changerObjects.put(e.getKey(), (PropertyChanger)context.getBean(e.getValue()));
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		context = applicationContext;
	}

	@Override
	public void reloadProperties() {
		loadProperties();
        removeProperties();
        applyProperties();
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		log.debug("Service got a ContextRefreshedEvent "+event);
		if (changerObjects==null) {
			fixFolderParentBug();
			initialize();			//initialize only if not initialized already		
		}
	}

//	@Override
//	public void onApplicationEvent(ContextStartedEvent event) {
//		log.debug("Service go a ContextStartedEvent "+event);
//		initialize();
//	}

	// This is a fix to bug #31746 affecting versions prior to 5.0.
	// This code needs to be removed after 4.7.1 reaches end-of-life
	// TODO: refactor all upgrade related code (this & AdhocUpgradeExecutor) to a dedicated upgrade service
	// It will check the existence of a parent to "/properties" and will set it to "/" if it is missing
	private void fixFolderParentBug() {
		RepositoryService rep = getRepository();
		Folder folder = rep.getFolder(null, Folder.SEPARATOR
				+ CONTENT_FOLDER);
		if (folder != null && folder.getParentFolder()==null) {
			Folder parent = rep.getFolder(null, Folder.SEPARATOR);
			if (parent != null) {
				folder.setParentFolder(parent);
				rep.saveFolder(null, folder);
				log.info("Property folder has been assigned as a child of the root folder");
			} else {
				log.error("Root folder does not exist");
			}
		}		
	}

}
