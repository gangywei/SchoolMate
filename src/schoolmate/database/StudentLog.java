package schoolmate.database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import schoolmate.control.Helper;
import schoolmate.model.DBConnect;
import schoolmate.model.Education;
import schoolmate.model.Student;
import schoolmate.model.User;
import schoolmate.model.Work;
import schoolmate.view.PencilMain;

public class StudentLog {
	private static Connection connect=DBConnect.getConnection();
	private static Statement stmt = null;
	private static ResultSet res;
	private static PreparedStatement pStat;
	
	/* 
	 * inter：判断是否有这个人的学历信息，根据 姓名、学院、毕业年份、学历判断。count=1 得到学生ID，返回count（推测为同一个人的人数）
	 * time:2018/03/15
	 */
	public static int uniqueStu(Student stu,Statement stmt) throws SQLException{
		int count = 0;
		String s_phone = stu.s_phone;
		if(s_phone.equals(""))
			s_phone = "*****";
		String str = "select count(s.s_id) as totle,s.s_id from student s join education e on s.s_id=e.s_id where s_name='"+stu.s_name+"' and s_faculty='"+stu.s_faculty+"' and s_graduate='"+stu.s_graduate+"' and s_education='"+stu.s_education+"';";
		res = stmt.executeQuery(str);
		while (res.next()) {
			count = res.getInt("totle");
			if(count==1)
				stu.s_id = res.getInt("s_id");
			else
				break;
		}
		return count;
	}
	/* 
	 * inter：判断同一个人的不同学历，根据 姓名、学历不同、毕业年份大于数据库6年以内。count=1 得到学生ID，返回count（推测为同一个人学历不同的情况）
	 * time:2018/03/15
	 */
	public static int uniqueStuDegree(Student stu,Statement stmt) throws SQLException{
		int count = 0;
		String s_phone = stu.s_phone;
		if(s_phone.equals(""))
			s_phone = "*****";
		String str = "select count(s.s_id) as totle,s.s_id from student s join education e on s.s_id=e.s_id where s_name='"+stu.s_name+"' and s_faculty!='"+stu.s_faculty+"' and (s_graduate<'"+(Integer.parseInt(stu.s_graduate)-4)+"' or s_graduate>'"+(Integer.parseInt(stu.s_graduate)+4)+"');";
		res = stmt.executeQuery(str);
		while (res.next()) {
			count = res.getInt("totle");
			if(count==1)
				stu.s_id = res.getInt("s_id");
			else
				break;
		}
		return count;
	}
	
