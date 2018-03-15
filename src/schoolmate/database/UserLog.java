package schoolmate.database;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Vector;

import javax.swing.JOptionPane;

import schoolmate.control.Helper;
import schoolmate.model.DBConnect;
import schoolmate.model.User;
import schoolmate.view.PencilMain;

public class UserLog {
	private static Connection connect=DBConnect.getConnection();
	private static Statement stmt = null;
	private static ResultSet res;
	
	public static boolean SelectUser(String userName) throws SQLException{
		int count = 0;
		stmt = connect.createStatement();
		String sql;
		try {
			sql = "SELECT count(*) totle FROM user where u_count='"+userName+"'";
			System.out.println(sql);
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
		    if(count==1){
		    	return true;
		    }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static String[] getUserBID(int id){
		String[] user = null;
		String sql;
		try {
			stmt = connect.createStatement();
			sql = "SELECT u_name,u_role,u_count,faculty FROM user where u_id="+id+";";
			res = stmt.executeQuery(sql);
			while (res.next()) {
				user = new String[5];
				user[0] = res.getString("u_name");
				user[1] = res.getString("u_count");
				user[2] = res.getString("faculty");
				user[3] = res.getInt("u_role")+"";
			}
			res.close();
			stmt.close(); 
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return user;
	}
	
	public static String[] getUser(String count,String pwd,long time){
		String[] user = null;
		String sql;
		try {
			stmt = connect.createStatement();
			sql = "SELECT u_name,u_problem,u_answer,u_role,faculty,u_sign FROM user where u_count='"+count+"' and u_pwd='"+Helper.EncoderByMd5(pwd)+"';";
			res = stmt.executeQuery(sql);
			long sign = 0;
			int number = 0;
			while (res.next()) {
				number++;
				user = new String[5];
				user[0] = res.getString("u_name");
				user[1] = res.getInt("u_problem")+"";
				user[2] = res.getString("u_answer");
				user[3] = res.getString("faculty");
				user[4] = res.getInt("u_role")+"";
				sign = res.getInt("u_sign");
			}
			if(number==0)
				JOptionPane.showMessageDialog(null, "用户名或密码错误");
			if(sign<time){
				JOptionPane.showMessageDialog(null, "请联系管理员激活账户");
				return null;
			}
			res.close();
			stmt.close(); 
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return user;
	}
	
	public static boolean updateAns(String count,int index,String answer,String name,String faculty){
		try {
			stmt = connect.createStatement();
			String sql = "update user set u_problem="+index+",u_answer='"+answer+"',u_name='"+name+"',faculty='"+faculty+"' where u_count = '"+count+"';";
			int res = stmt.executeUpdate(sql);
			System.out.println(sql);
			if(res==1){
				connect.commit();
			    stmt.close(); 
			    return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean updateUser(String Name,String userName,int userRole,String userFaculty) throws NoSuchAlgorithmException, UnsupportedEncodingException, SQLException{
		boolean res = true;
		try{
			stmt = connect.createStatement();
			if(res){
				String sql = "INSERT INTO user "
						+ "(u_name,u_count,u_role,Faculty) "
						+ "VALUES "
						+ "('"+Name+"','"+userName+"',"+userRole+",'"+userFaculty+"')";
				int number = stmt.executeUpdate(sql);
				if(number<=0)
					res = false;
			}		
		}catch(Exception e){
			connect.rollback();
		}
		if(res)
			connect.commit();
		stmt.close();
		return res;
	}
	

	public static boolean insterUser(String Name,String userName,String passWord,int userProblem,String userAnswer,int userRole,String userFaculty) throws NoSuchAlgorithmException, UnsupportedEncodingException, SQLException{
		boolean res = true;
		try{
			stmt = connect.createStatement();
			if(res){
				String sql = "INSERT INTO user "
						+ "(u_name,u_count,u_pwd,u_problem,u_answer,u_role,Faculty,u_sign) "
						+ "VALUES "
						+ "('"+Name+"','"+userName+"','"+Helper.EncoderByMd5(passWord)+"',"+userProblem+",'"+userAnswer+"',"+userRole+",'"+userFaculty+"',"+new Date().getTime()/1000+")";
				int number = stmt.executeUpdate(sql);
				if(number<=0)
					res = false;
			}		
		}catch(Exception e){
			connect.rollback();
		}
		if(res)
			connect.commit();
		stmt.close();
		return res;
	}
	
	
	public static int delete(int id){
		int a ;
		try {
			stmt = connect.createStatement();
			String sql = "delete from user where u_id="+id;
			System.out.println(sql);
			a= stmt.executeUpdate(sql);
			connect.commit();
			stmt.close();
		} catch (SQLException e) {
			PencilMain.logger.error("删除用户信息抛错"+e.getMessage());
			e.printStackTrace();
			return 0;
		}
		return a;
	}
	
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
