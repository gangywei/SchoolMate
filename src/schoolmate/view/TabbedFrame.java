package schoolmate.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;

import schoolmate.control.Helper;
import schoolmate.database.AddressLog;
import schoolmate.database.FacultyLog;
import schoolmate.database.MajorLog;
import schoolmate.database.StudentLog;
import schoolmate.view.element.MyCheckList;
import schoolmate.view.element.MyTextField;

public class TabbedFrame extends JFrame implements ActionListener,ChangeListener,ItemListener{
	private static final long serialVersionUID = -97852095904032076L;
	private JPanel[] panels = new JPanel[7];
	private String[] condition = new String[8];	//对其他数据库检索的数组
	private String[] dbCondition = new String[8];	//学生表检索条件的数组
	private JLabel sexLabel,educationLabel,inLabel,outLabel;
	private JPanel sexPanel,educationPanel,btnPanel,yearPanel,cityTopPanel,cityMidPanel;
	private JButton selectAll = new JButton("全部选择");
	private JButton searchAllBtn = new JButton("查询所有");
	private JCheckBox[] sexRadio = {new JCheckBox("男"),new JCheckBox("女")};
	private JCheckBox[] educationBox = {new JCheckBox("专科"),new JCheckBox("本科"),
			new JCheckBox("硕士"),new JCheckBox("博士")};
	private JCheckBox[] facultyBox,majorBox,nationBox,provinceBox;
	private String[] labelName = {"其他条件","学院","专业","工作国家","工作省份","工作市区"};
	private MyTextField cityField; //市区的模糊搜索按钮
	private JButton cityBtn = new JButton("确认选择");
	private List<Object> cityList;
	private ArrayList<JCheckBox> cityBox;
	private JTabbedPane jTabbed = new JTabbedPane(JTabbedPane.TOP);
	private String[] faculty,nation,province,city;
	String[][] major;
	private MyCheckList inList,outList;
	private int selectIndex;
	private Calendar date = Calendar.getInstance();
	private int inYear = 1952,outYear = date.get(Calendar.YEAR);
	private String years[] = new String[outYear-inYear+1];
	private CollectDataFrame collect;
	private String[] inArray,outArray;
	public TabbedFrame(CollectDataFrame collect){
		this.collect = collect;
		init();
	}
	
