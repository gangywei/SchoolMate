package schoolmate.view.element.tablePanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.sql.SQLException;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import schoolmate.control.tableModel.WorkModel;
import schoolmate.database.StudentLog;
import schoolmate.database.UserLog;
import schoolmate.database.WorkLog;
import schoolmate.model.DBConnect;
import schoolmate.model.Work;
import schoolmate.view.PencilMain;
import schoolmate.view.StudentDetailFrame;
import schoolmate.view.element.MyTable;
import schoolmate.view.element.TabelPanel;

public class WorkDetailPanel extends TabelPanel implements ActionListener{
	public int sId;
	private StudentDetailFrame detail;
	public WorkDetailPanel(PencilMain pencil,int sId,StudentDetailFrame detail){
		super(pencil);
		this.sId = sId;
		this.detail = detail;
		init();
	}
	public WorkDetailPanel(PencilMain pencil,StudentDetailFrame detail){
		super(pencil);
		this.detail = detail;
		init();
	}
	
	public void getDetail(int sId){
		this.sId = sId;
		updateTabel();
	}
	
	public void initTable(){
		model = new WorkModel(data);
		alterItem.addActionListener(this);
		deleteItem.addActionListener(this);
		updateTabel();
	}
	
	public void updateTabel(){
		try {
			data = UserLog.dao("select wl_id,s_id,nation,province,city,s_work,s_worktitle,s_workspace,s_workphone from worklog where s_id = "+sId);
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
		int wlId = (int)model.getCell(nowSelect, 0);
		if(item==deleteItem){
			if(PencilMain.dbControl){
				int res =JOptionPane.showConfirmDialog(this,"删除这条记录的信息吗？","删除信息提示",JOptionPane.YES_NO_OPTION);
				if(res==0){
					boolean result = true;
					try {
						Work work = new Work("", "", "", "", "", "", "");
						work.s_id = sId;
						work.wl_id = wlId;
						WorkLog.deleteWork(work, null);
						if(nowSelect==model.data.size()-1){	//更新校友记录
							if(nowSelect==0) {
								StudentLog.updateWork(work, DBConnect.getStmt());
							}else
								StudentLog.updateWork(WorkLog.searchWorkOne(sId), DBConnect.getStmt());	
						}
					} catch (SQLException e1) {
						result = false;
						e1.printStackTrace();
					}
					if(result){
						updateTabel();
						pencil.collectDataFrame.updateTabel(null,"",true,true);
					}else
						JOptionPane.showMessageDialog(null, "删除记录失败！");
				}
			}else{
				JOptionPane.showMessageDialog(null, "正在导入数据，不允许该操作");
			}
		}else if(item==alterItem){
			if(nowSelect==model.data.size()-1){
				pencil.addStudentLog(2,sId,detail,wlId);
			}else
				pencil.addStudentLog(0,sId,detail,wlId);
		}
	}
}
