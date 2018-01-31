package schoolmate.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import schoolmate.model.DBConnect;

public class AddressLog {
	//type=1代表可以进行搜索功能的省和市。type=0不可以进行汇总
	private static Connection connect=DBConnect.getConnection();
	private static Statement stmt = null;
	private static ResultSet res;
	//国家的处理方法
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
	
	public static void insertNation(String address,Statement stmt) throws SQLException{
		String sql = "INSERT INTO nation (n_name) VALUES ('"+address+"');";
		stmt.executeUpdate(sql);
	}
	
	//省份的处理方法
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
	
	public static String[] allProvince(String str){
		int count = 0;
		try {
			stmt = connect.createStatement();
			String sql = "select count(*) totle from province "+str+";";
			res = stmt.executeQuery(sql);
			while (res.next()) {
				count = res.getInt("totle");
			}
			String data[] = new String[count];
			sql = "select * from province "+str+";";
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
	
	public static void insertProvince(String address,String nation,Statement stmt) throws SQLException{
		String sql = "INSERT INTO province (p_name,n_name) VALUES ('"+address+"','"+nation+"');";
		stmt.executeUpdate(sql);
	}
	
	//市区的处理方法
	public static boolean searchCity(String address,String province) throws SQLException{
		int count = 0;
		stmt = connect.createStatement();
		String sql = "SELECT count(*) totle FROM city where c_name='"+address+"' and p_name= '"+province+"';";
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
	
	public static String[] allCity(String str){
		int count = 0;
		try {
			stmt = connect.createStatement();
			String sql = "select count(*) totle from city "+str+";";
			res = stmt.executeQuery(sql);
			while (res.next()) {
				count = res.getInt("totle");
			}
			String data[] = new String[count];
			sql = "select * from city "+str+";";
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
	
	public static void insertCity(String address,String province,Statement stmt) throws SQLException{
		String sql = "INSERT INTO city (c_name,p_name) VALUES ('"+address+"','"+province+"');";
		stmt.executeUpdate(sql);
	}
}
