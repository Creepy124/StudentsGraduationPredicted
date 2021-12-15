package Untils;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class ImportDbImpl implements ImportDb{

	@Override
	public void importData(String path, String table) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void importSubject(String subjectID, String subjectName, int group, String studentTable) {
		Connection conn = DBConnection.getConnection("predict", "root", "StrongPass123");
		String addIntoSubjectTable = "Insert into" + studentTable + "(subjectID, subjectName, group) values("+ subjectID + ","+ subjectName +","+ group+")";
		String addColumnsIntoData = "Alter table Student add "+subjectName.trim() + " varchar(100), " + "SoLanHoc "+ subjectName.trim() + " int(11)";
		Statement stmt = null;
		 try {
			stmt = conn.createStatement();
		} catch (SQLException e) {
			System.out.println("Cant create database statement");
		}
		 try {
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			System.out.println("Cant turn off auto commit");
		}
		 try {
			 stmt.execute(addIntoSubjectTable);
			 stmt.execute(addColumnsIntoData);
			 stmt.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				System.out.println("cant rollback");
			}
		}
		 
		 try {
			conn.close();
		} catch (SQLException e) {
			System.out.println("Cant close connection");
		}
		
	}
	
	
}
