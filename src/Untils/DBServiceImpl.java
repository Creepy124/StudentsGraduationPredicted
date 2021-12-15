package Untils;

import java.sql.Statement;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.mysql.jdbc.CallableStatement;

import convertProject.Subject;


/*
 * This class contain all methods that relative to database such as: create table, truncate, transformed , load file
 */
public class DBServiceImpl implements DBService {
	String targetDBName;
	String password;
	String userName;

	public DBServiceImpl(String targetDBName, String userName, String password) {
		this.targetDBName = targetDBName;
		this.password = password;
		this.userName = userName;
	}

	@Override
	public boolean existTable(String tableName) throws SQLException {
		DatabaseMetaData dbm = DBConnection.getConnection(targetDBName, userName, password).getMetaData();
		ResultSet tables = dbm.getTables(null, null, tableName, null);
		if (tables.next()) {
			return true;
		}
		return false;
	}

	@Override
	public int createTable(String tableName, List<String> header, String type) throws SQLException {
		String sql = "CREATE TABLE " + tableName + " (";
		for (int i = 0; i < header.size(); i++) {
			sql += "`" + header.get(i) + "` "+type+" ,";
		}
		sql = sql.substring(0, sql.length() - 2) + ");";
		System.out.println(sql);
		Connection connection = DBConnection.getConnection(targetDBName, userName, password);
		PreparedStatement ps = connection.prepareStatement(sql.trim());
		return ps.executeUpdate();
	}

	@Override
	public int truncateTable(String tableName) throws SQLException {
		Connection connection = DBConnection.getConnection(targetDBName, userName, password);
		PreparedStatement ps = connection.prepareStatement("TRUNCATE TABLE " + this.targetDBName + "." + tableName);
//		System.out.println("TRUNCATE TABLE " + tableName);
		return ps.executeUpdate();
	}

	@Override
	public int loadFile(String sourceFile, String tableName, String dilimiter) throws SQLException {
		Connection connection = DBConnection.getConnection(targetDBName, userName, password);
		PreparedStatement ps = connection.prepareStatement("LOAD DATA INFILE '" + sourceFile + "' INTO TABLE "
				+ targetDBName + "." + tableName + "\r\n" + "FIELDS TERMINATED BY '" + dilimiter + "' \r\n"
				+ "ENCLOSED BY '\"' \r\n" + "LINES TERMINATED BY '\\n'" + "IGNORE 1 lines");
		System.out.println("LOAD DATA INFILE '" + sourceFile + "' INTO TABLE " + targetDBName + "." + tableName + "\r\n"
				+ "FIELDS TERMINATED BY '" + dilimiter + "' \r\n" + "ENCLOSED BY '\"' \r\n"
				+ "LINES TERMINATED BY '\\n'" + "IGNORE 1 lines");
		return ps.executeUpdate();
	}

	@Override
	public int tranformNullValue(String tableName, String col, String defaut) throws SQLException {
		Connection con = DBConnection.getConnection(targetDBName, userName, password);
		String sql = "Update " + tableName + " set " + col + " = '" + defaut + "' where " + col + " is Null";
		PreparedStatement pre = con.prepareStatement(sql);
//		System.out.println(sql);
		return pre.executeUpdate();
	}

	@Override
	public int deleteNullID(String tableName, String col) throws SQLException {
		Connection con = DBConnection.getConnection(targetDBName, userName, password);
		String sql = "Delete from " + tableName + " where " + col + " is Null";
//		System.out.println(sql);
		PreparedStatement pre = con.prepareStatement(sql);
		return pre.executeUpdate();
	}

	@Override
	public void callProcedure(String procName) throws SQLException {
		Connection con = DBConnection.getConnection(targetDBName, userName, password);
		String query = "{CALL " + procName + "()}";
		CallableStatement stmt = (CallableStatement) con.prepareCall(query);
		stmt.execute();
	}

	@Override
	public int getFlag(String state) throws SQLException {
		Connection con = DBConnection.getConnection(targetDBName, userName, password);
		String sql = "Select config_id from configuration where flag = '" + state + "'";
		PreparedStatement pre = con.prepareStatement(sql);
		ResultSet rs = pre.executeQuery();
		if (rs.next()) {
			return rs.getInt("config_id");
		}
		return 0;
	}

