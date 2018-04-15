package schoolmate.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import schoolmate.model.DBConnect;
import schoolmate.view.PencilMain;

public class AddressLog {
	//type=1代表可以进行搜索功能的省和市。type=0不可以进行汇总
	private static Connection connect=DBConnect.getConnection();
	private static Statement stmt = null;
	private static ResultSet res;
	/* 
	 * inter：判断该国家是否存在。 true=存在 false=不存在。
	 * time:2018/03/15
	 */
	public static boolean searchNation(String address) throws SQLException{
		int count = 0;
		stmt = connect.createStatement();
		String sql = "SELECT count(*) totle FROM nation where n_name='"+address+"';";
		res = stmt.executeQuery(sql);
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
	 * inter：根据国家的所有字段，删除国家数据，并通过 delProvinceNa和delCityNa方法，删除国家下的省份和市区。 并使用delStuFromAdd方法删除该地区的学生
	 * time:2018/03/15
	 */
	public static boolean deleteNation(String name){
		try {
			stmt = connect.createStatement();
			String sql = "delete from nation where n_name='"+name+"';";
			StudentLog.delStuFromAdd("where s_nation='"+name, stmt);
			stmt.executeUpdate(sql);
			delProvinceNa(name,stmt);
			delCityNa(name, stmt);
			connect.commit();
			stmt.close();
		} catch (SQLException e) {
			PencilMain.logger.error("删除国家信息抛错"+e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/* 
	 * inter：根据国家的所有字段，删除国家数据，并通过 delProvinceNa和delCityNa方法，删除国家下的省份和市区。
	 * time:2018/03/15
	 */
	public static boolean updateNation(String old[],String now[]){
		try {
			stmt = connect.createStatement();
			String sql = "update nation set n_name='"+now[0]+"' where n_name='"+old[0]+"';";
			stmt.executeUpdate(sql);
			WorkLog.updateWork(old,now,stmt,2);
			updProvinceNa(old, now ,stmt);
			updCityNa(old, now,stmt);
			connect.commit();
			stmt.close();
		} catch (SQLException e) {
			PencilMain.logger.error("删除国家信息抛错"+e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}
	/* 
	 * inter：得到所有的国家名称，用来提示用户输入数据。
	 * time:2018/03/15
	 */
	public static String[] allNation(){
		int count = 0;
		try {
			stmt = connect.createStatement();
			String sql = "select count(*) totle from nation;";
			res = stmt.executeQuery(sql);
			while (res.next()) {
				count = res.getInt("totle");
			}
			String data[] = new String[count];
			sql = "select * from nation";
			res = stmt.executeQuery(sql);
			int i=0;
			while (res.next()) {
				data[i] = res.getString("n_name");
				i++;
			}
			return data;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	/* 
	 * inter：一般用于国家不存在时，在一个事务的情况下插入一个国家。
	 * time:2018/03/15
	 */
	public static void insertNation(String address,Statement stmt) throws SQLException{
		String sql = "INSERT INTO nation (n_name) VALUES ('"+address+"');";
		stmt.executeUpdate(sql);
	}
	
	/* 
	 * inter：判断该省份是否存在。 true=存在 false=不存在。
	 * time:2018/03/15
	 */
	public static boolean searchProvince(String address,String nation) throws SQLException{
		int count = 0;
		stmt = connect.createStatement();
		String sql = "SELECT count(*) totle FROM province where p_name='"+address+"' and n_name= '"+nation+"';";
		res = stmt.executeQuery(sql);
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
	 * inter：根据省份的所有字段，删除省份数据，并通过 delCityFromPro 方法，删除省份下的市区。并通过 delStuFromAdd 方法删除该地区的学生信息。
	 * time:2018/03/15
	 */
	public static int deleteProvince(String province,String nation){
		int a ;
		try {
			stmt = connect.createStatement();
			String strName = "where s_province='"+province +"' and s_nation='"+nation;
			String name = province +"' and n_name='"+nation+"";
			StudentLog.delStuFromAdd(strName, stmt);
			delCityFromPro(name,stmt);
			String sql = "delete from province where p_name='"+name+"' and n_name='"+nation+"'";
			a= stmt.executeUpdate(sql);
			connect.commit();
			stmt.close();
		} catch (SQLException e) {
			PencilMain.logger.error("删除省份信息抛错"+e.getMessage());
			e.printStackTrace();
			return 0;
		}
		return a;
	}
	
	/* old：原省份数据 ，now：修改后的省份数据
	 * inter：根据市区的所有字段，更新对应的市区数据，
	 * time:2018/03/15
	 */
	public static boolean updateProvince(String old[],String now[]){
		try {
			stmt = connect.createStatement();
			String name = old[1] +"' and n_name='"+old[0]+"";
			String sql = "update province set p_name='"+now[1]+"',n_name='"+now[0]+"' where p_name='"+name+"';";	
			stmt.executeUpdate(sql);
			WorkLog.updateWork(old,now,stmt,1);
			updCityFromPro(old,now,stmt);
			connect.commit();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	/*
	 * inter：根据一定情况得到该情况下的省份数据。一般用于提示用户输入
	 * time:2018/03/15
	 */
	public static String[] allProvince(String str){
		int count = 0;
		try {
			stmt = connect.createStatement();
			String sql = "select count(distinct(p_name)) totle from province "+str+";";
			res = stmt.executeQuery(sql);
			while (res.next()) {
				count = res.getInt("totle");
			}
			String data[] = new String[count];
			sql = "select distinct(p_name) from province "+str+";";
			res = stmt.executeQuery(sql);
			int i=0;
			while (res.next()) {
				data[i] = res.getString("p_name");
				i++;
			}
			return data;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	/* 
	 * inter：用于省份不存在时，在一个事务的情况下插入一个省份。
	 * time:2018/03/15
	 */
	public static void insertProvince(String address,String nation,Statement stmt) throws SQLException{
		String sql = "INSERT INTO province (p_name,n_name) VALUES ('"+address+"','"+nation+"');";
		stmt.executeUpdate(sql);
	}
	
	/* 
	 * inter：删除国家时同时删除省份的标签
	 * time:2018/03/15
	 */
	public static void delProvinceNa(String nation,Statement stmt) throws SQLException{
		String sql = "delete from province where n_name='"+nation+"'";
		stmt.executeUpdate(sql);
	}
	
	/* 
	 * inter：更新国家时同时更新省份的标签
	 * time:2018/03/15
	 */
	public static void updProvinceNa(String old[],String now[],Statement stmt) throws SQLException{
		String sql = "update province set n_name='"+now[0]+"' where n_name='"+old[0]+"'";
		stmt.executeUpdate(sql);
	}
	
	//判断是否存在该市区，判断市区的所有字段
	public static boolean searchCity(String address,String province,String nation) throws SQLException{
		int count = 0;
		stmt = connect.createStatement();
		String sql = "SELECT count(*) totle FROM city where c_name='"+address+"' and p_name= '"+province+"' and n_name= '"+nation+"';";
		res = stmt.executeQuery(sql);
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
	 * inter：根绝删除省份是删除市区标签
	 * time:2018/03/15
	 */
	public static void delCityFromPro(String province,Statement stmt) throws SQLException{
		String sql = "delete from city where p_name='"+province+"';";	
		stmt.executeUpdate(sql);
	}
	/* 
	 * inter：根据的更新的省份字段，更新对应的市区数据
	 * time:2018/03/15
	 */
	public static void updCityFromPro(String old[],String now[],Statement stmt) throws SQLException{
		String name = old[1] +"' and n_name='"+old[0]+"";
		String sql = "update city set p_name='"+now[1]+"',n_name='"+now[0]+"' where p_name='"+name+"';";
		stmt.executeUpdate(sql);
	}
	/* 
	 * inter：根绝删除国家同一事务删除市区标签
	 * time:2018/03/15
	 */
	public static void delCityNa(String nation,Statement stmt) throws SQLException{
		String sql = "delete from city where n_name='"+nation+"';";		
		stmt.executeUpdate(sql);
	}
	/* 
	 * inter：根据的更新的国家字段，更新对应的市区数据
	 * time:2018/03/15
	 */
	public static void updCityNa(String old[],String now[],Statement stmt) throws SQLException{
		String sql = "update city set n_name='"+now[0]+"' where n_name='"+old[0]+"';";
		stmt.executeUpdate(sql);
	}
	/* 
	 * inter：删除市区的数据。并通过 delStuFromAdd 方法删除该地区的学生信息。
	 * time:2018/03/15
	 */
	public static boolean deleteCity(String city,String province,String nation){
		try {
			stmt = connect.createStatement();
			String strName = "where s_city='"+city+"' and s_province='"+province+"' and s_nation='"+nation;
			String name = city+"' and p_name='"+province+"' and n_name='"+nation;
			StudentLog.delStuFromAdd(strName, stmt);
			String sql = "delete from city where c_name='"+name+"';";	
			stmt.executeUpdate(sql);
			connect.commit();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	/* old：原市区数据 ，now：修改后的市区数据
	 * inter：根据市区的所有字段，更新对应的市区数据
	 * time:2018/03/15
	 */
	public static boolean updateCity(String old[],String now[]){
		try {
			stmt = connect.createStatement();
			String name = old[2]+"' and p_name='"+old[1]+"' and n_name='"+old[0];
			String sql = "update city set p_name='"+now[1]+"', n_name='"+now[0]+"', c_name='"+now[2]+"' where c_name='"+name+"';";	
			stmt.executeUpdate(sql);
			WorkLog.updateWork(old,now,stmt,0);
			connect.commit();
			stmt.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static String[] allCity(String str){
		int count = 0;
		try {
			stmt = connect.createStatement();
			String sql = "select count(distinct(c_name)) totle from city "+str+";";
			res = stmt.executeQuery(sql);
			while (res.next()) {
				count = res.getInt("totle");
			}
			String data[] = new String[count];
			sql = "select distinct(c_name) from city "+str+";";
			System.out.println(sql);
			res = stmt.executeQuery(sql);
			int i=0;
			while (res.next()) {
				data[i] = res.getString("c_name");
				i++;
			}
			return data;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	/* 
	 * inter：用于市区不存在时，在一个事务的情况下插入一个市区。
	 * time:2018/03/15
	 */
	public static void insertCity(String address,String province,String nation,Statement stmt) throws SQLException{
		String sql = "INSERT INTO city (c_name,p_name,n_name) VALUES ('"+address+"','"+province+"','"+nation+"');";
		stmt.executeUpdate(sql);
	}
}
