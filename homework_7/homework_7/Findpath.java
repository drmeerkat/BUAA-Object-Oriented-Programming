package homework_7;

import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;



public class Findpath {
	private PriorityQueue<myPoint> frontier;
	private Hashtable<String, myPoint> came_from;
	private Hashtable<String, Integer> cost_sofar;
	private int[][] map;
	private Neighbours [] neighbours;
	private final static int map_size = Readin.getMapSize();
	
	public Findpath(int[][] map){
		frontier = new PriorityQueue<>(
				new Comparator<myPoint>() {
					public int compare(myPoint a, myPoint b){
						return a.getPriority() - b.getPriority();
					}
				});
		came_from = new Hashtable<>();
		cost_sofar = new Hashtable<>();
		this.map = map;
		neighbours = new Neighbours[map_size*map_size];
		for (int i = 0;i<map_size*map_size;i++){
			neighbours[i] = new Neighbours();
		}
		initNeighbours();
		
	}
	
	
	private void initNeighbours(){
		int i,row,col;
		
		for (i = 0;i<map_size*map_size;i++){
			row = i/map_size;
			col = i%map_size;
//			System.out.println(row+" "+col);
			switch (map[row][col]) {
			case 0:
				break;
			case 1:
				neighbours[i].add(new myPoint(row, col+1));
				neighbours[i+1].add(new myPoint(row, col));
				break;
			case 2:
				neighbours[i].add(new myPoint(row+1, col));
				neighbours[i+map_size].add(new myPoint(row, col));
				break;
			case 3:
				neighbours[i].add(new myPoint(row, col+1));
				neighbours[i+1].add(new myPoint(row, col));
				neighbours[i].add(new myPoint(row+1, col));
				neighbours[i+map_size].add(new myPoint(row, col));
				break;
			default:
				break;
			}
		}
	}
	
	public LinkedList<myPoint> search(myPoint start,myPoint goal){
		LinkedList<myPoint> route = new LinkedList<>();
		myPoint current;
		myPoint temp;
		int priority;
		int cost_new;
		frontier.clear();
		came_from.clear();
		cost_sofar.clear();
		
		frontier.add(start);
		came_from.put(start.toString(), start);//起始点的camefrom是自己
		cost_sofar.put(start.toString(), 0);
		
		while (!frontier.isEmpty()){
			current = frontier.poll();
			
			if (current.equals(goal)){
				break;
			}
			
			
			Iterator<myPoint> it = neighbours[current.getID()].getIt();
			while (it.hasNext()){
				temp = it.next();
				cost_new = cost_sofar.get(current.toString()) + 1;//graph cost = 1
//				System.out.println("current: "+current.toString()+" "+temp.toString()+" "+cost_sofar.containsKey(temp.toString()));
				if (!cost_sofar.containsKey(temp.toString()) 
						|| cost_new < cost_sofar.get(temp.toString())){
					cost_sofar.put(temp.toString(), cost_new);
					priority = cost_new + heuristic(goal,temp);
					temp.setPriority(priority);
					frontier.add(temp);
					came_from.put(temp.toString(), current);
					
				}
			}
		}
//		如何从数组came from到路径点数组
		route.addFirst(goal);
		while (!came_from.get(goal.toString()).equals(start)){
			route.addFirst(came_from.get(goal.toString()));
			goal = came_from.get(goal.toString());
		}
		route.addFirst(start);
		return route;
	}
	
	private int heuristic(myPoint goal, myPoint now){
		return Math.abs(goal.getRow()-now.getRow())+Math.abs(goal.getCol()-now.getCol());
	}
	
	class Neighbours{
		private LinkedList<myPoint> neis;
		private Iterator<myPoint> iterator;
		private boolean isReturned;
		public Neighbours() {
			isReturned = false;
			neis = new LinkedList<>();
		}
		
		public boolean isReturned() {
			return isReturned;
		}
		
		public void add(myPoint temp){
			neis.add(temp);
		}
		
		public Iterator<myPoint> getIt(){
			isReturned = true;
			iterator = neis.iterator();
			return iterator;
		}
		
		public LinkedList<myPoint> getNei(){
			return neis;
		}
	}
	
	public LinkedList<myPoint> getmyPointNeighbours(myPoint point){
//		System.out.println(point.getRow()*map_size+point.getCol());
		return neighbours[point.getRow()*map_size+point.getCol()].getNei();
	}
}
