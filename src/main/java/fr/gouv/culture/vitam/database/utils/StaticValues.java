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
import java.net.URL;
import java.util.Locale;

import org.dom4j.io.OutputFormat;

import fr.gouv.culture.vitam.constance.ConstanceDataType;

/**
 * Static values used by all classes
 * 
 * @author "Frederic Bregier"
 * 
 */
public class StaticValues {

	public static final String CONFIG_VITAM = "vitam-config.xml";
	public static final String UTF_8 = "UTF-8";
	public static final String ISO_8859_15 = "ISO-8859-15";
	public static final String windows_1252 = "windows-1252";
	public static final String CURRENT_OUTPUT_ENCODING = UTF_8;
	public static final String TYPEH2 = "h2";
	public static final String TYPEPOSTGRE = "postgresql";
	public static final String TYPEORACLE = "oracle";
	public static final String TYPEMYSQL = "mysql";
	public static final String DEFAULT_TYPE = TYPEH2;
	public static final String JDBCH2 = "jdbc:h2:";
	public static final String JDBCPOSTGRE = "jdbc:postgresql:";
	public static final String JDBCMYSQL = "jdbc:mysql:";
	public static final String JDBCORACLE = "jdbc:oracle:thin@:";
	public static final String DEFAULT_JDBC_START = JDBCH2;
	public static final String DEFAULT_JDBC_OPTION = ";AUTO_SERVER=TRUE";
	public static final String DEFAULT_USER = "AN";
	public static final String DEFAULT_PWD = "AN";
	public static final String DEFAULT_READUSER = "lecteur";
	public static final String DEFAULT_READPWD = "lecteur";
	public static ConfigLoader config;
	public static PreferencesResourceBundle LABELS;
	public static OutputFormat defaultOutputFormat;
	
	// Fixed element
	public static final String RESOURCES_LICENSE_TXT = "/resources/LICENSE.txt";

	public static final String ABOUT = "Copyright (c) 2012 Ministere de la Culture et de la Communication\n"
			+ "Sous-Direction du Systeme d'Information\nProjet Vitam\n\nVersion: " + Version.ID
			+ "\n\nContributeurs: Frederic Bregier\n\n"
			+ "Site web: http://www.archivesnationales.culture.gouv.fr/\n\n"
			+ "Licence: GPLV3\n";
	public static String HELP_COMMAND;

	public static enum LBL {
		appName, option_langue, menu_file, menu_edit, menu_tools, menu_help,
		file_import, file_quit, tool_export, tool_visual, tool_select, tool_sql,
		tool_xmlimport, tool_csvimport,
		edit_copy, edit_clear,
		help_about, help_config,
		label_about,
		action_import, action_export,
		error_error, error_warning, error_alerte,
		error_parser, error_writer,
		error_filenotfound, error_filenotfile, error_fileaccess,
		error_notequal, error_notenough,
		button_save, button_cancel, button_exit, button_update;

		public String label;

		private LBL() {
			label = this.name().replaceFirst("_", ".");
		}

		public String get() {
			return LABELS.get(this.label);
		}
	}

	public final static void initialize() {
		StaticValues.LABELS = new PreferencesResourceBundle(Locale.getDefault());
		config = new ConfigLoader(CONFIG_VITAM);
		defaultOutputFormat = OutputFormat.createPrettyPrint();
		defaultOutputFormat.setEncoding(CURRENT_OUTPUT_ENCODING);
		if (LBL.option_langue.get().equalsIgnoreCase("fr")) {
			HELP_COMMAND = "Necessite au moins \"--xml fichier\" ou \"--print fichier\" ou \"--convertpdfa source destination\" ou \" --checkdigest fichier\" comme argument\n" +
					"\t[--help] pour imprimer cette aide\n" +
					"\t[-0,--config configurationFile (defaut=vitam-config.xml)]";
		} else {
			HELP_COMMAND = "Need at least \"--xml filename\" or \"--print filename\" or \"--convertpdfa from to\" or \"--checkdigest filename\" as argument\n" +
					"\t[--help] to print help\n" +
					"\t[-0,--config configurationFile (default=vitam-config.xml)]";
		}
		ConstanceDataType.initializeTypes();
	}

	public final static String resourceToFile(String arg) {
		File test = new File(arg);
		if (test.exists()) {
			return test.getAbsolutePath();
		}
		URL url = arg.getClass().getResource(arg);
		if (url == null) {
			return null;
		}
		String value = url.toString();
		value = value.substring(6).replaceAll("%20", "\\ ");
		return new File(value).getAbsolutePath();
	}

	public final static String resourceToParent(String arg) {
		return new File(arg.getClass().getResource(arg).toString()).getParentFile().toURI()
				.getPath();
	}

	public final static String resourceToURL(String arg) {
		return arg.getClass().getResource(arg).getFile();
	}

	/**
	 * Utility to free memory
	 */
	public final static void freeMemory() {
		long total = Runtime.getRuntime().totalMemory();
		for (int i = 0; i < 10; i++) {
			System.gc();
		}
		while (true) {
			System.gc();
			long newtotal = Runtime.getRuntime().totalMemory();
			if ((((double) (total - newtotal)) / (double) total) < 0.1) {
				break;
			} else {
				total = newtotal;
			}
		}
		System.gc();
	}

	/**
	 * 
	 * @param fullPath
	 * @param fromPath
	 * @return the sub path starting from fromPath (inclusive last dir) of fullPath
	 */
	public static final String getSubPath(File fullPath, File fromPath) {
		String spath = fullPath.getAbsolutePath();
		String sparent = fromPath.getParentFile().getAbsolutePath() + File.separator;
		return spath.replace(sparent, "");
	}
}
