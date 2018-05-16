package schoolmate.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;
import schoolmate.control.tableModel.StudentModel;
import schoolmate.database.FacultyLog;
import schoolmate.database.StudentLog;
import schoolmate.model.DBConnect;
import schoolmate.model.Student;
import schoolmate.view.element.MyFileFilter;
import schoolmate.view.element.MyTextField;

public class ImportExlFrame extends JInternalFrame implements ActionListener{
	public int errorCount;	//导入信息的错误数量
	public JPanel importPanel,btnPanel,bottomPanel;
	public String filePath;
	public File file = null;
	public JTable table;
	private boolean threadCon = true;	//线程正常执行
	private Thread importThreat;
	private StudentModel errorModel = new StudentModel(2);
	public Object[][] data = null;
	public ArrayList<String[]> errorList = new ArrayList<String[]>();
	private Workbook workbook;
	private XSSFWorkbook xwb;
	public int totleRow;
	private JLabel fileLabel = new JLabel("文件地址：");
    private JLabel fileText = new JLabel();
    private JLabel numberLabel = new JLabel("记录数：");
    private JLabel numberText = new JLabel();
    private JLabel interLabel = new JLabel("导入记录：");
    private JButton downloadBtn = new JButton("下载模板");
    private JButton importBtn = new JButton("选择文件");
    private JButton startBtn = new JButton("开  始");
    private JButton stopBtn = new JButton("取  消");
    private JButton exportBtn = new JButton("导出数据");
    private JProgressBar processBar = new JProgressBar(0,100);	//创建进度条
    private JScrollPane scroll;
    private PencilMain pencil;
    private String[] facultyAry;
    private List<Object> facultyList;

    private JRadioButton imtype1 = new JRadioButton("检查学历");
	private JRadioButton imtype2 = new JRadioButton("跳过检查");

    //导入Excel方法变量
    int userSize = StudentModel.excelCol.length;
    private String user[] = new String[userSize];

