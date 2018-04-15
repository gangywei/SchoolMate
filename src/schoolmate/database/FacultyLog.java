package schoolmate.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import schoolmate.model.DBConnect;
import schoolmate.view.PencilMain;

public class FacultyLog {
	private static Connection connect=DBConnect.getConnection();
	private static Statement stmt = null;
	private static ResultSet res;
	/* 
	 * inter：判断该学院是否存在。 true=存在 false=不存在。
	 * time:2018/03/15
	 */
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
	/* 
	 * inter：得到所有的学院名称，用来提示用户输入数据。
	 * time:2018/03/15
	 */
	public static String[] allFaculty(String limit){
		int count = 0;
		try {
			stmt = connect.createStatement();
			String sql = "select count(*) totle from faculty "+limit+";";
			
			res = stmt.executeQuery(sql);
			while (res.next()) {
				count = res.getInt("totle");
			}
			String data[] = new String[count];
			sql = "select * from faculty "+limit+";";
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
	
	@SuppressWarnings("unused")
	/* 
	 * inter：新加学院信息
	 * time:2018/03/15
	 */
	public static boolean insertFaculty(String faculty,Statement stmt) throws SQLException{
		if(stmt==null)
			stmt = connect.createStatement();
		String sql = "INSERT INTO faculty (f_name) VALUES ('"+faculty+"');";
		int res = stmt.executeUpdate(sql);
		if(stmt==null){
			connect.commit();
			stmt.close();
		}
		if(res>0)
			return true;
		else
			return false;
	}
	/*
	 * inter：根据学院的字段，删除对应的学院信息，并通过delMajorFac方法删除学院下的专业。
	 * time:2018/03/15
	 */
	public static boolean deleteFaculty(String name){
		try {
			stmt = connect.createStatement();
			String sql = "delete from faculty where f_name='"+name+"'";
			StudentLog.delStuFromEdu("where s_faculty='"+name, stmt);
			MajorLog.delMajorFac(name,stmt);
			stmt.executeUpdate(sql);
			connect.commit();
			stmt.close();
		} catch (SQLException e) {
			PencilMain.logger.error("删除学院标签抛错"+e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}
	/*
	 * inter：根据原来学院的字段，更新对应的学院信息，并通过updMajorFac更新专业的信息
	 * time:2018/03/15
	 */
	public static boolean updateFactlty(String[] old, String[] now) {
		try {
			stmt = connect.createStatement();
			String sql = "update faculty set f_name='"+now[0]+"' where f_name='"+old[0]+"'";
			MajorLog.updMajorFac(now[0],old[0],stmt);
			EducationLog.updateEdu(old, now, stmt, 1);
			stmt.executeUpdate(sql);
			connect.commit();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
