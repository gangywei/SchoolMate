package schoolmate.model;

public class Worklog {
	public int wl_id;
	public String s_no;
	public String s_workspace;
	public String s_work;
	public String s_worktitle;
	public String province;
	public String city;
	public String nation;
	public Worklog(String workspace,String work,String worktitle,String province,String city,String nation){
		s_work = work;
		this.city = city;
		s_workspace = workspace;
		s_worktitle = worktitle;
		this.province = province;
		this.nation = nation;
	}
}
