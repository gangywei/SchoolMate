package schoolmate.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Calendar;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import schoolmate.database.AddressLog;
import schoolmate.database.FacultyLog;
import schoolmate.database.MajorLog;
import schoolmate.database.StudentLog;
import schoolmate.view.element.MyCheckList;

public class TabbedFrame extends JInternalFrame implements ActionListener,ChangeListener,ItemListener{
	private JPanel[] panels = new JPanel[7];
	private String[] condition = new String[8];
	private String[] dbCondition = new String[8];
	private JLabel sexLabel,educationLabel,inLabel,outLabel;
	private JPanel sexPanel,educationPanel,btnPanel,yearPanel;
	private boolean[] conditionState = new boolean[7];
	private JButton searchBtn = new JButton("分页查询");
	private JButton searchAllBtn = new JButton("查询所有");
	private JRadioButton[] sexRadio = {new JRadioButton("男"),new JRadioButton("女"),new JRadioButton("无")};
	private JCheckBox[] educationBox = {new JCheckBox("专科"),new JCheckBox("本科"),
			new JCheckBox("硕士"),new JCheckBox("博士")};
	private JCheckBox[] facultyBox,majorBox,nationBox,provinceBox,cityBox,workBox;
	private String[] labelName = {"其他条件","学院","专业","工作国家","工作省份","工作市区","工作职称"};
	private JTabbedPane jTabbed = new JTabbedPane(JTabbedPane.TOP);
	private String[] faculty,major,nation,province,city,work;
	private MyCheckList inList,outList;
	private int selectIndex;
	private Calendar date = Calendar.getInstance();
	private int inYear = 1985,outYear = date.get(Calendar.YEAR);
	private String years[] = new String[outYear-inYear+1];
	private CollectDataFrame collect;
	private String[] inArray,outArray;
	public TabbedFrame(CollectDataFrame collect){
		init();
		this.collect = collect;
	}
	