	/* 
	 * inter：插入学生记录时，判断数据库是否存在学生的基本信息，国家、省份、市区、学院、专业。不存在在同一个事务下插入基本记录。
	 * time:2018/03/15
	 */
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
				AddressLog.insertNation(nation,stmt);
			}
		}
		if(!province.equals("")){
			boolean provinceRes = AddressLog.searchProvince(province,nation);
			if(!provinceRes){
				AddressLog.insertProvince(province,nation,stmt);
			}
		}
		if(!city.equals("")){
			boolean cityRes = AddressLog.searchCity(city,province,nation);
			if(!cityRes){
				AddressLog.insertCity(city,province,nation,stmt);
			}
		}
	}
	/* 
	 * inter：判断是否存在该账号的用户。 count=该账号的人数
	 * time:2018/03/15
	 */
	public static int searchSno(String sno,Statement stmt) throws SQLException{
		if(stmt==null){
			stmt = connect.createStatement();
		}
		int count = 0;
		String sql = "select count(*) totle from student where s_no='"+sno+"'";	
		res = stmt.executeQuery(sql);
		while (res.next()) {
			count = res.getInt("totle");
		}
		return count;
	}
	/* 
	 * inter：判断输入的信息格式是否正确，并插入该信息。学生姓名不为空就可以插入。
	 * time:2018/03/15
	 */
	public static String insertStudent(Student stu) throws SQLException{
		String result = stu.judgeStudent();
		try{
			if(result==null){
				stmt = connect.createStatement();
				String sql = "INSERT INTO student "
						+ "(s_no,s_name,s_sex,s_birth,s_person,s_hometown,s_province,"
						+ "s_city,s_workspace,s_worktitle,s_work,s_workphone,s_homephone,s_phone,"
						+ "s_tphone,s_address,s_postcode,s_email,s_qq,s_remark1,s_nation,"
						+ "s_weixin,s_remark2,s_remark3,s_remark4,s_remark5,update_time) "
						+ "VALUES "
						+ "('"+stu.s_no+"','"+stu.s_name+"','"+stu.s_sex+"','"+stu.s_birth+"','"+stu.s_person+"','"+stu.s_hometown+"','"+stu.s_province+"','"
						+stu.s_city+"','"+stu.s_workspace+"','"+stu.s_worktitle+"','"+stu.s_work+"','"+stu.s_workphone+"','"+stu.s_homephone+"','"+stu.s_phone+"','"
						+stu.s_tphone+"','"+stu.s_address+"','"+stu.s_postcode+"','"+stu.s_email+"','"+stu.s_qq+"','"+stu.s_remark1+"','"+stu.s_nation+"','"
						+stu.s_weixin+"','"+stu.s_remark2+"','"+stu.s_remark3+"'"+ ",'"+stu.s_remark4+"','"+stu.s_remark5+"',"+Helper.dataTime(null)+");";
				stmt.executeUpdate(sql);
				int id = 0;
				res = stmt.executeQuery("select max(s_id) as id from student;");
				while (res.next()) {
					id = res.getInt("id");
					stu.s_id = id;
				}
				insertOption(stu.s_faculty, stu.s_major,stu.s_nation, stu.s_province, stu.s_city,stmt);
				boolean res = false;
				Education edu = new Education(stu.s_id,stu.s_no,stu.s_education,stu.s_faculty,stu.s_major,stu.s_class,stu.s_enter,stu.s_graduate);
				res = EducationLog.insertEdu(stmt,edu);
				if(!res){
					throw new Exception("【推测学历重复】存在该校友的学历信息，请检查信息是否重复");
				}
				String log = stu.s_no+" "+stu.s_name+" "+stu.s_workspace+" "+stu.s_work+" "+stu.s_worktitle+" "+stu.s_phone+" "+stu.s_tphone+" "+stu.s_address+" "+stu.s_email+" "+stu.s_qq;
				FullsearchLog.insertFullsearch(log, id, stmt);	//检索表
				WorkLog.insertWork(stu, stmt);	//学生工作记录
				connect.commit();
			}else{
				throw new Exception(result);
			}
		}catch(Exception e){
			System.out.println(e.getMessage());
			connect.rollback();
			return e.getMessage();
		}
		stmt.close();
		return null;
	}
	/* 0 正常导入，推测为一个人的检查，推测存在这个人并且学历信息需要添加学历信息。1 跳过推测为一个人的检查， 直接导入学生数据。
	 * inter：Excel表导入学生记录，
	 * time:2018/03/15
	 */
	public static String importExl(Statement stmt,Student stu,int type) throws SQLException{
		String result = stu.judgeStudent();
		try{
			if(result==null){
				int count = 0;
				int degCount = 0;
				if(type==0){
					count = uniqueStu(stu,stmt);
					degCount = uniqueStuDegree(stu,stmt);
					if(count>0)	//出现count>0，就会导入到符合信息的教育记录。
						throw new Exception("【推测学历重复】存在该校友的学历信息，请检查信息是否重复");
					if(degCount>0)	//出现count>0，就会导入到符合信息的教育记录。
						throw new Exception("【推测数据更新】存在该校友的学历信息，请判断是否需要更新");
				}
				if(degCount==0&&count==0){	//导入学生记录+学历信息。
					String sql = "INSERT INTO student "
							+ "(s_no,s_name,s_sex,s_birth,s_person,s_hometown,s_province,"
							+ "s_city,s_workspace,s_worktitle,s_work,s_workphone,s_homephone,s_phone,"
							+ "s_tphone,s_address,s_postcode,s_email,s_qq,s_remark1,s_nation,"
							+ "s_weixin,s_remark2,s_remark3,s_remark4,s_remark5,update_time) "
							+ "VALUES "
							+ "('"+stu.s_no+"','"+stu.s_name+"','"+stu.s_sex+"','"+stu.s_birth+"','"+stu.s_person+"','"+stu.s_hometown+"','"+stu.s_province+"','"
							+stu.s_city+"','"+stu.s_workspace+"','"+stu.s_worktitle+"','"+stu.s_work+"','"+stu.s_workphone+"','"+stu.s_homephone+"','"+stu.s_phone+"','"
							+stu.s_tphone+"','"+stu.s_address+"','"+stu.s_postcode+"','"+stu.s_email+"','"+stu.s_qq+"','"+stu.s_remark1+"','"+stu.s_nation+"','"
							+stu.s_weixin+"','"+stu.s_remark2+"','"+stu.s_remark3+"'"+ ",'"+stu.s_remark4+"','"+stu.s_remark5+"',"+Helper.dataTime(null)+");";
					stmt.executeUpdate(sql);
					int id = 0;
					res = stmt.executeQuery("select max(s_id) as id from student;");
					while (res.next()) {
						id = res.getInt("id");
						stu.s_id = id;
					}
					
					insertOption(stu.s_faculty, stu.s_major,stu.s_nation, stu.s_province, stu.s_city,stmt);
					if(!stu.s_education.equals("")){
						boolean res = false;
						Education edu = new Education(stu.s_id,stu.s_no,stu.s_education,stu.s_faculty,stu.s_major,stu.s_class,stu.s_enter,stu.s_graduate);
						res = EducationLog.insertEdu(stmt,edu);
						if(!res){
							throw new Exception("【推测学历重复】存在该校友的学历信息，请检查信息是否重复");
						}
					}else
						throw new Exception("学历信息为空，请补充学历信息");
					
					String log = stu.s_no+" "+stu.s_name+" "+stu.s_workspace+" "+stu.s_work+" "+stu.s_worktitle+" "+stu.s_phone+" "+stu.s_tphone+" "+stu.s_address+" "+stu.s_email+" "+stu.s_qq;
					FullsearchLog.insertFullsearch(log, id, stmt);	//检索表
					WorkLog.insertWork(stu, stmt);	//学生工作记录
				}	
			}else{
				throw new Exception(result);
			}
		}catch(Exception e){
			System.out.println(e.getMessage());
			return e.getMessage();
		}
		return null;
	}
	/* 对学生基本信息的更新操作，更新基本信息，并更新索引表（排除学历信息和工作信息）
	 * time:2018/03/15
	 */
	public static String updateStudent(Student stu,int sId) throws SQLException{
		String result = stu.judgeStudent();
		try{
			if(result==null){
				stmt = connect.createStatement();
				//每行5个
				String sql = "update student set s_no='"+stu.s_no+"',s_name='"+stu.s_name+"',s_sex='"+stu.s_sex+"',s_birth='"+stu.s_birth+"',s_person='"+stu.s_person
						+"',s_hometown='"+stu.s_hometown+"',s_workphone='"+stu.s_workphone+"',s_homephone='"+stu.s_homephone+"',s_phone='"+stu.s_phone+"',s_tphone='"+stu.s_tphone
						+"',s_address='"+stu.s_address+"',s_postcode='"+stu.s_postcode+"',s_email='"+stu.s_email+"',s_qq='"+stu.s_qq+"',s_remark1='"+stu.s_remark1
						+"',s_weixin='"+stu.s_weixin+"',s_remark2='"+stu.s_remark2+"',s_remark3='"+stu.s_remark3+"',s_remark4='"+stu.s_remark4+"',s_remark5='"+stu.s_remark5
						+"' where s_id='"+sId+"';";
				int count = stmt.executeUpdate(sql);
				if(count==1){
					insertOption(stu.s_faculty, stu.s_major,stu.s_nation, stu.s_province, stu.s_city,stmt);
					String log = stu.s_no+" "+stu.s_name+" "+stu.s_workspace+" "+stu.s_phone+" "+stu.s_tphone+" "+stu.s_address+" "+stu.s_weixin+" "+stu.s_qq+" "
							+stu.s_work+" "+stu.s_worktitle;
					FullsearchLog.updateFullsearch(log,sId,stmt);
					connect.commit();
				}else{
					throw new Exception("修改用户执行失败");
				}
			}else{
				throw new Exception(result);
			}
		}catch(Exception e){
			if(connect!=null){
				result = e.getMessage();
				connect.rollback();
				return result;
			}
		}
		stmt.close();
		return null;
	}
	/* 在一个事务下刷新学生的修改日期
	 * time:2018/03/15
	 */
	public static void updateTime(int id,Statement stmt,long time) throws SQLException{
		String sql = "update student set update_time="+time+" where s_id="+id;
		stmt.executeUpdate(sql);
	}
	/* 在一个事务下刷新学生的修改日期
	 * time:2018/03/15
	 */
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
	/* 得到所有的备注字段
	 * time:2018/03/15
	 */
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
	/* 更新的备注字段
	 * time:2018/03/15
	 */
	public static boolean updateRemarks(String str,int i) throws SQLException{
		stmt = connect.createStatement();
		String sql = "update remark set r_title = '"+str+"' where  r_id ="+(i+1);
		int res = stmt.executeUpdate(sql);
		if(res==1){
			connect.commit();
			stmt.close();
			return true;
		}
		return false;
	}
	/* 根据学生的id字段，删除学生记录并删除教育、工作、全文检索记录
	 * time:2018/03/15
	 */
	public static boolean deleteStudent(int sId){
		try {
			stmt = connect.createStatement();
			String sql = "delete from student where s_id="+sId;
			stmt.executeUpdate(sql);
			EducationLog.deleteEdu(stmt, sId);
			FullsearchLog.deleteFullsearch(sId,stmt);
			WorkLog.deleteWork(sId, stmt);
			connect.commit();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	/*
	 * inter:使用in查询，删除多条数据。删除教育、工作、全文检索记录
	 * time：2018/03/15
	 */
	public static boolean deleteMany(String str){
		try {
			stmt = connect.createStatement();
			String sql = "delete from student where s_id in ("+str+");";
			stmt.executeUpdate(sql);
			EducationLog.deleteManyEdu(stmt, str);
			FullsearchLog.deleteManyFs(str,stmt);
			WorkLog.deleteManyWk(str, stmt);
			connect.commit();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	/*
	 * inter:使用in查询，在一个事务下删除多条数据。删除教育、工作、全文检索记录
	 * time：2018/03/15
	 */
	public static void deleteMany(String str,Statement stmt){
		try {
			String sql = "delete from student where s_id in ("+str+");";
			stmt.executeUpdate(sql);
			EducationLog.deleteManyEdu(stmt, str);
			FullsearchLog.deleteManyFs(str,stmt);
			WorkLog.deleteManyWk(str, stmt);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/*
	 * inter:得到所有的职称分类
	 * time：2018/03/15
	 */
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
	
	/*
	 * inter:根据用户ID，得到用户的全文检索数据。一般用于学生全文检索数据的更新
	 * time：2018/03/15
	 */
	public static String getStudentLog(int sId,Statement stmt) throws SQLException{
		String sql = "select s_no,s_name,s_phone,s_tphone,s_address,s_qq,s_weixin from student where s_id = "+sId;
		res = stmt.executeQuery(sql);
		String data = "";
		while (res.next()) {
			data += res.getString("s_no")+" ";
			data += res.getString("s_name")+" ";
			data += res.getString("s_phone")+" ";
			data += res.getString("s_tphone")+" ";
			data += res.getString("s_address")+" ";
			data += res.getString("s_qq")+" ";
			data += res.getString("s_weixin")+" ";
		}
		return data;
	}
	
	/*
	 * inter:学生添加工作时，更新学生的当前工作信息。
	 * time：2018/03/15
	 */
	public static void updateWork(Work stu,Statement stmt) throws SQLException{
		boolean change = false;
		if(stmt==null){
			change = true;
			stmt = connect.createStatement();
		}
		String sql = "update student set s_workspace='"+stu.s_workspace+"',s_work='"+stu.s_work+"',s_worktitle='"+stu.s_worktitle+"',s_province='"+stu.province+"',s_city='"
		+stu.city+"',s_nation='"+stu.nation+"',update_time="+Helper.dataTime(null)+" where s_id="+stu.s_id;
		stmt.executeUpdate(sql);
		String log = getStudentLog(stu.s_id, stmt);
		log += stu.s_work+" "+stu.s_worktitle+" "+stu.s_workspace;
		FullsearchLog.updateFullsearch(log, stu.s_id, stmt);
		if(change){
			connect.commit();
			stmt.close();
		}
	}
	
	/*
	 * inter:各个办公室数据合并的方法，根据数据开始和结束时间得到更新的数据
	 * time：2018/03/15
	 */
	public static Vector<Object[]> getUpdate(long time,long eTime,String limit) throws SQLException{
		String option;
		if(limit.equals(""))
			option = "";
		else
			option = " and "+limit;
		String sql = "select s.s_no,s_name,s_sex,s_birth,s_person,s_hometown,e.s_faculty,e.s_major,"
			+ "e.s_class,e.s_education,e.s_enter,e.s_graduate,w.nation,w.province,w.city,w.s_workspace,w.s_worktitle,"
			+ "w.s_work,s_workphone,s_homephone,s_phone,s_tphone,s_address,s_postcode,s_email,s_qq,s_weixin,s_remark1,"
			+ "s_remark2,s_remark3,s_remark4,s_remark5 from"+" (select * from student where update_time>="+time+" and update_time<="+eTime+") s join ("
			+ "select * from education  where update_time>="+time+" and update_time<="+eTime+option+") e on s.s_id=e.s_id left join "
			+ "(select * from worklog where update_time>="+time+" and update_time<="+eTime+") w on s.s_id=w.s_id order by s.update_time asc;";
		stmt = connect.createStatement();
		System.out.println(sql);
		ResultSet rs = stmt.executeQuery(sql);
		ResultSetMetaData rsmd=rs.getMetaData();//用于获取关于 ResultSet 对象中列的类型和属性信息的对象
		int colNum=rsmd.getColumnCount();	//得到列数
		Vector<Object[]> info = new Vector<Object[]>();
		int i=0;
		while (rs.next()){
			info.add((new Object[colNum]));
			for (int j=0;j<colNum;j++){
				info.elementAt(i)[j]=(Object) rs.getObject(j+1);
			}
			i++;
		}
		return info;
	}
	/*
	 * inter:根据教育流程标签的删除条件的到待删除的数据ID集合，使用deleteMany方法删除多个学生。
	 * time：2018/03/15
	 */
	public static void delStuFromEdu(String str,Statement stmt){
		String strId = "";
		String sql = "select group_concat(distinct(s_id)) strId from education "+str+"';";	//得到所有的学生Id。删除工作记录，检索信息。
		try {
			res = stmt.executeQuery(sql);
			while (res.next()) {
				strId = res.getString("strId");
			}	
			deleteMany(strId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/*
	 * inter:根据工作地点标签的删除条件的到待删除的数据ID集合，使用deleteMany方法删除多个学生。
	 * time：2018/03/15
	 */
	public static void delStuFromAdd(String str,Statement stmt){
		String strId = "";
		String sql = "select group_concat(distinct(s_id)) strId from student "+str+"';";	//得到所有的学生Id。删除工作记录，检索信息。
		try {
			res = stmt.executeQuery(sql);
			while (res.next()) {
				strId = res.getString("strId");
			}	
			deleteMany(strId);
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
