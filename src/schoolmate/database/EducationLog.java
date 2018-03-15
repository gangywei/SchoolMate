package schoolmate.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import schoolmate.control.Helper;
import schoolmate.model.DBConnect;
import schoolmate.model.Education;

public class EducationLog {
	private static Connection connect=DBConnect.getConnection();
	private static Statement stmt = null;
	private static ResultSet res;
	public static boolean insertEdu(Statement stmt,Education edu) throws SQLException{
		boolean change = false;
		if(stmt==null){
			change = true;
			stmt = connect.createStatement();
		}
		if(!searchEdu(stmt, edu.s_id, edu.s_education)){
			if(change)
				stmt.close();
			return false;
		}
		String sql = "INSERT INTO Education (s_id,s_no,s_faculty,s_major,s_class,s_education,s_enter,s_graduate,update_time) VALUES "
				+ "("+edu.s_id+",'"+edu.s_no+"','"+edu.s_faculty+"','"+edu.s_major+"','"+edu.s_class+"','"+edu.s_education+"','"+edu.s_enter+"','"+edu.s_graduate+"',"+Helper.dataTime(null)+");";
		int res = 0;
		res = stmt.executeUpdate(sql);
		if(change){
			connect.commit();
			stmt.close();
		}
		return true;
	}
	
	/* old 原数据，now修改后的数据，type 0=专业，1=学院
	 * inter:根据教育流程标签的old（学院、专业）数据更新为now数据。在一个事务下更新 
	 * time：2018/03/15
	 */
	public static void updateEdu(String old[],String now[],Statement stmt,int type) throws SQLException{
		String sql = "";
		if(type==0)
			sql = "update Education set s_faculty='"+now[0]+"',s_major='"+now[1]+"' where s_faculty='"+old[0]+"'and s_major='"+old[1]+"';";
		else
			sql = "update Education set s_faculty='"+now[0]+"' where s_faculty='"+old[0]+"';";
		stmt.executeUpdate(sql);
	}
	
	public static boolean updateEdu(Statement stmt,Education edu) throws SQLException{
		boolean change = false;
		if(stmt==null){
			change = true;
			stmt = connect.createStatement();
		}
		long time = Helper.dataTime(null);
		String sql = "update Education set s_faculty='"+edu.s_faculty+"',s_major='"+edu.s_major+"',s_class='"+edu.s_class+"',s_education='"+edu.s_education+
				"',s_enter='"+edu.s_enter+"',s_graduate='"+edu.s_graduate+"',update_time="+time+" where e_id="+edu.e_id;
		stmt.executeUpdate(sql);
		StudentLog.updateTime(edu.s_id, stmt, time);
		if(change){
			connect.commit();
			stmt.close();
		}
		return true;
	}
	
	public static void deleteEdu(Statement stmt,int s_id) throws SQLException{
		boolean change = false;
		if(stmt==null){
			change = true;
			stmt = connect.createStatement();
		}
		String sql = "delete from Education where e_id="+s_id+"";
		stmt.executeUpdate(sql);
		if(change){
			connect.commit();
			stmt.close();
		}
	}
	
	public static String[] searchEdu(int id){
		String[] edu = null;
		String sql;
		try {
			stmt = connect.createStatement();
			sql = "SELECT s_no,s_faculty,s_major,s_class,s_education,s_enter,s_graduate FROM education where e_id="+id+";";
			res = stmt.executeQuery(sql);
			while (res.next()) {
				edu = new String[7];
				edu[0] = res.getString("s_no");
				edu[1] = res.getString("s_faculty");
				edu[2] = res.getString("s_major");
				edu[3] = res.getString("s_class");
				edu[4] = res.getString("s_education");
				edu[5] = res.getString("s_enter");
				edu[6] = res.getString("s_graduate");
			}
			res.close();
			stmt.close(); 
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return edu;
	}
	
	public static void deleteManyEdu(Statement stmt,String count) throws SQLException{
		String sql = "delete from Education where s_id in ("+count+");";
		stmt.executeUpdate(sql);
	}
	
	public static String getEid(String condition){
		String strId = "";
		try {
			stmt = connect.createStatement();
			String sql = "select group_concat(e_id) strId from education e "+condition+";";
			res = stmt.executeQuery(sql);
			while (res.next()) {
				strId = res.getString("strId");
			}	
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return strId;
	}
	//true=数据库没有该记录，可以插入。
	public static boolean searchEdu(Statement stmt,int s_id,String education) throws SQLException{
		int count = 0;
		String sql = "SELECT count(*) totle FROM Education where s_id="+s_id+" and s_education='"+education+"';";
		res = stmt.executeQuery(sql);
		while (res.next()) {
			count = res.getInt("totle");
		}
		res.close();
		if(count>=1)
			return false;
		return true;
	}
	
	public static Vector<Object[]> getUpdate(long time) throws SQLException{
		String sql = "select * from education where update_time>="+time;
		stmt = connect.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		ResultSetMetaData rsmd=rs.getMetaData();//用于获取关于 ResultSet 对象中列的类型和属性信息的对象
		int colNum=rsmd.getColumnCount()-1;	//得到列数
		Vector<Object[]> info = new Vector<Object[]>();
		int i=0;
		while (rs.next()){
			info.add((new Object[colNum]));
			info.elementAt(i)[0] = 2;
			for (int j=1;j<colNum;j++){
				info.elementAt(i)[j]=(Object) rs.getObject(j+1);
			}
			i++;
		}
		return info;
	}
}
