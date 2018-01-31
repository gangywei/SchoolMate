package schoolmate.database;

import java.sql.SQLException;
import java.sql.Statement;

public class FullsearchLog {
	public static void insertFullsearch(String log,int id,Statement stmt) throws SQLException{
		String sql = "INSERT INTO fullsearch (fs_content,s_id) VALUES ('"+log+"',"+id+");";
		System.out.println(sql);
		stmt.executeUpdate(sql);
	}
	public static void updateFullsearch(String log,int id,Statement stmt) throws SQLException{
		String sql = "update fullsearch set fs_content='"+log+"' where s_id="+id+";";
		stmt.executeUpdate(sql);
	}
	public static void deleteFullsearch(int id,Statement stmt) throws SQLException{
		String sql = "delete from fullsearch where s_id="+id+";";
		stmt.executeUpdate(sql);
	}
	public static void deleteManyFs(String count,Statement stmt) throws SQLException{
		String sql = "delete from fullsearch where s_id in ("+count+");";
		System.out.println(sql);
		stmt.executeUpdate(sql);
	}
}
