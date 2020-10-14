package 连连看;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements ActionListener ,MouseListener,KeyListener{

	private Image[] pics;//图片数组
	private int n;//行列数
	private int[][] map;//存储地图信息
	private int leftX = 140,leftY = 80;//row,column表示人物坐标；leftX,leftY记载左上角图片位置
	private boolean isClick = false;//标记是否第一次选中图片
	private int clickId,clickX,clickY;//记录首次选中图片的id,以及数组下标
	private int linkMethod;//连接方式
	private Node z1,z2;//存储拐角点的信息
	private Map mapUtil;//地图工具类
	public static int count = 0;//存储消去图案的个数
	
	
	public static final int LINKBYHORIZONTAL = 1,LINKBYVERTICAL = 2,LINKBYONECORNER = 3,LINKBYTWOCORNER = 4;
	public static final int BLANK_STATE = -1;
	
	public GamePanel(int count){
		setSize(600, 600);
		n = 10;
		mapUtil = new Map(count, n);
		map = mapUtil.getMap();//获取初始时，图片种类为count,行列数为n的地图信息
		this.setVisible(true); 
		this.addKeyListener(this);
		this.addMouseListener(this);
		this.setFocusable(true);
		getPics();
		repaint();
	}
	

	//初始化图片数组
	private void getPics() {
		pics = new Image[10];
		for(int i=0;i<=9;i++){
			pics[i] = Toolkit.getDefaultToolkit().getImage("D:/Game/LinkGame/pic"+(i+1)+".png");
		}		
	}

	
	
	public void paint(Graphics g){
		g.clearRect(0, 0, 800, 30);
		
		for(int i=0;i<n;i++){
			for(int j=0;j<n;j++){
				if(map[i][j]!=BLANK_STATE){
					g.drawImage(pics[map[i][j]],leftX+j*50,leftY+i*50,50,50,this);
				}else{
					g.clearRect(leftX+j*50,leftY+i*50,50,50);
				}
			}
		}
		
	}
	

	//判断是否可以水平相连
	private boolean horizontalLink(int clickX1, int clickY1, int clickX2, int clickY2) {
		
		if(clickY1>clickY2){//保证y1<y2
			int temp1 = clickX1;
			int temp2 = clickY1;
			clickX1 = clickX2;
			clickY1 = clickY2;
			clickX2 = temp1;
			clickY2 = temp2;
		}
		
		if(clickX1==clickX2){//如果两个选中图片的所在行数相同，说明可能可以水平相联
			
			for(int i=clickY1+1;i<clickY2;i++){
				if(map[clickX1][i]!=BLANK_STATE){//如果两图片中间还有其他图片，说明不能直接水平相连
					return false;
				}
			}
			
			//System.out.println("我们俩水平相连，我的下标是（"+clickX1+","+clickY1+"),它的坐标是("+clickX2+","+clickY2+")");
			linkMethod = LINKBYHORIZONTAL;
			return true;
		}
		
		
		return false;
	}


	//判断是否可以垂直连接
	private boolean verticalLink(int clickX1, int clickY1, int clickX2, int clickY2) {
		
		if(clickX1>clickX2){//保证x1<x2
			int temp1 = clickX1;
			int temp2 = clickY1;
			clickX1 = clickX2;
			clickY1 = clickY2;
			clickX2 = temp1;
			clickY2 = temp2;
		}
		
		if(clickY1==clickY2){//如果两个选中图片的所在列数相同，说明可能可以垂直相联
			
			for(int i=clickX1+1;i<clickX2;i++){
				if(map[i][clickY1]!=BLANK_STATE){//如果两图片中间还有其他图片，说明不能直接垂直相连
					return false;
				}
			}

			linkMethod = LINKBYVERTICAL;
			//System.out.println("我们俩垂直相连，我的下标是（"+clickX1+","+clickY1+"),它的坐标是("+clickX2+","+clickY2+")");

			return true;
		}
		
		
		
		return false;
	}
	
	//判断是否可以通过一个拐点相连
	private boolean oneCornerLink(int clickX1, int clickY1, int clickX2, int clickY2) {
		
		if(clickY1>clickY2){//保证(x1,y1)是矩形的左上角或者左下角
			int temp1 = clickX1;
			int temp2 = clickY1;
			clickX1 = clickX2;
			clickY1 = clickY2;
			clickX2 = temp1;
			clickY2 = temp2;
		}
		
		if(clickX1<clickX2){//如果(x1,y1)位于矩形左上角
			
			//判断右上角是否为空并且可以直接与(x1,y1)和(x2,y2)相连接,(clickX1, clickY2)是右上角拐点下标
			if(map[clickX1][clickY2]==BLANK_STATE&&horizontalLink(clickX1, clickY1, clickX1, clickY2)&&verticalLink(clickX2,clickY2,clickX1,clickY2)){
				linkMethod = LINKBYONECORNER;
				z1 = new Node(clickX1,clickY2);
				return true;
			}
			
			//判断左下角是否为空并且可以直接与(x1,y1)和(x2,y2)相连接,(clickX2, clickY1)是左下角拐点下标
			if(map[clickX2][clickY1]==BLANK_STATE&&horizontalLink(clickX2, clickY2, clickX2, clickY1)&&verticalLink(clickX1,clickY1,clickX2, clickY1)){
				linkMethod = LINKBYONECORNER;
				z1 = new Node(clickX2,clickY1);
				return true;
			}
			
		}else{//如果(x1,y1)位于矩形左下角
			
			//判断左上角是否为空并且可以直接与(x1,y1)和(x2,y2)相连接,(clickX2, clickY1)是左上角拐点下标			
			if(map[clickX2][clickY1]==BLANK_STATE&&horizontalLink(clickX2, clickY2, clickX2, clickY1)&&verticalLink(clickX1,clickY1,clickX2, clickY1)){
				linkMethod = LINKBYONECORNER;
				z1 = new Node(clickX2,clickY1);
				return true;				
			}
			
			//判断右下角是否为空并且可以直接与(x1,y1)和(x2,y2)相连接,(clickX1, clickY2)是右下角拐点下标			
			if(map[clickX1][clickY2]==BLANK_STATE&&horizontalLink(clickX1, clickY1, clickX1, clickY2)&&verticalLink(clickX2,clickY2,clickX1, clickY2)){
				linkMethod = LINKBYONECORNER;
				z1 = new Node(clickX1,clickY2);
				return true;				
			}
				
		}
			
		return false;
	}
	
	
	
	//判断是否可以通过两个拐点相连
	private boolean twoCornerLink(int clickX1, int clickY1, int clickX2, int clickY2) {
		
		//向上查找
		for(int i=clickX1-1;i>=-1;i--){
			
			//两个拐点在选中图案的上侧，并且两个拐点在地图区域之外
			if(i==-1&&throughVerticalLink(clickX2, clickY2, true)){
				z1 = new Node(-1,clickY1);
				z2 = new Node(-1,clickY2);
				linkMethod = LINKBYTWOCORNER;
				return true;
			}
			
			if(i>=0&&map[i][clickY1]==BLANK_STATE){
				
				if(oneCornerLink(i, clickY1, clickX2, clickY2)){
					linkMethod = LINKBYTWOCORNER;
					z1 = new Node(i,clickY1);
					z2 = new Node(i,clickY2);
					return true;
				}
				
			
			}else{
				break;
			}
			
		}
		
		//向下查找
		for(int i=clickX1+1;i<=n;i++){
			
			//两个拐点在选中图案的下侧，并且两个拐点在地图区域之外
			if(i==n&&throughVerticalLink(clickX2, clickY2, false)){
				z1 = new Node(n,clickY1);
				z2 = new Node(n,clickY2);
				linkMethod = LINKBYTWOCORNER;
				return true;
			}
			
			if(i!=n&&map[i][clickY1]==BLANK_STATE){
				
				if(oneCornerLink(i, clickY1, clickX2, clickY2)){
					linkMethod = LINKBYTWOCORNER;
					z1 = new Node(i,clickY1);
					z2 = new Node(i,clickY2);
					return true;
				}
			
			}else{
				break;
			}
			
		}
		
		
		//向左查找
		for(int i=clickY1-1;i>=-1;i--){

			//两个拐点在选中图案的左侧，并且两个拐点在地图区域之外
			if(i==-1&&throughHorizontalLink(clickX2, clickY2, true)){
				linkMethod = LINKBYTWOCORNER;
				z1 = new Node(clickX1,-1);
				z2 = new Node(clickX2,-1);
				return true;
			} 
			
			
			if(i!=-1&&map[clickX1][i]==BLANK_STATE){
				
				if(oneCornerLink(clickX1, i, clickX2, clickY2)){
					linkMethod = LINKBYTWOCORNER;
					z1 = new Node(clickX1,i);
					z2 = new Node(clickX2,i);
					return true;
				}
			
			}else{
				break;
			}
			
		}
		
		//向右查找
		for(int i=clickY1+1;i<=n;i++){

			//两个拐点在选中图案的右侧，并且两个拐点在地图区域之外
			if(i==n&&throughHorizontalLink(clickX2, clickY2, false)){
				z1 = new Node(clickX1,n);
				z2 = new Node(clickX2,n);
				linkMethod = LINKBYTWOCORNER;
				return true;
			}
			
			if(i!=n&&map[clickX1][i]==BLANK_STATE){
				
				if(oneCornerLink(clickX1, i, clickX2, clickY2)){
					linkMethod = LINKBYTWOCORNER;
					z1 = new Node(clickX1,i);
					z2 = new Node(clickX2,i);
					return true;
				}
				
			}else{
				break;
			}			
			
		}
		
		
		return false;
	}


	//根据flag,判断(x1,y1)左右两侧中的一侧是否还有其他图片，如果没有，可以相连
	private boolean throughHorizontalLink(int clickX, int clickY,boolean flag){

		if(flag){//向左查找
			
			for(int i=clickY-1;i>=0;i--){
				if(map[clickX][i]!=BLANK_STATE){
					return false;
				}
			}			
			
		}else{//向右查找
			
			for(int i=clickY+1;i<n;i++){
				if(map[clickX][i]!=BLANK_STATE){
					return false;
				}
			}
			
		}
		
		return true;
	}

	
	//根据flag,判断(x1,y1)上下两侧中的一侧是否还有其他图片，如果没有，可以相连
	private boolean throughVerticalLink(int clickX,int clickY,boolean flag){
		
		if(flag){//向上查找
			
			for(int i=clickX-1;i>=0;i--){
				if(map[i][clickY]!=BLANK_STATE){
					return false;
				}
			}
			
		}else{//向下查找
			
			for(int i=clickX+1;i<n;i++){
				if(map[i][clickY]!=BLANK_STATE){
					return false;
				}
			}
			
		}
	
		
		return true;
	}

	//画选中框
	private void drawSelectedBlock(int x, int y, Graphics g) {
		Graphics2D g2 = (Graphics2D) g;//生成Graphics对象
		BasicStroke s = new BasicStroke(1);//宽度为1的画笔
		g2.setStroke(s);
		g2.setColor(Color.RED);
		g.drawRect(x+1, y+1, 48, 48);
	}

	//画线，此处的x1,y1,x2,y2二维数组下标
	@SuppressWarnings("static-access")
	private void drawLink(int x1, int y1, int x2, int y2) {

		Graphics g = this.getGraphics();
		Point p1 = new Point(y1*50+leftX+25,x1*50+leftY+25);
		Point p2 = new Point(y2*50+leftX+25,x2*50+leftY+25);
		if(linkMethod == LINKBYHORIZONTAL || linkMethod == LINKBYVERTICAL){
			g.drawLine(p1.x, p1.y,p2.x, p2.y);
			System.out.println("无拐点画线");
		}else if(linkMethod ==LINKBYONECORNER){
			Point point_z1 = new Point(z1.y*50+leftX+25,z1.x*50+leftY+25);//将拐点转换成像素坐标
			g.drawLine(p1.x, p1.y,point_z1.x, point_z1.y);
			g.drawLine(p2.x, p2.y,point_z1.x, point_z1.y);
			System.out.println("单拐点画线");			
		}else{
			Point point_z1 = new Point(z1.y*50+leftX+25,z1.x*50+leftY+25);
			Point point_z2 = new Point(z2.y*50+leftX+25,z2.x*50+leftY+25);
			
			if(p1.x!=point_z1.x&&p1.y!=point_z1.y){//保证(x1,y1)与拐点z1在同一列或者同一行
				Point temp;
				temp = point_z1;
				point_z1 = point_z2;
				point_z2 = temp;
			}

			g.drawLine(p1.x, p1.y, point_z1.x, point_z1.y);
			g.drawLine(p2.x, p2.y, point_z2.x, point_z2.y);
			g.drawLine(point_z1.x,point_z1.y, point_z2.x, point_z2.y);
			
			System.out.println("双拐点画线");			
		}
		
		count+=2;//消去的方块数目+2
		GameClient.textField.setText(count+"");
		try {
			Thread.currentThread().sleep(500);//延时500ms
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		repaint();
		map[x1][y1] = BLANK_STATE;
		map[x2][y2] = BLANK_STATE;
		isWin();//判断游戏是否结束
	}

	
	public void clearSelectBlock(int i,int j,Graphics g){
		g.clearRect(j*50+leftX, i*50+leftY, 50, 50);
		g.drawImage(pics[map[i][j]],leftX+j*50,leftY+i*50,50,50,this);
//		System.out.println("清空选定"+i+","+j);
	}

	
	//提示，如果有可以连接的方块就消去并且返回true
	private boolean find2Block() {
		
//		boolean isFound = false;
		
		if(isClick){//如果之前玩家选中了一个方块，清空该选中框
			clearSelectBlock(clickX, clickY, this.getGraphics());
		isClick = false;
		}
		
		for(int i=0;i<n;i++){
			for(int j=0;j<n;j++){
				
				if(map[i][j]==BLANK_STATE){
					continue;
				}				
				
				for(int p=i;p<n;p++){
					for(int q=0;q<n;q++){
						  if(map[p][q]!=map[i][j]||(p==i&&q==j)){//如果图案不相等
							  continue;
						  }						  
						  
						  if(verticalLink(p,q,i,j)||horizontalLink(p,q,i,j)
								  ||oneCornerLink(p,q,i,j)||twoCornerLink(p,q,i,j)){
							  drawSelectedBlock(j*50+leftX, i*50+leftY, this.getGraphics());
							  drawSelectedBlock(q*50+leftX, p*50+leftY, this.getGraphics());
							  drawLink(p, q, i, j);
							  repaint();
							  return true;
						  }
				
					}
				}				
			}
		}
		
		isWin();
		
		return false;
	}


	private void isWin() {
		
		if(count==n*n){
			String msg = "恭喜您通关成功，是否开始新局？";
			int type = JOptionPane.YES_NO_OPTION;
			String title = "过关";
			int choice = 0;
			choice = JOptionPane.showConfirmDialog(null, msg,title,type);
			if(choice==1){
				System.exit(0);
			}else if(choice == 0){
				startNewGame();
			}
		}
		
	}
	
	

	public void startNewGame() {
		// TODO Auto-generated method stub
		count = 0;
		mapUtil = new Map(10,n);		
		map = mapUtil.getMap();
		isClick = false;
		clickId = -1;
		clickX = -1;
		clickY = -1;
		linkMethod = -1;
		GameClient.textField.setText(count+"");
		repaint();
	}



	public class Node{
		int x;
		int y;
		
		public Node(int x,int y){
			this.x = x;
			this.y = y;
		}
		
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
		if(e.getKeyCode() == KeyEvent.VK_A){//打乱地图			
			map = mapUtil.getResetMap();
			repaint();
		}
		
		if(e.getKeyCode() == KeyEvent.VK_D){//智能提示
			if(!find2Block()){
				JOptionPane.showMessageDialog(this, "没有可以连通的方块了");
			}
			
			isWin();//判断是否游戏结束
			
		}
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
		Graphics g = this.getGraphics();
		
		int x = e.getX()-leftX;//点击位置x-偏移量x
		int y = e.getY()-leftY;//点击位置y-偏移量y
		int i = y/50;//对应数组行数,根据像素坐标转换成数组下标坐标
		int j = x/50;//对应数组列数
		if(x<0||y<0)//超出地图范围
			return ;
		
	
		
		if(isClick){//第二次点击
		
			if(map[i][j]!=BLANK_STATE){
				if(map[i][j]==clickId){//点击的是相同图片Id,但不是重复点击同一图片
					if(i==clickX&&j==clickY)
					return ;
					
					if(verticalLink(clickX,clickY,i,j)||horizontalLink(clickX,clickY,i,j)||oneCornerLink(clickX,clickY,i,j)||twoCornerLink(clickX,clickY,i,j)){//如果可以连通，画线连接，然后消去选中图片并重置第一次选中标识						
						drawSelectedBlock(j*50+leftX,i*50+leftY,g);
						drawLink(clickX,clickY,i,j);//画线连接
						isClick = false;	

					}else{
						clickId = map[i][j];//重新选中图片并画框
						clearSelectBlock(clickX,clickY,g);
						clickX = i;
						clickY = j;
						drawSelectedBlock(j*50+leftX,i*50+leftY,g);
					}
					
				}else{
					clickId = map[i][j];//重新选中图片并画框
					clearSelectBlock(clickX,clickY,g);
					clickX = i;
					clickY = j;
					drawSelectedBlock(j*50+leftX,i*50+leftY,g);
				}
				
			}
			
		}else{//第一次点击
			if(map[i][j]!=BLANK_STATE){
				//选中图片并画框
				clickId = map[i][j];
				isClick = true;
				clickX = i;
				clickY = j;
				drawSelectedBlock(j*50+leftX,i*50+leftY,g);
			}
		}
	}



	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}
	
	
	@Override 
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
	
	}
	

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

}
