package schoolmate.control.tableModel;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

import schoolmate.control.BaseModel;

public class WorkModel extends BaseModel{
	private static String[] Column = {"工作国家","工作省份","工作市区","职务","职称","工作单位"};//5个字段
	
	public WorkModel(Vector<Object[]> data){
		super(Column, data);
	}
	
	public Object getValueAt(int row, int col) {
		return data.elementAt(row)[col+2];	//隐藏数据库中的ID字段。
	}
}
