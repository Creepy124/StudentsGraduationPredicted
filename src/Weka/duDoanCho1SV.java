package Weka;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import Untils.DBServiceImpl;
import Untils.FileServiceImpl;
import Weka.Classify.WekaClassifier;
import convertProject.Converter;
import convertProject.Student;
import convertProject.convert;
import weka.classifiers.trees.J48;
import weka.gui.simplecli.Cls;

public class duDoanCho1SV {
public static void main(String[] args) throws Exception {
//	DBServiceImpl db = new DBServiceImpl("dudoantotnghiep", "root", "1234");
	FileServiceImpl fs = new FileServiceImpl();
	
	Classify c = new Classify();
	// Gain ratio
	J48 j48 = (J48) c.getClassifier(WekaClassifier.J48);
	
	c.setJ48(j48, false, 0.2, 2, 3, false, 1);
	
	//tao file train test
	Converter cv = new Converter();
	
	//tao file test 
	long begin = System.currentTimeMillis();
		Student st = cv.getStudentInfoFromTxt(new File("file/bb.txt"));
		long end = System.currentTimeMillis();
		System.out.println("Time: " + (end - begin) + " ms");
		
		cv.exportAStudentToXLSX(st, new File("file/1Student/test.xlsx"));
		end = System.currentTimeMillis();
		System.out.println("Time: " + (end - begin) + " ms");
		
		cv.exportAStudentToArff(st, "file/1Student/test.arff");
		end = System.currentTimeMillis();
		System.out.println("Time: " + (end - begin) + " ms");
		
	// tao file train, phuong thuc nay tu tao ra file csv tuong ung
//		fs.exportStudiedFile(st, "file/1Student/train.xlsx");
		fs.exportStudiedTrainFileArff(st, "file/1Student/train.arff");
		end = System.currentTimeMillis();
		System.out.println("Time: " + (end - begin) + " ms");
		
	Run run = new Run();

	String train = "file/1Student/train.arff";
	String test = "file/1Student/test.arff";
	
	List<Boolean> list = run.run(WekaClassifier.J48, train, test, 30, j48);
	end = System.currentTimeMillis();
	System.out.println("Time: " + (end - begin) + " ms");
	
	for(Boolean b: list) {
		System.out.println(b);
	}
}
}
