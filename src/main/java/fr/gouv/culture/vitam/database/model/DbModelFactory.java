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


import org.waarp.common.database.DbAdmin;
import org.waarp.common.database.exception.WaarpDatabaseNoConnectionException;
import org.waarp.common.database.model.DbType;


/**
 * Factory to store the Database Model object
 * 
 * @author Frederic Bregier
 * 
 */
public class DbModelFactory extends org.waarp.common.database.model.DbModelFactory {
	public static DbVitamModel dbVitamModel = null;
	/**
	 * Initialize the Database Model according to arguments.
	 * 
	 * @param dbdriver
	 * @param dbserver
	 * @param dbuser
	 * @param dbpasswd
	 * @param write
	 * @throws WaarpDatabaseNoConnectionException
	 */
	public static DbAdmin initialize(String dbdriver, String dbserver,
			String dbuser, String dbpasswd, boolean write)
			throws WaarpDatabaseNoConnectionException {
		DbType type = DbType.getFromDriver(dbdriver);
		switch (type) {
			case H2:
				dbModel = dbVitamModel = new DbModelH2(dbserver, dbuser, dbpasswd);
				break;
			case Oracle:
				dbModel = dbVitamModel = new DbModelOracle(dbserver, dbuser, dbpasswd);
				break;
			case PostGreSQL:
				dbModel = dbVitamModel = new DbModelPostgresql();
				break;
			case MySQL:
				dbModel = dbVitamModel = new DbModelMysql(dbserver, dbuser, dbpasswd);
				break;
			default:
				throw new WaarpDatabaseNoConnectionException(
						"TypeDriver unknown: " + type);
		}
		return new DbAdmin(type, dbserver, dbuser, dbpasswd,
				write);
	}

}
