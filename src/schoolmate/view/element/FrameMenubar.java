package schoolmate.view.element;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;

import schoolmate.control.Helper;
import schoolmate.database.UserLog;
import schoolmate.view.PencilMain;
import sun.security.util.Pem;

public class FrameMenubar implements ActionListener{
	private JMenuBar MenuBar;
	private JMenu mainMenu,dataMenu,userMenu;
	private JButton submitBtn;
	private JDialog alterDialog;
	JPasswordField pwdInput,tpwdInput;
	private JMenuItem exitItem,userItem,chPwdItem;
	private JMenuItem backupItem,renewItem,outputItem;
	private JMenuItem addUserItem,manUserItem;
	private PencilMain pencilMain;
	
	JComboBox<String> userPro = new JComboBox<String>(PencilMain.education);
	JPasswordField userAns = new JPasswordField(15);
	public FrameMenubar(PencilMain pencil){
		pencilMain = pencil;
	}
	
	public JMenuBar createMenu(){
		ImageIcon icon=new ImageIcon("resource/img/deng.png");
		submitBtn = new JButton("确     定");
		submitBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));  
		submitBtn.setForeground(Color.white);  
		submitBtn.addActionListener(this);
		MenuBar = new JMenuBar();
		
		mainMenu = new JMenu("主菜单");
		mainMenu.setIcon(icon);
		exitItem = new JMenuItem("关闭系统 ");
		userItem = new JMenuItem("用户信息");
		chPwdItem = new JMenuItem("修改密码");
		userItem.addActionListener(this);
		chPwdItem.addActionListener(this);
		exitItem.addActionListener(this);
		mainMenu.add(userItem);
		mainMenu.add(chPwdItem);
		mainMenu.add(exitItem);
		
		ImageIcon backIcon=new ImageIcon("resource/img/backup.png");
		dataMenu = new JMenu("备份与恢复");
		backupItem = new JMenuItem("数据备份");
		renewItem = new JMenuItem("数据恢复");
		outputItem = new JMenuItem("导出更新数据");
		renewItem.addActionListener(this);
		backupItem.addActionListener(this);
		outputItem.addActionListener(this);
		dataMenu.add(backupItem);
		dataMenu.add(renewItem);
		dataMenu.add(outputItem);
		dataMenu.setIcon(backIcon);
		MenuBar.add(mainMenu);
		MenuBar.add(dataMenu);
		
		ImageIcon userIcon=new ImageIcon("resource/img/user.png");
		userMenu =  new JMenu("用户管理");
		addUserItem = new JMenuItem("添加用户");
		addUserItem.addActionListener(this);
		manUserItem = new JMenuItem("管理用户");
		manUserItem.addActionListener(this);
		userMenu.setIcon(userIcon);
		userMenu.add(addUserItem);
		userMenu.add(manUserItem);
		if(pencilMain.nowUser.u_role>=2){
			MenuBar.add(userMenu);
		}
		return MenuBar;
	}
	
	public void MenuControl(boolean state){
		mainMenu.setEnabled(state);
		userMenu.setEnabled(state);
		dataMenu.setEnabled(state);
	}
	
	public void showDialog(){
		if(alterDialog!=null)
			alterDialog.dispose();
		alterDialog = new JDialog();
		JPanel alterPanel = new JPanel();
        alterDialog.setSize(400, 270);
        alterDialog.setLocation((PencilMain.frameWidth-400)/2,(PencilMain.frameHeight-270)/2);
        alterDialog.setTitle("修改密码");
        JLabel pwdLabel = new JLabel("密码：");
        JLabel tpwdLabel = new JLabel("再次输入：");
        pwdInput = new JPasswordField(80);
        tpwdInput = new JPasswordField(80);
        
    	JLabel proLabel = new JLabel("密保问题："),
    			ansLabel = new JLabel("回答问题：");
    	userPro.setSelectedIndex(pencilMain.nowUser.u_problem);
        
        GroupLayout layout = new GroupLayout(alterPanel);
        GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		hGroup.addGap(10);//添加间隔
		hGroup.addGroup(layout.createParallelGroup().addComponent(proLabel).addComponent(ansLabel).addComponent(pwdLabel)
                .addComponent(tpwdLabel).addComponent(submitBtn));
		hGroup.addGap(10);//添加间隔
		hGroup.addGroup(layout.createParallelGroup().addComponent(userPro).addComponent(userAns).addComponent(pwdInput)
                .addComponent(tpwdInput));
		hGroup.addGap(10);
		layout.setHorizontalGroup(hGroup);//设置水平组
		
		//创建GroupLayout的垂直连续组，，越先加入的ParallelGroup，优先级级别越高。几行
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		 vGroup.addGap(10);
        vGroup.addGroup(layout.createParallelGroup().addComponent(proLabel).addComponent(userPro));
        vGroup.addGap(10);
        vGroup.addGroup(layout.createParallelGroup().addComponent(ansLabel).addComponent(userAns));
        vGroup.addGap(10);
        vGroup.addGroup(layout.createParallelGroup().addComponent(pwdLabel).addComponent(pwdInput));
        vGroup.addGap(10);
        vGroup.addGroup(layout.createParallelGroup().addComponent(tpwdLabel).addComponent(tpwdInput));
        vGroup.addGap(10);
        vGroup.addGroup(layout.createParallelGroup().addComponent(submitBtn));
        vGroup.addGap(10);
        layout.setVerticalGroup(vGroup);//设置垂直组
        alterPanel.setLayout(layout);
        alterDialog.setVisible(true);
        alterDialog.add(alterPanel);
	}
	
	public void alterPwd() throws NoSuchAlgorithmException, UnsupportedEncodingException, SQLException{
		String pwd,tpwd,answer;
		int index = userPro.getSelectedIndex();
		answer = new String(userAns.getPassword());
		pwd = new String(pwdInput.getPassword());
		tpwd = new String(tpwdInput.getPassword());
		if(index>0){
			if(!Helper.matchRegular(answer, PencilMain.regular[index])){
				JOptionPane.showMessageDialog(null, "密保回答不符合要求！");
				return;
			}
			if(!pwd.equals("")&&!tpwd.equals("")){
				if(pwd.equals(tpwd)){
					boolean res = UserLog.alterPwd(PencilMain.nowUser.u_count,pwd,index,answer);
					if(res){
						JOptionPane.showMessageDialog(null, "修改成功");
						alterDialog.dispose();
					}else{
						JOptionPane.showMessageDialog(null, "修改失败");
					}
				}else{
					JOptionPane.showMessageDialog(null, "输入的密码不相同");
				}
			}else{
				JOptionPane.showMessageDialog(null, "密码框不可以为空");
			}
		}else{
			JOptionPane.showMessageDialog(null, "请输入密保问题!");
		}
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		Object temp = e.getSource();
		if(temp instanceof JMenuItem){
			temp = (JMenuItem)temp;
			if(temp==exitItem){
				System.exit(0);
			}else if(temp==chPwdItem){
				showDialog();
			}else if(temp==userItem){
				pencilMain.userDetail();
			}else if(temp==backupItem){
				pencilMain.backupsData();
			}else if(temp==renewItem){
				pencilMain.reNewFrame();
			}else if(temp==addUserItem){
				pencilMain.addUser(0);
			}else if(temp==manUserItem){
				pencilMain.allUser();
			}else if(temp==outputItem){
				pencilMain.backupUpdate();
			}
		}else if(temp instanceof JButton){
			if((JButton)temp==submitBtn){
				try {
					alterPwd();
				} catch (NoSuchAlgorithmException e1) {
					e1.printStackTrace();
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}
