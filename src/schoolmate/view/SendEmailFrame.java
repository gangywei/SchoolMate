package schoolmate.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import org.apache.commons.codec.binary.Base64;
import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import schoolmate.control.EmailManager;
import schoolmate.control.Helper;
import schoolmate.control.StudentModel;

public class SendEmailFrame extends JInternalFrame implements ActionListener{
	private JPanel importPanel;
	private boolean threadCon;	//线程控制
	private String content;	//发送的主要内容
	private StudentModel studentModel = null;
	private int sendCount = 1;
	public String[] fileList = null;	//	选择的文件
	private JWebBrowser jWebBrowser;	//浏览器模型
    private JLabel nameLabel = new JLabel("邮件地址：");
    private JTextField nameInput = new JTextField(20);
    private JButton fileBtn = new JButton("附件");
    private JTextField fileInput = new JTextField(20);
    private JLabel interLabel = new JLabel("发送记录：");
    private JButton stopBtn = new JButton("暂  停");
    private JButton startBtn = new JButton("开  始"); 
    private String regEmail = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
    private EmailManager emailManage = new EmailManager();
    private final String URL = "file:///E:/assess/summer/index.html";
    private JProgressBar processBar = new JProgressBar();	//创建进度条 
    public SendEmailFrame(StudentModel studentModel){
    	this.studentModel = studentModel;
    	init();
    }
    public void init(){	
    	setClosable(true);	//提供关闭按钮
    	setMaximizable(true); //设置提供最大化按钮
    	stopBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));  
    	stopBtn.setForeground(Color.white);  
    	stopBtn.addActionListener(this);
    	startBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));  
    	startBtn.setForeground(Color.white);  
    	startBtn.addActionListener(this);
    	fileBtn.addActionListener(this);
    	importPanel = new JPanel();
    	GroupLayout layout = new GroupLayout(importPanel);
    	importPanel.setLayout(layout);
    	
    	processBar.setStringPainted(true);// 设置进度条上的字符串显示，false则不能显示  
	    processBar.setBackground(Color.GREEN);
    	
    	if(studentModel!=null){
    		nameInput.setEditable(false);
    		nameInput.setText("点击开始按钮，开始发送邮件");
    		sendCount = studentModel.getSelectCount();
    		processBar.setString("待发送的邮件数量 "+sendCount);
    	}else{
    		processBar.setString("输入想要发送的邮件地址，发送邮件");
    	}
    	fileInput.setEditable(false);
		//创建GroupLayout的水平连续组，，越先加入的ParallelGroup，优先级级别越高。几列
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		hGroup.addGap(20);//添加间隔
		hGroup.addGroup(layout.createParallelGroup().addComponent(nameLabel).addComponent(fileBtn)
				.addComponent(interLabel).addComponent(startBtn));
		hGroup.addGap(15);
		hGroup.addGroup(layout.createParallelGroup().addComponent(nameInput).addComponent(fileInput).addComponent(stopBtn)
				.addComponent(processBar));
		hGroup.addGap(20);
		layout.setHorizontalGroup(hGroup);//设置水平组
		//创建GroupLayout的垂直连续组，，越先加入的ParallelGroup，优先级级别越高。几行
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		vGroup.addGap(20);//添加间隔
		vGroup.addGroup(layout.createParallelGroup().addComponent(nameLabel).addComponent(nameInput));
		vGroup.addGap(20);
		vGroup.addGroup(layout.createParallelGroup().addComponent(fileBtn).addComponent(fileInput));
		vGroup.addGap(20);
		vGroup.addGroup(layout.createParallelGroup().addComponent(interLabel).addComponent(processBar));
		vGroup.addGap(20);
		vGroup.addGroup(layout.createParallelGroup().addComponent(startBtn).addComponent(stopBtn));
		vGroup.addGap(20);
		layout.setVerticalGroup(vGroup);//设置垂直组
		add(importPanel,BorderLayout.SOUTH);
		
		jWebBrowser = new JWebBrowser();  
        jWebBrowser.navigate(URL);  
        jWebBrowser.setPreferredSize(new Dimension(800,400));
        jWebBrowser.setBarsVisible(false);  
        jWebBrowser.setMenuBarVisible(false); 
        jWebBrowser.setButtonBarVisible(false);  
        jWebBrowser.setStatusBarVisible(false);
        add(jWebBrowser,BorderLayout.CENTER);
		
		setVisible(true);
		setSize(800, 600);
		setLocation((PencilMain.showWidth-800)/2, 0);
    }
    
    /**
     * 将图片转换成Base64编码 ,带头文件
     * @param imgFile 待处理图片
     */
    public static String imageToBase64Head(String imgFile){
        //将图片文件转化为字节数组字符串，并对其进行Base64编码处理
   	 	String type = imgFile.substring(imgFile.length()-3,imgFile.length());
   	 	//为编码添加头文件字符串
        String head = "data:image/"+type+";base64,";
        return head + imageToBase64(imgFile);
    }
    /**
     * 将图片转换成Base64编码
     * @param imgFile 待处理图片
     */
    public static String imageToBase64(String imgFile){
        //将图片文件转化为字节数组字符串，并对其进行Base64编码处理
    	InputStream in = null;
        byte[] data = null;
        try {
        	//读取图片字节数组
            in = new FileInputStream(imgFile);        
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
        return new String(Base64.encodeBase64(data));
    }
    
    public class sendEmailThread extends Thread{
    	public void run(){
    		boolean error = false;
    		int rowTotle = studentModel.data.size();
    		for(int i=0;i<rowTotle&&threadCon;i++){
    			if((Boolean)studentModel.data.elementAt(i)[0]){
    				String toEmail = (String)studentModel.data.elementAt(i)[24];
    				processBar.setString((i+1)+" / "+sendCount);
    				if(!toEmail.equals("")&&Helper.matchRegular(toEmail, regEmail)){
    					nameInput.setText(toEmail);
    					try {
							emailManage.sendMail(toEmail,content,fileList);
						} catch (UnsupportedEncodingException e) {
							error = true;
							JOptionPane.showMessageDialog(null, e.getMessage());
							e.printStackTrace();
						}
    				}
    			}
    		}
    		startBtn.setEnabled(true);
    		if(!error)
    			JOptionPane.showMessageDialog(null, "发送完成");
    	}
    }
    
    public void actionPerformed(ActionEvent e) {
		JButton btn = (JButton)e.getSource();
		if(btn==stopBtn){
			threadCon = false;
		}else if(btn==startBtn){
			//解析要发送的内容
			startBtn.setEnabled(false);
			String html = jWebBrowser.getHTMLContent();
			Document doc = Jsoup.parse(html);
			Elements images = doc.getElementsByTag("img");
			for (Element img : images) {
	            String src = img.attr("src");
	            String base64 = imageToBase64Head(src);
	            img.attr("src",base64);
	        }
			content = doc.select("div.note-editable").html();
			//发送邮件
			if(studentModel==null){
				startBtn.setEnabled(false);
				new Thread(
					new Runnable() {
						public void run() {
							String toEmail = nameInput.getText();
							if(!toEmail.equals("")&&Helper.matchRegular(toEmail, regEmail)){
								processBar.setString("正在给 "+toEmail+"发送邮件");
								try {
									emailManage.sendMail(toEmail,content,fileList);
								} catch (UnsupportedEncodingException e1) {
									JOptionPane.showMessageDialog(null, e1.getMessage());
									e1.printStackTrace();
								}
							}else{
								JOptionPane.showMessageDialog(null, "邮箱输入不符合要求，请重新输入");
							}
							startBtn.setEnabled(true);
						}
					}
				).start();
			}else{
				threadCon = true;
				new sendEmailThread().start();
			}
		}else if(btn==fileBtn){
			JFileChooser fileChoose = new JFileChooser("F:");
	        fileChoose.setMultiSelectionEnabled(true);
	        fileChoose.setFileSelectionMode(JFileChooser.FILES_ONLY);
	        fileChoose.setFileHidingEnabled(false);
	        fileChoose.setAcceptAllFileFilterUsed(false);
	        int returnValue = fileChoose.showOpenDialog(null);
	        if (returnValue == JFileChooser.APPROVE_OPTION){  
	        	File[] files = fileChoose.getSelectedFiles();
	        	fileList = new String[files.length];
	        	String fString = "";
	        	int fileSize = 0;
	        	for(int i=0;i<files.length;i++){
	        		fileList[i] = files[i].getPath();
	        		fString+=files[i].getName()+"  ";
	        		fileSize += files[i].length()/(1024);
	        	}
	        	if(fString.equals(""))
	        		fileInput.setText("没有选择文件");
	        	else
	        		fileInput.setText(fString+" 文件大小："+fileSize+"kb");
	        }
	        
		}
    }
}