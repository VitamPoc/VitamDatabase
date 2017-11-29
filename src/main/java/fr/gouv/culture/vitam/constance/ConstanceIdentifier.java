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
package fr.gouv.culture.vitam.constance;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.waarp.common.database.exception.WaarpDatabaseSqlException;
import org.waarp.common.logging.WaarpInternalLogger;
import org.waarp.common.logging.WaarpInternalLoggerFactory;


import fr.gouv.culture.vitam.database.DbField;
import fr.gouv.culture.vitam.database.DbFieldValue;
import fr.gouv.culture.vitam.database.DbSchema;
import fr.gouv.culture.vitam.database.DbTable;
import fr.gouv.culture.vitam.database.DbTableRow;
import fr.gouv.culture.vitam.reader.LengthBasedDelimiterReader;
import fr.gouv.culture.vitam.reader.MultipleLengthBasedDelimiterReader;
import fr.gouv.culture.vitam.reader.SimpleDelimiterReader;
import fr.gouv.culture.vitam.reader.VitamReader;

/**
 * Constance Identifier: load structure and data, in memory or in database
 * @author "Frederic Bregier"
 * 
 */
public class ConstanceIdentifier {
	/**
	 * Internal Logger
	 */
	private static final WaarpInternalLogger logger = WaarpInternalLoggerFactory
			.getLogger(ConstanceIdentifier.class);
	
	public static final String SEPARATOR = ",";
	public static final String FIXNAME = "fix";
	public static final String CSVNAME = "csv";
	public static final String TYPENAME = "type_";
	public static final String CSVRANG = "RANG ";

	public static enum ConstanceFormat {
		N_FICH, SYMB, POS_DEPA, LONG, POS_ARRI, TYP_DONN, NUM_INF, REF_TABLE, REF_CHAMP, ISPK;
		/**
		 * 
		 * @return the real name as in the file (_ => -)
		 */
		public String getName() {
			return this.name().replace('_', '-');
		}
	}

	public static enum ConstanceStruct {
		CSVTYPE, UNIQUETYPE, MULTIPLETYPE
	}

	public File technicalDescription;
	public ConstanceStruct type;
	public String refDataFile;
	public List<ConstanceField> simpleTypes;
	public List<List<ConstanceField>> multipleTypes;
	public int[] positions;
	public int[][] positionsMultiple;
	public List<String[]> datavalues;
	public String separator;
	public String commonTableName;

	/**
	 * @param technicalDescription
	 * @param separator
	 * @param commonTableName
	 */
	public ConstanceIdentifier(File technicalDescription, String separator, String commonTableName) {
		this.technicalDescription = technicalDescription;
		this.separator = separator;
		this.commonTableName = commonTableName;
	}

	/**
	 * Constructor for XML load
	 */
	public ConstanceIdentifier(File xmlfile, ConstanceStruct struct, String datafile) {
		this.technicalDescription = xmlfile;
		this.type = struct;
		this.refDataFile = datafile;
	}
	
