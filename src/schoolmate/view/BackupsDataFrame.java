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
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;

public class BackupsDataFrame extends JInternalFrame implements ActionListener{
	public JPanel BackupsPanel,interPanel;
	String str = new String(
			"<strong>数据备份功能</strong><br/><hr>"
			+ "<p>备份当前数据库里的所有数据！</p>");
	JEditorPane editPane = new JEditorPane("text/html", str);
	private String filePath;
	public int sheetCount=0,totleRow;
	private InputStream input;
    private OutputStream output;
    private static int length;
    private JLabel nameLabel = new JLabel("文件名：");
    SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
    private JTextField nameInput = new JTextField(df.format(new Date())+"数据备份");
    private JLabel pathLabel = new JLabel("文件路径：");
    private JLabel fileText = new JLabel("点击选择地址按钮");
    private JButton SelectPath = new JButton("选择地址");
    private JButton startBtn = new JButton("开  始");
	public BackupsDataFrame() {
		Backups();
	}

	public void Backups() {
		interPanel = new JPanel(new BorderLayout(20,5));
		interPanel.setBackground(Color.WHITE);
		editPane.setEnabled(false);
		interPanel.add(editPane);
		add(interPanel,BorderLayout.NORTH);
    	setClosable(true);//提供关闭按钮
    	setTitle("数据备份功能");
    	SelectPath.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));  
    	SelectPath.setForeground(Color.white);  
    	SelectPath.addActionListener(this);
    	startBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));  
    	startBtn.setForeground(Color.white);  
    	startBtn.addActionListener(this);
    	startBtn.setEnabled(false);
    	BackupsPanel = new JPanel();
    	BackupsPanel.setBackground(Color.white);
    	GroupLayout layout = new GroupLayout(BackupsPanel);
    	BackupsPanel.setLayout(layout);
		setSize(450, 260);
		setLocation((PencilMain.showWidth-450)/2, 0);
		setVisible(true);
		//创建GroupLayout的水平连续组，，越先加入的ParallelGroup，优先级级别越高。几列
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		hGroup.addGap(20);//添加间隔
		hGroup.addGroup(layout.createParallelGroup().addComponent(nameLabel).addComponent(pathLabel).addComponent(SelectPath));
		hGroup.addGap(15);
		hGroup.addGroup(layout.createParallelGroup().addComponent(nameInput).addComponent(fileText).addComponent(startBtn));
		hGroup.addGap(20);
		layout.setHorizontalGroup(hGroup);//设置水平组
		//创建GroupLayout的垂直连续组，，越先加入的ParallelGroup，优先级级别越高。几行
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		vGroup.addGap(20);//添加间隔
		vGroup.addGroup(layout.createParallelGroup().addComponent(nameLabel).addComponent(nameInput));
		vGroup.addGap(20);
		vGroup.addGroup(layout.createParallelGroup().addComponent(pathLabel).addComponent(fileText));
		vGroup.addGap(20);
		vGroup.addGroup(layout.createParallelGroup().addComponent(SelectPath).addComponent(startBtn));
		vGroup.addGap(20);
		layout.setVerticalGroup(vGroup);//设置垂直组
		add(BackupsPanel);
	}


	public void fileCopy(String beginFilename, String endFilename) {
        // 创建输入输出流对象
        try {
            input = new FileInputStream(beginFilename);
            File file = new File(endFilename);
            //判断文件夹是否存在,如果不存在则创建文件夹
            output = new FileOutputStream(endFilename);
            // 获取文件长度
            try {
                length = input.available();
                // 创建缓存区域
                byte[] buffer = new byte[length];
                // 将文件中的数据写入缓存数组
                input.read(buffer);
                // 将缓存数组中的数据输出到文件
                output.write(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        } finally {
            if (input != null && output != null) {
                try {
                    input.close(); // 关闭流
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                startBtn.setEnabled(false);
                JOptionPane.showMessageDialog(this, "备份已完成");
                dispose();
            }

        }
    }
	public void actionPerformed(ActionEvent e) {
		JButton btn = (JButton)e.getSource();
		if(btn == SelectPath){
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
			if(nameInput.getText().equals("")){
				JOptionPane.showMessageDialog(null, "请输入文件名。");
				return;
			}else if(filePath==null){
				JOptionPane.showMessageDialog(null, "请选择备份文件地址。");
				return;
			}else{
				try {
					fileCopy(PencilMain.PPATH+"/spy/mates.lite",
							filePath+"\\"+nameInput.getText()+".temp");
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
