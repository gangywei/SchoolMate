package schoolmate.view;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.UIManager;
import org.apache.log4j.Logger;
import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;
import chrriis.common.UIUtils;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import schoolmate.control.tableModel.StudentModel;
import schoolmate.model.User;
import schoolmate.view.element.FrameMenubar;
import schoolmate.view.element.FrameToolBar;
import schoolmate.view.element.TabelPanel;

public class PencilMain extends JFrame{
	private static final long serialVersionUID = 1L;
	public static final int COLUM = 34;
	public static User nowUser;
	public static int rowCount=25;
	public JDesktopPane desktopPane;
	public static boolean dbControl = true;	//数据库一写多读的要求 false=>正在处理大量的写数据。
	private SendEmailFrame sendEmailFrame;
	private AllUserFrame userFrame;
	private LabelManageFrame labelFrame;
	private FrameMenubar frameMenubar;
	private FrameToolBar toolbar;
	public TabbedFrame tabbedFrame;
	private ImportExlFrame importFrame;	//文件导入界面
	private BackupUpdateFrame backupUpdate;
	public CollectDataFrame collectDataFrame;	//汇总功能界面
	private OutportExlFrame output;	//导出标准Excel界面
	private BackupsDataFrame backupsFrame;	//数据备份界面
	private ReNewFrame reNewFrame;	//数据恢复界面
	private LoginFrame loginFrame;//登录界面
	private RemarksFrame Remarks;
	private UserDetailFrame userDetail;
	private PencilMain _this;
	private ForgetPasswordFrame forgetPassword;	//忘记密码界面
	public static int frameWidth,frameHeight;	//屏幕宽高
	public static int showWidth,showHeight;
	public static String education[] = {"  请选择设置的密保问题    ","数字与字母组合6至12位"};
	public static String regular[] = {"","^[\\d]{6,12}$","^[\\w]{6,12}$"};
	public static String dbPath = "jdbc:sqlite:E:/assess/schoolmates.db";
	public static String[] DEFAULT_FONT  = new String[]{
		    "CheckBox.font",
		    "ScrollPane.font",
		    "ToolTip.font",
		    "EditorPane.font",
		    "RadioButton.font",
		    "CheckBoxMenuItem.font",
		    "TextPane.font",
		    "TextField.font",
		    "TitledBorder.font",
		    "Label.font",
		    "PasswordField.font",
		};
	public static String[] MID_FONT = new String[]{
			"Menu.font",
			"List.font", 
			"Table.font",
			"Button.font",
			"ComboBox.font",
		    "MenuItem.font",
			"PopupMenu.font",
			"ProgressBar.font",
		    
	};
	public static Logger logger = Logger.getLogger(PencilMain.class);
	public PencilMain() throws Exception{
    	initFrame();
    }

	@SuppressWarnings("serial")
	public void initFrame() throws Exception{
		_this = this;
		setTitle("校友管理系统");
		setIconImage(getImagePath("school.png"));
		desktopPane = new JDesktopPane(){
			protected void paintComponent(Graphics g) {  
		        ImageIcon img = new ImageIcon("resource/img/matebg.png");
		        g.drawImage(img.getImage(), 0, 0, getWidth(), getHeight(), img.getImageObserver());
		    }
		};
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frameWidth= (int) screenSize.getWidth();
        frameHeight= (int) screenSize.getHeight();
        add(desktopPane);
        setBounds(0,0,frameWidth*3/4,frameHeight*3/4);
		setVisible(true);
		setLocationRelativeTo(null); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.addWindowStateListener(new WindowStateListener () {
 		   	public void windowStateChanged(WindowEvent e) {
 		   		int state = e.getNewState();
	 		    if(state == 0||state == 6) {
	 		    	getShowSize();
	 		    }
 		   	}
        });
	}
	
