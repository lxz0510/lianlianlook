package ������;

import java.util.ArrayList;

public class Map {
	
	private int[][] map;
	private int count;
	private int n;
	
	
	public Map(int count,int n){//һ����count�ֲ�ͬ��ͼ��,n��n��		
		map = MapFactory.getMap(n);//��ȡn��n�е�����
		this.count = count;
		this.n = n;
	}
	
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getN() {
		return n;
	}

	public void setN(int n) {
		this.n = n;
	}

	public int[][] getMap(){
		
		ArrayList<Integer> list = new ArrayList<Integer>();//�Ƚ�����ͼƬID��ӵ�list��
		
		for(int i=0;i<n*n/10;i++){			
			for(int j=0;j<count;j++){
			list.add(j);
			}			
		}		
		
	for(int i=0;i<n;i++){
		for(int j=0;j<n;j++){
			int	index = (int) (Math.random()*list.size());//��list�����ȡһ��ͼƬID����������ӵ������У��ٴ�list��ɾ������
			map[i][j] = list.get(index);
			list.remove(index);	
		}
	}

	return map;//����һ��ͼƬ������ɵĵ�ͼ����

	}

	
	public int[][] getResetMap(){//��ȡ�ٴδ��Һ�ĵ�ͼ��Ϣ
		
		ArrayList<Integer> list = new ArrayList<Integer>();//list�����洢ԭ�ȵĵ�ͼ��Ϣ
		
		for(int i=0;i<n;i++){
			for(int j=0;j<n;j++){
				if(map[i][j]!=-1)//���(x,y)����ͼƬID��Ϊ-1����ô����ͼƬid��ӵ�list
					list.add(map[i][j]);		
				map[i][j]=-1;
			}
		}
		
		//��ԭ�ȵ�ͼ��ʣ���δ��ȥ��ͼƬ����
		while(!list.isEmpty()){
			
			int	index = (int) (Math.random()*list.size());//��list�����ȡһ��ͼƬID����������ӵ������У��ٴ�list��ɾ������
			boolean flag = false;
			
			while(!flag){
				int i = (int) (Math.random()*n);//��ȡ����ĵ�ͼ����
				int j = (int) (Math.random()*n);
				if(map[i][j]==-1){//�����λ����ͼƬ
					map[i][j] = list.get(index);
					list.remove(index);
					flag = true;
				}	
			}
			
		}
		
		return map;
		
	}
	
	
}
