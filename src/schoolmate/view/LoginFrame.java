package schoolmate.view;

import javax.swing.JInternalFrame;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;

import schoolmate.control.Helper;
import schoolmate.database.UserLog;
import schoolmate.model.User;
import schoolmate.view.element.RegexDocument;

public class LoginFrame extends JInternalFrame implements ActionListener{
	private PencilMain pencilMain;
	protected JPanel loginContent;
	private JTextField userCount = new JTextField("18439331592",15);
    private JPasswordField userPwd = new JPasswordField("18439331592",15);  
    private JLabel userLabel = new JLabel("账     号：");  
    private JLabel pwdLabel = new JLabel("密     码：");  
    private JButton loginBtn = new JButton("登    录");  
    private JButton resetBtn = new JButton("忘记密码");
    
	public LoginFrame(PencilMain pencil){
		initPanel();
		pencilMain = pencil;
	}
	public void initPanel(){
		setTitle("登录页面");
		FlowLayout layout = new FlowLayout(1,30,20);
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
        setLocation((PencilMain.showWidth-400)/2, (PencilMain.showHeight-240)/2);
        add(loginContent);
		setVisible(true);
	}
	
	//修改密码后更新输入框的输入
	public void setUser(){
		userCount.setText(pencilMain.nowUser.u_count);
		userPwd.setText(pencilMain.nowUser.u_pwd);
	}
	
	public void actionPerformed(ActionEvent e) {
		JButton btn = (JButton)e.getSource();
		if(btn==loginBtn){
			String uCount = userCount.getText().trim();
			String uPwd = new String(userPwd.getPassword()).trim();
			if(uCount.equals("")||uPwd.equals("")){
				JOptionPane.showMessageDialog(null, "用户名或密码不能为空");
				return;
			}else{
				String regPwd = "^[\\w]{6,12}$";
				if(!Helper.matchPhone(uCount)){
					JOptionPane.showMessageDialog(null, "用户名为手机号！");
					return;
				}else if(!Helper.matchRegular(uPwd, regPwd)){
					JOptionPane.showMessageDialog(null, "密码为6-12位英文或数字的组合");
					return;
				}
				try {
					String[] result = UserLog.getUser(uCount,uPwd,new Date().getTime()/1000);
					if(result!=null){
						PencilMain.nowUser = new User(uCount,uPwd);
						pencilMain.nowUser.faculty = result[3];
						pencilMain.nowUser.u_role = Integer.parseInt(result[4]);
						pencilMain.nowUser.u_problem = Integer.parseInt(result[1]);
						pencilMain.nowUser.u_answer = result[2];
						pencilMain.nowUser.u_name = result[0];
						pencilMain.loginSuccess();
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

