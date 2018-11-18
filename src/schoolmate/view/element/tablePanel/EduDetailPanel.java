package schoolmate.view.element.tablePanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.sql.SQLException;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import schoolmate.control.tableModel.EducationModel;
import schoolmate.database.EducationLog;
import schoolmate.database.UserLog;
import schoolmate.view.PencilMain;
import schoolmate.view.StudentDetailFrame;
import schoolmate.view.element.MyTable;
import schoolmate.view.element.TabelPanel;

public class EduDetailPanel extends TabelPanel implements ActionListener{
	public int sId;
	private StudentDetailFrame detail;
	public EduDetailPanel(PencilMain pencil,int sId,StudentDetailFrame detail){
		super(pencil);
		this.sId = sId;
		this.detail = detail;
		init();
	}
	
	public void getDetail(int sId){
		this.sId = sId;
		updateTabel();
	}
	
	public void initTable(){
		alterItem.addActionListener(this);
		deleteItem.addActionListener(this);
		model = new EducationModel(data);
		updateTabel();
	}
	
	public void updateTabel(){
		try {
			data = UserLog.dao("select e_id,s_id,s_no,s_faculty,s_major,s_class,s_education,s_enter,s_graduate from education where s_id = "+sId);
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
		int eId = (int)model.getCell(nowSelect, 0);
		if(item==deleteItem){
			if(PencilMain.dbControl){
				if(nowSelect==model.data.size()-1&&nowSelect==0){
					JOptionPane.showMessageDialog(this,"校友教育记录不允许为空，可以进行修改操作");
					return;
				}
				int res =JOptionPane.showConfirmDialog(this,"删除这条记录的所有信息","删除信息提示",JOptionPane.YES_NO_OPTION);
				if(res==0){
					boolean result = true;
					try {
						String old[] = {model.getCell(nowSelect, 2).toString(),(String)model.getCell(nowSelect, 5)};
						EducationLog.deleteEdu(null, eId, sId, old);
					} catch (SQLException e1) {
						result = false;
						e1.printStackTrace();
					}
					if(result){
						updateTabel();
						pencil.collectDataFrame.updateTabel(null,null,true,true,-1);
					}else
						JOptionPane.showMessageDialog(null, "删除记录失败！");
				}
			}else{
				JOptionPane.showMessageDialog(null, "正在导入数据，不允许该操作");
			}
		}else if(item==alterItem){
			pencil.addStudentLog(1,sId,detail,eId);
		}
	}
}

