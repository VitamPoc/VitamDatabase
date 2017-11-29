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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.waarp.common.database.exception.WaarpDatabaseNoDataException;
import org.waarp.common.database.exception.WaarpDatabaseSqlException;
import org.waarp.common.logging.WaarpInternalLogger;
import org.waarp.common.logging.WaarpInternalLoggerFactory;

import fr.gouv.culture.vitam.constance.ConstanceIdentifier;
import fr.gouv.culture.vitam.constance.ConstanceIdentifier.ConstanceStruct;
import fr.gouv.culture.vitam.database.utils.StaticValues;
import fr.gouv.culture.vitam.writer.XmlWriter;

/**
 * DbSchema that contains all Tables and Fields
 * @author "Frederic Bregier"
 * 
 */
public class DbSchema {
	public static final String DATAFILE_ATTRIBUTE = "datafile";

	public static final String POSITION_FIELD = "position";

	public static final String POSITIONS_FIELD = "positions";

	public static final String MULTIPLEPOSITIONS_FIELD = "multiplepositions";

	public static final String SEPARATOR_ATTRIBUTE = "separator";

	public static final String TABLE_FIELD = "table";

	public static final String RANK_ATTRIBUTE = "rank";

	public static final String DESCRIPTION_ATTRIBUTE = "description";

	public static final String NB_ATTRIBUTE = "nb";

	public static final String ORDER_FIELD = "order";

	public static final String IDENTIFIER_FIELD = "identifier";

	public static final String SCHEMA_FIELD = "schema";

	public static final String NAME_ATTRIBUTE = "name";

	public static final String ROW_FIELD = "row";

	public static final String ERROR_ATTRIBUTE = "error";

	public static final String TYPE_ATTRIBUTE = "type";

	public static final String LENGTH_ATTRIBUTE = "length";

	public static final String NULLABLE_ATTRIBUTE = "nullable";

	public static final String FIELDS_FIELD = "fields";

	public static final String PRIMARY_KEYS_FIELD = "primaryKeys";
	
	public static final String PRIMARY_KEY_FIELD = "primaryKey";

	public static final String INDEXES_FIELD = "indexes";

	public static final String INDEX_FIELD = "index";

	public static final String FIELD_FIELD = "field";

	public static final String CONSTRAINTS_FIELD = "constraints";

	public static final String CONSTRAINT_FIELD = "constraint";

	public static final String TARGETTABLE_ATTRIBUTE = "targettable";

	public static final String TARGETFIELD_ATTRIBUTE = "targetfield";

	public static final String ROWS_FIELD = "rows";

	public static final String CONDITION_FIELD = "condition";

	public static final String OPERATOR_ATTRIBUTE = "operator";

	public static final String OPERAND_FIELD = "operand";

	public static final String CONDITIONS_FIELD = "conditions";

	public static final String LIMIT_ATTRIBUTE = "limit";

	public static final String DBSELECT_FIELD = "dbselect";

	public static final String SELECTED_FIELD = "selected";

	public static final String FROM_FIELD = "from";

	public static final String ORDERASC_FIELD = "orderasc";

	public static final String ORDERDESC_FIELD = "orderdesc";

	public static final String OFFSET_ATTRIBUTE = "offset";


	/**
	 * Internal Logger
	 */
	private static final WaarpInternalLogger logger = WaarpInternalLoggerFactory
			.getLogger(DbSchema.class);
	
	public static final String FIELDSEP = "__";
	public static final String PKSEP = "__PK__";
	public static final String IDXSEP = "__IDX__";
	public static final String CSTRSEP = "__CSTR__";

	public String name;
	public String description;
	public ConstanceIdentifier identifier;
	private List<DbTable> tables;
	/**
	 * Where name are as:<br>
	 * - TABLEname => DbTable - TABLEname_FIELDname => DbField, - TABLEname_IDX_name =>
	 * List<DbField> - TABLEname_PK_name => List<DbField> - TABLEname_CSTR_FIELDname => DbField from
	 * another table than FIELDname
	 */
	public HashMap<String, Object> objects;
	public List<String> constructionOrder;


