package schoolmate.control.tableModel;

import java.util.Vector;

import schoolmate.control.BaseModel;

public class FacultyModel extends BaseModel{
	private static String[] Column = {"学院"};//5个字段
	
	public FacultyModel(Vector<Object[]> data){
		super(Column, data);
	}
	
	public Object getValueAt(int row, int col) {
		return data.elementAt(row)[col+1];	//隐藏数据库中的ID字段。
	}
}