	private Image getImagePath(String resource){
		Image image=null;
		InputStream is = (InputStream) this.getClass().getResourceAsStream(resource);   
		try {
			image = ImageIO.read(is);
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return image;
	}
	
	//得到中间面板的宽和高
	public void getShowSize(){
		Dimension size = desktopPane.getSize();
		if(collectDataFrame!=null){
			collectDataFrame.setSize(size);
		}
    	showWidth = size.width;
    	showHeight = size.height;
    	repaint();
	}
		
	public static void main(String[] args) throws Exception{
        try
        {
        	for (int i = 0; i < DEFAULT_FONT.length; i++)
        	    UIManager.put(DEFAULT_FONT[i],new Font("微软雅黑", Font.PLAIN,14));
        	for(int i=0;i<MID_FONT.length;i++)
        		UIManager.put(MID_FONT[i],new Font("微软雅黑", Font.PLAIN,13));
        	UIManager.put("RootPane.setupButtonVisible", false);
        	BeautyEyeLNFHelper.frameBorderStyle = BeautyEyeLNFHelper.FrameBorderStyle.osLookAndFeelDecorated;
        	BeautyEyeLNFHelper.translucencyAtFrameInactive = false;
        	org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
        }
        catch (Exception e){
        	e.printStackTrace();
        }
        PencilMain pencil = new PencilMain();
        pencil.getShowSize();
        pencil.showLogin();
	}
	
	public void showLogin() throws Exception{
		if(loginFrame==null){
			loginFrame = new LoginFrame(this);
			desktopPane.add(loginFrame);
		}else
			loginFrame.setVisible(!loginFrame.isShowing());
		if(nowUser!=null)
			loginFrame.setUser();
		loginFrame.toFront();
	}
	
	//汇总
	public void collectPanel() throws Exception{
		if(collectDataFrame==null){
			collectDataFrame = new CollectDataFrame(this);
			desktopPane.add(collectDataFrame);
		}
		if(collectDataFrame.isIcon()==true)
			collectDataFrame.setIcon(false);
		collectDataFrame.toFront();
		collectDataFrame.setMaximum(true);
	}
	
	public void forgetPassword(){
		if(forgetPassword==null){
			forgetPassword = new ForgetPasswordFrame(this);
			desktopPane.add(forgetPassword);
		}
		forgetPassword.toFront();
	}
	
	public void userLogin() throws Exception{
		loginFrame.hide();
		frameMenubar = new FrameMenubar(this);
		toolbar = new FrameToolBar(this);
		setJMenuBar(frameMenubar.createMenu());
		add(toolbar,BorderLayout.NORTH);
		getContentPane().revalidate();
		getContentPane().repaint();
		collectPanel();
	}
	public void alterLabel(TabelPanel tableDemo,String[] label,String[] text,int type){
		AlterLabelFrame alterLabel = new AlterLabelFrame(tableDemo,label,text,type);
		desktopPane.add(alterLabel);
		alterLabel.toFront();
	}
	//显示用户详情
	public void userDetail(){
		if(userDetail==null||userDetail.isClosed()){
			userDetail = new UserDetailFrame(this);
			desktopPane.add(userDetail);
		}
		userDetail.toFront();
	}
	public void importExl() throws PropertyVetoException{
		//null或者点关闭之后重新初始化，点击菜单栏改变显示的状态
		if(importFrame==null||importFrame.isClosed()){
			importFrame = new ImportExlFrame(this);       
	        desktopPane.add(importFrame);
		}
        importFrame.toFront();
	}
	public void addWorkLog(int type,int id,StudentDetailFrame detail,int updateId) throws PropertyVetoException{
		//null或者点关闭之后重新初始化，点击菜单栏改变显示的状态
		AddWorkLogFrame addWorkLogFrame = new AddWorkLogFrame(type,id,detail,updateId);       
        desktopPane.add(addWorkLogFrame);
		addWorkLogFrame.toFront();
	}
	public void sendEmail(StudentModel studentModel){
		UIUtils.setPreferredLookAndFeel();   
		if(!NativeInterface.isOpen()){
			NativeInterface.initialize();
			NativeInterface.open();  
		}
        EventQueue.invokeLater(new Runnable() {  
            public void run() {  
                try {  
                	if(sendEmailFrame==null||sendEmailFrame.isClosed()){
	                	sendEmailFrame = new SendEmailFrame(studentModel,_this);
	                	desktopPane.add(sendEmailFrame);
                	}
                	sendEmailFrame.setMaximum(true);
            		sendEmailFrame.toFront();
                } catch (Exception e) {  
                    e.printStackTrace();  
                }  
            }  
        });  
        //介绍网站https://sourceforge.net/p/djproject/discussion/671154/thread/e813001e/
        //NativeInterface.runEventPump();  
	}
	public void studentDetail(String[] temp,int type){
		StudentDetailFrame detailFrame = new StudentDetailFrame(this,temp,type);
		desktopPane.add(detailFrame);
		detailFrame.toFront();
	}
	//导出Excel
	public void outputExl(StudentModel studentModel,int type,String title){
		if(backupsFrame==null||backupsFrame.isClosed()){
			output = new OutportExlFrame(studentModel,type,title);
			desktopPane.add(output);
			output.toFront();
		}else{
			output.dispose();
		}
	} 
	//备份
	public void backupsData(){
		if(backupsFrame==null||backupsFrame.isClosed()){
			backupsFrame = new BackupsDataFrame();
			desktopPane.add(backupsFrame);
		}
		backupsFrame.toFront();
	}
	
	public void backupUpdate(){
		if(backupUpdate==null||backupUpdate.isClosed()){
			backupUpdate = new BackupUpdateFrame(this);
			desktopPane.add(backupUpdate);
		}
		backupUpdate.toFront();
	}
	
	//还原
	public void reNewFrame(){
		if(reNewFrame==null||reNewFrame.isClosed()){
			reNewFrame = new ReNewFrame();
			desktopPane.add(reNewFrame);
		}
		reNewFrame.toFront();
	}
	//修改备注
	public void Remarks(){
		if(Remarks==null||Remarks.isClosed()){
			Remarks = new RemarksFrame();
			desktopPane.add(Remarks);
		}
		Remarks.toFront();
	}
	//添加用户
	public void addUser(int id){
		AddUserFrame addUserFrame = new AddUserFrame(id);
		desktopPane.add(addUserFrame);
		addUserFrame.toFront();
	}
	
	public void labelManage() throws SQLException{
		if(labelFrame==null||labelFrame.isClosed()){
			labelFrame = new LabelManageFrame(collectDataFrame);
			desktopPane.add(labelFrame);
		}
		labelFrame.toFront();
	}
	//展示用户
	public void allUser(){
		if(userFrame==null||userFrame.isClosed()){
			userFrame = new AllUserFrame(this);
			desktopPane.add(userFrame);
		}
		userFrame.toFront();
	}
	//根据当前操作状态修改界面，保证数据库只出现一写或者多读的情况。
	public void dbControl(boolean type){
		dbControl = type;
		collectDataFrame.MenuControl(dbControl);
		frameMenubar.MenuControl(dbControl);
		toolbar.MenuControl(dbControl);
	}
	
}
