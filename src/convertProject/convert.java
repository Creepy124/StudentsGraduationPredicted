package convertProject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import java.io.*;
import java.sql.SQLException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import Untils.DBServiceImpl;
import Untils.FileServiceImpl;

public class convert {
	List<String> listMonChuyenNganh;
	List<String> listMonCoBan;
	List<String> listMonTuChon1;
	List<String> listMonTuChon2;
	List<String> listMonTuChon3;
	List<Subject> listSubjects;
	List<String> listPhanLoai;

	public convert() throws SQLException {
		String phanLoai = "Diem mon Co ban,Diem mon Chuyen nganh,Diem mon Tu chon 1,Diem mon Tu chon 2,Diem mon Tu chon 3,"
				+ "Tot nghiep,Som Tre,Loai";

		DBServiceImpl db = new DBServiceImpl("dudoantotnghiep", "root", "1234");
		listMonChuyenNganh = db.getSubjectIDByType("CN");
		listMonCoBan = db.getSubjectIDByType("CB");
		listMonTuChon1 = db.getSubjectIDByType("TC1");
		listMonTuChon2 = db.getSubjectIDByType("TC2");
		listMonTuChon3 = db.getSubjectIDByType("TC3");
		listSubjects = db.getSubjectList();
		listPhanLoai = Arrays.asList(phanLoai.split(","));
	}
//	String subject = "Toan cao cap  A1,Vat Ly 2,Toan cao cap  A2,Cac ng.ly CB cua CN MacLenin,Xac suat thong ke,Phap luat dai cuong,"
//			+ "Duong loi CM cua Dang CSVN,Tu tuong HCM,Toan cao cap A3,Lap trinh co ban,Nhap mon tin hoc,Cau truc may tinh,"
//			+ "Lap trinh nang cao,Nhap mon CN phan mem,Nhap mon he dieu hanh,Cau truc du lieu,Mang may tinh co ban,Thiet ke huong doi tuong,"
//			+ "Nhap mon co so du lieu,He dieu hanh nang cao,Ly thuyet do thi,Lap trinh mang,Phan tich va thiet ke HTTT,Giao tiep Nguoi-may,"
//			+ "Lap trinh Web,Nhap mon tri tue nhan tao,He quan tri co so du lieu,Mang may tinh nang cao,An ninh mang,Quan tri mang,"
//			+ "Thuong mai dien tu,Quan ly du an phan mem,ĐBCL va kiem thu phan mem,An toan va bao mat he thong TT,Chuyen de cong nghe phan mem,"
//			+ "Lap trinh mang nang vao,Marketing can ban,Do hoa may tinh,Data Mining,Data Warehouse,Lap trinh.NET,He thong thong tin quan ly,"
//			+ "Chuyen de DB2,Chuyen de Oracle,Lap trinh tren TB di dong,Lap trinh C++ trong LINUX,He thong thong tin dia ly UD,"
//			+ "Giai phap mang cho DN,Giai phap phan mem chinh phu ĐT,Chuyen de he thong thong tin,Chuyen de mang may tinh & TT,"
//			+ "Chuyen de ma nguon mo,DACN Cong nghe phan mem,ĐACN He thong thong tin,Chuyen de WEB,Tieu luan TN,"
//			+ "Khoa luan tot nghiep,Chuyen de Java,DACN Mang may tinh va TT";

//	String coBan = "202108,202206,202109,200106,202121,202622,200104,200107,202110";
//
//	String chuyenNganh = "214321,214201,214231,214331,214370,214242,214441,214241,214352,214442,214251,214351,214252,214461,214361,214462,214463";
//
//	String tuChon1 = "214451,214282,214275,214271,214483,214383,214379,214464,214376";
//
//	String tuchon2 = "214273,208453,214353,214485,214477,214372,214471,214489,214488,214274,214284,214465,214285,214289,214481,214283";
//
//	String tuchon3 = "214382,214385,214487,214374,214983,214982,214286,214287";

