package schoolmate.view.element;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

public class MyTable extends JTable{
	public MyTable(TableModel model) {
		super(model);
	}
	
	public MyTable() {
		super();
		DefaultTableCellRenderer render = new DefaultTableCellRenderer();     
	}
	
	public void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    protected Color colorForRow(int row) {
        return (row % 2 == 0) ? new Color(230,240,230) : Color.white;
    }

    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component c = super.prepareRenderer(renderer, row, column);
        if (isCellSelected(row, column) == false) {
            c.setBackground(colorForRow(row));
            c.setForeground(UIManager.getColor("Table.foreground"));
        } else {
            c.setBackground(UIManager.getColor("Table.selectionBackground"));
            c.setForeground(UIManager.getColor("Table.selectionForeground"));
        }
        return c;
    }
}
