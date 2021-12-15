package Untils;

import java.sql.Statement;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.mysql.jdbc.ResultSetMetaData;

import convertProject.Student;
import convertProject.Subject;

/*
 * This class contain all methods that relative to files that were downloaded from remote such as: 
 * move file to another place,
 * convert xlsx to csv
 * write to a file (error file)
 */
public class FileServiceImpl implements FileService {

	@Override
	public boolean moveFile(String target_dir, File file) throws IOException {
		BufferedInputStream bReader = new BufferedInputStream(new FileInputStream(file));
		BufferedOutputStream bWriter = new BufferedOutputStream(
				new FileOutputStream(target_dir + File.separator + file.getName()));
		byte[] buff = new byte[1024 * 10];
		int data = 0;
		while ((data = bReader.read(buff)) != -1) {
			bWriter.write(buff, 0, data);
		}
		bReader.close();
		bWriter.close();
		file.delete();
		return true;
	}

	@Override
	public void writeLinesToFile(String fPath, String lines) throws IOException {
		Path file = Paths.get(fPath);
		DataOutputStream dos;
		dos = new DataOutputStream(Files.newOutputStream(file, StandardOpenOption.APPEND));
		dos.write(lines.getBytes());
		dos.flush();
	}

	public String convertToCsv(String path) throws EncryptedDocumentException, IOException {
		File file = new File(path);
		InputStream is = new FileInputStream(file);
		Workbook wb = WorkbookFactory.create(is);
		Sheet sheet = wb.getSheetAt(0);
		Iterator<Row> rowIterator = sheet.iterator();
		String str = "";
		int line = 0;
		int colNum = 0;
		while (rowIterator.hasNext()) {
			line++;
			Row fRow = rowIterator.next();
			if (line == 1) {
				colNum = fRow.getLastCellNum();
			}
			for (int i = 0; i < colNum; i++) {
				DataFormatter df = new DataFormatter();
				Cell cell = fRow.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
				String s = "";
				if (cell.getCellType() == CellType.FORMULA) {
					if (cell.getCachedFormulaResultType() == CellType.NUMERIC) {
						str += (cell.getNumericCellValue() + "").trim();
						if (i != colNum - 1) {
							str += ",";
						}
					}

					else if (cell.getCachedFormulaResultType() == CellType.STRING) {
						str += (cell.getStringCellValue()).trim();

					}
				}

				else if (cell.getCellType() == CellType.BLANK || cell == null) {
					if (i != colNum - 1) {
						str += ",";
					}
				}

				else if (cell.getCellType() != CellType.BLANK) {
					str += (df.formatCellValue(cell) + "").trim();
					if (i != colNum - 1) {
						str += ",";
					}
				}
			}
			str += "\n";
		}
		str = VNCharactersUtils.removeAccent(str.trim());
		File fileout = new File(
				file.getParent() + file.separator + file.getName().substring(0, file.getName().length() - 5) + ".csv");
		FileOutputStream fos = new FileOutputStream(fileout);
		fos.write(str.getBytes(StandardCharsets.UTF_8));
		return fileout.getName();
	}

	public Student getStudentFromCSVfile(File file) {

		try {
			BufferedReader breader = new BufferedReader(new FileReader(file));
			String[] header = breader.readLine().split(",");
			String[] info = breader.readLine().split(",");
			Student student = new Student(info[0], info[1]);
			for (int i = 1; i < info.length - 2; i += 2) {
				if (Integer.parseInt(info[i]) > 0) {
					Subject sj = new Subject(header[i - 1]);
					student.getSubjectsScore().add(sj);
				}
			}
			return student;
		} catch (FileNotFoundException e) {
			System.out.println("File not exists");
		} catch (IOException e) {
			System.out.println("Cant read file");
		}
		return null;
	}

