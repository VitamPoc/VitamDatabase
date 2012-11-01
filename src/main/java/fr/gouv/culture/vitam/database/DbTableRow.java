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
import java.util.List;

import org.dom4j.DocumentFactory;
import org.dom4j.Element;

/**
 * DbTableRow containing values for one Table according to Fields
 * @author "Frederic Bregier"
 * 
 */
public class DbTableRow {
	public DbTable table;
	public int rank;
	private List<DbFieldValue> values;
	/**
	 * @param table
	 * @param rank
	 */
	public DbTableRow(DbTable table, int rank) {
		this.table = table;
		this.rank = rank;
		this.values = new ArrayList<DbFieldValue>();
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
	/**
	 * @return the rank
	 */
	public int getRank() {
		return rank;
	}
	/**
	 * @param rank the rank to set
	 */
	public void setRank(int rank) {
		this.rank = rank;
	}
	/**
	 * @return the values
	 */
	public List<DbFieldValue> getValues() {
		return values;
	}

	public void addValue(DbFieldValue value) {
		this.values.add(value);
	}
	
	public Element getElement() {
		DocumentFactory factory = DocumentFactory.getInstance();
		Element row = factory.createElement(DbSchema.ROW_FIELD);
		row.addAttribute(DbSchema.RANK_ATTRIBUTE, Integer.toString(rank));
		row.addAttribute(DbSchema.NB_ATTRIBUTE, Integer.toString(values.size()));
		for (DbFieldValue value : values) {
			Element evalue = value.getElement();
			row.add(evalue);
		}
		return row;
	}
}
