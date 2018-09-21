package schoolmate.view;
import java.awt.*;
import java.awt.event.*;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;
import schoolmate.control.Helper;
import schoolmate.database.FacultyLog;
import schoolmate.database.UserLog;
import schoolmate.view.element.MulitCombobox;
import schoolmate.view.element.MyTextField;
import schoolmate.view.element.tablePanel.AllUserPanel;
public class AddUserFrame extends JInternalFrame implements ActionListener{
	private MulitCombobox mulit; //多选权限按钮
	private Object[] FacultyStr;
	private Object[] defaultValue;
	protected JPanel loginContent,loginContent1,interPanel;
	String str = new String(
			"<strong>添加用户功能</strong>"
			+ "<p>新添加用户的默认密码和密保信息为<strong>用户账号</strong>！</p>");
	private JEditorPane editPane = new JEditorPane("text/html", str);
	private JTextField userName = new JTextField(15);
	private MyTextField userCount = new MyTextField("用户手机号",15);
	private JRadioButton userRole1 = new JRadioButton("普通用户");
	private JRadioButton userRole2 = new JRadioButton("学院管理者");
	private JRadioButton userRole3 = new JRadioButton("系统管理者");
	
	private JLabel facultyLabel = new JLabel("所属学院：");
	private JLabel roleLabel = new JLabel("用户身份：");
	private JLabel nameLabel = new JLabel("用户姓名：");
	private JLabel userLabel = new JLabel("用户账号：");
	
	private JButton confirm = new JButton("确    认");  
	private JButton cancel = new JButton("取    消");
	private AllUserPanel allUser;
	private int sNo = 0;
	private String user[];	//修改用户时保存用户的基本信息
	//sNo！=0表示修改用户，=0 表示添加用户
	public AddUserFrame(AllUserPanel allUser,int sNo){
		this.sNo = sNo;
		this.allUser = allUser;
		initPanel();
	}
	public void initPanel(){
		interPanel = new JPanel(new BorderLayout(20,5));
		if(sNo==0){
			setTitle("添加用户界面");
			interPanel.add(editPane);
			editPane.setEnabled(false);	
			userRole1.setSelected(true);
			defaultValue = new String[]{""};
		}else{
			setTitle("修改用户界面");
			user = UserLog.getUserBID(sNo);
			userName.setText(user[0]);
			userCount.setText(user[1]);
			defaultValue = user[2].split(",");
			if(Integer.parseInt(user[3])==3)
				userRole3.setSelected(true);
			else if(Integer.parseInt(user[3])==2)
				userRole2.setSelected(true);
			else
				userRole1.setSelected(true);
		}
		add(interPanel,BorderLayout.NORTH);
		FlowLayout layout = new FlowLayout(1,20,20);
		loginContent = new JPanel();
		loginContent.setBackground(Color.WHITE);
		loginContent1 = new JPanel(new GridLayout(2,2));
		loginContent.setLayout(layout);
		loginContent.setPreferredSize(new Dimension(320, 180));    
		confirm.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));  
		confirm.setForeground(Color.white);  
		confirm.addActionListener(this);
		cancel.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));  
		cancel.setForeground(Color.white);  
		cancel.addActionListener(this);
		confirm.setPreferredSize(new Dimension(150, 35));
		cancel.setPreferredSize(new Dimension(150, 35));
		
		FacultyStr = FacultyLog.allFaculty("");
		
		mulit = new MulitCombobox(FacultyStr, defaultValue);
		mulit.setPreferredSize(new Dimension(200,30));
		
		ButtonGroup grp = new ButtonGroup();
		grp.add(userRole1);
		loginContent1.add(userRole1);
		if(PencilMain.nowUser.u_role==3){
			grp.add(userRole2);
			grp.add(userRole3);
			loginContent1.add(userRole2);
			loginContent1.add(userRole3);
		}
		loginContent.add(nameLabel);  
		loginContent.add(userName);
		
		loginContent.add(userLabel);  
		loginContent.add(userCount);
		
		loginContent.add(roleLabel); 
		loginContent.add(loginContent1);
		
		loginContent.add(facultyLabel);  
		loginContent.add(mulit);
		
		loginContent.add(confirm); 
		loginContent.add(cancel);
		setSize(360, 400);
		setLocation((PencilMain.showWidth-360)/2, 0);
		add(loginContent);
		setVisible(true);
		setClosable(true);
	}
	public void actionPerformed(ActionEvent e) {
		JButton btn = (JButton)e.getSource();
		if(btn==confirm){
			String userNumber = userCount.getText().trim();
			String uName = userName.getText().trim();
			Object[] v = mulit.getSelectedValues();  
			StringBuilder builder = new StringBuilder(); 
			for(Object dv : v){  
	             builder.append(dv);  
	             builder.append(",");  
	        } 
			String faculty = builder.toString();
			if(uName.equals("")||userNumber.equals("")){
				JOptionPane.showMessageDialog(this, "姓名、账号不能为空");
				return;
			}else if(!Helper.matchPhone(userNumber)){
				JOptionPane.showMessageDialog(this, "用户账号为手机号，格式错误！");
				return;
			}else if(faculty.equals("")||faculty==null){
				JOptionPane.showMessageDialog(this, "请选择用户所属的学院");
				return;
			}
			faculty = faculty.substring(0,faculty.length()-1);
			try {
				int userRole;
				if(userRole1.isSelected())
					userRole = 1;
				else if(userRole2.isSelected())
					userRole = 2;
				else
					userRole = 3;
				boolean result = false;
				if(sNo==0||!userNumber.equals(user[1]))
					result = UserLog.SelectUser(userNumber);
				if(result){
					JOptionPane.showMessageDialog(this, "用户账号已存在");
					return;
				}else{
					if(sNo==0){	//添加用户
						boolean res = UserLog.insterUser(uName,userNumber,userNumber,1,userNumber,userRole,faculty);
						if(res){
							JOptionPane.showMessageDialog(this, "添加成功");
							this.dispose();
						}else{
							JOptionPane.showMessageDialog(this, "添加失败");
						}
					}else{	//修改用户
						boolean res = UserLog.updateUser(uName,userNumber,userRole,1,faculty,sNo);
						if(res){
							JOptionPane.showMessageDialog(this, "修改成功");
							allUser.updateTabel();
							this.dispose();
						}else{
							JOptionPane.showMessageDialog(this, "修改失败");
						}
					}
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}else if(btn==cancel){
			dispose();
		}
	}
	public void doDefaultCloseAction() {  
	    dispose();
	}
}