	public void mainMethod(String source, String destination) throws FileNotFoundException, IOException {
		File exportFile = new File(destination);
		if (exportFile.exists()) {
			exportFile.delete();
		}
		exportFile.createNewFile();
		Workbook excel = new XSSFWorkbook();
		
		String[] subjects = new String[listSubjects.size()];
		for (int i = 0; i < listSubjects.size(); i++) {
			subjects[i] = listSubjects.get(i).getSubjectName();
		}

		Sheet sheet = excel.createSheet();

		List<String> tmpHeader = new ArrayList<String>();

		for (int k = 0; k < subjects.length; k++) {
			tmpHeader.add(subjects[k]);
			tmpHeader.add("So lan hoc " + subjects[k]);
		}

		List<String> header = Stream.of(tmpHeader, listPhanLoai).flatMap(Collection::stream)
				.collect(Collectors.toList());

		Row firstRow = sheet.createRow(0);
		firstRow.createCell(0).setCellValue("MSSV");
		firstRow.createCell(1).setCellValue("Ten SV");
		for (int i = 0; i < header.size(); i++) {
			Cell cell = firstRow.createCell(i + 2);
			cell.setCellValue(header.get(i));
		}
		int rowNum = 1;

		List<String> listStudent = getStudentName(source);

		outter: for (String stuName : listStudent) {

			Student stu = getInfo(stuName, source);

			if (!checkStudent(stu)) {
				continue outter;
			}

			List<Subject> listCoBan = getlistGradeOfStudent(stuName, listMonCoBan, source);
			List<Subject> listChuyenNganh = getlistGradeOfStudent(stuName, listMonChuyenNganh, source);
			List<Subject> listTuChon1 = getlistGradeOfStudent(stuName, listMonTuChon1, source);
			List<Subject> listTuChon2 = getlistGradeOfStudent(stuName, listMonTuChon2, source);
			List<Subject> listTuChon3 = getlistGradeOfStudent(stuName, listMonTuChon3, source);

			List<Subject> finalList = Stream.of(listCoBan, listChuyenNganh, listTuChon1, listTuChon2, listTuChon3)
					.flatMap(Collection::stream).collect(Collectors.toList());

			double diemCoBan = getLetterScore(getDiemTong(getLearntSubjects(listCoBan)));
			double diemChuyenNganh = getLetterScore(getDiemTong(getLearntSubjects(listChuyenNganh)));
			double diemTuChon1 = getLetterScore(getDiemTong(getLearntSubjects(listTuChon1)));
			double diemTuChon2 = getLetterScore(getDiemTong(getLearntSubjects(listTuChon2)));
			double diemTuChon3 = getLetterScore(getDiemTong(getLearntSubjects(listTuChon3)));

			String typeGraduate = getTypeGraduate(stu.getDiemtb());
			
			double tc = stu.getSoTinchi();

			int colNum = 2;
			Row row = sheet.createRow(rowNum++);
			row.createCell(0).setCellValue(stu.getStudentID());
			row.createCell(1).setCellValue(stu.getNameStudent());
			for (Subject sub : finalList) {
				row.createCell(colNum++).setCellValue(sub.getNumGrade());
				row.createCell(colNum++).setCellValue(sub.getSoLanHoc());
			}
			row.createCell(colNum++).setCellValue(diemCoBan);
			row.createCell(colNum++).setCellValue(diemChuyenNganh);
			row.createCell(colNum++).setCellValue(diemTuChon1);
			row.createCell(colNum++).setCellValue(diemTuChon2);
			row.createCell(colNum++).setCellValue(diemTuChon3);

			boolean bool;
			if (bool = isOnTime(stu)) {
				row.createCell(colNum++).setCellValue("True");
			} else {
				row.createCell(colNum++).setCellValue("False");
			}
			row.createCell(colNum++).setCellValue(isOnTime2(stu));
			row.createCell(colNum++).setCellValue(typeGraduate);
		}
		FileOutputStream fileOut = new FileOutputStream(exportFile);
		excel.write(fileOut);
		fileOut.close();
		excel.close();
	}

	private boolean checkStudent(Student stu) {
		if (stu.getSohocky() <= 5)
			return false;
		if (stu.getDiemtb() <= 1.0)
			return false;
		if (stu.getSohocky() >= 5 && stu.getSoTinchi() <= 89)
			return false;

		return true;
	}

	private boolean isOnTime(Student stu) {
		if (stu.getSohocky() <= 8 && stu.getSoTinchi() >= 135)
			return true;
		return false;
	}
	
	private String isOnTime2(Student stu) {
		String result ="";
		if (stu.getSohocky() < 8 && stu.getSoTinchi() >= 135)
			result = "Som han";
		if (stu.getSohocky() == 8 && stu.getSoTinchi() >= 135)
			result = "Dung han";
		if (stu.getSohocky() > 8)
			result = "Tre han";
		return result;
	}

	private double getLetterScore(double score) {
		if (score == 4)
			return 4;
		if (score >= 3.5)
			return 3.5;
		if (score >= 3)
			return 3;
		if (score >= 2.5)
			return 2.5;
		if (score >= 2)
			return 2;
		if (score > 1)
			return 1.5;
		if (score == 1)
			return 1;
		else
			return 0;
	}

	private String getTypeGraduate(double score) {
		if (score >= 3.6) {
			return "Xuat sac";
		}
		if (score >= 3.2) {
			return "Gioi";
		}
		if (score >= 2.5) {
			return "Kha";
		}
		if (score >= 2.0) {
			return "Trung Binh";
		}
		if (score >= 1.0) {
			return "Yeu";
		}

		else
			return "Kem";

	}

	private double getDiemTong(List<Subject> listSubject) {
		double result = 0;
		int tinchi = 0;
		for (Subject sub : listSubject) {
			result += sub.getNumGrade() * sub.getTinchi();
			tinchi += sub.getTinchi();
		}
		return (double) Math.round((result / tinchi) * 100) / 100;
	}

