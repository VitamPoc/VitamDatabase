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

/**
 * @author "Frederic Bregier"
 * 
 */
public enum DbSqlKeywords {
	SELECT("SELECT fieldx FROM tablex WHERE condx"), 
	DISTINCT, WHERE, NOT, IS_NULL("IS NULL"), IS_NOT_NULL("IS NOT NULL"), 
	ORDER_BY("ORDER BY"), ASC, DESC, 
	ALL, AS, FROM, LEFT, RIGHT, OUTER, JOIN, INNER, CROSS, NATURAL, ON,
	AND, OR, EXISTS, ANY, SOME, 
	BETWEEN, IN, DISTINCT_FROM("DISTINCT FROM"),
	LIKE, ESCAPE, REGEXP, GROUP_BY("GROUP BY"), HAVING, 
	UNION("UNION (SELECT * FROM tablex)"), MINUS("MINUS (SELECT * FROM tablex)"), 
	EXCEPT("EXCEPT (SELECT * FROM tablex)"), INTERSECT("INTERSECT (SELECT * FROM tablex)"),
	NULLS, FIRST, LAST, LIMIT, OFFSET,
	INSERT_INTO("INSERT INTO tablex ( fieldx ) VALUES ( valuex )"), VALUES, DEFAULT, DIRECT, SORTED, SET,
	UPDATE("UPDATE tablex SET fieldx = valuex"), 
	DELETE("DELETE FROM tablex WHERE condx"),
	// need ()
	AVG("AVG( )"), COUNT("COUNT( )"), MAX("MAX( )"), MIN("MIN( )"), SUM("SUM( )"),
	ABS("ABS( )"), MOD("MOD( )"), CEILING("CEILING( )"), CEIL("CEIL( )"),
	FLOOR("FLOOR( )"), RAND("RAND( )"), RANDOM("RANDOM( )"), ROUND("ROUND( )"),
	TRUNCATE("TRUNCATE( )"), TRUNC("TRUNC( )"), ZERO("ZERO( )"),
	LENGTH("LENGTH( )"), CHAR("CHAR( )"), CONCAT("CONCAT( )"),
	LOWER("LOWER( )"), LCASE("LCASE( )"), UPPER("UPPER( )"), UCASE("UCASE( )"),
	LEFT_("LEFT( )"), RIGHT_("RIGHT( )"), LOCATE("LOCATE( )"), POSITION("POSITION( )"),
	LPAD("LPAD( )"), RPAD("RPAD( )"),
	LTRIM("LTRIM( )"), RTRIM("RTRIM( )"), TRIM("TRIM( )"),
	REPEAT("REPEAT( )"), REPLACE("REPLACE( )"), SUBSTRING("SUBSTRING( )"), SUBSTR("SUBSTR( )"),
	CURRENT_DATE("CURRENT_DATE( )"), CURDATE("CURDATE( )"), CURRENT_TIME("CURRENT_TIME( )"), CURTIME(
			"CURTIME( )"),
	CURRENT_TIMESTAMP("CURRENT_TIMESTAMP( )"), NOW("NOW( )"),
	DATEADD("DATEADD( )"), DATEDIFF("DATEDIFF( )"), DAYNAME("DAYNAME( )"), DAY_OF_MONTH(
			"DAY_OF_MONTH( )"),
	DAY_OF_WEEK("DAY_OF_WEEK( )"), DAY_OF_YEAR("DAY_OF_YEAR( )"),
	EXTRACT("EXTRACT( )"), FORMATDATETIME("FORMATDATETIME( )"), HOUR("HOUR( )"), MINUTE("MINUTE( )"),
	MONTH("MONTH( )"), MONTHNAME("MONTHNAME( )"), PARSEDATETIME("PARSEDATETIME( )"),
	QUARTER("QUARTER( )"), SECOND("SECOND( )"), WEEK("WEEK( )"), YEAR("YEAR( )");

	private String name;

	private DbSqlKeywords() {
		this.name = this.name();
	}

	private DbSqlKeywords(String name) {
		this.name = name;
	}

	public String toString() {
		return this.name;
	}

}
