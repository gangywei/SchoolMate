package schoolmate.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import schoolmate.model.DBConnect;
import schoolmate.view.PencilMain;

public class MajorLog {
	private static Connection connect=DBConnect.getConnection();
	private static Statement stmt = null;
	private static ResultSet res;
	public static boolean searchMajor(String major,String faculty) throws SQLException{
		int count = 0;
		stmt = connect.createStatement();
		String sql = "SELECT count(*) totle FROM major where m_name='"+major+"' and f_name='"+faculty+"';";
		res = stmt.executeQuery(sql);
		while (res.next()) {
			count = res.getInt("totle");
		}
		res.close();
		stmt.close(); 
		if(count==1)
			return true;
		return false;
	}
	//筛选条件
	public static String[] allMajor(String condition){
		int count = 0;
		try {
			stmt = connect.createStatement();
			String sql = "select count(*) totle from major "+condition+";";
			res = stmt.executeQuery(sql);
			while (res.next()) {
				count = res.getInt("totle");
			}
			String data[] = new String[count];
			sql = "select * from major "+ condition +";";
			res = stmt.executeQuery(sql);
			int i=0;
			while (res.next()) {
				data[i] = res.getString("m_name");
				i++;
			}
			return data;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void insertMajor(String major,String faculty,Statement stmt) throws SQLException{
		boolean pool = false;	//新建数据库通道
		if(stmt==null)
			pool = true;	//新建通道
		if(pool)
			stmt = connect.createStatement();
		String sql = "INSERT INTO major (m_name,f_name) VALUES ('"+major+"','"+faculty+"');";
		stmt.executeUpdate(sql);
		if(pool){
			connect.commit();
			stmt.close();
		}
	}
	/*
	 * inter：根据学院的字段，删除学院下的专业。
	 * time:2018/03/15
	 */
	public static void delMajorFac(String faculty,Statement stmt) throws SQLException{
		String sql = "delete from major where f_name='"+faculty+"'";
		stmt.executeUpdate(sql);
	}
	/*
	 * inter：根据学院的字段，更新学院对应专业下学院。
	 * time:2018/03/15
	 */
	public static void updMajorFac(String now,String old,Statement stmt) throws SQLException{
		String sql = "update major set f_name='"+now+"' where f_name='"+old+"'";
		stmt.executeUpdate(sql);
	}
	/*
	 * inter：根据专业的所有字段，删除对应的专业标签
	 * time:2018/03/15
	 */
	public static boolean deleteMajor(String major,String faculty){
		try {
			stmt = connect.createStatement();
			String name = major+"' and f_name='"+faculty;
			StudentLog.delStuFromEdu("where s_major='"+major+"'and s_faculty='"+faculty, stmt);
			String sql = "delete from major where m_name='"+name+"'";
			stmt.executeUpdate(sql);
			connect.commit();
			stmt.close();
		} catch (SQLException e) {
			PencilMain.logger.error("删除专业标签抛错"+e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}
	/* old：原专业数据 ，now：修改后的数据
	 * inter：根据专业的所有字段，更新对应的专业数据
	 * time:2018/03/15
	 */
	public static boolean updateMajor(String[] old,String[] now){
		try {
			stmt = connect.createStatement();
			String name = old[1]+"' and f_name='"+old[0];
			String sql = "update major set m_name='"+now[1]+"',f_name='"+now[0]+"' where m_name='"+name+"'";
			stmt.executeUpdate(sql);
			EducationLog.updateEdu(old, now, stmt, 0);
			connect.commit();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
