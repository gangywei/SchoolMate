package schoolmate.view;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Panel;
import javax.swing.JFrame;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class SWTPanel extends Panel {
	//用来搭载shell的canvas
	private Canvas canvas = null;
	//控制SWT组件的子线程
	private DisplayThread displayThread = null;
	
	public SWTPanel()
	{
		setBackground(Color.black);
		displayThread = new DisplayThread();
		displayThread.start();
		canvas = new Canvas();
		setLayout(new BorderLayout());
		add(canvas, BorderLayout.CENTER);
	}
	
	//改变panel呈现的样式
	public void addNotify() 
	{
		super.addNotify();
		Display dis=displayThread.getDisplay();
		//syncExec的函数作用是让dis所在线程视自己情况，找机会执行后面代码
		dis.syncExec( new Runnable() {
			//此处添加SWT界面的代码
			public void run() {
		    Shell shell = SWT_AWT.new_Shell(displayThread.getDisplay(), canvas );
		    shell.setSize(800, 800);
		    
		    final Text text=new Text(shell,SWT.BORDER); 
	        text.setBounds(110,5,560,25); 
	        Button button=new Button(shell,SWT.BORDER); 
	        button.setBounds(680,5,100,25);        
	        button.setText("go"); 
	        Label label=new Label(shell,SWT.LEFT); 
	        label.setText("输入网址 :"); 
	        label.setBounds(5, 5, 100, 25); 
	        
	        final Browser browser=new Browser(shell,SWT.FILL); 
	        browser.setUrl("www.baidu.com");
	        text.setText("www.baidu.com");
	        browser.setBounds(5,30,800,760); 
	        
	        button.addListener(SWT.Selection, new Listener() 
	        { 
	            public void handleEvent(Event event) 
	            { 
	                String input=text.getText().trim(); 
	                if(input.length()==0)return; 
	                if(!input.startsWith("http://")) 
	                { 
	                    input="http://"+input; 
	                    text.setText(input); 
	                } 
	                browser.setUrl(input); 
	            } 
	        }); 
		   }
		  } );
	}
	
	class DisplayThread extends Thread{
		private Display display;
		Object lock = new Object();
		
		public void run(){
			//添加同步，DisplayThread和SWTPanel所在不同线程，访问同一个display对象，
			//SWTPanel需要等DisplayThread创建display对象后才能引用，所以此处要添加同步
			synchronized (lock) {
				display = Display.getDefault();
				lock.notifyAll();
			}
			swtEventLoop();
		}
		
		private void swtEventLoop(){
			while(true)
			{
				if(!display.readAndDispatch())
				{
					display.sleep();
				}
			}
		}
		
		public Display getDisplay() {
			synchronized (lock) {
				while (display == null) 
				{
					try {
						lock.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				return display;
			}
		}
	}
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.setLayout(new BorderLayout());
		frame.setSize(800, 800);
		
		SWTPanel swtPanel = new SWTPanel();
		frame.add(swtPanel,BorderLayout.CENTER);
	}
}