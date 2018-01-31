package schoolmate.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;

import schoolmate.control.WorkModel;
import schoolmate.database.FacultyLog;
import schoolmate.database.MajorLog;
import schoolmate.database.StudentLog;
import schoolmate.database.WorkLog;
import schoolmate.view.element.DateChooser;
import schoolmate.view.element.MyTextField;
import schoolmate.view.element.TableLeftMouse;

public  class StudentDetailFrame extends JInternalFrame implements ActionListener{

	private String[] student;
	private String sexArray[]={"","男","女"};
	private String education[] = {"","专科","本科","硕士","博士"};
	public JPanel detailPanel;
	private JPanel detailTabel;
	private WorkModel workModel;
	private JTable workTabel;
	private JScrollPane tableScroll;
	private JLabel countLabel = new JLabel("学号：");
    private JTextField countInput = new JTextField(10);
    private JLabel nameLabel = new JLabel("姓名：");  
    private JTextField nameInput = new MyTextField("必填内容",10,null);
    private JLabel sexLabel = new JLabel("性别：");  
    private JComboBox<String> sexBox = new JComboBox<String>(sexArray);
    private JLabel birthLabel = new JLabel("出生日期："); 
    private DateChooser dateChooser = DateChooser.getInstance("yyyyMMdd");
    private JTextField birthInput = new JTextField("");  
    private JLabel personLabel = new JLabel("身份证号：");
    private JTextField personInput = new JTextField(10);
    private JLabel hometownLabel = new JLabel("家庭住址：");
    private JTextField hometownInput = new JTextField(10);
    private JLabel classLabel = new JLabel("班级：");
    private JTextField classInput = new MyTextField("计算机科学与技术151",10,null);
    private JLabel facultyLabel = new JLabel("学院：");
    private JTextField facultyInput;
    private JLabel majorLabel = new JLabel("系别：");
    private JTextField majorInput;
    private JLabel educationLabel = new JLabel("学历：");
    private JComboBox<String> educationInput = new JComboBox<String>(education);
    private JLabel graduateLabel = new JLabel("毕业年份：");
    private JTextField graduateInput = new JTextField(10);
    private JLabel provinceLabel = new JLabel("工作省份：");
    private JTextField provinceInput = new JTextField(10);
    private JLabel cityLabel = new JLabel("工作市区：");
    private JTextField cityInput = new JTextField(10);
    private JLabel workspaceLabel = new JLabel("工作单位：");
    private JTextField workspaceInput = new JTextField(10);
    private JLabel worktitleLabel = new JLabel("职称：");
    private JTextField worktitleInput = new MyTextField("教授",10,null);
    private JLabel workLabel = new JLabel("职务：");
    private JTextField workInput = new MyTextField("教师",10,null);
    private JLabel workphoneLabel = new JLabel("单位电话：");
    private JTextField workphoneInput = new JTextField(10);
    private JLabel homephoneLabel = new JLabel("家庭电话：");
    private JTextField homephoneInput = new JTextField(10);
    private JLabel phoneLabel = new JLabel("手机号：");
    private JTextField phoneInput = new JTextField(10);
    private JLabel tphoneLabel = new JLabel("副手机号：");
    private JTextField tphoneInput = new JTextField(10);
    private JLabel addressLabel = new JLabel("通讯地址：");
    private JTextField addressInput = new JTextField(10);
    private JLabel postcodeLabel = new JLabel("邮编：");
    private JTextField postcodeInput = new JTextField(10);
    private JLabel emailLabel = new JLabel("Email：");
    private JTextField emailInput = new MyTextField("123456@qq.com",10,null);
    private JLabel qqLabel = new JLabel("QQ：");
    private JTextField qqInput = new JTextField(10);
    private JLabel remarkLabel1 = new JLabel("备注1：");
    private JTextField remarkInput1 = new JTextField(10);
    private JLabel remarkLabel2 = new JLabel("备注2：");
    private JTextField remarkInput2 = new JTextField(10);
    private JLabel remarkLabel3 = new JLabel("备注3：");
    private JTextField remarkInput3 = new JTextField(10);
    private JLabel remarkLabel4 = new JLabel("备注4：");
    private JTextField remarkInput4 = new JTextField(10);
    private JLabel remarkLabel5 = new JLabel("备注5：");
    private JTextField remarkInput5 = new JTextField(10);
    private JLabel nationLanel = new JLabel("工作国家：");
    private JTextField nationInput = new JTextField(10);
    private JLabel enterLabel = new JLabel("入学年份：");
    private JTextField enterInput = new JTextField(10);
    private JLabel weixinLabel = new JLabel("微信：");
    private JTextField weixinInput = new JTextField(10);
    //sId判断为添加还是修改 0=添加 >1=修改;
	public StudentDetailFrame(PencilMain pencil,String[] student) throws SQLException{
		this.student = student;
		init();
		setData();
	}
	public void init() throws SQLException{
		setClosable(true);//提供关闭按钮
		setResizable(true);  //允许自由调整大小 
        setIconifiable(true); //设置提供图标化按钮
        setMaximizable(true); //设置提供最大化按钮
        setTitle(student[3]+"的详细信息");
		sexBox.setEditable(true);
		dateChooser.register(birthInput);
		facultyInput = new MyTextField("信息工程学院",10,null);
		majorInput = new MyTextField("信息工程系",10,null);
		setSize(PencilMain.showWidth,690);
		toFront();
		detailPanel = new JPanel();
		GroupLayout layout = new GroupLayout(detailPanel);
		detailPanel.setLayout(layout);
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
				.addComponent(worktitleInput).addComponent(phoneInput).addComponent(addressInput).addComponent(weixinInput).addComponent(remarkInput3));
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
        		.addComponent(remarkLabel5).addComponent(remarkInput5));
        vGroup.addGap(30);
        layout.setVerticalGroup(vGroup);//设置垂直组

        Vector<Object[]> data = null;
		try {
			//{"工作省份","工作市区","职务","职称","工作单位"};
			data = WorkLog.dao("select s_id,nation,province,city,s_work,s_worktitle,s_workspace from worklog where s_id = '"+student[1]+"'");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
        workModel = new WorkModel(data);
        workTabel = new JTable(workModel);
        workTabel.setAutoCreateRowSorter(true);
        workTabel.setFillsViewportHeight(true);
        tableScroll = new JScrollPane(workTabel);
        detailTabel = new JPanel();
        detailTabel.add(tableScroll);
		add(detailPanel,BorderLayout.WEST);
		add(detailTabel);
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
		countInput.setText(student[2]);countInput.setEditable(false);
		nameInput.setText(student[3]);nameInput.setEditable(false);
		sexBox.setSelectedItem(student[4]);sexBox.setEditable(false);
		birthInput.setText(student[5]);birthInput.setEditable(false);
		personInput.setText(student[6]);personInput.setEditable(false);
		hometownInput.setText(student[7]);hometownInput.setEditable(false);
		facultyInput.setText(student[8]);facultyInput.setEditable(false);
		majorInput.setText(student[9]);majorInput.setEditable(false);
		classInput.setText(student[10]);classInput.setEditable(false);
		educationInput.setSelectedItem(student[11]);educationInput.setEditable(false);
		enterInput.setText(student[12]);enterInput.setEditable(false);
		graduateInput.setText(student[13]);graduateInput.setEditable(false);
		nationInput.setText(student[14]);nationInput.setEditable(false);
		provinceInput.setText(student[15]);provinceInput.setEditable(false);
		cityInput.setText(student[16]);cityInput.setEditable(false);
		workspaceInput.setText(student[17]);workspaceInput.setEditable(false);
		worktitleInput.setText(student[18]);worktitleInput.setEditable(false);
		workInput.setText(student[19]);workInput.setEditable(false);
		workphoneInput.setText(student[20]);workphoneInput.setEditable(false);
		homephoneInput.setText(student[21]);homephoneInput.setEditable(false);
		phoneInput.setText(student[22]);phoneInput.setEditable(false);
		tphoneInput.setText(student[23]);tphoneInput.setEditable(false);
		addressInput.setText(student[24]);addressInput.setEditable(false);
		postcodeInput.setText(student[25]);postcodeInput.setEditable(false);
		emailInput.setText(student[26]);emailInput.setEditable(false);
		qqInput.setText(student[27]);qqInput.setEditable(false);
		weixinInput.setText(student[28]);weixinInput.setEditable(false);
		remarkInput1.setText(student[29]);remarkInput1.setEditable(false);
		remarkInput2.setText(student[30]);remarkInput2.setEditable(false);
		remarkInput3.setText(student[31]);remarkInput3.setEditable(false);
		remarkInput4.setText(student[32]);remarkInput4.setEditable(false);
		remarkInput5.setText(student[33]);remarkInput5.setEditable(false);
	}
	
	public void actionPerformed(ActionEvent e) {
		// TODO 自动生成的方法存根
		
	}

}