	public void loadTechnicalDescription() throws IOException {
		// Hypothesis: Constance format
		// N-FICH SYMB POS-DEPA LONG POS-ARRI TYP-DONN NUM-INF
		SimpleDelimiterReader reader = new SimpleDelimiterReader(technicalDescription, "\t");
		// first line is only declaring the header, so just check the consistency
		String[] line = reader.readOneLine();
		if (line != null) {
			boolean headerCorrect = true;
			for (int i = 0; i < line.length; i++) {
				if (!line[i].equals(ConstanceFormat.values()[i].getName())) {
					logger.warn("Entête incorrecte: " + line[i]);
					headerCorrect = false;
				}
			}
			if (!headerCorrect) {
				return;
			}
			refDataFile = null;
			simpleTypes = new ArrayList<ConstanceField>();
			// check first line to decide between csv or other
			line = reader.readOneLine();
			if (line != null) {
				if (line.length > 3) {
					type = ConstanceStruct.CSVTYPE;
				}
				if (type == ConstanceStruct.CSVTYPE) {
					while (line != null) {
						if (line[ConstanceFormat.POS_DEPA.ordinal()].startsWith(CSVRANG)) {
							line[ConstanceFormat.POS_DEPA.ordinal()] =
									line[ConstanceFormat.POS_DEPA.ordinal()]
											.substring(CSVRANG.length());
						}
						ConstanceField field = new ConstanceField(line);
						if (refDataFile == null) {
							refDataFile = field.N_FICH.replaceAll(" ", "");
							if (! refDataFile.equals(field.N_FICH)) {
								logger.warn("Attention: le nom du fichier de données contient le caractère ' ' !");
							}
						}
						simpleTypes.add(field);
						line = reader.readOneLine();
					}
				} else {
					// first line is to be ignored: no information
					line = reader.readOneLine();
					List<int[]> tempList = null;
					// now check between simple or multiple
					if (line[ConstanceFormat.POS_DEPA.ordinal()].length() == 0) {
						type = ConstanceStruct.UNIQUETYPE;
					} else {
						type = ConstanceStruct.MULTIPLETYPE;
						multipleTypes = new ArrayList<List<ConstanceField>>();
						tempList = new ArrayList<int[]>();
					}
					// ignore this first line
					line = reader.readOneLine();

					while (line != null) {
						if (type == ConstanceStruct.MULTIPLETYPE &&
								line[ConstanceFormat.SYMB.ordinal()].trim().length() == 0) {
							// new item so backup the previous one
							multipleTypes.add(simpleTypes);
							int[] temp = new int[simpleTypes.size()];
							int i = 0;
							for (ConstanceField field : simpleTypes) {
								temp[i] = field.POS_ARRI;
								i++;
							}
							tempList.add(temp);
							simpleTypes = new ArrayList<ConstanceField>();
							// skip one header line
							line = reader.readOneLine();
						}
						ConstanceField field = new ConstanceField(line);
						if (refDataFile == null) {
							refDataFile = field.N_FICH.replaceAll(" ", "");
							if (! refDataFile.equals(field.N_FICH)) {
								logger.warn("Attention: le nom du fichier de données contient le caractère ' ' !");
							}
						}
						simpleTypes.add(field);
						line = reader.readOneLine();
					}
					if (type == ConstanceStruct.MULTIPLETYPE &&
							!simpleTypes.isEmpty()) {
						multipleTypes.add(simpleTypes);
						int[] temp = new int[simpleTypes.size()];
						int i = 0;
						for (ConstanceField field : simpleTypes) {
							temp[i] = field.POS_ARRI;
							i++;
						}
						tempList.add(temp);
						positionsMultiple = new int[tempList.size()][];
						for (i = 0; i < tempList.size(); i++) {
							positionsMultiple[i] = tempList.get(i);
						}
						tempList.clear();
						tempList = null;
					} else {
						positions = new int[simpleTypes.size()];
						int i = 0;
						for (ConstanceField field : simpleTypes) {
							positions[i] = field.POS_ARRI;
							i++;
						}
					}
				}
			}
		}
	}

	public void printStructure() {
		if (type == null) {
			logger.warn("Identification non prête");
			return;
		}
		switch (type) {
			case CSVTYPE:
				System.out.println("CSV avec " + simpleTypes.size() + " champs");
				for (ConstanceField field : simpleTypes) {
					System.out.println("\t" + field.toString());
				}
				break;
			case MULTIPLETYPE:
				System.out.println("MultipleType avec " + multipleTypes.size() + " types");
				int i = 0;
				for (List<ConstanceField> fields : multipleTypes) {
					System.out.println("\tType: " + i);
					for (int j = 0; j < positionsMultiple[i].length - 1; j++) {
						System.out.print(positionsMultiple[i][j] + SEPARATOR);
					}
					System.out.println(positionsMultiple[i][positionsMultiple[i].length - 1]);
					for (ConstanceField field : fields) {
						System.out.println("\t" + field.toString());
					}
					System.out.println();
					i++;
				}
				break;
			case UNIQUETYPE:
				System.out.println("UniqueType avec " + simpleTypes.size() + " champs");
				for (int j = 0; j < positions.length - 1; j++) {
					System.out.print(positions[j] + SEPARATOR);
				}
				System.out.println(positions[positions.length - 1]);
				for (ConstanceField field : simpleTypes) {
					System.out.println("\t" + field.toString());
				}
				break;
		}
		System.out.println();
	}
	
	public VitamReader getReader() throws IOException {
		VitamReader reader = null;
		switch (type) {
			case CSVTYPE: {
				File data = new File(technicalDescription.getParentFile(), refDataFile);
				reader = new SimpleDelimiterReader(data, separator);
				// first row to ignore
				reader.readOneLine();
			}
				break;
			case MULTIPLETYPE: {
				File data = new File(technicalDescription.getParentFile(), refDataFile);
				reader = new MultipleLengthBasedDelimiterReader(
						data, positionsMultiple);
			}
				break;
			case UNIQUETYPE: {
				File data = new File(technicalDescription.getParentFile(), refDataFile);
				reader = new LengthBasedDelimiterReader(data, positions);
			}
				break;
		}
		return reader;
	}

