package schoolmate.view.element;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;

public class MyTextField extends JTextField implements FocusListener{
	private int state = 0;	//1->输入了内容	0->提示
	private String showText;
	private MyPopupMenu popupMenu;
	private List<Object> model;
	private boolean isCaretListener = false;
	public MyTextField(String text,int column,List<Object> model){
		super(text,column);
		showText = text;
		setForeground(Color.GRAY);
		addFocusListener(this);
		if(model!=null){
			this.model = model;
			popupMenu = new MyPopupMenu();
			addCaretListener(new MyCaretListener());
		}
	}
	//getText方法的重写
	public String getText(){
		if(this.state==1){
			return super.getText();
		}
		return "";
	}
	//输入框修改事件
	public void setText(String str){
		if(str!=showText){
			state = 1;
			setForeground(Color.BLACK);
		}
		super.setText(str);
	}
	//鼠标点击事件
    public void focusGained(FocusEvent e) {
        if(state==0){
        	state = 1;
            this.setText("");
            setForeground(Color.BLACK);
        }
        isCaretListener = true;
    }
    //失去焦点事件
    public void focusLost(FocusEvent e) {
    	isCaretListener = false;
        String temp = getText();
        if(temp.equals("")){
            setForeground(Color.GRAY);
            this.setText(showText);
            state = 0;
        }else{
        	this.setText(temp);
        }
    }
    //显示事件
    public void popupList(String key) {
		int count = popupMenu.updataPopupMenu(key);
		int hight = this.getHeight()*count;
		if(count>0){
				popupMenu.setPopupSize(this.getWidth(),100);
			if(isShowing()){
				popupMenu.show(this, 0,this.getHeight());
				if(isCaretListener)
					requestFocus();
			}
		}else{
			popupMenu.setVisible(false);
		}
	}
    //输入框里的内容发生了变化
    private class MyCaretListener implements CaretListener {
		int index = 0;
		public void caretUpdate(CaretEvent e) {
			if(isCaretListener&&index!=e.getDot())
			{
				try {
					popupList(getText(0, e.getDot()));
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}
				index = e.getDot();
			}			
		}
	}
    public void setTextValue(String str){
		this.setText(str);
	}
	private void setVisibleFalseAndLostFocus(){
		popupMenu.setVisible(false);
		isCaretListener = false;
	}
    //要显示的弹出菜单
    private class MyPopupMenu extends JPopupMenu{
		private JList<Object> showList;
		private JScrollPane scroll;
		private List<Object> listModel;	//Jlist显示的数据
		public MyPopupMenu(){
			super();
			showList = new JList<Object>();
			listModel = new ArrayList<Object>();
			scroll = new JScrollPane(showList);
			showList.addListSelectionListener(new MyListSelectListener());
			showList.addMouseListener(new MouseAdapter(){
				 public void mouseClicked(MouseEvent e){
				  	   if (e.getClickCount()==1){
				  		   setVisibleFalseAndLostFocus();
				  	   }	
				 }
			});
			this.setLayout(new BorderLayout());
			this.add(scroll,"Center");
			this.setFocusable(false);
		}
		//更新文本框选择的元素
		public int updataPopupMenu(String key){		
			if(model==null){
				return 0;
			}
			int i = 0;		
			listModel.clear();
			for(Object obj: model){
				if(obj.toString().startsWith(key)){	//测试此字符串是否以指定的前缀开头。
					listModel.add(obj);
				}
			}
			showList.setListData(listModel.toArray());
			return listModel.size();
		}
		//得到Jlist选择的内容
		public Object getSelectObj(){
			return showList.getSelectedValue();
		}		
	}
    //更新文本框的数据
    private class MyListSelectListener implements ListSelectionListener{
		public void valueChanged(ListSelectionEvent e) {
			Object o  = popupMenu.getSelectObj();
			if(o!=null){
				setTextValue(o.toString());
			}			
		}		
	}
}
