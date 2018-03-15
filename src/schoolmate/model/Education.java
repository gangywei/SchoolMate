package schoolmate.model;

public class Education {
	public int s_id;
	public int e_id;
	public String s_no;
	public String s_faculty;
	public String s_major;
	public String s_class;
	public String s_enter;
	public String s_education;
	public String s_graduate;
	public Education(int s_id,String sNo,String education,String faculty,String major,String classes,String enter,String graduate){
		this.s_no = sNo;
		this.s_id = s_id;
		this.s_faculty = faculty;
		this.s_major = major;
		this.s_class = classes;
		this.s_enter = enter;
		this.s_education = education;
		this.s_graduate = graduate;
	}
}
