package schoolmate.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;

import schoolmate.control.tableModel.StudentModel;
import schoolmate.database.EducationLog;
import schoolmate.database.StudentLog;
import schoolmate.database.WorkLog;
import schoolmate.model.DBConnect;
import schoolmate.model.Education;
import schoolmate.model.Student;
import schoolmate.model.Work;
import schoolmate.view.element.MyTable;

public class SelectUpdateFrame extends JInternalFrame implements ActionListener{
	private Student stu;
	private int checkRes;
	private Thread thread;
	private MyTable table;
	private JLabel msgLabel = new JLabel();
	private JScrollPane scroll,tableScroll;
	public StudentModel studentModel;
	public Vector<Object[]> data = new Vector<Object[]>();
	private JPanel interPanel,stuTabelPanel,btnPanel;
	private JButton jumpBtn,updateBtn,closeBtn,insertBtn;
	private String userStr = new String();
	private JEditorPane editPane;
	private String cell = "strftime('%Y-%m-%d',datetime(s.update_time, 'unixepoch')),s.s_id,e.s_no,s_name,s_sex,s_birth,s_person,s_hometown,e.s_faculty,e.s_major,"
			+ "e.s_class,e.s_education,e.s_enter,e.s_graduate,s_nation,s_province,s_city,s_workspace,s_worktitle,"
			+ "s_work,s_workphone,s_homephone,s_phone,s_tphone,s_address,s_postcode,s_email,s_remark1,s_qq,s_weixin,"
			+ "s_remark2,s_remark3,s_remark4,s_remark5,s.update_time";
	public SelectUpdateFrame(Thread thread,Student stu,int checkRes) {
		if(checkRes<4)
			msgLabel.setText("学历可能需要更新");
		else{
			msgLabel.setText("工作信息可能需要更新");
		}
		String[] remarks = StudentLog.SelectRemarks();
		msgLabel.setFont(new java.awt.Font("幼园", 1, 20));
		this.stu = stu;
		this.thread = thread;
		this.checkRes = checkRes;
		studentModel = new StudentModel(data,0);
		this.userStr = new String(
				"<h3>导入的用户信息</h3>"
				+"<ul>"
				+ "<li>学号："+stu.s_no+"</li>"
				+ "<li>姓名："+stu.s_name+"</li>"
				+ "<li>性别："+stu.s_sex+"</li>"
				+ "<li>出生日期："+stu.s_birth+"</li>"
				+ "<li>身份证号："+stu.s_person+"</li>"
				+ "<li>籍贯："+stu.s_hometown+"</li>"
				+ "<li>学院："+stu.s_faculty+"</li>"
				+ "<li>专业："+stu.s_major+"</li>"
				+ "<li>班级："+stu.s_class+"</li>"
				+ "<li>学历："+stu.s_education+"</li>"
				+ "<li>入学年份："+stu.s_enter+"</li>"
				+ "<li>毕业年份："+stu.s_graduate+"</li>"
				+ "<li>工作国家："+stu.s_nation+"</li>"
				+ "<li>工作省份："+stu.s_province+"</li>"
				+ "<li>工作市区："+stu.s_city+"</li>"
				+ "<li>工作单位："+stu.s_workspace+"</li>"
				+ "<li>职务："+stu.s_work+"</li>"
				+ "<li>职称："+stu.s_worktitle+"</li>"
				+ "<li>工作电话："+stu.s_workphone+"</li>"
				+ "<li>家庭电话："+stu.s_homephone+"</li>"
				+ "<li>手机："+stu.s_phone+"</li>"
				+ "<li>手机2："+stu.s_tphone+"</li>"
				+ "<li>通讯地址："+stu.s_address+"</li>"
				+ "<li>邮编："+stu.s_postcode+"</li>"
				+ "<li>邮箱："+stu.s_email+"</li>"
				+ "<li>"+remarks[0]+"："+stu.s_remark1+"</li>"
				+ "<li>QQ："+stu.s_qq+"</li>"
				+ "<li>微信："+stu.s_weixin+"</li>"
				+ "<li>"+remarks[1]+"："+stu.s_remark2+"</li>"
				+ "<li>"+remarks[2]+"："+stu.s_remark3+"</li>"
				+ "<li>"+remarks[3]+"："+stu.s_remark4+"</li>"
				+ "<li>"+remarks[4]+"："+stu.s_remark5+"</li>"
				+ "</ul>"
				);
		editPane = new JEditorPane("text/html", userStr);
		init();
	}
	
