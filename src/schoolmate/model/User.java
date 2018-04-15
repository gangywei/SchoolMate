package schoolmate.model;

public class User {
	private int u_id;
	public String u_name;
	public String u_count;
	public String u_pwd;
	public int u_problem;
	public String u_answer;
	public int u_role;
	public String faculty;
	public User(String count,String pwd){
		u_count = count;
		u_pwd = pwd;
	}
	
	//用户的权限状态	1=普通用户 2=管理员 3=管理者
	public String getRole(){
		if(u_role==3){
			return "系统管理者";
		}if(u_role==2){
			return "学员管理者";
		}else{
			return "普通用户";
		}
	}
}