	@Override
	public void updateFlag(int config_id, String state) throws SQLException {
		Connection con = DBConnection.getConnection(targetDBName, userName, password);
		String sql = "Update configuration set flag = '" + state + "' where config_id = " + config_id;
//		System.out.println(sql);
		PreparedStatement pre;
		pre = con.prepareStatement(sql);
		pre.executeUpdate();
	}

	@Override
	public int nextConfig(int configID) throws SQLException {

		Connection con = DBConnection.getConnection(targetDBName, userName, password);
		String sql = "Select flag from configuration where config_id = " + configID;

		PreparedStatement pre = con.prepareStatement(sql);
		ResultSet rs = pre.executeQuery();
		if (rs.next()) {
			String tmp = rs.getString("flag");
			if (tmp.isEmpty() || tmp == null) {
				return configID;
			} else {
				configID++;
				return nextConfig(configID);
			}
		}
		return 0;
	}

	public List<String> getColumnName(ResultSetMetaData rsMetaData) throws SQLException{
		List<String> result = new LinkedList<>();
		System.out.println(rsMetaData.getColumnCount());
		for (int i = 1; i <= rsMetaData.getColumnCount(); i++) {
			System.out.println(i);
			result .add(rsMetaData.getColumnLabel((i)));
		}
		return result;
	}
	
	@Override
	public void exportExcel(String path, String table, int ignoreColumn) throws SQLException, FileNotFoundException, IOException {
		
		String sql ="Select * from " +this.targetDBName+"."+table;
		Connection conn = DBConnection.getConnection(targetDBName, userName, password);
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		
		ResultSetMetaData rsMetaData = rs.getMetaData();
		List<String> header = getColumnName(rsMetaData);
		
		File exportFile = new File(path);
		if (exportFile.exists()) {
			exportFile.delete();
		}
		exportFile.createNewFile();
		Workbook excel = new XSSFWorkbook();
		Sheet sheet = excel.createSheet();
		Row firstRow = sheet.createRow(0);
		
		for (int i = ignoreColumn; i < header.size(); i++) {
			Cell cell = firstRow.createCell(i-ignoreColumn);
			cell.setCellValue(header.get(i));
		}
		
		int rowNum = 1;
		int colNum=0;
		while(rs.next()) {
			Row row = sheet.createRow(rowNum++);
			for (int i = ignoreColumn; i < header.size(); i++) {
				row.createCell(colNum++).setCellValue(rs.getString(header.get(i)));
			}
			colNum = 0;
		}
		FileOutputStream fileOut = new FileOutputStream(exportFile);
		excel.write(fileOut);
		fileOut.close();
		excel.close();
	}
	
	@Override
	public List<Subject> getSubjectList() throws SQLException{
		List<Subject> listSJ = new ArrayList<Subject>();
		Connection conn = DBConnection.getConnection(targetDBName, userName, password);
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("select * from subjects");
		while(rs.next()) {
			Subject subject = new Subject(rs.getString("subjectName"));
			subject.setSubjectID(rs.getString("SubjectID"));
			subject.setGroupID(rs.getInt("groupID"));
			subject.setTinchi(rs.getInt("TC"));
			listSJ.add(subject);
		}
		return listSJ;
	}
	
	public static Subject getSubjectByName(String subjectName, Connection conn2) throws SQLException {
		String sjname = VNCharactersUtils.removeAccent(subjectName);
		Subject subject = new Subject(sjname);
		String sql = "select * from subjects where subjectName = '"+ sjname +"'";
		Statement stmt = conn2.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		if(rs.next()) {
			subject.setGroupID(rs.getInt("groupID"));
			subject.setSubjectID(rs.getString("SubjectID"));
			subject.setTinchi(rs.getInt("TC"));
		}
		else {
			return null;
		}
		stmt.close();
		return subject;
	}

	public List<String> getSubjectIDByType(String type){
		List<String> result = new LinkedList<String>();
		String sql = "Select subjectID from "+this.targetDBName+".subjects where groupID=";
		int groupID = 0;
		switch(type) {
			case "CB":
				groupID=1;
				break;
			case "CN":
				groupID=2;
				break;
			case "TC1":
				groupID=3;
				break;
			case "TC2":
				groupID=4;
				break;
			case "TC3":
				groupID=5;
				break;
		}
		sql +=groupID;
		Connection con = DBConnection.getConnection(this.targetDBName, this.userName, this.password);
		
		PreparedStatement ps;
		try {
			ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery(sql);
			while(rs.next()) {
				result.add(rs.getString(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
}
