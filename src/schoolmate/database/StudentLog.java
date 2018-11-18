package schoolmate.database;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import schoolmate.control.Helper;
import schoolmate.model.DBConnect;
import schoolmate.model.Education;
import schoolmate.model.Student;
import schoolmate.model.Work;

public class StudentLog {
	private static Connection connect=DBConnect.getConnection();
	private static Statement stmt = null;
	
	/* 
	 * inter：判断是否有这个人信息，存在不导入信息
	 *     根据 姓名、学院、毕业年份、学历、工作地点、工作职称。count=1 得到学生ID，返回count（推测为同一个人的人数）
	 * time:2018/03/15
	 */
	public static int uniqueStu(Student stu) throws SQLException{
		int count = 0;
		String condition = "";
		if(!stu.s_graduate.equals(""))
			condition = " and s_graduate='"+stu.s_graduate+"'";
		if(!stu.s_enter.equals(""))
			condition += " and s_enter='"+stu.s_enter+"'";
		String str = "select count(s.s_id) as totle from student s join education e on s.s_id=e.s_id where s_name='"+stu.s_name+"' and s_faculty='"+stu.s_faculty+"' and s_major='"+stu.s_major+"' and s_workspace='"+stu.s_workspace+"' and s_worktitle='"+stu.s_worktitle+"' "+condition+" and s_education='"+stu.s_education+"';";
		ResultSet res = DBConnect.getStmt().executeQuery(str);
		while (res.next()) {
			count = res.getInt("totle");
		}
		return count;
	}
	/* 
	 * inter：根据 姓名、性别、学历不同（不可以为空）、毕业年份关系->判断同一个人的不同学历
	 * time:2018/03/15
	 */
	public static int uniqueStuDegree(Student stu) throws SQLException{
		int count = 0;
		String condition = "";
		if(!stu.s_graduate.equals("")) {	//毕业年份 >= 当前-4 <=当前-2 
			int graduate = Integer.parseInt(stu.s_graduate);
			condition = " and ((s_graduate>='"+(graduate-4)+"' and s_graduate<='"+(graduate-2)+"')"
					+" or (s_graduate<='"+(graduate+4)+"' and s_graduate>='"+(graduate+2)+"'))";
		}else if(stu.s_graduate.equals("")&&!stu.s_enter.equals("")) {
			int enter = Integer.parseInt(stu.s_enter);
			condition = " and ((s_enter>='"+(enter-4)+"' and s_enter<='"+(enter-2)+"')"
					+" or (s_enter<='"+(enter+4)+"' and s_enter>='"+(enter+2)+"'))";
		}else
			return count;	//如果没有年份，就直接导入
		String str = "select count(s.s_id) as totle from student s join education e on s.s_id=e.s_id where s_name='"+stu.s_name+"' and s_sex='"+stu.s_sex+"' and s_education!='"+stu.s_education+"'and s_education!=''"+condition;
		ResultSet res = DBConnect.getStmt().executeQuery(str);
		while (res.next()) {
			count = res.getInt("totle");
		}
		return count;
	}
	
	/*
	 * 得到数据库中的校友信息总数
	 */
	public static int getDataCount() {
		stmt = DBConnect.getStmt();
		int count = 0;
		ResultSet res;
		try {
			res = stmt.executeQuery("select count(s_id) totle from student");
			while (res.next()) {
				count = res.getInt("totle");
			}
			stmt.close();
		} catch (SQLException e) {}
		return count;
	}
	
