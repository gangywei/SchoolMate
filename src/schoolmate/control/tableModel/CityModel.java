package schoolmate.control.tableModel;

import java.util.Vector;

import schoolmate.control.BaseModel;

public class CityModel extends BaseModel{
	private static String[] Column = {"国家","省份","市区"};//5个字段
	
	public CityModel(Vector<Object[]> data){
		super(Column, data);
	}
	
	public Object getValueAt(int row, int col) {
		return data.elementAt(row)[col];	//隐藏数据库中的ID字段。
	}
}
