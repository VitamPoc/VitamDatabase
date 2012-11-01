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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.DocumentFactory;
import org.dom4j.Element;

import fr.gouv.culture.vitam.constance.ConstanceIdentifier.ConstanceStruct;

/**
 * DbTable containing all Fields and Rows and Indexes, Primary Key, Constraints
 * @author "Frederic Bregier"
 * 
 */
public class DbTable {
	public String name;
	public String description;
	public String datafile;
	public ConstanceStruct type;
	public int rank;
	private List<DbField> fields;
	private List<DbTableRow> rows;
	/**
	 * Also note in HashMap indexes as TABLEname_PK_name
	 */
	public List<DbField> primaryKeys;
	/**
	 * All names as TABLEname_IDX_name, in particular TABLEname_PK_name
	 */
	public HashMap<String, List<DbField>> indexes;
	/**
	 * All names as TABLEname_CSTR_FIELDname
	 */
	public HashMap<String, DbField> constraints;

	/**
	 * @param name
	 * @param description
	 */
	public DbTable(String name, String description, int rank, ConstanceStruct type) {
		this.name = name;
		this.description = description;
		this.fields = new ArrayList<DbField>();
		this.rows = new ArrayList<DbTableRow>();
		this.rank = rank;
		this.type = type;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
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
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the fields
	 */
	public List<DbField> getFields() {
		return fields;
	}
	
	public DbField getField(int rank) {
		return fields.get(rank);
	}

	public int nbFields() {
		return fields.size();
	}
	/**
	 * @return the rows
	 */
	public List<DbTableRow> getRows() {
		return rows;
	}

	public void addRow(DbTableRow row) {
		rows.add(row);
	}
	public int nbRows() {
		return rows.size();
	}
	/**
	 * 
	 * @param rank
	 *            0 <= size-1
	 * @return the corresponding TableRow at rank
	 */
	public DbTableRow getTableRow(int rank) {
		return this.rows.get(rank);
	}

	private void initPrimaryKeys() {
		primaryKeys = new ArrayList<DbField>();
	}
	/**
	 * @return the primaryKeys
	 */
	public List<DbField> getPrimaryKeys() {
		return primaryKeys;
	}

	/**
	 * @param primaryKeys
	 *            the primaryKeys to set
	 */
	public void setPrimaryKeys(List<DbField> primaryKeys) {
		this.primaryKeys = primaryKeys;
	}

	/**
	 * 
	 * @param pk
	 */
	public void addPrimaryKey(DbField pk) {
		if (primaryKeys == null) {
			initPrimaryKeys();
		}
		primaryKeys.add(pk);
	}

	private void initIndexes() {
		indexes = new HashMap<String, List<DbField>>();
	}

	/**
	 * @return the indexes
	 */
	public HashMap<String, List<DbField>> getIndexes() {
		return indexes;
	}

	/**
	 * @param indexes
	 *            the indexes to set
	 */
	public void setIndexes(HashMap<String, List<DbField>> indexes) {
		this.indexes = indexes;
	}
	
	/**
	 * 
	 * @param name
	 * @param index
	 */
	public void addIndex(String name, List<DbField> index) {
		if (indexes == null) {
			initIndexes();
		}
		indexes.put(name, index);
	}


	private void initConstraints() {
		constraints = new HashMap<String, DbField>();
	}

	/**
	 * @return the constraints
	 */
	public HashMap<String, DbField> getConstraints() {
		return constraints;
	}

	/**
	 * @param constraints
	 *            the constraints to set
	 */
	public void setConstraints(HashMap<String, DbField> constraints) {
		this.constraints = constraints;
	}
	/**
	 * 
	 * @param name
	 * @param constraint
	 */
	public void addConstraint(String name, DbField constraint) {
		if (indexes == null) {
			initConstraints();
		}
		constraints.put(name, constraint);
	}
	
	public String toString() {
		return name;
	}


	public static DbTable buildFromXml(Element table, DbSchema schema) {
		DbTable dbtable = null;
		String name = table.attributeValue(DbSchema.NAME_ATTRIBUTE);
		String description = table.attributeValue(DbSchema.DESCRIPTION_ATTRIBUTE);
		String rank = table.attributeValue(DbSchema.RANK_ATTRIBUTE);
		String type = table.attributeValue(DbSchema.TYPE_ATTRIBUTE);
		String datafile = table.attributeValue(DbSchema.DATAFILE_ATTRIBUTE);
		dbtable = new DbTable(name, description, 
				Integer.parseInt(rank), ConstanceStruct.valueOf(type));
		if (datafile != null) {
			dbtable.datafile = datafile;
		}
		@SuppressWarnings("unchecked")
		List<Element> fields = 
			table.selectNodes(DbSchema.FIELDS_FIELD+"/*");
		if (fields != null) {
			for (Element field : fields) {
				DbField dbfield = DbField.buildFromXml(field, dbtable);
				schema.addField(dbfield);
				dbtable.fields.add(dbfield);
			}
		}
		@SuppressWarnings("unchecked")
		List<Element> primaryKeys = 
			table.selectNodes(DbSchema.PRIMARY_KEYS_FIELD+"/" + DbSchema.PRIMARY_KEY_FIELD);
		if (primaryKeys != null) {
			for (Element field : primaryKeys) {
				DbField dbfield = schema.getField(dbtable.name, 
						field.attributeValue(DbSchema.NAME_ATTRIBUTE));
				dbtable.addPrimaryKey(dbfield);
			}
		}
		@SuppressWarnings("unchecked")
		List<Element> indexes = 
			table.selectNodes(DbSchema.INDEXES_FIELD+"/" + DbSchema.INDEX_FIELD);
		if (indexes != null) {
			for (Element index : indexes) {
				String nameidx = index.attributeValue(DbSchema.NAME_ATTRIBUTE);
				List<DbField> idxlist = new ArrayList<DbField>();
				@SuppressWarnings("unchecked")
				List<Element> idxname = index.selectNodes(DbSchema.FIELD_FIELD);
				for (Element idx : idxname) {
					DbField dbfield = schema.getField(dbtable.name, 
							idx.attributeValue(DbSchema.NAME_ATTRIBUTE));
					idxlist.add(dbfield);
				}
				dbtable.addIndex(nameidx, idxlist);
			}
		}
		@SuppressWarnings("unchecked")
		List<Element> constraints = 
			table.selectNodes(DbSchema.CONSTRAINTS_FIELD+"/" + DbSchema.CONSTRAINT_FIELD);
		if (constraints != null) {
			for (Element constraint : constraints) {
				String fieldsrc = constraint.attributeValue(DbSchema.FIELD_FIELD);
				String tablecstr = constraint.attributeValue(DbSchema.TARGETTABLE_ATTRIBUTE);
				String fieldcstr = constraint.attributeValue(DbSchema.TARGETFIELD_ATTRIBUTE);
				DbField dbfield = schema.getField(tablecstr, fieldcstr);
				dbtable.addConstraint(fieldsrc, dbfield);
			}
		}
		return dbtable;
	}
	
	public Element getElement(boolean data) {
		DocumentFactory factory = DocumentFactory.getInstance();
		Element table = factory.createElement(DbSchema.TABLE_FIELD);
		table.addAttribute(DbSchema.NAME_ATTRIBUTE, name);
		table.addAttribute(DbSchema.DESCRIPTION_ATTRIBUTE, description);
		table.addAttribute(DbSchema.RANK_ATTRIBUTE, Integer.toString(rank));
		table.addAttribute(DbSchema.TYPE_ATTRIBUTE, type.toString());
		if (datafile != null) {
			table.addAttribute(DbSchema.DATAFILE_ATTRIBUTE, datafile);
		}
		if (fields != null) {
			Element efields = factory.createElement(DbSchema.FIELDS_FIELD);
			for (DbField field : fields) {
				Element efield = field.getElement();
				efields.add(efield);
			}
			efields.addAttribute(DbSchema.NB_ATTRIBUTE, Integer.toString(fields.size()));
			table.add(efields);
		}
		if (primaryKeys != null) {
			Element efields = factory.createElement(DbSchema.PRIMARY_KEYS_FIELD);
			for (DbField field : primaryKeys) {
				Element pk = factory.createElement(DbSchema.PRIMARY_KEY_FIELD);
				pk.addAttribute(DbSchema.NAME_ATTRIBUTE, field.name);
				efields.add(pk);
			}
			table.add(efields);
		}
		if (indexes != null) {
			Element efields = factory.createElement(DbSchema.INDEXES_FIELD);
			for (String idx : indexes.keySet()) {
				Element index = factory.createElement(DbSchema.INDEX_FIELD);
				index.addAttribute(DbSchema.NAME_ATTRIBUTE, idx);
				for (DbField field : indexes.get(idx)) {
					Element efield = factory.createElement(DbSchema.FIELD_FIELD);
					efield.addAttribute(DbSchema.NAME_ATTRIBUTE, field.name);
					index.add(efield);
				}
				efields.add(index);
			}
			efields.addAttribute(DbSchema.NB_ATTRIBUTE, Integer.toString(indexes.size()));
			table.add(efields);
		}
		if (constraints != null) {
			Element efields = factory.createElement(DbSchema.CONSTRAINTS_FIELD);
			for (String name : constraints.keySet()) {
				Element index = factory.createElement(DbSchema.CONSTRAINT_FIELD);
				index.addAttribute(DbSchema.FIELD_FIELD, name);
				DbField field = constraints.get(name);
				index.addAttribute(DbSchema.TARGETTABLE_ATTRIBUTE, field.table.name);
				index.addAttribute(DbSchema.TARGETFIELD_ATTRIBUTE, field.name);
				efields.add(index);
			}
			efields.addAttribute(DbSchema.NB_ATTRIBUTE, Integer.toString(constraints.size()));
			table.add(efields);
		}
		if (data && rows != null && rows.size() > 0) {
			Element erows = factory.createElement(DbSchema.ROWS_FIELD);
			erows.addAttribute(DbSchema.NB_ATTRIBUTE, Integer.toString(rows.size()));
			for (DbTableRow row : rows) {
				Element erow = row.getElement();
				erows.add(erow);
			}
			table.add(erows);
		}
		return table;
	}
}
