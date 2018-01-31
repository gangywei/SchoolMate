package schoolmate.view;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyVetoException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;

import schoolmate.database.FacultyLog;
import schoolmate.database.MajorLog;
import schoolmate.database.StudentLog;
import schoolmate.model.Student;
import schoolmate.view.element.DateChooser;
import schoolmate.view.element.MyTextField;

public class AddStudentFrame extends JInternalFrame implements ActionListener{
	private String[] student;
	private String sexArray[]={"","男","女"};
	private String education[] = {"","专科","本科","硕士","博士"};
	public JPanel addPanel;
    private JLabel countLabel = new JLabel("学号：");
    private JTextField countInput = new JTextField(20);
    private JLabel nameLabel = new JLabel("姓名：");  
    private JTextField nameInput = new MyTextField("必填内容",20,null);
    private JLabel sexLabel = new JLabel("性别：");  
    private JComboBox<String> sexBox = new JComboBox<String>(sexArray);
    private JLabel birthLabel = new JLabel("出生日期："); 
    private DateChooser dateChooser = DateChooser.getInstance("yyyyMMdd");
    private JTextField birthInput = new JTextField("");  
    private JLabel personLabel = new JLabel("身份证号：");
    private JTextField personInput = new JTextField(20);
    private JLabel hometownLabel = new JLabel("家庭住址：");
    private JTextField hometownInput = new JTextField(20);
    private JLabel classLabel = new JLabel("班级：");
    private JTextField classInput = new MyTextField("计算机科学与技术151",20,null);
    private JLabel facultyLabel = new JLabel("学院：");
    private JTextField facultyInput;
    private JLabel majorLabel = new JLabel("系别：");
    private JTextField majorInput;
    private JLabel educationLabel = new JLabel("学历：");
    private JComboBox<String> educationInput = new JComboBox<String>(education);
    private JLabel graduateLabel = new JLabel("毕业年份：");
    private JTextField graduateInput = new JTextField(20);
    private JLabel provinceLabel = new JLabel("工作省份：");
    private JTextField provinceInput = new JTextField(20);
    private JLabel cityLabel = new JLabel("工作市区：");
    private JTextField cityInput = new JTextField(20);
    private JLabel workspaceLabel = new JLabel("工作单位：");
    private JTextField workspaceInput = new JTextField(20);
    private JLabel worktitleLabel = new JLabel("职称：");
    private JTextField worktitleInput = new MyTextField("教授",20,null);
    private JLabel workLabel = new JLabel("职务：");
    private JTextField workInput = new MyTextField("教师",20,null);
    private JLabel workphoneLabel = new JLabel("单位电话：");
    private JTextField workphoneInput = new JTextField(20);
    private JLabel homephoneLabel = new JLabel("家庭电话：");
    private JTextField homephoneInput = new JTextField(20);
    private JLabel phoneLabel = new JLabel("手机号：");
    private JTextField phoneInput = new JTextField(20);
    private JLabel tphoneLabel = new JLabel("副手机号：");
    private JTextField tphoneInput = new JTextField(20);
    private JLabel addressLabel = new JLabel("通讯地址：");
    private JTextField addressInput = new JTextField(20);
    private JLabel postcodeLabel = new JLabel("邮编：");
    private JTextField postcodeInput = new JTextField(20);
    private JLabel emailLabel = new JLabel("Email：");
    private JTextField emailInput = new MyTextField("123456@qq.com",20,null);
    private JLabel qqLabel = new JLabel("QQ：");
    private JTextField qqInput = new JTextField(20);
    private JLabel remarkLabel1 = new JLabel("备注1：");
    private JTextField remarkInput1 = new JTextField(20);
    private JLabel remarkLabel2 = new JLabel("备注2：");
    private JTextField remarkInput2 = new JTextField(20);
    private JLabel remarkLabel3 = new JLabel("备注3：");
    private JTextField remarkInput3 = new JTextField(20);
    private JLabel remarkLabel4 = new JLabel("备注4：");
    private JTextField remarkInput4 = new JTextField(20);
    private JLabel remarkLabel5 = new JLabel("备注5：");
    private JTextField remarkInput5 = new JTextField(20);
    private JLabel nationLanel = new JLabel("工作国家：");
    private JTextField nationInput = new JTextField(20);
    private JLabel enterLabel = new JLabel("入学年份：");
    private JTextField enterInput = new JTextField(20);
    private JLabel weixinLabel = new JLabel("微信：");
    private JTextField weixinInput = new JTextField(20);
    
