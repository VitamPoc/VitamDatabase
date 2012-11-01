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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.dom4j.DocumentFactory;
import org.dom4j.Element;

import fr.gouv.culture.vitam.database.DbField;
import fr.gouv.culture.vitam.database.DbSchema;
import fr.gouv.culture.vitam.database.DbTable;

/**
 * Helper class for Select sql command
 * @author "Frederic Bregier"
 *
 */
public class DbSelect {
	public List<DbField> selected = new ArrayList<DbField>();
	public HashSet<String> selectedFields = new HashSet<String>();
	public HashMap<String, DbTable> fromTables = new HashMap<String, DbTable>();
	public List<DbCondition> conditions = new ArrayList<DbCondition>();
	public HashMap<String, DbField> orderByAsc = new HashMap<String, DbField>();
	public HashMap<String, DbField> orderByDesc = new HashMap<String, DbField>();
	public int limit = 30;
	public int offset = 0;
	/**
	 * Default constructor
	 */
	public DbSelect() {
	}
	
	public String toStringNoLimitNoOrder() {
		cleanTableFrom();
		if (selected.isEmpty()) {
			return DbModelFactory.dbVitamModel.validConnectionString();
		}
		StringBuilder builder = new StringBuilder("SELECT DISTINCT ");
		HashSet<String> tempHash = new HashSet<String>();
		for (DbField field : selected) {
			builder.append(field.toString());
			if (tempHash.contains(field.name)) {
				builder.append(" AS ");
				builder.append(field.toStringDb());
			} else {
				tempHash.add(field.name);
			}
			builder.append(',');
		}
		tempHash.clear();
		builder.deleteCharAt(builder.length()-1);
		builder.append(" FROM ");
		for (DbTable table : fromTables.values()) {
			builder.append(table.toString());
			builder.append(',');
		}
		builder.deleteCharAt(builder.length()-1);
		if (! conditions.isEmpty()) {
			builder.append(" WHERE ");
			int lastPos = builder.length();
			for (DbCondition condition : conditions) {
				builder.append(condition.toString());
				lastPos = builder.length();
				builder.append(" AND ");
			}
			builder.delete(lastPos, builder.length());
		}
		return builder.toString();
	}

	public String toStringNoLimit() {
		String result = toStringNoLimitNoOrder();
		StringBuilder builder = new StringBuilder(result);
		boolean orderby = false;
		if (! orderByAsc.isEmpty()) {
			orderby = true;
			builder.append(" ORDER BY ");
			for (DbField field : orderByAsc.values()) {
				builder.append(field.toString());
				builder.append(" ASC,");
			}			
			builder.deleteCharAt(builder.length()-1);
		}
		if (! orderByDesc.isEmpty()) {
			if (! orderby) {
				builder.append(" ORDER BY ");
			} else {
				builder.append(",");

			}
			for (DbField field : orderByDesc.values()) {
				builder.append(field.toString());
				builder.append(" DESC,");
			}			
			builder.deleteCharAt(builder.length()-1);
		}
		return builder.toString();
	}

	public String toString() {
		String result = toStringNoLimit();
		result = DbModelFactory.dbVitamModel.getLimitOffset(result, offset, limit);
		return result;
	}
	/**
	 * @return the limit
	 */
	public int getLimit() {
		return limit;
	}
	/**
	 * @param limit the limit to set
	 */
	public void setLimit(int limit) {
		this.limit = limit;
	}
	/**
	 * @return the offset
	 */
	public int getOffset() {
		return offset;
	}
	/**
	 * @param offset the offset to set
	 */
	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	public void addSelected(DbField field) {
		if (selectedFields.contains(field.toString())) {
			return;
		}
		selectedFields.add(field.toString());
		selected.add(field);
		addTable(field.table);
	}
	public void remSelected(DbField field) {
		selected.remove(field);
		selectedFields.remove(field.toString());
		orderByAsc.remove(field.toString());
		orderByDesc.remove(field.toString());
	}
	public void addTable(DbTable table) {
		if (fromTables.containsKey(table.name)) {
			return;
		}
		fromTables.put(table.name, table);
	}
	public void remTable(DbTable table) {
		fromTables.remove(table.name);
	}
	public void addCondition(DbCondition condition) {
		conditions.add(condition);
		for (Object object : condition.operands) {
			if (object instanceof DbField) {
				DbField field = (DbField) object;
				if (fromTables.containsKey(field.table.name)) {
					continue;
				}
				fromTables.put(field.table.name, field.table);
			}
		}
	}
	public void removeCondition(DbCondition condition) {
		conditions.remove(condition);
	}
	/**
	 * 
	 * @param field
	 * @throws IllegalArgumentException if both asc and desc will be selected
	 */
	public void addOrderAsc(DbField field) {
		if (orderByAsc.containsKey(field.toString())) {
			return;
		}
		if (orderByDesc.containsKey(field.toString())) {
			throw new IllegalArgumentException("L'Ordre ne peut pas être à la fois Descendant et Ascendant pour un champ : " + field.toString());
		}
		addSelected(field);
		orderByAsc.put(field.toString(), field);
	}
	public void remOrderAsc(DbField field) {
		orderByAsc.remove(field.toString());
	}
	/**
	 * 
	 * @param field
	 * @throws IllegalArgumentException if both asc and desc will be selected
	 */
	public void addOrderDesc(DbField field) {
		if (orderByDesc.containsKey(field.toString())) {
			return;
		}
		if (orderByAsc.containsKey(field.toString())) {
			throw new IllegalArgumentException("L'Ordre ne peut pas être à la fois Descendant et Ascendant pour un champ : " + field.toString());
		}
		addSelected(field);
		orderByDesc.put(field.toString(), field);
	}
	public void remOrderDesc(DbField field) {
		orderByDesc.remove(field.toString());
	}
	public void cleanTableFrom() {
		HashMap<String, DbTable> fromTablesTmp = new HashMap<String, DbTable>();
		for (DbField field : selected) {
			if (fromTablesTmp.containsKey(field.table.name)) {
				continue;
			}
			fromTablesTmp.put(field.table.name, field.table);
		}
		for (DbField field : orderByAsc.values()) {
			if (fromTablesTmp.containsKey(field.table.name)) {
				continue;
			}
			fromTablesTmp.put(field.table.name, field.table);
		}
		for (DbField field : orderByDesc.values()) {
			if (fromTablesTmp.containsKey(field.table.name)) {
				continue;
			}
			fromTablesTmp.put(field.table.name, field.table);
		}
		for (DbCondition condition : conditions) {
			for (Object object : condition.operands) {
				if (object instanceof DbField) {
					DbField field = (DbField) object;
					if (fromTablesTmp.containsKey(field.table.name)) {
						continue;
					}
					fromTablesTmp.put(field.table.name, field.table);
				}
			}
		}
		fromTables.clear();
		fromTables = fromTablesTmp;
	}
	
