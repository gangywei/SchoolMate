package schoolmate.view;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;
import schoolmate.control.tableModel.StudentModel;
import schoolmate.database.EducationLog;
import schoolmate.database.StudentLog;
import schoolmate.model.User;
import schoolmate.view.element.MyTable;
import schoolmate.view.element.TableLeftMouse;

public class CollectDataFrame extends JInternalFrame implements ActionListener{
	private String cell = "s.s_id,e.s_no,s_name,s_sex,s_birth,s_person,s_hometown,e.s_faculty,e.s_major,"
			+ "e.s_class,e.s_education,e.s_enter,e.s_graduate,s_nation,s_province,s_city,s_workspace,s_worktitle,"
			+ "s_work,s_workphone,s_homephone,s_phone,s_tphone,s_address,s_postcode,s_email,s_remark1,s_qq,s_weixin,"
			+ "s_remark2,s_remark3,s_remark4,s_remark5,s.update_time";
	public int dataNum = 30,dataTotle = 0,nowIndex = 1,nowSelect=-1;
	double btnNum = 0;
	private JScrollPane scroll;
	public MyTable table;
	private RowListener rowLis;
	private JLabel pageLabel;	//页码标签
	public String instant="",condition="",//模糊查询和组合查询的条件
			eduContion="",	//教育表的筛选条件
			limitStr = "",	limitTemp;	//用户的权限字段
	public StudentModel studentModel;
	public Vector<Object[]> data = new Vector<Object[]>();
	private JPanel topPanel,bottomPanel;
	private JPanel conditionPanel,searchPanel;
	private JButton instantBtn = new JButton("模糊搜索");
	private JButton groupBtn = new JButton("组合查询");
	private Icon icon=new ImageIcon(PencilMain.PPATH+"/resource/img/loading.gif");
	private JLabel loadingLabel = new JLabel(icon);
	private JLabel collectLabel = new JLabel("");
	public JLabel collectText = new JLabel("数据统计");
	private JPanel bottomLeft = new JPanel();
	private JPanel bottomRight = new JPanel();
	private JButton exportBtn = new JButton("导出数据");
	private JButton deleteBtn = new JButton("删除数据");
	private JButton emailBtn = new JButton("邮件群发");
	public JButton refeshBtn = new JButton("刷新");
	private JButton allBtn = new JButton("全部选中");
	private TableLeftMouse tableLeftMouse;	//右键
	public PencilMain pencil;
	public TabbedFrame tabbedFrame;
	private BlurSearchFrame blurSearchFrame;
	public CollectDataFrame(PencilMain pencil) throws Exception{
		this.pencil = pencil;
		//默认显示用户管理的学院
		String[] res = PencilMain.nowUser.faculty.split(",");
		limitTemp = "(";
		for(int i=0;i<res.length;i++)
			limitTemp += '"'+res[i]+"\",";
		limitTemp += "''";	//搜索学院为空的数据
		limitTemp += ')';
		limitStr = "s_faculty in "+limitTemp;
		init();
	}
	public void init() throws Exception{
		setTitle("数据显示界面");
		setResizable(true);  //允许自由调整大小 
        setMaximizable(true); //设置提供最大化按钮
        setIconifiable(true); //设置提供图标化按钮
		setLayout(new BorderLayout());
		topPanel = new JPanel();
		topPanel.setBackground(Color.WHITE);
		searchPanel = new JPanel();
		conditionPanel = new JPanel();
		loadingLabel.setVisible(false);
		allBtn.addActionListener(this);
		allBtn.setForeground(Color.white);  
		allBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
		emailBtn.addActionListener(this);
		emailBtn.setForeground(Color.white);  
		emailBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
		groupBtn.addActionListener(this);
		groupBtn.setForeground(Color.white);  
		groupBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
		refeshBtn.addActionListener(this);
		refeshBtn.setForeground(Color.white);  
		refeshBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
		deleteBtn.addActionListener(this);
		deleteBtn.setForeground(Color.white);  
		deleteBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
		exportBtn.addActionListener(this);
		exportBtn.setForeground(Color.white);  
		exportBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
		
		instantBtn.addActionListener(this);
		instantBtn.setForeground(Color.white);  
		instantBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
		topPanel.setLayout(new BorderLayout());
		bottomPanel = new JPanel();
		bottomPanel.setBackground(Color.WHITE);
		bottomPanel.setLayout(new BorderLayout(15,15));
		pageLabel = new JLabel();
		//初始化Table显示
		studentModel = new StudentModel(data,0);
		table = new MyTable(studentModel);
		rowLis = new RowListener();
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setFillsViewportHeight(true);
		tableLeftMouse = new TableLeftMouse(this);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);	//表格只允许单选
		table.getSelectionModel().addListSelectionListener(rowLis);
		table.addMouseListener(new MouseAdapter() {
			public void mouseReleased(java.awt.event.MouseEvent e) {
				if (e.isPopupTrigger()){
					tableLeftMouse.show((JComponent)e.getSource(),e.getX(),e.getY());
				}
			}
		});
		updateTabel(null, null);
		scroll = new JScrollPane(table);
		scroll.setBackground(Color.WHITE);
		conditionPanel.add(groupBtn);
		conditionPanel.add(instantBtn);
		if(PencilMain.nowUser.u_role>1)
			conditionPanel.add(allBtn);
		conditionPanel.add(refeshBtn);
		conditionPanel.add(loadingLabel);
		if(PencilMain.nowUser.u_role>1){
			searchPanel.add(exportBtn);
			searchPanel.add(deleteBtn);
			searchPanel.add(emailBtn);
		}
		
		searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT,10,5));
		topPanel.add(searchPanel,BorderLayout.EAST);
		topPanel.add(conditionPanel,BorderLayout.WEST);
		
		bottomLeft.add(pageLabel);
		bottomLeft.add(collectLabel);
		bottomLeft.add(collectText);
		bottomPanel.add(bottomLeft,BorderLayout.WEST);
		bottomPanel.add(bottomRight,BorderLayout.EAST);
		
		add(topPanel,BorderLayout.NORTH);
		add(scroll);
		add(bottomPanel,BorderLayout.SOUTH);
		setSize(PencilMain.showWidth,PencilMain.showHeight-75);
		setVisible(true);
	}
	
	//列表行监听
	private class RowListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent event) {
            if (event.getValueIsAdjusting()) {
                return;
            }
            if(nowSelect==event.getFirstIndex())
            	nowSelect = event.getLastIndex();
            else
            	nowSelect = event.getFirstIndex();
        }
    }
	//数据库一写多读
	public void MenuControl(boolean dbState){
		instantBtn.setEnabled(dbState);
		deleteBtn.setEnabled(dbState);
		refeshBtn.setEnabled(dbState);
		groupBtn.setEnabled(dbState);
		emailBtn.setEnabled(dbState);
		repaint();
	}
	/* 查询完成后更新底部的汇总信息和分页按钮
	 * sql 语句，type=区分是否显示分页按钮（1=>分页搜索，0=>搜索全部，2=>页码搜索）
	 */
	public void updateBottom(String sql,int type) throws SQLException{
		if(type<2){
			btnNum = 1;
			nowIndex = 1;
			dataTotle = studentModel.data.size();
			collectText.setText(" 总记录数："+dataTotle);
		}
		if(type==1){
			btnNum = Math.ceil((float)dataTotle/(float)dataNum);
			nowIndex = 1;
		}
		//pageLabel.setText("当前页："+nowIndex+" 总页面："+(int)btnNum);
		allBtn.setText("全部选中");
		table.updateUI();
		this.updateUI();
	}
	
	/*
	 * inter：联合查询3个表得到符合条件的学生记录，并显示数据，
	 * condTemp 组合查询条件，insTemp 全文检索条件
	 */
	public void updateTabel(String condTemp[],String insTemp){
		new Thread(){
			public void run(){
				try {
					if(!loadingLabel.isShowing()){
						loadingLabel.setVisible(true);
						long startTime=System.currentTimeMillis();   //获取开始时间
						if(insTemp!=null&&!insTemp.equals(""))	//全文检索数据项,得到全文检索符合条件的学生 id。(因为部分原因改为模糊查询)
							instant = "join (select s_id from fullsearch where fs_content like '%"+insTemp+"%') as fs on fs.s_id=s.s_id";
						if(condTemp!=null){	//当数据为null时，表示不更新搜索条件。	
							condition = condTemp[0];
							if(pencil.nowUser.u_role==3)
								eduContion = "join (select e.* from education e "+condTemp[1]+")";
							else
								eduContion = "join (select e.* from education e "+condTemp[1]+" and "+limitStr+")";
						}
						//考虑权限定义的内容
						if(eduContion.equals(""))
							if(pencil.nowUser.u_role==3)
								eduContion = " join education";
							else
								eduContion = " join (select * from education where "+limitStr+")";
						//得到不重复的教育记录。
						data = StudentLog.dao("select "+cell+" from student s "+eduContion+" e on s.s_id=e.s_id "+instant+condition+" order by s.update_time desc;");
						long endTime=System.currentTimeMillis(); //获取结束时间
						System.out.println("程序运行时间： "+(endTime-startTime)+"ms");
						studentModel.setData(data);
						table = new MyTable(studentModel);
						updateBottom("select count(DISTINCT(s.s_id)) totle from (select DISTINCT(s_id) from student "+condition+") s "+eduContion+" e on s.s_id=e.s_id "+instant+" ;",0);
						loadingLabel.setVisible(false);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	public void actionPerformed(ActionEvent e) {
		JButton btn = (JButton)e.getSource();
		if(btn==allBtn){
			for(int i=0;i<data.size();i++){
				if(allBtn.getText()=="全部选中"){
					data.elementAt(i)[0] = true;
				}else{
					data.elementAt(i)[0] = false;
				}	
			}
			if(allBtn.getText()=="全部选中")
				allBtn.setText("取消全选");
			else
				allBtn.setText("全部选中");
			this.updateUI();
		}else if(btn==refeshBtn){
			updateTabel(null,"");
		}else if(btn==groupBtn){
			if(tabbedFrame==null)
				tabbedFrame = new TabbedFrame(this);
			tabbedFrame.toFront();
		}else if(btn==instantBtn){
			if(blurSearchFrame==null||blurSearchFrame.isClosed()){
				blurSearchFrame = new BlurSearchFrame(this);
				pencil.desktopPane.add(blurSearchFrame);
			}
			blurSearchFrame.toFront();
		}else if(btn==deleteBtn){
			int res =JOptionPane.showConfirmDialog(this,"删除选中记录的所有信息？","删除信息提示",JOptionPane.YES_NO_OPTION);
			if(res==0){
				new Thread(){
					public void run(){
						String selectIndex = studentModel.getSelect();
						if(selectIndex==null){
							JOptionPane.showMessageDialog(null, "选中表格前面的选框，进行数据删除或者导出Excel");
						}else{
							try {
								boolean result = StudentLog.deleteMany(selectIndex);
								if(!result)
									JOptionPane.showMessageDialog(null, "删除失败！");
								else
									updateTabel(null,null);
								selectIndex=null;
							} catch (Exception e1) {
								e1.printStackTrace();
							}
						}
					}
				}.start();
			}
		}else if(btn==emailBtn){
			if(studentModel.getSelectCount()==0)
				pencil.sendEmail(null);
			else
				pencil.sendEmail(studentModel);
		}else if(btn==exportBtn){
			int count = studentModel.getSelectCount();
			if(count>0){
				SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
				pencil.outputExl(studentModel,1,df.format(new Date())+"导出Excel");
			}else{
				JOptionPane.showMessageDialog(null, "请选择想要导出的数据");
			}
		}
	}
}
