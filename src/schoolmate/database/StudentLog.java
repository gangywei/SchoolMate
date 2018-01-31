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
import schoolmate.model.User;
import schoolmate.view.PencilMain;

public class StudentLog {
	private static Connection connect=DBConnect.getConnection();
	private static Statement stmt = null;
	private static ResultSet res;
	
	//插入或者更新用户信息时，更新其他表格的数据(学院、系别、国家、省、市)
	public static void insertOption(String faculty,String major,String nation,String province,String city,Statement stmt) throws SQLException{
		if(!faculty.equals("")){
			boolean faclutyRes = FacultyLog.searchFaculty(faculty);
			if(!faclutyRes){
				FacultyLog.insertFaculty(faculty,null);
			}
		}
		if(!major.equals("")){
			boolean majorRes = MajorLog.searchMajor(major,faculty);
			if(!majorRes){
				MajorLog.insertMajor(major,faculty,stmt);
			}
		}
		if(!nation.equals("")){
			boolean nationRes = AddressLog.searchNation(nation);
			if(!nationRes){
				AddressLog.insertProvince(province,nation,stmt);
			}
		}
		if(!province.equals("")){
			boolean provinceRes = AddressLog.searchProvince(province,nation);
			if(!provinceRes){
				AddressLog.insertProvince(province,nation,stmt);
			}
		}
		if(!city.equals("")){
			boolean cityRes = AddressLog.searchCity(city,province);
			if(!cityRes){
				AddressLog.insertCity(city,province,stmt);
			}
		}
	}
	
