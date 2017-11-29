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
package fr.gouv.culture.vitam.database.utils.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * @author "Frederic Bregier"
 * 
 */
public class CheckComboBox extends JComboBox {
	private static final long serialVersionUID = 3036396630195449462L;

	private String header;
	private List<ObjCheckBox> cbs;
	private Map<Object, Boolean> mapObjSelected;
	private List<CheckComboBoxSelectionChangedListener> changedListeners = new Vector<CheckComboBoxSelectionChangedListener>();

	private Object nullObject = new Object();

	public CheckComboBox(final Set<Object> objs, String header) {
		this(objs, false, header);
	}

	public CheckComboBox(final Set<Object> objs, boolean selected, String header) {
		resetObjs(objs, selected, header);
	}

	public CheckComboBox(final Set<Object> objs, final Set<Object> selected, String header) {
		this.header = header;
		mapObjSelected = new LinkedHashMap<Object, Boolean>();
		for (Object obj : objs) {
			if (obj == null)
				obj = nullObject;
			mapObjSelected.put(obj, selected.contains(obj));
		}

		reset();
	}

	public CheckComboBox(Map<Object, Boolean> mapObjSelected, String header) {
		this.mapObjSelected = mapObjSelected;
		this.header = header;
		reset();
	}

	public void addSelectionChangedListener(CheckComboBoxSelectionChangedListener l) {
		if (l == null) {
			return;
		}
		changedListeners.add(l);
	}

	public void removeSelectionChangedListener(CheckComboBoxSelectionChangedListener l) {
		changedListeners.remove(l);
	}

	public void resetObjs(final Set<Object> objs, boolean selected, String header) {
		this.header = header;
		mapObjSelected = new LinkedHashMap<Object, Boolean>();
		for (Object obj : objs) {
			mapObjSelected.put(obj, selected);
		}

		reset();
	}
	
	public void resetObjs(final Object[] objs, boolean selected, String header) {
		this.header = header;
		mapObjSelected = new LinkedHashMap<Object, Boolean>();
		for (Object obj : objs) {
			mapObjSelected.put(obj, selected);
		}

		reset();
	}

	public void resetObjs(final Object[] objs, boolean []selected, String header) {
		this.header = header;
		mapObjSelected = new LinkedHashMap<Object, Boolean>();
		for (int i = 0 ; i < objs.length; i++) {
			mapObjSelected.put(objs[i], selected[i]);
		}

		reset();
	}

	public Object[] getSelectedItems() {
		Set<Object> ret = new TreeSet<Object>(); // alphabetically
		for (Map.Entry<Object, Boolean> entry : mapObjSelected.entrySet()) {
			Object obj = entry.getKey();
			Boolean selected = entry.getValue();

			if (selected) {
				ret.add(obj);
			}
		}

		if (ret.isEmpty())
			return null;

		return ret.toArray(new Object[ret.size()]);
	}

	public void addSelectedItems(Collection<Object> c) {
		if (c == null)
			return;

		for (Object obj : c) {
			if (mapObjSelected.containsKey(obj)) {
				mapObjSelected.put(obj, true);
			}
		}

		reset();
		repaint();
	}

	public void addSelectedItems(Object[] objs) {
		if (objs == null)
			return;

		for (Object obj : objs) {
			if (mapObjSelected.containsKey(obj)) {
				mapObjSelected.put(obj, true);
			}
		}

		reset();
		repaint();
	}

	
	private void reset() {
		this.removeAllItems();

		initCBs();

		this.addItem(new String());
		for (JCheckBox cb : cbs) {
			this.addItem(cb);
		}

		setRenderer(new CheckBoxRenderer(cbs, header));
		addActionListener(this);
	}

	private void initCBs() {
		cbs = new Vector<ObjCheckBox>();

		boolean selectedAll = true;
		boolean selectedNone = true;

		ObjCheckBox cb;
		for (Map.Entry<Object, Boolean> entry : mapObjSelected.entrySet()) {
			Object obj = entry.getKey();
			Boolean selected = entry.getValue();

			if (selected) {
				selectedNone = false;
			} else {
				selectedAll = false;
			}

			cb = new ObjCheckBox(obj);
			cb.setSelected(selected);
			cbs.add(cb);
		}

		cb = new ObjCheckBox("Tout selectionner");
		cb.setSelected(selectedAll);
		cbs.add(cb);

		cb = new ObjCheckBox("Tout retirer");
		cb.setSelected(selectedNone);
		cbs.add(cb);
	}

