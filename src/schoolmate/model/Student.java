package schoolmate.model;

import schoolmate.control.Helper;

public class Student {
	//学号	姓名	性别	出生日期	身份证号	籍贯	学院	专业	
	//所在班级	学历/学位	入学年份	毕业年份	工作国家	工作省份	工作市区	工作单位	
	//职务	职称	办公电话	宅电	手机1	手机2	通讯地址	邮编	
	//E-mail	QQ	微信	备注字段	备注字段	备注字段	备注字段	备注字段
	public Student(String name,String sex,String birth,String person,String hometown,String faculty,String major,
			String classs,String education,String enter,String graduate,String nation,String province,String city,String workspace,
			String work,String worktitle,String workphone,String homephone,String phone,String tphone,String address,String postcode,
			String email,String qq,String weixin,String remark1,String remark2,String remark3,String remark4,String remark5){
		s_name = name;
		s_sex = sex;
		s_birth = birth;
		s_person = person;
		s_hometown = hometown;
		s_faculty = faculty;
		s_major = major;
		s_class = classs;
		s_enter = enter;
		s_education = education;
		s_nation = nation;
		s_province = province;
		s_graduate = graduate;
		s_city = city;
		s_workspace = workspace;
		s_worktitle = worktitle;
		s_work = work;
		s_homephone = homephone;
		s_workphone = worktitle;
		s_phone = phone;
		s_tphone = tphone;
		s_address = address;
		s_postcode = postcode;
		s_email = email;
		s_qq = qq;
		s_weixin = weixin;
		s_remark1 = remark1;
		s_remark2 = remark2;
		s_remark3 = remark3;
		s_remark4 = remark4;
		s_remark5 = remark5;
	}
	//学生的基本信息
	public Student(String name,String sex,String birth,String person,String hometown,String homephone,String phone,String tphone,String address,String postcode,
			String email,String qq,String weixin,String remark1,String remark2,String remark3,String remark4,String remark5){
		s_name = name;
		s_sex = sex;
		s_birth = birth;
		s_person = person;
		s_hometown = hometown;
		s_homephone = homephone;
		s_phone = phone;
		s_tphone = tphone;
		s_address = address;
		s_postcode = postcode;
		s_email = email;
		s_qq = qq;
		s_weixin = weixin;
		s_remark1 = remark1;
		s_remark2 = remark2;
		s_remark3 = remark3;
		s_remark4 = remark4;
		s_remark5 = remark5;
	}
	
	public String judgeStudent(){
		if(s_name.equals(""))
			return "姓名不可以为空";
		String regPerson = "^[0-9]{17}[0-9Xx]{1}$";
		if(!s_person.equals(""))
			if(!Helper.matchRegular(s_person, regPerson))
				return "身份证号输入错误";
		String regPhone = "^1([358][0-9]|4[579]|66|7[0135678]|9[89])[0-9]{8}$";
		if(!s_phone.equals(""))
			if(!Helper.matchRegular(s_phone, regPhone))
				return "手机号输入错误";
		if(!s_tphone.equals(""))
			if(!Helper.matchRegular(s_tphone, regPhone))
				return "副手机号输入错误";
		String regEmail = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
		if(!s_email.equals(""))
			if(!Helper.matchRegular(s_email, regEmail))
				return "邮箱输入错误";
		return null;
	}
	
	public String[] toArray(){
		String temp[] = {"",s_name,s_sex,s_birth,s_person,s_hometown,s_faculty,s_major,s_class,
				s_education,s_enter,s_graduate,s_nation,s_province,s_city,s_workspace,s_work,s_worktitle,s_workphone,
				s_homephone,s_phone,s_tphone,s_address,s_postcode,s_email,s_qq,s_weixin,s_remark1,s_remark2,
				s_remark3,s_remark4,s_remark5,""};	//添加一个错误信息字段
		return temp;
	}
	public int s_id;
	public String s_no = "";
	public String s_name;
	public String s_sex;
	public String s_birth;
	public String s_person;
	public String s_hometown;
	public String s_faculty;
	public String s_major;
	public String s_class;
	public String s_enter;
	public String s_education;
	public String s_nation;
	public String s_province;
	public String s_graduate;
	public String s_city;
	public String s_workspace;
	public String s_worktitle;
	public String s_work;
	public String s_workphone;
	public String s_homephone;
	public String s_phone;
	public String s_tphone;
	public String s_address;
	public String s_postcode;
	public String s_email;
	public String s_qq;
	public String s_weixin;
	public String s_remark1;
	public String s_remark2;
	public String s_remark3;
	public String s_remark4;
	public String s_remark5;
}
