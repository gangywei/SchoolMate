package schoolmate.view;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;

import schoolmate.control.Helper;
import schoolmate.database.StudentLog;
import schoolmate.database.UserLog;
import schoolmate.model.User;
import schoolmate.view.element.MyTextField;

public class ForgetPasswordFrame extends JInternalFrame implements ActionListener{
	private PencilMain pencilMain;
	protected JPanel loginContent;
	private MyTextField userCount = new MyTextField("用户注册手机号",15);
	private JComboBox<String> userPro = new JComboBox<String>(PencilMain.education);
	private JPasswordField userAns = new JPasswordField(15); 
    private JPasswordField userPwd = new JPasswordField(15);  
    private JPasswordField confirmUserPwd = new JPasswordField(15);
   private JLabel userLabel = new JLabel("账      号： ");  
    private JLabel proLabel = new JLabel("问      题： ");  
    private JLabel ansLabel = new JLabel("答      案： "); 
    private JLabel pwdLabel = new JLabel("新 密 码： ");
    private JLabel confirmPwdLabel = new JLabel("确认密码：");
    private JButton confirm = new JButton("确    认");  
    private JButton cancel = new JButton("取    消");
	public ForgetPasswordFrame(PencilMain pencil){
		initPanel();
		pencilMain = pencil;
	}
	public void initPanel(){
		setTitle("忘记密码");
		FlowLayout layout = new FlowLayout(1,20,20);
        loginContent = new JPanel();
        loginContent.setLayout(layout);
        loginContent.setBackground(Color.WHITE);
        loginContent.setPreferredSize(new Dimension(360, 180));    
        confirm.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));  
        confirm.setForeground(Color.white);  
        confirm.addActionListener(this);
        cancel.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));  
        cancel.setForeground(Color.white);  
        cancel.addActionListener(this);
        confirm.setPreferredSize(new Dimension(150, 35));
        cancel.setPreferredSize(new Dimension(150, 35));
        loginContent.add(userLabel);  
        loginContent.add(userCount);  
        loginContent.add(proLabel);  
        loginContent.add(userPro); 
        loginContent.add(ansLabel);  
        loginContent.add(userAns); 
        loginContent.add(pwdLabel);  
        loginContent.add(userPwd); 
        loginContent.add(confirmPwdLabel);
        loginContent.add(confirmUserPwd);  
        loginContent.add(confirm); 
        loginContent.add(cancel);
        setSize(360, 360);
        setLocation((PencilMain.showWidth-360)/2, (PencilMain.showHeight-360)/2);
        add(loginContent);
		setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		JButton btn = (JButton)e.getSource();
		if(btn==confirm){
			String uname = userCount.getText().trim();
			int index = userPro.getSelectedIndex();
			String answer = new String(userAns.getPassword()).trim();
			String upwd = new String(userPwd.getPassword()).trim();
			String confirmUpwd = new String(confirmUserPwd.getPassword()).trim();
			
			if(uname.equals("")||upwd.equals("")||confirmUpwd.equals("")){
				JOptionPane.showMessageDialog(null, "用户名或密码不能为空");
				return;
			}else if(index==0){
				JOptionPane.showMessageDialog(null, "请选择并填写密保问题！");
				return;
			}else if(!upwd.trim().equals(confirmUpwd.trim())){
				JOptionPane.showMessageDialog(null, "密码不一致");
				return;
			}else if(!Helper.matchRegular(upwd.trim(), PencilMain.regular[1])){
				JOptionPane.showMessageDialog(null, "密码格式为6-12位数字或字母");
				return;
			}

			try {
				boolean result = UserLog.SelectUser(uname,index,answer);
				if(result){
					if(UserLog.alterPwd(uname.trim(), upwd.trim(),index,answer)){
						PencilMain.nowUser = new User(uname, upwd);
						JOptionPane.showMessageDialog(null, "修改成功,即将返回登陆页面");
						pencilMain.showLogin();
						dispose();
					}else{
						JOptionPane.showMessageDialog(null, "修改密码失败，请重新操作");
						return;
					}
				}else{
					JOptionPane.showMessageDialog(null, "用户名或者密保问题输入错误，找不到该用户");
					return;
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}

		}else if(btn==cancel){
			dispose();
			try {
				pencilMain.showLogin();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
}
