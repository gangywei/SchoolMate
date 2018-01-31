package schoolmate.view;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.beans.PropertyVetoException;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import org.apache.log4j.Logger;
import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;
import chrriis.common.UIUtils;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import schoolmate.control.StudentModel;
import schoolmate.model.User;
import schoolmate.view.element.FrameMenubar;
import schoolmate.view.element.FrameToolBar;

public class PencilMain extends JFrame{
	private static final long serialVersionUID = 1L;
	public static final int COLUM = 34;
	public JPanel bgPanel;
	public static User nowUser;
	public static int rowCount=25;
	public JDesktopPane desktopPane;
	public static boolean dbControl = true;	//数据库一写多读的要求 false=>正在处理大量的写数据。
	private ImportExlFrame importFrame;	//文件导入界面
	private AddStudentFrame addStudentFrame;	//添加学生信息
	private CollectDataFrame collectDataFrame;	//汇总功能界面
	private BackupsDataFrame backupsFrame;	//数据备份界面
	private ReNewFrame reNewFrame;	//数据恢复界面
	private LoginFrame loginFrame;//登录界面
	private UserDetailFrame userDetail;
	private ForgetPasswordFrame forgetPassword;	//忘记密码界面
	public static int frameWidth,frameHeight;	//屏幕宽高
	public static int showWidth,showHeight;
	public static String dbPath = "jdbc:sqlite:E:\\assess\\schoolmates.db";
	public static String[] DEFAULT_FONT  = new String[]{
		    "Table.font"
		    ,"TableHeader.font"
		    ,"CheckBox.font"
		    ,"Tree.font"
		    ,"Viewport.font"
		    ,"ProgressBar.font"
		    ,"RadioButtonMenuItem.font"
		    ,"ToolBar.font"
		    ,"ColorChooser.font"
		    ,"ToggleButton.font"
		    ,"Panel.font"
		    ,"TextArea.font"
		    ,"Menu.font"
		    ,"TableHeader.font"
		    ,"TextField.font"
		    ,"OptionPane.font"
		    ,"MenuBar.font"
		    ,"Button.font"
		    ,"Label.font"
		    ,"PasswordField.font"
		    ,"ScrollPane.font"
		    ,"MenuItem.font"
		    ,"ToolTip.font"
		    ,"List.font"
		    ,"EditorPane.font"
		    ,"Table.font"
		    ,"TabbedPane.font"
		    ,"RadioButton.font"
		    ,"CheckBoxMenuItem.font"
		    ,"TextPane.font"
		    ,"PopupMenu.font"
		    ,"TitledBorder.font"
		    ,"ComboBox.font"
		};
	public static Logger logger = Logger.getLogger(PencilMain.class);
	public PencilMain() throws Exception{
    	initFrame();
    }

	@SuppressWarnings("serial")
	public void initFrame() throws Exception{
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
	//得到中间面板的宽和高
	public void getShowSize(){
		Dimension size = getContentPane().getSize();
    	showWidth = size.width;
    	showHeight = size.height;
	}
		
	public static void main(String[] args) throws Exception{
        try
        {
        	for (int i = 0; i < DEFAULT_FONT.length; i++)
        	    UIManager.put(DEFAULT_FONT[i],new Font("微软雅黑", Font.PLAIN,16));
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
        //pencil.userLogin();
        //pencil.userDetail();
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
	
	public void forgetPassword(){
		if(forgetPassword==null){
			forgetPassword = new ForgetPasswordFrame(this);
			desktopPane.add(forgetPassword);
		}else{
			forgetPassword.setVisible(!forgetPassword.isShowing());
		}
		forgetPassword.toFront();
	}
	
	public void userLogin() throws Exception{
		loginFrame.hide();
		FrameMenubar frameMenubar = new FrameMenubar(this);
		FrameToolBar toolbar = new FrameToolBar(this);
		setJMenuBar(frameMenubar.createMenu());
		add(toolbar,BorderLayout.NORTH);
		getContentPane().revalidate();
		getContentPane().repaint();
		collectPanel();
	}
	//显示用户详情
	public void userDetail(){
		userDetail = new UserDetailFrame(this);
		desktopPane.add(userDetail);
		userDetail.toFront();
	}
	public void addStudent(String[] temp) throws PropertyVetoException, SQLException{
		if(addStudentFrame!=null)
			addStudentFrame.setIcon(!addStudentFrame.isIcon());
		if(addStudentFrame==null||addStudentFrame.isClosed()){
			addStudentFrame = new AddStudentFrame(this,temp);
			desktopPane.add(addStudentFrame);
		}
		addStudentFrame.toFront();
	}
	public void importExl() throws PropertyVetoException{
		if(importFrame==null||importFrame.isClosed()){
			importFrame = new ImportExlFrame(this);       
	        desktopPane.add(importFrame);
		}else{
			importFrame.setIcon(!importFrame.isIcon());
		}
        importFrame.toFront();
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
                	SendEmailFrame sendEmailFrame = new SendEmailFrame(studentModel);
                	desktopPane.add(sendEmailFrame);
            		sendEmailFrame.toFront();
                } catch (Exception e) {  
                    e.printStackTrace();  
                }  
            }  
        });  
        //介绍网站https://sourceforge.net/p/djproject/discussion/671154/thread/e813001e/
        //NativeInterface.runEventPump();  
	}
	public void studentDetail(String[] temp) throws SQLException{
		StudentDetailFrame detailFrame = new StudentDetailFrame(this,temp);
		desktopPane.add(detailFrame);
		try {
			detailFrame.setMaximum(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		detailFrame.toFront();
	}
	//导出Excel
	public void outputExl(StudentModel studentModel,int type,String title){
		OutportExlFrame output = new OutportExlFrame(studentModel,type,title);
		desktopPane.add(output);
		output.toFront();
	} 
	//汇总
	public void collectPanel() throws Exception{
		if(collectDataFrame==null){
			collectDataFrame = new CollectDataFrame(this);
			desktopPane.add(collectDataFrame);
		}
		collectDataFrame.toFront();
		collectDataFrame.setIcon(!collectDataFrame.isIcon());
		collectDataFrame.setMaximum(true);
	}
	//备份
	public void backupsData(){
		if(backupsFrame==null){
			backupsFrame = new BackupsDataFrame();
			desktopPane.add(backupsFrame);
		}else
			backupsFrame.setVisible(!backupsFrame.isShowing());
		backupsFrame.toFront();
	}
	//还原
	public void reNewFrame(){
		if(reNewFrame==null){
			reNewFrame = new ReNewFrame();
			desktopPane.add(reNewFrame);
		}else
			reNewFrame.setVisible(!reNewFrame.isShowing());
		reNewFrame.toFront();
	}
	//修改备注
	public void Remarks(){
		RemarksFrame Remarks = new RemarksFrame();
		desktopPane.add(Remarks);
		Remarks.toFront();
	}
	//根据当前操作状态修改界面，保证数据库只出现一写或者多读的情况。
	public void dbControl(boolean type){
		dbControl = type;
		collectDataFrame.MenuControl(dbControl);
	}
}