	public void init(){
		for(int now=outYear,i=0;now>=inYear;now--,i++)
			years[i] = now+"";
    	setResizable(true);  //允许自由调整大小 
    	setTitle("组合搜索功能");
    	if(PencilMain.nowUser.u_role<=2){	//普通用户权限(初始化查询条件)
	    	condition[1] = "f_name in "+collect.limitTemp;
			dbCondition[1] = collect.limitStr;
    	}
    	GridLayout layout = new GridLayout(15,8);
		for(int i=0;i<labelName.length;i++){
			panels[i] = new JPanel();
			if(i!=0&&i!=5&&i!=2)
				panels[i].setLayout(layout);
			else{
				panels[i].setLayout(new BorderLayout());
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
		sexPanel.add(sexLabel);
		sexPanel.add(sexRadio[0]);
		sexPanel.add(sexRadio[1]);
		
		educationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,10,10));
		educationPanel.add(educationLabel);
		educationPanel.add(educationBox[0]);
		educationPanel.add(educationBox[1]);
		educationPanel.add(educationBox[2]);
		educationPanel.add(educationBox[3]);
		
		btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,5));
		selectAll.addActionListener(this);
		selectAll.setForeground(Color.WHITE);
		selectAll.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));  
		searchAllBtn.addActionListener(this);
		searchAllBtn.setForeground(Color.WHITE);
		searchAllBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));  
		
		//btnPanel.add(searchBtn);
		btnPanel.add(searchAllBtn);
		
		
		panels[0].add(sexPanel,BorderLayout.NORTH);
		panels[0].add(educationPanel);
		panels[0].add(yearPanel,BorderLayout.SOUTH);
		
		
		add(jTabbed);
		add(btnPanel,BorderLayout.SOUTH);
		setSize(700, 450);
		setLocation((PencilMain.showWidth-700)/2, 0);
		jTabbed.addChangeListener(this);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				collect.condition = "";
				collect.eduContion = "";
				collect.tabbedFrame = null;
				dispose();
			}
		});
		setVisible(true);
		setLocationRelativeTo(null);
	}
	/*
	 * 遍历所有标签页，得到数据库的筛选条件。返回数组[联合查询条件+学生教育记录的查询条件]->提高查询效率
	 */
	public String[] getCondition(){
		String str = "";	//联合查询的条件
		String str2 = "";	//对于教育记录的索引条件
		int i;
		//性别
		dbCondition[0] = null;
		if(sexRadio[1].isSelected()&&sexRadio[0].isSelected())
			dbCondition[0] = "(s_sex!='')";
		else if(sexRadio[1].isSelected())
			dbCondition[0] = "(s_sex='女')";
		else if(sexRadio[0].isSelected())
			dbCondition[0] = "(s_sex='男')";
		//学历
		dbCondition[7] = null;
		for(i=0;i<educationBox.length;i++){
			if(educationBox[i].isSelected()){
				if(dbCondition[7]==null){
					dbCondition[7] = "(e.s_education='"+educationBox[i].getText()+"'";
				}else{
					dbCondition[7] += " or e.s_education='"+educationBox[i].getText()+"'";
				}
			}
		}
		if(dbCondition[7]!=null)
			dbCondition[7]+=')';
		//学院
		if(PencilMain.nowUser.u_role>=1){
			dbCondition[1] = null;
			for(i=0;(facultyBox!=null)&&i<facultyBox.length;i++){
				if(facultyBox[i].isSelected()){
					if(dbCondition[1]==null){
							dbCondition[1] = "(s_faculty='"+facultyBox[i].getText()+"'";
					}else{
						dbCondition[1] += " or s_faculty='"+facultyBox[i].getText()+"'";
					}
				}
			}
			if(dbCondition[1]!=null)
				dbCondition[1]+=')';
		}
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
		//国家
		dbCondition[3] = null;
		for(i=0;(nationBox!=null)&&i<nationBox.length;i++){
			if(nationBox[i].isSelected()){
				if(dbCondition[3]==null){
					dbCondition[3] = "(s_nation='"+nationBox[i].getText()+"'";
				}else{
					dbCondition[3] += " or s_nation='"+nationBox[i].getText()+"'";
				}
			}
		}
		if(dbCondition[3]!=null)
			dbCondition[3]+=')';
		//省份
		dbCondition[4] = null;
		for(i=0;(provinceBox!=null)&&i<provinceBox.length;i++){
			if(provinceBox[i].isSelected()){
				if(dbCondition[4]==null){
					dbCondition[4] = "(s_province='"+provinceBox[i].getText()+"'";
					}else{
						dbCondition[4] += " or s_province='"+provinceBox[i].getText()+"'";
					}
			}
		}
		if(dbCondition[4]!=null)
			dbCondition[4]+=')';
		//市区
		dbCondition[5] = null;
		for(i=0;(cityBox!=null)&&i<cityBox.size();i++){
			if(cityBox.get(i).isSelected()){
				if(dbCondition[5]==null){
					dbCondition[5] = "(s_city='"+cityBox.get(i).getText()+"'";
					}else{
						dbCondition[5] += " or s_city='"+cityBox.get(i).getText()+"'";
					}
			}
		}
		if(dbCondition[5]!=null)
			dbCondition[5]+=')';
		//组织检索条件
		for(i=0;i<dbCondition.length;i++){
			if(dbCondition[i]!=null){
				if(i==7||i==1||i==2){
					if(str2.equals("")){
						str2 = "where "+dbCondition[i];
					}else{
						str2 += " and "+dbCondition[i];
					}
				}else{
					if(str.equals("")){
						str = "where "+dbCondition[i];
					}else{
						str += " and "+dbCondition[i];
					}
				}
			}
		}
		//入学和毕业时间检索条件
		inArray = inList.getSelectVal();
		String inCondition = null;
		for(i=0;i<inArray.length;i++){
			if(inCondition==null){
				inCondition="(e.s_enter='"+inArray[i]+"'";
			}else{
				inCondition+=" or e.s_enter='"+inArray[i]+"'";
			}
		}
		if(inCondition!=null){
			inCondition+=')';
			if(str2.equals("")){
				str2 = "where "+inCondition;
			}else{
				str2+="and "+inCondition;
			}
		}
		outArray = outList.getSelectVal();
		String outCondition = null;
		for(i=0;i<outArray.length;i++){
			if(outCondition==null){
				outCondition="(e.s_graduate='"+outArray[i]+"'";
			}else{
				outCondition+=" or e.s_graduate='"+outArray[i]+"'";
			}
		}
		if(outCondition!=null){
			outCondition+=')';
			if(str2.equals("")){
				str2 = "where "+outCondition;
			}else{
				str2+="and "+outCondition;
			}
		}
		String[] strAry = {str,str2};//{学生表条件，教育记录条件}
		return strAry;
	}
	//{"其他","学院","专业","国家","省份","市区","职务"};
	public void stateChanged(ChangeEvent e) {
		selectIndex = jTabbed.getSelectedIndex();
		switch(selectIndex){
			case 1:	//学院
				if(facultyBox==null){
					if(PencilMain.nowUser.u_role==3)
						faculty = FacultyLog.allFaculty("");
					else{
						faculty = PencilMain.nowUser.faculty.split(",");
					}
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
				if(majorBox==null){
					GridLayout layout = new GridLayout(15,6);
					major = MajorLog.allMajor();
					int length = 0;
					for(int i=0;i<major.length;i++)
						length += major[i][1].split(",").length;
					int index = major.length;
					String[] majorName = new String[index];
					JPanel[] majorPanel = new JPanel[index];
					for(int i=0;i<index;i++)
						majorName[i] = major[i][0];
					JTabbedPane majorTabbed = new JTabbedPane(JTabbedPane.TOP);
					majorBox = new JCheckBox[length];
					int boxCount = 0;
					for(int i=0;i<index;i++){
						majorPanel[i] = new JPanel();
						majorPanel[i].setLayout(layout);
						String[] majorTemp = major[i][1].split(",");
						for(int j=0;j<majorTemp.length;j++){
							majorBox[boxCount] = new JCheckBox(majorTemp[j]);
							majorBox[boxCount].addItemListener(this);
							majorPanel[i].add(majorBox[boxCount]);
							boxCount++;
						}
						majorTabbed.add(majorPanel[i],majorName[i]);
					}
					panels[selectIndex].add(majorTabbed);
				}
				break;
			case 3:	//国家
				if(nationBox==null){
					nation = AddressLog.allNation();
					Object[] temp = Helper.arrayUnique(nation);
					int length = temp.length;
					nationBox = new JCheckBox[length];
					for(int i=0;i<length;i++){
						nationBox[i] = new JCheckBox((String)temp[i]);
						nationBox[i].addItemListener(this);
						panels[selectIndex].add(nationBox[i]);
					}
				}
				break;
			case 4:	//省份
				if(provinceBox==null){
					provinceBox = null;
					String str = "";
					province = AddressLog.allProvince(str);
					Object[] temp = Helper.arrayUnique(province);
					int length = temp.length;
					provinceBox = new JCheckBox[length];
					for(int i=0;i<length;i++){
						provinceBox[i] = new JCheckBox((String)temp[i]);
						provinceBox[i].addItemListener(this);
						panels[selectIndex].add(provinceBox[i]);
					}
				}
				break;
			case 5:	//市区
				if(cityBox==null){
					cityBox = new ArrayList<JCheckBox>();
					cityBtn.addActionListener(this);
					cityBtn.setForeground(Color.WHITE);
					cityBtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));  
					cityMidPanel = new JPanel();
					cityField = new MyTextField("请在这里输入检索的市区数据！！", 15);
					cityTopPanel = new JPanel(new BorderLayout());
					city = AddressLog.allCity("");
					cityList = new ArrayList<Object>();
					for(int i=1;i<city.length;i++)
						cityList.add(city[i]);
					cityField.setMenu(cityList);
					cityTopPanel.add(cityField,BorderLayout.CENTER);
					cityTopPanel.add(cityBtn,BorderLayout.EAST);
					panels[selectIndex].add(cityTopPanel,BorderLayout.NORTH);
					panels[selectIndex].add(cityMidPanel,BorderLayout.CENTER);
				}
				break;
		}
		panels[selectIndex].repaint();
	}

	public void actionPerformed(ActionEvent e) {
		JButton btn = (JButton)e.getSource();
		String str[] = getCondition();
		if(btn==selectAll){
			
		}else if(btn==searchAllBtn){
			if(str!=null)
				collect.updateTabel(str,null);
		}else if(btn==cityBtn) {
			String cityStr = cityField.getText().trim();
			boolean state = false;
			for(int i=0;i<cityBox.size();i++)
				if(cityBox.get(i).getText().equals(cityStr)) {
					state = true;
					break;
				}	
			if(state==false) {
				JCheckBox newBox = new JCheckBox(cityStr,true);
				cityBox.add(newBox);
				for(int i=0;i<cityBox.size();i++) 
					cityMidPanel.add(cityBox.get(i));
				this.repaint();
			}else {
				JOptionPane.showMessageDialog(this, "检索的数据已存在，请重新选择");
			}
		}
	}

	public void itemStateChanged(ItemEvent e) {}  
}
