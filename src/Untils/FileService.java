package Untils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.apache.poi.EncryptedDocumentException;

import convertProject.Subject;

public interface FileService {

	//move file has "WH" action (file was loaded)
	public boolean moveFile(String target_dir, File file) throws IOException ;
	
	//Write error after send mail (local/error.txt)
	public void writeLinesToFile(String fPath, String lines) throws IOException;
	
	//cant load xlsx, convert to csv to use load data in file
	public String convertToCsv(String path) throws EncryptedDocumentException, IOException;
	
}
