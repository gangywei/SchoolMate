package schoolmate.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import schoolmate.model.DBConnect;

public class FacultyLog {
	private static Connection connect=DBConnect.getConnection();
	private static Statement stmt = null;
	private static ResultSet res;
	public static boolean searchFaculty(String faculty) throws SQLException{
		int count = 0;
		stmt = connect.createStatement();
		String sql = "SELECT count(*) totle FROM faculty where f_name='"+faculty+"';";
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
	
	public static String[] allFaculty(){
		int count = 0;
		try {
			stmt = connect.createStatement();
			String sql = "select count(*) totle from faculty;";
			res = stmt.executeQuery(sql);
			while (res.next()) {
				count = res.getInt("totle");
			}
			String data[] = new String[count];
			sql = "select * from faculty";
			res = stmt.executeQuery(sql);
			int i=0;
			while (res.next()) {
				data[i] = res.getString("f_name");
				i++;
			}
			return data;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void insertFaculty(String faculty,Statement stmt) throws SQLException{
		boolean pool = false;	//新建数据库通道
		if(stmt==null)
			pool = true;	//新建通道
		if(pool)
			stmt = connect.createStatement();
		String sql = "INSERT INTO faculty (f_name) VALUES ('"+faculty+"');";
		stmt.executeUpdate(sql);
		connect.commit();
		if(pool){
			connect.commit();
			stmt.close();
		}
	}
}