	public Student getStudentFromExcelfile(File file) throws SQLException {
		try {
			XSSFWorkbook workbook = null;
			try {
				workbook = new XSSFWorkbook(file);
			} catch (InvalidFormatException e) {
				System.out.println("Cant open file");
			}
			Sheet sheet = workbook.getSheetAt(0);

			Row header = sheet.getRow(0);
			Row info = sheet.getRow(1);

			Student student = new Student();
			for (int cellNum = 1; cellNum < info.getLastCellNum() - 2; cellNum += 2) {
				if (info.getCell(cellNum).getNumericCellValue() > 0) {
					Subject subject = DBServiceImpl.getSubjectByName(header.getCell(cellNum - 1).getStringCellValue(),
							DBConnection.getConnection());
					student.getSubjectsScore().add(subject);
				}
			}
			workbook.close();
			return student;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Cant read file");
			return null;
		}

	}

	public Student getStudentFromArffFile(File file) throws IOException, SQLException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = "";
		String[] lineWord = null;
		Student st = new Student();
		String data = "";
		List<Subject> subjectList = new ArrayList<Subject>();
		while ((line = br.readLine()) != null) {
		//	lineWord = line.split(" ");
			if (line.startsWith("@attribute")) {
				String name = line.substring(line.indexOf("'")+1, line.lastIndexOf("'"));
				Subject sj = DBServiceImpl.getSubjectByName(name, DBConnection.getConnection());
				if (sj != null) {
					subjectList.add(sj);
					br.readLine();
				}
			}
			if (line.startsWith("@data")) {
				data = br.readLine();
			}
		}
		lineWord = data.split(",");
		for (int i = 0; i < subjectList.size(); i++) {
			subjectList.get(i).setNumGrade(Double.parseDouble(lineWord[((i+1)*2) - 2]));
			subjectList.get(i).setSoLanHoc(Integer.parseInt(lineWord[(i+1)*2 -1]));
		}
		st.setSubjectsScore(subjectList);
		return st;
	}
