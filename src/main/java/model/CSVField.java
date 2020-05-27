package model;

import java.util.ArrayList;

public enum CSVField {

	VERSION("Version"), FILENAME("File Name"), BUGGY("Buggy"), LOC("LOC"), NREVISIONS("NR"), NFIX("NFix"),
	NAUTH("NAuth"), LOCTOUCHED("LOC_touched"), CHURN("Churn"), MAXCHURN("MAX_Churn"), AVGCHURN("AVG_Churn"), 
	LOCADDED("LOC_added"), MAXLOCADDED("MAX_LOC_added"), AVGLOCADDED("AVG_LOC_added"), AGE("Age");
	
	private String name;
	
	private CSVField(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	public static Iterable<String> getFieldsName(CSVField[] fields) {
		ArrayList<String> res = new ArrayList<>();
		for (CSVField field : fields) {
			res.add(field.toString());
		}
		return res;
	}
}
