
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
 *�����壺�Ӵ��� 
 *FrameUtil��
 */

public class FrameUtil extends JFrame {
	public static final int WIDTH = 300;
	public static final int HEIGHT = 400;
	private JPanel toolbar; //�����������������ť
	private DrawPanel drawPanel; //���̵����
	private JButton startButton,backButton,exitButton,connectButton; //��ť��ʼ�����塢�˳�
	private JMenuBar menuBar; //�˵���
	private JMenu sysMenu;	//ϵͳ��
	private JMenuItem startMenuItem,backMenuItem,exitMenuItem,bgcMenuItem; //��ʼ�����塢�˳�������ɫ
	private MyListener myListener;
	private float hue,saturation,brightness;//��ɫֵ: ɫ�������Ͷȣ�����
	private JLabel clock;	//���ʱ��
	public FrameUtil() {
		System.out.println("��ӭ������ף����Ŀ��ģ�");
	}
	public void init() {
		myListener = new MyListener();	//ʵ����������
		clock = new JLabel();	//ʱ���
		this.setTitle("������������");
		toolbar = new JPanel();
		startButton = new JButton("��ʼ");
		startButton.addActionListener(myListener); //��ť��Ӽ�����,��ȡ�¼�Դ
		backButton = new JButton("����");
		backButton.addActionListener(myListener);
		exitButton = new JButton("�˳�");
		exitButton.addActionListener(myListener);
		connectButton = new JButton("����");
		connectButton.addActionListener(myListener);
		drawPanel = new DrawPanel();
		menuBar = new JMenuBar();
		sysMenu = new JMenu("����");
		startMenuItem = new JMenuItem("��ʼ");
		startMenuItem.addActionListener(myListener);
		backMenuItem = new JMenuItem("����");
		backMenuItem.addActionListener(myListener);
		bgcMenuItem = new JMenuItem("����ɫ");
		bgcMenuItem.addActionListener(myListener);
		exitMenuItem = new JMenuItem("�˳�");
		exitMenuItem.addActionListener(myListener);
		//���ô��ڵĲ˵���
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
		//��Ӳ��ַ�ʽΪBorderLayout
		this.setLayout(new BorderLayout());
		this.add(toolbar,BorderLayout.NORTH);
		this.add(drawPanel,BorderLayout.CENTER);
		//�ر�
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//����������
		this.setResizable(false);
		//���ÿ��
		//this.setSize(WIDTH, HEIGHT);
		this.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width-WIDTH)/2-100,
				(Toolkit.getDefaultToolkit().getScreenSize().height-HEIGHT)/2-150);
		pack();	//�����˴��ڵĴ�С�����ʺ������������ѡ��С�Ͳ��֡�
		//���ʱ��
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
		//��ʾ�Ӵ�
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
				//�����ʾ��Ч
				try {
					FileInputStream fileau=new FileInputStream("resource\\win.wav" );
					AudioStream as=new AudioStream(fileau);
					AudioPlayer.player.start(as);
				}catch (Exception e1){
					e1.printStackTrace();
				}
				int i=JOptionPane.showConfirmDialog(drawPanel,"��ȷ��Ҫ�˳���Ϸ��");
				if(i==0) {
					System.out.println("��Ϸ������ף��������죡");
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