package schoolmate.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;

import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;

import schoolmate.control.Helper;
import schoolmate.database.AddressLog;
import schoolmate.database.FacultyLog;
import schoolmate.database.MajorLog;
import schoolmate.database.UserLog;
import schoolmate.view.element.TabelPanel;

import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class AlterLabelFrame extends JInternalFrame implements ActionListener{
	private int type;	//判断修改哪一项
	private int labSize;
	private int rowIndex;
	private String[] textValue;	//原始的数据
	private JLabel[] labAry;
	private JPanel detailPanel,btnPanel;
	private JTextField[] textAry;
	private TabelPanel table;
	private JButton confirm = new JButton("确    认");  
	private JButton cancel = new JButton("取    消");
	public AlterLabelFrame(TabelPanel tableDemo,String[] label,String[] text,int index,int type){
		this.type = type;
		textValue = text;
		table = tableDemo;
		rowIndex = index;
		labSize = label.length;
		labAry = new JLabel[labSize];
		textAry = new JTextField[labSize];
		for(int i=0;i<labSize;i++){
			labAry[i] = new JLabel(label[i]+"：");
			textAry[i] = new JTextField(text[i],15);
		}
		initPanel();
	}
	
	public void initPanel(){
		setClosable(true);
		setTitle("修改导入字段");
		confirm.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));  
		confirm.setForeground(Color.white);  
		confirm.addActionListener(this);
		cancel.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));  
		cancel.setForeground(Color.white);  
		cancel.addActionListener(this);
		btnPanel = new JPanel();
		btnPanel.add(cancel);
		btnPanel.add(confirm);
		detailPanel = new JPanel();
		detailPanel.setBackground(Color.WHITE);
		GroupLayout layout = new GroupLayout(detailPanel);
		detailPanel.setLayout(layout);
		//创建GroupLayout的水平连续组，，越先加入的ParallelGroup，优先级级别越高。几列
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		hGroup.addGap(20);//添加间隔
		ParallelGroup labGroup = layout.createParallelGroup();
		for(int i=0;i<labSize;i++)
			labGroup.addComponent(labAry[i]);
		hGroup.addGroup(labGroup);
		hGroup.addGap(20);//添加间隔
		ParallelGroup textGroup = layout.createParallelGroup();
		for(int i=0;i<labSize;i++)
			textGroup.addComponent(textAry[i]);
		hGroup.addGroup(textGroup);
		layout.setHorizontalGroup(hGroup);//设置水平组
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		vGroup.addGap(15);
		for(int i=0;i<labSize;i++){
			vGroup.addGroup(layout.createParallelGroup().addComponent(labAry[i]).addComponent(textAry[i]));
			vGroup.addGap(15);
		}
		hGroup.addGap(20);//添加间隔
		vGroup.addGap(15);
		layout.setVerticalGroup(vGroup);//设置垂直组
		add(detailPanel,BorderLayout.CENTER);
		add(btnPanel,BorderLayout.SOUTH);
		setVisible(true);
		setSize(350, 80+50*labSize);
		setLocation((PencilMain.showWidth-350)/2, 30);
	}
	
	public void actionPerformed(ActionEvent e) {
		JButton btn = (JButton)e.getSource();
		if(btn==confirm){
			String nowValue[] = new String[labSize];
			boolean change = false;
			for(int i=0;i<labSize;i++){
				nowValue[i] = textAry[i].getText().trim();
				if(!nowValue[i].equals(textValue[i]))
					change = true;
			}
			if(change==false){
				JOptionPane.showMessageDialog(this, "请修改数据后再提交。");
				return;
			}
			boolean res = false;
			int title = 0;
			switch (type) {
			case 0:
				title =JOptionPane.showConfirmDialog(this,"该操作将修改所有关联数据的国家信息，是否继续？","任务提示",JOptionPane.YES_NO_OPTION);
				if(title==0)
					res = AddressLog.updateNation(textValue, nowValue);
				break;
			case 1:
				title =JOptionPane.showConfirmDialog(this,"该操作将修改所有关联数据的省份信息，是否继续？","任务提示",JOptionPane.YES_NO_OPTION);
				if(title==0)
					res = AddressLog.updateProvince(textValue, nowValue);
				break;
			case 2:
				title =JOptionPane.showConfirmDialog(this,"该操作将修改所有关联数据的市区信息，是否继续？","任务提示",JOptionPane.YES_NO_OPTION);
				if(title==0)
					res = AddressLog.updateCity(textValue, nowValue);
				break;
			case 3:
				title =JOptionPane.showConfirmDialog(this,"该操作将修改所有关联数据的学院信息，是否继续？","任务提示",JOptionPane.YES_NO_OPTION);
				if(title==0)
					res = FacultyLog.updateFactlty(textValue, nowValue);
				break;
			case 4:
				title =JOptionPane.showConfirmDialog(this,"该操作将修改所有关联数据的专业信息，是否继续？","任务提示",JOptionPane.YES_NO_OPTION);
				if(title==0)
					res = MajorLog.updateMajor(textValue, nowValue);
				break;
			default:
				break;
			}
			if(!res)
				JOptionPane.showMessageDialog(this, "更新数据库失败");
			else{
				//更新数据
				for(int i=0;i<labSize;i++)
					table.model.setCell(rowIndex, i, nowValue[i]);
				table.updateUI();
				dispose();
			}
		}else if(btn==cancel){
			dispose();
		}
	}

}
