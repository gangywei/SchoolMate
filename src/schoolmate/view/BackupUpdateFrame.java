package schoolmate.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;

import schoolmate.control.Helper;
import schoolmate.control.tableModel.StudentModel;
import schoolmate.database.EducationLog;
import schoolmate.database.StudentLog;
import schoolmate.database.WorkLog;
import schoolmate.view.element.DateChooser;

public class BackupUpdateFrame extends JInternalFrame implements ActionListener{
	public JPanel BackupsPanel,interPanel;
	private String filePath;
	public int sheetCount=0,totleRow;
    private boolean threadCon = false;
    private JLabel nameLabel = new JLabel("文件名：");
    private JLabel beginLabel = new JLabel("开始时间：");
    private JLabel endLabel = new JLabel("结束时间：");
    private DateChooser beginChoose = DateChooser.getInstance("yyyyMMdd");
    private DateChooser endChoose = DateChooser.getInstance("yyyyMMdd");
    private JTextField beginInput = new JTextField("",19); 
    private JTextField endInput = new JTextField("",19); 
    SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
    private JTextField nameInput = new JTextField(df.format(new Date())+"导出更新数据",20);
    private JLabel pathLabel = new JLabel("文件路径：");
    private JLabel fileText = new JLabel("点击选择地址按钮：");
    private JLabel statusJLabel = new JLabel("备份状态：");
    private JLabel backupsStatus = new JLabel("未备份");
    private JButton selectPath = new JButton("选择地址");
    private JButton startBtn = new JButton("开  始");
    private Vector<Object[]> studentLog;
    private PencilMain pencil;
	public BackupUpdateFrame(PencilMain pencil) {
		this.pencil = pencil;
		Backups();
	}

