package schoolmate.view.element;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;

public class MyTextField extends JTextField implements FocusListener{
	private int state = 0;	//1->输入了内容 0->提示  2->正在输入内容
	private int index = -1;	//焦点的坐标
	private String showText;
	private MyPopupMenu popupMenu;
	private List<Object> model;
	public MyTextField(String text,int column){
		super(text,column);
		showText = text;
		setForeground(Color.GRAY);
		addFocusListener(this);
	}
	public void setMenu(List<Object> model){
		if(model!=null){
			this.model = model; 
			popupMenu = new MyPopupMenu();
			addCaretListener(new MyCaretListener(this));
		}
	}
	//getText方法的重写
	public String getText(){
		if(this.state>=1){
			return super.getText();
		}
		return "";
	}
	//设置提示信息
	public void setShowText(String value){
		showText = value;
		super.setText(value);
		setForeground(Color.GRAY);
	}
	//输入框修改事件
	public void setText(String str){
		if(str!=showText){
			state = 1;
		}
		super.setText(str);
		setForeground(Color.BLACK);
	}
	//鼠标点击事件
    public void focusGained(FocusEvent e) {
    	if(state==0) {
    		super.setText("");
    		setForeground(Color.BLACK);
    	}	
        state = 2;
    }
    //失去焦点事件
    public void focusLost(FocusEvent e) {
    	String temp = getText();
    	if(temp.equals("")){
        	state = 0;
        }else{
        	state = 1;
        } 
    }
    //显示列表事件
    public void popupList(String key) {
		int count = popupMenu.updataPopupMenu(key);
		if(count>0){
			popupMenu.setPopupSize(this.getWidth(),150);
			if(isShowing()){
				popupMenu.show(this, 0,this.getHeight());
				requestFocus();
			}
		}else{
			popupMenu.setVisible(false);
		}
	}
    //输入框里的内容发生了变化
    private class MyCaretListener implements CaretListener {
    	MyTextField textFile;
    	public MyCaretListener(MyTextField textFile){
    		this.textFile = textFile;
    	}
		public void caretUpdate(CaretEvent e) {
			if(state!=1&&index==-1){
				try {
					popupList(getText(0, e.getDot()));
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}
				index = e.getDot();
			}
		}
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
				  		   popupMenu.setVisible(false);
				  		   state = 1;
				  	   }	
				 }
			});
			this.setLayout(new BorderLayout());
			this.add(scroll,"Center");
			this.setFocusable(false);
		}
		//更新文本框选择的元素
		public int updataPopupMenu(String key){		
			if(model==null)
				return 0;
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
				setText(o.toString());
			}			
		}		
	}
}
