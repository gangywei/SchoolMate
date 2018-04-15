package schoolmate.view.element.tablePanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import schoolmate.control.tableModel.UserModel;
import schoolmate.database.UserLog;
import schoolmate.view.CollectDataFrame;
import schoolmate.view.PencilMain;
import schoolmate.view.element.MyTable;
import schoolmate.view.element.TabelPanel;

public class AllUserPanel extends TabelPanel implements ActionListener{
	public String role = "";
	public AllUserPanel(PencilMain pencil){
		super(pencil);
		init();
	}
	
	public void initTable(){
		alterItem.addActionListener(this);
		deleteItem.addActionListener(this);
		model = new UserModel(data);
		updateTabel();
	}
	
	public void updateTabel(){
		try {
			if(PencilMain.nowUser.u_role==3)
				role = "where u_role<="+3;
			else
				role = "where u_role<"+2+";";
			data = UserLog.dao("select u_id, u_name ,u_count ,faculty,u_role from user "+role);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		model.setData(data);
		table = new MyTable(model);
		this.updateUI();
	}
	
	public void actionPerformed(ActionEvent e) {
		if(nowSelect==-1){
			JOptionPane.showMessageDialog(null, "请选择需要修改的行");
			return;
		}
		JMenuItem item = (JMenuItem) e.getSource();
		int sNo = (int)model.getCell(nowSelect, 0);
		if(item==deleteItem){
			if(PencilMain.dbControl){
				int res =JOptionPane.showConfirmDialog(this,"删除这条记录的所有信息","删除信息提示",JOptionPane.YES_NO_OPTION);
				if(res==0){
					boolean result = UserLog.deleteUser(sNo);
					if(result){
						updateTabel();
						pencil.collectDataFrame.refeshBtn.doClick();
					}else
						JOptionPane.showMessageDialog(null, "删除用户失败！");
				}
			}else{
				JOptionPane.showMessageDialog(null, "正在导入数据，不允许该操作");
			}
		}else if(item==alterItem){
			pencil.addUser(this,sNo);
		}
	}
}
