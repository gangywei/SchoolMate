package schoolmate.view;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import schoolmate.control.StudentModel;
import schoolmate.database.AddressLog;
import schoolmate.database.FacultyLog;
import schoolmate.database.FullsearchLog;
import schoolmate.database.MajorLog;
import schoolmate.database.StudentLog;
import schoolmate.view.element.TableLeftMouse;

public class CollectDataFrame extends JInternalFrame implements ActionListener{
	public int dataNum = 2,dataTotle = 0,nowIndex = 1,nowSelect;
	double btnNum = 0;
	private JScrollPane scroll;
	public JTable table;
	public boolean searchType = true;	//true=分页,false=不分页;默认为分页
	public String instant="",condition="";	//模糊查询和组合查询的条件
	public StudentModel studentModel;
	public Vector<Object[]> data = null;
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
	private JButton allBtn = new JButton("全选");
	private TableLeftMouse tableLeftMouse;	//右键
	private JButton[] pageList = {new JButton("首页"),new JButton("上一页"),new JButton("下一页"),new JButton("尾页")};
	public PencilMain pencil;
	private TabbedFrame tabbedFrame;
	private BlurSearchFrame blurSearchFrame;
	public CollectDataFrame(PencilMain pencil) throws Exception{
		init();
		this.pencil = pencil;
	}
	public void init() throws Exception{
		setTitle("数据汇总功能");
		setClosable(true);  //提供关闭按钮
		setResizable(true);  //允许自由调整大小 
        setIconifiable(true); //设置提供图标化按钮
        setMaximizable(true); //设置提供最大化按钮
		data = StudentLog.dao("select * from student limit "+dataNum);
		setLayout(new BorderLayout());
		topPanel = new JPanel();
		searchPanel = new JPanel();
		conditionPanel = new JPanel();
		allBtn.addActionListener(this);
		emailBtn.addActionListener(this);
		groupBtn.addActionListener(this);
		deleteBtn.addActionListener(this);
		exportBtn.addActionListener(this);
		instantBtn.addActionListener(this);
		for(int i=0;i<4;i++){
			pageList[i].addActionListener(this);
		}
		topPanel.setLayout(new BorderLayout());
		bottomPanel = new JPanel();
		bottomPanel.setLayout(new BorderLayout(15,15));
		studentModel = new StudentModel(data,0);
		table = new JTable(studentModel);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setAutoCreateRowSorter(true);
		table.setFillsViewportHeight(true);
		tableLeftMouse = new TableLeftMouse(this);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);	//表格只允许单选
		table.getSelectionModel().addListSelectionListener(new RowListener());
		table.addMouseListener(new MouseListener() {
			public void mouseClicked(java.awt.event.MouseEvent e) {}
			public void mouseEntered(java.awt.event.MouseEvent e) {}
			public void mouseExited(java.awt.event.MouseEvent e) {}
			public void mousePressed(java.awt.event.MouseEvent e) {}
			public void mouseReleased(java.awt.event.MouseEvent e) {
				if (e.isPopupTrigger()){
					tableLeftMouse.show((JComponent)e.getSource(), e.getX(),e.getY());
				}
			}
		});
		
		updateBottom(null,1);
		
		scroll = new JScrollPane(table);
		conditionPanel.add(groupBtn);
		conditionPanel.add(instantBtn);
		conditionPanel.add(allBtn);
		
