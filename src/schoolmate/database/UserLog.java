package schoolmate.database;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import schoolmate.control.Helper;
import schoolmate.model.DBConnect;
import schoolmate.model.User;

public class UserLog {
	private static Connection connect=DBConnect.getConnection();
	private static Statement stmt = null;
	private static ResultSet res;
	
	public static boolean userLogin(User user) throws SQLException, NoSuchAlgorithmException, UnsupportedEncodingException{
		int count = 0;
		stmt = connect.createStatement();
		String sql;
		try {
			sql = "SELECT count(*) totle FROM user where u_count='"+user.u_count+"' and u_pwd='"+Helper.EncoderByMd5(user.u_pwd)+"';";
			res = stmt.executeQuery(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		while (res.next()) {
			count = res.getInt("totle");
		}
		res.close();
		stmt.close(); 
		if(count==1)
			return true;
		return false;
	}
	
	public static boolean SelectUser(String userName,int index,String ans) throws SQLException{
		int count = 0;
		stmt = connect.createStatement();
		String sql;
		try {
			sql = "SELECT count(*) totle FROM user where u_count='"+userName+"' and u_problem="+index+" and u_answer='"+ans+"';";
			res = stmt.executeQuery(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		while (res.next()) {
			count = res.getInt("totle");
		}
		res.close();
		stmt.close(); 
		if(count==1)
			return true;
		return false;
	}
	
	public static boolean alterPwd(String userName,String passWord,int index,String ans) throws NoSuchAlgorithmException, UnsupportedEncodingException, SQLException{
		stmt = connect.createStatement();
		String sql = "update user set u_pwd ='"+Helper.EncoderByMd5(passWord)+"' where u_count ='"+userName+"' and u_problem="+index+" and u_answer='"+ans+"';";
		System.out.println(sql);
		try {
			int count = stmt.executeUpdate(sql);
			connect.commit();
		    stmt.close(); 
		    if(count>=1){
		    	return true;
		    }
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}
	
	public static String[] getUser(String count,String pwd){
		String[] user = null;
		String sql;
		try {
			stmt = connect.createStatement();
			sql = "SELECT u_problem,u_answer,u_role FROM user where u_count='"+count+"' and u_pwd='"+Helper.EncoderByMd5(pwd)+"';";
			res = stmt.executeQuery(sql);
			while (res.next()) {
				user = new String[3];
				user[0] = res.getInt("u_role")+"";
				user[1] = res.getInt("u_problem")+"";
				user[2] = res.getString("u_answer");
			}
			res.close();
			stmt.close(); 
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return user;
	}
	
	public static boolean updateAns(String count,int index,String answer){
		try {
			stmt = connect.createStatement();
			String sql = "update user set u_problem="+index+",u_answer='"+answer+"' where u_count = '"+count+"';";
			stmt.executeUpdate(sql);
			connect.commit();
		    stmt.close(); 
		    return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
}
