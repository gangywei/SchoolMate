package schoolmate.view;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

import schoolmate.view.element.TabelPanel;
import schoolmate.view.element.TableLeftMouse;
import schoolmate.view.element.tablePanel.AllCityPanel;
import schoolmate.view.element.tablePanel.AllFacultyPanel;
import schoolmate.view.element.tablePanel.AllMajorPanel;
import schoolmate.view.element.tablePanel.AllNationPanel;
import schoolmate.view.element.tablePanel.AllProvincePanel;

public class LabelManageFrame  extends JInternalFrame implements ChangeListener{
	private JTabbedPane jTabbed = new JTabbedPane(JTabbedPane.TOP);
	private TabelPanel[] panels = new TabelPanel[5];
	int selectIndex = -1;
	private String[] labelName = {"国家","省份","市区","学院","专业"};
	private CollectDataFrame collectFrame;
	public LabelManageFrame(CollectDataFrame collectFrame){
		this.collectFrame = collectFrame;
		initFrame();
	}
	
	public void initFrame(){
		setResizable(true);  //允许自由调整大小 
		setClosable(true);
		setTitle("删除导入字段");
		panels[0] = new AllNationPanel(collectFrame);
		panels[1] = new AllProvincePanel(collectFrame);
		panels[2] = new AllCityPanel(collectFrame);
		panels[3] = new AllFacultyPanel(collectFrame);
		panels[4] = new AllMajorPanel(collectFrame);
		for(int i=0;i<labelName.length;i++){
			jTabbed.add(panels[i],labelName[i]);
		}
		add(jTabbed);
		setSize(400, 400);
		setVisible(true);
		setLocation((PencilMain.showWidth-400)/2, 0);
		jTabbed.addChangeListener(this);
	}

	public void stateChanged(ChangeEvent e) {
		selectIndex = jTabbed.getSelectedIndex();
		panels[selectIndex].updateTabel();
	}
}