    private JButton submitBtn = new JButton("确  	  定");
    private String[] majorAry,facultyAry;
    private List<Object> majorList,facultyList;
    //sId判断为添加还是修改 0=添加 >1=修改;
	public AddStudentFrame(PencilMain pencil,String[] student) throws SQLException{
		init();
		if(student!=null){
			this.student = student;
			setData();
		}
	}
	public void init() throws SQLException{
		setClosable(true);	//提供关闭按钮
		setResizable(true);  //允许自由调整大小 
        setIconifiable(true); //设置提供图标化按钮
        setTitle("添加学生信息");
		sexBox.setEditable(true);
		dateChooser.register(birthInput);
		//按钮样式
		submitBtn.setPreferredSize(new Dimension(120, 35));
		submitBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));  
        submitBtn.setForeground(Color.white);  
        submitBtn.addActionListener(this);
        //输入框内容
        majorList = new ArrayList<Object>();
        facultyList = new ArrayList<Object>();
        facultyAry = FacultyLog.allFaculty();
		majorAry = MajorLog.allMajor("");
		for(int i=0;i<majorAry.length;i++)
			majorList.add(majorAry[i]);
		for(int i=0;i<facultyAry.length;i++)
			facultyList.add(facultyAry[i]);
		facultyInput = new MyTextField("信息工程学院",20,facultyList);
		majorInput = new MyTextField("信息工程系",20,majorList);
        
		setSize(1000, 600);
		setLocation((PencilMain.showWidth-1000)/2, 0);
		toFront();
		addPanel = new JPanel();
		GroupLayout layout = new GroupLayout(addPanel);
		addPanel.setLayout(layout);
		//创建GroupLayout的水平连续组，，越先加入的ParallelGroup，优先级级别越高。几列
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		hGroup.addGap(20);//添加间隔
		hGroup.addGroup(layout.createParallelGroup()
				.addComponent(countLabel).addComponent(birthLabel).addComponent(educationLabel).addComponent(facultyLabel).addComponent(nationLanel)
                .addComponent(workspaceLabel).addComponent(workphoneLabel).addComponent(tphoneLabel).addComponent(emailLabel).addComponent(remarkLabel1)
                .addComponent(remarkLabel4));
		hGroup.addGap(5);//添加间隔
		hGroup.addGroup(layout.createParallelGroup()
				.addComponent(countInput).addComponent(birthInput).addComponent(educationInput).addComponent(facultyInput).addComponent(nationInput )
                .addComponent(workspaceInput).addComponent(workphoneInput).addComponent(tphoneInput).addComponent(emailInput).addComponent(remarkInput1)
                .addComponent(remarkInput4));
		hGroup.addGap(20);
		hGroup.addGroup(layout.createParallelGroup()
				.addComponent(nameLabel).addComponent(personLabel).addComponent(enterLabel).addComponent(majorLabel).addComponent(provinceLabel)
				.addComponent(workLabel).addComponent(homephoneLabel).addComponent(postcodeLabel).addComponent(qqLabel).addComponent(remarkLabel2).
				addComponent(remarkLabel5));
		hGroup.addGap(5);
		hGroup.addGroup(layout.createParallelGroup()
				.addComponent(nameInput).addComponent(personInput).addComponent(enterInput).addComponent(majorInput).addComponent(provinceInput)
				.addComponent(workInput).addComponent(homephoneInput).addComponent(postcodeInput).addComponent(qqInput).addComponent(remarkInput2)
				.addComponent(remarkInput5));
		hGroup.addGap(20);
		hGroup.addGroup(layout.createParallelGroup()
				.addComponent(sexLabel).addComponent(hometownLabel).addComponent(graduateLabel).addComponent(classLabel).addComponent(cityLabel)
				.addComponent(worktitleLabel).addComponent(phoneLabel).addComponent(addressLabel).addComponent(weixinLabel).addComponent(remarkLabel3));
		hGroup.addGap(5);
		hGroup.addGroup(layout.createParallelGroup()
				.addComponent(sexBox).addComponent(hometownInput).addComponent(graduateInput).addComponent(classInput).addComponent(cityInput)
				.addComponent(worktitleInput).addComponent(phoneInput).addComponent(addressInput).addComponent(weixinInput).addComponent(remarkInput3)
				.addComponent(submitBtn));
		hGroup.addGap(20);
		layout.setHorizontalGroup(hGroup);//设置水平组
		//创建GroupLayout的垂直连续组，，越先加入的ParallelGroup，优先级级别越高。几行
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
        vGroup.addGap(30);
        vGroup.addGroup(layout.createParallelGroup()
        		.addComponent(countLabel).addComponent(countInput)
                .addComponent(nameLabel).addComponent(nameInput)
                .addComponent(sexLabel).addComponent(sexBox));
        vGroup.addGap(10);
        vGroup.addGroup(layout.createParallelGroup()
        		.addComponent(birthLabel).addComponent(birthInput)
                .addComponent(personLabel).addComponent(personInput)
                .addComponent(hometownLabel).addComponent(hometownInput));
        vGroup.addGap(10);
        vGroup.addGroup(layout.createParallelGroup()
        		.addComponent(educationLabel).addComponent(educationInput)
        		.addComponent(enterLabel).addComponent(enterInput)
        		.addComponent(graduateLabel).addComponent(graduateInput));
        vGroup.addGap(10);
        vGroup.addGroup(layout.createParallelGroup()
        		.addComponent(facultyLabel).addComponent(facultyInput)
        		.addComponent(majorLabel).addComponent(majorInput)
        		.addComponent(classLabel).addComponent(classInput));
        vGroup.addGap(10);
        vGroup.addGroup(layout.createParallelGroup()
        		.addComponent(nationLanel).addComponent(nationInput)
        		.addComponent(provinceLabel).addComponent(provinceInput)
        		.addComponent(cityLabel).addComponent(cityInput));
        vGroup.addGap(10);
        vGroup.addGroup(layout.createParallelGroup()
        		.addComponent(workspaceLabel).addComponent(workspaceInput)
        		.addComponent(workLabel).addComponent(workInput)
        		.addComponent(worktitleLabel).addComponent(worktitleInput));
        vGroup.addGap(10);
        vGroup.addGroup(layout.createParallelGroup()
        		.addComponent(workphoneLabel).addComponent(workphoneInput)
        		.addComponent(homephoneLabel).addComponent(homephoneInput)
        		.addComponent(phoneLabel).addComponent(phoneInput));
        vGroup.addGap(10);
        vGroup.addGroup(layout.createParallelGroup()
        		.addComponent(tphoneLabel).addComponent(tphoneInput)
        		.addComponent(postcodeLabel).addComponent(postcodeInput)
        		.addComponent(addressLabel).addComponent(addressInput));
        vGroup.addGap(10);
        vGroup.addGroup(layout.createParallelGroup()
        		.addComponent(emailLabel).addComponent(emailInput)
        		.addComponent(qqLabel).addComponent(qqInput)
        		.addComponent(weixinLabel).addComponent(weixinInput));
        vGroup.addGap(10);
        vGroup.addGroup(layout.createParallelGroup()
        		.addComponent(remarkLabel1).addComponent(remarkInput1)
        		.addComponent(remarkLabel2).addComponent(remarkInput2)
        		.addComponent(remarkLabel3).addComponent(remarkInput3));
        vGroup.addGap(10);
        vGroup.addGroup(layout.createParallelGroup()
        		.addComponent(remarkLabel4).addComponent(remarkInput4)
        		.addComponent(remarkLabel5).addComponent(remarkInput5)
        		.addComponent(submitBtn));
        vGroup.addGap(30);
        layout.setVerticalGroup(vGroup);//设置垂直组

		add(addPanel);
		setVisible(true);
		try {
			setSelected(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		setVisible(true);
		repaint();
	}
	/*{"583","2001198405","1","男","19800506","1231","河南滑县","法学","法学院","法学1班","本科","2003",
		"河南省","安阳市","内黄县公安局","科级","123123","0392-3325854","0392-3328569","13343727926",
		"123123","河南安阳开发大道156号","451002","4491552@qq.com","44791552"};*/
	public void setData(){
		submitBtn.setText("修改");
		countInput.setText(student[2]);
		nameInput.setText(student[3]);
		sexBox.setSelectedItem(student[4]);
		birthInput.setText(student[5]);
		personInput.setText(student[6]);
		hometownInput.setText(student[7]);
		facultyInput.setText(student[8]);
		majorInput.setText(student[9]);
		classInput.setText(student[10]);
		educationInput.setSelectedItem(student[11]);
		enterInput.setText(student[12]);
		graduateInput.setText(student[13]);
		nationInput.setText(student[14]);
		provinceInput.setText(student[15]);
		cityInput.setText(student[16]);
		workspaceInput.setText(student[17]);
		worktitleInput.setText(student[18]);
		workInput.setText(student[19]);
		workphoneInput.setText(student[20]);
		homephoneInput.setText(student[21]);
		phoneInput.setText(student[22]);
		tphoneInput.setText(student[23]);
		addressInput.setText(student[24]);
		postcodeInput.setText(student[25]);
		emailInput.setText(student[26]);
		qqInput.setText(student[27]);
		weixinInput.setText(student[28]);
		remarkInput1.setText(student[29]);
		remarkInput2.setText(student[30]);
		remarkInput3.setText(student[31]);
		remarkInput4.setText(student[32]);
		remarkInput5.setText(student[33]);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		JButton btn = (JButton)e.getSource();
		if(btn==submitBtn){
			String name = nameInput.getText();
			String count = countInput.getText();
			String sex = (String) sexBox.getSelectedItem();
			String birth = birthInput.getText();
			String person = personInput.getText();
			String hometown = hometownInput.getText();
			String classes = classInput.getText();
			String faculty = facultyInput.getText();
			String major = majorInput.getText();
			String education = (String) educationInput.getSelectedItem();
			String graduate = graduateInput.getText();
			String province = provinceInput.getText();
			String city = cityInput.getText();
			String workspace = workspaceInput.getText();
			String worktitle = worktitleInput.getText();
			String work = workInput.getText();
			String workphone = workphoneInput.getText();
			String homephone = homephoneInput.getText();
			String phone = phoneInput.getText();
			String tphone = tphoneInput.getText();
			String address = addressInput.getText();
			String postcode = postcodeInput.getText();
			String email = emailInput.getText();
			String qq = qqInput.getText();
			String remark1 = remarkInput1.getText();
			String remark2 = remarkInput2.getText();
			String remark3 = remarkInput3.getText();
			String remark4 = remarkInput4.getText();
			String remark5 = remarkInput5.getText();
			String enter = enterInput.getText();
			String nation = nationInput.getText();
			String weixin = weixinInput.getText();
			
			Student temp = new Student(count, name, sex, birth, person, hometown, faculty, major, classes, education, 
					enter, graduate, nation, province, city, workspace, work, worktitle, workphone, homephone, 
					phone, tphone, address, postcode, email, qq, weixin, remark1, remark2, remark3, 
					remark4, remark5);
			if(!name.equals("")){
				try {
					if(student==null){
						if(!name.trim().equals("")){
							String res = StudentLog.insertStudent(temp);
							if(res!=null)
								JOptionPane.showMessageDialog(null, res);
							else
								JOptionPane.showMessageDialog(null, "操作成功");
						}
					}else{
						if(!name.trim().equals("")){
							boolean workState = false;
							if(!workspace.equals(student[15])||!worktitle.equals(student[16])||!work.equals(student[17])||
									!province.equals(student[13])||!city.equals(student[13]))
								workState = true;
							System.out.println(student[1]);
							String res = StudentLog.updateStudent(temp, Integer.parseInt(student[1]),workState);
							if(res!=null)
								JOptionPane.showMessageDialog(null, res);
							else
								JOptionPane.showMessageDialog(null, "操作成功");
						}
					}
				} catch (SQLException e1) {
					System.err.println("插入学生"+e.getClass().getName() + ": " + e1.getMessage());
				}
			}else{
				JOptionPane.showMessageDialog(null, "姓名不允许为空");
			}
		}
	}
}
