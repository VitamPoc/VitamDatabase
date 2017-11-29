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

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentFactory;
import org.dom4j.Element;

import fr.gouv.culture.vitam.database.DbField;
import fr.gouv.culture.vitam.database.DbSchema;

public class DbCondition {
	static public enum DbOperator {
		// [NOT] Condition [=,<,>,<=,>=,<>, IS NOT NULL, BETWEEN val1 AND val2, LIKE (_=., %= *) string, LENGHT(original field)=valLength]
		Less("<"), LessOrEqual("<="), Equal("="), GreaterOrEqual(">="), Greater(">"), Different("<>"),
		IsNull(" IS NULL "), IsNotNull(" IS NOT NULL "), Between (" BETWEEN "), 
		NotBetween(" NOT BETWEEN "), Like(" LIKE "), NotLike(" NOT LIKE "), LENGTH(" LENGTH ");
		
		public String value;
		private DbOperator(String value) {
			this.value = value;
		}
		public String toString() {
			return value;
		}
		public static DbOperator[] getComparator() {
			return new DbOperator[] { Less, LessOrEqual, Equal, GreaterOrEqual, Greater, Different}; 
		}
	}

	public DbField operand0;
	public Object [] operands;
	public DbCondition.DbOperator operator;
	
	public DbCondition(DbCondition.DbOperator operator, Object ...objects) {
		this.operands = objects;
		this.operator = operator;
		if (! checkCorrectness()) {
			throw new IllegalArgumentException("Arguments incorrectes pour l'operateur: " + operator.name());
		}
		operand0 = (DbField) operands[0];
	}
	
	public boolean checkCorrectness() {
		switch (operator) {
			case Between:
			case NotBetween:
			case LENGTH:
				return operands.length >= 3 && operands[0] instanceof DbField;
			case Different:
			case Equal:
			case Greater:
			case GreaterOrEqual:
			case Less:
			case LessOrEqual:
			case Like:
			case NotLike:
				return operands.length >= 2 && operands[0] instanceof DbField;
			case IsNotNull:
			case IsNull:
				return operands.length >= 1 && operands[0] instanceof DbField;
			default:
				return false;
		}
	}

	public String toString() {
		String sep = "";
		switch (operand0.type) {
			case Types.CHAR:
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
				sep = "'";
		}
		String sep1 = sep;
		if (operands.length > 1 && operands[1] instanceof DbField) {
			sep1 = "";
		}
		switch (operator) {
			case Between:
				return " " + operand0 + " NOT BETWEEN " +sep+ operands[1] +sep+ 
						" AND " +sep+ operands[2] +sep+ " ";
			case NotBetween:
				return " " + operand0 + " BETWEEN " +sep+ operands[1] +sep+ 
						" AND " +sep+ operands[2] +sep+ " ";
			case LENGTH:
				return " LENGTH(" + operand0 + ") " + operands[1] + " " + operands[2] + " ";
			case Different:
				return " " + operand0 + " <> " +sep1+ operands[1] +sep1+ " ";
			case Equal:
				return " " + operand0 + " = " +sep1+ operands[1] +sep1+ " ";
			case Greater:
				return " " + operand0 + " > " +sep1+ operands[1] +sep1+ " ";
			case GreaterOrEqual:
				return " " + operand0 + " >= " +sep1+ operands[1] +sep1+ " ";
			case Less:
				return " " + operand0 + " < " +sep1+ operands[1] +sep1+ " ";
			case LessOrEqual:
				return " " + operand0 + " <= " +sep1+ operands[1] +sep1+ " ";
			case Like:
				return " " + operand0 + " LIKE '" + operands[1] + "' ";
			case NotLike:
				return " " + operand0 + " NOT LIKE '" + operands[1] + "' ";
			case IsNotNull:
				return " " + operand0 + " IS NOT NULL ";
			case IsNull:
				return " " + operand0 + " IS NULL ";
			default:
				return null;
		}
	}
	
	public static DbCondition fromElement(Element condition, DbSchema schema) {
		String opname = condition.attributeValue(DbSchema.OPERATOR_ATTRIBUTE);
		List<Object> obj = new ArrayList<Object>();
		@SuppressWarnings("unchecked")
		List<Element> elts = condition.selectNodes(DbSchema.OPERAND_FIELD);
		if (elts != null) {
			for (Element element : elts) {
				String type = element.attributeValue(DbSchema.TYPE_ATTRIBUTE);
				String val = element.getText();
				if (type.equals(DbField.class.getSimpleName())) {
					DbField field = (DbField) schema.getObject(val.replace(".", DbSchema.FIELDSEP));
					obj.add(field);
				} else {
					obj.add(val);
				}
			}
			elts.clear();
			elts = null;
		}
		DbOperator op = DbOperator.valueOf(opname);
		return new DbCondition(op, obj.toArray());
	}
	public Element toElement() {
		DocumentFactory factory = DocumentFactory.getInstance();
		Element root = factory.createElement(DbSchema.CONDITION_FIELD);
		root.addAttribute(DbSchema.OPERATOR_ATTRIBUTE, operator.name());
		for (int i = 0; i < operands.length; i++) {
			Element op = factory.createElement(DbSchema.OPERAND_FIELD);
			op.addAttribute("rank", Integer.toString(i));
			if (operands[i] instanceof DbField) {
				op.addAttribute(DbSchema.TYPE_ATTRIBUTE, DbField.class.getSimpleName());
			} else {
				op.addAttribute(DbSchema.TYPE_ATTRIBUTE, operands[i].getClass().getSimpleName());
			}
			op.setText(operands[i].toString());
			root.add(op);
		}
		return root;
	}
}