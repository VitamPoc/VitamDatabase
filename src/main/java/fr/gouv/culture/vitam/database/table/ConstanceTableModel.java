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
package fr.gouv.culture.vitam.database.table;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import fr.gouv.culture.vitam.database.DbTable;
import fr.gouv.culture.vitam.database.DbVitam2Database;

/**
 * @author "Frederic Bregier"
 * 
 */
public class ConstanceTableModel extends AbstractTableModel {
	private static final long serialVersionUID = -2291713489328675869L;
	private static final int DEFAULTLIMITROW = 30;
	private DbTable table;
	private DbVitam2Database database;
	private int totalRow = 1;
	private int currentPage = 0;
	private int currentRows = 0;
	private int totalCol = 1;
	private int rowperpage = DEFAULTLIMITROW;
	private List<String[]> curRows = null;
	private boolean []fields = null;

	public ConstanceTableModel() {
		this.totalRow = 1;
		this.currentRows = 0;
		this.curRows = null;
	}

	public void setValidRows(boolean []fields) {
		this.fields = fields;
		if (fields != null) {
			totalCol = 0;
			for (int i = 0; i < fields.length; i++) {
				if (fields[i]) {
					totalCol++;
				}
			}
			if (totalCol == 0 && table == null) {
				totalCol = 1;
			}
		} else {
			if (table != null) {
				totalCol = table.nbFields();
			} else {
				totalCol = 1;
			}
		}
		fireTableStructureChanged();
	}
	private int getNbRow() {
		int nb = totalRow - currentPage * rowperpage;
		if (nb > rowperpage) {
			nb = rowperpage;
		}
		return nb;
	}
	@Override
	public int getColumnCount() {
		return totalCol;
	}

	@Override
	public int getRowCount() {
		return currentRows;
	}

	@Override
	public Object getValueAt(int row, int col) {
		if (this.curRows == null || this.curRows.size() == 0) {
			return "no_database";
		}
		if (fields != null) {
			int rank = 0;
			int realRank = 0;
			for (realRank = 0; realRank < fields.length; realRank++) {
				if (fields[realRank]) {
					if (rank == col) {
						break;
					}
					rank++;
				}
			}
			return this.curRows.get(row)[realRank];
		} else {
			return this.curRows.get(row)[col];
		}
	}

	@Override
	public String getColumnName(int column) {
		if (table != null) {
			if (fields != null) {
				int rank = 0;
				int realRank = 0;
				for (realRank = 0; realRank < fields.length; realRank++) {
					if (fields[realRank]) {
						if (rank == column) {
							break;
						}
						rank++;
					}
				}
				return table.getField(realRank).getName();
			} else {
				return table.getField(column).getName();
			}
		}
		return "no_database";
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	public void first() {
		if (table == null) {
			return;
		}
		currentPage = 0;
		currentRows = getNbRow();
		this.curRows = this.database.getRows(table, currentPage * rowperpage, currentRows);
		fireTableStructureChanged();
	}

	public void next() {
		if (table == null) {
			return;
		}
		if (currentPage + 1 < getPageCount()) {
			currentPage++;
			currentRows = getNbRow();
			this.curRows = this.database.getRows(table, currentPage * rowperpage, currentRows);
			fireTableStructureChanged();
		}
	}

	public int getPage() {
		return currentPage + 1;
	}
	public int getPageCount() {
		return (int) Math.ceil(((float) totalRow / (float) rowperpage));
	}
	public int getRowPerPage() {
		return rowperpage;
	}
	public int setRowPerPage(int rpp) {
		if (rpp > 0) {
			rowperpage = rpp;
			first();
		}
		return rowperpage;
	}
	public int setPage(int page) {
		if (table == null) {
			return -1;
		}
		if (page > 0 && page < getPageCount()) {
			currentPage = page - 1;
			currentRows = getNbRow();
			this.curRows = this.database.getRows(table, currentPage * rowperpage, currentRows);
			fireTableStructureChanged();
		}
		return currentPage + 1;
	}
	public void prev() {
		if (table == null) {
			return;
		}
		if (currentPage > 0) {
			currentPage--;
			currentRows = getNbRow();
			this.curRows = this.database.getRows(table, currentPage * rowperpage, currentRows);
			fireTableStructureChanged();
		}
	}
	
	public void last() {
		if (table == null) {
			return;
		}
		currentPage = getPageCount()-1;
		currentRows = getNbRow();
		this.curRows = this.database.getRows(table, currentPage * rowperpage, currentRows);
		fireTableStructureChanged();
	}
	
	public void changeDbTable(DbTable newTable) {
		this.table = newTable;
		this.fields = null;
		if (table == null) {
			totalCol = 1;
			currentPage = 0;
			currentRows = 0;
			curRows = null;
		} else {
			totalCol = this.table.nbFields();
			currentPage = 0;
			this.totalRow = this.database.rowCount(table);
			this.currentRows = getNbRow();
			this.curRows = this.database.getRows(table, currentPage * rowperpage, currentRows);
		}
		fireTableStructureChanged();
	}
	
	public void initialize(DbVitam2Database database, DbTable newTable) {
		this.database = database;
		changeDbTable(newTable);
	}
}
