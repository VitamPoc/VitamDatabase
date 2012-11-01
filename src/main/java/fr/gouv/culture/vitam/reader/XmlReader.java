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
package fr.gouv.culture.vitam.reader;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

/**
 * Vitam Reader
 * @author "Frederic Bregier"
 * 
 */
public class XmlReader implements VitamReader {
	public File file;
	public String [] xpaths;
	private Document document;
	private List<Node> nodes;

	/**
	 * @param filexml
	 * @param xpaths First xpath is the global xpath (foreach xpaths[0]) while others are subfields
	 * @throws DocumentException 
	 */
	@SuppressWarnings("unchecked")
	public XmlReader(File filexml, String[] xpaths) throws DocumentException {
		this.file = filexml;
		this.xpaths = xpaths;

		SAXReader saxReader = new SAXReader();
		document = saxReader.read(file);
		nodes = document.selectNodes(xpaths[0]);
	}

	@Override
	public String[] readOneLine() throws IOException {
		if (this.nodes != null) {
			if (this.nodes.isEmpty()) {
				this.nodes = null;
				return null;
			}
			String [] result = new String[xpaths.length-1];
			Node root = this.nodes.remove(0);
			for (int i = 1; i < xpaths.length; i++) {
				Node sub = root.selectSingleNode(xpaths[i]);
				if (sub != null) {
					result[i-1] = sub.getText();
				} else {
					result[i-1] = null;
				}
			}
			return result;
		}
		return null;
	}

	@Override
	public String[] readOneLine(int rank) throws IOException {
		return readOneLine();
	}

}
