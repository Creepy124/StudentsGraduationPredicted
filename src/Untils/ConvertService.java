package Untils;

import java.util.List;

import convertProject.Subject;

public interface ConvertService {
	public double calculateGrade(List<Subject> listSubject) ;
	public double get4thScore(double score);
	public double getLetterScoreFrom4thScore(double score);
}
