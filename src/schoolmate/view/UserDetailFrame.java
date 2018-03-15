package schoolmate.view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.GroupLayout;
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
import schoolmate.database.FacultyLog;
import schoolmate.database.UserLog;
import schoolmate.model.User;
import schoolmate.view.element.MyTextField;

public class UserDetailFrame extends JInternalFrame implements ActionListener{
	private PencilMain pencilMain;
	protected JPanel loginContent;
	private User user;
	private JComboBox<String> userPro = new JComboBox<String>(PencilMain.education);
	private JPasswordField userAns = new JPasswordField(15); 
	private JLabel nameLabel = new JLabel("用户 名：");
	private JTextField nameShow = new JTextField("");
	private JLabel facultyLabel = new JLabel("学     院：");
	private JLabel facultyShow = new JLabel("信息工程学院");
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
        loginContent.setBackground(Color.WHITE);
        
        confirm.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));  
        confirm.setForeground(Color.white);
        confirm.addActionListener(this);
        nameShow.setText(user.u_name);
        userShow.setText(user.u_count);
        userAns.setText(user.u_answer);
        roleShow.setText(user.getRole());
        facultyShow.setText(user.faculty);
        userPro.setSelectedIndex(user.u_problem);
        
        //创建GroupLayout的水平连续组，，越先加入的ParallelGroup，优先级级别越高。几列
  		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
  		hGroup.addGap(20);//添加间隔
  		hGroup.addGroup(layout.createParallelGroup().addComponent(nameLabel).addComponent(facultyLabel).addComponent(userLabel).addComponent(roleLabel)
  				.addComponent(proLabel).addComponent(ansLabel).addComponent(confirm));
  		hGroup.addGap(15);
  		hGroup.addGroup(layout.createParallelGroup().addComponent(nameShow).addComponent(facultyShow).addComponent(userShow).addComponent(roleShow)
  				.addComponent(userPro).addComponent(userAns));
  		hGroup.addGap(20);
  		layout.setHorizontalGroup(hGroup);//设置水平组
  		//创建GroupLayout的垂直连续组，，越先加入的ParallelGroup，优先级级别越高。几行
  		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
  		vGroup.addGap(20);
  		vGroup.addGroup(layout.createParallelGroup().addComponent(nameLabel).addComponent(nameShow));
  		vGroup.addGap(20);
  		vGroup.addGroup(layout.createParallelGroup().addComponent(facultyLabel).addComponent(facultyShow));
  		vGroup.addGap(20);
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
        setSize(400, 380);
        setLocation((PencilMain.showWidth-400)/2, 0);
        add(loginContent);
		setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		JButton btn = (JButton)e.getSource();
		if(btn==confirm){
			String ans = new String(userAns.getPassword());
			int index = userPro.getSelectedIndex();
			if(index>0){
				if(!Helper.matchRegular(ans, PencilMain.regular[index])){
					JOptionPane.showMessageDialog(this, "密保问题不符合要求！");
					return;
				}
				if(user.u_problem==index&&user.u_answer.equals(ans)){
					JOptionPane.showMessageDialog(this, "密保问题没有修改，请重新设置！");
					return;
				}else{
					boolean res = UserLog.updateAns(pencilMain.nowUser.u_count, index, ans,nameShow.getText(),facultyShow.getText());
					if(res){
						user.u_problem = index;
						user.u_answer = ans;
						user.u_name = nameShow.getText();
						JOptionPane.showMessageDialog(this, "更新成功");
						this.dispose();
					}else{
						JOptionPane.showMessageDialog(this, "修改失败");
					}
				}
				
			}else 
				JOptionPane.showMessageDialog(null, "请选择密保问题！");
		}
	}
}
