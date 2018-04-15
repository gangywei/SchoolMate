package schoolmate.database;

import java.sql.SQLException;
import java.sql.Statement;

public class FullsearchLog {
	/* 
	 * inter：插入全文检索信息
	 * time:2018/03/15
	 */
	public static void insertFullsearch(String log,int id,Statement stmt) throws SQLException{
		String sql = "INSERT INTO fullsearch (fs_content,s_id) VALUES ('"+log+"',"+id+");";
		stmt.executeUpdate(sql);
	}
	/* 
	 * inter：修改全文检索信息
	 * time:2018/03/15
	 */
	public static void updateFullsearch(String log,int id,Statement stmt) throws SQLException{
		String sql = "update fullsearch set fs_content='"+log+"' where s_id="+id+";";
		stmt.executeUpdate(sql);
	}
	/* 
	 * inter：删除全文检索信息
	 * time:2018/03/15
	 */
	public static void deleteFullsearch(int id,Statement stmt) throws SQLException{
		String sql = "delete from fullsearch where s_id="+id+";";
		stmt.executeUpdate(sql);
	}
	/* 
	 * inter：多选删除全文检索信息，count=多选的学生ID
	 * time:2018/03/15
	 */
	public static void deleteManyFs(String count,Statement stmt) throws SQLException{
		String sql = "delete from fullsearch where s_id in ("+count+");";
		stmt.executeUpdate(sql);
	}
}
