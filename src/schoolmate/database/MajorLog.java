package schoolmate.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import schoolmate.model.DBConnect;

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
}
