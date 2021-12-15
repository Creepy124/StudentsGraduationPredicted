package Untils;

public interface ImportDb {
	public void importData(String path, String table);
	
	public void importSubject(String subjectID, String subjectName, int group, String studentTable);
	
}
