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
package fr.gouv.culture.vitam.database.model;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.waarp.common.database.DbPreparedStatement;
import org.waarp.common.database.DbRequest;
import org.waarp.common.database.DbSession;
import org.waarp.common.database.exception.WaarpDatabaseNoConnectionException;
import org.waarp.common.database.exception.WaarpDatabaseNoDataException;
import org.waarp.common.database.exception.WaarpDatabaseSqlException;
import org.waarp.common.logging.WaarpInternalLogger;
import org.waarp.common.logging.WaarpInternalLoggerFactory;

import fr.gouv.culture.vitam.constance.ConstanceIdentifier;
import fr.gouv.culture.vitam.database.DbField;
import fr.gouv.culture.vitam.database.DbFieldValue;
import fr.gouv.culture.vitam.database.DbSchema;
import fr.gouv.culture.vitam.database.DbTable;
import fr.gouv.culture.vitam.database.DbTableRow;
import fr.gouv.culture.vitam.database.DbVitam2Database;
import fr.gouv.culture.vitam.database.utils.StaticValues;
import fr.gouv.culture.vitam.reader.VitamReader;

/**
 * Abstract common for all DB implementation
 * @author "Frederic Bregier"
 *
 */
public class AbstractDbVitamModel {
	public static int TIMEOUT_IN_SECOND = 30;
	/**
	 * Internal Logger
	 */
	private static final WaarpInternalLogger logger = WaarpInternalLoggerFactory
			.getLogger(AbstractDbVitamModel.class);
	
	protected static void loadDbTableDataFromFile(DbPreparedStatement preparedStatement, 
			DbSchema schema, ConstanceIdentifier identifier, String tableName) 
					throws WaarpDatabaseSqlException, IOException, WaarpDatabaseNoConnectionException, WaarpDatabaseNoDataException {
		int read = 0;
		VitamReader reader = identifier.getReader();
		switch (identifier.type) {
			case CSVTYPE: {
				String[] values = null;
				int warning = 0;
				DbTable table = schema.getTable(tableName);
				while ((values = reader.readOneLine()) != null) {
					read++;
					if (values.length != identifier.simpleTypes.size()) {
						logger.warn("Attention: nombre de champs insuffisant en ligne: "+(read+1));
						warning++;
					}
					addOneTableRow(preparedStatement, table, values, read, 0);
				}
				if (warning > 0) {
					logger.warn("Enregistrements lus: " + read + " Mal formés (CSV): " + warning);
				}
			}
				break;
			case MULTIPLETYPE: {
				String[] values = null;
				DbTable table = schema.getTable(tableName);
				while ((values = reader.readOneLine(table.rank)) != null) {
					read++;
					addOneTableRow(preparedStatement, table, values, read, 1);
				}
			}
				break;
			case UNIQUETYPE: {
				String[] values = null;
				DbTable table = schema.getTable(tableName);
				while ((values = reader.readOneLine()) != null) {
					read++;
					addOneTableRow(preparedStatement, table, values, read, 0);
				}
			}
				break;
		}
		System.out.println("Enregistrements lus: " + read);
	}
	
	protected static void addOneTableRow(DbPreparedStatement preparedStatement,
			DbTable table, String[] values, int rank, int startCol) 
					throws WaarpDatabaseSqlException, WaarpDatabaseNoConnectionException, WaarpDatabaseNoDataException {
		PreparedStatement prStatement = 
				preparedStatement.getPreparedStatement();
		for (int j = startCol; j < values.length; j++) {
			DbFieldValue value = 
					new DbFieldValue(table.getField(j-startCol), values[j]);
			DbVitam2Database.setTrueValue(prStatement, value, j-startCol+1);
		}
		int count = preparedStatement.executeUpdate();
		if (count <= 0) {
			throw new WaarpDatabaseNoDataException("No row inserted");
		}
	}

	protected static final void fillTables(DbSession session, DbSchema schema) throws WaarpDatabaseNoDataException, WaarpDatabaseNoConnectionException, WaarpDatabaseSqlException  {
		if (schema.constructionOrder == null) {
			schema.createBuildOrder();
		}
		for (String tableName : schema.constructionOrder) {
			DbTable table = schema.getTable(tableName);
			String action = "INSERT INTO " + table.name + "(";
			String fields = "";
			String future = "";
			for (DbField field : table.getFields()) {
				fields += field.name + ",";
				future += "?,";
			}
			fields = fields.substring(0, fields.length()-1);
			future = future.substring(0, future.length()-1);
			action += fields + ") VALUES(" + future + ")";
			DbPreparedStatement preparedStatement = new DbPreparedStatement(session);
			preparedStatement.createPrepareStatement(action);
			for (DbTableRow tableRow : table.getRows()) {
				int i = 0;
				for (DbFieldValue value : tableRow.getValues()) {
					PreparedStatement prStatement = 
							preparedStatement.getPreparedStatement();
					i++;
					DbVitam2Database.setTrueValue(prStatement, value, i);
				}
				int count = preparedStatement.executeUpdate();
				if (count <= 0) {
					throw new WaarpDatabaseNoDataException("No row inserted");
				}
			}
		}
	}