	public List<Subject> getlistGradeOfStudent(String stuName, List<String> listSubject, String source)
			throws FileNotFoundException, IOException {
		List<Subject> result = new ArrayList<Subject>();

		for (int i = 0; i < listSubject.size(); i++) {
			result.add(new Subject(listSubject.get(i)));
		}

		POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(source));
		Workbook wb2007 = new HSSFWorkbook(fs);
		Sheet firstSheet = wb2007.getSheetAt(0);
		Iterator<Row> rowIterator = firstSheet.iterator();

		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			if (row.getCell(1).toString().equals(stuName)) {
				Iterator<Cell> cellIterator = row.iterator();

				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					String value = cell.toString();
					if (listSubject.contains(value)) {
						int index = listSubject.indexOf(value);
						int studying = 0;
						int colIndex = cell.getColumnIndex();

						// Điểm chữ
						String letterGrade = row.getCell(colIndex + 4).toString();
						double numGrade = 0;
						System.out.println(cell.getAddress());
						// được miễn học
						if (letterGrade.equals("M")) {
							numGrade = 4.0;
						}
						// Vắng thi
						else if (!letterGrade.equals("V")) {
							if (row.getCell(colIndex + 5).toString().isEmpty()) {
								numGrade = 0;
								studying++;
							}
							// điểm số hệ 4
							else
								numGrade = Double.parseDouble(row.getCell(colIndex + 5).toString());
						}

						Subject sub = result.get(index);
						// nn86860c
						sub.setTinchi(Double.parseDouble(row.getCell(colIndex + 2).toString()));
						// nếu đang học thì k tính
						sub.setSoLanHoc(sub.getSoLanHoc() + 1 - studying);
						if (sub.getNumGrade() < numGrade) {
							sub.setLetterGrade(letterGrade);
							sub.setNumGrade(numGrade);
						}
						// tạch môn
						if (!letterGrade.equals("F"))
							sub.setFail(false);
					}
				}
			}
		}
		return result;
	}

	public List<Subject> getLearntSubjects(List<Subject> listSubjects) {
		for (int i = 0; i < listSubjects.size(); i++) {
			if (listSubjects.get(i).getSoLanHoc() == 0)
				listSubjects.remove(i);
		}
		return listSubjects;
	}

	public Student getInfo(String studentName, String source) throws FileNotFoundException, IOException {
		Student stu = null;
		int sohocki = 0;
		int sotinchi = 0;
		double diem = 0;
		String studentID = "";
		POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(source));
		Workbook wb2007 = new HSSFWorkbook(fs);
		Sheet firstSheet = wb2007.getSheetAt(0);
		Iterator<Row> rowIterator = firstSheet.iterator();

		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();

			Iterator<Cell> cellIterator = row.iterator();

			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();

				String name = row.getCell(1).toString();
				String stringCel = cell.toString();
				// chỉ tính học kỳ chính, đúng hạn sẽ tối đa 8 kì
				if (name.equalsIgnoreCase(studentName)
						&& (stringCel.contains("Học kỳ 1") || stringCel.contains("Học kỳ 2"))) {
					studentID = row.getCell(0).toString();
					sohocki++;
					// điểm tích luỹ hệ 4
					diem = Double.parseDouble(row.getCell(17).toString());
					sotinchi = (int) Double.parseDouble(row.getCell(16).toString());
				}
			}
		}
		stu = new Student(studentID, studentName);
		stu.setDiemtb(diem);
		stu.setSohocky(sohocki);
		stu.setSoTinchi(sotinchi);
		return stu;
	}

	public List<String> getStudentName(String source) throws FileNotFoundException, IOException {
		List<String> result = new ArrayList<String>();

		POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(source));
		Workbook wb2007 = new HSSFWorkbook(fs);
		Sheet firstSheet = wb2007.getSheetAt(0);
		Iterator<Row> rowIterator = firstSheet.iterator();
		Row row = rowIterator.next();
		while (rowIterator.hasNext()) {
			row = rowIterator.next();

			if (row.getCell(1) != null)
				if (!result.contains(row.getCell(1).toString())) {
					result.add(row.getCell(1).toString());
				}
		}
		return result;
	}

	public static void main(String[] args) throws IOException, SQLException {
		convert cv = new convert();
		DBServiceImpl db = new DBServiceImpl("dudoantotnghiep", "root", "1234");
		FileServiceImpl fs = new FileServiceImpl();
		String source = "E:\\HDD\\wordspace\\ProjectWeka2\\2016_khoacntt.xls";
//		String source = "E:\\HDD\\wordspace\\ProjectWeka2\\2016_khoacntt.xls";
		String destination = "E:\\HDD\\wordspace\\ProjectWeka2\\SV16.xlsx";
		cv.mainMethod(source, destination);
//		fs.convertToCsv("E:\\HDD\\wordspace\\ProjectWeka2\\Export4.xlsx");
//		db.loadFile("E:\\\\HDD\\\\wordspace\\\\ProjectWeka2\\\\Export3.csv", "final", ",");
//		for(String s: cv.getStudentName())
//		System.out.println(s);

//	}
	}
}
