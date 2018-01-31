package schoolmate.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import schoolmate.model.DBConnect;
import schoolmate.model.Student;
import schoolmate.model.Worklog;

public class WorkLog {
	private static Connection connect=DBConnect.getConnection();
	private static Statement stmt = null;
	private static ResultSet res;
	
	public static void insertWork(Student stu,Statement stmt) throws SQLException{
		String sql = "INSERT INTO worklog (s_id,s_workspace,s_work,s_worktitle,province,city,nation) VALUES "
				+ "("+stu.s_id+",'"+stu.s_workspace+"','"+stu.s_work+"','"+stu.s_worktitle+"','"+stu.s_province+"','"+stu.s_city+"','"+stu.s_nation+"');";
		stmt.executeUpdate(sql);
	}
	public static void deleteWork(int id,Statement stmt) throws SQLException{
		String sql = "delete from worklog where s_id="+id+";";
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
}
