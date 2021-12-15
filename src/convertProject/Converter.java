package convertProject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import Untils.ConvertServiceImpl;
import Untils.DBConnection;
import Untils.DBServiceImpl;
import Untils.VNCharactersUtils;
import weka.core.converters.ConverterUtils;
import weka.gui.sql.DbUtils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class Converter {
	List<Subject> listSubject;
	DBServiceImpl db = new DBServiceImpl("dudoantotnghiep", "root", "1234");
	public Converter() throws SQLException {
		listSubject = db.getSubjectList();
	}
	
	static List<Subject> dsCoban = new ArrayList<Subject>();
	static List<Subject> dsChuyenNganh = new ArrayList<Subject>();
	static List<Subject> dstuChon1 = new ArrayList<Subject>();
	static List<Subject> dstuchon2 = new ArrayList<Subject>();
	static List<Subject> dstuchon3 = new ArrayList<Subject>();
	static List<String> dsmamonhoc = new ArrayList<String>();
	
	static ConvertServiceImpl convertUtils = new ConvertServiceImpl();
	

	public Student getStudentInfoFromTxt(File txtfile) throws SQLException {
		for (int i = 0; i < listSubject.size(); i++) {
			dsmamonhoc.add(listSubject.get(i).getSubjectID());
		}

		Student student = new Student();

		// Tao file csv

		BufferedReader br = null;

		try {
			java.io.FileReader fr = new java.io.FileReader(txtfile);
			br = new BufferedReader(fr);

			String line;
			String[] lineWords;

			int id = 0;

			while ((line = br.readLine()) != null) {
				lineWords = line.split("\t");
				if (lineWords[0].equals("Mã sinh viên ")) {

					student.setStudentID(lineWords[1]);
				} else if (lineWords[0].equals("Tên sinh viên ")) {
					student.setStudentName(lineWords[1]);
				} else if (lineWords.length > 1) {
					if (dsmamonhoc.contains(lineWords[1]) && (Integer.parseInt(lineWords[0]) > id)) {
						Subject newsj = new Subject("");
						id = Integer.parseInt(lineWords[0]);
						newsj.setSubjectName(lineWords[2]);
						newsj.setSubjectID(lineWords[1]);
						if (lineWords[8].equals("  ")) {
							continue;
						}
						newsj.setNumGrade(convertUtils.get4thScore(Double.parseDouble(lineWords[8])));
						newsj.setTinchi(Integer.parseInt(lineWords[3]));
						newsj.setGroupID(listSubject.get(dsmamonhoc.indexOf(lineWords[1])).getGroupID());
						student.adjustASubject(newsj);
//						System.out.println(newsj.getSoLanHoc());
						listSubject.set(dsmamonhoc.indexOf(lineWords[1]), newsj);
					}
				}

			}

			Map<String, List<Subject>> mapOfList = student.getDSs();
			dsCoban = mapOfList.get("CB");
			dsChuyenNganh = mapOfList.get("CN");
			dstuChon1 = mapOfList.get("TC1");
			dstuchon2 = mapOfList.get("TC2");
			dstuchon3 = mapOfList.get("TC3");

		} catch (FileNotFoundException e) {
			System.out.println("File not found: " + txtfile.getAbsolutePath());
		} catch (IOException e) {
			System.out.println("Unable to read file: " + txtfile.getAbsolutePath());
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				System.out.println("Unable to close file: " + txtfile.getAbsolutePath());
			} catch (NullPointerException ex) {
			}
		}
		return student;
	}

	public void exportCSV(String source, String des) throws UnsupportedEncodingException, SQLException {

		Student st = getStudentInfoFromTxt(new File(source));
		exportStudentToCSV(st, new File(des));
	}

	public void exportXLSX(String source, String des) throws SQLException {
		Student st = getStudentInfoFromTxt(new File(source));
		exportAStudentToXLSX(st, new File(des));

	}

	public void exportStudentToCSV(Student st, File filecsv) throws UnsupportedEncodingException {

		StringBuffer header = new StringBuffer();
		StringBuffer studentinfo = new StringBuffer();
		for (int i = 0; i < st.getSubjectsScore().size(); i++) {
			Subject subject = st.getSubjectsScore().get(i);
			header.append(VNCharactersUtils.removeAccent(subject.getSubjectName()) + ",");
			header.append("So lan hoc " + VNCharactersUtils.removeAccent(subject.getSubjectName()) + ",");
			studentinfo.append(subject.getNumGrade() + ",");
			studentinfo.append(subject.getSoLanHoc() + ",");
		}
		if (dsCoban.size() > 0) {
			header.append(new String("Diem mon co ban".getBytes(), "UTF-8") + ",");
			studentinfo.append(convertUtils.getLetterScoreFrom4thScore(convertUtils.calculateGrade(dsCoban)) + ",");
		}
		if (dsChuyenNganh.size() > 0) {
			header.append(new String("Diem mon chuyen nganh".getBytes(), "UTF-8") + ",");
			studentinfo
					.append(convertUtils.getLetterScoreFrom4thScore(convertUtils.calculateGrade(dsChuyenNganh)) + ",");
		}
		if (dstuChon1.size() > 0) {
			header.append(new String("Diem mon tu chon 1".getBytes(), "UTF-8") + ",");
			studentinfo.append(convertUtils.getLetterScoreFrom4thScore(convertUtils.calculateGrade(dstuChon1)) + ",");
		}
		if (dstuchon2.size() > 0) {
			header.append(new String("Diem mon tu chon 2".getBytes(), "UTF-8") + ",");
			studentinfo.append(convertUtils.getLetterScoreFrom4thScore(convertUtils.calculateGrade(dstuchon2)) + ",");
		}
		if (dstuchon3.size() > 0) {
			header.append(new String("Diem mon tu chon 3".getBytes(), "UTF-8"));
			studentinfo.append(convertUtils.getLetterScoreFrom4thScore(convertUtils.calculateGrade(dstuchon3)) + ",");
		}
		header.append(new String("Tot Nghiep".getBytes(), "UTF-8"));
		studentinfo.append("?");
		try {
			OutputStreamWriter os = new OutputStreamWriter(new FileOutputStream(filecsv));
			try {
				os.write(header.toString());
				os.write("\n");
				os.write(studentinfo.toString());
				os.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Unable to write to file");
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("File not found");
		}

	}

	public void exportAStudentToXLSX(Student st, File filexlsx) {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet();

		Row headerRow = sheet.createRow(0);
		Row infoRow = sheet.createRow(1);
		int columnCount = 0;
		for (int i = 0; i < st.getSubjectsScore().size(); i++) {
			Subject subject = st.getSubjectsScore().get(i);

			headerRow.createCell(columnCount).setCellValue(subject.getSubjectName());
			infoRow.createCell(columnCount).setCellValue(subject.getNumGrade());
			columnCount++;

			headerRow.createCell(columnCount).setCellValue("So Lan Hoc " + subject.getSubjectName());
			infoRow.createCell(columnCount).setCellValue(subject.getSoLanHoc());
			columnCount++;
		}
		if (dsCoban.size() > 0) {
			headerRow.createCell(columnCount).setCellValue("Diem mon co ban");
			infoRow.createCell(columnCount)
					.setCellValue(convertUtils.getLetterScoreFrom4thScore(convertUtils.calculateGrade(dsCoban)));
			columnCount++;
		}
		if (dsChuyenNganh.size() > 0) {
			headerRow.createCell(columnCount).setCellValue("Diem mon chuyen nganh");
			infoRow.createCell(columnCount)
					.setCellValue(convertUtils.getLetterScoreFrom4thScore(convertUtils.calculateGrade(dsChuyenNganh)));
			columnCount++;
		}
		if (dstuChon1.size() > 0) {
			headerRow.createCell(columnCount).setCellValue("Diem mon tu chon 1");
			infoRow.createCell(columnCount)
					.setCellValue(convertUtils.getLetterScoreFrom4thScore(convertUtils.calculateGrade(dstuChon1)));
			columnCount++;
		}
		if (dstuchon2.size() > 0) {
			headerRow.createCell(columnCount).setCellValue("Diem mon tu chon 2");
			infoRow.createCell(columnCount)
					.setCellValue(convertUtils.getLetterScoreFrom4thScore(convertUtils.calculateGrade(dstuchon2)));
			columnCount++;
		}
		if (dstuchon3.size() > 0) {
			headerRow.createCell(columnCount).setCellValue("Diem mon tu chon 3");
			infoRow.createCell(columnCount)
					.setCellValue(convertUtils.getLetterScoreFrom4thScore(convertUtils.calculateGrade(dstuchon3)));
			columnCount++;
		}
		headerRow.createCell(columnCount).setCellValue("Tot nghiep");
		infoRow.createCell(columnCount).setCellValue("?");

		try (FileOutputStream outputStream = new FileOutputStream(filexlsx)) {
			workbook.write(outputStream);
			outputStream.close();
		} catch (FileNotFoundException e) {
			System.out.println("XLSX file not found");
		} catch (IOException e) {
			System.out.println("unable write to XLSX");
		}
	}

	public void exportAStudentToArff(Student student, String path) throws IOException {
		PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(path))));
