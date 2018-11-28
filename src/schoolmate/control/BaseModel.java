package schoolmate.control;

import java.util.Vector;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

public class BaseModel  extends DefaultTableModel implements TableModelListener{
	public String[] nowColumn;
	public Vector<Object[]> data;
	
	public BaseModel(String[] nowColumn,Vector<Object[]> data){
		this.nowColumn = nowColumn;
		this.setData(data);
	}
	
	public BaseModel(String[] nowColumn){
		this.nowColumn = nowColumn;
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
		return data.elementAt(row)[col];
	}
	
	public Object getCell(int row,int col){
		return data.elementAt(row)[col];
	}
	
	public void addRow(String[] temp){
		data.add(temp);
		this.setRowCount(data.size());
	}
	
	public void remove(int index){
		data.remove(index);
		this.setRowCount(data.size());
	}
	
	public void deleteSelect(){
		for(int i=data.size()-1;i>=0;i--){
			if((Boolean)data.elementAt(i)[0]){
				removeRow(i);
			}
		}
	}
	
	public void setCell(int row,int col,Object obj){
		data.elementAt(row)[col] = obj;
	}
	
	public Class getColumnClass(int c) {
		return getValueAt(0, c).getClass();
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

	@Override
	public void tableChanged(TableModelEvent e) {
		
	} 
}
