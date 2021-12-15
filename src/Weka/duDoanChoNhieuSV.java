package Weka;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

import Untils.DBServiceImpl;
import Untils.FileServiceImpl;
import Weka.Classify.WekaClassifier;
import convertProject.convert;
import weka.classifiers.trees.J48;

public class duDoanChoNhieuSV {
public static void main(String[] args) throws SQLException, FileNotFoundException, IOException {
	DBServiceImpl db = new DBServiceImpl("dudoantotnghiep", "root", "1234");
	FileServiceImpl fs = new FileServiceImpl();
	
	Classify c = new Classify();
	// Gain ratio
	J48 j48 = (J48) c.getClassifier(WekaClassifier.J48);
	
	c.setJ48(j48, false, 0.2, 2, 3, false, 1);
	
	convert convert = new convert();
	String source = "2016_khoacntt.xls";
	String des = 	"docs/import.xlsx";
//	convert.mainMethod(source, des);
//	fs.convertToCsv("docs/import.xlsx");
	db.loadFile("E:\\\\HDD\\\\wordspace\\\\ProjectWeka2\\\\docs\\\\import.csv", "final", ",");
	
}
}
