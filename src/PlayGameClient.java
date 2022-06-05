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
 *�����壺��ʼ��Ϸ 
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
	private JPanel toolbar; //�����������������ť
	private DrawPanel drawPanel; //���̵����
	private JButton startButton,backButton,exitButton,connectButton; //��ť��ʼ�����塢�˳�
	private JMenuBar menuBar; //�˵���
	private JMenu sysMenu;	//ϵͳ��
	private JMenuItem startMenuItem,backMenuItem,exitMenuItem,bgcMenuItem; //��ʼ�����塢�˳�������ɫ
	private MyListener myListener;
	private JLabel clock;	//���ʱ��
	private Color color=new Color(145,125,62);		//���̱�����ɫ
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
		System.out.println("��ӭ������ף����Ŀ��ģ�");
		myListener = new MyListener();	//ʵ����������
		clock = new JLabel();	//ʱ���
		this.setTitle("������������ͻ���");
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
		sysMenu = new JMenu("ϵͳ");
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
	    	  clock.setText(DateFormat.getDateTimeInstance().format(new Date()));
	      }
	    });
	    timer.setRepeats(true);
	    timer.setCoalesce(true);
	    timer.setInitialDelay(0);
	    timer.start();
	    //������ɫ
	    bgcMenuItem.addActionListener(new ActionListener() {	
			public void actionPerformed(ActionEvent e) {
				Color cc=JColorChooser.
						showDialog(drawPanel,"���̱�����ɫ", 
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
		//��ʾ�Ӵ�
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
				//�����ʾ��Ч
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
				int i=JOptionPane.showConfirmDialog(drawPanel,"��ȷ��Ҫ�˳���Ϸ��");
				if(i==0) {
					System.out.println("��Ϸ������ף��������죡");
					drawPanel.exit();
				}
			}
			
		}
	}
	@Override
	public void run() {}
	//���ӷ�����
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