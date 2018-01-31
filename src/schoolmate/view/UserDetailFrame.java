package schoolmate.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;
import schoolmate.database.UserLog;
import schoolmate.model.User;

public class UserDetailFrame extends JInternalFrame implements ActionListener{
	private PencilMain pencilMain;
	protected JPanel loginContent;
	private int index,role;
	private User user;
	private String education[] = {"  请选择设置的密保问题  ","我的账号","最喜欢的成语","其他数字密码","好朋友的名字"};
	private JComboBox<String> userPro = new JComboBox<String>(education);
	private JTextField userAns = new JTextField(15); 
    private JLabel userLabel = new JLabel("账     号： "); 
    private JLabel userShow = new JLabel("");
    private JLabel proLabel = new JLabel("问     题： "); 
    private JLabel roleLabel = new JLabel("角     色： "); 
    private JLabel ansLabel = new JLabel("答     案： "); 
    private JLabel roleShow = new JLabel("");
    private JButton confirm = new JButton("修改信息");  
	public UserDetailFrame(PencilMain pencil){
		pencilMain = pencil;
		user = pencilMain.nowUser;
		initPanel();
	}
	public void initPanel(){
		setClosable(true);  //提供关闭按钮
		setTitle("用户详情");
        loginContent = new JPanel();
        GroupLayout layout = new GroupLayout(loginContent);
        loginContent.setLayout(layout);
        setBackground(Color.WHITE);
        loginContent.setBackground(Color.WHITE);
        
        confirm.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));  
        confirm.setForeground(Color.white);  
        confirm.addActionListener(this);
        userShow.setText(user.u_count);
        roleShow.setText(user.getRole());
        userAns.setText(user.u_answer);
        userPro.setSelectedIndex(user.u_problem);
        
        //创建GroupLayout的水平连续组，，越先加入的ParallelGroup，优先级级别越高。几列
  		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
  		hGroup.addGap(20);//添加间隔
  		hGroup.addGroup(layout.createParallelGroup().addComponent(userLabel).addComponent(roleLabel)
  				.addComponent(proLabel).addComponent(ansLabel).addComponent(confirm));
  		hGroup.addGap(15);
  		hGroup.addGroup(layout.createParallelGroup().addComponent(userShow).addComponent(roleShow)
  				.addComponent(userPro).addComponent(userAns));
  		hGroup.addGap(20);
  		layout.setHorizontalGroup(hGroup);//设置水平组
  		//创建GroupLayout的垂直连续组，，越先加入的ParallelGroup，优先级级别越高。几行
  		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
  		vGroup.addGap(20);//添加间隔
  		vGroup.addGroup(layout.createParallelGroup().addComponent(userLabel).addComponent(userShow));
  		vGroup.addGap(20);
  		vGroup.addGroup(layout.createParallelGroup().addComponent(roleLabel).addComponent(roleShow));
  		vGroup.addGap(20);
  		vGroup.addGroup(layout.createParallelGroup().addComponent(proLabel).addComponent(userPro));
  		vGroup.addGap(20);
  		vGroup.addGroup(layout.createParallelGroup().addComponent(ansLabel).addComponent(userAns));
  		vGroup.addGap(20);
  		vGroup.addGroup(layout.createParallelGroup().addComponent(confirm));
  		vGroup.addGap(20);
  		layout.setVerticalGroup(vGroup);//设置垂直组
        setSize(400, 300);
        setLocation((PencilMain.showWidth-450)/2, (PencilMain.showHeight-240)/2);
        add(loginContent);
		setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		JButton btn = (JButton)e.getSource();
		if(btn==confirm){
			String ans = userAns.getText();
			int index = userPro.getSelectedIndex();
			if(userPro.getSelectedIndex()==index&&user.u_answer==ans){
				JOptionPane.showMessageDialog(null, "更新成功");
			}else{
				if(index>0){
					boolean res = UserLog.updateAns(pencilMain.nowUser.u_count, index, ans);
					if(res){
						user.u_problem = index;
						user.u_answer = ans;
						JOptionPane.showMessageDialog(null, "更新成功");
					}else{
						JOptionPane.showMessageDialog(null, "修改失败");
					}
				}else{
					JOptionPane.showMessageDialog(null, "请选择密保问题");
				}
			}
		}
	}
}
