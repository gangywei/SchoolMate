package schoolmate.view;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
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
	private String cell = "s.s_id,s.s_no,s_name,s_sex,s_birth,s_person,s_hometown,e.s_faculty,e.s_major,"
			+ "e.s_class,e.s_education,e.s_enter,e.s_graduate,s_nation,s_province,s_city,s_workspace,s_worktitle,"
			+ "s_work,s_workphone,s_homephone,s_phone,s_tphone,s_address,s_postcode,s_email,s_qq,s_weixin,s_remark1,"
			+ "s_remark2,s_remark3,s_remark4,s_remark5,s.update_time";
	public int dataNum = 30,dataTotle = 0,nowIndex = 1,nowSelect=-1;
	double btnNum = 0;
	private JScrollPane scroll;
	public MyTable table;
	public User user;
	private RowListener rowLis;
	private JLabel pageLabel;	//页码标签
	public boolean searchType = true;	//true=分页,false=不分页;默认为分页
	public String instant="",condition="",eduContion="",limitStr = "",	//模糊查询和组合查询的条件
			limitTemp;	//用户的权限字段
	public StudentModel studentModel;
	public Vector<Object[]> data = new Vector<Object[]>();
	private JPanel topPanel,bottomPanel;
	private JPanel conditionPanel,searchPanel;
	private JButton instantBtn = new JButton("模糊搜索");
	private JButton groupBtn = new JButton("组合查询");
	private JLabel collectLabel = new JLabel("");
	public JLabel collectText = new JLabel("数据统计");
	private JPanel bottomLeft = new JPanel(),
			bottomRight = new JPanel();
	private JButton exportBtn = new JButton("导出数据");
	private JButton deleteBtn = new JButton("删除数据");
	private JButton emailBtn = new JButton("邮件群发");
	public JButton refeshBtn = new JButton("刷新");
	private JButton allBtn = new JButton("全部选中");
	private TableLeftMouse tableLeftMouse;	//右键
	private JButton[] pageList = {new JButton("首页"),new JButton("上一页"),new JButton("下一页"),new JButton("尾页")};
	public PencilMain pencil;
	public TabbedFrame tabbedFrame;
	private BlurSearchFrame blurSearchFrame;
	public CollectDataFrame(PencilMain pencil) throws Exception{
		this.pencil = pencil;
		this.user = PencilMain.nowUser;
		if(user.u_role==1){
			String[] res = user.faculty.split(",");
			limitTemp = "(";
			for(int i=0;i<res.length;i++){
				limitTemp += '"'+res[i]+"\",";
			}
			limitTemp = limitTemp.substring(0,limitTemp.length()-1);
			limitTemp += ')';
			limitStr = "s_faculty in "+limitTemp;
		}
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
		for(int i=0;i<4;i++){
			pageList[i].addActionListener(this);
			pageList[i].setForeground(Color.white);  
			pageList[i].setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
		}
		topPanel.setLayout(new BorderLayout());
		bottomPanel = new JPanel();
		bottomPanel.setBackground(Color.WHITE);
		bottomPanel.setLayout(new BorderLayout(15,15));
		
		pageLabel = new JLabel();
		
		//初始化Table显示
		studentModel = new StudentModel(data,0);
		updateTabel(null,null,false);
		
		rowLis = new RowListener();
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		//table.setAutoCreateRowSorter(true);
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
		scroll = new JScrollPane(table);
		scroll.setBackground(Color.WHITE);
		conditionPanel.add(groupBtn);
		conditionPanel.add(instantBtn);
		conditionPanel.add(allBtn);
		conditionPanel.add(refeshBtn);
		
		searchPanel.add(exportBtn);
		searchPanel.add(deleteBtn);
		searchPanel.add(emailBtn);
		searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT,10,5));
		topPanel.add(searchPanel,BorderLayout.EAST);
		topPanel.add(conditionPanel,BorderLayout.WEST);
		
		bottomLeft.add(pageLabel);
		bottomLeft.add(collectLabel);
		bottomLeft.add(collectText);
		bottomPanel.add(bottomLeft,BorderLayout.WEST);
		
		add(topPanel,BorderLayout.NORTH);
		add(scroll);
		add(bottomPanel,BorderLayout.SOUTH);
		setSize(pencil.showWidth,pencil.showHeight-75);
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
            //table.getValueAt(nowSelect, column);
        }
    }
	//数据库一写多读
	public void MenuControl(boolean dbState){
		instantBtn.setEnabled(dbState);
		deleteBtn.setEnabled(dbState);
		refeshBtn.setEnabled(dbState);
		groupBtn.setEnabled(dbState);
		emailBtn.setEnabled(dbState);
		for(int i=0;i<pageList.length;i++)
			pageList[i].setEnabled(dbState);
		repaint();
	}
	/* 查询完成后更新底部的汇总信息和分页按钮
	 * sql 语句，type=区分是否显示分页按钮（1=>分页搜索，0=>搜索全部，2=>页码搜索）
	 */
	public void updateBottom(String sql,int type) throws SQLException{
		if(type<2){
			btnNum = 1;
			nowIndex = 1;
			bottomRight.removeAll();
			dataTotle = StudentLog.getNumber(sql);
			collectText.setText(" 总记录数："+dataTotle);
		}
		if(type==1){
			btnNum = Math.ceil((float)dataTotle/(float)dataNum);
			for(int i=0;i<4;i++){
				bottomRight.add(pageList[i]);
			}
			bottomPanel.add(bottomRight,BorderLayout.EAST);
			nowIndex = 1;
		}
		if(type>0){
			if(nowIndex==1){
				pageList[0].setEnabled(false);
				pageList[1].setEnabled(false);
			}else{
				pageList[0].setEnabled(true);
				pageList[1].setEnabled(true);
			}
			if(nowIndex==btnNum){
				pageList[3].setEnabled(false);
				pageList[2].setEnabled(false);
			}else{
				pageList[3].setEnabled(true);
				pageList[2].setEnabled(true);
			}
			if(btnNum<=1){
				for(int i=0;i<pageList.length;i++)
					pageList[i].setEnabled(false);
			}
		}
		pageLabel.setText("当前页："+nowIndex+" 总页面："+(int)btnNum);
		allBtn.setText("全部选中");
		bottomRight.updateUI();
		table.updateUI();
		this.updateUI();
	}
	
	//根据条件更新数据 condition=聚合查询（null不更新数据）	instant=模糊查询（null不更新数据）	type=true分页查询，false查询全部
	//得到不重复的s_id，得到不重复的e_id，合并。
	public void updateTabel(String condTemp[],String insTemp,boolean type){
		searchType = type;
		try {
			String eId = "";
			if(insTemp!=null&&!insTemp.equals(""))
				instant = "join (select s_id from fullsearch where fs_content match '"+insTemp+"') as fs on fs.s_id=s.s_id";
			if(condTemp!=null){	//当数据为null时，表示不更新搜索条件。	
				condition = condTemp[0];	//student表的检索条件
				eId = EducationLog.getEid(condTemp[1]);
				if(eId!="")	//组装了对两个表的搜索条件
					if(limitStr.equals(""))
						eduContion = "join (select * from education where e_id in ("+eId+"))";
					else
						eduContion = "join (select * from education where e_id in ("+eId+") and "+limitStr+")";
			}
			//考虑权限定义的内容
			if(eduContion.equals(""))
				if(limitStr.equals(""))
					eduContion = " join education "+eduContion;
				else
					eduContion = " join (select * from education where "+limitStr+") "+eduContion;
			//得到不重复的人，得到不重复的教育记录。
			data = StudentLog.dao("select "+cell+" from (select * from student "+condition+") s "+eduContion+" e on s.s_id=e.s_id "+instant+" order by s.update_time desc;");
			studentModel.setData(data);
			table = new MyTable(studentModel);
			updateBottom("select count(DISTINCT(s.s_id)) totle from (select DISTINCT(s_id) from student "+condition+") s "+eduContion+" e on s.s_id=e.s_id "+instant+" ;",0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
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
			updateTabel(null,null,searchType);
		}else if(btn==groupBtn){
			if(tabbedFrame==null||tabbedFrame.isClosed()){
				tabbedFrame = new TabbedFrame(this);
				pencil.desktopPane.add(tabbedFrame);
			}
			tabbedFrame.toFront();
		}else if(btn==instantBtn){
			if(blurSearchFrame==null||blurSearchFrame.isClosed()){
				blurSearchFrame = new BlurSearchFrame(this);
				pencil.desktopPane.add(blurSearchFrame);
			}
			blurSearchFrame.toFront();
		}else if(btn==deleteBtn){
			String selectIndex = studentModel.getSelect();
			if(selectIndex==null){
				JOptionPane.showMessageDialog(null, "选中表格前面的选框，进行数据删除或者导出Excel");
			}else{
				try {
					int res =JOptionPane.showConfirmDialog(this,"删除选中记录的所有信息？","删除信息提示",JOptionPane.YES_NO_OPTION);
					if(res==0){
						boolean result = StudentLog.deleteMany(selectIndex);
						if(!result)
							JOptionPane.showMessageDialog(null, "删除失败！");
						updateTabel(null,null,searchType);
						selectIndex=null;
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}else if(btn==emailBtn){
			if(studentModel.getSelectCount()==0)
				pencil.sendEmail(null);
			else
				pencil.sendEmail(studentModel);
		}else if(btn==exportBtn){
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
			pencil.outputExl(studentModel,1,df.format(new Date())+"导出Excel");
		}else if(btn==pageList[0]){
			try {
				nowIndex = 1;
				data = StudentLog.dao("select * from (select "+cell+" from student s "+eduContion+" e on s.s_id=e.s_id) as stu "+instant+" limit "+dataNum+" offset "+(nowIndex-1)*dataNum);
				studentModel.setData(data);
				table = new MyTable(studentModel);
				updateBottom(null,2);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}else if(btn==pageList[1]){
			try {
				nowIndex--;
				data = StudentLog.dao("select * from (select "+cell+" from student s "+eduContion+" e on s.s_id=e.s_id) as stu "+instant+" limit "+dataNum+" offset "+(nowIndex-1)*dataNum);
				studentModel.setData(data);
				table = new MyTable(studentModel);
				updateBottom(null,2);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}else if(btn==pageList[2]){
			try {
				nowIndex++;
				data = StudentLog.dao("select * from (select "+cell+" from student s "+eduContion+" e on s.s_id=e.s_id) as stu "+instant+" limit "+dataNum+" offset "+(nowIndex-1)*dataNum);
				studentModel.setData(data);
				table = new MyTable(studentModel);
				updateBottom(null,2);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}else if(btn==pageList[3]){
			try {
				nowIndex = (int) btnNum;
				data = StudentLog.dao("select * from (select "+cell+" from student s "+eduContion+" e on s.s_id=e.s_id) as stu "+instant+" limit "+dataNum+" offset "+(nowIndex-1)*dataNum);
				studentModel.setData(data);
				table = new MyTable(studentModel);
				updateBottom(null,2);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
}