	@SuppressWarnings("unchecked")
	public static DbSelect fromElement(Element root, DbSchema schema) {
		DbSelect dbSelect = new DbSelect();
		dbSelect.offset = Integer.parseInt(root.attributeValue(DbSchema.OFFSET_ATTRIBUTE));
		dbSelect.limit = Integer.parseInt(root.attributeValue(DbSchema.LIMIT_ATTRIBUTE));

		List<Element> elts = root.selectNodes(DbSchema.SELECTED_FIELD+"/"+DbSchema.FIELD_FIELD);
		if (elts != null) {
			for (Element element : elts) {
				String val = element.getText().replace(".", DbSchema.FIELDSEP);
				DbField field = (DbField) schema.getObject(val);
				if (field == null) {
					System.err.println("SELECT Non trouvé : " + val);
				} else {
					dbSelect.addSelected(field);
				}
			}
		}
		elts = root.selectNodes(DbSchema.FROM_FIELD+"/"+DbSchema.TABLE_FIELD);
		if (elts != null) {
			for (Element element : elts) {
				String val = element.getText();
				DbTable table = schema.getTable(val);
				if (table == null) {
					System.err.println("TABLE Non trouvée : " + val);
				} else {
					dbSelect.addTable(table);
				}
			}
		}
		elts = root.selectNodes(DbSchema.CONDITIONS_FIELD+"/"+DbSchema.CONDITION_FIELD);
		if (elts != null) {
			for (Element element : elts) {
				DbCondition condition = DbCondition.fromElement(element, schema);
				dbSelect.addCondition(condition);
			}
		}
		elts = root.selectNodes(DbSchema.ORDERASC_FIELD+"/"+DbSchema.FIELD_FIELD);
		if (elts != null) {
			for (Element element : elts) {
				String val = element.getText().replace(".", DbSchema.FIELDSEP);
				DbField field = (DbField) schema.getObject(val);
				if (field == null) {
					System.err.println("ASC Non trouvé : " + val);
				} else {
					dbSelect.addOrderAsc(field);
				}
			}
		}
		elts = root.selectNodes(DbSchema.ORDERDESC_FIELD+"/"+DbSchema.FIELD_FIELD);
		if (elts != null) {
			for (Element element : elts) {
				String val = element.getText().replace(".", DbSchema.FIELDSEP);
				DbField field = (DbField) schema.getObject(val);
				if (field == null) {
					System.err.println("Desc Non trouvé : " + val);
				} else {
					dbSelect.addOrderDesc(field);
				}
			}
		}
		return dbSelect;
	}
	public Element toElement() {
		cleanTableFrom();
		DocumentFactory factory = DocumentFactory.getInstance();
		Element root = factory.createElement(DbSchema.DBSELECT_FIELD);
		root.addAttribute(DbSchema.OFFSET_ATTRIBUTE, Integer.toString(offset));
		root.addAttribute(DbSchema.LIMIT_ATTRIBUTE, Integer.toString(limit));
		
		if (selected.isEmpty()) {
			return root;
		}
		Element select = factory.createElement(DbSchema.SELECTED_FIELD);
		for (DbField field : selected) {
			Element efield = factory.createElement(DbSchema.FIELD_FIELD);
			efield.setText(field.toString());
			select.add(efield);
		}
		root.add(select);
		Element from = factory.createElement(DbSchema.FROM_FIELD);
		for (DbTable table : fromTables.values()) {
			Element efield = factory.createElement(DbSchema.TABLE_FIELD);
			efield.setText(table.toString());
			from.add(efield);
		}
		root.add(from);
		if (! conditions.isEmpty()) {
			Element econd = factory.createElement(DbSchema.CONDITIONS_FIELD);
			for (DbCondition condition : conditions) {
				Element efield = condition.toElement();
				econd.add(efield);
			}
			root.add(econd);
		}
		if (! orderByAsc.isEmpty()) {
			Element eorder = factory.createElement(DbSchema.ORDERASC_FIELD);
			for (DbField field : orderByAsc.values()) {
				Element efield = factory.createElement(DbSchema.FIELD_FIELD);
				efield.setText(field.toString());
				eorder.add(efield);
			}
			root.add(eorder);
		}
		if (! orderByDesc.isEmpty()) {
			Element eorder = factory.createElement(DbSchema.ORDERDESC_FIELD);
			for (DbField field : orderByDesc.values()) {
				Element efield = factory.createElement(DbSchema.FIELD_FIELD);
				efield.setText(field.toString());
				eorder.add(efield);
			}
			root.add(eorder);
		}
		return root;
	}
}
