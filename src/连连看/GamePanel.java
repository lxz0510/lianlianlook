package ������;

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

	private Image[] pics;//ͼƬ����
	private int n;//������
	private int[][] map;//�洢��ͼ��Ϣ
	private int leftX = 140,leftY = 80;//row,column��ʾ�������ꣻleftX,leftY�������Ͻ�ͼƬλ��
	private boolean isClick = false;//����Ƿ��һ��ѡ��ͼƬ
	private int clickId,clickX,clickY;//��¼�״�ѡ��ͼƬ��id,�Լ������±�
	private int linkMethod;//���ӷ�ʽ
	private Node z1,z2;//�洢�սǵ����Ϣ
	private Map mapUtil;//��ͼ������
	public static int count = 0;//�洢��ȥͼ���ĸ���
	
	
	public static final int LINKBYHORIZONTAL = 1,LINKBYVERTICAL = 2,LINKBYONECORNER = 3,LINKBYTWOCORNER = 4;
	public static final int BLANK_STATE = -1;
	
	public GamePanel(int count){
		setSize(600, 600);
		n = 10;
		mapUtil = new Map(count, n);
		map = mapUtil.getMap();//��ȡ��ʼʱ��ͼƬ����Ϊcount,������Ϊn�ĵ�ͼ��Ϣ
		this.setVisible(true); 
		this.addKeyListener(this);
		this.addMouseListener(this);
		this.setFocusable(true);
		getPics();
		repaint();
	}
	

	//��ʼ��ͼƬ����
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
	

	//�ж��Ƿ����ˮƽ����
	private boolean horizontalLink(int clickX1, int clickY1, int clickX2, int clickY2) {
		
		if(clickY1>clickY2){//��֤y1<y2
			int temp1 = clickX1;
			int temp2 = clickY1;
			clickX1 = clickX2;
			clickY1 = clickY2;
			clickX2 = temp1;
			clickY2 = temp2;
		}
		
		if(clickX1==clickX2){//�������ѡ��ͼƬ������������ͬ��˵�����ܿ���ˮƽ����
			
			for(int i=clickY1+1;i<clickY2;i++){
				if(map[clickX1][i]!=BLANK_STATE){//�����ͼƬ�м仹������ͼƬ��˵������ֱ��ˮƽ����
					return false;
				}
			}
			
			//System.out.println("������ˮƽ�������ҵ��±��ǣ�"+clickX1+","+clickY1+"),����������("+clickX2+","+clickY2+")");
			linkMethod = LINKBYHORIZONTAL;
			return true;
		}
		
		
		return false;
	}


	//�ж��Ƿ���Դ�ֱ����
	private boolean verticalLink(int clickX1, int clickY1, int clickX2, int clickY2) {
		
		if(clickX1>clickX2){//��֤x1<x2
			int temp1 = clickX1;
			int temp2 = clickY1;
			clickX1 = clickX2;
			clickY1 = clickY2;
			clickX2 = temp1;
			clickY2 = temp2;
		}
		
		if(clickY1==clickY2){//�������ѡ��ͼƬ������������ͬ��˵�����ܿ��Դ�ֱ����
			
			for(int i=clickX1+1;i<clickX2;i++){
				if(map[i][clickY1]!=BLANK_STATE){//�����ͼƬ�м仹������ͼƬ��˵������ֱ�Ӵ�ֱ����
					return false;
				}
			}

			linkMethod = LINKBYVERTICAL;
			//System.out.println("��������ֱ�������ҵ��±��ǣ�"+clickX1+","+clickY1+"),����������("+clickX2+","+clickY2+")");

			return true;
		}
		
		
		
		return false;
	}
	
	//�ж��Ƿ����ͨ��һ���յ�����
	private boolean oneCornerLink(int clickX1, int clickY1, int clickX2, int clickY2) {
		
		if(clickY1>clickY2){//��֤(x1,y1)�Ǿ��ε����Ͻǻ������½�
			int temp1 = clickX1;
			int temp2 = clickY1;
			clickX1 = clickX2;
			clickY1 = clickY2;
			clickX2 = temp1;
			clickY2 = temp2;
		}
		
		if(clickX1<clickX2){//���(x1,y1)λ�ھ������Ͻ�
			
			//�ж����Ͻ��Ƿ�Ϊ�ղ��ҿ���ֱ����(x1,y1)��(x2,y2)������,(clickX1, clickY2)�����Ͻǹյ��±�
			if(map[clickX1][clickY2]==BLANK_STATE&&horizontalLink(clickX1, clickY1, clickX1, clickY2)&&verticalLink(clickX2,clickY2,clickX1,clickY2)){
				linkMethod = LINKBYONECORNER;
				z1 = new Node(clickX1,clickY2);
				return true;
			}
			
			//�ж����½��Ƿ�Ϊ�ղ��ҿ���ֱ����(x1,y1)��(x2,y2)������,(clickX2, clickY1)�����½ǹյ��±�
			if(map[clickX2][clickY1]==BLANK_STATE&&horizontalLink(clickX2, clickY2, clickX2, clickY1)&&verticalLink(clickX1,clickY1,clickX2, clickY1)){
				linkMethod = LINKBYONECORNER;
				z1 = new Node(clickX2,clickY1);
				return true;
			}
			
		}else{//���(x1,y1)λ�ھ������½�
			
			//�ж����Ͻ��Ƿ�Ϊ�ղ��ҿ���ֱ����(x1,y1)��(x2,y2)������,(clickX2, clickY1)�����Ͻǹյ��±�			
			if(map[clickX2][clickY1]==BLANK_STATE&&horizontalLink(clickX2, clickY2, clickX2, clickY1)&&verticalLink(clickX1,clickY1,clickX2, clickY1)){
				linkMethod = LINKBYONECORNER;
				z1 = new Node(clickX2,clickY1);
				return true;				
			}
			
			//�ж����½��Ƿ�Ϊ�ղ��ҿ���ֱ����(x1,y1)��(x2,y2)������,(clickX1, clickY2)�����½ǹյ��±�			
			if(map[clickX1][clickY2]==BLANK_STATE&&horizontalLink(clickX1, clickY1, clickX1, clickY2)&&verticalLink(clickX2,clickY2,clickX1, clickY2)){
				linkMethod = LINKBYONECORNER;
				z1 = new Node(clickX1,clickY2);
				return true;				
			}
				
		}
			
		return false;
	}
	
	
	
	//�ж��Ƿ����ͨ�������յ�����
	private boolean twoCornerLink(int clickX1, int clickY1, int clickX2, int clickY2) {
		
		//���ϲ���
		for(int i=clickX1-1;i>=-1;i--){
			
			//�����յ���ѡ��ͼ�����ϲ࣬���������յ��ڵ�ͼ����֮��
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
		
		//���²���
		for(int i=clickX1+1;i<=n;i++){
			
			//�����յ���ѡ��ͼ�����²࣬���������յ��ڵ�ͼ����֮��
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
		
		
		//�������
		for(int i=clickY1-1;i>=-1;i--){

			//�����յ���ѡ��ͼ������࣬���������յ��ڵ�ͼ����֮��
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
		
		//���Ҳ���
		for(int i=clickY1+1;i<=n;i++){

			//�����յ���ѡ��ͼ�����Ҳ࣬���������յ��ڵ�ͼ����֮��
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


	//����flag,�ж�(x1,y1)���������е�һ���Ƿ�������ͼƬ�����û�У���������
	private boolean throughHorizontalLink(int clickX, int clickY,boolean flag){

		if(flag){//�������
			
			for(int i=clickY-1;i>=0;i--){
				if(map[clickX][i]!=BLANK_STATE){
					return false;
				}
			}			
			
		}else{//���Ҳ���
			
			for(int i=clickY+1;i<n;i++){
				if(map[clickX][i]!=BLANK_STATE){
					return false;
				}
			}
			
		}
		
		return true;
	}

	
	//����flag,�ж�(x1,y1)���������е�һ���Ƿ�������ͼƬ�����û�У���������
	private boolean throughVerticalLink(int clickX,int clickY,boolean flag){
		
		if(flag){//���ϲ���
			
			for(int i=clickX-1;i>=0;i--){
				if(map[i][clickY]!=BLANK_STATE){
					return false;
				}
			}
			
		}else{//���²���
			
			for(int i=clickX+1;i<n;i++){
				if(map[i][clickY]!=BLANK_STATE){
					return false;
				}
			}
			
		}
	
		
		return true;
	}

	//��ѡ�п�
	private void drawSelectedBlock(int x, int y, Graphics g) {
		Graphics2D g2 = (Graphics2D) g;//����Graphics����
		BasicStroke s = new BasicStroke(1);//���Ϊ1�Ļ���
		g2.setStroke(s);
		g2.setColor(Color.RED);
		g.drawRect(x+1, y+1, 48, 48);
	}

	//���ߣ��˴���x1,y1,x2,y2��ά�����±�
	@SuppressWarnings("static-access")
	private void drawLink(int x1, int y1, int x2, int y2) {

		Graphics g = this.getGraphics();
		Point p1 = new Point(y1*50+leftX+25,x1*50+leftY+25);
		Point p2 = new Point(y2*50+leftX+25,x2*50+leftY+25);
		if(linkMethod == LINKBYHORIZONTAL || linkMethod == LINKBYVERTICAL){
			g.drawLine(p1.x, p1.y,p2.x, p2.y);
			System.out.println("�޹յ㻭��");
		}else if(linkMethod ==LINKBYONECORNER){
			Point point_z1 = new Point(z1.y*50+leftX+25,z1.x*50+leftY+25);//���յ�ת������������
			g.drawLine(p1.x, p1.y,point_z1.x, point_z1.y);
			g.drawLine(p2.x, p2.y,point_z1.x, point_z1.y);
			System.out.println("���յ㻭��");			
		}else{
			Point point_z1 = new Point(z1.y*50+leftX+25,z1.x*50+leftY+25);
			Point point_z2 = new Point(z2.y*50+leftX+25,z2.x*50+leftY+25);
			
			if(p1.x!=point_z1.x&&p1.y!=point_z1.y){//��֤(x1,y1)��յ�z1��ͬһ�л���ͬһ��
				Point temp;
				temp = point_z1;
				point_z1 = point_z2;
				point_z2 = temp;
			}

			g.drawLine(p1.x, p1.y, point_z1.x, point_z1.y);
			g.drawLine(p2.x, p2.y, point_z2.x, point_z2.y);
			g.drawLine(point_z1.x,point_z1.y, point_z2.x, point_z2.y);
			
			System.out.println("˫�յ㻭��");			
		}
		
		count+=2;//��ȥ�ķ�����Ŀ+2
		GameClient.textField.setText(count+"");
		try {
			Thread.currentThread().sleep(500);//��ʱ500ms
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		repaint();
		map[x1][y1] = BLANK_STATE;
		map[x2][y2] = BLANK_STATE;
		isWin();//�ж���Ϸ�Ƿ����
	}

	
	public void clearSelectBlock(int i,int j,Graphics g){
		g.clearRect(j*50+leftX, i*50+leftY, 50, 50);
		g.drawImage(pics[map[i][j]],leftX+j*50,leftY+i*50,50,50,this);
//		System.out.println("���ѡ��"+i+","+j);
	}

	
	//��ʾ������п������ӵķ������ȥ���ҷ���true
	private boolean find2Block() {
		
//		boolean isFound = false;
		
		if(isClick){//���֮ǰ���ѡ����һ�����飬��ո�ѡ�п�
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
						  if(map[p][q]!=map[i][j]||(p==i&&q==j)){//���ͼ�������
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
			String msg = "��ϲ��ͨ�سɹ����Ƿ�ʼ�¾֣�";
			int type = JOptionPane.YES_NO_OPTION;
			String title = "����";
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
		
		if(e.getKeyCode() == KeyEvent.VK_A){//���ҵ�ͼ			
			map = mapUtil.getResetMap();
			repaint();
		}
		
		if(e.getKeyCode() == KeyEvent.VK_D){//������ʾ
			if(!find2Block()){
				JOptionPane.showMessageDialog(this, "û�п�����ͨ�ķ�����");
			}
			
			isWin();//�ж��Ƿ���Ϸ����
			
		}
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
		Graphics g = this.getGraphics();
		
		int x = e.getX()-leftX;//���λ��x-ƫ����x
		int y = e.getY()-leftY;//���λ��y-ƫ����y
		int i = y/50;//��Ӧ��������,������������ת���������±�����
		int j = x/50;//��Ӧ��������
		if(x<0||y<0)//������ͼ��Χ
			return ;
		
	
		
		if(isClick){//�ڶ��ε��
		
			if(map[i][j]!=BLANK_STATE){
				if(map[i][j]==clickId){//���������ͬͼƬId,�������ظ����ͬһͼƬ
					if(i==clickX&&j==clickY)
					return ;
					
					if(verticalLink(clickX,clickY,i,j)||horizontalLink(clickX,clickY,i,j)||oneCornerLink(clickX,clickY,i,j)||twoCornerLink(clickX,clickY,i,j)){//���������ͨ���������ӣ�Ȼ����ȥѡ��ͼƬ�����õ�һ��ѡ�б�ʶ						
						drawSelectedBlock(j*50+leftX,i*50+leftY,g);
						drawLink(clickX,clickY,i,j);//��������
						isClick = false;	

					}else{
						clickId = map[i][j];//����ѡ��ͼƬ������
						clearSelectBlock(clickX,clickY,g);
						clickX = i;
						clickY = j;
						drawSelectedBlock(j*50+leftX,i*50+leftY,g);
					}
					
				}else{
					clickId = map[i][j];//����ѡ��ͼƬ������
					clearSelectBlock(clickX,clickY,g);
					clickX = i;
					clickY = j;
					drawSelectedBlock(j*50+leftX,i*50+leftY,g);
				}
				
			}
			
		}else{//��һ�ε��
			if(map[i][j]!=BLANK_STATE){
				//ѡ��ͼƬ������
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
