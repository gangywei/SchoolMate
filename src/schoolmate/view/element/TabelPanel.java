package schoolmate.view.element;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.beans.PropertyVetoException;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import schoolmate.control.BaseModel;
import schoolmate.view.CollectDataFrame;
import schoolmate.view.PencilMain;

public abstract class TabelPanel extends JPanel{
	
	protected MyTable table;
	public BaseModel model;
	protected JScrollPane tableScroll;
	protected int nowSelect = -1;
	protected JPopupMenu popupMenu  = new JPopupMenu();
	protected JMenuItem alterItem = new JMenuItem("修改");
	protected JMenuItem deleteItem = new JMenuItem("删除");	
	public Vector<Object[]> data = new Vector<Object[]>();
	protected CollectDataFrame collectFrame;
	protected PencilMain pencil;
	
	public TabelPanel(){
		init();
	}
	
	public TabelPanel(PencilMain pencil){
		this.pencil = pencil;
	}
	
	public TabelPanel(CollectDataFrame collectFrame){
		this.collectFrame = collectFrame;
		init();
	}
	
	public void init(){
		setLayout(new BorderLayout());
		initTable();
		popupMenu.add(alterItem);
		popupMenu.add(deleteItem);
		//table.setAutoCreateRowSorter(true);
		table.setFillsViewportHeight(true);
        tableScroll = new JScrollPane(table);
        tableScroll.setBackground(Color.WHITE);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(new RowListener());
        table.addMouseListener(new MouseAdapter() {
			public void mouseReleased(java.awt.event.MouseEvent e) {
				if (e.isPopupTrigger()){
					popupMenu.show((JComponent)e.getSource(), e.getX(),e.getY()); 
				}
			}
		});
		add(tableScroll);
		setVisible(true);
	}
	
	/*
	 * inter:更新当前表格的显示，并且刷新Collect界面。
	 * time:2018/03/15
	 */
	public void updateTotle(){
		updateTabel();
		collectFrame.refeshBtn.doClick();
	}
	
	public abstract void initTable();
	public abstract void updateTabel();
	//表格行监听
	public class RowListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent event) {
            if (event.getValueIsAdjusting()) {
                return;
            }
            if(nowSelect==event.getFirstIndex())
            	nowSelect = event.getLastIndex();
            else
            	nowSelect = event.getFirstIndex();
        }
    }
}
