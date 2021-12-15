package Weka;
 
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericToNominal;
import weka.filters.unsupervised.attribute.RemoveUseless;
import weka.filters.unsupervised.instance.Randomize;
import weka.filters.unsupervised.instance.RemovePercentage;
import weka.gui.beans.PredictionAppender;
import weka.gui.treevisualizer.PlaceNode2;
import weka.gui.treevisualizer.TreeVisualizer;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import Untils.DBServiceImpl;
import Untils.FileServiceImpl;
import Weka.Classify.WekaClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.evaluation.Prediction;
import weka.classifiers.evaluation.output.prediction.AbstractOutput;
import weka.classifiers.evaluation.output.prediction.PlainText;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.Id3;
import weka.classifiers.trees.J48;
import weka.core.FastVector;
import weka.core.Instances;

public class Run {

	public List<Boolean> run(WekaClassifier classifier, String trainFile, String testFile, int splitPercent,
			Classifier classify) throws Exception {
		// Load data
		
		List<Boolean> result;
		Instances testingData = null;

		DataSource source = new DataSource(trainFile);
		Instances modelData = source.getDataSet();
		
//		RemoveUseless removeUseless = new RemoveUseless();
//		removeUseless.setInputFormat(modelData);
//		modelData = Filter.useFilter(modelData, removeUseless);
		
		if (!testFile.isEmpty()) {
			testingData = new DataSource(testFile).getDataSet();
			if (testingData.classIndex() == -1) {
				testingData.setClassIndex(testingData.numAttributes() - 1);
			}
//			removeUseless.setInputFormat(testingData);
//			testingData = Filter.useFilter(testingData, removeUseless);
		}
		
		// Make the last attribute be the classification attribute
		if (modelData.classIndex() == -1) {
			modelData.setClassIndex(modelData.numAttributes() - 1);
		}

		// ID3 can't process numeric attributes
		if (classifier.equals(WekaClassifier.ID3)) {
			NumericToNominal num = new NumericToNominal();
			num.setInputFormat(modelData);
			modelData = Filter.useFilter(modelData, num);

			if (!testFile.isEmpty()) {
				num.setInputFormat(testingData);
				testingData = Filter.useFilter(testingData, num);
			}
		}

		Instances[] set = new Instances[] {};
		// Just test the accuracy of model
		if (testFile.isEmpty()) {
			// Split model into training set + testing set
			set = splitTrainVal(modelData, splitPercent);
		}
		// test the input data by model and testing data
		else {
			set = new Instances[] { modelData, testingData };
		}

		Instances train = set[0];
		Instances test = set[1];

		// Building model
		classify.buildClassifier(train);
		
		//This following show the Graph of J48
//		final javax.swing.JFrame jf = 
//			       new javax.swing.JFrame("Weka Classifier Tree Visualizer: J48");
//			     jf.setSize(500,400);
//			     jf.getContentPane().setLayout(new BorderLayout());
//			     TreeVisualizer tv = new TreeVisualizer(null,
//			    		 ((J48) classify).graph(),
//			         new PlaceNode2());
//			     jf.getContentPane().add(tv, BorderLayout.CENTER);
//			     jf.addWindowListener(new java.awt.event.WindowAdapter() {
//			       public void windowClosing(java.awt.event.WindowEvent e) {
//			         jf.dispose();
//			       }
//			     });

//			     jf.setVisible(true);
//			     tv.fitToScreen();
//		System.out.println(classify);
		
		// Evaluate model through test set
		StringBuffer predictionSB = new StringBuffer();
		AbstractOutput output = new PlainText();
		output.setHeader(test);
		output.setBuffer(predictionSB);
		Evaluation evaluation = new Evaluation(train);
		evaluation.evaluateModel(classify, test,output);
		if (testFile.isEmpty()) {
			String sumaryString = evaluation.toSummaryString("Results\n ", false) + "\n" + evaluation.toMatrixString()
					+ "\n" + evaluation.toClassDetailsString();
//			System.out.println(sumaryString);
			writeResult(sumaryString, "docs/Compare/" + classifier + ".txt");
			return null;
		} else {
			String predictionsString = output.getBuffer().toString();
//			System.out.println(predictionsString);
			result = new LinkedList<Boolean>();
			String[] splitString = predictionsString.split("\n");
			for (String str : splitString) {
				if (str.contains("False"))
					result.add(false);
				else
					result.add(true);
			}
			return result;

		}
	}