//	public void exportStudiedCSVFile(Student st, String des) throws SQLException, IOException {
//		Connection conn = DBConnection.getConnection();
//		StringBuilder sb = new StringBuilder();
//		sb.append("Select ");
//		String sjname = null;
//		for (int i = 0; i < st.getSubjectsScore().size(); i++) {
//			sjname = st.getSubjectsScore().get(i).getSubjectName();
//			sb.append("`" + sjname + "`," + "`So Lan Hoc " + sjname + "`");
//			if (i != st.getSubjectsScore().size() - 1) {
//				sb.append(",");
//			} else {
//				sb.append(",`Tot Nghiep` from final");
//			}
//		}
//
//		String sql = VNCharactersUtils.removeAccent(sb.toString());
//		Statement stmt = conn.createStatement();
//		ResultSet rs = stmt.executeQuery(sql);
//		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(des)));
//		sb.delete(0, sb.length()-1);
//		StringBuilder info = new StringBuilder();
//		ResultSetMetaData metaData = (ResultSetMetaData) rs.getMetaData();
//		int columnCount = metaData.getColumnCount();
//		for (int i = 0; i <= columnCount-1; i++) {
//			sb.append(metaData.getCatalogName(i));
//			if(i != columnCount - 1) {
//				sb.append(",");
//			}
//		}
//		bw.write(sb.toString());
//		sb.delete(0, sb.length()-1);
//		while(rs.next()) {
//			
//		}
//		
//		
//	}
	public void exportStudiedTrainFileArff(Student st, String des) throws SQLException, IOException {
		Connection conn = DBConnection.getConnection();
		StringBuilder sb = new StringBuilder();
		sb.append("Select ");
		String sjname = null;
		for (int i = 0; i < st.getSubjectsScore().size(); i++) {
			sjname = st.getSubjectsScore().get(i).getSubjectName();
			sb.append("`" + sjname + "`," + "`So Lan Hoc " + sjname + "`");
			if (i != st.getSubjectsScore().size() - 1) {
				sb.append(",");
			} else {
				sb.append(",`Tot Nghiep` from final");
			}
		}

		String sql = VNCharactersUtils.removeAccent(sb.toString());
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(des))));
		pw.println("@relation train");
		pw.println();
		for (int i = 0; i < st.getSubjectsScore().size(); i++) {
			String sjName = VNCharactersUtils.removeAccent(st.getSubjectsScore().get(i).getSubjectName());
			pw.println("@attribute " + "'" + sjName + "'" + " numeric");
			pw.println("@attribute " + "'So lan hoc " + sjName + "'" + " numeric");
		}
		Map<String, List<Subject>> mapOfList = st.getDSs();
		if(mapOfList.get("CB").size() > 0) {
			pw.println("@attribute "+ "'Diem mon co ban'" + " numeric");
		}
		if(mapOfList.get("CN").size() > 0) {
			pw.println("@attribute "+ "'Diem mon chuyen nganh'" + " numeric");
		}
		if(mapOfList.get("TC1").size() > 0) {
			pw.println("@attribute "+ "'Diem mon tu chon 1'" + " numeric");
		}
		if(mapOfList.get("TC2").size() > 0) {
			pw.println("@attribute "+ "'Diem mon tu chon 2'" + " numeric");
		}
		if(mapOfList.get("TC3").size() > 0) {
			pw.println("@attribute "+ "'Diem mon tu chon 3'" + " numeric");
		}
		pw.println("@attribute "+ "'Tot nghiep'" + " {True,False}");
		pw.println();
		pw.println("@data");
		ResultSetMetaData metaData = (ResultSetMetaData) rs.getMetaData();
		int coulumnCount = metaData.getColumnCount();
		Student tempSt= null;
		sb = new StringBuilder();
		while (rs.next()) {
			tempSt = new Student();
			sb = new StringBuilder();
			for (int i = 1; i < coulumnCount-1; i+=2) {
				Subject sj = DBServiceImpl.getSubjectByName(metaData.getColumnName(i), conn);
				sj.setSoLanHoc(Integer.parseInt(rs.getString(i + 1).trim()));
				sj.setNumGrade(Double.parseDouble(rs.getString(i).trim()));
				tempSt.getSubjectsScore().add(sj);
				
				sb.append(rs.getString(i).trim() + ",");
				sb.append(rs.getString(i + 1).trim()+ ",");
			}
			mapOfList = tempSt.getDSs();
			ConvertServiceImpl convertUtils = new ConvertServiceImpl();
			if(mapOfList.get("CB").size() > 0) {
				sb.append(convertUtils.calculateGrade(mapOfList.get("CB"))+",");
			}
			if(mapOfList.get("CN").size() > 0) {
				sb.append(convertUtils.calculateGrade(mapOfList.get("CB"))+",");
			}
			if(mapOfList.get("TC1").size() > 0) {
				sb.append(convertUtils.calculateGrade(mapOfList.get("CB"))+",");
			}
			if(mapOfList.get("TC2").size() > 0) {
				sb.append(convertUtils.calculateGrade(mapOfList.get("CB"))+",");
			}
			if(mapOfList.get("TC3").size() > 0) {
				sb.append(convertUtils.calculateGrade(mapOfList.get("CB"))+",");
			}
			sb.append(rs.getString("Tot nghiep"));
			pw.println(sb.toString());
		}
		pw.close();
	}

	public void exportStudiedFile(Student st, String des) throws SQLException {
		// DBConnection conn = new DBConnection();
		Connection conn = DBConnection.getConnection();
		StringBuilder sb = new StringBuilder();
		sb.append("Select ");
		String sjname = null;
		for (int i = 0; i < st.getSubjectsScore().size(); i++) {
			sjname = st.getSubjectsScore().get(i).getSubjectName();
			sb.append("`" + sjname + "`," + "`So Lan Hoc " + sjname + "`");
			if (i != st.getSubjectsScore().size() - 1) {
				sb.append(",");
			} else {
				sb.append(",`Tot Nghiep` from final");
			}
		}

		String sql = VNCharactersUtils.removeAccent(sb.toString());
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet();
		Row header = sheet.createRow(0);
		ResultSetMetaData metaData = (ResultSetMetaData) rs.getMetaData();
		int columnNum = metaData.getColumnCount();
		Map<String, List<Subject>> mapofList = new HashMap<String, List<Subject>>();
		for (int i = 1; i <= columnNum - 1; i++) {
			header.createCell(i - 1).setCellValue(metaData.getColumnName(i));
		}
		int numRow = 1;
		Student tempSt = new Student();
		ConvertServiceImpl convertUtils = new ConvertServiceImpl();
		while (rs.next()) {
			Row row = sheet.createRow(numRow);
			for (int i = 1; i <= columnNum - 2; i += 2) {
				row.createCell(i - 1).setCellValue(rs.getString(i));
				row.createCell(i).setCellValue(rs.getString(i + 1));

				Subject sj = DBServiceImpl.getSubjectByName(metaData.getColumnName(i), conn);
				sj.setSoLanHoc(Integer.parseInt(rs.getString(i + 1).trim()));
				sj.setNumGrade(Double.parseDouble(rs.getString(i)));
				tempSt.getSubjectsScore().add(sj);
			}

			mapofList = tempSt.getDSs();
			int count = columnNum - 1;
			if (mapofList.get("CB").size() > 0) {
				header.createCell(count).setCellValue("Diem mon co ban");
				row.createCell(count).setCellValue(convertUtils.calculateGrade(mapofList.get("CB")));
				count++;
			}
			if (mapofList.get("CN").size() > 0) {
				header.createCell(count).setCellValue("Diem mon chuyen nganh");
				row.createCell(count).setCellValue(convertUtils.calculateGrade(mapofList.get("CN")));
				count++;
			}
			if (mapofList.get("TC1").size() > 0) {
				header.createCell(count).setCellValue("Diem mon tu chon 1");
				row.createCell(count).setCellValue(convertUtils.calculateGrade(mapofList.get("TC1")));
				count++;
			}
			if (mapofList.get("TC2").size() > 0) {
				header.createCell(count).setCellValue("Diem mon tu chon 2");
				row.createCell(count).setCellValue(convertUtils.calculateGrade(mapofList.get("TC2")));
				count++;
			}
			if (mapofList.get("TC3").size() > 0) {
				header.createCell(count).setCellValue("Diem mon tu chon 3");
				row.createCell(count).setCellValue(convertUtils.calculateGrade(mapofList.get("TC3")));
				count++;
			}
			header.createCell(count).setCellValue("Tot Nghiep");
			row.createCell(count).setCellValue(rs.getString("Tot nghiep"));
			numRow++;
		}
		try {
			FileOutputStream outputStream = new FileOutputStream(new File(des));
			workbook.write(outputStream);
			outputStream.close();
			outputStream.flush();
			stmt.close();
			conn.close();
			convertToCsv(des);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) throws SQLException, EncryptedDocumentException, IOException {
		FileServiceImpl fileServiceImpl = new FileServiceImpl();
//		try {
//			fileServiceImpl.convertToCsv("E:\\Warehouse2\\monhoc2013.xlsx");
//		} catch (EncryptedDocumentException | IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		Student st = fileServiceImpl.getStudentFromArffFile((new File("file/test.arff")));
//		for (int i = 0; i < st.getSubjectsScore().size(); i++) {
//			System.out.println(st.getSubjectsScore().get(i).getSubjectName());
//		}
		try {
			fileServiceImpl.exportStudiedTrainFileArff(st, "file/train.arff");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// fileServiceImpl.convertToCsv("file/studied.xlsx");
	}

}
