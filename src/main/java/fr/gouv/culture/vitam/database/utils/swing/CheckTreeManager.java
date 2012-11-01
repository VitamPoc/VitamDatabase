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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

/**
 * @author Santhosh Kumar T
 * @email santhosh@in.fiorano.com
 * 
 * @author Jianjiong Gao -- add listener
 */
public class CheckTreeManager extends MouseAdapter implements TreeSelectionListener
{
	private CheckTreeSelectionModel selectionModel;
	private TreePathSelectable selectable;
	protected JTree tree = new JTree();
	protected final int hotspot = new JCheckBox().getPreferredSize().width;
	private Vector<SelectionChangeListener> scls;

	public CheckTreeManager(JTree tree, boolean dig, TreePathSelectable selectable)
	{
		this.tree = tree;
		selectionModel = new CheckTreeSelectionModel(tree.getModel(), dig);
		this.selectable = selectable;

		// note: if largemodel is not set
		// then treenodes are getting truncated.
		// need to debug further to find the problem
		if (selectable != null)
			tree.setLargeModel(true);

		tree.setCellRenderer(new CheckTreeCellRenderer(tree.getCellRenderer(), selectionModel,
				selectable));
		tree.addMouseListener(this);
		selectionModel.addTreeSelectionListener(this);

		scls = new Vector<SelectionChangeListener>();
	}

	public void addSelectionChangeListener(SelectionChangeListener scl) {
		scls.add(scl);
	}

	public void removeSelectionChangeListener(SelectionChangeListener scl) {
		scls.remove(scl);
	}

	public TreePathSelectable getSelectable(TreePathSelectable selectable)
	{
		return selectable;
	}

	public void mouseClicked(MouseEvent me)
	{
		TreePath path = tree.getPathForLocation(me.getX(), me.getY());
		if (path == null)
			return;
		if (me.getX() > tree.getPathBounds(path).x + hotspot)
			return;

		if (selectable != null && !selectable.isSelectable(path))
			return;

		boolean selected = selectionModel.isPathSelected(path, selectionModel.isDigged());

		if (!selected) {
			tree.expandPath(path);
		}

		selectionModel.removeTreeSelectionListener(this);

		try
		{
			if (selected)
				selectionModel.removeSelectionPath(path);
			else
				selectionModel.addSelectionPath(path);
		} finally
		{
			selectionModel.addTreeSelectionListener(this);
			tree.treeDidChange();

			for (SelectionChangeListener scl : scls) {
				scl.selectionChanged(new SelectionChangeEvent(path));
			}
		}

	}

	public CheckTreeSelectionModel getSelectionModel()
	{
		return selectionModel;
	}

	public void valueChanged(TreeSelectionEvent e)
	{
		tree.treeDidChange();
	}
}
