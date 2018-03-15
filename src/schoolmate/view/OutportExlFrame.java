package schoolmate.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;

import schoolmate.control.tableModel.StudentModel;
import schoolmate.view.element.MyTextField;

public class OutportExlFrame extends JInternalFrame implements ActionListener{
	public JPanel importPanel,checkPanel,btnPanel;
	private String filePath;
	private boolean threadCon;
	private int outputType;	//outputType=>判断导出的类型，1=>导出选中的数据。0=>导出全部数据,2=>导出标准Excel模板。
	public int sheetCount=0,totleRow,//选中的行数
			dataTotle=0;//所有的数据
	private StudentModel studentModel;
    private JLabel nameLabel = new JLabel("文件名：");
    private JTextField nameInput = new MyTextField("导出文件名",20);
    private JLabel pathLabel = new JLabel("文件路径：");
    private JLabel fileText = new JLabel("点击选择地址按钮：");
    private JLabel interLabel = new JLabel("数据记录：");
    private JButton fileBtn = new JButton("文件地址");
    private JButton startBtn = new JButton("开  始");
    private JButton stopBtn = new JButton("取  消");
    private JButton allBtn = new JButton("取消全选");
    private JLabel checkLabel = new JLabel("导出字段：");
    private JCheckBox[] checkBox;
    private int columNum;
    private int modelType;	//赋值于tablemodel的类型，判断使用导出数据库里的数据(多两个字段选择框和数据ID)，或者导出EXCEL
    private boolean[] checkRes;
    private JProgressBar processBar = new JProgressBar();	//创建进度条 
    public OutportExlFrame(StudentModel studentModel,int type,String name){
    	this.studentModel = studentModel;
    	nameInput.setText(name);
    	this.outputType = type;
    	init();
    }

