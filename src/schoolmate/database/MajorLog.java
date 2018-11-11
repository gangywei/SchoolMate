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
	/* 
	 * inter：插入全文检索信息
	 * time:2018/03/15
	 */
	public static boolean searchMajor(String faculty,String major) throws SQLException{
		int count = 0;
		stmt = DBConnect.getStmt();
		String sql = "SELECT count(*) totle FROM major where m_name='"+major+"' and f_name='"+faculty+"';";
		ResultSet res = stmt.executeQuery(sql);
		while (res.next()) {
			count = res.getInt("totle");
		}
		res.close();
		stmt.close(); 
		if(count>=1)
			return true;
		return false;
	}
	/* 
	 * inter：得到所有的专业名
	 * time:2018/03/15
	 */
	public static String[] allMajor(String condition){
		int count = 0;
		try {
			stmt = DBConnect.getStmt();
			String sql = "select count(*) totle from major "+condition+";";
			ResultSet res = stmt.executeQuery(sql);
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
	public static String[][] allMajor1(String condition){
		int count = 0;
		try {
			stmt = DBConnect.getStmt();
			String sql = "select count(*) totle from major "+condition+" group by f_name;";
			System.out.println(sql);
			ResultSet res = stmt.executeQuery(sql);
			while (res.next()) {
				count++;
			}
			String data[][] = new String[count][2];
			sql = "select group_concat(m_name) as m_name,f_name from major "+condition+" group by f_name;";
			res = stmt.executeQuery(sql);
			int i=0;
			while (res.next()) {
				data[i][0] = res.getString("f_name");
				data[i][1] = res.getString("m_name");
				i++;
			}
			return data;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	/* 
	 * inter：null=在同一事务下，在一个新的事务下插入一个学院信息。
	 * time:2018/03/15
	 */
	public static void insertMajor(String major,String faculty,Statement stmt) throws SQLException{
		boolean pool = false;	//新建数据库通道
		if(stmt==null)
			pool = true;	//新建通道
		if(pool)
			stmt = DBConnect.getStmt();
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
	 * inter：根据学院和专业信息删除专业。
	 * time:2018/03/15
	 */
	public static void delMajor(String faculty,String major,Statement stmt) throws SQLException{
		String sql = "delete from major where m_name='"+major+"' and f_name='"+faculty+"'";
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
			stmt = DBConnect.getStmt();
			StudentLog.delStuFromEdu("where s_major='"+major+"'and s_faculty='"+faculty, stmt);
			String sql = "delete from major where m_name='"+major+"' and f_name='"+faculty+"'";
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
	/* old：原专业数据 ，now：修改后的数据	[0=学院 1=专业]
	 * inter：根据专业的所有字段，更新对应的专业数据
	 * time:2018/03/15
	 */
	public static boolean updateMajor(String[] old,String[] now){
		try {
			stmt = DBConnect.getStmt();
			//不存在修改成为的数据，修改数据;存在删除当前数据。
			if(!searchMajor(now[0],now[1])){	
				String sql = "update major set m_name='"+now[1]+"',f_name='"+now[0]+"' where m_name='"+old[1]+"' and f_name='"+old[0]+"'";
				stmt.executeUpdate(sql);
			}else{
				delMajor(old[0],old[1],stmt);
			}
			if(!FacultyLog.searchFaculty(now[0]))
				FacultyLog.insertFaculty(now[0], stmt);
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
