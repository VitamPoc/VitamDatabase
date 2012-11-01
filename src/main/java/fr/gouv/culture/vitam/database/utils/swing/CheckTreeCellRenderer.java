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

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

/**
 * @author Santhosh Kumar T
 * @email santhosh@in.fiorano.com
 */

public class CheckTreeCellRenderer extends JPanel implements TreeCellRenderer
{
	private static final long serialVersionUID = 5434803647397586147L;

	private CheckTreeSelectionModel selectionModel;
	private TreePathSelectable selectable;
	private TreeCellRenderer delegate;
	private TristateCheckBox checkBox;

	public CheckTreeCellRenderer(TreeCellRenderer delegate, CheckTreeSelectionModel selectionModel,
			TreePathSelectable selectable)
	{
		this.delegate = delegate;
		this.selectionModel = selectionModel;
		this.selectable = selectable;
		setLayout(new BorderLayout());
		setOpaque(false);
		setFont(((JComponent) delegate).getFont());
		checkBox = new TristateCheckBox();
		checkBox.setOpaque(false);
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected,
			boolean expanded,
			boolean leaf, int row, boolean hasFocus)
	{
		Component renderer = delegate
				.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

		TreePath path = tree.getPathForRow(row);
		if (path != null)
		{
			if (selectionModel.isPathSelected(path, selectionModel.isDigged()))
				checkBox.setState(Boolean.TRUE);
			else
				checkBox.setState(selectionModel.isDigged()
						&& selectionModel.isPartiallySelected(path) ? null
						: Boolean.FALSE);
		}
		removeAll();
		checkBox.setVisible(path == null || selectable == null || selectable.isSelectable(path));
		add(checkBox, BorderLayout.WEST);
		add(renderer, BorderLayout.CENTER);
		return this;
	}
}