	public void Backups() {
		interPanel = new JPanel(new BorderLayout(20,5));
		interPanel.setBackground(Color.WHITE);
		add(interPanel,BorderLayout.NORTH);
    	setClosable(true);//提供关闭按钮
    	setTitle("导出更新数据功能");
    	Calendar calen = Calendar.getInstance();	//得到下一天
    	calen.setTime(new Date());
    	calen.add(Calendar.DAY_OF_MONTH, 1);
    	endInput.setText(df.format(calen.getTime()));
    	endChoose.register(endInput);
    	beginChoose.register(beginInput);
    	selectPath.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));  
    	selectPath.setForeground(Color.white);  
    	selectPath.addActionListener(this);
    	startBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));  
    	startBtn.setForeground(Color.white);  
    	startBtn.addActionListener(this);
    	startBtn.setEnabled(false);
    	BackupsPanel = new JPanel();
    	BackupsPanel.setBackground(Color.white);
    	GroupLayout layout = new GroupLayout(BackupsPanel);
    	BackupsPanel.setLayout(layout);
		setSize(400, 360);
		setLocation((PencilMain.showWidth-400)/2, 0);
		setVisible(true);
		//创建GroupLayout的水平连续组，，越先加入的ParallelGroup，优先级级别越高。几列
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		hGroup.addGap(20);//添加间隔
		hGroup.addGroup(layout.createParallelGroup().addComponent(nameLabel).addComponent(beginLabel).addComponent(endLabel).addComponent(pathLabel)
				.addComponent(statusJLabel).addComponent(selectPath));
		hGroup.addGap(15);
		hGroup.addGroup(layout.createParallelGroup().addComponent(nameInput).addComponent(beginChoose).addComponent(endChoose).addComponent(fileText)
				.addComponent(backupsStatus).addComponent(startBtn));
		hGroup.addGap(20);
		layout.setHorizontalGroup(hGroup);//设置水平组
		//创建GroupLayout的垂直连续组，，越先加入的ParallelGroup，优先级级别越高。几行
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		vGroup.addGap(20);//添加间隔
		vGroup.addGroup(layout.createParallelGroup().addComponent(nameLabel).addComponent(nameInput));
		vGroup.addGap(20);
		vGroup.addGroup(layout.createParallelGroup().addComponent(beginLabel).addComponent(beginChoose));
		vGroup.addGap(20);
		vGroup.addGroup(layout.createParallelGroup().addComponent(endLabel).addComponent(endChoose));
		vGroup.addGap(20);
		vGroup.addGroup(layout.createParallelGroup().addComponent(pathLabel).addComponent(fileText));
		vGroup.addGap(20);
		vGroup.addGroup(layout.createParallelGroup().addComponent(statusJLabel).addComponent(backupsStatus));
		vGroup.addGap(20);
		vGroup.addGroup(layout.createParallelGroup().addComponent(selectPath).addComponent(startBtn));
		vGroup.addGap(20);
		layout.setVerticalGroup(vGroup);//设置垂直组
		add(BackupsPanel);
	}


	//导出EXCEL文件（文件路径，文件名）
    public void outputExcel(String path,String name,long beginTime,long endTime) throws IOException{
    	try {
    		if(pencil.nowUser.u_role==3)
    			studentLog = StudentLog.getUpdate(beginTime,endTime,"");
    		else
    			studentLog = StudentLog.getUpdate(beginTime,endTime,pencil.collectDataFrame.limitStr);
		} catch (SQLException e1) {
			e1.printStackTrace();
			JOptionPane.showMessageDialog(this, "导入信息数据量过大或者信息错误");
			return;
		}
		HSSFWorkbook workbook = new HSSFWorkbook();
	    HSSFSheet sheet = workbook.createSheet("sheet1");	// 创建工作表
	    HSSFRow headRow = sheet.createRow((int) 0);	//设置表头
	    int colLength = StudentModel.excelCol.length;
	    for(int i=0;i<colLength;i++)	//表头数据
    			headRow.createCell(i).setCellValue(StudentModel.excelCol[i]); 
	    new Thread(new Runnable(){	//表格数据
	    	File xlsFile;
	    	FileOutputStream xlsStream;
	    	public void run() {
	    		int cellNum,	//单元格数目
	    		size;	//数据大小
	    		try {
			    	xlsFile = new File(path+"\\"+name+".xls");
				    xlsStream = new FileOutputStream(xlsFile);
				} catch (IOException e) {
					if(e.getMessage().indexOf("无法访问")>0){
						JOptionPane.showMessageDialog(null, "文件被占用，无法访问");
					}else
						JOptionPane.showMessageDialog(null, e.getMessage());
					return;
				}
	    		threadCon = true;
	    		size = studentLog.size();
	    		if(size==0){
	    			JOptionPane.showMessageDialog(null, "没有数据需要导出，请重新选择日期");
	    			return;
	    		}else if(size>50000) {
	    			JOptionPane.showMessageDialog(null, "数据量过大，请先选择部分进行导入");
	    			return;
	    		}
	    		cellNum = studentLog.elementAt(0).length;
	    		backupsStatus.setText("正在导出更新的学生数据");
	    		HSSFCellStyle contextstyle =workbook.createCellStyle();	//单元格数据格式
	    		for (int row = 1; row <= size&&threadCon; row++){
    				HSSFRow rows = sheet.createRow(row);
    				try {
    					for (int col = 0; col < cellNum; col++){
    						HSSFCell contentCell = rows.createCell(col);
    						Object val = studentLog.elementAt(row-1)[col];
    						if(val==null)
    							rows.createCell(col).setCellValue("");
    						else{
    							boolean isNum = ((String) val).matches("^(-?\\d+)(\\.\\d+)?$");
    							if(isNum){
    								contextstyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,#0"));//数据格式只显示整数
    								contentCell.setCellStyle(contextstyle);
    								contentCell.setCellValue(Double.parseDouble(val.toString()));
    							}else{
    								contentCell.setCellValue(val.toString());
    							}
    						}
    					}
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null,"第"+row+"条记录存在空数据，请修改数据重新导出");
					}
	    		}
			    try {
				    workbook.write(xlsStream);
					xlsStream.close();
				} catch (IOException e){
					JOptionPane.showMessageDialog(null, e.getMessage());
				}
			    if(threadCon)
			    	backupsStatus.setText("导出完成，可以关闭窗口啦！！");
			    else
			    	backupsStatus.setText("任务中止 ！！");
	    	}
	    }).start(); 
	}
	
	public void actionPerformed(ActionEvent e) {
		JButton btn = (JButton)e.getSource();
		if(btn == selectPath){
			JFileChooser fileChoose = new JFileChooser("F:");
	        //是否可多选   
	        fileChoose.setMultiSelectionEnabled(false);
	        fileChoose.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	        //设置是否显示隐藏文件  
	        fileChoose.setFileHidingEnabled(false);
	        fileChoose.setAcceptAllFileFilterUsed(false);
	        int returnValue = fileChoose.showOpenDialog(null);
	        if (returnValue == JFileChooser.APPROVE_OPTION){  
	        	File file = fileChoose.getSelectedFile();
	        	if(file.exists()){
	        		filePath = file.toString();
	        		fileText.setText(filePath);
	        		startBtn.setEnabled(true);
	        	}else{
	        		JOptionPane.showMessageDialog(null, "目录不存在，请重新选择！");
	        	}
	        }
		}else if(btn == startBtn){
			String beginTime = beginInput.getText();
			String endTime = endInput.getText();
			if(nameInput.getText().equals("")){
				JOptionPane.showMessageDialog(null, "请输入文件名。");
				return;
			}else if(filePath==null){
				JOptionPane.showMessageDialog(null, "请选择备份文件地址。");
				return;
			}else if(beginTime.equals("")){
				JOptionPane.showMessageDialog(null, "请选择开始时间。");
				return;
			}else if(endTime.equals("")){
				JOptionPane.showMessageDialog(null, "请选择结束时间。");
				return;
			}else{
				try {
					long ntime = Helper.dataTime(endTime);
					long btime = Helper.dataTime(beginTime);
					if(ntime<=btime){
						JOptionPane.showMessageDialog(null, "结束时间小于开始时间。");
						return;
					}
					outputExcel(filePath,nameInput.getText(),btime,ntime);
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, "备份未成功！");
					e1.printStackTrace();
					return;
				}
			}
		}
	}
	public void doDefaultCloseAction() {  
	    dispose();
	}
}
