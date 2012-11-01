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
package fr.gouv.culture.vitam.database;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;

import org.waarp.common.database.DbConstant;
import org.waarp.common.database.DbSession;
import org.waarp.common.database.exception.WaarpDatabaseNoConnectionException;
import org.waarp.common.database.exception.WaarpDatabaseNoDataException;
import org.waarp.common.database.exception.WaarpDatabaseSqlException;
import org.waarp.common.logging.WaarpInternalLogger;
import org.waarp.common.logging.WaarpInternalLoggerFactory;

import fr.gouv.culture.vitam.constance.ConstanceIdentifier;
import fr.gouv.culture.vitam.database.model.DbModelFactory;
import fr.gouv.culture.vitam.database.model.DbSelect;

/**
 * Module to handle transformation from DbVitam to real Database support
 * @author "Frederic Bregier"
 *
 */
public class DbVitam2Database {
	/**
	 * Internal Logger
	 */
	private static final WaarpInternalLogger logger = WaarpInternalLoggerFactory
			.getLogger(DbVitam2Database.class);
	
	public DbVitam2Database(String dbdriver, String dbserver, String dbuser, 
			String dbpasswd) throws WaarpDatabaseNoConnectionException {
		try {
			DbConstant.admin =
					DbModelFactory.initialize(dbdriver, dbserver, dbuser, dbpasswd,
							true);
			logger.info("Database connection: " + (DbConstant.admin != null) + ":"
					+ (DbConstant.noCommitAdmin != null));
		} catch (WaarpDatabaseNoConnectionException e2) {
			logger.error("Unable to Connect to DB", e2);
			throw e2;
		}
	}
	
	public void dropDatabases(DbSchema schema) throws WaarpDatabaseNoDataException, WaarpDatabaseNoConnectionException {
		DbModelFactory.dbVitamModel.dropTables(DbConstant.admin.session, schema);
	}
	public void createDatabases(DbSchema schema) throws WaarpDatabaseNoDataException, WaarpDatabaseNoConnectionException {
		DbModelFactory.dbVitamModel.createTables(DbConstant.admin.session, schema);
	}
	public void fillDatabases(DbSchema schema) throws WaarpDatabaseNoDataException, WaarpDatabaseNoConnectionException, WaarpDatabaseSqlException {
		DbModelFactory.dbVitamModel.fillTables(DbConstant.admin.session, schema);
	}
	public void fillDatabases(DbSchema schema, ConstanceIdentifier identifier) throws WaarpDatabaseNoDataException, WaarpDatabaseNoConnectionException, WaarpDatabaseSqlException, IOException {
		DbModelFactory.dbVitamModel.fillTablesFromFile(DbConstant.admin.session, schema, identifier);
	}
	public int rowCount(DbTable table) {
		return DbModelFactory.dbVitamModel.getRowCount(DbConstant.admin.session, table);
	}

	public List<String[]> getRows(DbTable table, int start, int limit) {
		return DbModelFactory.dbVitamModel.getRows(DbConstant.admin.session, table, start, limit);
	}
	public int rowCount(DbSelect select) throws WaarpDatabaseSqlException {
		return DbModelFactory.dbVitamModel.getRowCount(DbConstant.admin.session, select);
	}
	public List<String[]> getRows(DbSelect select) throws WaarpDatabaseSqlException {
		return DbModelFactory.dbVitamModel.getRows(DbConstant.admin.session, select);
	}
	public int rowCount(String select) throws WaarpDatabaseSqlException {
		return DbModelFactory.dbVitamModel.getRowCount(DbConstant.admin.session, select);
	}
	public List<String[]> getRows(String select, int start, int limit) throws WaarpDatabaseSqlException {
		String action = select;
		if (start > 0 || limit > 0) {
			action = DbModelFactory.dbVitamModel.getLimitOffset(action, start, limit);
		}
		return DbModelFactory.dbVitamModel.getRows(DbConstant.admin.session, action);
	}
	public void close() {
		DbConstant.admin.close();
	}
	/**
	 * Set Value into PreparedStatement
	 * 
	 * @param ps
	 * @param value
	 * @param rank
	 *            >= 1
	 * @throws WaarpDatabaseSqlException
	 */
	static public void setTrueValue(PreparedStatement ps, DbFieldValue value, int rank)
			throws WaarpDatabaseSqlException {
		try {
			switch (value.field.type) {
				case Types.VARCHAR:
					if (value.value == null) {
						ps.setNull(rank, Types.VARCHAR);
						break;
					}
					ps.setString(rank, (String) value.value);
					break;
				case Types.LONGVARCHAR:
					if (value.value == null) {
						ps.setNull(rank, Types.LONGVARCHAR);
						break;
					}
					ps.setString(rank, (String) value.value);
					break;
				case Types.BIT:
					if (value.value == null) {
						ps.setNull(rank, Types.BIT);
						break;
					}
					ps.setBoolean(rank, (Boolean) value.value);
					break;
				case Types.TINYINT:
					if (value.value == null) {
						ps.setNull(rank, Types.TINYINT);
						break;
					}
					ps.setByte(rank, (Byte) value.value);
					break;
				case Types.SMALLINT:
					if (value.value == null) {
						ps.setNull(rank, Types.SMALLINT);
						break;
					}
					ps.setShort(rank, (Short) value.value);
					break;
				case Types.INTEGER:
					if (value.value == null) {
						ps.setNull(rank, Types.INTEGER);
						break;
					}
					ps.setInt(rank, (Integer) value.value);
					break;
				case Types.BIGINT:
					if (value.value == null) {
						ps.setNull(rank, Types.BIGINT);
						break;
					}
					ps.setLong(rank, (Long) value.value);
					break;
				case Types.REAL:
					if (value.value == null) {
						ps.setNull(rank, Types.REAL);
						break;
					}
					ps.setFloat(rank, (Float) value.value);
					break;
				case Types.DOUBLE:
					if (value.value == null) {
						ps.setNull(rank, Types.DOUBLE);
						break;
					}
					ps.setDouble(rank, (Double) value.value);
					break;
				case Types.VARBINARY:
					if (value.value == null) {
						ps.setNull(rank, Types.VARBINARY);
						break;
					}
					ps.setBytes(rank, (byte[]) value.value);
					break;
				case Types.DATE:
					if (value.value == null) {
						ps.setNull(rank, Types.DATE);
						break;
					}
					ps.setDate(rank, (Date) value.value);
					break;
				case Types.TIMESTAMP:
					if (value.value == null) {
						ps.setNull(rank, Types.TIMESTAMP);
						break;
					}
					ps.setTimestamp(rank, (Timestamp) value.value);
					break;
				case Types.CLOB:
					if (value.value == null) {
						ps.setNull(rank, Types.CLOB);
						break;
					}
					ps.setClob(rank, (Reader) value.value);
					break;
				case Types.BLOB:
					if (value.value == null) {
						ps.setNull(rank, Types.BLOB);
						break;
					}
					ps.setBlob(rank, (InputStream) value.value);
					break;
				default:
					throw new WaarpDatabaseSqlException("Type not supported: " +
							value.field.type + " at " + rank);
			}
		} catch (ClassCastException e) {
			throw new WaarpDatabaseSqlException("Setting values casting error: " +
					value.field.type + " at " + rank, e);
		} catch (SQLException e) {
			DbSession.error(e);
			throw new WaarpDatabaseSqlException("Setting values in error: " +
					value.field.type + " at " + rank, e);
		}
	}
}
