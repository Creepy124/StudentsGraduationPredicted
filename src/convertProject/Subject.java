package convertProject;

public class Subject {
	private String subjectID;
	private String subjectName;
	private String letterGrade;
	private int tinchi;
	private double numGrade;
	private boolean isFail;
	private int soLanHoc;
	private int groupID;
	
	
	public int getGroupID() {
		return groupID;
	}

	public void setGroupID(int groupID) {
		this.groupID = groupID;
	}

	public Subject(String subjectName) {
		this.subjectName = subjectName;
		this.tinchi = 0;
		this.soLanHoc = 0;
		this.isFail = true;
		this.numGrade = 0;
		this.letterGrade = "F";
	}

	public int getSoLanHoc() {
		return soLanHoc;
	}
	public void setSoLanHoc(int soLanHoc) {
		this.soLanHoc = soLanHoc;
	}
	public double getNumGrade() {
		return numGrade;
	}
	public void setNumGrade(double numGrade) {
		this.numGrade = numGrade;
	}

	
	public String getSubjectID() {
		return subjectID;
	}

	public void setSubjectID(String subjectID) {
		this.subjectID = subjectID;
	}

	public void setTinchi(int tinchi) {
		this.tinchi = tinchi;
	}

	public int getTinchi() {
		return tinchi;
	}
	public void setTinchi(double tinchi) {
		this.tinchi = (int) tinchi ;
	}
	public String getLetterGrade() {
		return letterGrade;
	}
	public void setLetterGrade(String letterGrade) {
		this.letterGrade = letterGrade;
	}

	@Override
	public String toString() {
		return "Subject [subjectName=" + subjectName + ", letterGrade=" + letterGrade + ", tinchi=" + tinchi
				+ ", numGrade=" + numGrade + ", isFail=" + isFail + ", soLanHoc=" + soLanHoc + "]";
	}

	public String getSubjectName() {
		return subjectName;
	}
	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}
	
	public boolean isFail() {
		return isFail;
	}
	public void setFail(boolean isFail) {
		this.isFail = isFail;
	}
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if(obj instanceof Subject) {
			Subject sj = (Subject) obj;
			return sj.getSubjectName().equals(this.subjectName);
		}
		return false;
	}
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}
	
}
