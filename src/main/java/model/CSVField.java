package model;

import java.util.ArrayList;

public enum CSVField {
	
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
		
		private static final String[] names =  {"Version", "File Name", "Buggy", "LOC", "NR", "NFix", "NAuth", "LOC_touched",
				"Churn", "Age", "AVG_Churn", "LOC_added", "AVG_LOC_added"};
		
		 @Override
		 public String toString() {
		    return names[this.ordinal()];
		 }
		 
		 public static Iterable<String> getFieldsName(CSVField[] fields) {
				ArrayList<String> res = new ArrayList<>();
				for (CSVField field : fields) {
					res.add(field.toString());
				}
				return res;
		}
}
	
	
