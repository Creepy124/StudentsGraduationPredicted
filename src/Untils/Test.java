package Untils;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.poi.EncryptedDocumentException;

public class Test {
	public static void main(String[] args) throws SQLException, EncryptedDocumentException, IOException {
		DBServiceImpl db = new DBServiceImpl("dudoantotnghiep", "root", "1234");
		FileServiceImpl fileService = new FileServiceImpl();
		String subject = "MSSV,Ten SV,Toan cao cap A1,So lan hoc Toan cao cap A1,Vat Ly 2,So lan hoc Vat Ly 2,Toan cao cap A2,So lan hoc Toan cao cap A2,Cac ng.ly CB cua CN MacLenin,So lan hoc Cac ng.ly CB cua CN MacLenin,Xac suat thong ke,So lan hoc Xac suat thong ke,Phap luat dai cuong,So lan hoc Phap luat dai cuong,Duong loi CM cua Dang CSVN,So lan hoc Duong loi CM cua Dang CSVN,Tu tuong Ho Chi Minh,So lan hoc Tu tuong Ho Chi Minh,Toan cao cap A3,So lan hoc Toan cao cap A3,Lap trinh co ban,So lan hoc Lap trinh co ban,Nhap mon tin hoc,So lan hoc Nhap mon tin hoc,Cau truc may tinh,So lan hoc Cau truc may tinh,Lap trinh nang cao,So lan hoc Lap trinh nang cao,Nhap mon CN phan mem,So lan hoc Nhap mon CN phan mem,Nhap mon he dieu hanh,So lan hoc Nhap mon he dieu hanh,Cau truc du lieu,So lan hoc Cau truc du lieu,Mang may tinh co ban,So lan hoc Mang may tinh co ban,Thiet ke huong doi tuong,So lan hoc Thiet ke huong doi tuong,Nhap mon co so du lieu,So lan hoc Nhap mon co so du lieu,He dieu hanh nang cao,So lan hoc He dieu hanh nang cao,Ly thuyet do thi,So lan hoc Ly thuyet do thi,Lap trinh mang,So lan hoc Lap trinh mang,Phan tich va thiet ke HTTT,So lan hoc Phan tich va thiet ke HTTT,Giao tiep nguoi _may,So lan hoc Giao tiep nguoi _may,Lap trinh Web,So lan hoc Lap trinh Web,Nhap mon tri tue nhan tao,So lan hoc Nhap mon tri tue nhan tao,He quan tri co so du lieu,So lan hoc He quan tri co so du lieu,Mang may tinh nang cao,So lan hoc Mang may tinh nang cao,An ninh mang,So lan hoc An ninh mang,Quan tri mang,So lan hoc Quan tri mang,Thuong mai dien tu,So lan hoc Thuong mai dien tu,Quan ly du an phan mem,So lan hoc Quan ly du an phan mem,DBCL va kiem thu phan mem,So lan hoc DBCL va kiem thu phan mem,An toan va bao mat he thong TT,So lan hoc An toan va bao mat he thong TT,Chuyen de cong nghe phan mem,So lan hoc Chuyen de cong nghe phan mem,Lap trinh mang nang vao,So lan hoc Lap trinh mang nang vao,Marketing can ban,So lan hoc Marketing can ban,Do hoa may tinh,So lan hoc Do hoa may tinh,Data Mining,So lan hoc Data Mining,Data Warehouse,So lan hoc Data Warehouse,Lap trinh .NET,So lan hoc Lap trinh .NET,He thong thong tin quan ly,So lan hoc He thong thong tin quan ly,Chuyen de DB2,So lan hoc Chuyen de DB2,Chuyen de Oracle,So lan hoc Chuyen de Oracle,Lap trinh tren TB di dong,So lan hoc Lap trinh tren TB di dong,Lap trinh C++ trong LINUX,So lan hoc Lap trinh C++ trong LINUX,He thong thong tin dia ly UD,So lan hoc He thong thong tin dia ly UD,Giai phap mang cho DN,So lan hoc Giai phap mang cho DN,Giai phap phan mem chinh phu DT,So lan hoc Giai phap phan mem chinh phu DT,Chuyen de he thong thong tin,So lan hoc Chuyen de he thong thong tin,Chuyen de mang may tinh & TT,So lan hoc Chuyen de mang may tinh & TT,Chuyen de ma nguon mo,So lan hoc Chuyen de ma nguon mo,DACN Cong nghe phan mem,So lan hoc DACN Cong nghe phan mem,DACN He thong thong tin,So lan hoc DACN He thong thong tin,Chuyen de WEB,So lan hoc Chuyen de WEB,Tieu luan TN,So lan hoc Tieu luan TN,Khoa luan tot nghiep,So lan hoc Khoa luan tot nghiep,Chuyen de Java,So lan hoc Chuyen de Java,DACN Mang may tinh va TT,So lan hoc DACN Mang may tinh va TT";
		String coBan = "202108,202206,202109,200106,202121,202622,200104,200107,202110";

		String chuyenNganh = "214321,214201,214231,214331,214370,214242,214441,214241,214352,214442,214251,214351,214252,214461,214361,214462,214463";

		String tuChon1 = "214451,214282,214275,214271,214483,214383,214379,214464,214376";

		String tuchon2 = "214273,208453,214353,214485,214477,214372,214471,214489,214488,214274,214284,214465,214285,214289,214481,214283";

		String tuchon3 = "214382,214385,214487,214374,214983,214982,214286,214287";

		String phanLoai = "Diem mon Co ban,Diem mon Chuyen nganh,Diem mon Tu chon 1,Diem mon Tu chon 2,Diem mon Tu chon 3,"
				+ "Tot nghiep";
		List<String> listPhanLoai = Arrays.asList(phanLoai.split(","));

		String[] subjects = subject.split(",");
		String[] subjectsID = coBan.concat(",").concat(chuyenNganh).concat(",").concat(tuChon1).concat(",")
				.concat(tuchon2).concat(",").concat(tuchon3).split(",");

		List<String> tmpHeader = new ArrayList<String>();
		for (int k = 0; k < subjects.length; k++) {
			tmpHeader.add(subjects[k]);
//			tmpHeader.add("So lan hoc " + subjects[k]);
		}

//		List<String> header = Stream.of(tmpHeader, listPhanLoai).flatMap(Collection::stream)
//			.collect(Collectors.toList());
////			header.add(0, "Ten sinh vien");
//			db.createTable("final", header, "VARCHAR(100)");

//	fileService.convertToCsv("E:\\HDD\\wordspace\\DMProjectWekaAPI\\Export2.xlsx");
//	db.loadFile("E:\\\\HDD\\\\wordspace\\\\DMProjectWekaAPI\\\\Export2.csv", "final", ",");
	db.loadFile("E:\\\\HDD\\\\wordspace\\\\ProjectWeka2\\\\src\\\\Weka\\\\Export.csv", "final", ",");
//		Connection connection = DBConnection.getConnection("dudoantotnghiep", "root", "1234");
//		List<String> list = db.getSubjectIDByType("CN");
//		for (int i = 0; i < list.size(); i++) {
//			System.out.println(list.get(i));

		}
//	for (int i = 0; i < subjects.length; i++) {
//	
//	String sql = "INSERT INTO subjects (SubjectID, SubjectName)\r\n" + 
//			"VALUES ('" + subjectsID[i] + "', '" + subjects[i] +"');";
//		System.out.println(sql);
//		PreparedStatement ps = connection.prepareStatement(sql);
//		System.out.println(ps.executeUpdate());
//	}
//	List<String> subjectsName = new LinkedList<String>();
//	String sql1 = "Select * from subjects";
//	PreparedStatement ps = connection.prepareStatement(sql1);
//	ResultSet rs = ps.executeQuery();	
//	while(rs.next()) {
//		subjectsName.add(rs.getString(2));
//	}
//		for (int i = 0; i < subjectsName.size(); i++) {
//			System.out.println(subjectsName.get(i));
//		}
//	}
//	DBServiceImpl dbs = new DBServiceImpl("predict", "root", "StrongPass123");
//	File f = new File("D:\\JavaPrograming\\ProjectWeka\\export.csv");
//	db.loadFile("D:/JavaPrograming/ProjectWeka/export2.csv", "final", "|");

//	}
}