	public void init() {
		setMaximizable(true); //设置提供最大化按钮
        setIconifiable(true); //设置提供图标化按钮
		setTitle(stu.s_name+"校友信息更新");
		setLayout(new BorderLayout());
		updateBtn = new JButton("选择更新");
		insertBtn = new JButton("直接导入");
		jumpBtn = new JButton("暂时跳过");
		closeBtn = new JButton("直接关闭");
		btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,10,5));
		jumpBtn.addActionListener(this);
		jumpBtn.setForeground(Color.white);  
		jumpBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
		closeBtn.addActionListener(this);
		closeBtn.setForeground(Color.white);  
		closeBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
		updateBtn.addActionListener(this);
		updateBtn.setForeground(Color.white);  
		updateBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
		insertBtn.addActionListener(this);
		insertBtn.setForeground(Color.white);  
		insertBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
		btnPanel.add(updateBtn);
		btnPanel.add(insertBtn);
		btnPanel.add(jumpBtn);
		btnPanel.add(closeBtn);
		btnPanel.add(msgLabel);
		interPanel = new JPanel();
		stuTabelPanel = new JPanel(new BorderLayout());
		interPanel.add(editPane);
		try {
			String condition = null;
			String str = "";
			if(checkRes>=4) {
				if(!stu.s_graduate.equals(""))
					condition = " and s_graduate='"+stu.s_graduate+"'";
				if(!stu.s_enter.equals(""))
					condition += " and s_enter='"+stu.s_enter+"'";
				str = "select "+cell+" from student s join education e on s.s_id=e.s_id where s_name='"+stu.s_name+"' and s_faculty='"+stu.s_faculty+"' and s_major='"+stu.s_major+"' and (s_workspace!='"+stu.s_workspace+"' or s_worktitle!='"+stu.s_worktitle+"') "+condition+" and s_education='"+stu.s_education+"' and s_education!='';";
			}else {
				if(!stu.s_graduate.equals(""))	//毕业年份 >= 当前-4 <=当前-2 
					condition = "' and ((s_graduate>='"+(Integer.parseInt(stu.s_graduate)-4)+"' and s_graduate<='"+(Integer.parseInt(stu.s_graduate)-2)+"')"
							+" or (s_graduate<='"+(Integer.parseInt(stu.s_graduate)+4)+"' and s_graduate>='"+(Integer.parseInt(stu.s_graduate)+2)+"'))";
				else if(stu.s_graduate.equals("")&&!stu.s_enter.equals(""))
					condition = "' and ((s_enter>='"+(Integer.parseInt(stu.s_enter)-4)+"' and s_enter<='"+(Integer.parseInt(stu.s_enter)-2)+"')"
							+" or (s_enter<='"+(Integer.parseInt(stu.s_enter)+4)+"' and s_enter>='"+(Integer.parseInt(stu.s_enter)+2)+"'))";
				str = "select "+cell+" from student s left join education e on s.s_id = e.s_id where s_name='"+stu.s_name+"' and s_sex='"+stu.s_sex+"' and s_education!='"+stu.s_education+condition+" order by s.update_time desc;";
			}
			data = StudentLog.dao(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		studentModel.setData(data);
		table = new MyTable(studentModel);
		table.setFillsViewportHeight(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		scroll = new JScrollPane(interPanel);
		tableScroll = new JScrollPane(table);
		stuTabelPanel.add(tableScroll);
		stuTabelPanel.add(btnPanel,BorderLayout.NORTH);
		add(scroll,BorderLayout.WEST);
		add(stuTabelPanel,BorderLayout.CENTER);
		setSize(PencilMain.showWidth,PencilMain.showHeight-75);
		setVisible(true);
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		JButton btn = (JButton)e.getSource();
		if(btn==jumpBtn) {
			ImportExlFrame.importState = 1;
		}else if(btn==closeBtn) {
			ImportExlFrame.importState = 2;
		}else if(btn==updateBtn) {
			ImportExlFrame.importState = 3;
			if(studentModel.getSelectCount()==1) {
				String id = studentModel.getSelect();
				int index = studentModel.getSelectIndex();
				try {
					boolean res = false;
					Education edu = new Education(Integer.parseInt(id), stu.s_no, stu.s_education, 
							stu.s_faculty, stu.s_major, stu.s_class, stu.s_enter,stu.s_graduate);
					if(checkRes<4)
						res = EducationLog.insertEdu(DBConnect.getStmt(), edu,true);
					if(checkRes>=4) {
						Work work = new Work(stu.s_nation, stu.s_province, stu.s_city, stu.s_work,stu.s_worktitle, stu.s_workspace, stu.s_workphone);
						work.s_id = Integer.parseInt(id);
						WorkLog.insertWork(work, DBConnect.getStmt());
					}	
				} catch (NumberFormatException e1) {
					e1.printStackTrace();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}else if(btn==insertBtn) {
			try {
				StudentLog.importExl(DBConnect.getStmt(),stu);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		synchronized (thread) {
			thread.notify();
			this.dispose();
		}  
	}

}