	/**
	 * @param name
	 * @param description
	 * @param identifier
	 */
	public DbSchema(String name, String description, ConstanceIdentifier identifier) {
		this.name = name;
		this.description = description;
		this.identifier = identifier;
		this.tables = new ArrayList<DbTable>();
		this.objects = new HashMap<String, Object>();
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
	 * @return the tables
	 */
	public List<DbTable> getTables() {
		return tables;
	}

	/**
	 * @param table
	 *            the table to add
	 * @return the hash entry
	 */
	public String addTable(DbTable table) {
		this.tables.add(table);
		this.objects.put(table.name, table);
		return table.name;
	}
	
	public int nbTables() {
		return tables.size();
	}


	/**
	 * @return the objects of the schema
	 */
	public HashMap<String, Object> getObjects() {
		return objects;
	}

	/**
	 * @param objects
	 *            the objects to set
	 */
	public void setObjects(HashMap<String, Object> objects) {
		this.objects = objects;
	}

	/**
	 * 
	 * @param name
	 * @return the associated Object
	 */
	public Object getObject(String name) {
		return this.objects.get(name);
	}

	/**
	 * 
	 * @param name
	 * @return the associated Table
	 */
	public DbTable getTable(String name) {
		return (DbTable) this.objects.get(name);
	}

	public static final String getFieldHashName(DbField field) {
		return field.table.name + FIELDSEP + field.name;
	}

	public static final String getFieldHashName(String tableName, String fieldName) {
		return tableName + FIELDSEP + fieldName;
	}

	/**
	 * 
	 * @param field
	 * @return the hash entry
	 */
	public String addField(DbField field) {
		String name = getFieldHashName(field);
		this.objects.put(name, field);
		return name;
	}

	/**
	 * 
	 * @param tableName
	 * @param fieldName
	 * @return the associated DbField if exist
	 */
	public DbField getField(String tableName, String fieldName) {
		return (DbField) this.objects.get(getFieldHashName(tableName, fieldName));
	}

	public static final String getPkHashName(DbTable table, String name) {
		return table.name + PKSEP + name;
	}

	public static final String getPkHashName(String tableName, String pkName) {
		return tableName + PKSEP + pkName;
	}

	/**
	 * @param table
	 * @param fields
	 * @param name
	 * @return the hash entry
	 */
	public String addPk(DbTable table, List<DbField> fields, String name) {
		String name2 = getPkHashName(table, name);
		this.objects.put(name2, fields);
		for (DbField dbField : fields) {
			table.addPrimaryKey(dbField);
		}
		return name2;
	}

	/**
	 * 
	 * @param tableName
	 * @param pkName
	 * @return the associated List of DbField for Primary Key if exist
	 */
	@SuppressWarnings("unchecked")
	public List<DbField> getPk(String tableName, String pkName) {
		return (List<DbField>) this.objects.get(getPkHashName(tableName, pkName));
	}

	public static final String getIdxHashName(DbTable table, String name) {
		return table.name + IDXSEP + name;
	}

	public static final String getIdxHashName(String tableName, String idxName) {
		return tableName + IDXSEP + idxName;
	}

	/**
	 * @param table
	 * @param fields
	 * @param name
	 * @return the hash entry
	 */
	public String addIdx(DbTable table, List<DbField> fields, String name) {
		String name2 = getIdxHashName(table, name);
		this.objects.put(name2, fields);
		table.addIndex(name2, fields);
		return name2;
	}

	/**
	 * 
	 * @param tableName
	 * @param idxName
	 * @return the associated List of DbField for Index if exist
	 */
	@SuppressWarnings("unchecked")
	public List<DbField> getIdx(String tableName, String idxName) {
		return (List<DbField>) this.objects.get(getIdxHashName(tableName, idxName));
	}

	public static final String getCstrHashName(DbField fieldSrc) {
		return fieldSrc.table.name + CSTRSEP + fieldSrc.name;
	}

	public static final String getCstrHashName(String tableName, String cstrName) {
		return tableName + CSTRSEP + cstrName;
	}

	/**
	 * @param fieldSrc
	 * @param fieldDst
	 * @return the hash entry
	 */
	public String addConstraint(DbField fieldSrc, DbField fieldDst) {
		String name2 = getCstrHashName(fieldSrc);
		this.objects.put(name2, fieldDst);
		fieldSrc.table.addConstraint(name2, fieldDst);
		return name2;
	}

	/**
	 * 
	 * @param tableName
	 * @param cstrName
	 * @return the associated DbField as constraint if exist
	 */
	public DbField getConstraint(String tableName, String cstrName) {
		return (DbField) this.objects.get(getCstrHashName(tableName, cstrName));
	}
	
	public void createBuildOrder() throws WaarpDatabaseNoDataException {
		if (this.tables == null || this.tables.isEmpty()) {
			throw new WaarpDatabaseNoDataException("Tables non construites");
		}
		HashMap<String, List<String>> relation = new HashMap<String, List<String>>();
		HashMap<String, List<String>> relationReverse = new HashMap<String, List<String>>();
		HashMap<String, Integer> hash = new HashMap<String, Integer>();
		for (DbTable table : this.tables) {
			hash.put(table.name, 1);
			if (table.constraints != null && !table.constraints.isEmpty()) {
				ArrayList<String> list = new ArrayList<String>();
				for (DbField field : table.constraints.values()) {
					list.add(field.table.name);
					List<String> listReverse = relationReverse.get(field.table.name);
					if (listReverse == null) {
						listReverse = new ArrayList<String>();
						relationReverse.put(field.table.name, listReverse);
					}
					listReverse.add(table.name);
				}
				relation.put(table.name, list);
			} else {
				relation.put(table.name, new ArrayList<String>());
			}
		}
		int maxrank = 0;
		for (String tableName : relation.keySet()) {
			List<String> list = relation.get(tableName);
			Integer rank = hash.get(tableName);
			if (rank == null) {
				rank = 1;
			}
			if (! list.isEmpty()) {
				for (String table2 : list) {
					Integer value = hash.get(table2);
					if (value != null) {
						rank = rank > value ? rank : value;
					}
				}
			}
			maxrank = maxrank > rank ? maxrank : rank;
			hash.put(tableName, rank);
			list = relationReverse.get(tableName);
			if (list != null) {
				for (String table2 : list) {
					Integer value = hash.get(table2);
					if (value != null) {
						value = value > rank ? value : rank+1;
					}
					maxrank = maxrank > value ? maxrank : value;
					hash.put(table2, value);
				}
			}
		}
		relation.clear();
		relation = null;
		relationReverse.clear();
		relationReverse = null;
		HashMap<Integer, TreeSet<String>> hashsorted = new HashMap<Integer, TreeSet<String>>();
		for (String tableName : hash.keySet()) {
			int rank = hash.get(tableName);
			TreeSet<String> treeset = hashsorted.get(rank);
			if (treeset == null) {
				treeset = new TreeSet<String>();
				hashsorted.put(rank, treeset);
			}
			treeset.add(tableName);
		}
		hash.clear();
		hash = null;
		List<String> order = new ArrayList<String>();
		for (int i = 0; i <= maxrank; i++) {
			TreeSet<String> treeset = hashsorted.get(i);
			if (treeset != null) {
				for (String tableName : treeset) {
					order.add(tableName);
				}
				treeset.clear();
			}
		}
		hashsorted.clear();
		hashsorted = null;
		constructionOrder = order;
	}
	
	public void printOrder() {
		System.out.print("Ordre de création: ");
		for (String tableName : constructionOrder) {
			System.out.print(tableName + " ");
		}
		System.out.println();
	}
	
	public static DbSchema buildFromXml(File xml, Element rootExport) {
		DbSchema schema = null;
		@SuppressWarnings("unchecked")
		List<Element> schemas = rootExport.selectNodes(SCHEMA_FIELD);
		boolean shouldComputeOrder = false;
		if (schemas != null && schemas.size() > 1) {
			shouldComputeOrder = true;
		}
		for (Element rootSchema : schemas) {
			String name = rootSchema.attributeValue(NAME_ATTRIBUTE);
			String description = rootSchema.attributeValue(DESCRIPTION_ATTRIBUTE);
			Element identifier = (Element) rootSchema.selectSingleNode(IDENTIFIER_FIELD);
			ConstanceIdentifier cidentifier = null;
			String datafile = null;
			if (identifier != null) {
				String type = identifier.attributeValue(TYPE_ATTRIBUTE);
				datafile = identifier.attributeValue(DATAFILE_ATTRIBUTE);
				ConstanceStruct ctype = ConstanceStruct.valueOf(type);
				cidentifier = new ConstanceIdentifier(xml, ctype, datafile);
				switch (ctype) {
					case CSVTYPE:
						cidentifier.separator = identifier.attributeValue(SEPARATOR_ATTRIBUTE);
						break;
					case MULTIPLETYPE: {
						@SuppressWarnings("unchecked")
						List<Element> multiple = identifier.selectNodes(MULTIPLEPOSITIONS_FIELD+"/"+POSITIONS_FIELD);
						int size = multiple.size();
						int [][] imultiple = new int [size][];
						int rank = 0;
						for (Element element : multiple) {
							@SuppressWarnings("unchecked")
							List<Element> pos = element.selectNodes(POSITION_FIELD);
							imultiple[rank] = new int[pos.size()];
							int i = 0;
							for (Element element2 : pos) {
								imultiple[rank][i] = Integer.parseInt(element2.getText());
								i++;
							}
							rank ++;
						}
						cidentifier.positionsMultiple = imultiple;
					}
						break;
					case UNIQUETYPE: {
						@SuppressWarnings("unchecked")
						List<Element> pos = identifier.selectNodes(POSITIONS_FIELD+"/"+POSITION_FIELD);
						int ipositions[] = new int[pos.size()];
						int i = 0;
						for (Element element2 : pos) {
							ipositions[i] = Integer.parseInt(element2.getText());
							i++;
						}
						cidentifier.positions = ipositions;
					}
						break;
				}
			}
			if (schema == null) {
				schema = new DbSchema(name, description, cidentifier);
			}
			@SuppressWarnings("unchecked")
			List<Element> orders = rootSchema.selectNodes(ORDER_FIELD+"/"+TABLE_FIELD);
			if (orders != null) {
				if (schema.constructionOrder == null) {
					schema.constructionOrder = new ArrayList<String>();
				}
				// Load table according to order
				for (Element element : orders) {
					String tablename = element.getText();
					schema.constructionOrder.add(tablename);
					Element table = (Element) rootSchema.selectSingleNode(
							TABLE_FIELD+"[@"+NAME_ATTRIBUTE+"='"+tablename+"']");
					if (table == null) {
						System.err.println(StaticValues.LBL.error_error.get() + "Table non trouvee: " + tablename);
						continue;
					}
					DbTable dbtable = DbTable.buildFromXml(table, schema);
					if (datafile != null && dbtable.datafile == null) {
						dbtable.datafile = datafile;
					}
					schema.addTable(dbtable);
				}
			} else {
				// no order so load tables as they come
				@SuppressWarnings("unchecked")
				List<Element> tables = rootSchema.selectNodes(TABLE_FIELD);
				for (Element table : tables) {
					DbTable dbtable = DbTable.buildFromXml(table, schema);
					if (datafile != null && dbtable.datafile == null) {
						dbtable.datafile = datafile;
					}
					schema.addTable(dbtable);
				}
			}		
		}
		if (shouldComputeOrder) {
			try {
				schema.createBuildOrder();
				schema.printOrder();
			} catch (WaarpDatabaseNoDataException e) {
				System.err.println(StaticValues.LBL.error_error.get() + e.toString());
			}
		}
		return schema;
	}
	
	public Element getElement(boolean recursive, boolean data) {
		DocumentFactory factory = DocumentFactory.getInstance();
		Element schema = factory.createElement(SCHEMA_FIELD);
		schema.addAttribute(NAME_ATTRIBUTE, name);
		schema.addAttribute(DESCRIPTION_ATTRIBUTE, description);
		if (identifier != null) {
			Element eltidentifier = factory.createElement(IDENTIFIER_FIELD);
			eltidentifier.addAttribute(TYPE_ATTRIBUTE, identifier.type.name());
			eltidentifier.addAttribute(DATAFILE_ATTRIBUTE, identifier.refDataFile);
			switch (identifier.type) {
				case CSVTYPE:
					eltidentifier.addAttribute(SEPARATOR_ATTRIBUTE, identifier.separator);
					break;
				case MULTIPLETYPE: {
					Element poslistm = factory.createElement(MULTIPLEPOSITIONS_FIELD);
					for (int []posmul : identifier.positionsMultiple) {
						Element poslist = factory.createElement(POSITIONS_FIELD);
						for (int ipos : posmul) {
							Element pos = factory.createElement(POSITION_FIELD);
							pos.setText(Integer.toString(ipos));
							poslist.add(pos);
						}
						poslistm.add(poslist);
					}
					eltidentifier.add(poslistm);
				}
					break;
				case UNIQUETYPE: {
					Element poslist = factory.createElement(POSITIONS_FIELD);
					for (int ipos : identifier.positions) {
						Element pos = factory.createElement(POSITION_FIELD);
						pos.setText(Integer.toString(ipos));
						poslist.add(pos);
					}
					eltidentifier.add(poslist);
				}
					break;
			}
			schema.add(eltidentifier);
		}
		if (constructionOrder != null) {
			Element order = factory.createElement(ORDER_FIELD);
			int rank = 0;
			for (String table : constructionOrder) {
				Element etable = factory.createElement(TABLE_FIELD);
				etable.addAttribute(RANK_ATTRIBUTE, Integer.toString(rank));
				etable.setText(table);
				order.add(etable);
				rank ++;
			}
			schema.add(order);
		}
		if (recursive) {
			schema.addAttribute(NB_ATTRIBUTE, Integer.toString(tables.size()));
			for (DbTable table : tables) {
				if (data && table.getRows() != null && table.getRows().size() > 0) {
					Element etable = table.getElement(data);
					schema.add(etable);
				} else {
					Element etable = table.getElement(false);
					if (data) {
						// fill data using file
						try {
							XmlWriter.loadDbTableDataFromFile(this, table, etable);
						} catch (IOException e) {
							logger.warn(StaticValues.LBL.error_error.get() + e);
						} catch (WaarpDatabaseSqlException e) {
							logger.warn(StaticValues.LBL.error_error.get() + e);
						}
					}
					schema.add(etable);
				}
			}
		}
		return schema;
	}
}
