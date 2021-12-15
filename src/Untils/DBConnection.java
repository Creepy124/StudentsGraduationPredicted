package Untils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
	private static String dbName = "dudoantotnghiep";
	private static String userName = "root";
	private static String passWord = "1234";
	
	public static Connection getConnection(String dbName, String userName, String password) {
		Connection con = null;
		String url = "jdbc:mysql://localhost:3306/" + dbName
				+ "?useSSL=false&characterEncoding=utf8&allowLoadLocalInfile=true";
		try {
			if (con == null || con.isClosed()) {
				con = DriverManager.getConnection(url, userName, password);
			}
		} catch (SQLException e) {
			return null;
		}
		return con;
	}
	public static Connection getConnection() {
		return getConnection(dbName, userName, passWord);
	}

	public static void main(String[] args) {
		DBConnection d = new DBConnection();
		d.getConnection("control", "root", "1234");
	}
}