	protected static final void fillTablesFromFile(DbSession session, DbSchema schema, 
			ConstanceIdentifier identifier) 
					throws WaarpDatabaseNoDataException, WaarpDatabaseNoConnectionException, WaarpDatabaseSqlException, IOException  {
		if (schema.constructionOrder == null) {
			schema.createBuildOrder();
		}
		for (String tableName : schema.constructionOrder) {
			DbTable table = schema.getTable(tableName);
			String action = "INSERT INTO " + table.name + "(";
			String fields = "";
			String future = "";
			for (DbField field : table.getFields()) {
				fields += field.name + ",";
				future += "?,";
			}
			//System.out.println(table.name + " : "+ fields +" : " + future);
			fields = fields.substring(0, fields.length()-1);
			future = future.substring(0, future.length()-1);
			action += fields + ") VALUES(" + future + ")";
			DbPreparedStatement preparedStatement = new DbPreparedStatement(session);
			try {
				preparedStatement.createPrepareStatement(action);
				
				loadDbTableDataFromFile(preparedStatement, schema, identifier, table.name);
			} finally {
				preparedStatement.realClose();
			}
		}
	}
	/**
	 * 
	 * @param session
	 * @param table
	 * @return the number of rows within the table
	 */
	protected static final int getRowCount(DbSession session, DbTable table) {
		String action = "SELECT COUNT(*) FROM " + table.name;
		DbRequest request = null;
		try {
			request = new DbRequest(session);
			request.select(action);
			if (request.getNext()) {
				ResultSet resultSet = request.getResultSet();
				return resultSet.getInt(1);
			}
			return -1;
		} catch (WaarpDatabaseNoConnectionException e) {
			logger.warn(StaticValues.LBL.error_error.get() + e);
			return -1;
		} catch (WaarpDatabaseSqlException e) {
			logger.warn(StaticValues.LBL.error_error.get() + e);
			return -1;
		} catch (SQLException e) {
			logger.warn(StaticValues.LBL.error_error.get() + e);
			return -1;
		} finally {
			if (request != null) {
				request.close();
			}
		}
	}


	protected static final  List<String[]> getRows(DbSession session, DbTable table, int start, int limit) {
		String action = "SELECT * FROM " + table.name;
		action = DbModelFactory.dbVitamModel.getLimitOffset(action, start, limit);
		DbRequest request = null;
		try {
			request = new DbRequest(session);
			request.select(action);
			List<String[]> list = new ArrayList<String[]>();
			while (request.getNext()) {
				ResultSet resultSet = request.getResultSet();
				String [] result = new String[table.nbFields()];
				for (int i = 1; i <= result.length; i++) {
					result[i-1] = resultSet.getString(i);
				}
				list.add(result);
			}
			return list;
		} catch (WaarpDatabaseNoConnectionException e) {
			logger.warn(StaticValues.LBL.error_error.get() + e);
			return null;
		} catch (WaarpDatabaseSqlException e) {
			logger.warn(StaticValues.LBL.error_error.get() + e);
			return null;
		} catch (SQLException e) {
			logger.warn(StaticValues.LBL.error_error.get() + e);
			return null;
		} finally {
			if (request != null) {
				request.close();
			}
		}
	}
	private static final int LIMITCOUNTROW = 5000;
	/**
	 * 
	 * @param session
	 * @param select
	 * @return the number of rows within the select
	 * @throws WaarpDatabaseSqlException 
	 */
	protected static final int getRowCount(DbSession session, DbSelect select) throws WaarpDatabaseSqlException {
		int start = select.offset;
		int limit = select.limit;
		select.offset = 0;
		select.limit = LIMITCOUNTROW;
		String action = "SELECT COUNT(*) FROM (" + DbModelFactory.dbVitamModel.getLimitOffset(select.toStringNoLimitNoOrder(),0,LIMITCOUNTROW) + ")";
		select.offset = start;
		select.limit = limit;
		
		DbRequest request = null;
		try {
			request = new DbRequest(session);
			request.select(action, TIMEOUT_IN_SECOND);
			if (request.getNext()) {
				ResultSet resultSet = request.getResultSet();
				int result = resultSet.getInt(1);
				if (result != LIMITCOUNTROW) {
					return result;
				}
			}
			return -1;
		} catch (WaarpDatabaseNoConnectionException e) {
			logger.warn(StaticValues.LBL.error_error.get() + e);
			return -1;
		} catch (WaarpDatabaseSqlException e) {
			logger.warn(StaticValues.LBL.error_error.get() + e);
			throw e;
		} catch (SQLException e) {
			logger.warn(StaticValues.LBL.error_error.get() + e);
			return -1;
		} finally {
			if (request != null) {
				request.close();
			}
		}
	}