	public static String insertStudent(Student stu) throws SQLException{
		String result = stu.judgeStudent();
		try{
			if(result==null){
				stmt = connect.createStatement();
				String sql = "INSERT INTO student "
						+ "(s_no,s_name,s_sex,s_birth,s_person,s_hometown,s_faculty,s_major,s_class,s_education,"
						+ "s_graduate,s_province,s_city,s_workspace,s_worktitle,s_work,s_workphone,s_homephone,s_phone,s_tphone,"
						+ "s_address,s_postcode,s_email,s_qq,s_remark1,s_enter,s_nation,s_weixin,s_remark2,s_remark3,"
						+ "s_remark4,s_remark5) "
						+ "VALUES "
						+ "("+stu.getNo()+",'"+stu.s_name+"','"+stu.s_sex+"','"+stu.s_birth+"','"+stu.s_person+"','"+stu.s_hometown+"','"+stu.s_faculty+"','"+stu.s_major+"','"+stu.s_class+"','"+stu.s_education+"','"
						+stu.s_graduate+"','"+stu.s_province+"','"+stu.s_city+"','"+stu.s_workspace+"','"+stu.s_worktitle+"','"+stu.s_work+"','"+stu.s_workphone+"','"+stu.s_homephone+"','"+stu.s_phone+"','"+stu.s_tphone+"','"
						+stu.s_address+"','"+stu.s_postcode+"','"+stu.s_email+"','"+stu.s_qq+"','"+stu.s_remark1+"','"+stu.s_enter+"','"+stu.s_nation+"','"+stu.s_weixin+"','"+stu.s_remark2+"','"+stu.s_remark3+"'"
						+ ",'"+stu.s_remark4+"','"+stu.s_remark5+"');";
				stmt.executeUpdate(sql);
				int id = 0;
				res = stmt.executeQuery("select max(s_id) as id from student;");
				while (res.next()) {
					id = res.getInt("id");
					stu.s_id = id;
				}
				insertOption(stu.s_faculty, stu.s_major,stu.s_nation, stu.s_province, stu.s_city,stmt);
				String log = stu.s_no+" "+stu.s_name+" "+stu.s_workspace+" "+stu.s_work+" "+stu.s_worktitle+" "+stu.s_phone+" "+stu.s_tphone+" "+stu.s_address+" "+stu.s_email+" "+stu.s_qq;
				FullsearchLog.insertFullsearch(log, id, stmt);	//检索表
				WorkLog.insertWork(stu, stmt);	//学生工作记录
				connect.commit();
			}else{
				throw new Exception(result);
			}
		}catch(Exception e){
			if(result==null){
				result = "存在该学号的学生";
			}
			System.out.println(e.getMessage());
			connect.rollback();
			return result;
		}
		stmt.close();
		return null;
	}
	/* 对学生信息的更新操作，
	 * Boolean true=>更新工作信息，false=>不更新工作信息。
	 */
	public static String updateStudent(Student stu,int sId,boolean type) throws SQLException{
		String result = stu.judgeStudent();
		try{
			if(result==null){
				stmt = connect.createStatement();
				if(type&&!stu.s_no.equals("")){
					stu.s_id = sId;
					WorkLog.insertWork(stu, stmt);
				}
				//每行5个
				String sql = "update student set s_no="+stu.getNo()+",s_name='"+stu.s_name+"',s_sex='"+stu.s_sex+"',s_birth='"+stu.s_birth+"',s_person='"+stu.s_person
						+"',s_hometown='"+stu.s_hometown+"',s_faculty='"+stu.s_faculty+"',s_major='"+stu.s_major+"',s_class='"+stu.s_class+"',s_education='"+stu.s_education
						+"',s_graduate='"+stu.s_graduate+"',s_province='"+stu.s_province+"',s_city='"+stu.s_city+"',s_workspace='"+stu.s_workspace+"',s_worktitle='"+stu.s_worktitle
						+"',s_work='"+stu.s_work+"',s_workphone='"+stu.s_workphone+"',s_homephone='"+stu.s_homephone+"',s_phone='"+stu.s_phone+"',s_tphone='"+stu.s_tphone
						+"',s_address='"+stu.s_address+"',s_postcode='"+stu.s_postcode+"',s_email='"+stu.s_email+"',s_qq='"+stu.s_qq+"',s_remark1='"+stu.s_remark1
						+"',s_enter='"+stu.s_enter+"',s_weixin='"+stu.s_weixin+"',s_nation='"+stu.s_nation+"',s_remark2='"+stu.s_remark2+"',s_remark3='"+stu.s_remark3
						+"',s_remark4='"+stu.s_remark4+"',s_remark5='"+stu.s_remark5
						+"' where s_id='"+sId+"';";
				System.out.println(sql);
				stmt.executeUpdate(sql);
				insertOption(stu.s_faculty, stu.s_major,stu.s_nation, stu.s_province, stu.s_city,stmt);
				String log = stu.s_no+" "+stu.s_name+" "+stu.s_workspace+" "+stu.s_work+" "+stu.s_worktitle+" "+stu.s_phone+" "+stu.s_tphone+" "+stu.s_address+" "+stu.s_email+" "+stu.s_qq;
				FullsearchLog.updateFullsearch(log, sId,stmt);
				connect.commit();
			}else{
				throw new Exception(result);
			}
		}catch(Exception e){
			if(connect!=null){
				result = e.getMessage();
				System.out.println(e.getMessage());
				connect.rollback();
				return result;
			}
		}
		stmt.close();
		return null;
	}
	//整个导入ELX过程是一个事务操作
	public static String importExl(Statement stmt,Student stu) throws SQLException{
		String result = stu.judgeStudent();
		try{
			if(result==null){
				String sql = "INSERT INTO student "
						+ "(s_no,s_name,s_sex,s_birth,s_person,s_hometown,s_faculty,s_major,s_class,s_education,"
						+ "s_graduate,s_province,s_city,s_workspace,s_worktitle,s_work,s_workphone,s_homephone,s_phone,s_tphone,"
						+ "s_address,s_postcode,s_email,s_qq,s_remark1,s_enter,s_nation,s_weixin,s_remark2,s_remark3,"
						+ "s_remark4,s_remark5) "
						+ "VALUES "
						+ "("+stu.getNo()+",'"+stu.s_name+"','"+stu.s_sex+"','"+stu.s_birth+"','"+stu.s_person+"','"+stu.s_hometown+"','"+stu.s_faculty+"','"+stu.s_major+"','"+stu.s_class+"','"+stu.s_education+"','"
						+stu.s_graduate+"','"+stu.s_province+"','"+stu.s_city+"','"+stu.s_workspace+"','"+stu.s_worktitle+"','"+stu.s_work+"','"+stu.s_workphone+"','"+stu.s_homephone+"','"+stu.s_phone+"','"+stu.s_tphone+"','"
						+stu.s_address+"','"+stu.s_postcode+"','"+stu.s_email+"','"+stu.s_qq+"','"+stu.s_remark1+"','"+stu.s_enter+"','"+stu.s_nation+"','"+stu.s_weixin+"','"+stu.s_remark2+"','"+stu.s_remark3+"'"
						+ ",'"+stu.s_remark4+"','"+stu.s_remark5+"');";
				System.out.println(sql);
				stmt.executeUpdate(sql);
				int id = 0;
				res = stmt.executeQuery("select max(s_id) as id from student;");
				while (res.next()) {
					id = res.getInt("id");
					stu.s_id = id;
				}
				insertOption(stu.s_faculty, stu.s_major,stu.s_nation, stu.s_province, stu.s_city,stmt);
				String log = stu.s_no+" "+stu.s_name+" "+stu.s_workspace+" "+stu.s_work+" "+stu.s_worktitle+" "+stu.s_phone+" "+stu.s_tphone+" "+stu.s_address+" "+stu.s_email+" "+stu.s_qq;
				FullsearchLog.insertFullsearch(log, id, stmt);
				WorkLog.insertWork(stu, stmt);
			}else{
				throw new Exception(result);
			}
		}catch(Exception e){
			result = e.getMessage();
			System.out.println(e.getMessage());
			connect.rollback();
			return result;
		}
		return null;
	}
	
	
	
