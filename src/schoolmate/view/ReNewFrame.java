package schoolmate.view;

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
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;

import schoolmate.view.element.MyFileFilter;

public class ReNewFrame extends JInternalFrame implements ActionListener{
	public JPanel BackupsPanel;
	private String filePath;
	public int sheetCount=0,totleRow;
	private InputStream input;
    private OutputStream output;
    private static int length;
    private JLabel pathLabel = new JLabel("文件路径：");
    private JLabel fileText = new JLabel("点击选择地址按钮：");
    private JLabel StatusJLabel = new JLabel("状态：");
    private JLabel BackupsStatus = new JLabel("未恢复");
    private JButton SelectPath = new JButton("选择地址");
    private JButton startBtn = new JButton("开  始");
	public ReNewFrame() {
		Backups();
	}
	public void Backups() {
    	setClosable(true);//提供关闭按钮
    	setTitle("数据恢复功能");
    	SelectPath.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));  
    	SelectPath.setForeground(Color.white);  
    	SelectPath.addActionListener(this);
    	startBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));  
    	startBtn.setForeground(Color.white);  
    	startBtn.addActionListener(this);
    	BackupsPanel = new JPanel();
    	GroupLayout layout = new GroupLayout(BackupsPanel);
    	BackupsPanel.setLayout(layout);
		setSize(450, 200);
		setLocation((PencilMain.showWidth-450)/2, 0);
		setVisible(true);
		//创建GroupLayout的水平连续组，，越先加入的ParallelGroup，优先级级别越高。几列
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		hGroup.addGap(20);//添加间隔
		hGroup.addGroup(layout.createParallelGroup().addComponent(pathLabel)
				.addComponent(StatusJLabel).addComponent(SelectPath));
		hGroup.addGap(15);
		hGroup.addGroup(layout.createParallelGroup().addComponent(fileText)
				.addComponent(BackupsStatus).addComponent(startBtn));
		hGroup.addGap(20);
		layout.setHorizontalGroup(hGroup);//设置水平组
		//创建GroupLayout的垂直连续组，，越先加入的ParallelGroup，优先级级别越高。几行
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		vGroup.addGap(20);//添加间隔
		vGroup.addGroup(layout.createParallelGroup().addComponent(pathLabel).addComponent(fileText));
		vGroup.addGap(20);
		vGroup.addGroup(layout.createParallelGroup().addComponent(StatusJLabel).addComponent(BackupsStatus));
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

        } finally {
            if (input != null && output != null) {
                try {
                    input.close(); // 关闭流
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                BackupsStatus.setText("恢复成功，可以关闭窗口啦!");
            }

        }
    }
	public void actionPerformed(ActionEvent e) {
		JButton btn = (JButton)e.getSource();
		if(btn == SelectPath){
			JFileChooser fileChoose = new JFileChooser("F:");
			String[] arg = {"db"};
			MyFileFilter filter = new MyFileFilter(arg);
	        //是否可多选   
			fileChoose.addChoosableFileFilter(filter);//导入可选择的文件的后缀名类型
	        fileChoose.setMultiSelectionEnabled(false);
	        fileChoose.setFileSelectionMode(JFileChooser.FILES_ONLY);
	        //设置是否显示隐藏文件  
	        fileChoose.setFileHidingEnabled(false);
	        fileChoose.setAcceptAllFileFilterUsed(false);
	        int returnValue = fileChoose.showOpenDialog(null);
	        if (returnValue == JFileChooser.APPROVE_OPTION)
	        {  
	        	filePath = fileChoose.getSelectedFile().toString();
	        	fileText.setText(filePath);
	        }
		}else if(btn == startBtn){
			if(filePath==null){
				JOptionPane.showMessageDialog(null, "为了恢复文件，请输入文件名。");
				return;
			}else{
				try {
					fileCopy(filePath,"E:\\assess\\schoolmates.db");
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	public void doDefaultCloseAction() {  
	    this.setVisible(false);		//我们只让该JInternalFrame隐藏，并不是真正的关闭  
	}
}