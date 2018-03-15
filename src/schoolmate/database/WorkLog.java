package schoolmate.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import schoolmate.control.Helper;
import schoolmate.model.DBConnect;
import schoolmate.model.Student;
import schoolmate.model.Work;

public class WorkLog {
	private static Connection connect=DBConnect.getConnection();
	private static Statement stmt = null;
	private static ResultSet res;
	
	public static void insertWork(Student stu,Statement stmt) throws SQLException{
		String sql = "INSERT INTO worklog (s_id,s_workspace,s_work,s_worktitle,province,city,nation,update_time) VALUES "
				+ "("+stu.s_id+",'"+stu.s_workspace+"','"+stu.s_work+"','"+stu.s_worktitle+"','"+stu.s_province+"','"+stu.s_city+"','"+stu.s_nation+"',"+Helper.dataTime(null)+");";
		stmt.executeUpdate(sql);
	}
	/* old 原数据，now修改后的数据，type 0=市区，1=省市，2=国家
	 * inter:根据教育流程标签的old（学院、专业）数据更新为now数据。在一个事务下更新。
	 * time：2018/03/15
	 */
	public static void updateWork(String old[],String now[],Statement stmt,int type) throws SQLException{
		String sql = "";
		String sql2 = "";
		if(type==0){
			sql = "update worklog set nation='"+now[0]+"',province='"+now[1]+"',city='"+now[2]+"' where nation='"+old[0]+
			"' and province='"+old[1]+"' and city='"+old[2]+"';";
			sql2 = "update student set s_nation='"+now[0]+"',s_faculty='"+now[1]+"',s_major='"+now[2]+"' where nation='"+old[0]+
					"' and s_faculty='"+old[1]+"' and s_major='"+old[2]+"';";
		}else if(type==1){
			sql = "update worklog set nation='"+now[0]+"',province='"+now[1]+"' where nation='"+old[0]+"' and province='"+old[1]+"';";
			sql2 = "update student set s_nation='"+now[0]+"',s_province='"+now[1]+"' where s_nation='"+old[0]+"' and s_province='"+old[1]+"';";
		}else{
			sql = "update worklog set nation='"+now[0]+"' where nation='"+old[0]+"';";
			sql2 = "update student set s_nation='"+now[0]+"' where s_nation='"+old[0]+"';";
		}
		stmt.executeUpdate(sql);
		stmt.executeUpdate(sql2);
	}
	
	public static void insertWork(Work stu,Statement stmt) throws SQLException{
		boolean change = false;
		if(stmt==null){
			change = true;
			stmt = connect.createStatement();
		}
		String sql = "INSERT INTO worklog (s_id,s_workspace,s_work,s_worktitle,province,city,nation,update_time) VALUES "
				+ "("+stu.s_id+",'"+stu.s_workspace+"','"+stu.s_work+"','"+stu.s_worktitle+"','"+stu.province+"','"+stu.city+"','"+stu.nation+"',"+Helper.dataTime(null)+");";
		stmt.executeUpdate(sql);
		StudentLog.updateWork(stu, stmt);
		if(change){
			connect.commit();
			stmt.close();
		}
	}
	
	//type判断是否更新学生信息表。0 = 不更新 1 = 更新
	public static void updateWork(Work stu,Statement stmt,int type) throws SQLException{
		boolean change = false;
		if(stmt==null){
			change = true;
			stmt = connect.createStatement();
		}
		String sql = "update worklog set s_workspace='"+stu.s_workspace+"',s_work='"+stu.s_work+"',s_worktitle='"+stu.s_worktitle+"',province='"+stu.province+"',city='"
		+stu.city+"',nation='"+stu.nation+"',update_time="+Helper.dataTime(null)+" where wl_id="+stu.wl_id;
		stmt.executeUpdate(sql);
		if(type==1)
			StudentLog.updateWork(stu, stmt);
		if(change){
			connect.commit();
			stmt.close();
		}
	}
	
	public static String[] searchWork(int id){
		String[] work = null;
		String sql;
		try {
			stmt = connect.createStatement();
			sql = "SELECT s_workspace,s_work,s_worktitle,nation,province,city FROM worklog where wl_id="+id+";";
			res = stmt.executeQuery(sql);
			while (res.next()) {
				work = new String[6];
				work[0] = res.getString("nation");
				work[1] = res.getString("province");
				work[2] = res.getString("city");
				work[3] = res.getString("s_work");
				work[4] = res.getString("s_worktitle");
				work[5] = res.getString("s_workspace");
			}
			res.close();
			stmt.close(); 
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return work;
	}
	//type = 0不更新学生表，type = 1更新学生记录
	public static void deleteWork(Work work,Statement stmt,int type) throws SQLException{
		boolean change = false;
		if(stmt==null){
			change = true;
			stmt = connect.createStatement();
		}
		String sql = "delete from worklog where wl_id="+work.wl_id+";";
		stmt.executeUpdate(sql);
		if(type==1)
			StudentLog.updateWork(work, stmt);
		if(change){
			connect.commit();
			stmt.close();
		}
	}
	//删除学生时删除学生的所有工作记录
	public static void deleteWork(int sId,Statement stmt) throws SQLException{
		String sql = "delete from worklog where s_id="+sId+";";
		stmt.executeUpdate(sql);
	}
	
	public static void deleteManyWk(String count,Statement stmt) throws SQLException{
		String sql = "delete from worklog where s_id in ("+count+");";
		stmt.executeUpdate(sql);
	}
	
	//返回table数组
	public static Vector<Object[]> dao(String str) throws Exception{
		PreparedStatement stmt = connect.prepareStatement(str);
		ResultSet rs = stmt.executeQuery();
		int count = 0;
		while (rs.next()){
			count++;
		}
		rs = stmt.executeQuery();
		ResultSetMetaData rsmd=rs.getMetaData();//用于获取关于 ResultSet 对象中列的类型和属性信息的对象
		int colNum=rsmd.getColumnCount();	//得到列数
		Vector<Object[]> info = new Vector<Object[]>();
		int i=0;
		while (rs.next()){
			info.add((new Object[colNum]));
			for (int j=1;j<=colNum;j++){
				info.elementAt(i)[j-1]=(Object) rs.getObject(j);
			}
			i++;
		}
		return info;
	}
	
	public static Vector<Object[]> getUpdate(long time) throws SQLException{
		String sql = "select * from worklog where update_time>="+time;
		stmt = connect.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		ResultSetMetaData rsmd=rs.getMetaData();//用于获取关于 ResultSet 对象中列的类型和属性信息的对象
		int colNum=rsmd.getColumnCount()-1;	//得到列数
		Vector<Object[]> info = new Vector<Object[]>();
		int i=0;
		while (rs.next()){
			info.add((new Object[colNum]));
			info.elementAt(i)[0] = 3;	//表示工作信息
			for (int j=1;j<colNum;j++){
				info.elementAt(i)[j]=(Object) rs.getObject(j+1);
			}
			i++;
		}
		return info;
	}
}
