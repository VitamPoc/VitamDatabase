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
import java.util.List;

import org.waarp.common.database.DbSession;
import org.waarp.common.database.exception.WaarpDatabaseNoConnectionException;
import org.waarp.common.database.exception.WaarpDatabaseNoDataException;
import org.waarp.common.database.exception.WaarpDatabaseSqlException;
import org.waarp.common.database.model.DbModel;

import fr.gouv.culture.vitam.constance.ConstanceIdentifier;
import fr.gouv.culture.vitam.database.DbSchema;
import fr.gouv.culture.vitam.database.DbTable;

/**
 * @author "Frederic Bregier"
 *
 */
public interface DbVitamModel extends DbModel {
	/**
	 * Drop tables and indexes
	 * @param session
	 * @param schema
	 * @throws WaarpDatabaseNoDataException
	 * @throws WaarpDatabaseNoConnectionException
	 */
	public void dropTables(DbSession session, DbSchema schema) throws WaarpDatabaseNoDataException, WaarpDatabaseNoConnectionException;
	/**
	 * Drop tables and indexes and create them
	 * @param session
	 * @param schema
	 * @throws WaarpDatabaseNoDataException
	 * @throws WaarpDatabaseNoConnectionException
	 */
	public void createTables(DbSession session, DbSchema schema) throws WaarpDatabaseNoDataException, WaarpDatabaseNoConnectionException;
	/**
	 * Fill in tables
	 * @param session
	 * @param schema
	 * @throws WaarpDatabaseNoDataException
	 * @throws WaarpDatabaseNoConnectionException
	 * @throws WaarpDatabaseSqlException
	 */
	public void fillTables(DbSession session, DbSchema schema) throws WaarpDatabaseNoDataException, WaarpDatabaseNoConnectionException, WaarpDatabaseSqlException;
	/**
	 * Fill in tables from file directly
	 * @param session
	 * @param schema
	 * @param identifier
	 * @throws WaarpDatabaseNoDataException
	 * @throws WaarpDatabaseNoConnectionException
	 * @throws WaarpDatabaseSqlException
	 * @throws IOException
	 */
	public void fillTablesFromFile(DbSession session, DbSchema schema, 
			ConstanceIdentifier identifier) 
					throws WaarpDatabaseNoDataException, WaarpDatabaseNoConnectionException, WaarpDatabaseSqlException, IOException;
	/**
	 * 
	 * @param session
	 * @param table
	 * @return the number of rows within the table or -1 if an error occurs
	 */
	public int getRowCount(DbSession session, DbTable table);
	/**
	 * 
	 * @param session
	 * @param table
	 * @param start
	 * @param limit
	 * @return the list of limit rows starting at rank start, or null if an error occurs
	 */
	public List<String[]> getRows(DbSession session, DbTable table, int start, int limit);
	/**
	 * 
	 * @param session
	 * @param select
	 * @return the number of rows within the select or -1 if an error occurs
	 */
	public int getRowCount(DbSession session, DbSelect select) throws WaarpDatabaseSqlException;
	/**
	 * 
	 * @param session
	 * @param select
	 * @return the list of rows according to select, or null if an error occurs
	 */
	public List<String[]> getRows(DbSession session, DbSelect select) throws WaarpDatabaseSqlException;
	/**
	 * 
	 * @param session
	 * @param select
	 * @return the number of rows within the select or -1 if an error occurs
	 */
	public int getRowCount(DbSession session, String select) throws WaarpDatabaseSqlException;
	/**
	 * 
	 * @param session
	 * @param select
	 * @return a List of Array of Strings, the first rank being the field names, then the values. 
	 * 		In case of Insert/Update/Delete, will be "QueryCount" then the count of lines touched.
	 * @throws WaarpDatabaseSqlException
	 */
	public List<String[]> getRows(DbSession session, String select) throws WaarpDatabaseSqlException;
	/**
	 * @param sql
	 * @param start
	 * @param limit
	 * @return the condition to limit rows starting at rank start
	 */
	public String getLimitOffset(String sql, int start, int limit);
	
	public String validConnectionString();
}
