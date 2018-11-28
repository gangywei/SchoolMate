package schoolmate.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;

public class BlurSearchFrame extends JInternalFrame implements ActionListener{
	static String oldSearch = "";
	public JPanel interPanel,contentPanel,bottomPanel;
	String str = new String(
			"<strong>模糊查询功能</strong><br/><hr>"
			+ "<p>支持对<strong>市区、学号、姓名、手机号、QQ、微信、职务、职称、邮箱、班级、工作单<br/>位字段</strong>进行模糊查找，查询较慢，请稍等。</p>");
	JEditorPane editPane = new JEditorPane("text/html", str);
	JTextField instantInput = new JTextField(20);
	JLabel instantLabel = new JLabel("模糊查询字段");
	private JButton searchBtn = new JButton("分页查询");
	private JButton searchAllBtn = new JButton("查询所有");
	private Thread copyThread;
	private CollectDataFrame collect;
	public BlurSearchFrame(CollectDataFrame collect){
		init();
		this.collect = collect;
	}
	
	public void init(){
		editPane.setEnabled(false);
		setClosable(true);//提供关闭按钮
    	setResizable(true);  //允许自由调整大小 
    	setTitle("模糊搜索功能");
    	interPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,20,5));
    	bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,10));
    	contentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,10,30));
    	interPanel.add(editPane);
    	
    	contentPanel.add(instantLabel);
    	contentPanel.add(instantInput);
    	
    	searchBtn.addActionListener(this);
    	searchBtn.setForeground(Color.WHITE);
    	searchBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));  
    	searchAllBtn.addActionListener(this);
    	searchAllBtn.setForeground(Color.WHITE);
    	searchAllBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));  
    	//bottomPanel.add(searchBtn);
    	bottomPanel.add(searchAllBtn);
    	
    	add(interPanel,BorderLayout.NORTH);
    	add(bottomPanel,BorderLayout.SOUTH);
    	add(contentPanel,BorderLayout.CENTER);
    	
    	setVisible(true);
    	setSize(600, 260);
		setLocation((PencilMain.showWidth-560)/2, 0);
		copyThread = new Thread(){
			public void run(){
				while(true){
					try {
						getClipboardString();
						sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		copyThread.start();
	}

	public void actionPerformed(ActionEvent e) {
		String text = instantInput.getText().trim();
		JButton btn = (JButton)e.getSource();
		if(btn==searchBtn){
			if(text.equals(""))
				collect.updateTabel(null,null,false,false,2);
			else
				collect.updateTabel(null,text,true,false,0);
		}else if(btn==searchAllBtn){
			if(text.equals("")){
				JOptionPane.showMessageDialog(this, "查询不可以为空");
				return;
			}
			collect.updateTabel(null,text,true,false,0);
		}
	}
	
	/**
     * 	从剪贴板中获取文本（粘贴） https://blog.csdn.net/xietansheng/article/details/70478266
     */
    public void getClipboardString() {
        // 获取系统剪贴板
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        // 获取剪贴板中的内容
        Transferable trans = clipboard.getContents(null);
        if (trans != null) {
            // 判断剪贴板中的内容是否支持文本
            if (trans.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                try {
                    // 获取剪贴板中的文本内容
                    String text = (String) trans.getTransferData(DataFlavor.stringFlavor);
                    if(!oldSearch.equals(text)){
                    	int res =JOptionPane.showConfirmDialog(this,"复制了数据 "+text+" 是否进行模糊查找？","任务提示",JOptionPane.YES_NO_OPTION);
                        if(res==0){
                            instantInput.setText(text);
                            searchBtn.doClick();
                        }
                        oldSearch = text;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
	
	public void doDefaultCloseAction() { 
		this.copyThread.stop();
		collect.instant = "";
		instantInput.setText("");
	    dispose();
	}
}
