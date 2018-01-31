package schoolmate.view.element;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;

public class MyCheckList extends JScrollPane{
	public String[] listData;
	private int listLength;
	private JList listCheckBox;	//复选框
    private JList listDescription;	//描述信息
    private CheckBoxItem[] checkBoxs;
	public MyCheckList(String[] data){
		listData = data;
		listLength = data.length;
		checkBoxs = buildCheckBoxItems(listData.length);
		listCheckBox = new JList(checkBoxs);
	    listDescription = new JList(listData);
	    listDescription.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listDescription.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                if (me.getClickCount() != 2) {
                    return;
                }
                int selectedIndex = listDescription.locationToIndex(me.getPoint());
                if (selectedIndex < 0) {
                    return;
                }
                CheckBoxItem item = (CheckBoxItem) listCheckBox.getModel().getElementAt(selectedIndex);
                item.setChecked(!item.isChecked());
                listCheckBox.repaint();
            }
        });
        listCheckBox.setCellRenderer(new CheckBoxRenderer());
        listCheckBox.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listCheckBox.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                int selectedIndex = listCheckBox.locationToIndex(me.getPoint());
                if (selectedIndex < 0) {
                    return;
                }
                CheckBoxItem item = (CheckBoxItem) listCheckBox.getModel().getElementAt(selectedIndex);
                item.setChecked(!item.isChecked());
                listDescription.setSelectedIndex(selectedIndex);
                listCheckBox.repaint();
            }
        });
	    
        setRowHeaderView(listCheckBox);
        setViewportView(listDescription);
        listDescription.setFixedCellHeight(25);
        listCheckBox.setFixedCellWidth(25);
        listCheckBox.setFixedCellHeight(listDescription.getFixedCellHeight());
	}
	
	public String[] getSelectVal(){
		ArrayList<String> res = new ArrayList<>();
		for(int i=0;i<listLength;i++){
			if(checkBoxs[i].isChecked())
				res.add(listData[i]);
		}
		int count = res.size();
		String[] result = new String[count];
		for(int i=0;i<count;i++){
			result[i] = res.get(i);
		}
		return result;
	}
		
	private CheckBoxItem[] buildCheckBoxItems(int totalItems) {
        CheckBoxItem[] checkboxItems = new CheckBoxItem[totalItems];
        for (int counter = 0; counter < totalItems; counter++) {
            checkboxItems[counter] = new CheckBoxItem();
        }
        return checkboxItems;
    }
	
	class CheckBoxItem {
        private boolean isChecked;
        public CheckBoxItem() {
            isChecked = false;
        }
        public boolean isChecked() {
            return isChecked;
        }
        public void setChecked(boolean value) {
            isChecked = value;
        }
    }
	
	/* Inner class that renders JCheckBox to JList*/
    class CheckBoxRenderer extends JCheckBox implements ListCellRenderer {
        public CheckBoxRenderer() {
            setBackground(UIManager.getColor("List.textBackground"));
            setForeground(UIManager.getColor("List.textForeground"));
        }
        public Component getListCellRendererComponent(JList listBox, Object obj, int currentindex,
                boolean isChecked, boolean hasFocus) {
            setSelected(((CheckBoxItem) obj).isChecked());
            return this;
        }
    }
}
