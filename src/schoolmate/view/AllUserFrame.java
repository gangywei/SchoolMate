package schoolmate.view;
import java.beans.PropertyVetoException;
import javax.swing.JInternalFrame;
import schoolmate.view.element.tablePanel.AllUserPanel;
public  class AllUserFrame extends JInternalFrame{
	protected PencilMain pencil;
	private AllUserPanel userPanel;
	public AllUserFrame(PencilMain pencil){
		super();
		this.pencil = pencil;
		initFrame();
	}
	
	public void initFrame(){
		setClosable(true);//提供关闭按钮
		setTitle("用户管理界面");
		userPanel = new AllUserPanel(pencil);
		add(userPanel);
		try {
			setSelected(true);
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		setBounds((PencilMain.showWidth-500)/2,10,500,540);
		setVisible(true);
	}
	
	public void doDefaultCloseAction() {  
	    dispose();
	}
}
