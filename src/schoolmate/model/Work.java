package schoolmate.model;

public class Work {
	public int wl_id;
	public int s_id;
	public String s_workspace;
	public String s_work;
	public String s_worktitle;
	public String province;
	public String city;
	public String nation;
	public Work(String nation,String province,String city,String work,String worktitle,String workspace){
		s_work = work;
		this.city = city;
		s_workspace = workspace;
		s_worktitle = worktitle;
		this.province = province;
		this.nation = nation;
	}
}