	public void convertCSVtoARFF(String path) throws IOException {
		 CSVLoader loader = new CSVLoader();
		    loader.setSource(new File(path));
		    Instances data = loader.getDataSet();

		    // save ARFF
		    ArffSaver saver = new ArffSaver();
		    path = path.substring(0, path.length()-4)+".arff";
		    System.out.println(path);
		    saver.setInstances(data);
		    saver.setFile(new File(path));
		    saver.writeBatch();
	}
	private void writeResult(String result, String path) throws FileNotFoundException {
		File file = new File(path);
		if (file.exists()) {
			file.delete();
			file = new File(path);
		}
		PrintWriter pw = new PrintWriter(file);
		pw.write(result);
		pw.flush();
		pw.close();
	}

	public static Instances[] splitTrainVal(Instances data, double percent) throws Exception {

		// Randomize data
		RemoveUseless rm = new RemoveUseless();
		rm.setInputFormat(data);
		data = Filter.useFilter(data, rm);
		
		Randomize rand = new Randomize();
		rand.setInputFormat(data);
		rand.setRandomSeed(50);
		data = Filter.useFilter(data, rand);

		
		
		// Remove testPercentage from data to get the train set
		RemovePercentage rp = new RemovePercentage();
		rp.setInputFormat(data);
		rp.setPercentage(percent);
		Instances train = Filter.useFilter(data, rp);

		// Remove trainPercentage from data to get the test set
		rp = new RemovePercentage();
		rp.setInputFormat(data);
		rp.setPercentage(percent);
		rp.setInvertSelection(true);
		Instances test = Filter.useFilter(data, rp);

		return new Instances[] { train, test };
	}

	public void exportAllDataToTrain(String des, int ignoreColumn, DBServiceImpl db, String table) {
		FileServiceImpl fs = new FileServiceImpl();
		try {
			db.exportExcel(des, table, ignoreColumn);
			fs.convertToCsv(des);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) throws Exception {
		DBServiceImpl db = new DBServiceImpl("dudoantotnghiep", "root", "1234");
		
		Run run = new Run();
//		run.exportAllDataToTrain("docs/Compare/ex.xlsx", 2, db, "final");
		Classify c = new Classify();

		// Gain ratio
		J48 j48 = (J48) c.getClassifier(WekaClassifier.J48);
		c.setJ48(j48, false, 0.7, 2, 3, false, 1);

		// Infomation gain
		Id3 id3 = (Id3) c.getClassifier(WekaClassifier.ID3);

		// KNN
		IBk ibk = (IBk) c.getClassifier(WekaClassifier.IBk);
		// BallTree LinearNNSearch CoverTree FilteredNeighbourSearch KDTree
		c.setKNN(ibk, 5, "I", true, "LinearNNSearch");

		// Bayes
		NaiveBayes bayes = (NaiveBayes) c.getClassifier(WekaClassifier.NAIVE_BAYES);
		c.setBayes(bayes, true, true, false);

		// test 30%
		run.run(WekaClassifier.J48, "docs/Compare/ex.csv","", 30, j48);
//		run.run(WekaClassifier.ID3, "docs/Compare/ex.csv","", 30, id3);
//		run.run(WekaClassifier.IBk, "docs/Compare/ex.csv", "", 30, ibk);
//		
//		run.run(WekaClassifier.NAIVE_BAYES, "docs/Compare/ex.csv", "", 30, bayes);
		
	}
}
