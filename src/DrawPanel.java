
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

/**
 *五子棋：棋盘类 
 *DrawPanel类
 */

public class DrawPanel extends JPanel implements MouseListener,Runnable{
    public static ServerSocket serverSocket=null;
    public static Socket clientSocket=null;
    public  Socket client=null;
    public DataOutputStream out=null;
    public static DataInputStream in=null;
    private Thread thread;
	public static final int MARGIN = 30;//边距
	public static final int GRID_SPAN = 35;//网格间距
	public static final int ROWS = 14;//行
	public static final int COLS = 14;//列
	private static int x_index,y_index;	//存储鼠标点击时的索引值
	private boolean isBlack = true;	//存储棋子的颜色
	private Chess[] chessList = new Chess[(ROWS+1)*(COLS+1)];	//存放棋子的数组
	private int chessCount = 0;	//棋子的个数
	private  String getCoord;	//获取坐标值
	private boolean gameOver = false;	//游戏是否结束
	//连接服务器
	public void  connect(){
		InetAddress localIP=null;
		try {
			if(client==null){
				localIP = InetAddress.getLocalHost();
				client=new Socket(localIP,8001);
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
		catch(IOException e2){
			e2.printStackTrace();
		}
	}
		
	@Override
	public void run() {
	}
	public DrawPanel() {
		this.setBackground(new Color(145,125,62));	//系统默认背景色
		this.addMouseListener(this); //添加鼠标监听
		//添加背景音乐
		try {
			FileInputStream fileau=new FileInputStream("resource\\bgm.wav" );
			AudioStream as=new AudioStream(fileau);
				AudioPlayer.player.start(as);
		}catch (Exception e){
			e.printStackTrace();
		}
		thread = new Thread();
	}
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		//画棋盘-横线
		for(int i=0;i<=ROWS;i++) {
			g.drawLine(MARGIN, MARGIN+GRID_SPAN*i,
					MARGIN+GRID_SPAN*ROWS, MARGIN+i*GRID_SPAN);
		}
		//画棋盘-竖线
		for(int i=0;i<=COLS;i++) {
			g.drawLine(MARGIN+GRID_SPAN*i, MARGIN, 
					MARGIN+i*GRID_SPAN, MARGIN+GRID_SPAN*COLS);
		}
		//画棋子
		for(int i=0;i<chessCount;i++) {
			int xPos = chessList[i].getX()*GRID_SPAN+MARGIN;
			int yPos = chessList[i].getY()*GRID_SPAN+MARGIN;
			//赋颜色给数组中的棋子
			g.setColor(chessList[i].getColor());	
			//将棋子画在交叉点上
			g.fillOval(xPos-Chess.DIAMETER/2, 
					yPos-Chess.DIAMETER/2, 30, 30);
			
			//在最后一颗棋子上（即当前落下这颗）画上红框
			if(i==chessCount-1) {
				g.setColor(Color.red);
				g.drawRect(xPos-Chess.DIAMETER/2, yPos-Chess.DIAMETER/2, 
						Chess.DIAMETER, Chess.DIAMETER);
			}
		}	
		
	}
	//getPreferredSize() 设置当前组件大小为最佳 ，无需手动调用  配合pack()使用
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(MARGIN*2+GRID_SPAN*ROWS, MARGIN*2+GRID_SPAN*COLS);
	}
	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {
		// 鼠标按下时触发
		x_index = (e.getX()-MARGIN+GRID_SPAN/2)/GRID_SPAN;
		y_index = (e.getY()-MARGIN+GRID_SPAN/2)/GRID_SPAN;
//		System.out.println("("+x_index+","+y_index+")");
		getCoord = "当前"+(isBlack?"黑棋":"白棋")+"坐标为("+x_index+","+y_index+")";
		try{
			out.writeUTF(getCoord);
		}
		catch(Exception e1){
			e1.printStackTrace();
		}
		//首先判断是否是一个可用的棋子
		//1、游戏结束不能再落子
		if(gameOver) {
			return;
		}
		//2、棋子不能落在棋盘外面，超过边界值范围则不允许落子
		if(x_index<0||x_index>COLS||y_index<0||y_index>ROWS) {
			return;
		}
		//3、位置上有棋子，则不能再落子
		if(findChess(x_index, y_index)) {	
			//若该位置上存在棋子，则退出
			return;
		}
		//生成棋子
		Chess chess = new Chess(x_index, y_index, isBlack?Color.black:Color.white);
		//将生成的棋子添加到数组中
		chessList[chessCount++] = chess;
		System.out.println("棋子的个数："+chessCount);
		//添加落棋子音效
		try {
			FileInputStream fileau=new FileInputStream("resource\\chess.wav" );
			AudioStream as=new AudioStream(fileau);
			AudioPlayer.player.start(as);
		}catch (Exception e1){
			e1.printStackTrace();
		}
		this.repaint();	//重新绘制
		//判断赢棋
		if(isWin()) {
			//添加提示音效
			try {
				FileInputStream fileau=new FileInputStream("resource\\win.wav" );
				AudioStream as=new AudioStream(fileau);
				AudioPlayer.player.start(as);
			}catch (Exception e1){
				e1.printStackTrace();
			}
			String msg = String.format("游戏结束，%s获胜！", isBlack?"黑棋":"白棋");
			System.out.println(msg);
			JOptionPane.showMessageDialog(this, msg);	//弹出提示框
			gameOver = true;	//游戏结束
		}
		isBlack = !isBlack;
	}
	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	//判断位置上是否已经有棋子(通过获取棋子的索引值进行判断)
	private boolean findChess(int x,int y) {
		for(Chess c:chessList) {	//遍历存放棋子的数组
			if(c!=null&&c.getX()==x&&c.getY()==y) {
				return true;
			}
		}
		return false;
	}
	//得到棋盘上的棋子
	private Chess getChess(int x,int y,Color color) {
		//遍历装棋子的数组
		for(Chess c:chessList) {
			if(c!=null&&c.getX()==x&&c.getY()==y&&c.getColor()==color) {
				return c;
			}
		}
		return null;
	}
	/** 
	 * 判断输赢，需要考虑四个方向，及横向、纵向、左斜、右斜
	 */
	private boolean isWin() {
		return search1()||search2()||search3()||search4();
	}
	//斜向----西南-->东北
	private boolean search1() {
		int continueCount = 1;	//连续棋子的个数
		//斜向上寻找
		//是否超出棋盘范围
		for(int x=x_index+1,y=y_index-1;x<=COLS&&y>=0;x++,y--) {	
			Color c = isBlack?Color.black:Color.white; 	//获取棋子的颜色
			if(getChess(x, y, c)!=null) {//	若该棋子存在
				continueCount++;	//计数器累加
			}else {
				break;
			}
		}
		//斜向下寻找
		for(int x=x_index-1,y=y_index+1;x>=0&&y<=ROWS;x--,y++) {
			Color c = isBlack?Color.black:Color.white;
			if(getChess(x, y, c)!=null) {
				continueCount++;
			}else {
				break;
			}
		}
		//判断是否五子连珠
		if(continueCount >= 5) {
			return true;
		}else {
			continueCount = 1;	//重新开始新一轮的计数
		}
		return false;	//默认没有赢
	}
	//斜向----西北-->东南
	private boolean search2() {
		int continueCount = 1;	//连续棋子的个数
		//斜向上寻找
		for(int x=x_index-1,y=y_index-1;x>=0&&y>=0;x--,y--) {	//是否超出棋盘范围
			Color c = isBlack?Color.black:Color.white; 	//获取棋子的颜色
			if(getChess(x, y, c)!=null) {//	若该棋子存在
				continueCount++;	//计数器累加
			}else {
				break;
			}
		}
		//斜向下寻找
		for(int x=x_index+1,y=y_index+1;x<=COLS&&y<=ROWS;x++,y++) {
			Color c = isBlack?Color.black:Color.white;
			if(getChess(x, y, c)!=null) {
				continueCount++;
			}else {
				break;
			}
		}
		//判断是否五子连珠
		if(continueCount >= 5) {
			return true;
		}else {
			continueCount = 1;	//重新开始新一轮的计数
		}
		return false;	//默认没有赢
	}
	//水平方向，左右-西东
	private boolean search3() {
		int continueCount = 1;
		//向西寻找
		for(int x=x_index-1;x>=0;x--) {
			Color c = isBlack?Color.black:Color.white;
			if(getChess(x, y_index, c)!=null) {
				continueCount++;
			}else {
				break;
			}
		}
		//向东寻找
		for(int x=x_index+1;x<=COLS;x++) {
			Color c = isBlack?Color.black:Color.white;
			if(getChess(x, y_index, c)!=null) {
				continueCount++;
			}else {
				break;
			}
		}
		//五子连珠
		if(continueCount>=5) {
			return true;
		}else {
			continueCount = 1;
		}
		return false;
	}
	//垂直方向，上下-南北
	private boolean search4() {
		int continueCount = 1;
		//向北寻找
		for(int y=y_index-1;y>=0;y--) {
			Color c = isBlack?Color.black:Color.white;
			if(getChess(x_index, y, c)!=null) {
				continueCount++;
			}else {
				break;
			}
		}
		//向南寻找
		for(int y=y_index+1;y<=ROWS;y++) {
			Color c = isBlack?Color.black:Color.white;
			if(getChess(x_index, y, c)!=null) {
				continueCount++;
			}else {
				break;
			}
		}
		//五子连珠
		if(continueCount>=5) {
			return true;
		}else {
			continueCount = 1;
		}
		return false;
	}
	//重新开始游戏的方法
	public void restartGame() {
		//清除棋子
		for(int i=0;i<chessList.length;i++) {
			//将存放棋子的数组清空
			chessList[i] = null;	
		}
		//将游戏相关变量重置恢复
		isBlack = true;
		gameOver = false;
		chessCount = 0;
		//调用repaint()方法重画棋盘棋子
		this.repaint();
	}
	//悔棋的方法
	public void goBack() {
		//棋盘中没有棋子，则不能悔棋
		if(chessCount==0) {
			return;
		}
		//如果游戏已结束，则不能悔棋
		if(gameOver) {
			//添加提示音效
			try {
				FileInputStream fileau=new FileInputStream("resource\\win.wav" );
				AudioStream as=new AudioStream(fileau);
				AudioPlayer.player.start(as);
			}catch (Exception e1){
				e1.printStackTrace();
			}
			JOptionPane.showMessageDialog(this, 
					"游戏已结束，不能悔棋了哟！");
			return;
		}
		chessList[chessCount-1] = null;
		chessCount--;
		if(chessCount>0) {
			x_index = chessList[chessCount-1].getX();
			y_index = chessList[chessCount-1].getY();
		}
		isBlack = !isBlack;	//恢复上一颗棋子的颜色
		//重画棋子
		this.repaint();
	}
	//退出的方法
	public void exit() {
		System.exit(0);
	}
}