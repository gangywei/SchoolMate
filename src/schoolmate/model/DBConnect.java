package schoolmate.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import javax.swing.JOptionPane;
import schoolmate.view.PencilMain;

public class DBConnect {
	private static Connection conn = null;
	private static Statement stmt = null;
    public static Connection getConnection() {
    	if(conn==null){
    		try {
    			Class.forName("org.sqlite.JDBC");
    			conn = DriverManager.getConnection(PencilMain.dbPath,"root","121961");
    			conn.setAutoCommit(false);
	        } catch ( Exception e ) {
	        	JOptionPane.showMessageDialog(null,"没有找到数据库文件");
	        	System.exit(0);
	        }
    	}
        return conn;
    }
}
