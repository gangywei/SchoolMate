package schoolmate.view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;

import schoolmate.control.Helper;
import schoolmate.database.AddressLog;
import schoolmate.database.EducationLog;
import schoolmate.database.FacultyLog;
import schoolmate.database.MajorLog;
import schoolmate.database.WorkLog;
import schoolmate.model.Education;
import schoolmate.model.Work;
import schoolmate.view.element.MyTextField;

public class AddStudentLogFrame extends JInternalFrame implements ActionListener{
	private JPanel detailPanel = new JPanel();
	private String education[] = {"","专科","本科","硕士","博士"};
	private JLabel workLabel[] = {new JLabel("工作国家："),new JLabel("工作省份："),new JLabel("工作市区：")
			,new JLabel("职    务："),new JLabel("职    称："),new JLabel("工作单位：")};
	private JLabel eduLabel[] = {new JLabel("学    号："),new JLabel("学     院："),new JLabel("专    业："),new JLabel("班    级：")
			,new JLabel("学    历："),new JLabel("入学年份："),new JLabel("毕业年份：")};
	private MyTextField inputAry[] = new MyTextField[7];
	private List<Object> majorList,facultyList,eduList,nationList,provinceList,cityList,yearList;
	private String[] majorAry,facultyAry;
	private JButton submitBtn = new JButton("提    交"),
			resetBtn = new JButton("关闭页面");
	private int type,sId;
	private int nowId = 0;
	private String data[];
	private StudentDetailFrame detail;
	private Calendar date = Calendar.getInstance();
	private int inYear = 1985,outYear = date.get(Calendar.YEAR);
	//type 判断0=工作记录 1=教育记录 2=需要修改学生表的工作记录
	//n_id 修改信息的now_ID,0=不修改
	public AddStudentLogFrame(int type,int sId,StudentDetailFrame detail,int n_id){
		this.nowId = n_id;
		this.detail = detail;
		this.type = type;
		this.sId = sId;
		initFrame();
	}
	