	public static int getNumber(String sql) throws SQLException{
		int count = 0;
		stmt = connect.createStatement();
		if(sql==null)
			sql = "select count(*) totle from student";	
		res = stmt.executeQuery(sql);
		while (res.next()) {
			count = res.getInt("totle");
		}
		return count;
	}
	
	public static String[] SelectRemarks() throws SQLException{
		String[] str = new String[5];
		stmt = connect.createStatement();
		String sql = "select r_title from remark";
		res = stmt.executeQuery(sql);
		int i = 0;
		while (res.next()) {
			str[i] = res.getString("r_title");
			i++;
		}
		return str;
	}
	 
	public static void updateRemarks(String str,int i) throws SQLException{
		stmt = connect.createStatement();
		String sql = "update remark set r_title = '"+str+"' where  r_id ="+(i+1);
		System.out.println(sql);
		stmt.executeUpdate(sql);
		connect.commit();
		stmt.close();
		return;
	}
	
	public static boolean deleteStudent(int sId){
		try {
			stmt = connect.createStatement();
			String sql = "delete from student where s_id="+sId;
			System.out.println(sql);
			stmt.executeUpdate(sql);
			FullsearchLog.deleteFullsearch(sId,stmt);
			WorkLog.deleteWork(sId, stmt);
			connect.commit();
			stmt.close();
		} catch (SQLException e) {
			PencilMain.logger.error("删除学生信息抛错"+e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static boolean deleteMany(String str){
		try {
			stmt = connect.createStatement();
			String sql = "delete from student where s_id in ("+str+");";
			stmt.executeUpdate(sql);
			FullsearchLog.deleteManyFs(str,stmt);
			WorkLog.deleteManyWk(str, stmt);
			connect.commit();
			stmt.close();
			return true;
		} catch (SQLException e) {
			PencilMain.logger.error("多选删除学生信息抛错"+e.getMessage());
			e.printStackTrace();
		}
		return false;
	}
	
	//得到所有的职称分类
	public static String[] allWorkTitle(){
		int count = 0;
		try {
			stmt = connect.createStatement();
			String sql = "select count( distinct s_worktitle) as totle from student;";
			res = stmt.executeQuery(sql);
			while (res.next()) {
				count = res.getInt("totle");
			}
			String data[] = new String[count];
			sql = "select distinct s_worktitle from student;";
			res = stmt.executeQuery(sql);
			int i=0;
			while (res.next()) {
				data[i] = res.getString("s_worktitle");
				i++;
			}
			return data;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
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
			info.add((new Object[colNum+1]));
			info.elementAt(i)[0] = false;
			for (int j=1;j<=colNum;j++){
				info.elementAt(i)[j]=(Object) rs.getObject(j);
			}
			i++;
		}
		return info;
	}
}
