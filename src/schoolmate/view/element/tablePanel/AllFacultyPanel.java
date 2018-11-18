package schoolmate.view.element.tablePanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import schoolmate.control.BaseModel;
import schoolmate.database.FacultyLog;
import schoolmate.database.UserLog;
import schoolmate.view.CollectDataFrame;
import schoolmate.view.PencilMain;
import schoolmate.view.element.MyTable;
import schoolmate.view.element.TabelPanel;

public class AllFacultyPanel extends TabelPanel implements ActionListener{
	private static String[] Column = {"学院"};//5个字段
	
	public AllFacultyPanel(CollectDataFrame collectFrame) {
		super(collectFrame);
	}

	public void initTable(){
		try {
			data = UserLog.dao("select f_name from faculty");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		model = new BaseModel(Column,data);
		table = new MyTable(model);
		alterItem.addActionListener(this);
		deleteItem.addActionListener(this);
	}
	
	public void updateTabel(){
		try {
			data = UserLog.dao("select f_name from faculty");
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
		String sName = (String) model.getCell(nowSelect, 0);
		String[] text = {sName};
		JMenuItem item = (JMenuItem) e.getSource();
		if(item==deleteItem){
			if(PencilMain.dbControl){
				int res =JOptionPane.showConfirmDialog(this,"删除该字段并删除该字段的所有数据，请先做好excel备份","删除信息提示",JOptionPane.YES_NO_OPTION);
				if(res==0){
					boolean result = FacultyLog.deleteFaculty(sName);
					if(result)
						try {
							updateTabel();
							collectFrame.updatePageNum();
							collectFrame.updateTabel(null, null, true, true, -1);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					else
						JOptionPane.showMessageDialog(null, "删除该字段失败！");
				}
			}else{
				JOptionPane.showMessageDialog(null, "正在导入数据，不允许该操作");
			}
		}else if(item==alterItem){
			if(PencilMain.dbControl){
				collectFrame.pencil.alterLabel(this,model.nowColumn,text,nowSelect,3);
			}else{
				JOptionPane.showMessageDialog(null, "正在导入数据，不允许该操作");
			}
		}
	}
}