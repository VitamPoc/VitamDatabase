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

import java.sql.Types;


public enum GenericDBType {
	CHAR(Types.CHAR, "CHAR"),
	VARCHAR(Types.VARCHAR, "VARCHAR"),
	LONGVARCHAR(Types.LONGVARCHAR, "LONGVARCHAR"),
	BIT(Types.BIT, "BOOLEAN"),
	BOOLEAN(Types.BIT, "BOOLEAN"),
	TINYINT(Types.TINYINT, "TINYINT"),
	SMALLINT(Types.SMALLINT, "SMALLINT"),
	INTEGER(Types.INTEGER, "INTEGER"),
	BIGINT(Types.BIGINT, "BIGINT"),
	REAL(Types.REAL, "REAL"),
	DOUBLE(Types.DOUBLE, "DOUBLE"),
	VARBINARY(Types.VARBINARY, "BINARY"),
	BINARY(Types.VARBINARY, "BINARY"),
	DATE(Types.DATE, "DATE"),
	TIMESTAMP(Types.TIMESTAMP, "TIMESTAMP"),
	CLOB(Types.CLOB, "CLOB"),
	BLOB(Types.BLOB, "BLOB");

	public int type;

	public String constructor;

	private GenericDBType(int type, String constructor) {
		this.type = type;
		this.constructor = constructor;
	}

	public static int getType(String type) {
		try {
			GenericDBType dbType = GenericDBType.valueOf(type);
			return dbType.type;
		} catch (IllegalArgumentException e) {
			System.err.println("Type unknown: " + type);
			return Types.OTHER; // unknown
		}
	}
	
	public static String getType(int sqltype, int length) {
		switch (sqltype) {
			case Types.CHAR:
				return CHAR.constructor;
			case Types.VARCHAR:
				return VARCHAR.constructor;
			case Types.LONGVARCHAR:
				return LONGVARCHAR.constructor;
			case Types.BIT:
				return BIT.constructor;
			case Types.BIGINT:
			case Types.INTEGER:
			case Types.SMALLINT:
			case Types.TINYINT:
				if (length <= 0) {
					return BIGINT.constructor;
				} else if (length <= 2) {
					return TINYINT.constructor;
				} else if (length <= 4) {
					return SMALLINT.constructor;
				} else if (length <= 9) {
					return INTEGER.constructor;
				} else {
					return BIGINT.constructor;
				}
			case Types.REAL:
				return REAL.constructor;
			case Types.DOUBLE:
				return DOUBLE.constructor;
			case Types.VARBINARY:
				return VARBINARY.constructor;
			case Types.DATE:
				return DATE.constructor;
			case Types.TIMESTAMP:
				return TIMESTAMP.constructor;
			case Types.CLOB:
				return CLOB.constructor;
			case Types.BLOB:
				return BLOB.constructor;
			default:
				return null;
		}
	}
}