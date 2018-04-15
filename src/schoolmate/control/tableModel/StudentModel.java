package schoolmate.control.tableModel;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import schoolmate.database.StudentLog;

public class StudentModel extends DefaultTableModel implements TableModelListener{
	private static String[] dbCol = {"选择框","学号","姓名","性别","出生日期","身份证号","籍贯","学院","专业","班级",
			"学历","入学年份","毕业年份","工作国家","工作省份","工作市区","工作单位","职称","职务","办公电话",
			"家用电话","手机","手机2","通讯地址","邮编","E-mail","QQ","微信","","","","",""};	//33个字段。 隐藏了一个id字段
	public static String[] excelCol = {"学号","姓名","性别","出生日期","身份证号","籍贯","学院","专业","班级","学历",
			"入学年份","毕业年份","工作国家","工作省份","工作市区","工作单位","职称","职务","办公电话","家用电话",
			"手机","手机2","通讯地址","邮编","E-mail","QQ","微信","","","","",""};	//32个字段
	public static String[] errorCol = {"错误信息","学号","姓名","性别","出生日期","身份证号","籍贯","学院","专业","班级","学历",
			"入学年份","毕业年份","工作国家","工作省份","工作市区","工作单位","职称","职务","办公电话","家用电话",
			"手机","手机2","通讯地址","邮编","E-mail","QQ","微信","","","","",""};	//33个字段
	public String nowColumn[];	//当前使用的表格信息
	public String[] remarks = new String[5];	//数据库学生表备注字段
	public int type;	//判断使用哪种表头=>影响到数据的导出和数据的显示 0=>数据库(导出学生数据和发送邮件导出信息)  1=>Excel(只用于导入Excel)
	public Vector<Object[]> data = new Vector<Object[]>();
	public StudentModel(Vector<Object[]> data,int type){
		updateRemark();
		this.type = type;
		if(type==0)
			nowColumn = dbCol;
		else if(type==1)
			nowColumn = excelCol;
		else if(type==2)
			nowColumn = errorCol;
		this.setData(data);
	}
	//更新备注字段
	public void updateRemark(){
		try {
			remarks = StudentLog.SelectRemarks();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		for(int i = 5;i>0;i--){
			dbCol[dbCol.length-i] = remarks[5-i];
			excelCol[excelCol.length-i] = remarks[5-i];
			errorCol[errorCol.length-i] = remarks[5-i];
		}
	}
	
	public StudentModel(int type){
		this.type = type;
		if(type==0)
			nowColumn = dbCol;
		else if(type==1)
			nowColumn = excelCol;
		else if(type==2) 
			nowColumn = errorCol;
	}
	public int getColumnCount() {
		return nowColumn.length;
	}
	/*
	 * inter:删除tableModle中的所有数据
	 */
	public void removeAll(){
		this.data.clear();
		this.setRowCount(0);
	}
	/* j=删除几行
	 * inter:从后向前删除tableModle的数据
	 */
	public void removeLast(int j){
		int length = data.size();
		for(int i=1;i<=j;i++){
			data.remove(length-i);
		}
		this.setRowCount(length-j);
	}
	public String getColumnName(int col) {
		return nowColumn[col];
	}
	/*
	 * inter:设置tableModle的数据，可能存在相同的学生，使用条件判断id是否存在，去除相同的学生
	 */
	public void setData(Vector<Object[]> data){
		String str = "";
		for(int i=0;i<data.size();i++){
			String temp = data.elementAt(i)[1].toString();
			if(str.indexOf(temp)==-1)
				str += temp+",";
			else{
				data.remove(i);
				i--;
			}
		}
		this.data = data;
		this.setRowCount(data.size());
	}
	public Object getValueAt(int row, int col) {
		if(this.getRowCount()>0)
			if(col>=1&&type==0)
				return data.elementAt(row)[col+1];	//隐藏数据库中的ID字段。
			else
				return data.elementAt(row)[col];
		return "";
	}
	
	public Object getCell(int row,int col){
		return data.elementAt(row)[col];
	}

	public Class getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}
	/*
	 * inter:返回选中的学生的id字符串，以逗号分隔。
	 */
	public String getSelect(){
		String condition = "";
		for(int i=0;i<data.size();i++){
			if((Boolean)data.elementAt(i)[0]){
				condition+=data.elementAt(i)[1]+",";
			}
		}
		if(condition.equals(""))
			return null;
		else
			return condition.substring(0,condition.length()-1);
	}
	
	public void addRow(Object[] temp){
		data.add(temp);
		this.setRowCount(data.size());
	}
	/*
	 * inter:返回选中学生的数量
	 */
	public int getSelectCount(){
		int totle = 0;
		for(int i=0;i<data.size();i++){
			if((Boolean)data.elementAt(i)[0]){
				totle++;
			}
		}
		return totle;
	}
	
	public void deleteSelect(){
		for(int i=data.size()-1;i>=0;i--){
			if((Boolean)data.elementAt(i)[0]){
				removeRow(i);
			}
		}
	}
	
	//对表格的修改操作
    public boolean isCellEditable(int row, int col) {
        if (col < 1) {
        	System.out.println(row);
            return true;
        } else {
            return false;
        }
    }
    
    public void setValueAt(Object value, int row, int col) {
    	data.elementAt(row)[col] = value;
    }

	@Override
	public void tableChanged(TableModelEvent e) {
		System.out.println(e);
	} 
}
