package schoolmate.view.element.tablePanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import schoolmate.control.tableModel.ProvinceModel;
import schoolmate.database.AddressLog;
import schoolmate.database.UserLog;
import schoolmate.view.CollectDataFrame;
import schoolmate.view.PencilMain;
import schoolmate.view.element.MyTable;
import schoolmate.view.element.TabelPanel;

public class AllProvincePanel extends TabelPanel implements ActionListener{
	public AllProvincePanel(CollectDataFrame collectFrame) {
		super(collectFrame);
	}

	public void initTable(){
		try {
			data = UserLog.dao("select n_name,p_name from province");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		model = new ProvinceModel(data);
		table = new MyTable(model);
		alterItem.addActionListener(this);
		deleteItem.addActionListener(this);
	}
	
	public void updateTabel(){
		try {
			data = UserLog.dao("select n_name,p_name from province");
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
		String sProvince = (String) model.getCell(nowSelect, 1);
		String sNation = (String) model.getCell(nowSelect, 0);
		String[] text = {sNation,sProvince};
		if(item==deleteItem){
			if(PencilMain.dbControl){
				int res =JOptionPane.showConfirmDialog(this,"删除该字段并删除该字段的所有数据，请先做好excel备份","删除信息提示",JOptionPane.YES_NO_OPTION);
				if(res==0){
					int result = AddressLog.deleteProvince(sProvince,sNation);
					if(result>=1)
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
				collectFrame.pencil.alterLabel(this,model.nowColumn,text,nowSelect,1);
			}else{
				JOptionPane.showMessageDialog(null, "正在导入数据，不允许该操作");
			}
		}
	}
}