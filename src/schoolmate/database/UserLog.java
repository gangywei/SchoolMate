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
	/* 
	 * inter: 判断数据库中是否存在该账号。 存在=true 不存在=false
	 * time：2018/03/15
	 */
	public static boolean SelectUser(String number) throws SQLException{
		int count = 0;
		stmt = connect.createStatement();
		String sql;
		try {
			sql = "SELECT count(*) totle FROM user where u_count='"+number+"'";
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
	/* 
	 * inter: 根据用户账号和密码登录用户。 存在=true 不存在=false
	 * time：2018/03/15
	 */
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
	/* 
	 * inter: 用户修改密码功能，(用户账号、问题下标、问题答案)
	 * time：2018/03/15
	 */
	public static boolean alterPwd(String number,String passWord,int index,String ans) throws NoSuchAlgorithmException, UnsupportedEncodingException, SQLException{
		stmt = connect.createStatement();
		String sql = "update user set u_pwd ='"+Helper.EncoderByMd5(passWord)+"' where u_count ='"+number+"' and u_problem="+index+" and u_answer='"+ans+"';";
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
	/* 
	 * inter: 根据用户ID返回用户信息。
	 * time：2018/03/15
	 */
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
	/* 
	 * inter: 用户登录，根据用户的ID删除用户，当数据库操作数=1时返回值true，否则事务回滚返回false。
	 * 判断用户登录时间大于注册时间并小于结束时间，用户使用时间递增更新，当前使用时间少于数据库使用时间，判断软件是否到期。
	 * time：2018/03/15
	 */
	public static String[] getUser(String count,String pwd,long time){
		String[] user = null;
		String sql;
		try {
			stmt = connect.createStatement();
			sql = "SELECT u_name,u_problem,u_answer,u_role,faculty,u_sign,u_use FROM user where u_count='"+count+"' and u_pwd='"+Helper.EncoderByMd5(pwd)+"';";
			res = stmt.executeQuery(sql);
			long endTime = 0;
			long useTime = 0;
			int number = 0;
			while (res.next()) {
				number++;
				user = new String[5];
				user[0] = res.getString("u_name");
				user[1] = res.getInt("u_problem")+"";
				user[2] = res.getString("u_answer");
				user[3] = res.getString("faculty");
				user[4] = res.getInt("u_role")+"";
				endTime = res.getInt("u_sign");
				useTime = res.getInt("u_use");
			}
			long begTime = endTime-31536000;
			res.close();
			if(number==0){
				JOptionPane.showMessageDialog(null, "用户名或密码错误");
				stmt.close(); 
				return null;
			}
			//判断软件是否过期
			if(time>begTime&&time<endTime){
				long tempTime = time-begTime;	
				if(tempTime>=useTime){	//当前使用时间>数据库使用时间=更新使用时间
					sql = "update user set u_use="+tempTime+" where u_count='"+count+"';";
					stmt.executeUpdate(sql);
					connect.commit();
				}else{	//检测到修改系统时间
					JOptionPane.showMessageDialog(null, "请联系管理员激活账户");
					stmt.close();
					return null;
				}
			}else{
				JOptionPane.showMessageDialog(null, "请联系管理员激活账户");
				stmt.close();
				return null;
			}
		} catch (Exception e) {
			if(e.getMessage().indexOf("locked")>-1){
				JOptionPane.showMessageDialog(null, "多个客户端连接数据库，请检查运行的程序");
				return null;
			}
			e.printStackTrace();
		} 
		return user;
	}
	/* 
	 * inter:用户自己修改信息，根据用户的ID删除用户，当数据库操作数=1时返回值true，否则事务回滚返回false
	 * time：2018/03/15
	 */
	public static boolean updateAns(String count,int index,String answer,String name,String faculty){
		try {
			stmt = connect.createStatement();
			String sql = "update user set u_problem="+index+",u_answer='"+answer+"',u_name='"+name+"',faculty='"+faculty+"' where u_count = '"+count+"';";
			int res = stmt.executeUpdate(sql);
			stmt.close(); 
			if(res==1){
				connect.commit();
			    return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	/* 
	 * inter:管理员修改用户信息。根据用户的ID删除用户，当数据库操作数=1时返回值true，否则事务回滚返回false
	 * time：2018/03/15
	 */
	public static boolean updateUser(String Name,String count,int userRole,int index,String faculty,int sNo) throws SQLException{
		try{
			stmt = connect.createStatement();
			String sql = "update user set u_problem="+index+",u_name='"+Name+"',faculty='"+faculty+"',u_count = '"+count+"',u_role='"+userRole+"' where u_id="+sNo+";";
			System.out.println(sql);
			int number = stmt.executeUpdate(sql);
			stmt.close();
			if(number==1){
				connect.commit();
				return true;	
			}
			connect.rollback();
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	/* 
	 * inter:根据用户的ID删除用户，当数据库操作数=1时返回值true，否则事务回滚返回false
	 * time：2018/03/15
	 */
	public static boolean insterUser(String Name,String userName,String passWord,int userProblem,String userAnswer,int userRole,String userFaculty){
		try{
			stmt = connect.createStatement();
			String sql = "INSERT INTO user "
					+ "(u_name,u_count,u_pwd,u_problem,u_answer,u_role,Faculty,u_sign,u_use) "
					+ "VALUES "
					+ "('"+Name+"','"+userName+"','"+Helper.EncoderByMd5(passWord)+"',"+userProblem+",'"+userAnswer+"',"+userRole+",'"+userFaculty+"',"+new Date().getTime()/1000+",0)";
			int number = stmt.executeUpdate(sql);
			stmt.close();
			if(number==1){
				connect.commit();
				return true;
			}
			connect.rollback();
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	/* 
	 * inter:根据用户的ID删除用户，当数据库操作数=1时返回值true，否则事务回滚返回false
	 * time：2018/03/15
	 */
	public static boolean deleteUser(int id){
		try {
			stmt = connect.createStatement();
			String sql = "delete from user where u_id="+id;
			int number= stmt.executeUpdate(sql);
			stmt.close();
			if(number==1){
				connect.commit();
				return true;
			}
			connect.rollback();
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return false;
	}
	
	public static Vector<Object[]> dao(String str) throws Exception{
		PreparedStatement stmt = connect.prepareStatement(str);
		ResultSet rs = stmt.executeQuery();
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
