 package schoolmate.view;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;

import schoolmate.database.StudentLog;

public class RemarksFrame extends JInternalFrame implements ActionListener{
	public JPanel BackupsPanel;
	public int sheetCount=0,totleRow;
	String[] RemarksDate = new String[5];
    private JLabel RemarksLabel[] = new JLabel[5];
    private JTextField[] Remarks = new JTextField[5];
    private JButton Update = new JButton("修    改");
    private JButton Back = new JButton("返    回");
	public RemarksFrame() {
		Backups();
	}
	public void Backups() {
		for(int i=0;i<5;i++){
			RemarksLabel[i] = new JLabel("备注"+(i+1)+" :");
			Remarks[i] = new JTextField();
		}
		setTitle("修改备注字段");
    	setClosable(true);//提供关闭按钮
    	Update.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));  
    	Update.setForeground(Color.white);  
    	Update.addActionListener(this);
    	Back.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));  
    	Back.setForeground(Color.white);  
    	Back.addActionListener(this);
    	BackupsPanel = new JPanel();
    	BackupsPanel.setBackground(Color.WHITE);
    	GroupLayout layout = new GroupLayout(BackupsPanel); 
    	BackupsPanel.setLayout(layout);
		setSize(360, 360);
		setLocation((PencilMain.showWidth-360)/2, 0);
		setVisible(true);
		//创建GroupLayout的水平连续组，，越先加入的ParallelGroup，优先级级别越高。几列
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		hGroup.addGap(30);//添加间隔
		hGroup.addGroup(layout.createParallelGroup().addComponent(RemarksLabel[0])
				.addComponent(RemarksLabel[1]).addComponent(RemarksLabel[2]).addComponent(RemarksLabel[3])
		.addComponent(RemarksLabel[4]).addComponent(Update));
		hGroup.addGap(20); 
		hGroup.addGroup(layout.createParallelGroup().addComponent(Remarks[0])
				.addComponent(Remarks[1]).addComponent(Remarks[2]).addComponent(Remarks[3])
		.addComponent(Remarks[4]).addComponent(Back));
		hGroup.addGap(30);
		layout.setHorizontalGroup(hGroup);//设置水平组
		//创建GroupLayout的垂直连续组，，越先加入的ParallelGroup，优先级级别越高。几行
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		vGroup.addGap(20);//添加间隔
		for(int i=0;i<5;i++){
			vGroup.addGroup(layout.createParallelGroup().addComponent(RemarksLabel[i]).addComponent(Remarks[i]));
			vGroup.addGap(20);
		}
		vGroup.addGroup(layout.createParallelGroup().addComponent(Update).addComponent(Back));
		vGroup.addGap(30);
		layout.setVerticalGroup(vGroup);//设置垂直组
		add(BackupsPanel);
		try {
			RemarksDate = StudentLog.SelectRemarks();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		for(int i = 0;i<5;i++){
			Remarks[i].setText(RemarksDate[i]);
		}
	}
	public void actionPerformed(ActionEvent e) {
		JButton btn = (JButton)e.getSource();
		boolean update = false;
		if(btn == Update){
			for(int i = 0;i<5;i++){
				if(!Remarks[i].getText().equals(RemarksDate[i])){
					try {
						StudentLog.updateRemarks(Remarks[i].getText(), i);
						update = true;
					} catch (SQLException e1) {
						JOptionPane.showMessageDialog(this, "修改失败！");
						e1.printStackTrace();
						return;
					}
				}
			}
			if(update){
				JOptionPane.showMessageDialog(this, "修改成功！重新登录后更新信息");
			}else{
				JOptionPane.showMessageDialog(this, "改变数据后再点击修改！");
			}
		}else if(btn == Back){
			dispose();
		}
	}
	
	public void doDefaultCloseAction() {
		dispose();
	}
}