		searchPanel.add(exportBtn);
		searchPanel.add(deleteBtn);
		searchPanel.add(emailBtn);
		searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT,10,5));
		topPanel.add(searchPanel,BorderLayout.EAST);
		topPanel.add(conditionPanel,BorderLayout.WEST);
		
		bottomLeft.add(collectLabel);
		bottomLeft.add(collectText);
		bottomPanel.add(bottomLeft,BorderLayout.WEST);
		
		add(topPanel,BorderLayout.NORTH);
		add(scroll);
		add(bottomPanel,BorderLayout.SOUTH);
		setSize(PencilMain.showWidth,600);
		setVisible(true);
	}
	
	private class RowListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent event) {
            if (event.getValueIsAdjusting()) {
                return;
            }
            nowSelect = event.getLastIndex();
        }
    }
	//数据库一写多读
	public void MenuControl(boolean dbState){
		instantBtn.setEnabled(dbState);
		deleteBtn.setEnabled(dbState);
		groupBtn.setEnabled(dbState);
		for(int i=0;i<pageList.length;i++)
			pageList[i].setEnabled(dbState);
		repaint();
	}
	/* 查询完成后更新底部的汇总信息和分页按钮
	 * sql 语句，type=区分是否显示分页按钮（1=>分页搜索，0=>搜索全部，2=>页码搜索）
	 */
	public void updateBottom(String sql,int type) throws SQLException{
		if(type<2){
			bottomRight.removeAll();
			dataTotle = StudentLog.getNumber(sql);
			collectText.setText("该分类的总人数："+dataTotle);
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
		allBtn.setText("全选");
		bottomRight.updateUI();
		table.updateUI();
		this.updateUI();
	}
	
	//根据条件更新数据 condition=聚合查询（null不更新数据）	instant=模糊查询（null不更新数据）	type=true分页查询，false查询全部
	public void updateTabel(String condTemp,String insTemp,boolean type){
		searchType = type;
		try {
			if(insTemp!=null&&!insTemp.equals(""))
				instant = "join (select s_no from fullsearch where fs_content match '"+insTemp+"') as fs on fs.s_no=stu.s_no";
			if(condTemp!=null)	//当数据为null时，表示不更新搜索条件。
				condition = condTemp;
			System.out.println("select stu.* from (select * from student "+condition+") stu "+instant+" limit "+dataNum+";");
			if(type)
				data = StudentLog.dao("select * from (select * from student "+condition+" limit "+dataNum+") as stu "+instant+";");
			else
				data = StudentLog.dao("select stu.* from (select * from student "+condition+") stu "+instant+";");
			studentModel.setData(data);
			table = new JTable(studentModel);
			if(type)
				updateBottom("select count(*) totle from (select * from student "+condition+") as stu "+instant+" limit "+dataNum+";",1);
			else
				updateBottom("select count(*) totle from (select * from student "+condition+") stu "+instant+";",0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		JButton btn = (JButton)e.getSource();
		if(btn==allBtn){
			for(int i=0;i<data.size();i++){
				if(allBtn.getText()=="全选"){
					data.elementAt(i)[0] = true;
				}else{
					data.elementAt(i)[0] = false;
				}	
			}
			if(allBtn.getText()=="全选")
				allBtn.setText("取消");
			else
				allBtn.setText("全选");
			this.updateUI();
		}else if(btn==groupBtn){
			if(tabbedFrame==null){
				tabbedFrame = new TabbedFrame(this);
				pencil.desktopPane.add(tabbedFrame);
			}else{
				tabbedFrame.setVisible(true);
			}
			tabbedFrame.toFront();
		}else if(btn==instantBtn){
			if(blurSearchFrame==null){
				blurSearchFrame = new BlurSearchFrame(this);
				pencil.desktopPane.add(blurSearchFrame);
			}else{
				blurSearchFrame.setVisible(true);
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
				data = StudentLog.dao("select stu.* from (select * from student "+condition+" limit "+dataNum+" offset "+(nowIndex-1)*dataNum+") stu "+instant);
				studentModel.setData(data);
				table = new JTable(studentModel);
				updateBottom(null,2);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}else if(btn==pageList[1]){
			try {
				nowIndex--;
				data = StudentLog.dao("select stu.* from (select * from student "+condition+" limit "+dataNum+" offset "+(nowIndex-1)*dataNum+") stu "+instant);
				studentModel.setData(data);
				table = new JTable(studentModel);
				updateBottom(null,2);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}else if(btn==pageList[2]){
			try {
				nowIndex++;
				System.out.println("select stu.* from (select * from student "+condition+" limit "+dataNum+" offset "+(nowIndex-1)*dataNum+ ") stu "+instant);
				data = StudentLog.dao("select stu.* from (select * from student "+condition+" limit "+dataNum+" offset "+(nowIndex-1)*dataNum+ ") stu "+instant);
				studentModel.setData(data);
				table = new JTable(studentModel);
				updateBottom(null,2);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}else if(btn==pageList[3]){
			try {
				nowIndex = (int) btnNum;
				data = StudentLog.dao("select stu.* from (select * from student "+condition+" limit "+dataNum+" offset "+(nowIndex-1)*dataNum+") stu "+instant);
				studentModel.setData(data);
				table = new JTable(studentModel);
				updateBottom(null,2);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
}
