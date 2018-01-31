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
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;

import schoolmate.control.StudentModel;
import schoolmate.database.FacultyLog;
import schoolmate.database.StudentLog;
import schoolmate.model.DBConnect;
import schoolmate.model.Student;
import schoolmate.view.element.MyFileFilter;
import schoolmate.view.element.MyTextField;

public class ImportExlFrame extends JInternalFrame implements ActionListener{
	public JPanel importPanel,btnPanel;
	public String filePath;
	public File file = null;
	public JTable table;
	private boolean threadCon = true;	//线程正常执行
	private Thread importThreat;
	public StudentModel studentModel = new StudentModel(2);
	public Object[][] data = null;
	public ArrayList<String[]> errorList = new ArrayList<String[]>();
	private Workbook workbook;
	private XSSFWorkbook xwb;
	public int sheetCount=0,totleRow;
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
    private JProgressBar processBar = new JProgressBar();	//创建进度条 
    private JScrollPane scroll;
    private PencilMain pencil;
    private String[] facultyAry;
    private List<Object> facultyList;
    
    //导入Excel方法变量
    int userSize = studentModel.excelCol.length;
    private String user[] = new String[userSize];
    
    public ImportExlFrame(PencilMain pencil){
    	init();
    	this.pencil = pencil;
    }
    public void init(){
    	setClosable(true);//提供关闭按钮
    	setResizable(true);  //允许自由调整大小 
        setIconifiable(true); //设置提供图标化按钮
        setTitle("导入Excel信息");
    	table = new JTable(studentModel);
		table.setFillsViewportHeight(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		processBar.setStringPainted(true);// 设置进度条上的字符串显示，false则不能显示  
	    processBar.setBackground(Color.GREEN);
	    
	    facultyList = new ArrayList<Object>();
	    facultyAry = FacultyLog.allFaculty();
		for(int i=0;i<facultyAry.length;i++)
			facultyList.add(facultyAry[i]);
		
	    downloadBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));  
	    downloadBtn.setForeground(Color.white);  
	    downloadBtn.addActionListener(this);
	    downloadBtn.addActionListener(this);
    	importBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));  
    	importBtn.setForeground(Color.white);  
    	importBtn.addActionListener(this);
    	exportBtn.addActionListener(this);
    	exportBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));  
    	exportBtn.setForeground(Color.white);
    	stopBtn.addActionListener(this);
    	stopBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));  
    	stopBtn.setForeground(Color.white);  
    	startBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));  
    	startBtn.setForeground(Color.white);  
    	startBtn.addActionListener(this);
    	
    	btnControl(0);
    	
    	btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    	btnPanel.add(downloadBtn);btnPanel.add(importBtn);btnPanel.add(startBtn);
    	btnPanel.add(stopBtn);btnPanel.add(exportBtn);
    	
    	importPanel = new JPanel();
    	GroupLayout layout = new GroupLayout(importPanel);
    	importPanel.setLayout(layout);
		setSize(600, 590);
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
				.addComponent(processBar).addComponent(btnPanel));
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
		vGroup.addGroup(layout.createParallelGroup().addComponent(btnPanel));
		vGroup.addGap(20);
		layout.setVerticalGroup(vGroup);//设置垂直组
		scroll.setPreferredSize(new Dimension(400, 300));
		add(importPanel);
		add(scroll,BorderLayout.SOUTH);
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
    	}else if(type==3){	//结束导入
    		startBtn.setEnabled(true);
    		importBtn.setEnabled(true);
    		exportBtn.setEnabled(true);
    		stopBtn.setEnabled(false);
    	}else if(type==4){	//运行异常
    		importBtn.setEnabled(true);
    		startBtn.setEnabled(true);
    		exportBtn.setEnabled(false);
    		stopBtn.setEnabled(false);
    	}
    }
    
    public void selectFile() throws InvalidFormatException, IOException{
    	totleRow = 0;
    	fileText.setText(filePath);
    	if(file.getName().endsWith(".xls")){
    		workbook = WorkbookFactory.create(file);// 获得工作簿
    		sheetCount = workbook.getNumberOfSheets();// 获得工作表个数
    		for (int i = 0; i < sheetCount; i++){// 遍历工作表
        		Sheet sheet = workbook.getSheetAt(i);
        		totleRow += sheet.getLastRowNum();// 获得行数
    	    }
    	}else{
    		InputStream xlsxIo = new FileInputStream(filePath);
	        xwb = new XSSFWorkbook(xlsxIo); 
	        sheetCount = xwb.getNumberOfSheets();// 获得工作表个数
	        for (int i = 0; i < sheetCount; i++){// 遍历工作表
        		Sheet sheet = xwb.getSheetAt(i);
        		totleRow += sheet.getLastRowNum();// 获得行数
    	    }
    	}
    	numberText.setText("Excel 页表数= "+sheetCount+" 记录数= "+totleRow);
    	btnControl(1);
    }
    
    //导入xls的线程
    public class xlsThread extends Thread{
    	public void run(){
    		threadCon = true;
    		int errorCount = 0;
    		Connection connect=DBConnect.getConnection();
    		Statement stmt = null;
			try {
				stmt = connect.createStatement();
			} catch (SQLException e2) {
				e2.printStackTrace();
			}
    		pencil.dbControl(false);
        	DataFormatter formatter = new DataFormatter();
        	int nowCount = 0;
        	for (int i = 0; i < sheetCount&&threadCon; i++)// 遍历工作表
    	    {
    	    	Sheet sheet = workbook.getSheetAt(i);
    	    	int rows = sheet.getLastRowNum() + 1;	//获得行数
    	    	Row tmp = sheet.getRow(0);// 获得列数，先获得一行，在得到该行列数
    			if (tmp == null){
    			    continue;
    			}
    			int cols = userSize;	//得到列数
    			for (int nowRow = 1; nowRow < rows&&threadCon; nowRow++)// 读取数据
    			{
    				Row row = sheet.getRow(nowRow);
    				try{
	    				for (int col = 0; col < cols; col++){
	    					String val = formatter.formatCellValue(row.getCell(col));
	    					user[col] = val;
	    				}
    				} catch (RuntimeException e) {
    					System.out.println(e.getMessage());
    					threadCon = false;
						JOptionPane.showMessageDialog(null,"导入的Excel文件格式不符合要求");
						btnControl(4);
						break;
					}
    				nowCount++;
    				processBar.setString("导入第"+nowCount+"条 姓名"+user[1]);// 设置提示信息
    				importPanel.repaint();
    				Student stu = new Student(user[0], user[1], user[2], user[3], user[4], user[5], user[6], user[7], user[8], user[9], 
    						user[10], user[11], user[12], user[13], user[14], user[15], user[16],user[17], user[18], user[19], 
    						user[20], user[21], user[22], user[23], user[24], user[25], user[26],user[27], user[28], user[29],
    						user[30], user[31]);
    				try {
    					String res = null;
    					if(!user[1].trim().equals("")){
    						res = StudentLog.importExl(stmt, stu);
    					}
						if(res!=null){
							errorCount++;
							String[] temp = stu.toArray();
							temp[userSize] = res;
							studentModel.addRow(temp);
							importPanel.updateUI();
						}
					} catch (SQLException e) {
						System.out.println(e.getMessage());
						threadCon = false;
						JOptionPane.showMessageDialog(null,"导入的Excel文件内容不符合要求");
						btnControl(4);
						break;
					}
    			}
    	    }
        	try {
        		if(threadCon)
        			connect.commit();
			} catch (SQLException e) {
				e.printStackTrace();
			}
        	if(threadCon){
        		processBar.setString("导入失败的数据共 "+errorCount+" 条");
        		btnControl(3);
        	}else
        		processBar.setString("任务终止 ");
        	importPanel.updateUI();
        	pencil.dbControl(true);
    	}
    }
    
    //导入xlsx的线程
    public class xlsxThread extends Thread{
    	public void run(){
    		threadCon = true;
    		int errorCount = 0;
    		Connection connect=DBConnect.getConnection();
    		Statement stmt = null;
			try {
				stmt = connect.createStatement();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
        	pencil.dbControl(false);
        	DataFormatter formatter = new DataFormatter();
        	int nowCount = 0;
        	for (int i = 0; i < sheetCount&&threadCon; i++)// 遍历工作表
    	    {
        		XSSFSheet sheet = xwb.getSheetAt(i);
    	    	int rows = sheet.getLastRowNum() + 1;// 获得行数
    	    	XSSFRow tmp = sheet.getRow(0);// 获得列数，先获得一行，在得到该行列数
    			if (tmp == null){
    			    continue;
    			}
    			int cols = tmp.getPhysicalNumberOfCells();
    			for (int nowRow = 1; nowRow < rows&&threadCon; nowRow++){// 读取数据
    				XSSFRow row = sheet.getRow(nowRow);
    				try{
	    				for (int col = 0; col < cols; col++){
	    					String val = formatter.formatCellValue(row.getCell(col));
	    					user[col] = val;
	    				}
    				} catch (RuntimeException e) {
    					System.out.println(e.getMessage());
    					threadCon = false;
						JOptionPane.showMessageDialog(null,"导入的Excel文件格式不符合要求");
						btnControl(4);
						break;
    				}
    				nowCount++;
    				processBar.setString("导入第"+nowCount+"条 姓名"+user[1]);// 设置提示信息
    				importPanel.repaint();
    				Student stu = new Student(user[0], user[1], user[2], user[3], user[4], user[5], user[6], user[7], user[8], user[9], 
    						user[10], user[11], user[12], user[13], user[14], user[15], user[16],user[17], user[18], user[19], 
    						user[20], user[21], user[22], user[23], user[24], user[25], user[26],user[27], user[28], user[29],
    						user[30], user[31]);
    				try {
    					String res = null;
    					if(!user[1].trim().equals("")){
    						res = StudentLog.importExl(stmt, stu);
    					}
    					if(res!=null){
							errorCount++;
							String[] temp = stu.toArray();
							temp[userSize] = res;
							studentModel.addRow(temp);
							importPanel.updateUI();
						}
					} catch (SQLException e) {
						System.out.println(e.getMessage());
						threadCon = false;
						JOptionPane.showMessageDialog(null,"导入的Excel文件内容不符合要求");
						btnControl(4);
						break;
					}
    			}
    	    }
        	try {
        		if(threadCon)
        			connect.commit();
			} catch (SQLException e) {
				e.printStackTrace();
			}
        	if(threadCon){
        		processBar.setString("导入失败的数据共 "+errorCount+" 条");
        		btnControl(3);
        	}else
        		processBar.setString("任务终止 ");
        	importPanel.updateUI();
        	pencil.dbControl(true);
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
			pencil.outputExl(studentModel,0,df.format(new Date())+"导入错误Excel");
		}else if(btn==stopBtn){
			if(importThreat.getState()==Thread.State.RUNNABLE){
				threadCon = false;
				JOptionPane.showMessageDialog(null,"任务已结束");
			}else{
				JOptionPane.showMessageDialog(null,"任务没有执行");
			}
		}else if(btn==downloadBtn){
			StudentModel downModel = new StudentModel(1);
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
			pencil.outputExl(downModel,2,"标准Excel文档");
		}
    }
}