	protected static final List<String[]> getRows(DbSession session, DbSelect select) throws WaarpDatabaseSqlException {
		String action = select.toString();
		DbRequest request = null;
		try {
			request = new DbRequest(session);
			request.select(action, TIMEOUT_IN_SECOND);
			List<String[]> list = new ArrayList<String[]>();
			while (request.getNext()) {
				ResultSet resultSet = request.getResultSet();
				String [] result = new String[select.selected.size()];
				for (int i = 1; i <= result.length; i++) {
					result[i-1] = resultSet.getString(i);
				}
				list.add(result);
			}
			return list;
		} catch (WaarpDatabaseNoConnectionException e) {
			logger.warn(StaticValues.LBL.error_error.get() + e);
			return null;
		} catch (WaarpDatabaseSqlException e) {
			logger.warn(StaticValues.LBL.error_error.get() + e);
			throw e;
		} catch (SQLException e) {
			logger.warn(StaticValues.LBL.error_error.get() + e);
			return null;
		} finally {
			if (request != null) {
				request.close();
			}
		}
	}

	/**
	 * 
	 * @param session
	 * @param select
	 * @return the number of rows within the select
	 * @throws WaarpDatabaseSqlException 
	 */
	protected static final int getRowCount(DbSession session, String select) throws WaarpDatabaseSqlException {
		if (select == null) {
			return -1;
		}
		if (select.trim().length() == 0) {
			return -1;
		}
		if (! select.toLowerCase().trim().startsWith("select")) {
			return -1;
		}
		String action = "SELECT COUNT(*) FROM (" + DbModelFactory.dbVitamModel.getLimitOffset(select,0,LIMITCOUNTROW) + ")";
		
		DbRequest request = null;
		try {
			request = new DbRequest(session);
			request.select(action, TIMEOUT_IN_SECOND);
			if (request.getNext()) {
				ResultSet resultSet = request.getResultSet();
				int result = resultSet.getInt(1);
				if (result != LIMITCOUNTROW) {
					return result;
				}
			}
			return -1;
		} catch (WaarpDatabaseNoConnectionException e) {
			logger.warn(StaticValues.LBL.error_error.get() + e);
			return -1;
		} catch (WaarpDatabaseSqlException e) {
			logger.warn(StaticValues.LBL.error_error.get() + e);
			throw e;
		} catch (SQLException e) {
			logger.warn(StaticValues.LBL.error_error.get() + e);
			return -1;
		} finally {
			if (request != null) {
				request.close();
			}
		}
	}
	/**
	 * 
	 * @param session
	 * @param select
	 * @return a List of Array of Strings, the first rank being the field names, then the values. 
	 * 		In case of Insert/Update/Delete, will be "QueryCount" then the count of lines touched.
	 * @throws WaarpDatabaseSqlException
	 */
	protected static final List<String[]> getRows(DbSession session, String select) throws WaarpDatabaseSqlException {
		String action = select;
		if (select == null) {
			return null;
		}
		boolean isSelect = select.toLowerCase().trim().startsWith("select");
		if (action.trim().length() == 0) {
			return null;
		}
		DbRequest request = null;
		try {
			request = new DbRequest(session);
			if (isSelect) {
				request.select(action, TIMEOUT_IN_SECOND);
				List<String[]> list = new ArrayList<String[]>();
				ResultSet global = request.getResultSet();
				// Get result set meta data
				ResultSetMetaData metadata = global.getMetaData();
			    int numColumns = metadata.getColumnCount();
			    String [] header = new String[numColumns];
			    // Get the column names; column indices start from 1
			    for (int i=1; i<numColumns+1; i++) {
			        String columnName = metadata.getColumnName(i);
			        // Get the name of the column's table name
			        String tableName = metadata.getTableName(i);
			        header[i-1] = tableName+"."+columnName;
			    }
			    list.add(header);
				while (request.getNext()) {
					ResultSet resultSet = request.getResultSet();
					String [] result = new String[numColumns];
					for (int i = 1; i <= result.length; i++) {
						result[i-1] = resultSet.getString(i);
					}
					list.add(result);
				}
				return list;
			} else {
				// update, insert, delete
				int count = request.query(action);
				List<String[]> list = new ArrayList<String[]>();
				String [] header = new String [] { "QueryCount" };
				String [] value = new String [] { ""+count };
				list.add(header);
				list.add(value);
				return list;
			}
		} catch (WaarpDatabaseNoConnectionException e) {
			logger.warn(StaticValues.LBL.error_error.get() + e);
			return null;
		} catch (WaarpDatabaseSqlException e) {
			logger.warn(StaticValues.LBL.error_error.get() + e);
			throw e;
		} catch (SQLException e) {
			logger.warn(StaticValues.LBL.error_error.get() + e);
			return null;
		} finally {
			if (request != null) {
				request.close();
			}
		}
	}

}
