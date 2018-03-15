package schoolmate.control.tableModel;

import java.util.Vector;

import schoolmate.control.BaseModel;

public class ProvinceModel extends BaseModel{
	private static String[] Column = {"国家","省份"};//5个字段
	
	public ProvinceModel(Vector<Object[]> data){
		super(Column, data);
	}
}
