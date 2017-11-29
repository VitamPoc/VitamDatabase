/**
 * This file is part of Vitam Project.
 * 
 * Copyright 2010, Frederic Bregier, and individual contributors by the @author
 * tags. See the COPYRIGHT.txt in the distribution for a full listing of individual contributors.
 * 
 * All Vitam Project is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 * 
 * Vitam is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with Vitam. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package fr.gouv.culture.vitam.database.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Properties;


/**
 * Configuration Loader from file/environment
 * 
 * @author "Frederic Bregier"
 * 
 */
public class ConfigLoader {
	public int step = 10;
	public String xmlFile = null;
	public String technicalFile = null;
	public String separator;
	public String commonTableName;
	public String databaseType;
	public String databaseJDBC_Start;
	public String databaseJDBC_Option = "";
	public String databasePosition;
	public String databaseUser;
	public String databasePassword;
	public String databaseReadUser;
	public String databaseReadPassword;
	public DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	public boolean exportFullData = false;
	
	protected static String getProperty(Properties properties, String key, String defaultValue) {
		if (properties.containsKey(key)) {
			return properties.getProperty(key);
		}
		properties.setProperty(key, defaultValue);
		return defaultValue;
	}
	protected static int getProperty(Properties properties, String key, int defaultValue) {
		if (properties.containsKey(key)) {
			try {
				int value = Integer.parseInt(properties.getProperty(key));
				return value;
			} catch (NumberFormatException e) {
			}
		}
		properties.setProperty(key, Integer.toString(defaultValue));
		return defaultValue;
	}
	protected static long getProperty(Properties properties, String key, long defaultValue) {
		if (properties.containsKey(key)) {
			try {
				long value = Long.parseLong(properties.getProperty(key));
				return value;
			} catch (NumberFormatException e) {
			}
		}
		properties.setProperty(key, Long.toString(defaultValue));
		return defaultValue;
	}

	public void updateProperties(Properties properties) {
		separator = getProperty(properties, "vitam.separator", ",");
		commonTableName = getProperty(properties, "vitam.tablename", "AN_");
		databaseType = getProperty(properties, "vitam.databasetype", StaticValues.DEFAULT_TYPE);
		databaseJDBC_Start = getProperty(properties, "vitam.jdbcstart", StaticValues.DEFAULT_JDBC_START);
		databaseJDBC_Option = getProperty(properties, "vitam.jdbcoption", StaticValues.DEFAULT_JDBC_OPTION);
		databaseUser = getProperty(properties, "vitam.jdbcuser", StaticValues.DEFAULT_USER);
		databasePassword = getProperty(properties, "vitam.jdbcpwd", StaticValues.DEFAULT_PWD);
		databaseReadUser = getProperty(properties, "vitam.jdbcreaduser", StaticValues.DEFAULT_READUSER);
		databaseReadPassword = getProperty(properties, "vitam.jdbcreadpwd", StaticValues.DEFAULT_READPWD);
		databasePosition = getProperty(properties, "vitam.jdbcpath", "~/database");
		exportFullData = getProperty(properties, "vitam.exportfulldata", 1) == 1;
	}

	public void setProperties(Properties properties) {
		separator = getProperty(properties, "vitam.separator", separator);
		commonTableName = getProperty(properties, "vitam.tablename", commonTableName);
		databaseType = getProperty(properties, "vitam.databasetype", databaseType);
		databaseJDBC_Start = getProperty(properties, "vitam.jdbcstart", databaseJDBC_Start);
		databaseJDBC_Option = getProperty(properties, "vitam.jdbcoption", databaseJDBC_Option);
		databaseUser = getProperty(properties, "vitam.jdbcuser", databaseUser);
		databasePassword = getProperty(properties, "vitam.jdbcpwd", databasePassword);
		databaseReadUser = getProperty(properties, "vitam.jdbcreaduser", databaseReadUser);
		databaseReadPassword = getProperty(properties, "vitam.jdbcreadpwd", databaseReadPassword);
		databasePosition = getProperty(properties, "vitam.jdbcpath", databasePosition);
		exportFullData = getProperty(properties, "vitam.exportfulldata", exportFullData ? 1 : 0) == 1;
	}

	public boolean saveConfig() {
		if (xmlFile != null) {
			// based on XML config file
			File config = new File(xmlFile);
			Properties properties = new Properties();
			try {
				setProperties(properties);
				FileOutputStream out = new FileOutputStream(config);
				properties.storeToXML(out, "Vitam Tools configuration", StaticValues.CURRENT_OUTPUT_ENCODING);
				return true;
			} catch (FileNotFoundException e) {
				return false;
			} catch (IOException e) {
				return false;
			}
		}
		return false;
	}
	public void initialize(String xmlFile) {
		boolean configured = false;
		if (xmlFile != null) {
			this.xmlFile = xmlFile;
			// based on XML config file
			File config = new File(xmlFile);
			if (config.canRead()) {
				Properties properties = new Properties();
				FileInputStream in;
				try {
					in = new FileInputStream(config);
					properties.loadFromXML(in);
					in.close();
					
					updateProperties(properties);
					
					FileOutputStream out = new FileOutputStream(config);
					properties.storeToXML(out, "Vitam Tools configuration", StaticValues.CURRENT_OUTPUT_ENCODING);
					configured = true;
				} catch (FileNotFoundException e) {
				} catch (IOException e) {
				}
			}
		}
		if (!configured) {
			// based on environment setup
			separator = SystemPropertyUtil.getAndSet("vitam.separator", ",");
			commonTableName = SystemPropertyUtil.getAndSet("vitam.tablename", "AN_");
			databaseType = SystemPropertyUtil.getAndSet("vitam.databasetype", StaticValues.DEFAULT_TYPE);
			databaseJDBC_Start = SystemPropertyUtil.getAndSet("vitam.jdbcstart", StaticValues.DEFAULT_JDBC_START);
			databaseJDBC_Option = SystemPropertyUtil.getAndSet("vitam.jdbcoption", StaticValues.DEFAULT_JDBC_OPTION);
			databaseUser = SystemPropertyUtil.getAndSet("vitam.jdbcuser", StaticValues.DEFAULT_USER);
			databasePassword = SystemPropertyUtil.getAndSet("vitam.jdbcpwd", StaticValues.DEFAULT_PWD);
			databaseReadUser = SystemPropertyUtil.getAndSet("vitam.jdbcreaduser", StaticValues.DEFAULT_READUSER);
			databaseReadPassword = SystemPropertyUtil.getAndSet("vitam.jdbcreadpwd", StaticValues.DEFAULT_READPWD);
			databasePosition = SystemPropertyUtil.getAndSet("vitam.jdbcpath", "~/database");
			exportFullData = SystemPropertyUtil.getAndSetInt("vitam.exportfulldata", 1) == 1;
			saveConfig();
		}
	}

	/**
	 * 
	 */
	public ConfigLoader(String configfile) {
		initialize(configfile);
	}

}
