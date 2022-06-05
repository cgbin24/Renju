import java.awt.Color;

/**
 *五子棋：棋子类 
 *Chess类
 */

public class Chess {
	private int x = 0; 	//棋盘中x的索引值
	private int y = 0;
	private Color color; //棋子的颜色
	public static final int DIAMETER = 30;	//棋子的直径
	public Chess(int x, int y, Color color) {
		super();
		this.x = x;
		this.y = y;
		this.color = color;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}	
}