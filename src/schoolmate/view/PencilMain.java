package schoolmate.view;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
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
import schoolmate.view.element.tablePanel.AllUserPanel;

public class PencilMain extends JFrame{
	private static final long serialVersionUID = 1L;
	public static final int COLUM = StudentModel.excelCol.length+2;	//多两个字段
	public static User nowUser;	//当前登陆的用户信息
	public JDesktopPane desktopPane;	//多文档窗体容器
	public static boolean dbControl = true;	//sqlite数据库只支持一写多读，false=>正在处理大量的写数据。
	public SendEmailFrame sendEmailFrame;	//发送邮箱界面
	private AllUserFrame userFrame;	//用户管理界面
	private LabelManageFrame labelFrame;	//字段修改界面
	private FrameMenubar frameMenubar;	//顶部菜单栏
	private FrameToolBar frameToolbar;	//顶部功能栏
	public TabbedFrame tabbedFrame;	//条件检索界面
	private ImportExlFrame importFrame;	//文件导入界面
	private BackupUpdateFrame backupUpdate;	//	导出更新数据额界面
	public CollectDataFrame collectDataFrame;	//汇总功能界面
	private OutportExlFrame outputFrame;	//导出标准Excel界面
	private BackupsDataFrame backupsFrame;	//数据备份界面
	private ReNewFrame reNewFrame;	//数据恢复界面
	private LoginFrame loginFrame;	//登录界面
	private RemarksFrame remarkFrame;	//修改备注字段界面
	private UserDetailFrame userDetail;	//用户详情界面
	private PencilMain _this;
	public static final String PPATH = System.getProperty("user.dir");
	public static final String CPATH = PPATH+"/resource/config/soft.properties";
	private ForgetPasswordFrame forgetPassword;	//忘记密码界面
	public static int frameWidth,frameHeight;	//屏幕宽高
	public static int showWidth,showHeight;
	public static String education[] = {"  请选择设置的密保问题    ","数字与字母组合6至12位"};
	public static String regular[] = {"^[\\d]{6,12}$","^[\\w]{6,12}$"};
	public static String dbPath = "jdbc:sqlite:"+PPATH+"/spy/mates.lite";
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
	