    public void init(){
    	setClosable(true);//提供关闭按钮
    	setResizable(true);  //允许自由调整大小 
    	setIconifiable(true); //设置提供图标化按钮
    	setTitle("导出Excel信息");
    	checkPanel = new JPanel(new GridLayout(6,6));
    	checkPanel.add(checkLabel);
    	modelType = studentModel.type;
    	dataTotle = studentModel.data.size();
    	if(outputType==1){
    		totleRow = studentModel.getSelectCount();
			processBar.setString("数据记录总数 "+totleRow);
		}else if(outputType==0){
			processBar.setString("数据记录总数 "+dataTotle);
		}else if(outputType==2){
			allBtn.setEnabled(false);
			processBar.setString("选择导出路径，导出默认Excel");
		}
    	
    	//根据tableModel的类型显示可以选择的导出字段
    	columNum = studentModel.nowColumn.length;
    	if(modelType==0){	//当表格类型为0时，去掉选择框的表头字段
    		columNum-=1;
    	}
    	checkRes = new boolean[columNum];
    	checkBox = new JCheckBox[columNum];
    	//table表头字段的判断。
    	for(int i=0;i<columNum;i++){	
    		if(modelType==0)
    			checkBox[i] = new JCheckBox(studentModel.nowColumn[i+1],true);
    		else
    			checkBox[i] = new JCheckBox(studentModel.nowColumn[i],true);
    		checkPanel.add(checkBox[i]);
    	}
    	
    	fileBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));  
    	fileBtn.setForeground(Color.white);  
    	fileBtn.addActionListener(this);
    	startBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));  
    	startBtn.setForeground(Color.white);  
    	startBtn.addActionListener(this);
    	stopBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));  
    	stopBtn.setForeground(Color.white);  
    	stopBtn.addActionListener(this);
    	allBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));  
    	allBtn.setForeground(Color.white);  
    	allBtn.addActionListener(this);
    	startBtn.setEnabled(false);
    	importPanel = new JPanel();
    	importPanel.setBackground(Color.WHITE);
    	GroupLayout layout = new GroupLayout(importPanel);
    	importPanel.setLayout(layout);
		setSize(800, 460);
		setLocation((PencilMain.showWidth-700)/2, 0);
		setVisible(true);
		
		processBar.setStringPainted(true);// 设置进度条上的字符串显示，false则不能显示  
	    
	    btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	    btnPanel.add(fileBtn);btnPanel.add(allBtn);btnPanel.add(startBtn);btnPanel.add(stopBtn);
		//创建GroupLayout的水平连续组，，越先加入的ParallelGroup，优先级级别越高。几列
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		hGroup.addGap(20);//添加间隔
		hGroup.addGroup(layout.createParallelGroup().addComponent(nameLabel).addComponent(pathLabel).addComponent(checkLabel)
				.addComponent(interLabel));
		hGroup.addGap(15);
		hGroup.addGroup(layout.createParallelGroup().addComponent(nameInput).addComponent(fileText).addComponent(checkPanel)
				.addComponent(processBar).addComponent(btnPanel));
		hGroup.addGap(20);
		layout.setHorizontalGroup(hGroup);//设置水平组
		//创建GroupLayout的垂直连续组，，越先加入的ParallelGroup，优先级级别越高。几行
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		vGroup.addGap(20);//添加间隔
		vGroup.addGroup(layout.createParallelGroup().addComponent(nameLabel).addComponent(nameInput));
		vGroup.addGap(20);
		vGroup.addGroup(layout.createParallelGroup().addComponent(pathLabel).addComponent(fileText));
		vGroup.addGap(20);
		vGroup.addGroup(layout.createParallelGroup().addComponent(checkLabel).addComponent(checkPanel));
		vGroup.addGap(20);
		vGroup.addGroup(layout.createParallelGroup().addComponent(interLabel).addComponent(processBar));
		vGroup.addGap(10);
		vGroup.addGroup(layout.createParallelGroup().addComponent(btnPanel));
		vGroup.addGap(20);
		layout.setVerticalGroup(vGroup);//设置垂直组
		add(importPanel);
		btnControl(0);
    }
    
    public void btnControl(int type){
    	switch (type) {
		case 0:	//开始页面
			startBtn.setEnabled(false);
			stopBtn.setEnabled(false);
			break;
		case 1:	//任务开始
			nameInput.setEditable(false);
			fileBtn.setEnabled(false);
			allBtn.setEnabled(false);
			stopBtn.setEnabled(true);
			break;
		case 2:	//任务结束
			nameInput.setEditable(true);
			stopBtn.setEnabled(false);
			fileBtn.setEnabled(true);
			allBtn.setEnabled(true);
			break;
		case 3:
			startBtn.setEnabled(true);
			break;
		}
    }
    
    //导出EXCEL文件（文件路径，文件名）
    public void outputExcel(String path,String name) throws IOException{
    	btnControl(1);
		HSSFWorkbook workbook = new HSSFWorkbook();
	    HSSFSheet sheet = workbook.createSheet("sheet1");	// 创建工作表
	    HSSFRow headRow = sheet.createRow((int) 0);	//设置表头
	    int colLength = studentModel.nowColumn.length;
	    int nowSpan = 0;	//连接上一个导出的单元格
	    if(modelType==0){
		    for(int i=1;i<colLength;i++)	//表头数据,去掉一个选择框
		    	if(checkRes[i-1])
		    		headRow.createCell(i-1-nowSpan).setCellValue(studentModel.nowColumn[i]); 
		    	else
		    		nowSpan++;
	    }else{
	    	for(int i=0;i<colLength;i++)	//表头数据
	    		if(checkRes[i])
	    			headRow.createCell(i-nowSpan).setCellValue(studentModel.nowColumn[i]); 
	    		else
	    			nowSpan++;
	    }
	    new Thread(new Runnable(){	//表格数据
	    	File xlsFile;
	    	FileOutputStream xlsStream;
	    	int colLength = studentModel.nowColumn.length;
	    	public void run() {
	    		try {
			    	xlsFile = new File(path+"\\"+name+".xls");
				    xlsStream = new FileOutputStream(xlsFile);
				} catch (IOException e) {
					if(e.getMessage().indexOf("无法访问")>0){
						JOptionPane.showMessageDialog(null, "文件被占用，无法访问");
					}else
						JOptionPane.showMessageDialog(null, e.getMessage());
					btnControl(2);
					return;
				}
	    		threadCon = true;
	    		int begin = 0;
	    		if(modelType==0){	//因为数据库中有一个隐藏的ID字段，位于index=1，初始化begin=2，并且数组长度+1；
	    	    	begin = 2;
	    	    	colLength+=1;
	    		}
	    		int nowOut = 0;//当前导出的选中的记录数
	    		HSSFCellStyle contextstyle =workbook.createCellStyle();	//单元格数据格式
	    		for (int row = 1; row <= dataTotle&&threadCon; row++){
	    			if(((outputType==1&&(Boolean)studentModel.data.elementAt(row-1)[0]))||outputType!=1){
	    				HSSFRow rows;
	    				if(outputType==1){
	    					nowOut++;
	    					if(nowOut>totleRow)
	    						break;
	    					rows = sheet.createRow(nowOut);
	    				}else{
	    					rows = sheet.createRow(row);
	    				}
	    				int nowSpan = 0;	//控制每次从前一个表格后面开始填充
	    				//nowCell从零开始递增
    					for (int col = begin,nowCell = 0; col < colLength; col++,nowCell++){
    						if(checkRes[nowCell]){
    							HSSFCell contentCell = rows.createCell(nowCell-nowSpan);
    							Object data = studentModel.data.elementAt(row-1)[col];
    							if(data!=null){
	    							boolean isNum = ((String) data).matches("^(-?\\d+)(\\.\\d+)?$");
	    							if(isNum){
	    								contextstyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,#0"));//数据格式只显示整数
	    								contentCell.setCellStyle(contextstyle);
	    								contentCell.setCellValue(Double.parseDouble(data.toString()));
	    							}else{
	    								contentCell.setCellValue(data.toString());
	    							}
    							}else
    								contentCell.setCellValue("");
    						}else
    							nowSpan++;
		    			}
	    			}
	    			processBar.setString("正在导出第 "+row+" 条记录，总数 "+dataTotle);
	    		}
	    		processBar.setString("正在保存文件 ");
			    try {
				    workbook.write(xlsStream);
					xlsStream.close();
				} catch (IOException e){
					JOptionPane.showMessageDialog(null, e.getMessage());
				}
			    if(threadCon)
			    	processBar.setString("导出完成，可以关闭窗口啦！！");
			    else
			    	processBar.setString("任务中止 ！！");
			    btnControl(2);
	    	}
	    }).start(); 
	}
    
    //得到选择的导出字段
    public void outputColum(){
    	for(int i=0;i<columNum;i++){
    		if(checkBox[i].isSelected())
    			checkRes[i] = true;
    	}
    }
    
    public void actionPerformed(ActionEvent e) {
		JButton btn = (JButton)e.getSource();
		if(btn==fileBtn){
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
	        	if(file.isDirectory()){
	        		filePath = file.toString();
		        	fileText.setText(filePath);
		        	btnControl(3);
	        	}else{
	        		JOptionPane.showMessageDialog(null, "不存在该目录，请重新选择目录。");
	        	}
	        }
		}else if(btn==startBtn){
			if(nameInput.getText().trim().equals("")){
				JOptionPane.showMessageDialog(null, "请输入文件名。");
				return;
			}else{
				try {
					outputColum();
					outputExcel(filePath,nameInput.getText());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}else if(btn==stopBtn){
			int res =JOptionPane.showConfirmDialog(this,"确定停止该任务吗？","任务提示",JOptionPane.YES_NO_OPTION);
			if(res==0)
				threadCon = false;
		}else if(btn==allBtn){
			if(allBtn.getText()=="字段全选"){
				allBtn.setText("取消全选");
				for(int i=0;i<columNum;i++)
					checkBox[i].setSelected(true);
			}else{
				allBtn.setText("字段全选");
				for(int i=0;i<columNum;i++)
					checkBox[i].setSelected(false);
			}
		}
    }
    
	public void doDefaultCloseAction() {  
	    this.setVisible(false);// 我们只让该JInternalFrame隐藏，并不是真正的关闭  
	}
}


