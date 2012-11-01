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
import java.sql.Types;
import java.util.List;

import org.waarp.common.database.DbConstant;
import org.waarp.common.database.DbRequest;
import org.waarp.common.database.DbSession;
import org.waarp.common.database.exception.WaarpDatabaseNoConnectionException;
import org.waarp.common.database.exception.WaarpDatabaseNoDataException;
import org.waarp.common.database.exception.WaarpDatabaseSqlException;
import org.waarp.common.logging.WaarpInternalLogger;
import org.waarp.common.logging.WaarpInternalLoggerFactory;

import fr.gouv.culture.vitam.constance.ConstanceIdentifier;
import fr.gouv.culture.vitam.database.DbField;
import fr.gouv.culture.vitam.database.DbSchema;
import fr.gouv.culture.vitam.database.DbTable;
import fr.gouv.culture.vitam.database.utils.StaticValues;

/**
 * Oracle Database Model implementation
 * 
 * @author Frederic Bregier
 * 
 */
public class DbModelOracle extends org.waarp.common.database.model.DbModelOracle implements DbVitamModel {
	/**
	 * Internal Logger
	 */
	private static final WaarpInternalLogger logger = WaarpInternalLoggerFactory
			.getLogger(DbModelOracle.class);
	
	private static final String dropTableOracle = "DROP TABLE ";
	private static final String dropIndexOracle = "DROP INDEX ";
	private static final String cascade = " CASCADE";
	private static final String createTableOracle = "CREATE TABLE ";
	private static final String createIdxOracle = "CREATE INDEX ";
	private static final String primaryKey = " PRIMARY KEY ";
	private static final String notNull = " NOT NULL ";
	/**
	 * Create the object and initialize if necessary the driver
	 * 
	 * @param dbserver
	 * @param dbuser
	 * @param dbpasswd
	 * @throws WaarpDatabaseNoConnectionException
	 */
	public DbModelOracle(String dbserver, String dbuser, String dbpasswd)
			throws WaarpDatabaseNoConnectionException {
		super(dbserver, dbuser, dbpasswd);
	}

	@Override
	public void dropTables(DbSession session, DbSchema schema) throws WaarpDatabaseNoDataException,
			WaarpDatabaseNoConnectionException {
		if (schema.constructionOrder == null) {
			schema.createBuildOrder();
		}
		for (String tableName : schema.constructionOrder) {
			DbTable table = schema.getTable(tableName);
			DbRequest request = new DbRequest(session);
			String action = dropTableOracle + table.name + cascade;
			System.out.println(action);
			try {
				request.query(action);
			} catch (WaarpDatabaseNoConnectionException e) {
				logger.warn(StaticValues.LBL.error_error.get() + e);
				return;
			} catch (WaarpDatabaseSqlException e) {
				logger.warn(StaticValues.LBL.error_error.get() + e);
				return;
			} finally {
				request.close();
			}
			String primary = null;
			if (table.primaryKeys != null && ! table.primaryKeys.isEmpty()) {
				primary = table.name+"_pk";
				action = dropIndexOracle + primary;
				System.out.println(action);
				try {
					request.query(action);
				} catch (WaarpDatabaseNoConnectionException e) {
					logger.warn(StaticValues.LBL.error_error.get() + e);
					return;
				} catch (WaarpDatabaseSqlException e) {
					return;
				} finally {
					request.close();
				}
			}
			if (table.indexes != null && ! table.indexes.isEmpty()) {
				for (String idxname : table.indexes.keySet()) {
					action = dropIndexOracle + idxname;
					System.out.println(action);
					try {
						request.query(action);
					} catch (WaarpDatabaseNoConnectionException e) {
						logger.warn(StaticValues.LBL.error_error.get() + e);
						return;
					} catch (WaarpDatabaseSqlException e) {
						return;
					} finally {
						request.close();
					}
				}
			}
		}
	}