	/*
	 * inter: 判断学生的工作信息是否需要更新(姓名、性别、学院、专业、毕业年份、学历) 工作地点、职称不太相同
	 */
	public static int uniqueStuWork(Student stu) throws SQLException{
		stmt = DBConnect.getStmt();
		int count = 0;
		String condition = "";
		if(!stu.s_graduate.equals(""))
			condition = " and s_graduate='"+stu.s_graduate+"'";
		if(!stu.s_enter.equals(""))
			condition += " and s_enter='"+stu.s_enter+"'";
		String str = "select count(s.s_id) as totle,s.s_id from student s join education e on s.s_id=e.s_id where s_name='"+stu.s_name+"' and s_faculty='"+stu.s_faculty+"' and s_major='"+stu.s_major+"' and (s_workspace!='"+stu.s_workspace+"' or s_worktitle!='"+stu.s_worktitle+"') "+condition+" and s_education='"+stu.s_education+"';";
		ResultSet res = stmt.executeQuery(str);
		while (res.next()) {
			count = res.getInt("totle");
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
			boolean majorRes = MajorLog.searchMajor(faculty,major);
			if(!majorRes){
				MajorLog.insertMajor(major,faculty,stmt);
			}
		}
		if(!nation.equals("")){
			boolean nationRes = AddressLog.searchNation(nation);
			if(!nationRes){
				AddressLog.insertNation(nation);
			}
		}
		if(!province.equals("")){
			boolean provinceRes = AddressLog.searchProvince(province,nation);
			if(!provinceRes){
				AddressLog.insertProvince(province,nation);
			}
		}
		if(!city.equals("")){
			boolean cityRes = AddressLog.searchCity(city,province,nation);
			if(!cityRes){
				AddressLog.insertCity(city,province,nation);
			}
		}
	}
	/* 
	 * inter：判断输入的信息格式是否正确，并插入该信息。学生姓名不为空就可以插入。
	 * time:2018/03/15
	 */
	public static void insertStudent(Student stu) throws SQLException{
		stmt = DBConnect.getStmt();
		String sql = "INSERT INTO student "
				+ "(s_name,s_sex,s_birth,s_person,s_hometown,"
				+ "s_homephone,s_phone,s_tphone,s_address,s_postcode,"
				+ "s_email,s_qq,s_remark1,s_weixin,s_remark2,"
				+ "s_remark3,s_remark4,s_remark5,update_time) "
				+ "VALUES "
				+ "('"+stu.s_name+"','"+stu.s_sex+"','"+stu.s_birth+"','"+stu.s_person+"','"+stu.s_hometown+"','"
				+stu.s_homephone+"','"+stu.s_phone+"','"
				+stu.s_tphone+"','"+stu.s_address+"','"+stu.s_postcode+"','"+stu.s_email+"','"+stu.s_qq+"','"+stu.s_remark1+"','"
				+stu.s_weixin+"','"+stu.s_remark2+"','"+stu.s_remark3+"'"+ ",'"+stu.s_remark4+"','"+stu.s_remark5+"',"+Helper.dataTime(null)+");";
		stmt.executeUpdate(sql);
		int id = 0;
		ResultSet res = stmt.executeQuery("select max(s_id) as id from student;");
		while (res.next()) {
			id = res.getInt("id");
			stu.s_id = id;
		}
		Education edu = new Education(stu.s_id,"","","","","","","");
		EducationLog.insertEdu(stmt,edu,false);
		Work work = new Work("", "", "", "", "", "", "");
		work.s_id = id;
		WorkLog.insertWork(work, stmt);
		String log = stu.s_city+" "+stu.s_name+" "+stu.s_phone+" "+stu.s_hometown+" "+stu.s_address+" "+stu.s_tphone+" "+stu.s_weixin+" "+stu.s_qq+" "+stu.s_email+" & "+" & ";
		FullsearchLog.insertFullsearch(log, id, stmt);	//检索表
		connect.commit();
		stmt.close();
	}
	
