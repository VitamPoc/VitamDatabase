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

import org.dom4j.DocumentFactory;
import org.dom4j.Element;

import fr.gouv.culture.vitam.database.model.GenericDBType;

/**
 * DbField
 * 
 * @author "Frederic Bregier"
 * 
 */
public class DbField {
	public String name;
	public String description;
	/**
	 * java.sql.Types: OTHER = a DbField for Constraints
	 */
	public int type;
	public int length;
	public boolean nullable;
	public DbTable table;
	/**
	 * @param name
	 * @param description
	 * @param type
	 * @param nullable
	 * @param table
	 */
	public DbField(String name, String description, int type, int length, boolean nullable, DbTable table) {
		this.name = name;
		this.description = description;
		this.type = type;
		this.length = length;
		this.nullable = nullable;
		this.table = table;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}
	/**
	 * @return the length
	 */
	public int getLength() {
		return length;
	}
	/**
	 * @param length the length to set
	 */
	public void setLength(int length) {
		this.length = length;
	}
	/**
	 * @return the nullable
	 */
	public boolean isNullable() {
		return nullable;
	}
	/**
	 * @param nullable the nullable to set
	 */
	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}
	/**
	 * @return the table
	 */
	public DbTable getTable() {
		return table;
	}
	/**
	 * @param table the table to set
	 */
	public void setTable(DbTable table) {
		this.table = table;
	}
	
	public String toString() {
		return table.name+"."+name;
	}

	public String toStringDb() {
		return table.name+"_"+name;
	}

	public static DbField buildFromXml(Element field, DbTable dbtable) {
		DbField dbfield = null;
		String name = field.getName();
		String descr =  field.attributeValue(DbSchema.DESCRIPTION_ATTRIBUTE);
		String type = field.attributeValue(DbSchema.TYPE_ATTRIBUTE);
		String length =  field.attributeValue(DbSchema.LENGTH_ATTRIBUTE);
		String nullable = field.attributeValue(DbSchema.NULLABLE_ATTRIBUTE);
		int dbType = GenericDBType.getType(type);
		dbfield = new DbField(
				name, 
				descr,
				dbType,
				Integer.parseInt(length),
				Boolean.parseBoolean(nullable),
				dbtable);
		return dbfield;
	}
	
	public Element getElement() {
		DocumentFactory factory = DocumentFactory.getInstance();
		Element field= factory.createElement(name);
		field.addAttribute(DbSchema.DESCRIPTION_ATTRIBUTE, description);
		field.addAttribute(DbSchema.TYPE_ATTRIBUTE, GenericDBType.getType(type, length));
		field.addAttribute(DbSchema.LENGTH_ATTRIBUTE, Integer.toString(length));
		field.addAttribute(DbSchema.NULLABLE_ATTRIBUTE, Boolean.toString(nullable));
		return field;
	}
}