    public ImportExlFrame(PencilMain pencil){
    	init();
    	this.pencil = pencil;
    }
    public void init(){
    	imtype1.setSelected(true);
    	setClosable(true);//提供关闭按钮
    	setResizable(true);  //允许自由调整大小
        setTitle("导入Excel信息");
    	table = new JTable(errorModel);
		table.setFillsViewportHeight(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		processBar.setStringPainted(true);// 设置进度条上的字符串显示，false则不能显示

	    facultyList = new ArrayList<Object>();
	    facultyAry = FacultyLog.allFaculty("");
		for(int i=0;i<facultyAry.length;i++)
			facultyList.add(facultyAry[i]);

	    downloadBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
	    downloadBtn.setForeground(Color.white);
	    downloadBtn.addActionListener(this);
    	importBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
    	importBtn.setForeground(Color.white);
    	importBtn.addActionListener(this);
    	exportBtn.addActionListener(this);
    	exportBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
    	exportBtn.setForeground(Color.white);
    	stopBtn.addActionListener(this);
    	stopBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
    	stopBtn.setForeground(Color.white);
    	startBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
    	startBtn.setForeground(Color.white);
    	startBtn.addActionListener(this);

    	btnControl(0);

    	ButtonGroup grp = new ButtonGroup();
    	grp.add(imtype1);grp.add(imtype2);

    	bottomPanel = new JPanel(new BorderLayout());

    	btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    	btnPanel.setBackground(Color.WHITE);
    	btnPanel.add(downloadBtn);btnPanel.add(importBtn);btnPanel.add(startBtn);btnPanel.add(stopBtn);
    	btnPanel.add(exportBtn);btnPanel.add(imtype1);btnPanel.add(imtype2);

    	importPanel = new JPanel();
    	importPanel.setBackground(Color.white);
    	GroupLayout layout = new GroupLayout(importPanel);
    	importPanel.setLayout(layout);
		setSize(660, 510);
		setLocation((PencilMain.showWidth-600)/2, 0);
		setVisible(true);
		scroll = new JScrollPane(table);
		//创建GroupLayout的水平连续组，，越先加入的ParallelGroup，优先级级别越高。几列
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		hGroup.addGap(20);//添加间隔
		hGroup.addGroup(layout.createParallelGroup().addComponent(fileLabel).addComponent(numberLabel)
				.addComponent(interLabel));
		hGroup.addGap(15);
		hGroup.addGroup(layout.createParallelGroup().addComponent(fileText).addComponent(numberText)
				.addComponent(processBar));
		hGroup.addGap(20);
		layout.setHorizontalGroup(hGroup);//设置水平组
		//创建GroupLayout的垂直连续组，，越先加入的ParallelGroup，优先级级别越高。几行
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		vGroup.addGap(20);//添加间隔
		vGroup.addGroup(layout.createParallelGroup().addComponent(fileLabel).addComponent(fileText));
		vGroup.addGap(20);
		vGroup.addGroup(layout.createParallelGroup().addComponent(numberLabel).addComponent(numberText));
		vGroup.addGap(20);
		vGroup.addGroup(layout.createParallelGroup().addComponent(interLabel).addComponent(processBar));
		vGroup.addGap(20);
		vGroup.addGroup(layout.createParallelGroup());
		vGroup.addGap(20);
		layout.setVerticalGroup(vGroup);//设置垂直组
		scroll.setPreferredSize(new Dimension(400, 300));
		add(importPanel);
		bottomPanel.add(btnPanel,BorderLayout.NORTH);
		bottomPanel.add(scroll);
		add(bottomPanel,BorderLayout.SOUTH);
		try{
        	setSelected(true);
        }catch(PropertyVetoException propertyVetoE){
            propertyVetoE.printStackTrace();
        }
    }

    public void btnControl(int type){
    	if(type==0){	//没有选择文件
    		startBtn.setEnabled(false);
    		exportBtn.setEnabled(false);
    		stopBtn.setEnabled(false);
    	}else if(type==1){	//选择了文件
    		startBtn.setEnabled(true);
    	}else if(type==2){	//开始导入文件
    		startBtn.setEnabled(false);
    		importBtn.setEnabled(false);
    		stopBtn.setEnabled(true);
    	}else if(type==3){	//结束导入,可以导出记录
    		startBtn.setEnabled(false);
    		importBtn.setEnabled(true);
    		if(errorCount>0)
    			exportBtn.setEnabled(true);
    		stopBtn.setEnabled(false);
    	}else if(type==4){	//运行异常
    		threadCon = false;
    		importBtn.setEnabled(true);
    		startBtn.setEnabled(true);
    		if(errorCount>0)
    			exportBtn.setEnabled(true);
    		stopBtn.setEnabled(false);
    	}
    }

    //得到文件的信息，并判断shell的正确性。
    public void selectFile() throws InvalidFormatException, IOException{
    	totleRow = 0;
    	Sheet sheet;
    	if(file.getName().endsWith(".xls")){
    		workbook = WorkbookFactory.create(file);// 获得工作簿
    		sheet = workbook.getSheetAt(0);
    	}else{
    		InputStream xlsxIo = new FileInputStream(filePath);
	        xwb = new XSSFWorkbook(xlsxIo);
    		sheet = xwb.getSheetAt(0);
    	}
		Row tmp = sheet.getRow(0);// 获得列数，先获得一行，在得到该行列数
		if(tmp==null){
			JOptionPane.showMessageDialog(null,"Excel文件不包含表头信息，请检验ExceL数据是否正确！");
		    return;
		}else{
			int tempNum = tmp.getPhysicalNumberOfCells();	//每一行的列数
			if(tempNum!=userSize){
				JOptionPane.showMessageDialog(null,"Excel文件表头信息不正确，请检验ExceL数据是否正确！");
			    return;
			}
			totleRow += sheet.getLastRowNum();// 获得行数
			fileText.setText(filePath);
	    	numberText.setText("数据记录数= "+totleRow+",仅供参考");
	    	btnControl(1);
		}
    }

    //导入xls的线程
    public class xlsThread extends Thread{
    	public void run(){
    		int type = 0;
    		if(imtype2.isSelected())
				type = 1;
    		Connection connect=DBConnect.getConnection();
    		Statement stmt = null;
			try {
				stmt = connect.createStatement();
			} catch (SQLException e2) {
				e2.printStackTrace();
			}
    		threadCon = true;
    		errorCount = 0;
    		pencil.dbControl(false);
        	DataFormatter formatter = new DataFormatter();
        	int nowCount = 0;
        	/*线程开始执行时 重置为true,程序执行报错时修改为false
        	 * 当变量为true时提交数据库更新，线程为一整个事务操作。
        	 */
	    	Sheet sheet = workbook.getSheetAt(0);
	    	int rows = sheet.getLastRowNum() + 1;	//获得行数
			int cols = userSize;	//得到列数
			for (int nowRow = 1; nowRow < rows&&threadCon; nowRow++){// 读取数据,除去表头
				try {
					Thread.sleep(25);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				Row row = sheet.getRow(nowRow);
				if(row!=null){	//该行不为空
    				try{
	    				for (int col = 0; col < cols; col++){
	    					String val = formatter.formatCellValue(row.getCell(col));
	    					user[col] = val;
	    				}
    				} catch (RuntimeException e) {
    					threadCon = false;
						JOptionPane.showMessageDialog(null,"导入的Excel文件格式不符合要求");
						btnControl(4);
						break;
					}
    				nowCount++;
    				processBar.setString("正在导入第"+nowCount+"条记录 姓名"+user[1]);// 设置提示信息
    				importPanel.repaint();
    				Student stu = new Student(user[0], user[1], user[2], user[3], user[4], user[5], user[6], user[7], user[8], user[9],
    						user[10], user[11], user[12], user[13], user[14], user[15], user[16],user[17], user[18], user[19],
    						user[20], user[21], user[22], user[23], user[24], user[25], user[26],user[27], user[28], user[29],
    						user[30], user[31]);
    				try {
    					String res = null;
    					if(!user[1].trim().equals("")){	//判断名字非空
    						res = StudentLog.importExl(stmt, stu, type);
    					}else{
    						res = "名字不可以为空";
    					}
						if(res!=null){
							errorCount++;
							String[] temp = stu.toArray();
							temp[0] = res;
							errorModel.addRow(temp);
							importPanel.updateUI();
						}
					} catch (SQLException e) {
						JOptionPane.showMessageDialog(null,"导入的Excel文件内容不符合要求");
						btnControl(4);
						break;
					}
				}else{
					processBar.setString("跳过空行");// 设置提示信息
				}
			}
        	if(threadCon){
        		try {
					connect.commit();
				} catch (SQLException e) {
					JOptionPane.showMessageDialog(null, e.getMessage());
					e.printStackTrace();
				}
				if(errorCount==0){
					processBar.setString("导入成功！！");
				}else{
					processBar.setString("导入失败的数目有  "+errorCount+"条,请导出错误Excel,修改错误数据并重新导入");
				}
				btnControl(3);
			}else{
				try {
					connect.rollback();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				processBar.setString("任务终止 ");
			}
        	try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
        	importPanel.updateUI();
        	pencil.dbControl(true);
        	pencil.collectDataFrame.refeshBtn.doClick();
    	}
    }

    //导入xlsx的线程
    public class xlsxThread extends Thread{
    	public void run(){
    		int type = 0;
    		if(imtype2.isSelected())
				type = 1;
    		Connection connect=DBConnect.getConnection();
    		Statement stmt = null;
			try {
				stmt = connect.createStatement();
			} catch (SQLException e2) {
				e2.printStackTrace();
			}
    		threadCon = true;
    		errorCount = 0;
        	pencil.dbControl(false);
        	DataFormatter formatter = new DataFormatter();
        	int nowCount = 0;
    		XSSFSheet sheet = xwb.getSheetAt(0);
	    	int rows = sheet.getLastRowNum() + 1;// 获得行数
			int cols = userSize;	//得到列数
			for (int nowRow = 1; nowRow < rows&&threadCon; nowRow++){// 读取数据
				try {
					Thread.sleep(25);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				XSSFRow row = sheet.getRow(nowRow);
				if(row!=null){	//该行不为空
					try{
	    				for (int col = 0; col < cols; col++){
	    					String val = formatter.formatCellValue(row.getCell(col));
	    					user[col] = val;
	    				}
					} catch (RuntimeException e) {
						threadCon = false;
						JOptionPane.showMessageDialog(null,"导入的Excel文件格式不符合要求");
						btnControl(4);
						break;
					}
					nowCount++;
					processBar.setString("正在导入第"+nowCount+"条记录 姓名"+user[1]);// 设置提示信息
					importPanel.repaint();
					Student stu = new Student(user[0], user[1], user[2], user[3], user[4], user[5], user[6], user[7], user[8], user[9],
							user[10], user[11], user[12], user[13], user[14], user[15], user[16],user[17], user[18], user[19],
							user[20], user[21], user[22], user[23], user[24], user[25], user[26],user[27], user[28], user[29],
							user[30], user[31]);
					try {
						String res = null;
						if(!user[1].trim().equals("")){
							res = StudentLog.importExl(stmt, stu, type);
						}else{
							res = "名字不可以为空";
						}
						if(res!=null){
							errorCount++;
							String[] temp = stu.toArray();
							temp[0] = res;
							errorModel.addRow(temp);
							importPanel.updateUI();
						}
					} catch (SQLException e) {
						JOptionPane.showMessageDialog(null,"导入的Excel文件内容不符合要求");
						btnControl(4);
						break;
					}
				}else{
					processBar.setString("跳过空行");// 设置提示信息
				}
			}

        	if(threadCon){
        		try {
					connect.commit();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				if(errorCount==0){
					processBar.setString("导入成功！！");
				}else{
					processBar.setString("导入失败的数目  "+errorCount+"！！请导出错误Excel,修改错误数据并重新导入");
				}
				btnControl(3);
			}else{
				try {
					connect.rollback();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				processBar.setString("任务终止 ");
			}
        	try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
        	importPanel.updateUI();
        	pencil.dbControl(true);
        	pencil.collectDataFrame.refeshBtn.doClick();
    	}
    }

    //按钮的监听事件
    public void actionPerformed(ActionEvent e) {
		JButton btn = (JButton)e.getSource();
		if(btn==importBtn){
			String[] arg = {"xls","xlsx"};
			MyFileFilter filter = new MyFileFilter(arg);
			JFileChooser fileChoose = new JFileChooser("F:");
			fileChoose.addChoosableFileFilter(filter);
	        //是否可多选
	        fileChoose.setMultiSelectionEnabled(false);
	        //以允许用户只选择文件 默认
	        fileChoose.setFileSelectionMode(JFileChooser.FILES_ONLY);
	        //设置是否显示隐藏文件
	        fileChoose.setFileHidingEnabled(false);
	        fileChoose.setAcceptAllFileFilterUsed(false);
	        int returnValue = fileChoose.showOpenDialog(null);
	        if (returnValue == JFileChooser.APPROVE_OPTION){
	        	filePath = fileChoose.getSelectedFile().toString();
	        	file = new File(filePath);
	        	try {
					selectFile();
				} catch (InvalidFormatException | IOException e2) {
					e2.printStackTrace();
				}
	        }
		}else if(btn==startBtn){
			if(file!=null){
				btnControl(2);
				errorModel.removeAll();
				if(file.getName().endsWith(".xls")){
	        		//开辟一个线程用来更新数据
					importThreat = new xlsThread();
					importThreat.start();
	        	}else if(file.getName().endsWith(".xlsx")){
	        		importThreat = new xlsxThread();
	        		importThreat.start();
	        	}else{
	        		JOptionPane.showMessageDialog(null,"文件类型错误，只支持excel文件", "导入学生信息", JOptionPane.WARNING_MESSAGE);
	        	}
			}
		}else if(btn==exportBtn){
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
			pencil.outputExl(errorModel,0,df.format(new Date())+"导入错误Excel");
		}else if(btn==stopBtn){
			int res =JOptionPane.showConfirmDialog(this,"停止任务，并清空导入错误的学生信息吗？","任务提示",JOptionPane.YES_NO_OPTION);
			if(res==0)
				threadCon = false;
			btnControl(4);
		}else if(btn==downloadBtn){
			StudentModel downModel = new StudentModel(1);
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
			pencil.outputExl(downModel,2,"标准Excel文档");
		}
    }
    public void doDefaultCloseAction() {
	    dispose();
	}
}