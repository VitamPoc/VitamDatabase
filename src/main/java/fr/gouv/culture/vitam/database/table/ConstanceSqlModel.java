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

import org.waarp.common.database.exception.WaarpDatabaseSqlException;

import fr.gouv.culture.vitam.database.DbVitam2Database;

/**
 * @author "Frederic Bregier"
 * 
 */
public class ConstanceSqlModel extends AbstractTableModel {
	private static final long serialVersionUID = -2291713489328675869L;
	private static final int DEFAULTLIMITROW = 30;
	private String select;
	private DbVitam2Database database;
	private int totalRow = 1;
	private int currentPage = 0;
	private int currentRows = 0;
	private int totalCol = 1;
	private int rowperpage = DEFAULTLIMITROW;
	private List<String[]> curRows = null;
	private String[] fieldsArray = null;

	public ConstanceSqlModel() {
		this.totalRow = 1;
		this.currentRows = 0;
		this.curRows = null;
	}

	public void setValidRows() throws WaarpDatabaseSqlException {
		currentPage = 0;
		updateDbSelect();
		fireTableStructureChanged();
	}
	private int getNbRow() {
		if (totalRow < 0) {
			return rowperpage;
		}
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
		if (this.curRows.size() > row) {
			return this.curRows.get(row)[col];
		}
		return "";
	}

	@Override
	public String getColumnName(int column) {
		if (fieldsArray != null) {
			return fieldsArray[column];
		}
		return "no_database";
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	public void first() {
		if (select == null) {
			return;
		}
		currentPage = 0;
		currentRows = getNbRow();
		try {
			this.curRows = this.database.getRows(select, currentPage * rowperpage, currentRows);
			fieldsArray = this.curRows.remove(0);
			currentRows = this.curRows.size();
		} catch (WaarpDatabaseSqlException e) {
			this.curRows = null;
		}
		fireTableStructureChanged();
	}

	public void next() {
		if (select == null) {
			return;
		}
		int nbpage = getPageCount();
		if (nbpage > 0) {
			if (currentPage + 1 < nbpage) {
				currentPage++;
				currentRows = getNbRow();
				try {
					this.curRows = this.database.getRows(select, currentPage * rowperpage, currentRows);
					fieldsArray = this.curRows.remove(0);
					currentRows = this.curRows.size();
				} catch (WaarpDatabaseSqlException e) {
					this.curRows = null;
				}
				fireTableStructureChanged();
			}
		} else {
			List<String[]> temp = null;
			try {
				temp = this.database.getRows(select, currentPage * rowperpage, currentRows);
			} catch (WaarpDatabaseSqlException e) {
			}
			if (temp == null || temp.size() == 0) {
			} else {
				currentPage++;
				fieldsArray = temp.remove(0);
				currentRows = temp.size();
				this.curRows = temp;
			}
			fireTableStructureChanged();
		}
	}

	public int getPage() {
		return currentPage + 1;
	}
	public int getPageCount() {
		if (totalRow < 0) {
			return -1;
		}
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
		if (select == null) {
			return -1;
		}
		int nbpage = getPageCount();
		if (nbpage > 0) {
			if (page > 0 && page < getPageCount()) {
				currentPage = page - 1;
				currentRows = getNbRow();
				try {
					this.curRows = this.database.getRows(select, currentPage * rowperpage, currentRows);
					fieldsArray = this.curRows.remove(0);
					currentRows = this.curRows.size();
				} catch (WaarpDatabaseSqlException e) {
					this.curRows = null;
				}
				fireTableStructureChanged();
			}
		} else {
			if (page > 0) {
				List<String[]> temp = null;
				try {
					temp = this.database.getRows(select, currentPage * rowperpage, currentRows);
				} catch (WaarpDatabaseSqlException e) {
				}
				if (temp == null || temp.size() == 0) {
				} else {
					currentPage = page - 1;
					fieldsArray = temp.remove(0);
					currentRows = temp.size();
					this.curRows = temp;
				}
				fireTableStructureChanged();
			}
		}
		return currentPage + 1;
	}
	public void prev() {
		if (select == null) {
			return;
		}
		if (currentPage > 0) {
			currentPage--;
			currentRows = getNbRow();
			try {
				this.curRows = this.database.getRows(select, currentPage * rowperpage, currentRows);
				fieldsArray = this.curRows.remove(0);
				currentRows = this.curRows.size();
			} catch (WaarpDatabaseSqlException e) {
				this.curRows = null;
			}
			fireTableStructureChanged();
		}
	}
	
	public void last() {
		if (select == null) {
			return;
		}
		int nbpage = getPageCount();
		if (nbpage > 0) {
			currentPage = getPageCount()-1;
			currentRows = getNbRow();
			try {
				this.curRows = this.database.getRows(select, currentPage * rowperpage, currentRows);
				fieldsArray = this.curRows.remove(0);
				currentRows = this.curRows.size();
			} catch (WaarpDatabaseSqlException e) {
				this.curRows = null;
			}
			fireTableStructureChanged();
		} else {
			next();
		}
	}
	
	public void changeSelect(String newSelect) throws WaarpDatabaseSqlException {
		this.select = newSelect;
		if (select == null) {
			totalCol = 1;
			currentPage = 0;
			currentRows = 0;
			curRows = null;
		} else {
			currentPage = 0;
			try {
				updateDbSelect();
			} catch (WaarpDatabaseSqlException e) {
				if (this.fieldsArray != null) {
					totalCol = this.fieldsArray.length;
				}
				this.totalRow = -1;
				this.currentRows = getNbRow();
				this.curRows = null;
				currentRows = 0;
				throw e;
			}
		}
		fireTableStructureChanged();
	}
	
	private void updateDbSelect() throws WaarpDatabaseSqlException {
		this.totalRow = this.database.rowCount(select);
		this.currentRows = getNbRow();
		this.curRows = this.database.getRows(select, currentPage * rowperpage, currentRows);
		fieldsArray = this.curRows.remove(0);
		totalCol = this.fieldsArray.length;
		currentRows = this.curRows.size();
	}
	
	public void initialize(DbVitam2Database database) {
		this.database = database;
	}
}
