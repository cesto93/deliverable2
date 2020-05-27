package launch;

import java.util.logging.Level;
import java.util.logging.Logger;

import exporter.CSVExporter;
import model.EvaluationResult;
import utils.GetProperty;
import weka.ModelComparer;

public class PredictBuggyness {
	private static final Logger LOGGER = Logger.getLogger(PredictBuggyness.class.getName());
	private static final String METRICSUF = "_File.csv";
	private static final String EVALSUF = "_Evaluation.csv";

	public static void main(String[] args) {
		final String[] projName = {GetProperty.getProperty("projectName1"), GetProperty.getProperty("projectName2")};
		LOGGER.info(projName[0]);
		predictBugginess(projName[0]);
		LOGGER.info(projName[1]);
		predictBugginess(projName[1]);
		LOGGER.log(Level.INFO, "Done");
	}
	
	public static void predictBugginess(String projName) {
		EvaluationResult result = ModelComparer.compare(projName, projName + METRICSUF);
		CSVExporter.printEvaluationResult(result, projName + EVALSUF);
	}

}
