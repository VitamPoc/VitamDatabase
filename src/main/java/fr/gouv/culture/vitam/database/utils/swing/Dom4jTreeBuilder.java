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

import java.util.Iterator;

import javax.swing.tree.DefaultMutableTreeNode;

import org.dom4j.Attribute;
import org.dom4j.Element;

/**
 * @author "Frederic Bregier"
 * 
 */
public class Dom4jTreeBuilder {

	private DefaultMutableTreeNode currentNode = null;
	private DefaultMutableTreeNode rootNode = null;
	private Element root;

	public Dom4jTreeBuilder(Element root) {
		this.root = root;
	}

	public DefaultMutableTreeNode parseXml() {
		rootNode = new DefaultMutableTreeNode(root.getName());
		currentNode = rootNode;
		addAttribute(root);
		addElement(root);
		return rootNode;
	}

	public void addAttribute(Element curnode) {
		for (@SuppressWarnings("unchecked")
		Iterator<Attribute> it = curnode.attributeIterator(); it.hasNext();) {
			Attribute attr = it.next();
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(attr.getName() + "="
					+ attr.getText());
			currentNode.add(node);
		}
	}

	public void addElement(Element curnode) {
		DefaultMutableTreeNode previousNode = currentNode;
		for (@SuppressWarnings("unchecked")
		Iterator<Element> it = curnode.elementIterator(); it.hasNext();) {
			Element elt = it.next();
			DefaultMutableTreeNode node = null;
			if (elt.getTextTrim().length() == 0) {
				node = new DefaultMutableTreeNode(elt.getName());
				previousNode.add(node);
			} else {
				node = new DefaultMutableTreeNode(elt.getName());
				DefaultMutableTreeNode subnode = new DefaultMutableTreeNode("T=" + elt.getText());
				node.add(subnode);
				previousNode.add(node);
			}
			currentNode = node;
			addAttribute(elt);
			addElement(elt);
		}
		currentNode = previousNode;
	}

}
