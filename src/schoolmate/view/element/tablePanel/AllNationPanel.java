package schoolmate.view.element.tablePanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import schoolmate.control.BaseModel;
import schoolmate.database.AddressLog;
import schoolmate.database.UserLog;
import schoolmate.view.CollectDataFrame;
import schoolmate.view.PencilMain;
import schoolmate.view.element.MyTable;
import schoolmate.view.element.TabelPanel;

public class AllNationPanel extends TabelPanel implements ActionListener{
	public static String[] Column = {"国家"};//5个字段
	public AllNationPanel(CollectDataFrame collectFrame) {
		super(collectFrame);
	}

	public void initTable(){
		try {
			data = UserLog.dao("select n_name from nation");
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
			data = UserLog.dao("select n_name from nation");
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
		String sName = (String) model.getCell(nowSelect, 0);
		String[] text = {sName};
		if(item==deleteItem){
			if(PencilMain.dbControl){
				int res =JOptionPane.showConfirmDialog(this,"删除该字段并删除该字段的所有数据，请先做好excel备份","删除信息提示",JOptionPane.YES_NO_OPTION);
				if(res==0){
					boolean result = AddressLog.deleteNation(sName);
					if(result)
						try {
							updateTabel();
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
				collectFrame.pencil.alterLabel(this,model.nowColumn,text,nowSelect,0);
			}else{
				JOptionPane.showMessageDialog(null, "正在导入数据，不允许该操作");
			}
		}
	}
}
