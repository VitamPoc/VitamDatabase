/**
 * This file is part of Vitam Project.
 * 
 * Copyright 2010, Frederic Bregier, and individual contributors by the @author tags. See the
 * COPYRIGHT.txt in the distribution for a full listing of individual contributors.
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
package fr.gouv.culture.vitam.constance.test;

import java.io.File;
import java.io.IOException;

import org.waarp.common.database.exception.WaarpDatabaseException;
import org.waarp.common.database.exception.WaarpDatabaseNoConnectionException;
import org.waarp.common.database.exception.WaarpDatabaseSqlException;
import org.waarp.common.logging.WaarpInternalLogger;
import org.waarp.common.logging.WaarpInternalLoggerFactory;
import org.waarp.common.logging.WaarpSlf4JLoggerFactory;

import ch.qos.logback.classic.Level;


import fr.gouv.culture.vitam.constance.ConstanceIdentifier;
import fr.gouv.culture.vitam.database.DbSchema;
import fr.gouv.culture.vitam.database.DbVitam2Database;
import fr.gouv.culture.vitam.database.utils.StaticValues;
import fr.gouv.culture.vitam.writer.XmlWriter;

/**
 * @author "Frederic Bregier"
 * 
 */
public class Exemple {
	/**
	 * Internal Logger
	 */
	private static WaarpInternalLogger logger;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		WaarpInternalLoggerFactory.setDefaultFactory(new WaarpSlf4JLoggerFactory(Level.WARN));
		logger = WaarpInternalLoggerFactory.getLogger(Exemple.class);
		StaticValues.initialize();
		if (args.length == 0) {
			logger.warn("Fichier de description technique en premier argument");
			return;
		}
		long tfirst = System.currentTimeMillis();
		long first = Runtime.getRuntime().totalMemory();
		DbVitam2Database database = null;
		try {
			database = new DbVitam2Database("h2", "jdbc:h2:J:/Git/SEDA/VitamDatabase/databases/vitamdb;AUTO_SERVER=TRUE", "vitam", "vitam");
		} catch (WaarpDatabaseNoConnectionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		XmlWriter writer = new XmlWriter(new File("J:/Git/SEDA/VitamDatabase/databases/exportxml.xml"), "monroot");
		for (int i = 0; i < args.length; i++) {
			File technicalDescription = new File(args[i]);
			ConstanceIdentifier identifier = new ConstanceIdentifier(technicalDescription, 
					ConstanceIdentifier.SEPARATOR, "example");
			try {
				identifier.loadTechnicalDescription();
				identifier.printStructure();
				//identifier.loadData();
				/*DbSchema schema = identifier.getFullSchema();
				schema.createBuildOrder();
				schema.printOrder();
				long second = Runtime.getRuntime().totalMemory();
				System.out.println("Mémoire utilsée: " + ((second-first) / 1024 / 1024));
				database.createDatabases(schema);
				database.fillDatabases(schema);
				long third = Runtime.getRuntime().totalMemory();
				System.out.println("Mémoire utilsée: " + ((third-first) / 1024 / 1024));
				*//*identifier.loadData();
				schema = identifier.getSchema();
				long third = Runtime.getRuntime().totalMemory();
				System.out.println("Mémoire utilsée: " + ((third-first) / 1024 / 1024));*/
				DbSchema schema = identifier.getSimpleSchema();
				schema.createBuildOrder();
				schema.printOrder();
				writer.add(schema);
				database.dropDatabases(schema);
				database.createDatabases(schema);
				database.fillDatabases(schema, identifier);
				long fourth = Runtime.getRuntime().totalMemory();
				System.out.println("Mémoire utilsée: " + ((fourth-first) / 1024 / 1024));
				long tthird = System.currentTimeMillis();
				System.out.println("Temps écoulé: " + ((tthird-tfirst) / 1000));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.warn(StaticValues.LBL.error_error.get() + e);
			} catch (WaarpDatabaseSqlException e) {
				// TODO Auto-generated catch block
				logger.warn(StaticValues.LBL.error_error.get() + e);
			} catch (WaarpDatabaseException e) {
				// TODO Auto-generated catch block
				logger.warn(StaticValues.LBL.error_error.get() + e);
			}
		}
		try {
			writer.write();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.warn(StaticValues.LBL.error_error.get() + e);
		}
		database.close();
	}

}
