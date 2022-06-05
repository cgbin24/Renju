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
 * PlayGameServer��
 */

public class PlayGameServer extends JFrame implements Runnable {
	public static ServerSocket serverSocket1=null;
	public static ServerSocket serverSocket2=null;
    public static Socket clientSocket=null;
    public static DataOutputStream out=null;
    public static DataInputStream in=null;
	public static final int WIDTH = 200;
	public static final int HEIGHT = 300;
	private JPanel panel; //���̵����
	private JMenuBar menuBar; //�˵���
	private JMenu sysMenu;	//ϵͳ��
	private  static JTextArea textArea;
	private JButton connectButton,exitButton; //���������˳�
	private MyListener myListener;
	private JLabel clock;	//���ʱ��
	public static void main(String[] args) {
        new PlayGameServer().init();
	}
	public PlayGameServer() {
		// TODO Auto-generated constructor stub
		System.out.println("��ӭ������ף����Ŀ��ģ�");
		thread=new Thread(this);
	}
	public void init() {
		myListener = new MyListener();	//ʵ����������
		clock = new JLabel();	//ʱ���
		textArea = new JTextArea(20,30);//�������ݵ�����
		this.setTitle("�������������������");
		panel = new JPanel();
		panel.add(textArea);
		menuBar = new JMenuBar();
		sysMenu = new JMenu("ϵͳ");
		exitButton = new JButton("�˳�");
		exitButton.addActionListener(myListener);//��ť��Ӽ�����,��ȡ�¼�Դ
		connectButton = new JButton("��������");
		connectButton.addActionListener(myListener);
		//���ô��ڵĲ˵���
		this.setJMenuBar(menuBar);
		menuBar.setLayout(new BorderLayout());
		menuBar.add(sysMenu,BorderLayout.WEST);
		menuBar.add(clock,BorderLayout.EAST);
		sysMenu.add(connectButton);
		sysMenu.add(exitButton);
		//��Ӳ��ַ�ʽΪBorderLayout
		this.setLayout(new BorderLayout());
		this.add(panel,BorderLayout.CENTER);
		//��ӹ�����
		JScrollPane jsp = new JScrollPane(textArea); //���ı�������ӹ�����
        jsp.setBounds(0, 15, 350, 100);
        //Ĭ�ϵ������ǳ����ı���Ż���ʾ�����������������ù�����һֱ��ʾ
//         jsp.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
         panel.add(jsp);
		//�ر�
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//����������
		this.setResizable(false);
		//���ÿ��
		this.setSize(WIDTH, HEIGHT);
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
			if(e.getSource()==exitButton) {
				//�����ʾ��Ч
				try {
					FileInputStream fileau=new FileInputStream("resource\\win.wav" );
					AudioStream as=new AudioStream(fileau);
					AudioPlayer.player.start(as);
				}catch (Exception e1){
					e1.printStackTrace();
				}
				int i=JOptionPane.showConfirmDialog(panel,"��ȷ��Ҫ�˳���Ϸ��");
				if(i==0) {
					System.out.println("��Ϸ������ף��������죡");
					System.exit(0);
				}
			}
			if(e.getSource()==connectButton) {
				connect();
			}
		}
	}
	private Thread thread;
	//���ӿͻ���
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
            System.out.println("���ӳɹ�");
            if (!(thread.isAlive())) { 
                thread = new Thread(this);
            }
            thread.start();
        }
        catch(Exception e)
        {
            System.out.println("����ʧ��");
            e.printStackTrace();
        }
    }
	@Override
	public void run() {
            try{
                while(true){
                    if(in!=null){
                        String tmp=in.readUTF();
                        textArea.append("\n�ͻ���:\n"+tmp);
                    }
                }
            }catch (Exception e) {
                e.printStackTrace();
            }  
	}
}