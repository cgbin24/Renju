import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.Timer;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

/**
 *五子棋：开始游戏 
 *
 */

public class PlayGameClient extends JFrame implements Runnable{
	public static ServerSocket serverSocket=null;
    public static Socket clientSocket=null;
    public  Socket client=null;
    public DataOutputStream out=null;
    public static DataInputStream in=null;
    private Thread thread;
    private  static JTextArea textArea;
	public static final int WIDTH = 300;
	public static final int HEIGHT = 400;
	private JPanel toolbar; //工具栏里面的三个按钮
	private DrawPanel drawPanel; //棋盘的面板
	private JButton startButton,backButton,exitButton,connectButton; //按钮开始、悔棋、退出
	private JMenuBar menuBar; //菜单栏
	private JMenu sysMenu;	//系统栏
	private JMenuItem startMenuItem,backMenuItem,exitMenuItem,bgcMenuItem; //开始、悔棋、退出、背景色
	private MyListener myListener;
	private JLabel clock;	//添加时间
	private Color color=new Color(145,125,62);		//棋盘背景颜色
	public static void main(String[] args) {
		 EventQueue.invokeLater(new Runnable() {
	            public void run() {
	                try {
	                	new PlayGameClient();
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	            }
	        });
	}
	public PlayGameClient() {
		// TODO Auto-generated constructor stub
		System.out.println("欢迎回来，祝您玩的开心！");
		myListener = new MyListener();	//实例化监听器
		clock = new JLabel();	//时间表
		this.setTitle("单机版五子棋客户端");
		toolbar = new JPanel();
		startButton = new JButton("开始");
		startButton.addActionListener(myListener); //向按钮添加监听器,获取事件源
		backButton = new JButton("悔棋");
		backButton.addActionListener(myListener);
		exitButton = new JButton("退出");
		exitButton.addActionListener(myListener);
		connectButton = new JButton("连接");
		connectButton.addActionListener(myListener);
		drawPanel = new DrawPanel();
		menuBar = new JMenuBar();
		sysMenu = new JMenu("系统");
		startMenuItem = new JMenuItem("开始");
		startMenuItem.addActionListener(myListener);
		backMenuItem = new JMenuItem("悔棋");
		backMenuItem.addActionListener(myListener);
		bgcMenuItem = new JMenuItem("背景色");
		bgcMenuItem.addActionListener(myListener);
		exitMenuItem = new JMenuItem("退出");
		exitMenuItem.addActionListener(myListener);
		//设置窗口的菜单栏
		this.setJMenuBar(menuBar);
		menuBar.setLayout(new BorderLayout());
		menuBar.add(sysMenu,BorderLayout.WEST);
		menuBar.add(clock,BorderLayout.EAST);
		sysMenu.add(startMenuItem);
		sysMenu.add(backMenuItem);
		sysMenu.add(bgcMenuItem);
		sysMenu.add(exitMenuItem);
		toolbar.add(startButton);
		toolbar.add(backButton);
		toolbar.add(exitButton);
		toolbar.add(connectButton);
		//添加布局方式为BorderLayout
		this.setLayout(new BorderLayout());
		this.add(toolbar,BorderLayout.NORTH);
		this.add(drawPanel,BorderLayout.CENTER);
		//关闭
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//不可拉伸宽高
		this.setResizable(false);
		//设置宽高
		//this.setSize(WIDTH, HEIGHT);
		this.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width-WIDTH)/2-100,
				(Toolkit.getDefaultToolkit().getScreenSize().height-HEIGHT)/2-150);
		pack();	//调整此窗口的大小，以适合其子组件的首选大小和布局。
		//添加时间
		Timer timer = new Timer(0, new ActionListener() {
	      @Override
	      public void actionPerformed(ActionEvent e) {
	    	  clock.setText(DateFormat.getDateTimeInstance().format(new Date()));
	      }
	    });
	    timer.setRepeats(true);
	    timer.setCoalesce(true);
	    timer.setInitialDelay(0);
	    timer.start();
	    //背景颜色
	    bgcMenuItem.addActionListener(new ActionListener() {	
			public void actionPerformed(ActionEvent e) {
				Color cc=JColorChooser.
						showDialog(drawPanel,"棋盘背景颜色", 
						new Color(226,189,0));
				if(cc!=null){
					color=cc;
					drawPanel.setBackground(color);
				}				
			}
		});
	    connectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                connect();
                drawPanel.connect();
            }
        });
		//显示视窗
		this.setVisible(true);
		thread = new Thread();
	}
	private class MyListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				FileInputStream fileau=new FileInputStream("resource\\chess.wav" );
				AudioStream as=new AudioStream(fileau);
				AudioPlayer.player.start(as);
			}catch (Exception e1){
				e1.printStackTrace();
			}
			if(e.getSource()==startButton||e.getSource()==startMenuItem) {
				startButton.addActionListener(new ActionListener() {
		            public void actionPerformed(ActionEvent e) {
		                 String tmp=startButton.getText();
		                 System.out.println(tmp);
		                 try
		                 {
		                     out.writeUTF(tmp);
		                 }
		                 catch(Exception e1)
		                 {
		                     e1.printStackTrace();
		                 }
		            }
		        });
				drawPanel.restartGame();
			}
			if(e.getSource()==backButton||e.getSource()==backMenuItem) {
				backButton.addActionListener(new ActionListener() {
		            public void actionPerformed(ActionEvent e) {
		                 String tmp=backButton.getText();
		                 System.out.println(tmp);
		                 try
		                 {
		                     out.writeUTF(tmp);
		                 }
		                 catch(Exception e1)
		                 {
		                     e1.printStackTrace();
		                 }
		            }
		        });
				drawPanel.goBack();
			}
			if(e.getSource()==exitButton||e.getSource()==exitMenuItem) {
				//添加提示音效
				try {
					FileInputStream fileau=new FileInputStream("resource\\win.wav" );
					AudioStream as=new AudioStream(fileau);
					AudioPlayer.player.start(as);
				}catch (Exception e1){
					e1.printStackTrace();
				}
				exitButton.addActionListener(new ActionListener() {
		            public void actionPerformed(ActionEvent e) {
		                 String tmp=exitButton.getText();
		                 System.out.println(tmp);
		                 try
		                 {
		                     out.writeUTF(tmp);
		                 }
		                 catch(Exception e1)
		                 {
		                     e1.printStackTrace();
		                 }
		            }
		        });
				int i=JOptionPane.showConfirmDialog(drawPanel,"您确定要退出游戏吗？");
				if(i==0) {
					System.out.println("游戏结束，祝您生活愉快！");
					drawPanel.exit();
				}
			}
			
		}
	}
	@Override
	public void run() {}
	//连接服务器
	public void  connect(){
		InetAddress localIP=null;
		try {
			if(client==null){
				localIP = InetAddress.getLocalHost();
				client=new Socket(localIP,8000);
				out=new DataOutputStream(client.getOutputStream());
                in=new DataInputStream(client.getInputStream());
				if(!(thread.isAlive())){
					thread=new Thread(this);
				}
				thread.start();
			}
		} catch (UnknownHostException   e2) {
			e2.printStackTrace();
		}
		catch(IOException e2)
		{
			e2.printStackTrace();
		}
	}
}