//		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(path)));
		pw.write("@relation test");
		pw.write("\n\n");
		List<Subject> listSubject = student.getSubjectsScore();
		Map<String, List<Subject>> mapOfList = student.getDSs();
		StringBuffer sb = new StringBuffer();
		ConvertServiceImpl convertUtils = new ConvertServiceImpl();
		for (int i = 0; i < listSubject.size(); i++) {
			String tenmh = VNCharactersUtils.removeAccent(listSubject.get(i).getSubjectName());
//			System.out.println(tenmh);
			pw.write("@attribute " + "'" + tenmh + "'" + " numeric" + "\n");
			pw.write("@attribute " + "'So lan hoc " + tenmh + "'" + " numeric" + "\n");
			sb.append(convertUtils.getLetterScoreFrom4thScore(listSubject.get(i).getNumGrade()) + ",");
			sb.append(listSubject.get(i).getSoLanHoc() + ",");
		}
		if (mapOfList.get("CB").size() > 0) {
			pw.write("@attribute " + "'" + "Diem mon co ban" + "'"+ " numeric" + "\n");
			sb.append(convertUtils.calculateGrade(mapOfList.get("CB"))+ ",");
		}
		if (mapOfList.get("CN").size() > 0) {
			pw.write("@attribute " + "'" + "Diem mon chuyen nganh" +"'"+ " numeric" + "\n");
			sb.append(convertUtils.calculateGrade(mapOfList.get("CN"))+ ",");
		}
		if (mapOfList.get("TC1").size() > 0) {
			pw.write("@attribute " + "'" + "Diem mon tu chon 1" + "'"+" numeric" + "\n");
			sb.append(convertUtils.calculateGrade(mapOfList.get("TC1"))+ ",");
		}
		if (mapOfList.get("TC2").size() > 0) {
			pw.write("@attribute " + "'" + "Diem mon tu chon 2" + "'"+" numeric" + "\n");
			sb.append(convertUtils.calculateGrade(mapOfList.get("TC2"))+ ",");
		}
		if (mapOfList.get("TC3").size() > 0) {
			pw.write("@attribute " + "'" + "Diem mon tu chon 3" + "'"+" numeric" + "\n");
			sb.append(convertUtils.calculateGrade(mapOfList.get("TC3"))+ ",");
		}
		pw.write("@attribute " + "'" + "Tot nghiep" +"'"+ " {True,False}" + "\n");
		sb.append("?");
		pw.write("\n");
		pw.write("@data"+"\n");
		pw.write(sb.toString());
		pw.close();
	}

	public static void main(String[] args) throws UnsupportedEncodingException, SQLException {
		Converter cv = new Converter();
//		String source = "file/bb.txt";
//		String des = "file/export.xlsx";
//		cv.exportXLSX(source, des);
//		String des2 = "file/export.csv";
//		cv.exportCSV(source, des2);
		try {
			Student st = cv.getStudentInfoFromTxt(new File("file/bb.txt"));
			cv.exportAStudentToArff(st, "file/test.arff");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