	private void checkBoxSelectionChanged(int index) {
		int n = cbs.size();
		if (index < 0 || index >= n)
			return;

		// Set selectedObj = getSelected();
		if (index < n - 2) {
			ObjCheckBox cb = cbs.get(index);
			if (cb.getObj() == nullObject) {
				return;
			}

			if (cb.isSelected()) {
				cb.setSelected(false);
				mapObjSelected.put(cb.getObj(), false);

				cbs.get(n - 2).setSelected(false); // Select all
				cbs.get(n - 1).setSelected(getSelectedItems() == null); // select none
			} else {
				cb.setSelected(true);
				mapObjSelected.put(cb.getObj(), true);

				Object[] sobjs = getSelectedItems();
				cbs.get(n - 2).setSelected(sobjs != null && sobjs.length == n - 2); // Select all
				cbs.get(n - 1).setSelected(false); // select none
			}
		} else if (index == n - 2) {
			for (Object obj : mapObjSelected.keySet()) {
				if (obj != nullObject)
					mapObjSelected.put(obj, true);
			}

			for (int i = 0; i < n - 1; i++) {
				if (cbs.get(i) != nullObject)
					cbs.get(i).setSelected(true);
			}
			cbs.get(n - 1).setSelected(false);
		} else { // if (index==n-1)
			for (Object obj : mapObjSelected.keySet()) {
				mapObjSelected.put(obj, false);
			}

			for (int i = 0; i < n - 1; i++) {
				cbs.get(i).setSelected(false);
			}
			cbs.get(n - 1).setSelected(true);
		}

	}
	
	/**
	 * Used to invert a specific selection
	 * @param idx
	 */
	public void changeCheckBoxSelection(int idx) {
		checkBoxSelectionChanged(idx);
	}
	
	/**
	 * 
	 * @param idx
	 * @return the status of the checkbox at index idx
	 */
	public boolean isCheckBoxSelected(int idx) {
		ObjCheckBox cb = cbs.get(idx);
		if (cb == null) {
			return false;
		}
		return cb.isSelected();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		int sel = getSelectedIndex();

		if (sel == 0) {
			getUI().setPopupVisible(this, false);
		} else if (sel > 0) {
			checkBoxSelectionChanged(sel - 1);
			for (CheckComboBoxSelectionChangedListener l : changedListeners) {
				l.selectionChanged(sel - 1);
			}
		}

		this.setSelectedIndex(-1); // clear selection
	}

	@Override
	public void setPopupVisible(boolean flag)
	{
		// TODO this not work, fix it
		// Not code here prevents the populist from closing
	}

	// checkbox renderer for combobox
	class CheckBoxRenderer implements ListCellRenderer {
		private final DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
		private javax.swing.JSeparator separator;
		private final List<ObjCheckBox> cbs;
		private final String header;
		// private final Set objs;

		public CheckBoxRenderer(final List<ObjCheckBox> cbs, String header) {
			// setOpaque(true);
			this.cbs = cbs;
			// this.objs = objs;
			separator = new javax.swing.JSeparator(javax.swing.JSeparator.HORIZONTAL);
			this.header = header;
		}

		// @Override
		public Component getListCellRendererComponent(
				JList list,
				Object value,
				int index,
				boolean isSelected,
				boolean cellHasFocus) {
			if (index > 0 && index <= cbs.size()) {
				ObjCheckBox cb = cbs.get(index - 1);
				if (cb.getObj() == nullObject) {
					return separator;
				}

				cb.setBackground(isSelected ? Color.blue : Color.white);
				cb.setForeground(isSelected ? Color.white : Color.black);

				return cb;
			}

			String str;
			Object[] objs = getSelectedItems();
			if (objs == null) {
				if (header == null) {
					str = "Choisir un champs";
				} else {
					str = header;
				}
			} else {
				if (header == null) {
					Vector<String> strs = new Vector<String>();
					for (Object obj : objs) {
						strs.add(obj.toString());
					}
					str = strs.toString();
				} else {
					str = header;
				}
			}
			return defaultRenderer.getListCellRendererComponent(list, str, index, isSelected,
					cellHasFocus);
		}
	}

	class ObjCheckBox extends JCheckBox {
		private static final long serialVersionUID = -8379864820151303585L;

		private final Object obj;

		public ObjCheckBox(final Object obj) {
			super(obj.toString());
			this.obj = obj;
		}

		public Object getObj() {
			return obj;
		}
	}

}
