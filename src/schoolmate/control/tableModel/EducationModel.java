package schoolmate.control.tableModel;

import java.util.Vector;

import schoolmate.control.BaseModel;

public class EducationModel extends BaseModel{
private static String[] Column = {"学号","学院","专业","班级","学历","入学年份","毕业年份"};
	
	public EducationModel(Vector<Object[]> data){
		super(Column, data);
	}
	
	public Object getValueAt(int row, int col) {
		return data.elementAt(row)[col+2];	//隐藏数据库中的ID字段。
	}
}
