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
import sun.util.logging.resources.logging;

public class LoginFrame extends JInternalFrame implements ActionListener{
	private PencilMain pencilMain;
	protected JPanel loginContent;
	private JTextField userCount = new JTextField(15);
    private JPasswordField userPwd = new JPasswordField(15);  
    private JLabel userLabel = new JLabel("账   号：");  
    private JLabel pwdLabel = new JLabel("密   码：");  
    private JButton loginBtn = new JButton("登  录");  
    private JButton resetBtn = new JButton("忘记密码");
    
	public LoginFrame(PencilMain pencil){
		initPanel();
		pencilMain = pencil;
	}
	public void initPanel(){
		setTitle("登录页面");
		
		FlowLayout layout = new FlowLayout(1,20,20);
        loginContent = new JPanel();
        loginContent.setLayout(layout);
        loginContent.setBackground(Color.WHITE);
        loginContent.setPreferredSize(new Dimension(400, 200));    
		loginBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));  
        loginBtn.setForeground(Color.white);  
        loginBtn.addActionListener(this);
        resetBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));  
        resetBtn.setForeground(Color.white);  
        resetBtn.addActionListener(this);
        loginBtn.setPreferredSize(new Dimension(120, 35));
        resetBtn.setPreferredSize(new Dimension(120, 35));
        
        loginContent.add(userLabel);  
        loginContent.add(userCount);  
        loginContent.add(pwdLabel);  
        loginContent.add(userPwd);  
        loginContent.add(loginBtn);
        loginContent.add(resetBtn);
        setSize(400, 240);
        setLocation((PencilMain.showWidth-450)/2, (PencilMain.showHeight-240)/2);
        add(loginContent);
		setVisible(true);
	}
	
	public void setUser(){
		userCount.setText(pencilMain.nowUser.u_count);
		userPwd.setText(pencilMain.nowUser.u_pwd);
	}
	
	public void actionPerformed(ActionEvent e) {
		JButton btn = (JButton)e.getSource();
		if(btn==loginBtn){
			String uname = userCount.getText();
			String upwd = new String(userPwd.getPassword());
			if(uname.trim().equals("")||upwd.trim().equals("")){
				JOptionPane.showMessageDialog(null, "用户名或密码不能为空");
				return;
			}else{
				try {
					String[] result = UserLog.getUser(uname,upwd);
					if(result!=null){
						PencilMain.nowUser = new User(uname,upwd);
						pencilMain.nowUser.u_role = Integer.parseInt(result[0]);
						pencilMain.nowUser.u_problem = Integer.parseInt(result[1]);
						pencilMain.nowUser.u_answer = result[2];
						pencilMain.userLogin();
					}else{
						JOptionPane.showMessageDialog(null, "用户名或密码错误");
						return;
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}else if(btn==resetBtn){
			this.setVisible(false);
			pencilMain.forgetPassword();
		}
	}
}
