package Weka;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import Untils.DBConnection;
import Untils.DBServiceImpl;
import Untils.FileServiceImpl;
import Weka.Classify.WekaClassifier;
import convertProject.Converter;
import convertProject.Student;
import convertProject.Subject;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.gui.sql.DbUtils;

public class predict4AFile {
	public List<Student> getListStudent(File f) throws SQLException, InvalidFormatException, IOException {
		Connection conn = DBConnection.getConnection();
		List<Student> stList = new ArrayList<Student>();
		XSSFWorkbook workbook = new XSSFWorkbook(f);
		Sheet sheet = workbook.getSheetAt(0);
		Row headerRow = sheet.getRow(0);
		for (int i = 1; i < sheet.getLastRowNum(); i++) {
			System.out.println("row" + i);
			Student st = new Student();
			Row row = sheet.getRow(i);
			st.setStudentID(row.getCell(0).toString());
			int j = 2;
			while (j < row.getLastCellNum()) {
				int slh = (int) row.getCell(j + 1).getNumericCellValue();
				if (slh > 0) {
					String sjName = headerRow.getCell(j).getStringCellValue();
					Subject sj = DBServiceImpl.getSubjectByName(sjName, conn);
					if (sj != null) {
						sj.setNumGrade(row.getCell(j).getNumericCellValue());
						sj.setSoLanHoc(slh);
						st.getSubjectsScore().add(sj);
						j = j + 2;
					} else {
						break;
					}
				} else {
					j = j + 2;
				}
			}
			stList.add(st);
		}
		return stList;
	}

	public List<Boolean> getListResult(File f,WekaClassifier wekaClass, Classifier classifier) throws Exception {
		List<Student> stList = getListStudent(f);
		Converter cv = new Converter();
		FileServiceImpl fsi = new FileServiceImpl();
		Run run = new Run();
		
		List<Boolean> results = new ArrayList<Boolean>();
		for (int i = 0; i < stList.size(); i++) {
//			System.out.println(i);
			
			cv.exportAStudentToArff(stList.get(i), "file/Import1File/Test/" + stList.get(i).getStudentID() + ".arff");
			fsi.exportStudiedTrainFileArff(stList.get(i), "file/Import1File/Train/" + stList.get(i).getStudentID() + "studied.arff");
			
			Boolean r = run.run(wekaClass, "file//Import1File/Train/" + stList.get(i).getStudentID() + "studied.arff",
					"file/Import1File/Test/" + stList.get(i).getStudentID() + ".arff", 30, classifier).get(0);
			results.add(r);
			
		}
//		for (int i = 0; i < results.size(); i++) {
//			System.out.println(results.get(i));
//		}
		return results;

	}
	

	public static void main(String[] args) throws Exception {
//		Converter cv = new Converter();
//		Student st = cv.getStudentInfoFromTxt(new File("file/bb.txt"));
//		cv.exportAStudentToArff(st, "file/test.arff");
//		FileServiceImpl fileServiceImpl = new FileServiceImpl();
//		Student st2 = fileServiceImpl.getStudentFromArffFile((new File("file/test.arff")));
//		fileServiceImpl.exportStudiedTrainFileArff(st2, "file/train.arff");
//		
//		Run run = new Run();
//		Classify c = new Classify();
//		
//		NaiveBayes bayes = (NaiveBayes) c.getClassifier(WekaClassifier.NAIVE_BAYES);
//		c.setBayes(bayes, true, true, false);
//		
//		List<Boolean> a =	run.run(WekaClassifier.NAIVE_BAYES, "file/train.arff", "file/test.arff", 30, bayes);
//		for (int i = 0; i < a.size(); i++) {
//			System.out.println(a.get(i) +"As");
//			
//		}
		Classify c = new Classify();
		NaiveBayes bayes = (NaiveBayes) c.getClassifier(WekaClassifier.NAIVE_BAYES);
		c.setBayes(bayes, true, true, false);
		WekaClassifier wekaclass = WekaClassifier.NAIVE_BAYES;
		predict4AFile m = new predict4AFile();
		m.getListResult(new File("file/Export2.xlsx"), wekaclass, bayes);
	}

}
