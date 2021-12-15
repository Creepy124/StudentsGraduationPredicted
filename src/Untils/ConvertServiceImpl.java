package Untils;

import java.util.List;

import convertProject.Subject;

public class ConvertServiceImpl implements ConvertService{

	 public ConvertServiceImpl() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public double calculateGrade(List<Subject> listSubject) {
		double result = 0;
		int tinchi = 0;
		for (Subject sub : listSubject) {
			result += sub.getNumGrade() * sub.getTinchi();
			tinchi += sub.getTinchi();
		}
		return (double) this.getLetterScoreFrom4thScore(Math.round((result / tinchi) * 100) / 100);
	}

	@Override
	public double get4thScore(double score) {
		if (score >= 9)
			return 4;
		if (score >= 8)
			return 3.5;
		if (score >= 7)
			return 3;
		if (score >= 6)
			return 2.5;
		if (score >= 5)
			return 2;
		if (score > 4)
			return 1.5;
		if (score == 4)
			return 1;
		else
			return 0;
	}

	@Override
	public double getLetterScoreFrom4thScore(double score) {
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

}
