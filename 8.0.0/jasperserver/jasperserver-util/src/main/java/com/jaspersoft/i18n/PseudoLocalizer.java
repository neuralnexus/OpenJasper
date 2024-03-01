/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.jaspersoft.i18n;

import java.io.*;
import java.net.URL;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;


/**
 * This class generates pseudo localized bundle strings. 
 * It basically adds a predefined prefix and suffix to each string in a bundle file.
 * The predefined additions are defined in a property file as well 
 *
 */
public class PseudoLocalizer {

	private static final String PREFIX_KEY = "PREFIX";
	private static final String SUFFIX_KEY = "SUFFIX";
	private static final String BUNDLE_NAME_SUFFIX_KEY = "BUNDLE_NAME_SUFFIX";
	private Properties bundleEntries;
	private String prefix;
	private String suffix;
	private OutputStream out;
	
	private PseudoLocalizer(File localizerPropsFile, File bundleFile) {
		try{
			bundleEntries = loadProperties(bundleFile);
			
			Properties props = loadProperties(localizerPropsFile);
			prefix = props.getProperty(PREFIX_KEY, "").trim();
			suffix = props.getProperty(SUFFIX_KEY, "".trim());
			out = getOutputStream(bundleFile, props.getProperty(BUNDLE_NAME_SUFFIX_KEY).trim());
			
			localizeAndSave();
		} 
		catch (Exception ex) {
			ex.printStackTrace();
			return;
		}

	}
	
	/** 
	 * This class localizes the bundle strings and save them in a file contained 
	 * in the same directory with the original bundle file.
	 * @throws IOException
	 */
	private void localizeAndSave() 
	throws IOException{
		Properties localizedEntries = new Properties();
		Enumeration enumeration = bundleEntries.propertyNames();
		while (enumeration.hasMoreElements()) {
			String key = (String) enumeration.nextElement();
			String value = prefix + bundleEntries.getProperty(key) + suffix;
			System.out.println("Key = " + key + " Value: " + value);
			localizedEntries.setProperty(key, value);
		}
		
		localizedEntries.store(out, "THIS FILE IS AUTOMATICALLY GENERATED BY PSEUDO-LOCALIZER.");
	}

	/**
	 * This function gets the OutputStream of the file containing pseudo-localized properties.
	 * The filename will be  bundlefilename_{BUNDLE_NAME_SUFFIX}.properties
	 * @param oldFile file containing properties to be localized
	 * @param newFileNameSuffix BUNDLE_NAME_SUFFIX
	 * @return OutputStream of the file containing pseudo-localized properties
	 * @throws FileNotFoundException
	 */
	private static OutputStream getOutputStream(File oldFile, String newFileNameSuffix) 
	throws FileNotFoundException {
		String fileName = oldFile.getName();
		int index = fileName.indexOf(".");
		String newFileName = fileName.substring(0, index) + "_" + newFileNameSuffix + fileName.substring(index);
		File newFile = new File(oldFile.getParent() + File.separator + newFileName);
		System.out.println("New file name: " + newFile.getAbsolutePath());
		return new FileOutputStream(newFile);
	}
	
	
	/** Loads the properties from the given file 
	 * 
	 * @param file The file holding the properties
	 * @return properties
	 */
	private static final Properties loadProperties(File file)
	throws IOException {
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(file));
		}
		catch (IOException ex) {
			System.out.println("An error occured while loading " + file.getAbsolutePath() + ".");
			throw ex;
		}
		return props;
	}
	
	/** 
	 * The only public accessible class to localize the bundleFile 
	 * 
	 * @param localizerPropsFile file containing localizer specific properties
	 * @param bundleFile file to be pseudo-localized
	 */
	public static final void localize(File localizerPropsFile, File bundleFile) {
		new PseudoLocalizer(localizerPropsFile, bundleFile);
	}
	
	/** 
	 * For debuging purposes. Prints all the content of the properties file in the given locale.
	 * Usage example: debug(new File("C:\\workspace\\Jaspersoft\\i18n\\LicenseMessages_ja.properties"), "C:\\workspace\\Jaspersoft\\i18n\\LicenseMessages", Locale.JAPAN);
	 */
	private static void debug(File bundleFile, String bundleName, Locale locale) {
		try {
			Properties localizedEntries = loadProperties(bundleFile);
			ResourceBundle bundle= ResourceBundle.getBundle(bundleName, locale, new ClassLoader() {
				public URL findResource(String resource) {
					System.out.println("resource = " + resource);
					try {
						return new File(resource).toURL();
					} catch(Exception ex) {
						ex.printStackTrace();
						return null;
					}
			    }
			});
			Enumeration enumeration = localizedEntries.propertyNames();
			while (enumeration.hasMoreElements()) {
				String key = (String) enumeration.nextElement();
				String value = bundle.getString(key);
				System.out.println("Key = " + key + " Value: " + value);
				
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
	}
	
	/**
	 * Pseudo-Localizes the given bundle file with the properties in property file.
	 * Both files should be in ANSII, and the content of PREFIX and SUFFIX should be converted
	 * to ASCII with "native2ascii -encoding Unicode <filename>"
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("Usage: java com.jaspersoft.i18n.PseudoLocalizer {localizer_property_file_path} " +
    				"{bundle_file_path_name}\n" +
    				"where the {localizer_property_file} defines the following properties: \n\t" +
    				PREFIX_KEY + ": The string to be added infront of each bundle string\n\t" +
    				SUFFIX_KEY + ": The string to be added at the end of each bundle string\n\t" +
    				BUNDLE_NAME_SUFFIX_KEY + ": The localized filename extension according to standard Java rules," +
    				"e.g ja for japanese, de for german.\n\n" +
    				"For {localizer_property_file_path}, you need to use the native2ascii tool to convert the content to Latin-1/Unicode encodings:\n" +
    				"\t native2ascii -encoding Unicode {localizer_property_filename}\n");
    		return;
		}

		System.out.println("Localizer property file: " + args[0]);
		System.out.println("Bundle file: " + args[1]);
		localize(new File(args[0]), new File(args[1]));
		
		//debug(new File("C:\\workspace\\Jaspersoft\\i18n\\LicenseMessages_ja.properties"), "C:\\workspace\\Jaspersoft\\i18n\\LicenseMessages", Locale.JAPAN);
	}

}
