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
package fr.gouv.culture.vitam.writer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.waarp.common.database.exception.WaarpDatabaseSqlException;
import org.waarp.common.logging.WaarpInternalLogger;
import org.waarp.common.logging.WaarpInternalLoggerFactory;

import fr.gouv.culture.vitam.database.DbFieldValue;
import fr.gouv.culture.vitam.database.DbSchema;
import fr.gouv.culture.vitam.database.DbTable;
import fr.gouv.culture.vitam.database.DbTableRow;
import fr.gouv.culture.vitam.database.utils.StaticValues;
import fr.gouv.culture.vitam.reader.VitamReader;

/**
 * Xml Writer
 * @author "Frederic Bregier"
 *
 */
public class XmlWriter {
	/**
	 * Internal Logger
	 */
	private static final WaarpInternalLogger logger = WaarpInternalLoggerFactory
			.getLogger(XmlWriter.class);
	
	public File file;
	public String rootname;
	public Document document;
	/**
	 * @param file
	 * @param rootname
	 */
	public XmlWriter(File file, String rootname) {
		this.file = file;
		this.rootname = rootname;
		DocumentFactory factory = DocumentFactory.getInstance();
		document = factory.createDocument(StaticValues.CURRENT_OUTPUT_ENCODING);
		Element root = factory.createElement(rootname);
		document.setRootElement(root);
	}
	public void add(DbSchema schema) {
		Element eschema = schema.getElement(true, StaticValues.config.exportFullData);
		document.getRootElement().add(eschema);
	}
	public void write() throws IOException {
		FileOutputStream out = new FileOutputStream(file);
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding(StaticValues.CURRENT_OUTPUT_ENCODING);
		XMLWriter writer = new XMLWriter(out, format);
		writer.write(document);
		writer.close();
		out.close();
		document.clearContent();
		document = null;
		DocumentFactory factory = DocumentFactory.getInstance();
		document = factory.createDocument(StaticValues.CURRENT_OUTPUT_ENCODING);
		Element root = factory.createElement(rootname);
		document.setRootElement(root);
		StaticValues.freeMemory();
	}
	
	public static void loadDbTableDataFromFile(DbSchema schema, DbTable table, Element etable) throws WaarpDatabaseSqlException, IOException {
		int read = 0;
		VitamReader reader = schema.identifier.getReader();
		Element erows = DocumentFactory.getInstance().createElement("rows");
		switch (table.type) {
			case CSVTYPE: {
				String[] values = null;
				// first row to ignore
				if (reader.readOneLine() != null) {
					int warning = 0;
					while ((values = reader.readOneLine()) != null) {
						read++;
						if (values.length != table.nbFields()) {
							logger.warn("Attention: nombre de champs insuffisant en ligne: "+(read+1));
							warning++;
						}
						addOneTableRow(table, values, read, 0, erows);
					}
					if (warning > 0) {
						logger.warn("Enregistrements lus: " + read + " Mal formés (CSV): " + warning);
					}
				}
			}
				break;
			case MULTIPLETYPE: {
				String[] values = null;
				while ((values = reader.readOneLine(table.rank)) != null) {
					read++;
					addOneTableRow(table, values, read, 1, erows);
				}
			}
				break;
			case UNIQUETYPE: {
				String[] values = null;
				while ((values = reader.readOneLine()) != null) {
					read++;
					addOneTableRow(table, values, read, 0, erows);
				}
			}
				break;
		}
		etable.add(erows);
		System.out.println("Enregistrements lues: " + read);
	}

	protected static void addOneTableRow(DbTable table, String[] values, int rank, int startCol, Element erows) throws WaarpDatabaseSqlException {
		DbTableRow row = new DbTableRow(table, rank);
		for (int j = startCol; j < values.length; j++) {
			DbFieldValue value = 
					new DbFieldValue(table.getField(j-startCol), values[j]);
			row.addValue(value);
		}
		Element erow = row.getElement();
		erows.add(erow);
	}

}
