package schoolmate.view.element;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.sql.SQLException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;
import schoolmate.view.PencilMain;

public class FrameToolBar extends JToolBar implements ActionListener{
	private PencilMain pencilMain;
	public JButton poolBtn,addBtn,importBtn,remarks,labels;
	public ImageIcon pollImg,importImg,addImg,remarkImg,labelImg;
	public FrameToolBar(PencilMain main){
		pencilMain = main;
		pollImg = new ImageIcon("resource/img/pool.png");
		importImg = new ImageIcon("resource/img/import.png");
		addImg = new ImageIcon("resource/img/add.png");
		remarkImg = new ImageIcon("resource/img/remark.png");
		labelImg = new ImageIcon("resource/img/label.png");
		poolBtn = new JButton("信息汇总  ");
		poolBtn.setIcon(pollImg);
		addBtn = new JButton("添加信息  ");
		addBtn.setIcon(addImg);
		importBtn = new JButton("导入Excel  ");
		importBtn.setIcon(importImg);
		remarks = new JButton("修改备注  ");
		remarks.setIcon(remarkImg);
		labels = new JButton("修改字段");
		labels.setIcon(labelImg);
		labels.addActionListener(this);
		addBtn.addActionListener(this);
		remarks.addActionListener(this);
		poolBtn.addActionListener(this);
		importBtn.addActionListener(this);
		add(poolBtn);
		add(addBtn);
		add(importBtn);
		if(pencilMain.nowUser.u_role>=2){
			add(remarks);
			add(labels);
		}
	}
	
	public void MenuControl(boolean state){
		addBtn.setEnabled(state);
		remarks.setEnabled(state);
		labels.setEnabled(state);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		JButton btn = (JButton)e.getSource();
		if(btn==addBtn){
			pencilMain.studentDetail(null,2);
		}else if(btn==poolBtn){
			try {
				pencilMain.collectPanel();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}else if(btn==importBtn){
			try {
				pencilMain.importExl();
			} catch (PropertyVetoException e1) {
				e1.printStackTrace();
			}
		}else if(btn==remarks){
			pencilMain.Remarks();
		}else if(btn==labels){
			try {
				pencilMain.labelManage();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}
}