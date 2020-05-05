package model;

import java.util.ArrayList;

public class CSVField {
	private static final String[] names =  {"Version", "File Name", "Buggy", "LOC", "NR", "NFix", "NAuth", "LOC_touched",
											"Churn", "Age", "AVG_Churn", "LOC_added", "AVG_LOC_added"};
	
	public enum CSVFields {
		VERSION,
		FILENAME,
	    BUGGY,
	    LOC,
	    NREVISIONS,
	    NFIX,
	    NAUTH,
	    LOCTOUCHED,
	    CHURN,
	    AGE,
	    AVGCHURN,
	    LOCADDED,
	    AVGLOCADDED;
	}
	
	public CSVField() {
		throw new IllegalStateException("Utility class");
	}
	
	public static Iterable<String> getFieldsName(CSVFields[] fields) {
		ArrayList<String> res = new ArrayList<>();
		for (CSVFields field : fields) {
			res.add(getFieldName(field));
		}
		return res;
	}
	
	public static String getFieldName(CSVFields field) {
		return names[field.ordinal()];
	}
}
