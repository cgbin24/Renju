
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

/**
 *五子棋：视窗类 
 *FrameUtil类
 */

public class FrameUtil extends JFrame {
	public static final int WIDTH = 300;
	public static final int HEIGHT = 400;
	private JPanel toolbar; //工具栏里面的三个按钮
	private DrawPanel drawPanel; //棋盘的面板
	private JButton startButton,backButton,exitButton,connectButton; //按钮开始、悔棋、退出
	private JMenuBar menuBar; //菜单栏
	private JMenu sysMenu;	//系统栏
	private JMenuItem startMenuItem,backMenuItem,exitMenuItem,bgcMenuItem; //开始、悔棋、退出、背景色
	private MyListener myListener;
	private float hue,saturation,brightness;//颜色值: 色调，饱和度，亮度
	private JLabel clock;	//添加时间
	public FrameUtil() {
		System.out.println("欢迎回来，祝您玩的开心！");
	}
	public void init() {
		myListener = new MyListener();	//实例化监听器
		clock = new JLabel();	//时间表
		this.setTitle("单机版五子棋");
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
		sysMenu = new JMenu("设置");
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
		    	  clock.setText(DateFormat.
		    			  getDateTimeInstance().
		    			  format(new Date()));
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
			if(e.getSource()==startButton||e.getSource()==startMenuItem) {
				drawPanel.restartGame();
			}
			if(e.getSource()==backButton||e.getSource()==backMenuItem) {
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
				int i=JOptionPane.showConfirmDialog(drawPanel,"您确定要退出游戏吗？");
				if(i==0) {
					System.out.println("游戏结束，祝您生活愉快！");
					drawPanel.exit();
				}
			}
			if(e.getSource()==bgcMenuItem) {
				Random r = new Random();
				hue = (float) r.nextInt(255);
				saturation = (float) r.nextInt(255);
				brightness = (float) r.nextInt(255);
				drawPanel.setBackground(Color.getHSBColor(hue, saturation, brightness));
				System.out.println("Color.getHSBColor( "+hue+" , "+saturation+" , "+brightness+" )");
			}
		}
	}
}