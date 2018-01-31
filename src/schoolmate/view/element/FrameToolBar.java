package schoolmate.view.element;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.sql.SQLException;
import javax.swing.JButton;
import javax.swing.JToolBar;
import schoolmate.view.PencilMain;

public class FrameToolBar extends JToolBar implements ActionListener{
	private PencilMain pencilMain;
	public JButton poolBtn,addBtn,importBtn,backupBtn,renewBtn,remarks;
	public FrameToolBar(PencilMain main){
		pencilMain = main;
		poolBtn = new JButton("信息汇总  ");
		addBtn = new JButton("添加信息  ");
		importBtn = new JButton("导入Excel  ");
		backupBtn = new JButton("数据备份  ");
		renewBtn = new JButton("数据恢复  ");
		remarks = new JButton("修改备注  ");
		
		addBtn.addActionListener(this);
		remarks.addActionListener(this);
		poolBtn.addActionListener(this);
		renewBtn.addActionListener(this);
		importBtn.addActionListener(this);
		backupBtn.addActionListener(this);
		
		add(poolBtn);
		add(addBtn);
		add(importBtn);
		add(backupBtn);
		add(renewBtn);
		add(remarks);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		JButton btn = (JButton)e.getSource();
		if(btn==addBtn){
			try {
				pencilMain.addStudent(null);
			} catch (PropertyVetoException | SQLException e1) {
				e1.printStackTrace();
			}
		}else if(btn==importBtn){
			try {
				pencilMain.importExl();
			} catch (PropertyVetoException e1) {
				e1.printStackTrace();
			}
		}else if(btn==poolBtn){
			try {
				pencilMain.collectPanel();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}else if(btn==backupBtn){
			try {
				pencilMain.backupsData();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}else if(btn==renewBtn){
			try {
				pencilMain.reNewFrame();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}else if(btn==remarks){
			pencilMain.Remarks();
		}
	}
}