	private Image getImagePath(String resource){
		Image image=null;
		InputStream is = (InputStream) this.getClass().getResourceAsStream(resource);   
		try {
			image = ImageIO.read(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
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
	
	public void showLogin(){	//显示登录界面
		if(loginFrame==null||loginFrame.isClosed()){
			loginFrame = new LoginFrame(this);
			desktopPane.add(loginFrame);
		}else
			loginFrame.setVisible(!loginFrame.isShowing());
		if(nowUser!=null)
			loginFrame.setUser();
		loginFrame.toFront();
	}
	public void quitLogin(){	//退出登录
		nowUser = null;
		setJMenuBar(null);
		frameMenubar.closeDialog();
		remove(frameToolbar);
		desktopPane.removeAll();
		if(userDetail!=null)userDetail.dispose();
		if(importFrame!=null)importFrame.dispose();
		if(forgetPassword!=null)forgetPassword.dispose();
		if(userFrame!=null)userFrame.dispose();
		if(labelFrame!=null)labelFrame.dispose();
		if(remarkFrame!=null)remarkFrame.dispose();
		if(reNewFrame!=null)reNewFrame.dispose();
		if(backupUpdate!=null)backupUpdate.dispose();
		if(backupsFrame!=null)backupsFrame.dispose();
		collectDataFrame.dispose();
		showLogin();
		repaint();
	}
	public void loginSuccess() throws Exception{
		loginFrame.dispose();
		frameMenubar = new FrameMenubar(this);
		frameToolbar = new FrameToolBar(this);
		setJMenuBar(frameMenubar.createMenu());
		if(nowUser.u_role>1)
			add(frameToolbar,BorderLayout.NORTH);
		getContentPane().revalidate();
		getContentPane().repaint();
		collectPanel();
	}
	public void collectPanel() throws Exception{	//汇总
		if(collectDataFrame==null||collectDataFrame.isClosed()){
			collectDataFrame = new CollectDataFrame(this);
			desktopPane.add(collectDataFrame);
		}
		if(collectDataFrame.isIcon()==true)
			collectDataFrame.setIcon(false);
		collectDataFrame.toFront();
		collectDataFrame.setMaximum(true);
	}
	public void forgetPassword(){	//忘记密码
		if(forgetPassword==null||forgetPassword.isClosed()){
			forgetPassword = new ForgetPasswordFrame(this);
			desktopPane.add(forgetPassword);
		}else
			forgetPassword.setVisible(!forgetPassword.isShowing());
		forgetPassword.toFront();
	}
	public void alterLabel(TabelPanel tableDemo,String[] label,String[] text,int index,int type){	//修改数据库字段
		AlterLabelFrame alterLabel = new AlterLabelFrame(tableDemo,label,text,index,type);
		desktopPane.add(alterLabel);
		alterLabel.toFront();
	}
	public void userDetail(){	//显示用户详情
		if(userDetail==null||userDetail.isClosed()){
			userDetail = new UserDetailFrame(this);
			desktopPane.add(userDetail);
		}
		userDetail.toFront();
	}
	public void importExl(){
		if(importFrame==null||importFrame.isClosed()){
			importFrame = new ImportExlFrame(this);       
	        desktopPane.add(importFrame);
		}
        importFrame.toFront();
	}
	/*
	 * type0=工作记录，1=教育记录。id=修改的学生ID。 updateId=修改的信息id，默认为0
	 * inter：添加学生的工作或者学习记录
	 */
	public void addStudentLog(int type,int id,StudentDetailFrame detail,int updateId){
		AddStudentLogFrame addWorkLogFrame = new AddStudentLogFrame(type,id,detail,updateId);       
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
                	if(sendEmailFrame==null){
	                	sendEmailFrame = new SendEmailFrame(studentModel,_this);
                	}
            		sendEmailFrame.toFront();
                } catch (Exception e) {  
                    e.printStackTrace();  
                }  
            }  
        });  
        //介绍网站https://sourceforge.net/p/djproject/discussion/671154/thread/e813001e/
        //NativeInterface.runEventPump();  
	}
	/*
	 * temp=学生记录，type：0=展示，1=修改，2=添加
	 * inter：添加学生的工作或者学习记录
	 */
	public void studentDetail(String[] temp,int type){
		StudentDetailFrame detailFrame = new StudentDetailFrame(this,temp,type);
		desktopPane.add(detailFrame);
		detailFrame.toFront();
	}
	//导出Excel
	public void outputExl(StudentModel studentModel,int type,String title){
		if(outputFrame==null||outputFrame.isClosed()){
			outputFrame = new OutportExlFrame(studentModel,type,title);
			desktopPane.add(outputFrame);
			outputFrame.toFront();
		}else{
			outputFrame.dispose();
		}
	} 
	
	public void backupsData(){	//数据备份
		if(backupsFrame==null||backupsFrame.isClosed()){
			backupsFrame = new BackupsDataFrame();
			desktopPane.add(backupsFrame);
			
		}
		backupsFrame.toFront();
	}
	public void backupUpdate(){	//导出更新数据
		if(backupUpdate==null||backupUpdate.isClosed()){
			backupUpdate = new BackupUpdateFrame(this);
			desktopPane.add(backupUpdate);
		}
		backupUpdate.toFront();
	}
	public void reNewFrame(){	//数据库还原
		if(reNewFrame==null||reNewFrame.isClosed()){
			reNewFrame = new ReNewFrame();
			desktopPane.add(reNewFrame);
		}
		reNewFrame.toFront();
	}
	public void remarkFrame(){	//修改备注
		if(remarkFrame==null||remarkFrame.isClosed()){
			remarkFrame = new RemarksFrame();
			desktopPane.add(remarkFrame);
		}
		remarkFrame.toFront();
	}
	public void addUser(AllUserPanel alluser,int id){	//添加用户( id 修改的用户id)
		AddUserFrame addUserFrame = new AddUserFrame(alluser,id);
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
		frameToolbar.MenuControl(dbControl);
	}
	
}
