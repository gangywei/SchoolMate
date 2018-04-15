package schoolmate.control.tableModel;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

import schoolmate.control.BaseModel;

public class UserModel extends BaseModel{
	private static String[] nowColumn = {"姓名","账号","所属学院","身份"};//4个字段
	public UserModel(Vector<Object[]> data){
		super(nowColumn, data);
	}

	public Object getValueAt(int row, int col) {
		if(col==3)
			return setrole((int)data.elementAt(row)[col+1]);
		else
			return data.elementAt(row)[col+1];	//隐藏数据库中的ID字段。
	}

	public String setrole(int role){
		if(role==1){
			return "普通用户";
		}else if(role==2){
			return "学院管理者";
		}else if(role==3){
			return "系统管理者";
		}
		return "";
	}
}