	public void initFrame(){
		submitBtn.addActionListener(this);
		resetBtn.addActionListener(this);
		submitBtn.setForeground(Color.white);  
		submitBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));
		resetBtn.setForeground(Color.white);  
		resetBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
		setClosable(true);//提供关闭按钮
		GroupLayout layout = new GroupLayout(detailPanel);
		detailPanel.setLayout(layout);
		for(int i=0;i<7;i++)
			inputAry[i] = new MyTextField("", 15);
		if(nowId!=0){
			if(type!=1)
				data = WorkLog.searchWork(nowId);
			else
				data = EducationLog.searchEdu(nowId);
			for(int i=0;i<data.length&&data!=null;i++)
				inputAry[i].setText(data[i]);
		}
		
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		hGroup.addGap(20);//添加间隔
		if(type!=1){
			if(nowId!=0)
				setTitle("修改工作记录");
			else
				setTitle("添加工作记录");
			nationList = new ArrayList<Object>();
			provinceList = new ArrayList<Object>();
			cityList = new ArrayList<Object>();
			String nationAry[] = AddressLog.allNation();
			String provinceAry[] = AddressLog.allProvince("");
			String cityAry[] = AddressLog.allCity("");
			for(int i=0;i<nationAry.length;i++)
				nationList.add(nationAry[i]);
			for(int i=0;i<provinceAry.length;i++)
				provinceList.add(provinceAry[i]);
			for(int i=1;i<cityAry.length;i++)
				cityList.add(cityAry[i]);
			inputAry[0].setMenu(nationList);
			inputAry[1].setMenu(provinceList);
			inputAry[2].setMenu(cityList);
	  		hGroup.addGroup(layout.createParallelGroup().addComponent(workLabel[0]).addComponent(workLabel[1]).addComponent(workLabel[2]).addComponent(workLabel[3])
	  				.addComponent(workLabel[4]).addComponent(workLabel[5]).addComponent(submitBtn));
	  		hGroup.addGap(15);
			hGroup.addGroup(layout.createParallelGroup().addComponent(inputAry[0]).addComponent(inputAry[1]).addComponent(inputAry[2]).addComponent(inputAry[3])
	  				.addComponent(inputAry[4]).addComponent(inputAry[5]).addComponent(resetBtn));
	  		hGroup.addGap(20);
		}else{
			if(nowId!=0)
				setTitle("修改学历记录");
			else
				setTitle("添加学历记录");
			if(nowId==0){
				inputAry[4].setShowText("必填内容");
				inputAry[1].setShowText("必填内容");
				inputAry[6].setShowText("必填内容");
			}
			eduList = new ArrayList<Object>();
			yearList = new ArrayList<Object>();
			majorList = new ArrayList<Object>();
	        facultyList = new ArrayList<Object>();
	        if(detail.collect.user.u_role==1)
	        	facultyAry = FacultyLog.allFaculty("where f_name in "+detail.collect.limitTemp);
	        else
	        	facultyAry = FacultyLog.allFaculty("");
//	        for(int now=outYear;now>=inYear;now--)
//				yearList.add(now+"");
	        if(detail.collect.user.u_role==1)
	        	majorAry = MajorLog.allMajor("where f_name in "+detail.collect.limitTemp);
	        else
	        	majorAry = MajorLog.allMajor("");
			for(int i=0;i<majorAry.length;i++)
				majorList.add(majorAry[i]);
			for(int i=0;i<facultyAry.length;i++)
				facultyList.add(facultyAry[i]);
			for(int i=1;i<education.length;i++)
				eduList.add(education[i]);
			inputAry[1].setMenu(facultyList);
			inputAry[2].setMenu(majorList);
			inputAry[4].setMenu(eduList);
//			inputAry[5].setMenu(yearList);
//			inputAry[6].setMenu(yearList);
	  		hGroup.addGroup(layout.createParallelGroup().addComponent(eduLabel[0]).addComponent(eduLabel[1]).addComponent(eduLabel[2]).addComponent(eduLabel[3])
	  				.addComponent(eduLabel[4]).addComponent(eduLabel[5]).addComponent(eduLabel[6]).addComponent(submitBtn));
	  		hGroup.addGap(15);
			hGroup.addGroup(layout.createParallelGroup().addComponent(inputAry[0]).addComponent(inputAry[1]).addComponent(inputAry[2]).addComponent(inputAry[3])
	  				.addComponent(inputAry[4]).addComponent(inputAry[5]).addComponent(inputAry[6]).addComponent(resetBtn));
	  		hGroup.addGap(20);
		}
	
  		layout.setHorizontalGroup(hGroup);//设置水平组
  		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
        vGroup.addGap(20);
        if(type!=1){
        	for(int i=0;i<6;i++){
	        	vGroup.addGroup(layout.createParallelGroup()
	            		.addComponent(workLabel[i]).addComponent(inputAry[i]));
	        	vGroup.addGap(17);
        	}
    	}else{
    		for(int i=0;i<7;i++){
	        	vGroup.addGroup(layout.createParallelGroup()
	            		.addComponent(eduLabel[i]).addComponent(inputAry[i]));
	        	vGroup.addGap(15);
        	}
        }
        vGroup.addGroup(layout.createParallelGroup()
        		.addComponent(submitBtn).addComponent(resetBtn));
        vGroup.addGap(30);
        layout.setVerticalGroup(vGroup);//设置垂直组
        add(detailPanel);
        setVisible(true);
		setSize(400, 420);
		setLocation((PencilMain.showWidth-400)/2, 0);
	}
	
	public void actionPerformed(ActionEvent e) {
		JButton btn = (JButton)e.getSource();
		if(btn==submitBtn){
			if(type!=1){
				Work temp = new Work(inputAry[0].getText(),inputAry[1].getText(),inputAry[2].getText(),inputAry[3].getText(),
						inputAry[4].getText(),inputAry[5].getText());
				temp.s_id = sId;
				temp.wl_id = nowId;
				try {
					if(nowId==0){
						WorkLog.insertWork(temp, null);
					}else{
						if(type==0)
							WorkLog.updateWork(temp, null,0);
						else
							WorkLog.updateWork(temp, null,1);
					}
					detail.workTabel.updateTabel();
					this.dispose();
				} catch (SQLException e1) {
					JOptionPane.showMessageDialog(this, "添加失败");
					e1.printStackTrace();
				}
			}else{
				String s_no = inputAry[0].getText();
				String regNo = "[\\d]{8,15}";
				if(!s_no.equals(""))
					if(!Helper.matchRegular(s_no, regNo)){
						JOptionPane.showMessageDialog(this, "学号输入不符合要求");
						return;
					}
				if(Integer.parseInt(inputAry[5].getText())>=Integer.parseInt(inputAry[6].getText())){
					JOptionPane.showMessageDialog(this, "学生入学信息输入错误");
					return;
				}
				Education temp = new Education(0,s_no,inputAry[4].getText(),inputAry[1].getText(),inputAry[2].getText(),
						inputAry[3].getText(),inputAry[5].getText(),inputAry[6].getText());
				temp.s_id = sId;
				temp.e_id = nowId;
				boolean res = false;
				try {
					if(nowId==0){
						res = EducationLog.insertEdu(null, temp);
					}else{
						if(data[0].equals(s_no)&&data[3].equals(inputAry[3].getText())&&data[4].equals(inputAry[4].getText())&&data[5].equals(inputAry[5].getText())
								&&data[6].equals(inputAry[6].getText()))
							res = EducationLog.updateEdu(null, temp,true);	//需要查找数据库是否有该同学的学院专业数据
						else
							res = EducationLog.updateEdu(null, temp,false);
					}
					if(!res)	//查找数据库是否有该同学的学院专业数据的结果
						JOptionPane.showMessageDialog(this, "存在该学历信息");
					if(res){
						detail.eduTabel.updateTabel();
						this.dispose();
					}
				} catch (SQLException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(this, "添加失败");
				}
			}
			detail.collect.refeshBtn.doClick();
		}else if(btn==resetBtn){
			this.dispose();
		}
	}

}
