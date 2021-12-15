package Weka;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import Untils.DBServiceImpl;
import Untils.FileServiceImpl;
import Weka.Classify.WekaClassifier;
import convertProject.convert;
import weka.classifiers.bayes.NaiveBayes;

public class load1FileVaoDeTest {
	public static void main(String[] args) throws Exception {
		// load du lieu vao DB
		DBServiceImpl db = new DBServiceImpl("dudoantotnghiep", "root", "1234");
		FileServiceImpl fs = new FileServiceImpl();
		convert convert = new convert();
		String source = "2016_khoacntt.xls";
		String des = "file/Import1File/Export.xlsx";
//		convert.mainMethod(source,des);
		
		predict4AFile predict = new predict4AFile();
		Classify c = new Classify();
		NaiveBayes bayes = (NaiveBayes) c.getClassifier(WekaClassifier.NAIVE_BAYES);
		c.setBayes(bayes, true, true, false);
		WekaClassifier wekaClass = WekaClassifier.NAIVE_BAYES;
		long begin = System.currentTimeMillis();
		
		List<Boolean> result = predict.getListResult(new File("file/Import1File/Export.xlsx"), wekaClass, bayes);
		long end = System.currentTimeMillis();
		System.out.println("Time: " + (end - begin) + " ms");
		for (int i = 0; i < result.size(); i++) {
			System.out.println(i + " : " + result.get(i));
		}
	}
}
