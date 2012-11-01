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

import org.waarp.common.database.DbSession;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.waarp.common.database.DbRequest;
import org.waarp.common.database.exception.WaarpDatabaseNoConnectionException;
import org.waarp.common.database.exception.WaarpDatabaseSqlException;
import org.waarp.common.logging.WaarpInternalLogger;
import org.waarp.common.logging.WaarpInternalLoggerFactory;

import fr.gouv.culture.vitam.database.DbField;
import fr.gouv.culture.vitam.database.model.DbSelect;
import fr.gouv.culture.vitam.database.utils.StaticValues;

/**
 * Simple Delimiter Writer (as CSV)
 * @author "Frederic Bregier"
 *
 */
public class SimpleDelimiterWriter {
	/**
	 * Internal Logger
	 */
	private static final WaarpInternalLogger logger = WaarpInternalLoggerFactory
			.getLogger(SimpleDelimiterWriter.class);
	
	public File file;
	public String separator;
	public FileOutputStream outputStream;
	
	/**
	 * @param file
	 * @param separator
	 */
	public SimpleDelimiterWriter(File file, String separator) {
		this.file = file;
		this.separator = separator;
	}

	public void write(DbSession session, DbRequest request) throws WaarpDatabaseSqlException {
		boolean calledFromThere = true;
		try {
			if (outputStream == null) {
				calledFromThere = false;
				outputStream = new FileOutputStream(file);
			}
			StringBuilder builder = new StringBuilder();
			ResultSet global = request.getResultSet();
			ResultSetMetaData metadata = global.getMetaData();
		    int numColumns = metadata.getColumnCount();
			while (request.getNext()) {
				builder.setLength(0);
				ResultSet resultSet = request.getResultSet();
				boolean first = true;
				for (int i = 1; i <= numColumns; i++) {
					if (first) {
						first = false;
					} else {
						builder.append(separator);
					}
					builder.append(resultSet.getString(i));
				}
				builder.append("\n");
				outputStream.write(builder.toString().getBytes());
			}
		} catch (WaarpDatabaseNoConnectionException e) {
			logger.warn(StaticValues.LBL.error_error.get() + e);
		} catch (WaarpDatabaseSqlException e) {
			logger.warn(StaticValues.LBL.error_error.get() + e);
			throw e;
		} catch (SQLException e) {
			logger.warn(StaticValues.LBL.error_error.get() + e);
		} catch (IOException e) {
			logger.warn(StaticValues.LBL.error_error.get() + e);
		} finally {
			if (request != null) {
				request.close();
			}
			if (outputStream != null && ! calledFromThere) {
				try {
					outputStream.close();
				} catch (IOException e) {
				}
				outputStream = null;
			}
		}
	}
	
	public void write(DbSession session, DbSelect select) throws WaarpDatabaseSqlException {
		String action = select.toStringNoLimit();
		DbRequest request = null;
		try {
			outputStream = new FileOutputStream(file);
			StringBuilder builder = new StringBuilder();
			boolean first = true;
			for (DbField field : select.selected) {
				if (first) {
					first = false;
				} else {
					builder.append(separator);
				}
				builder.append(field.toString());
			}
			builder.append("\n");
			outputStream.write(builder.toString().getBytes());
			request = new DbRequest(session);
			request.select(action);
			write(session, request);
		} catch (WaarpDatabaseNoConnectionException e) {
			logger.warn(StaticValues.LBL.error_error.get() + e);
		} catch (WaarpDatabaseSqlException e) {
			logger.warn(StaticValues.LBL.error_error.get() + e);
			throw e;
		} catch (IOException e) {
			logger.warn(StaticValues.LBL.error_error.get() + e);
		} finally {
			if (request != null) {
				request.close();
			}
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
				}
				outputStream = null;
			}
		}
	}

	public void write(DbSession session, String select) throws WaarpDatabaseSqlException {
		String action = select;
		if (select == null) {
			return;
		}
		boolean isSelect = select.toLowerCase().trim().startsWith("select");
		if (!isSelect || action.trim().length() == 0) {
			return;
		}
		DbRequest request = null;
		try {
			outputStream = new FileOutputStream(file);
			request = new DbRequest(session);
			request.select(action);
			ResultSet global = request.getResultSet();
			// Get result set meta data
			ResultSetMetaData metadata = global.getMetaData();
		    int numColumns = metadata.getColumnCount();
			StringBuilder builder = new StringBuilder();
			boolean first = true;
		    // Get the column names; column indices start from 1
		    for (int i=1; i<numColumns+1; i++) {
				if (first) {
					first = false;
				} else {
					builder.append(separator);
				}
		        String columnName = metadata.getColumnName(i);
		        // Get the name of the column's table name
		        String tableName = metadata.getTableName(i);
				builder.append(tableName+"."+columnName);
		    }
			builder.append("\n");
			outputStream.write(builder.toString().getBytes());
			write(session, request);
		} catch (WaarpDatabaseNoConnectionException e) {
			logger.warn(StaticValues.LBL.error_error.get() + e);
		} catch (WaarpDatabaseSqlException e) {
			logger.warn(StaticValues.LBL.error_error.get() + e);
			throw e;
		} catch (SQLException e) {
			logger.warn(StaticValues.LBL.error_error.get() + e);
		} catch (IOException e) {
			logger.warn(StaticValues.LBL.error_error.get() + e);
		} finally {
			if (request != null) {
				request.close();
			}
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
				}
				outputStream = null;
			}
		}
	}
}