	@Override
	public void createTables(DbSession session, DbSchema schema) throws WaarpDatabaseNoDataException, WaarpDatabaseNoConnectionException  {
		if (schema.constructionOrder == null) {
			schema.createBuildOrder();
		}
		for (String tableName : schema.constructionOrder) {
			DbTable table = schema.getTable(tableName);
			DbRequest request = new DbRequest(session);
			String action = dropTableOracle + table.name + cascade;
			System.out.println(action);
			try {
				request.query(action);
			} catch (WaarpDatabaseNoConnectionException e) {
				logger.warn(StaticValues.LBL.error_error.get() + e);
				return;
			} catch (WaarpDatabaseSqlException e) {
				logger.warn(StaticValues.LBL.error_error.get() + e);
				return;
			} finally {
				request.close();
			}
			List<DbField> fields = table.getFields();
			// FOREIGN KEY(ID) REFERENCES TEST(ID)
			String constraints = null;
			if (table.constraints != null && ! table.constraints.isEmpty()) {
				constraints = "";
				for (String constraint: table.constraints.keySet()) {
					DbField source = schema.getField(tableName, constraint);
					DbField remote = table.constraints.get(constraint);
					if (source != null && remote != null) {
						constraints += " FOREIGN KEY("+source.name+") REFERENCES "+
								remote.table.name+"("+remote.name+"),";
					}
				}
				if (constraints.length() > 0) {
					constraints = constraints.substring(0, constraints.length()-1) + " ";
				}
			}
			String primary = null;
			if (table.primaryKeys != null && ! table.primaryKeys.isEmpty()) {
				primary = table.name+"_pk" + primaryKey + "(";
				for (int i = 0; i < table.primaryKeys.size(); i++) {
					DbField field = table.primaryKeys.get(i);
					primary += field.name + ",";
				}
				primary = primary.substring(0, primary.length()-1);
				primary += ") ";
				action = dropIndexOracle + table.name+"_pk";
				System.out.println(action);
				try {
					request.query(action);
				} catch (WaarpDatabaseNoConnectionException e) {
					logger.warn(StaticValues.LBL.error_error.get() + e);
					return;
				} catch (WaarpDatabaseSqlException e) {
					return;
				} finally {
					request.close();
				}
			}
			action = createTableOracle + table.name + "(";
			for (int i = 0; i < fields.size(); i++) {
				DbField field = fields.get(i);
				action += field.name;
				if (field.length > 0) {
					switch (field.type) {
						case Types.VARCHAR:
						case Types.LONGVARCHAR:
							if (field.length == 0) {
								action += " VARCHAR2(250) ";
							} else {
								action += " VARCHAR2("+field.length+") ";
							}
							break;
						default:
							action += DBType.getType(field.type);
					}
				} else {
					action += DBType.getType(field.type);
				}
				if (! field.nullable) {
					action += notNull;
				}
				action += ",";
			}
			if (primary == null && constraints == null) {
				if (fields.size() > 0) {
					action = action.substring(0, action.length()-1);
				}
				action += " )";
			} else {
				if (primary == null) {
					action += " CONSTRAINT " + constraints;
				} else if (constraints == null) {
					action += " CONSTRAINT " + primary;
				} else {
					action += " CONSTRAINT " + primary + ", " + constraints;
				}
				action += " )";
			}
			System.out.println(action);
			try {
				request.query(action);
			} catch (WaarpDatabaseNoConnectionException e) {
				logger.warn(StaticValues.LBL.error_error.get() + e);
				return;
			} catch (WaarpDatabaseSqlException e) {
				logger.warn(StaticValues.LBL.error_error.get() + e);
				return;
			} finally {
				request.close();
			}
			if (table.indexes != null && ! table.indexes.isEmpty()) {
				for (String idxname : table.indexes.keySet()) {
					action = dropIndexOracle + idxname;
					System.out.println(action);
					try {
						request.query(action);
					} catch (WaarpDatabaseNoConnectionException e) {
						logger.warn(StaticValues.LBL.error_error.get() + e);
						return;
					} catch (WaarpDatabaseSqlException e) {
						return;
					} finally {
						request.close();
					}
					action = createIdxOracle+idxname+" ON " + table.name + "(";
					List<DbField> list = table.indexes.get(idxname);
					for (int i = 0; i < list.size() - 1; i++) {
						DbField field = list.get(i);
						action += field.name + ", ";
					}
					DbField field = list.get(list.size()-1);
					action += field.name + ")";
					System.out.println(action);
					try {
						request.query(action);
					} catch (WaarpDatabaseNoConnectionException e) {
						logger.warn(StaticValues.LBL.error_error.get() + e);
						return;
					} catch (WaarpDatabaseSqlException e) {
						return;
					} finally {
						request.close();
					}
				}
			}
		}
	}
	
	@Override
	public void fillTables(DbSession session, DbSchema schema) throws WaarpDatabaseNoDataException, WaarpDatabaseNoConnectionException, WaarpDatabaseSqlException  {
		AbstractDbVitamModel.fillTables(session, schema);
	}

	public void fillTablesFromFile(DbSession session, DbSchema schema, 
			ConstanceIdentifier identifier) 
					throws WaarpDatabaseNoDataException, WaarpDatabaseNoConnectionException, WaarpDatabaseSqlException, IOException  {
		AbstractDbVitamModel.fillTablesFromFile(session, schema, identifier);
	}

	@Override
	public int getRowCount(DbSession session, DbTable table) {
		return AbstractDbVitamModel.getRowCount(session, table);
	}
	
	public String getLimitOffset(String action, int start, int limit) {
		String result = action;
		if (limit > 0) {
			if (start > 0) {
				return "SELECT * FROM ("+action+
						") WHERE ROWNUM BETWEEN " + start + " AND " + (start+limit);
			}
			return "SELECT * FROM ("+action+") WHERE ROWNUM <= " + limit;
		}
		return result;
	}

	public List<String[]> getRows(DbSession session, DbTable table, int start, int limit) {
		return AbstractDbVitamModel.getRows(session, table, start, limit);
	}
	public int getRowCount(DbSession session, DbSelect select) throws WaarpDatabaseSqlException {
		return AbstractDbVitamModel.getRowCount(session, select);
	}

	public List<String[]> getRows(DbSession session, DbSelect select) throws WaarpDatabaseSqlException {
		return AbstractDbVitamModel.getRows(session, select);
	}

	@Override
	public int getRowCount(DbSession session, String select) throws WaarpDatabaseSqlException {
		return AbstractDbVitamModel.getRowCount(session, select);
	}


	@Override
	public List<String[]> getRows(DbSession session, String select)
			throws WaarpDatabaseSqlException {
		return AbstractDbVitamModel.getRows(session, select);
	}

	@Override
	public void createTables(DbSession session) throws WaarpDatabaseNoConnectionException {
		// Do nothing 
	}

	@Override
	public void resetSequence(DbSession session, long newvalue)
			throws WaarpDatabaseNoConnectionException {
		// Do nothing since no sequence
	}

	@Override
	public long nextSequence(DbSession dbSession)
			throws WaarpDatabaseNoConnectionException,
			WaarpDatabaseSqlException, WaarpDatabaseNoDataException {
		// Do nothing since no sequence
		return DbConstant.ILLEGALVALUE;
	}
}
