package schoolmate.view.element;  
  
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;  
import java.awt.event.ActionEvent;  
import java.awt.event.ActionListener;  
import java.util.ArrayList;  
import java.util.List;  
  
import javax.swing.JButton;  
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JPanel;  
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;  
  
/** 
 *  
 * @author bugu 
 *  
 */  
public class MulitPopup extends JPopupMenu {  
  
    private List<ActionListener> listeners = new ArrayList<ActionListener>();  
    private Object[] values;    
    private JScrollPane scroll;
    private JList<Object> showList;
    private Object[] defaultValues;    
    private List<JCheckBox> checkBoxList = new ArrayList<JCheckBox>();   
    private JButton commitButton ;   
    private JButton cancelButton;   
    public static  String COMMIT_EVENT = "commit";   
    public static  String CANCEL_EVENT = "cancel";   
    public MulitPopup(Object[] value , Object[] defaultValue) {  
        super(); 
        listeners.clear();
        values = value;  
        defaultValues = defaultValue;  
        checkBoxList.clear();
        initComponent();  
    }  
  
    public void addActionListener(ActionListener listener) {  
        if (!listeners.contains(listener))  
            listeners.add(listener);  
    }  
  
    public void removeActionListener(ActionListener listener) {  
        if (listeners.contains(listener))  
            listeners.remove(listener);  
    }  
  
    private void initComponent() {  
    	
        JPanel checkboxPane = new JPanel();  
        JPanel buttonPane = new JPanel();  
        this.setLayout(new BorderLayout());  
      	if(values == null){
      		return;
      	}
        for(Object v : values){  
            JCheckBox temp = new JCheckBox(v.toString() , selected(v));  
            checkBoxList.add(temp);  
        }  
        checkboxPane.setLayout(new GridLayout(checkBoxList.size() , 1 ,3, 3));  
        for(JCheckBox box : checkBoxList){  
            checkboxPane.add(box);  
        }  
        
        commitButton = new JButton("选    择");  
        commitButton.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));  
        commitButton.setForeground(Color.white); 
        commitButton.addActionListener(new ActionListener(){  
            public void actionPerformed(ActionEvent e) {  
                commit();  
            }   
        });  
          
        cancelButton = new JButton("撤    销");
        cancelButton.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));  
        cancelButton.setForeground(Color.white);  
        cancelButton.addActionListener(new ActionListener(){  
            public void actionPerformed(ActionEvent e) {  
                cancel();  
            }    
        });  
          
        buttonPane.add(commitButton);  
        buttonPane.add(cancelButton);  
        scroll = new JScrollPane(checkboxPane);
        this.add(scroll , BorderLayout.CENTER);  
        this.add(buttonPane , BorderLayout.SOUTH);  
        setPopupSize(200,350);  
    }  
  
    private boolean selected(Object v) {  
        for(Object dv : defaultValues){  
            if( dv.equals(v) ){  
                return true;  
            }  
        }  
        return false;  
    }  
  
    protected void fireActionPerformed(ActionEvent e) {  
        for (ActionListener l : listeners) {  
            l.actionPerformed(e);  
        }  
    }  
      
    public Object[] getSelectedValues(){  
        List<Object> selectedValues = new ArrayList<Object>();  
        for(int i = 0 ; i < checkBoxList.size() ; i++){  
            if(checkBoxList.get(i).isSelected())  
                selectedValues.add(values[i]);  
        }  
        return selectedValues.toArray(new Object[selectedValues.size()]);  
    }  
  
    public void setDefaultValue(Object[] defaultValue) {  
        defaultValues = defaultValue;    
    }  
      
    public void commit(){  
        fireActionPerformed(new ActionEvent(this, 0, COMMIT_EVENT));  
    }  
      
    public void cancel(){  
        fireActionPerformed(new ActionEvent(this, 0, CANCEL_EVENT));  
    }  
  
}  