	public static int checkEducation(Student stu) {
		int count = 0;
		int degCount = 0;
		int workCount = 0;
		try {
			count = uniqueStu(stu);
			degCount = uniqueStuDegree(stu);
			workCount = uniqueStuWork(stu);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(count>0) {
			return 2;	//数据库中存在该校友信息，跳过导入
		}else {
			if(workCount>0&&degCount>0) {
				return 5;	//学历信息和工作信息都需要更新
			}else if(workCount>0) {
				return 4;	//工作信息需要更新
			}else if(degCount>0) {
				return 1;	//校友学历信息可能需要更新
			}else {
				return 3;	//正常导入
			}
		}
	}
	/* 导入学生的学历信息（学历信息、工作信息、检索表）。
	 * inter：Excel表导入学生记录，
	 * time:2018/03/15
	 */
	public static void importExl(Statement stmt,Student stu) throws SQLException{
		try{
			String sql = "INSERT INTO student "
					+ "(s_name,s_sex,s_birth,s_person,s_hometown,s_province,"
					+ "s_city,s_workspace,s_worktitle,s_work,s_workphone,s_homephone,s_phone,"
					+ "s_tphone,s_address,s_postcode,s_email,s_qq,s_remark1,s_nation,"
					+ "s_weixin,s_remark2,s_remark3,s_remark4,s_remark5,update_time) "
					+ "VALUES "
					+ "('"+stu.s_name+"','"+stu.s_sex+"','"+stu.s_birth+"','"+stu.s_person+"','"+stu.s_hometown+"','"+stu.s_province+"','"
					+stu.s_city+"','"+stu.s_workspace+"','"+stu.s_worktitle+"','"+stu.s_work+"','"+stu.s_workphone+"','"+stu.s_homephone+"','"+stu.s_phone+"','"
					+stu.s_tphone+"','"+stu.s_address+"','"+stu.s_postcode+"','"+stu.s_email+"','"+stu.s_qq+"','"+stu.s_remark1+"','"+stu.s_nation+"','"
					+stu.s_weixin+"','"+stu.s_remark2+"','"+stu.s_remark3+"'"+ ",'"+stu.s_remark4+"','"+stu.s_remark5+"',"+Helper.dataTime(null)+");";
			stmt.executeUpdate(sql);
			int id = 0;
			ResultSet res = stmt.executeQuery("select max(s_id) as id from student;");
			while (res.next()) {
				id = res.getInt("id");
				stu.s_id = id;
			}
			insertOption(stu.s_faculty, stu.s_major,stu.s_nation, stu.s_province, stu.s_city,stmt);
			Education edu = new Education(stu.s_id,stu.s_no,stu.s_education,stu.s_faculty,stu.s_major,stu.s_class,stu.s_enter,stu.s_graduate);
			EducationLog.insertEdu(stmt,edu,false);
			Work work = new Work(stu.s_nation, stu.s_province, stu.s_city, stu.s_work, stu.s_worktitle, stu.s_workspace, stu.s_workphone);
			work.s_id = id;
			WorkLog.insertWork(work, stmt);	//学生工作记录
			String log = stu.s_city+" "+stu.s_name+" "+stu.s_phone+" "+stu.s_hometown+" "+stu.s_address+" "+stu.s_tphone+" "+stu.s_weixin+" "+stu.s_qq+" "+stu.s_email+" & "
					+stu.s_work+" "+stu.s_worktitle+" "+stu.s_workspace+" & "+stu.s_class+" "+stu.s_no;
			FullsearchLog.insertFullsearch(log, id, stmt);	//检索表
		}catch(Exception e){
			e.getMessage();
		}
	}
	/* 对学生基本信息的更新操作，更新基本信息，并更新索引表（排除学历信息和工作信息）
	 * time:2018/03/15
	 */
	public static String updateStudent(Student stu,int sId) throws SQLException{
		String result = stu.judgeStudent();
		try{
			if(result==null){
				stmt = DBConnect.getStmt();
				//每行5个
				String sql = "update student set s_name='"+stu.s_name+"',s_sex='"+stu.s_sex+"',s_birth='"+stu.s_birth+"',s_person='"+stu.s_person
						+"',s_hometown='"+stu.s_hometown+"',s_homephone='"+stu.s_homephone+"',s_phone='"+stu.s_phone+"',s_tphone='"+stu.s_tphone
						+"',s_address='"+stu.s_address+"',s_postcode='"+stu.s_postcode+"',s_email='"+stu.s_email+"',s_qq='"+stu.s_qq+"',s_remark1='"+stu.s_remark1
						+"',s_weixin='"+stu.s_weixin+"',s_remark2='"+stu.s_remark2+"',s_remark3='"+stu.s_remark3+"',s_remark4='"+stu.s_remark4+"',s_remark5='"+stu.s_remark5
						+"' where s_id='"+sId+"';";
				int count = stmt.executeUpdate(sql);
				if(count==1){
					String log = stu.s_name+" "+stu.s_phone+" "+stu.s_hometown+" "+stu.s_address+" "+stu.s_tphone+" "+stu.s_weixin+" "+stu.s_qq+" "+stu.s_email;
					String str = FullsearchLog.getLog(sId, 0, log, stmt);
					FullsearchLog.updateFullsearch(str,sId,stmt);
					connect.commit();
				}else{
					stmt.close();
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
		return null;
	}
	/* 在一个事务下刷新学生的修改日期
	 * time:2018/03/15
	 */
	public static void updateTime(int id,Statement stmt,long time) throws SQLException{
		String sql = "update student set update_time="+time+" where s_id="+id;
		stmt.executeUpdate(sql);
	}
	/* 得到所有的备注字段
	 * time:2018/03/15
	 */
	public static String[] SelectRemarks(){
		String[] str = new String[5];
		try {
			stmt = DBConnect.getStmt();
			String sql = "select r_title from remark";
			ResultSet res;
			res = stmt.executeQuery(sql);
			int i = 0;
			while (res.next()) {
				str[i] = res.getString("r_title");
				i++;
			}
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return str;
	}
	/* 更新的备注字段
	 * time:2018/03/15
	 */
	public static boolean updateRemarks(String str,int i) throws SQLException{
		stmt = DBConnect.getStmt();
		String sql = "update remark set r_title = '"+str+"' where  r_id ="+(i+1);
		int res = stmt.executeUpdate(sql);
		if(res==1){
			connect.commit();
			stmt.close();
			return true;
		}else {
			stmt.close();
			return false;
		}
	}
	/* 根据学生的id字段，删除学生记录并删除教育、工作、全文检索记录
	 * time:2018/03/15
	 */
	public static boolean deleteStudent(int sId){
		try {
			stmt = DBConnect.getStmt();
			String sql = "delete from student where s_id="+sId;
			stmt.executeUpdate(sql);
			EducationLog.deleteEduFS(stmt, sId);
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
			stmt = DBConnect.getStmt();
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
	
	//返回table数组
	public static Vector<Object[]> dao(String str) throws Exception{
		System.out.println(str);
		Set<Object> idSet = new HashSet<Object>();	//使用 set 集合排除相同 id 的数据
		stmt = DBConnect.getStmt();
		ResultSet res = stmt.executeQuery(str);
		ResultSetMetaData rsmd=res.getMetaData();//用于获取关于 ResultSet 对象中列的类型和属性信息的对象
		int colNum=rsmd.getColumnCount();	//得到列数
		Vector<Object[]> info = new Vector<Object[]>();
		int i=0;
		while (res.next()){
			if(idSet.add(res.getObject(2))) {
				info.add((new Object[colNum+1]));
				info.elementAt(i)[0] = false;
				if(res!=null){
					for (int j=1;j<=colNum;j++){
						try {
							info.elementAt(i)[j]=res.getObject(j);
						} catch (NullPointerException e) {
							e.printStackTrace();
						}
					}
					i++;
				}
			}
		}
		idSet.clear();
		stmt.close();
		return info;
	}
	
	/*
	 * inter:学生添加工作时，更新学生的当前工作信息 + 并更新索引表
	 * time：2018/03/15
	 */
	public static void updateWork(Work work,Statement stmt) throws SQLException{
		boolean change = false;
		if(stmt==null){
			change = true;
			stmt = DBConnect.getStmt();
		}
		String sql = "update student set s_workspace='"+work.s_workspace+"',s_work='"+work.s_work+"',s_worktitle='"+work.s_worktitle+"',s_province='"+work.province+"',s_city='"
		+work.city+"',s_nation='"+work.nation+"',s_workphone='"+work.s_workphone+"',update_time="+Helper.dataTime(null)+" where s_id="+work.s_id;
		stmt.executeUpdate(sql);
		//所有修改工作记录都要更新索引表
		String log = work.s_work+" "+work.s_worktitle+" "+work.s_workspace;
		String str = FullsearchLog.getLog(work.s_id, 1, log, stmt);
		FullsearchLog.updateFullsearch(str,work.s_id,stmt);
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
		String sql = "select s_name,s_sex,s_birth,s_person,s_hometown,e.s_faculty,e.s_major,"
			+ "e.s_class,e.s_education,e.s_enter,e.s_graduate,w.nation,w.province,w.city,w.s_workspace,w.s_worktitle,"
			+ "w.s_work,s_homephone,s_phone,s_tphone,s_address,s_postcode,s_email,s_qq,s_weixin,s_remark1,"
			+ "s_remark2,s_remark3,s_remark4,s_remark5 from"+" (select * from student where update_time>="+time+" and update_time<="+eTime+") s join ("
			+ "select * from education  where update_time>="+time+" and update_time<="+eTime+option+") e on s.s_id=e.s_id left join "
			+ "(select * from worklog where update_time>="+time+" and update_time<="+eTime+") w on s.s_id=w.s_id order by s.update_time asc;";
		stmt = DBConnect.getStmt();
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
			ResultSet res = stmt.executeQuery(sql);
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
			ResultSet res = stmt.executeQuery(sql);
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