	public void init(){
		for(int now=outYear,i=0;now>=inYear;now--,i++)
			years[i] = now+"";
		
		setClosable(true);//提供关闭按钮
    	setResizable(true);  //允许自由调整大小 
    	setTitle("组合搜索功能");
    	
    	GridLayout layout = new GridLayout(10,10);
		for(int i=0;i<labelName.length;i++){
			panels[i] = new JPanel();
			if(i!=0)
				panels[i].setLayout(layout);
			else{
				panels[i].setLayout(new FlowLayout(FlowLayout.CENTER));
			}
			jTabbed.add(panels[i],labelName[i]);
		}
		
		yearPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		inLabel = new JLabel("入学年份：");
		outLabel = new JLabel("毕业年份：");
		inList = new MyCheckList(years);
		outList = new MyCheckList(years);
		yearPanel.add(inLabel);
		yearPanel.add(inList);
		yearPanel.add(outLabel);
		yearPanel.add(outList);
		
		sexLabel = new JLabel("性别：");
		educationLabel = new JLabel("学历：");
		sexPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,10,10));
		ButtonGroup sexGroup = new ButtonGroup();
		sexGroup.add(sexRadio[0]);
		sexGroup.add(sexRadio[1]);
		sexGroup.add(sexRadio[2]);
		sexPanel.add(sexLabel);
		sexPanel.add(sexRadio[0]);
		sexPanel.add(sexRadio[1]);
		sexPanel.add(sexRadio[2]);
		
		educationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,10,10));
		educationPanel.add(educationLabel);
		educationPanel.add(educationBox[0]);
		educationPanel.add(educationBox[1]);
		educationPanel.add(educationBox[2]);
		educationPanel.add(educationBox[3]);
		
		btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT,20,5));
		searchBtn.addActionListener(this);
		searchAllBtn.addActionListener(this);
		
		btnPanel.add(searchBtn);
		btnPanel.add(searchAllBtn);
		
		
		panels[0].add(sexPanel);
		panels[0].add(educationPanel);
		panels[0].add(yearPanel);
		
		
		add(jTabbed);
		add(btnPanel,BorderLayout.SOUTH);
		setSize(670, 460);
		setLocation((PencilMain.showWidth-670)/2, 0);
		jTabbed.addChangeListener(this);
		setVisible(true);
	}
	//得到检索条件
	public String getCondition(){
		
		String str = "";
//		if(conditionState[1]&&conditionState[2]){
//			JOptionPane.showMessageDialog(null, "院系的数据更新，请重新选择专业字段");
//			return null;
//		}else if(conditionState[3]&&conditionState[4]){
//			JOptionPane.showMessageDialog(null, "工作国家的数据更新，请重新选择工作省份和工作市区字段");
//			return null;
//		}else if(conditionState[4]&&conditionState[5]){
//			JOptionPane.showMessageDialog(null, "工作省份的数据更新，请重新选择工作市区");
//			return null;
//		}
		int i;
		//性别
		dbCondition[0] = null;
		for(i=0;i<sexRadio.length;i++){
			if(i<2&&sexRadio[i].isSelected()){
				dbCondition[0] = "(s_sex='"+sexRadio[i].getText()+"')";
			}
		}
		//学历
		dbCondition[7] = null;
		for(i=0;i<educationBox.length;i++){
			if(educationBox[i].isSelected()){
				if(dbCondition[7]==null){
					dbCondition[7] = "(s_education='"+educationBox[i].getText()+"'";
				}else{
					dbCondition[7] += " or s_education='"+educationBox[i].getText()+"'";
				}
			}
		}
		if(dbCondition[7]!=null)
			dbCondition[7]+=')';
		//专业
		dbCondition[2] = null;
		for(i=0;(majorBox!=null)&&i<majorBox.length;i++){
			if(majorBox[i].isSelected()){
				if(dbCondition[2]==null){
					dbCondition[2] = "(s_major='"+majorBox[i].getText()+"'";
					}else{
						dbCondition[2] += " or s_major='"+majorBox[i].getText()+"'";
					}
			}
		}
		if(dbCondition[2]!=null)
			dbCondition[2]+=')';
		//市区
		dbCondition[5] = null;
		for(i=0;(cityBox!=null)&&i<cityBox.length;i++){
			if(cityBox[i].isSelected()){
				if(dbCondition[5]==null){
					dbCondition[5] = "(s_city='"+cityBox[i].getText()+"'";
					}else{
						dbCondition[5] += " or s_city='"+cityBox[i].getText()+"'";
					}
			}
		}
		if(dbCondition[5]!=null)
			dbCondition[5]+=')';
		//职务
		dbCondition[6] = null;
		for(i=0;(workBox!=null)&&i<workBox.length;i++){
			if(workBox[i].isSelected()){
				if(dbCondition[6]==null){
					dbCondition[6] = "(s_worktitle='"+workBox[i].getText()+"'";
					}else{
						dbCondition[6] += " or s_worktitle='"+workBox[i].getText()+"'";
					}
			}
		}
		if(dbCondition[6]!=null)
			dbCondition[6]+=')';
		for(i=0;i<dbCondition.length;i++){
			if(dbCondition[i]!=null){
				if(str.equals("")){
					str = "where "+dbCondition[i];
				}else{
					str += " and "+dbCondition[i];
				}
			}
		}
		//入学和毕业时间
		inArray = inList.getSelectVal();
		String inCondition = null;
		for(i=0;i<inArray.length;i++){
			if(inCondition==null){
				inCondition="(s_graduate='"+inArray[i]+"'";
			}else{
				inCondition+=" or s_graduate='"+inArray[i]+"'";
			}
		}
		if(inCondition!=null){
			inCondition+=')';
			if(str.equals("")){
				str = "where "+inCondition;
			}else{
				str+="and "+inCondition;
			}
		}
		outArray = inList.getSelectVal();
		String outCondition = null;
		for(i=0;i<inArray.length;i++){
			if(outCondition==null){
				outCondition="(s_graduate='"+inArray[i]+"'";
			}else{
				outCondition+=" or s_graduate='"+inArray[i]+"'";
			}
		}
		if(outCondition!=null){
			outCondition+=')';
			if(str.equals("")){
				str = "where "+outCondition;
			}else{
				str+="and "+outCondition;
			}
		}
		System.out.println(str);
		return str;
	}
	//{"其他","学院","专业","国家","省份","市区","职务"};
	public void stateChanged(ChangeEvent e) {
		selectIndex = jTabbed.getSelectedIndex();
		switch(selectIndex){
		case 1:	//学院
			if(facultyBox==null){
				faculty = FacultyLog.allFaculty();
				int length = faculty.length;
				facultyBox = new JCheckBox[length];
				for(int i=0;i<length;i++){
					facultyBox[i] = new JCheckBox(faculty[i]);
					facultyBox[i].addItemListener(this);
					panels[selectIndex].add(facultyBox[i]);
				}
			}
			break;
		case 2:	//专业
			if(majorBox==null||conditionState[1]==true){
				majorBox = null;
				condition[1] = null;
				panels[selectIndex].removeAll();
				for(int i=0;(facultyBox!=null)&&i<facultyBox.length;i++){
					if(facultyBox[i].isSelected()){
						if(condition[1]==null){
							condition[1] = "f_name='"+facultyBox[i].getText()+"'";
							dbCondition[1] = "s_faculty='"+facultyBox[i].getText()+"'";
	 					}else{
	 						condition[1] += " or f_name='"+facultyBox[i].getText()+"'";
	 						dbCondition[1] += " or s_faculty='"+facultyBox[i].getText()+"'";
	 					}
					}
				}
				String str = "";
				if(condition[1]!=null)
					str = "where "+condition[1];	
				major = MajorLog.allMajor(str);
				int length = major.length;
				majorBox = new JCheckBox[length];
				for(int i=0;i<length;i++){
					majorBox[i] = new JCheckBox(major[i]);
					majorBox[i].addItemListener(this);
					panels[selectIndex].add(majorBox[i]);
				}
				conditionState[1]=false;
				conditionState[2]=false;
			}
			break;
		case 3:	//国家
			if(nationBox==null){
				nation = AddressLog.allNation();
				int length = nation.length;
				nationBox = new JCheckBox[length];
				for(int i=0;i<length;i++){
					nationBox[i] = new JCheckBox(nation[i]);
					nationBox[i].addItemListener(this);
					panels[selectIndex].add(nationBox[i]);
				}
			}
			break;
		case 4:	//省份
			if(provinceBox==null||conditionState[3]==true){
				provinceBox = null;
				condition[3] = null;
				panels[selectIndex].removeAll();
				for(int i=0;(provinceBox!=null)&&i<nationBox.length;i++){
					if(nationBox[i].isSelected()){
						if(condition[3]==null){
							condition[3] = "n_name='"+nationBox[i].getText()+"'";
							dbCondition[3] = "s_nation='"+nationBox[i].getText()+"'";
	 					}else{
	 						condition[3] += " or n_name='"+nationBox[i].getText()+"'";
	 						dbCondition[3] += " or s_nation='"+nationBox[i].getText()+"'";
	 					}
					}
				}
				String str = "";
				if(condition[3]!=null)
					str = "where "+condition[3];
				province = AddressLog.allProvince(str);
				int length = province.length;
				provinceBox = new JCheckBox[length];
				for(int i=0;i<length;i++){
					provinceBox[i] = new JCheckBox(province[i]);
					provinceBox[i].addItemListener(this);
					panels[selectIndex].add(provinceBox[i]);
				}
				conditionState[3]=false;
				conditionState[4]=false;
			}
			break;
		case 5:	//市区
			if(cityBox==null||conditionState[4]==true){
				cityBox = null;
				condition[4] = null;
				panels[selectIndex].removeAll();
				for(int i=0;(provinceBox!=null)&&i<provinceBox.length;i++){
					if(provinceBox[i].isSelected()){
						if(condition[4]==null){
							condition[4] = "p_name='"+provinceBox[i].getText()+"'";
							dbCondition[4] = "s_province='"+provinceBox[i].getText()+"'";
	 					}else{
	 						condition[4] += " or p_name='"+provinceBox[i].getText()+"'";
	 						dbCondition[4] += " or s_province='"+provinceBox[i].getText()+"'";
	 					}
					}
				}
				String str = "";
				if(condition[4]!=null)
					str = "where "+condition[4];
				city = AddressLog.allCity(str);
				int length = city.length;
				cityBox = new JCheckBox[length];
				for(int i=0;i<length;i++){
					cityBox[i] = new JCheckBox(city[i]);
					cityBox[i].addItemListener(this);
					panels[selectIndex].add(cityBox[i]);
				}
				conditionState[4]=false;
				conditionState[5]=false;
			}
			break;
		case 6:	//职称
			if(workBox==null){
				work = StudentLog.allWorkTitle();
				int length = work.length;
				workBox = new JCheckBox[length];
				for(int i=0;i<length;i++){
					workBox[i] = new JCheckBox(work[i]);
					workBox[i].addItemListener(this);
					panels[selectIndex].add(workBox[i]);
				}
			}
			break;
		}
		panels[selectIndex].repaint();
	}
	//{"其他","学院","专业","国家","省份","市区","职务"};
	public void itemStateChanged(ItemEvent e) {
		if(e.getSource() instanceof JCheckBox){
			JCheckBox item = (JCheckBox)e.getSource();
			conditionState[selectIndex] = true;
		}
	}
	
	public void doDefaultCloseAction() {  
	    this.setVisible(false);// 我们只让该JInternalFrame隐藏，并不是真正的关闭  
	}

	public void actionPerformed(ActionEvent e) {
		JButton btn = (JButton)e.getSource();
		String str = getCondition();
		if(btn==searchBtn){
			if(str!=null)
				collect.updateTabel(str,null,true);
		}else if(btn==searchAllBtn){
			if(str!=null)
				collect.updateTabel(str,null,false);
		}
	}  
}
