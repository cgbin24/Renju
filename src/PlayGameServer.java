import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

/*
 * PlayGameServer类
 */

public class PlayGameServer extends JFrame implements Runnable {
	public static ServerSocket serverSocket1=null;
	public static ServerSocket serverSocket2=null;
    public static Socket clientSocket=null;
    public static DataOutputStream out=null;
    public static DataInputStream in=null;
	public static final int WIDTH = 200;
	public static final int HEIGHT = 300;
	private JPanel panel; //棋盘的面板
	private JMenuBar menuBar; //菜单栏
	private JMenu sysMenu;	//系统栏
	private  static JTextArea textArea;
	private JButton connectButton,exitButton; //开启服务、退出
	private MyListener myListener;
	private JLabel clock;	//添加时间
	public static void main(String[] args) {
        new PlayGameServer().init();
	}
	public PlayGameServer() {
		// TODO Auto-generated constructor stub
		System.out.println("欢迎回来，祝您玩的开心！");
		thread=new Thread(this);
	}
	public void init() {
		myListener = new MyListener();	//实例化监听器
		clock = new JLabel();	//时间表
		textArea = new JTextArea(20,30);//接收数据的区域
		this.setTitle("单机版五子棋服务器端");
		panel = new JPanel();
		panel.add(textArea);
		menuBar = new JMenuBar();
		sysMenu = new JMenu("系统");
		exitButton = new JButton("退出");
		exitButton.addActionListener(myListener);//向按钮添加监听器,获取事件源
		connectButton = new JButton("开启服务");
		connectButton.addActionListener(myListener);
		//设置窗口的菜单栏
		this.setJMenuBar(menuBar);
		menuBar.setLayout(new BorderLayout());
		menuBar.add(sysMenu,BorderLayout.WEST);
		menuBar.add(clock,BorderLayout.EAST);
		sysMenu.add(connectButton);
		sysMenu.add(exitButton);
		//添加布局方式为BorderLayout
		this.setLayout(new BorderLayout());
		this.add(panel,BorderLayout.CENTER);
		//添加滚动条
		JScrollPane jsp = new JScrollPane(textArea); //在文本框上添加滚动条
        jsp.setBounds(0, 15, 350, 100);
        //默认的设置是超过文本框才会显示滚动条，以下设置让滚动条一直显示
//         jsp.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
         panel.add(jsp);
		//关闭
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//不可拉伸宽高
		this.setResizable(false);
		//设置宽高
		this.setSize(WIDTH, HEIGHT);
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
		//显示视窗
		this.setVisible(true);
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
			if(e.getSource()==exitButton) {
				//添加提示音效
				try {
					FileInputStream fileau=new FileInputStream("resource\\win.wav" );
					AudioStream as=new AudioStream(fileau);
					AudioPlayer.player.start(as);
				}catch (Exception e1){
					e1.printStackTrace();
				}
				int i=JOptionPane.showConfirmDialog(panel,"您确定要退出游戏吗？");
				if(i==0) {
					System.out.println("游戏结束，祝您生活愉快！");
					System.exit(0);
				}
			}
			if(e.getSource()==connectButton) {
				connect();
			}
		}
	}
	private Thread thread;
	//连接客户端
    public void connect(){
        try{
            serverSocket1 =new ServerSocket(8000);
            serverSocket2 =new ServerSocket(8001);
            clientSocket=serverSocket1.accept();
            clientSocket=serverSocket2.accept();
            in=new DataInputStream(clientSocket.
            		getInputStream());
            out=new DataOutputStream(clientSocket.
            		getOutputStream());
            System.out.println("连接成功");
            if (!(thread.isAlive())) { 
                thread = new Thread(this);
            }
            thread.start();
        }
        catch(Exception e)
        {
            System.out.println("连接失败");
            e.printStackTrace();
        }
    }
	@Override
	public void run() {
            try{
                while(true){
                    if(in!=null){
                        String tmp=in.readUTF();
                        textArea.append("\n客户端:\n"+tmp);
                    }
                }
            }catch (Exception e) {
                e.printStackTrace();
            }  
	}
}