package convertProject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Untils.ConvertServiceImpl;

public class Student {
	private String studentID;
	private String studentName;
	private int sohocky;
	private double diemtb;
	private double soTinchi;
	private boolean isGraduated;
	private List<Subject> subjectsScore; 
	ConvertServiceImpl convertUtils = new ConvertServiceImpl();
	
	public Student(String studentID, String studentName) {
		this.studentID = studentID;
		this.studentName = studentName;
		this.subjectsScore = new ArrayList<Subject>();
		this.isGraduated = false;
	}
	public Student() {
		subjectsScore = new ArrayList<Subject>();
	}
	public void addSubject(List<Subject> listsubject) {
		this.subjectsScore.addAll(listsubject);
	}
	
	public String getStudentID() {
		return studentID;
	}
	public void setStudentID(String studentID) {
		this.studentID = studentID;
	}
	public String getStudentName() {
		return studentName;
	}
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}
	public boolean isGraduated() {
		return isGraduated;
	}
	public void setGraduated(boolean isGraduated) {
		this.isGraduated = isGraduated;
	}
	public List<Subject> getSubjectsScore() {
		return subjectsScore;
	}
	public void setSubjectsScore(List<Subject> subjectsScore) {
		this.subjectsScore = subjectsScore;
	}
	public double getSoTinchi() {
		return soTinchi;
	}
	public void setSoTinchi(double soTinchi) {
		this.soTinchi = soTinchi;
	}
	public double getDiemtb() {
		return diemtb;
	}
	public void setDiemtb(double diemtb) {
		this.diemtb = diemtb;
	}
	public int getSohocky() {
		return sohocky;
	}
	public void setSohocky(int sohocky) {
		this.sohocky = sohocky;
	}
	public String getNameStudent() {
		return studentName;
	}
	public void setNameStudent(String nameStudent) {
		this.studentName = nameStudent;
	}
	public List<Subject> getMark() {
		return subjectsScore;
	}
	public void setMark(List<Subject> subjectsScore) {
		this.subjectsScore = subjectsScore;
	}
	public Boolean containSubject(String name) {
		for (Subject subject : subjectsScore) {
			if(subject.getSubjectName().equals(name)) {
				return true;
			}
		}
		return false;
	}
	public void adjustASubject(Subject newsj) {
		// TODO Auto-generated method stub
		for (int i = 0; i < subjectsScore.size(); i++) {
			Subject sj = subjectsScore.get(i);
			if(sj.getSubjectName().equals(newsj.getSubjectName())){
				subjectsScore.remove(i);
				newsj.setSoLanHoc(sj.getSoLanHoc()+1);
				double newGrade = (convertUtils.get4thScore(newsj.getNumGrade()) > sj.getNumGrade()) ? convertUtils.get4thScore(newsj.getNumGrade()) : sj.getNumGrade();
				newsj.setNumGrade(newGrade);
				subjectsScore.add(i, newsj);
				return;
			}
		}
		newsj.setSoLanHoc(1);
		subjectsScore.add(newsj);
	}
	public Map<String, List<Subject>> getDSs() {
		List<Subject> dscoban = new ArrayList<Subject>();
		List<Subject> dschuyennganh = new ArrayList<Subject>();
		List<Subject> dstuchon1 = new ArrayList<Subject>();
		List<Subject> dstuchon2 = new ArrayList<Subject>();
		List<Subject> dstuchon3 = new ArrayList<Subject>();
		for (int i = 0; i < subjectsScore.size(); i++) {
			switch (subjectsScore.get(i).getGroupID()) {
			case 1:
				dscoban.add(subjectsScore.get(i));
				break;
			case 2:
				dschuyennganh.add(subjectsScore.get(i));
				break;
			case 3:
				dstuchon1.add(subjectsScore.get(i));
				break;
			case 4:
				dstuchon2.add(subjectsScore.get(i));
				break;
			case 5:
				dstuchon3.add(subjectsScore.get(i));
				break;
			default:
				break;
			}
			
		}
		Map<String, List<Subject>> mapOfList = new HashMap<String, List<Subject>>();
		mapOfList.put("CB", dscoban);
		mapOfList.put("CN", dschuyennganh);
		mapOfList.put("TC1", dstuchon1);
		mapOfList.put("TC2", dstuchon2);
		mapOfList.put("TC3", dstuchon3);
		return mapOfList;
	}
	
}
