package schoolmate.view.element;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.sql.SQLException;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import org.apache.poi.ss.usermodel.DataFormatter;

import schoolmate.database.FullsearchLog;
import schoolmate.database.StudentLog;
import schoolmate.view.CollectDataFrame;
import schoolmate.view.PencilMain;

public class TableLeftMouse extends JPopupMenu implements ActionListener{
	private JMenuItem detailItem,updateItem,deleteItem;
	private CollectDataFrame panel;
	public TableLeftMouse(CollectDataFrame panel){
		detailItem = new JMenuItem("详细信息");	
		updateItem = new JMenuItem("修改信息");			
		deleteItem = new JMenuItem("删除信息");	
		detailItem.addActionListener(this);
		updateItem.addActionListener(this);
		deleteItem.addActionListener(this);
		add(detailItem);
		add(updateItem);
		add(deleteItem);
		this.panel = panel;
	}
	public void actionPerformed(ActionEvent e) {
		int length = PencilMain.COLUM;
		String student[] = new String[length];
		int row = panel.nowSelect;	//得到当前选择的行数
		if(row==-1){
			JOptionPane.showMessageDialog(null, "请选择需要修改的行");
			return;
		}
		JMenuItem item = (JMenuItem) e.getSource();
		if(item==updateItem){
			if(PencilMain.dbControl){
				for(int i=1;i<length;i++){
					student[i] = panel.studentModel.getCell(row, i).toString();
				}
				panel.pencil.studentDetail(student,1);
			}else{
				JOptionPane.showMessageDialog(null, "正在导入数据，不允许该操作");
			}
		}else if(item==deleteItem){
			if(PencilMain.dbControl){
				int sNo = (int)panel.studentModel.getCell(row, 1);
				int res =JOptionPane.showConfirmDialog(panel,"删除这条记录的所有信息","删除信息提示",JOptionPane.YES_NO_OPTION);
				if(res==0){
					boolean result = StudentLog.deleteStudent(sNo);
					if(result){
						panel.updateTabel(null,null,panel.searchType);
					}else
						JOptionPane.showMessageDialog(null, "删除学生失败！");
				}
			}else{
				JOptionPane.showMessageDialog(null, "正在导入数据，不允许该操作");
			}
		}else if(item==detailItem){
			if(PencilMain.dbControl){
				if(PencilMain.dbControl){
					DataFormatter formatter = new DataFormatter();
					for(int i=1;i<length;i++){
						if(panel.studentModel.getCell(row, i)==null)
							student[i] = "";	//防止数据库出现为空的字段
						else
							student[i] = panel.studentModel.getCell(row, i).toString();
					}
					panel.pencil.studentDetail(student,0);
				}else{
					JOptionPane.showMessageDialog(null, "正在导入数据，不允许该操作");
				}
			}else{
				JOptionPane.showMessageDialog(null, "正在导入数据，不允许该操作");
			}
		}
	}
	
}
