package schoolmate.control;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public class WorkModel extends DefaultTableModel{
	private static String[] nowColumn = {"工作国家","工作省份","工作市区","职务","职称","工作单位"};//5个字段
	public Vector<Object[]> data = new Vector<Object[]>();
	public Vector<Object[]> SelectData = new Vector<Object[]>();
	public WorkModel(Vector<Object[]> data){
		this.setData(data);
	}
	
	public int getColumnCount() {
		return nowColumn.length;
	}

	public String getColumnName(int col) {
		return nowColumn[col];
	}
	
	public void setData(Vector<Object[]> data){
		this.data = data;
		this.setRowCount(data.size());
	}
	
	public Object getValueAt(int row, int col) {
		return data.elementAt(row)[col+1];	//隐藏数据库中的ID字段。
	}
	
	public Object getCell(int row,int col){
		return data.elementAt(row)[col];
	}

	public Class getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}
	//返回以逗号分隔的学生id 信息
	public String getSelect(){
		String condition = "";
		for(int i=0;i<data.size();i++){
			if((Boolean)data.elementAt(i)[0]){
				condition+=("'"+data.elementAt(i)[2]+"',");
			}
		}
		if(condition.equals(""))
			return null;
		else
			return condition.substring(0,condition.length()-1);
	}
	
	public void addRow(String[] temp){
		data.add(temp);
		this.setRowCount(data.size());
	}
	
	public int SelectCount(){
		return SelectData.size();
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
            return true;
        } else {
            return false;
        }
    }
    
    public void setValueAt(Object value, int row, int col) {
    	data.elementAt(row)[col] = value;
    } 
}
