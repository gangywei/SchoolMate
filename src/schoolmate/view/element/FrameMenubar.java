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
import schoolmate.database.UserLog;
import schoolmate.view.PencilMain;

public class FrameMenubar implements ActionListener{
	private JMenuBar MenuBar;
	private JMenu userMenu;
	private JButton submitBtn;
	private JDialog alterDialog;
	JPasswordField pwdInput,tpwdInput;
	private JMenuItem exitItem,userItem,chPwdItem;
	private PencilMain pencilMain;
	
	String education[] = {"  请选择设置的密保问题  ","我的账号","最喜欢的成语","其他数字密码","好朋友的名字"};
	JComboBox<String> userPro = new JComboBox<String>(education);
	JTextField userAns = new JTextField(15);
	public FrameMenubar(PencilMain pencil){
		pencilMain = pencil;
	}
	
	public JMenuBar createMenu(){
		submitBtn = new JButton("确     定");
		submitBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));  
		submitBtn.setForeground(Color.white);  
		submitBtn.addActionListener(this);
		MenuBar = new JMenuBar();
		userMenu = new JMenu("功能栏");
		userMenu.setMnemonic(KeyEvent.VK_F);
		exitItem = new JMenuItem("关闭 ");
		userItem = new JMenuItem("用户信息");
		chPwdItem = new JMenuItem("修改密码");
		userItem.addActionListener(this);
		chPwdItem.addActionListener(this);
		exitItem.addActionListener(this);
		userMenu.add(userItem);
		userMenu.add(chPwdItem);
		userMenu.add(exitItem);
		MenuBar.add(userMenu);
		return MenuBar;
	}
	
	public void showDialog(){
		alterDialog = new JDialog();
		JPanel alterPanel = new JPanel();
        alterDialog.setSize(400, 270);
        
        alterDialog.setLocation((PencilMain.frameWidth-300)/2,(PencilMain.frameHeight-160)/2);
        alterDialog.setTitle("修改密码");
        JLabel pwdLabel = new JLabel("密码");
        JLabel tpwdLabel = new JLabel("再次输入密码");
        pwdInput = new JPasswordField(80);
        tpwdInput = new JPasswordField(80);
        
    	JLabel proLabel = new JLabel("问题"),
    			ansLabel = new JLabel("回答");
        
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
        alterDialog.setModal(true);//确保弹出的窗口在其他窗口前面
	}
	
	public void alterPwd() throws NoSuchAlgorithmException, UnsupportedEncodingException, SQLException{
		String pwd,tpwd,answer;
		int index = userPro.getSelectedIndex();
		answer = userAns.getText();
		pwd = new String(pwdInput.getPassword());
		tpwd = new String(tpwdInput.getPassword());
		if(index>0)
			if(!pwd.equals("")&&!tpwd.equals("")){
				if(pwd.equals(tpwd)){
					boolean res = UserLog.alterPwd(PencilMain.nowUser.u_count,pwd,index,answer);
					if(res){
						JOptionPane.showMessageDialog(null, "修改成功");
						alterDialog.setVisible(false);
					}else{
						JOptionPane.showMessageDialog(null, "修改失败");
					}
				}else{
					JOptionPane.showMessageDialog(null, "输入的密码不相同");
				}
			}else{
				JOptionPane.showMessageDialog(null, "密码框不可以为空");
			}
		else{
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
