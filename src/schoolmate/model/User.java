package schoolmate.model;

public class User {
	private int u_id;
	public String u_count;
	public String u_pwd;
	public int u_problem;
	public String u_answer;
	public int u_role;
	public int f_id;
	public User(String count,String pwd){
		u_count = count;
		u_pwd = pwd;
	}
	public String getRole(){
		if(u_role==0){
			return "管理员";
		}else{
			return "普通用户";
		}
	}
}
