package schoolmate.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import schoolmate.control.Helper;
import schoolmate.model.DBConnect;
import schoolmate.model.Work;

public class WorkLog {
	private static Connection connect=DBConnect.getConnection();
	private static Statement stmt = null;
	private static ResultSet res;
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
			sql2 = "update student set s_nation='"+now[0]+"',s_province='"+now[1]+"',s_city='"+now[2]+"' where s_nation='"+old[0]+
					"' and s_province='"+old[1]+"' and s_city='"+old[2]+"';";
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
	//检查是否需要更新数据库字段
	public static void checkField(String nation,String province,String city,Statement stmt) throws SQLException{
		if(!nation.equals("")){
			boolean nationRes = AddressLog.searchNation(nation);
			if(!nationRes){
				AddressLog.insertNation(nation,stmt);
			}
		}
		if(!province.equals("")){
			boolean provinceRes = AddressLog.searchProvince(province,nation);
			if(!provinceRes){
				AddressLog.insertProvince(province,nation,stmt);
			}
		}
		if(!city.equals("")){
			boolean cityRes = AddressLog.searchCity(city,province,nation);
			if(!cityRes){
				AddressLog.insertCity(city,province,nation,stmt);
			}
		}
	}
	
	/* 学生添加工作记录时，插入工作记录，并更新学生的工作记录，修改时间
	 * time：2018/03/15
	 */
	public static void insertWork(Work work,Statement stmt) throws SQLException{
		boolean change = false;
		if(stmt==null){
			change = true;
			stmt = DBConnect.getStmt();
		}
		checkField(work.nation,work.province,work.city,stmt);
		String sql = "INSERT INTO worklog (s_id,s_workspace,s_work,s_worktitle,province,city,nation,s_workphone,update_time) VALUES "
				+ "("+work.s_id+",'"+work.s_workspace+"','"+work.s_work+"','"+work.s_worktitle+"','"+work.province+"','"+work.city+"','"+work.nation+"','"+work.s_workphone+"',"+Helper.dataTime(null)+");";
		stmt.executeUpdate(sql);
		if(change){	//如果添加学生记录是一个单独的事务，需要更新学生的工作记录+检索表
			StudentLog.updateWork(work, stmt);
			connect.commit();
			stmt.close();
		}
	}
	/* 学生修改工作记录时，插入工作记录，并更新学生的工作记录，修改时间  type=1更新学生记录 =0不更新
	 * time：2018/03/15
	 */
	public static void updateWork(Work work,Statement stmt,int type) throws SQLException{
		boolean change = false;
		long time = Helper.dataTime(null);
		if(stmt==null){
			change = true;
			stmt = DBConnect.getStmt();
		}
		checkField(work.nation,work.province,work.city,stmt);
		String sql = "update worklog set s_workspace='"+work.s_workspace+"',s_work='"+work.s_work+"',s_worktitle='"+work.s_worktitle+"',province='"+work.province+"',city='"
		+work.city+"',nation='"+work.nation+"',s_workphone='"+work.s_workphone+"',update_time="+time+" where wl_id="+work.wl_id;
		stmt.executeUpdate(sql);
		StudentLog.updateTime(work.s_id, stmt, time);
		if(type==1)
			StudentLog.updateWork(work, stmt);
		if(change){
			connect.commit();
			stmt.close();
		}
	}
	/* 根据工作ID 得到工作记录的信息，一般用于工作记录的修改
	 * time：2018/03/15
	 */
	public static String[] searchWork(int id){
		String[] work = null;
		String sql;
		try {
			stmt = DBConnect.getStmt();
			sql = "SELECT s_workspace,s_work,s_worktitle,nation,province,s_workphone,city FROM worklog where wl_id="+id+";";
			res = stmt.executeQuery(sql);
			while (res.next()) {
				work = new String[7];
				work[0] = res.getString("nation");
				work[1] = res.getString("province");
				work[2] = res.getString("city");
				work[3] = res.getString("s_work");
				work[4] = res.getString("s_worktitle");
				work[5] = res.getString("s_workspace");
				work[6] = res.getString("s_workphone");
			}
			res.close();
			stmt.close(); 
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return work;
	}
	/* 学生修改工作记录时，插入工作记录，并更新学生的工作记录，修改时间+更新检索表
	 * time：2018/03/15
	 */
	public static void deleteWork(Work work,Statement stmt,int type) throws SQLException{
		boolean change = false;
		if(stmt==null){
			change = true;
			stmt = DBConnect.getStmt();
		}
		String sql = "delete from worklog where wl_id="+work.wl_id+";";
		stmt.executeUpdate(sql);
		if(type==1){	//更新学生表的工作记录+检索表信息
			StudentLog.updateWork(work, stmt);
		}
		if(change){
			connect.commit();
			stmt.close();
		}
	}
	/* 删除学生时删除学生的所有工作记录
	 * time：2018/03/15
	 */
	public static void deleteWork(int sId,Statement stmt) throws SQLException{
		String sql = "delete from worklog where s_id="+sId+";";
		stmt.executeUpdate(sql);
	}
	/* 删除学生时删除学生的所有工作记录
	 * time：2018/03/15
	 */
	public static void deleteManyWk(String count,Statement stmt) throws SQLException{
		String sql = "delete from worklog where s_id in ("+count+");";
		stmt.executeUpdate(sql);
	}
	
	//返回table数组
	public static Vector<Object[]> dao(String str) throws Exception{
		stmt = DBConnect.getStmt();
		ResultSet rs = stmt.executeQuery(str);
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
}