	public void loadData() throws IOException {
		if (type == null) {
			logger.warn("Identification non prête");
			return;
		}
		datavalues = new ArrayList<String[]>();
		VitamReader reader = getReader();
		boolean csv = type == ConstanceStruct.CSVTYPE;
		String[] values = null;
		int read = 0, warning = 0;
		while ((values = reader.readOneLine()) != null) {
			read++;
			if (csv && values.length != simpleTypes.size()) {
				logger.warn("Attention: une ligne n'a pas le nombre de champs suffisant: " + (read+1));
				warning++;
			}
			datavalues.add(values);
		}
		if (warning > 0) {
			logger.warn("Enregistrements lus: " + read + " Mal formés (CSV): " + warning);
		}
		System.out.println("Enregistrements lus: " + datavalues.size());
	}

	public void printData() {
		if (datavalues == null) {
			logger.warn("Data non prête");
			return;
		}
		System.out.println("Enregistrements lus: " + datavalues.size());
		for (String[] values : datavalues) {
			System.out.print("Type: " + values[0] + " : ");
			for (int i = 1; i < values.length - 1; i++) {
				System.out.print(values[i] + SEPARATOR);
			}
			System.out.println(values[values.length - 1]);
		}
		System.out.println();
	}

	private void loadDbTable(DbSchema schema, DbTable table, List<ConstanceField> fields) {
		schema.addTable(table);
		List<DbField> pks = new ArrayList<DbField>();
		for (int i = 0; i < fields.size(); i++) {
			ConstanceField field = fields.get(i);
			DbField dbfield = new DbField(field.SYMB, field.SYMB, 
					ConstanceDataType.getType(field.TYP_DONN), field.LONG, true, table);
			schema.addField(dbfield);
			table.getFields().add(dbfield);
			if (field.REF_TABLE != null && field.REF_TABLE.length() > 0) {
				String remoteTable = field.REF_TABLE;
				String remoteField = field.REF_CHAMP;
				DbField fieldDst = schema.getField(remoteTable, remoteField);
				if (fieldDst != null) {
					schema.addConstraint(dbfield, fieldDst);
				} else {
					logger.warn("Ne trouve pas le champ de contrainte : " +
							remoteTable + "." + remoteField);
				}
			}
			if (field.ISPK) {
				pks.add(dbfield);
			}
		}
		if (! pks.isEmpty()) {
			schema.addPk(table, pks, pks.get(0).name);
		}
	}
	private void loadDbTableData(DbTable table, List<String []> valuesList) throws WaarpDatabaseSqlException {
		for (int i = 0; i < valuesList.size(); i++) {
			String [] values = valuesList.get(i);
			addOneTableRow(table, values, i+1, 0);
		}
	}
	private void addOneTableRow(DbTable table, String[] values, int rank, int startCol) throws WaarpDatabaseSqlException {
		DbTableRow tableRow = new DbTableRow(table, rank);
		for (int j = startCol; j < values.length; j++) {
			DbFieldValue value = new DbFieldValue(table.getField(j-startCol), values[j]);
			tableRow.addValue(value);
		}
		table.addRow(tableRow);
	}
	private void loadDbTableDataFromFile(DbSchema schema) throws WaarpDatabaseSqlException, IOException {
		int read = 0;
		VitamReader reader = getReader();
		switch (type) {
			case CSVTYPE: {
				String[] values = null;
				int warning = 0;
				DbTable table = schema.getTables().get(0);
				while ((values = reader.readOneLine()) != null) {
					read++;
					if (values.length != simpleTypes.size()) {
						logger.warn("Attention: une ligne n'a pas le nombre de champs suffisant: " + (read+1));
						warning++;
					}
					addOneTableRow(table, values, read, 0);
				}
				if (warning > 0) {
					logger.warn("Enregistrements lus: " + read + " Mal formés (CSV): " + warning);
				}
			}
				break;
			case MULTIPLETYPE: {
				String[] values = null;
				while ((values = reader.readOneLine()) != null) {
					int rank = Integer.parseInt(values[0]);
					DbTable table = schema.getTables().get(rank);
					read++;
					addOneTableRow(table, values, read, 1);
				}
			}
				break;
			case UNIQUETYPE: {
				String[] values = null;
				DbTable table = schema.getTables().get(0);
				while ((values = reader.readOneLine()) != null) {
					read++;
					addOneTableRow(table, values, read, 0);
				}
			}
				break;
		}
		System.out.println("Enregistrements lus: " + read);
	}
	
