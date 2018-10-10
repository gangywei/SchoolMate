package schoolmate.database;

import java.sql.ResultSet;
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
	public static String searchFullsearch(int id,Statement stmt) throws SQLException{
		String log = null;
		String sql = "SELECT fs_content FROM fullsearch where s_id="+id+";";
		ResultSet res = stmt.executeQuery(sql);
		while (res.next()) {
			log = res.getString("fs_content");
		}
		return log;
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
	
	public static String getLog(int sId,int ind,String swap,Statement stmt) throws SQLException{
		String log = searchFullsearch(sId, stmt);
		String temp[] = log.split("&");
		switch (ind) {
		case 0:
			temp[ind] = swap;
			break;
		case 1:
			temp[ind] = swap;
			break;
		case 2:
			String swaps[] = swap.split(";");
			temp[ind] = temp[ind].replaceFirst(swaps[1], "");	//学号
			temp[ind] = temp[ind].replaceFirst(swaps[3], "");	//班级
			temp[ind] += ' '+swaps[0];
			temp[ind] += ' '+swaps[2];
			break;
		}
		String nowLog = temp[0]+" & "+temp[1]+" & "+temp[2];
		return nowLog;
	}
}
