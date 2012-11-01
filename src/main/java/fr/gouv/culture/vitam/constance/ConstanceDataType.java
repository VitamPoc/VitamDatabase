package fr.gouv.culture.vitam.constance;

import java.sql.Types;
import java.util.HashMap;

public class ConstanceDataType {
	static final public ConstanceDataType NUM = new ConstanceDataType("NUM", Types.BIGINT);
	static final public ConstanceDataType NUMV = new ConstanceDataType("NUMV", Types.DOUBLE);
	static final public ConstanceDataType AN = new ConstanceDataType("A.N.", Types.VARCHAR);
	static final public ConstanceDataType T = new ConstanceDataType("T", Types.VARCHAR);
	static final public ConstanceDataType SNUM = new ConstanceDataType("SNUM", Types.BIGINT);

	public static HashMap<String, Integer> types = new HashMap<String, Integer>();
	
	public static void initializeTypes() {
		types.put(NUM.name, NUM.type);
		types.put(NUMV.name, NUMV.type);
		types.put(AN.name, AN.type);
		types.put("A.N.", AN.type);
		types.put(T.name, T.type);
		types.put(SNUM.name, SNUM.type);
	}
	
	public String name;
	public int type;
	
	private ConstanceDataType(String name, int type) {
		this.name = name;
		this.type = type;
	}
	
	public static final int getType(String type) {
		if (types.containsKey(type)) {
			return types.get(type);
		}
		for (String name : types.keySet()) {
			if (type.startsWith(name)) {
				return types.get(name);
			}
		}
		return Types.VARCHAR;
	}
}