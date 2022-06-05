import java.awt.Color;

/**
 *�����壺������ 
 *Chess��
 */

public class Chess {
	private int x = 0; 	//������x������ֵ
	private int y = 0;
	private Color color; //���ӵ���ɫ
	public static final int DIAMETER = 30;	//���ӵ�ֱ��
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