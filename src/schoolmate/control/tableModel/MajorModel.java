package schoolmate.control.tableModel;

import java.util.Vector;

import schoolmate.control.BaseModel;

public class MajorModel extends BaseModel{
	private static String[] Column = {"学院","专业"};//5个字段
	
	public MajorModel(Vector<Object[]> data){
		super(Column, data);
	}
}
