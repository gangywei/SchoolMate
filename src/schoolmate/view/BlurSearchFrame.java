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

import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;

public class BlurSearchFrame extends JInternalFrame implements ActionListener{
	public JPanel interPanel,contentPanel,bottomPanel;
	String str = new String(
			"<strong>模糊查询功能</strong><br/><hr>"
			+ "<p>支持对<strong>市区、学号、姓名、手机号、QQ、微信、职务、职称、邮箱</strong>、班级字段进行模<br/>糊查找，查询较慢，请稍等。</p>");
	JEditorPane editPane = new JEditorPane("text/html", str);
	JTextField instantInput = new JTextField(20);
	JLabel instantLabel = new JLabel("模糊查询字段");
	private JButton searchBtn = new JButton("分页查询");
	private JButton searchAllBtn = new JButton("查询所有");
	
	private CollectDataFrame collect;
	public BlurSearchFrame(CollectDataFrame collect){
		init();
		this.collect = collect;
	}
	
	public void init(){
		editPane.setEnabled(false);
		setClosable(true);//提供关闭按钮
    	setResizable(true);  //允许自由调整大小 
    	setIconifiable(true); //设置提供图标化按钮
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
	}

	public void actionPerformed(ActionEvent e) {
		String text = instantInput.getText().trim();
		JButton btn = (JButton)e.getSource();
		if(btn==searchBtn){
			collect.updateTabel(null,text);
		}else if(btn==searchAllBtn){
			if(text.equals("")){
				JOptionPane.showMessageDialog(this, "查询不可以为空");
				return;
			}
			collect.updateTabel(null,text);
		}
	}
	
	public void doDefaultCloseAction() {  
		collect.instant = "";
		instantInput.setText("");
	    dispose();
	}
}
