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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Date;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.DateFormat;
import java.text.ParseException;

import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.waarp.common.database.exception.WaarpDatabaseSqlException;

/**
 * DbField Value
 * @author "Frederic Bregier"
 * 
 */
public class DbFieldValue {
	public DbField field;
	public Object value;

	public DbFieldValue(DbField field) {
		this.field = field;
	}

	public DbFieldValue(DbField field, String value) throws WaarpDatabaseSqlException {
		this.field = field;
		if (value != null) {
			setValueFromString(value);
		} else {
			this.value = null;
		}
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setValue(boolean value) {
		this.value = value;
	}

	public void setValue(byte value) {
		this.value = value;
	}

	public void setValue(short value) {
		this.value = value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public void setValue(long value) {
		this.value = value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public void setValue(byte[] value) {
		this.value = value;
	}

	public void setValue(Date value) {
		this.value = value;
	}

	public void setValue(Timestamp value) {
		this.value = value;
	}

	public void setValue(java.util.Date value) {
		this.value = new Timestamp(value.getTime());
	}

	public void setValue(Reader value) {
		this.value = value;
	}

	public void setValue(InputStream value) {
		this.value = value;
	}

	public Object getValue() throws WaarpDatabaseSqlException {
		switch (field.type) {
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
			case Types.BIT:
			case Types.TINYINT:
			case Types.SMALLINT:
			case Types.INTEGER:
			case Types.BIGINT:
			case Types.REAL:
			case Types.DOUBLE:
			case Types.VARBINARY:
			case Types.DATE:
			case Types.TIMESTAMP:
			case Types.CLOB:
			case Types.BLOB:
				return value;
			default:
				throw new WaarpDatabaseSqlException("Type inconnu: " + field.type);
		}
	}

	public String getValueAsString() throws WaarpDatabaseSqlException {
		if (value == null) {
			return null;
		}
		switch (field.type) {
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
				return (String) value;
			case Types.BIT:
				return ((Boolean) value).toString();
			case Types.TINYINT:
				return ((Byte) value).toString();
			case Types.SMALLINT:
				return ((Short) value).toString();
			case Types.INTEGER:
				return ((Integer) value).toString();
			case Types.BIGINT:
				return ((Long) value).toString();
			case Types.REAL:
				return ((Float) value).toString();
			case Types.DOUBLE:
				return ((Double) value).toString();
			case Types.VARBINARY:
				return new String((byte[]) value);
			case Types.DATE:
				return ((Date) value).toString();
			case Types.TIMESTAMP:
				return ((Timestamp) value).toString();
			case Types.CLOB: {
				StringBuilder sBuilder = new StringBuilder();
				Reader reader = ((Reader) value);
				char[] cbuf = new char[4096];
				int len;
				try {
					len = reader.read(cbuf);
					while (len > 0) {
						sBuilder.append(cbuf, 0, len);
						len = reader.read(cbuf);
					}
				} catch (IOException e) {
					throw new WaarpDatabaseSqlException("Error while reading Clob as String", e);
				}
				return sBuilder.toString();
			}
			case Types.BLOB: {
				InputStream reader = ((InputStream) value);
				try {
					return new java.util.Scanner(reader).useDelimiter("\\A").next();
				} catch (java.util.NoSuchElementException e) {
					throw new WaarpDatabaseSqlException("Error while reading Clob as String", e);
				}
			}
			default:
				throw new WaarpDatabaseSqlException("Type inconnu: " + field.type);
		}
	}

	public void setValueFromString(String svalue) throws WaarpDatabaseSqlException {
		try {
			switch (field.type) {
				case Types.VARCHAR:
				case Types.LONGVARCHAR:
					value = svalue;
					break;
				case Types.BIT:
					if (svalue.trim().length() == 0) {
						value = null;
					} else {
						value = Boolean.parseBoolean(svalue);
					}
					break;
				case Types.TINYINT:
					if (svalue.trim().length() == 0) {
						value = null;
					} else {
						value = Byte.parseByte(svalue.replace("+", ""));
					}
					break;
				case Types.SMALLINT:
					if (svalue.trim().length() == 0) {
						value = null;
					} else {
						value = Short.parseShort(svalue.replace("+", ""));
					}
					break;
				case Types.INTEGER:
					if (svalue.trim().length() == 0) {
						value = null;
					} else {
						value = Integer.parseInt(svalue.replace("+", ""));
					}
					break;
				case Types.BIGINT:
					if (svalue.trim().length() == 0) {
						value = null;
					} else {
						value = Long.parseLong(svalue.replace("+", ""));
					}
					break;
				case Types.REAL:
					if (svalue.trim().length() == 0) {
						value = null;
					} else {
						value = Float.parseFloat(svalue.replace("+", ""));
					}
					break;
				case Types.DOUBLE:
					if (svalue.trim().length() == 0) {
						value = null;
					} else {
						value = Double.parseDouble(svalue.replace("+", ""));
					}
					break;
				case Types.VARBINARY:
					value = svalue.getBytes();
					break;
				case Types.DATE:
					if (svalue.trim().length() == 0) {
						value = null;
					} else {
						try {
							value = DateFormat.getDateTimeInstance().parse(svalue);
						} catch (ParseException e) {
							throw new WaarpDatabaseSqlException("Error in Date: " + svalue, e);
						}
					}
					break;
				case Types.TIMESTAMP:
					if (svalue.trim().length() == 0) {
						value = null;
					} else {
						try {
							value = DateFormat.getDateTimeInstance().parse(svalue);
						} catch (ParseException e) {
							throw new WaarpDatabaseSqlException("Error in Timestamp: " + svalue, e);
						}
					}
					break;
				case Types.CLOB:
					try {
						value = new InputStreamReader(new FileInputStream(svalue));
					} catch (FileNotFoundException e) {
						throw new WaarpDatabaseSqlException("Error in CLOB: " + svalue, e);
					}
					break;
				case Types.BLOB:
					try {
						value = new FileInputStream(svalue);
					} catch (FileNotFoundException e) {
						throw new WaarpDatabaseSqlException("Error in BLOB: " + svalue, e);
					}
					break;
				default:
					throw new WaarpDatabaseSqlException("Type inconnu: " + field.type+ " pour "+field.name);
			}
		} catch (NumberFormatException e) {
			throw new WaarpDatabaseSqlException("Format incompatible: " + svalue + " pour "+field.name, e);
		}
	}

	/**
	 * @return the field
	 */
	public DbField getField() {
		return field;
	}

	/**
	 * @param field the field to set
	 */
	public void setField(DbField field) {
		this.field = field;
	}
	public Element getElement() {
		DocumentFactory factory = DocumentFactory.getInstance();
		Element value = factory.createElement(field.name);
		try {
			String sval = getValueAsString();
			if (sval != null) {
				value.setText(sval);
			}
		} catch (WaarpDatabaseSqlException e) {
			value.addAttribute(DbSchema.ERROR_ATTRIBUTE,"value error");
		}
		return value;
	}
}
