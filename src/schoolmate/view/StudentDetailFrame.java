package schoolmate.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;

import schoolmate.control.tableModel.WorkModel;
import schoolmate.database.StudentLog;
import schoolmate.database.WorkLog;
import schoolmate.model.Student;
import schoolmate.view.element.DateChooser;
import schoolmate.view.element.MyTextField;
import schoolmate.view.element.tablePanel.EduDetailPanel;
import schoolmate.view.element.tablePanel.WorkDetailPanel;

public  class StudentDetailFrame extends JInternalFrame implements ActionListener{
	private String[] student;
	private String sexArray[]={"","男","女"};
	public JPanel detailPanel;
	private JPanel detailTabel;
	private JPanel centerPanel = new JPanel();
	private JButton addWork = new JButton("添加工作记录"),
			addEducation = new JButton("添加学习记录"),
			submitBtn = new JButton("修改基础信息");
	public WorkDetailPanel workTabel;
	public EduDetailPanel eduTabel;
	private JLabel countLabel = new JLabel("学号：");
	private MyTextField countInput = new MyTextField("2001198405",10);
    private JLabel nameLabel = new JLabel("姓名：");  
    private JTextField nameInput = new MyTextField("必填内容",10);
    private JLabel sexLabel = new JLabel("性别：");  
    private JComboBox<String> sexBox = new JComboBox<String>(sexArray);
    private JLabel birthLabel = new JLabel("出生日期："); 
    private DateChooser dateChooser = DateChooser.getInstance("yyyyMMdd");
    private JTextField birthInput = new JTextField("");  
    private JLabel personLabel = new JLabel("身份证号：");
    private MyTextField personInput = new MyTextField("身份证号",10);
    private JLabel hometownLabel = new JLabel("家庭住址：");
    private JTextField hometownInput = new JTextField(10);
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
    private JTextField emailInput = new MyTextField("123456@qq.com",10);
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
    private JLabel weixinLabel = new JLabel("微信：");
    private JTextField weixinInput = new JTextField(10);
    private PencilMain pencil;
    public CollectDataFrame collect;
    private int type;
    //type判断为添加还是修改 0=展示  1=修改 2=添加;
	public StudentDetailFrame(PencilMain pencil,String[] student,int type){
		this.type = type;
		this.pencil = pencil;
		collect = pencil.collectDataFrame;
		this.student = student;
		init();
		if(type<2)
			setData();
	}
	public void init(){
		String Remarks[] = null;
		try {
			Remarks = StudentLog.SelectRemarks();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		remarkLabel1.setText(Remarks[0]);
		remarkLabel2.setText(Remarks[1]);
		remarkLabel3.setText(Remarks[2]);
		remarkLabel4.setText(Remarks[3]);
		remarkLabel5.setText(Remarks[4]);
		addWork.addActionListener(this);
		submitBtn.addActionListener(this);
		addEducation.addActionListener(this);
		centerPanel.add(submitBtn);
		centerPanel.add(addWork);
		centerPanel.add(addEducation);
		addWork.setForeground(Color.white);  
		addWork.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
		submitBtn.setForeground(Color.white);  
		submitBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
		addEducation.setForeground(Color.white);  
		addEducation.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
		setClosable(true);//提供关闭按钮
		setResizable(true);  //允许自由调整大小 
        setIconifiable(true); //设置提供图标化按钮
        if(type==2)
        	setTitle("添加学生信息");
        else
        	setTitle(student[3]+"的详细信息");
		sexBox.setEditable(true);
		dateChooser.register(birthInput);
		toFront();
		detailPanel = new JPanel();
		detailPanel.setBackground(Color.WHITE);
		GroupLayout layout = new GroupLayout(detailPanel);
		detailPanel.setLayout(layout);
		//创建GroupLayout的水平连续组，，越先加入的ParallelGroup，优先级级别越高。几列
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		hGroup.addGap(20);//添加间隔
		hGroup.addGroup(layout.createParallelGroup()
				.addComponent(countLabel).addComponent(phoneLabel).addComponent(emailLabel).addComponent(remarkLabel1));
		hGroup.addGap(5);//添加间隔
		hGroup.addGroup(layout.createParallelGroup()
				.addComponent(countInput).addComponent(phoneInput).addComponent(emailInput).addComponent(remarkInput1));
		hGroup.addGap(20);
		hGroup.addGroup(layout.createParallelGroup()
				.addComponent(nameLabel).addComponent(tphoneLabel).addComponent(qqLabel).addComponent(remarkLabel2));
		hGroup.addGap(5);
		hGroup.addGroup(layout.createParallelGroup()
				.addComponent(nameInput).addComponent(tphoneInput).addComponent(qqInput).addComponent(remarkInput2));
		hGroup.addGap(20);
		hGroup.addGroup(layout.createParallelGroup()
				.addComponent(sexLabel).addComponent(hometownLabel).addComponent(weixinLabel).addComponent(remarkLabel3));
		hGroup.addGap(5);
		hGroup.addGroup(layout.createParallelGroup()
				.addComponent(sexBox).addComponent(hometownInput).addComponent(weixinInput).addComponent(remarkInput3));
		hGroup.addGap(20);
		hGroup.addGroup(layout.createParallelGroup()
				.addComponent(birthLabel).addComponent(homephoneLabel).addComponent(postcodeLabel).addComponent(remarkLabel4));
		hGroup.addGap(5);
		hGroup.addGroup(layout.createParallelGroup()
				.addComponent(birthInput).addComponent(homephoneInput).addComponent(postcodeInput).addComponent(remarkInput4));
		hGroup.addGap(20);
		hGroup.addGroup(layout.createParallelGroup()
				.addComponent(personLabel).addComponent(workphoneLabel).addComponent(addressLabel).addComponent(remarkLabel5));
		hGroup.addGap(5);
		hGroup.addGroup(layout.createParallelGroup()
				.addComponent(personInput).addComponent(workphoneInput).addComponent(addressInput).addComponent(remarkInput5));
		hGroup.addGap(20);
		layout.setHorizontalGroup(hGroup);//设置水平组
		//创建GroupLayout的垂直连续组，，越先加入的ParallelGroup，优先级级别越高。几行
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
        vGroup.addGap(30);
        vGroup.addGroup(layout.createParallelGroup()
        		.addComponent(countLabel).addComponent(countInput)
                .addComponent(nameLabel).addComponent(nameInput)
                .addComponent(sexLabel).addComponent(sexBox)
                .addComponent(birthLabel).addComponent(birthInput)
                .addComponent(personLabel).addComponent(personInput)
                );
        vGroup.addGap(10);
        vGroup.addGroup(layout.createParallelGroup()
                .addComponent(phoneLabel).addComponent(phoneInput)
                .addComponent(tphoneLabel).addComponent(tphoneInput)
                .addComponent(hometownLabel).addComponent(hometownInput)
                .addComponent(homephoneLabel).addComponent(homephoneInput)
                .addComponent(workphoneLabel).addComponent(workphoneInput)
                );
        vGroup.addGap(10);
        vGroup.addGroup(layout.createParallelGroup()
        		.addComponent(emailLabel).addComponent(emailInput)
        		.addComponent(qqLabel).addComponent(qqInput)
        		.addComponent(weixinLabel).addComponent(weixinInput)
        		.addComponent(postcodeLabel).addComponent(postcodeInput)
        		.addComponent(addressLabel).addComponent(addressInput)
        		);
        vGroup.addGap(10);
        vGroup.addGroup(layout.createParallelGroup()
        		.addComponent(remarkLabel1).addComponent(remarkInput1)
        		.addComponent(remarkLabel2).addComponent(remarkInput2)
        		.addComponent(remarkLabel3).addComponent(remarkInput3)
        		.addComponent(remarkLabel4).addComponent(remarkInput4)
        		.addComponent(remarkLabel5).addComponent(remarkInput5));
        vGroup.addGap(30);
        layout.setVerticalGroup(vGroup);//设置垂直组
        if(type==2){
        	addWork.setEnabled(false);
        	addEducation.setEnabled(false);
        	submitBtn.setText("确认添加信息");
        	workTabel = new WorkDetailPanel(pencil,0,this);
            eduTabel = new EduDetailPanel(pencil, 0,this);
        }else{
        	workTabel = new WorkDetailPanel(pencil, Integer.parseInt(student[1]),this);
            eduTabel = new EduDetailPanel(pencil, Integer.parseInt(student[1]),this);
        }
        
        detailTabel = new JPanel(new BorderLayout());
        detailTabel.add(centerPanel);
        detailTabel.add(workTabel,BorderLayout.WEST);
        detailTabel.add(eduTabel,BorderLayout.EAST);
		add(detailPanel,BorderLayout.NORTH);
		add(detailTabel);
		setVisible(true);
		setSize(1100, 520);
		setLocation((PencilMain.showWidth-1100)/2, 0);
		try {
			setSelected(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		repaint();
	}
	
	/*{"583","2001198405","1","男","19800506","1231","河南滑县","法学","法学院","法学1班","本科","2003",
	"河南省","安阳市","内黄县公安局","科级","123123","0392-3325854","0392-3328569","13343727926",
	"123123","河南安阳开发大道156号","451002","4491552@qq.com","44791552"};*/
	public void setData(){
		countInput.setText(student[2]);
		nameInput.setText(student[3]);
		sexBox.setSelectedItem(student[4]);
		birthInput.setText(student[5]);
		personInput.setText(student[6]);
		hometownInput.setText(student[7]);
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
		if(type==0){
			submitBtn.setEnabled(false);
			countInput.setEnabled(false);
			nameInput.setEnabled(false);
			sexBox.setEnabled(false);
			birthInput.setEnabled(false);
			personInput.setEnabled(false);
			hometownInput.setEnabled(false);
			workphoneInput.setEnabled(false);
			homephoneInput.setEnabled(false);
			phoneInput.setEnabled(false);
			tphoneInput.setEnabled(false);
			addressInput.setEnabled(false);
			postcodeInput.setEnabled(false);
			emailInput.setEnabled(false);
			qqInput.setEnabled(false);
			weixinInput.setEnabled(false);
			remarkInput1.setEnabled(false);
			remarkInput2.setEnabled(false);
			remarkInput3.setEnabled(false);
			remarkInput4.setEnabled(false);
			remarkInput5.setEnabled(false);
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		JButton btn = (JButton)e.getSource();
		if(btn==addWork){
			try {
				pencil.addWorkLog(0,Integer.parseInt(student[1]),this,0);
			} catch (PropertyVetoException e1) {
				e1.printStackTrace();
			}
		}else if(btn==addEducation){
			try {
				pencil.addWorkLog(1,Integer.parseInt(student[1]),this,0);
			} catch (PropertyVetoException e1) {
				e1.printStackTrace();
			}
		}else if(btn==submitBtn){
			if(type==2){
				String name = nameInput.getText();
				String count = countInput.getText();
				String sex = (String) sexBox.getSelectedItem();
				String birth = birthInput.getText();
				String person = personInput.getText();
				String hometown = hometownInput.getText();
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
				String weixin = weixinInput.getText();
				Student temp = new Student(count, name, sex, birth, person, hometown, "", "", "", "", 
						"", "", "", "", "", "", "", "", workphone, homephone, 
						phone, tphone, address, postcode, email, qq, weixin, remark1, remark2, remark3, 
						remark4, remark5);
				student = temp.toArray();
				if(!name.equals("")){
					try {
						if(!name.trim().equals("")){
							String res = StudentLog.insertStudent(temp);
							if(res!=null)
								JOptionPane.showMessageDialog(null, res);
							else{
								student[1] = temp.s_id+"";
								workTabel.getDetail(temp.s_id);
								eduTabel.getDetail(temp.s_id);
								this.type = 1;	//变为修改状态
								submitBtn.setText("修改基础信息");
								addWork.setEnabled(true);
								addEducation.setEnabled(true);
								JOptionPane.showMessageDialog(null, "操作成功");
								pencil.collectDataFrame.refeshBtn.doClick();
							}
						}
					} catch (SQLException e1) {
						System.err.println("插入学生"+e.getClass().getName() + ": " + e1.getMessage());
					}
				}else{
					JOptionPane.showMessageDialog(null, "姓名不允许为空");
				}
			}else{
				String count = countInput.getText();
				if(!count.equals(""))
					if(!count.equals(student[2]))	//判断学号是否重复
						try {
							if(StudentLog.searchSno(student[2],null)!=0){
								JOptionPane.showMessageDialog(this, "存在该学号的学生,修改失败 ");
								return;
							}
						} catch (SQLException e2) {
							JOptionPane.showMessageDialog(this, "修改失败 ");
							e2.printStackTrace();
							return;
						}
				student[3] = nameInput.getText();
				student[2] = countInput.getText();
				student[4] = (String) sexBox.getSelectedItem();
				student[5] = birthInput.getText();
				student[6] = personInput.getText();
				student[7] = hometownInput.getText();
				student[20] = workphoneInput.getText();
				student[21] = homephoneInput.getText();
				student[22] = phoneInput.getText();
				student[23] = tphoneInput.getText();
				student[24] = addressInput.getText();
				student[25] = postcodeInput.getText();
				student[26] = emailInput.getText();
				student[27] = qqInput.getText();
				student[29] = remarkInput1.getText();
				student[30] = remarkInput2.getText();
				student[31] = remarkInput3.getText();
				student[32] = remarkInput4.getText();
				student[33] = remarkInput5.getText();
				student[28] = weixinInput.getText();
				if(!student[3].equals("")){
					try {
						if(!student[3].trim().equals("")){
							Student temp = new Student(student[2], student[3], student[4], student[5], student[6], student[7], student[8], student[9], student[10], 
									student[11], student[12], student[13], student[14], student[15], student[16], student[17], student[18], student[19], student[20], 
									student[21], student[22], student[23], student[24], student[25], student[26], student[27], student[28], student[29], student[30], 
									student[31], student[32],student[33]);
							String res = StudentLog.updateStudent(temp, Integer.parseInt(student[1]));
							if(res!=null)
								JOptionPane.showMessageDialog(this, res);
							else{
								JOptionPane.showMessageDialog(this, "操作成功");
								pencil.collectDataFrame.refeshBtn.doClick();
							}
						}
					} catch (SQLException e1) {
						System.err.println("添加学生"+e.getClass().getName() + ": " + e1.getMessage());
					}
				}else{
					JOptionPane.showMessageDialog(null, "姓名不允许为空");
				}
			}
		}
	}

}