	public DbSchema getFullSchema() throws WaarpDatabaseSqlException, IOException {
		if (type == null) {
			logger.warn("Structure non prête");
			return null;
		}
		DbSchema schema = getSimpleSchema();
		if (schema != null) {
			loadDataSchema(schema);
		}
		return schema;
	}

	
	public DbSchema getSimpleSchema() throws WaarpDatabaseSqlException, IOException {
		if (type == null) {
			logger.warn("Structure non prête");
			return null;
		}
		String schemaName = technicalDescription.getName();
		int pos = schemaName.lastIndexOf('.');
		if (pos > 0) {
			schemaName = schemaName.substring(0, pos);
		}
		DbSchema schema = new DbSchema(schemaName,
				technicalDescription.getName(), this);
		switch (type) {
			case CSVTYPE: {
				DbTable table = new DbTable(commonTableName+"_"+CSVNAME, CSVNAME, 0, type);
				loadDbTable(schema, table, simpleTypes);
				if (table.datafile == null) {
					table.datafile = this.refDataFile;
				}
				System.out.println("creation ok d'une table "+table.name+
						" avec "+table.nbFields()+" champs");
			}
				break;
			case MULTIPLETYPE: {
				for (int j = 0; j < multipleTypes.size(); j++) {
					List<ConstanceField> list = multipleTypes.get(j);
					DbTable table = new DbTable(commonTableName+"_"+TYPENAME+j, "multiple Rank "+j, j, type);
					loadDbTable(schema, table, list);
					if (table.datafile == null) {
						table.datafile = this.refDataFile;
					}
				}
				System.out.println("creation ok d'un schema "+schema.name+
						" avec "+schema.nbTables()+" tables");
			}
				break;
			case UNIQUETYPE: {
				DbTable table = new DbTable(commonTableName+"_"+FIXNAME, FIXNAME, 0, type);
				loadDbTable(schema, table, simpleTypes);
				if (table.datafile == null) {
					table.datafile = this.refDataFile;
				}
				System.out.println("creation ok d'une table "+table.name+
						" avec "+table.nbFields()+" champs");
			}
				break;
		}
		return schema;
	}

	public void loadDataSchema(DbSchema schema) throws WaarpDatabaseSqlException, IOException {
		if (type == null || schema == null) {
			logger.warn("Structure non prête");
			return;
		}
		switch (type) {
			case CSVTYPE: {
				for (DbTable table : schema.getTables()) {
					if (datavalues == null) {
						loadDbTableDataFromFile(schema);
					} else {
						loadDbTableData(table, datavalues);
					}
					System.out.println("creation ok d'une table "+table.name+
						" avec "+table.nbFields()+" champs et "+
						table.nbRows() + " enregistrements");
				}
			}
				break;
			case MULTIPLETYPE: {
				if (datavalues == null) {
					loadDbTableDataFromFile(schema);
				} else {
					for (int i = 0; i < datavalues.size(); i++) {
						String [] values = datavalues.get(i);
						int rank = Integer.parseInt(values[0]);
						DbTable table = schema.getTables().get(rank);
						addOneTableRow(table, values, i+1, 1);
					}
				}
				System.out.println("creation ok d'un schema "+schema.name+
						" avec "+schema.nbTables()+" tables");
				int totalrows = 0;
				for (DbTable table : schema.getTables()) {
					System.out.println("\tcreation ok d'une table "+table.name+
							" avec "+table.nbFields()+" champs et "+
							table.nbRows() + " enregistrements");
					totalrows += table.nbRows();
				}
				System.out.println("\tLignes lues: " + totalrows);
			}
				break;
			case UNIQUETYPE: {
				for (DbTable table : schema.getTables()) {
					if (datavalues == null) {
						loadDbTableDataFromFile(schema);
					} else {
						loadDbTableData(table, datavalues);
					}
					System.out.println("creation ok d'une table "+table.name+
							" avec "+table.nbFields()+" champs et "+
							table.nbRows() + " enregistrements");
				}
			}
				break;
		}
	}

}
