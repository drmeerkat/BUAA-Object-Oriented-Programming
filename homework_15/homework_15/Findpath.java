package homework_15;

import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;
/*Overview
 * 实例化GPS对象的类。保存了地图并承担出租车的找路方法调用接口，记录了地图的邻接矩阵等信息，并提供查询方法。
 * 抽象函数：从两个坐标点映射到一条路径，从一个输入的地图映射到地图信息的邻接矩阵和每个点的邻接点HashMap
 */


public class Findpath {
	private PriorityQueue<myPoint> frontier;
	private Hashtable<String, myPoint> came_from;
	private Hashtable<String, Integer> cost_sofar;
	private Hashtable<String, Integer> flow_sofar;
	private int[][] map;
	private Neighbours [] neighbours;
	private final static int map_size = Readin.getMapSize();
	
	public Findpath(int[][] map){
		/*@REQUIRES: (\all int[][] map;map != null)
		 *@MODIFIES: frontier
		 *			 came_from
		 *			 cost_sofar
		 *			 flow_sofar
		 *			 neighbours
		 *			 this.map
		 *@EFFECTS: normal_behaviour:
		 *			如果输入的地图不是空的，
		 *				则初始化类的各种属性和相关变量
		 *				并使用输入的地图构建邻接表
		 */
		frontier = new PriorityQueue<>(
				new Comparator<myPoint>() {
					public int compare(myPoint a, myPoint b){
						return a.getPriority() - b.getPriority();
					}
				});
		came_from = new Hashtable<>();
		cost_sofar = new Hashtable<>();
		flow_sofar = new Hashtable<>();
		this.map = map;
		neighbours = new Neighbours[map_size*map_size];
		for (int i = 0;i<map_size*map_size;i++){
			neighbours[i] = new Neighbours();
		}
		initNeighbours();
		
	}
	
	
	private void initNeighbours(){
		/*@REQUIRES: None
		 *@MODIFIES: neighbours
		 *@EFFECTS: normal_behaviour:
		 *			if (true)
		 *			      then 根据map信息，为地图中的所有点构建邻接表
		 */
		int i,row,col;
		
		for (i = 0;i<map_size*map_size;i++){
			row = i/map_size;
			col = i%map_size;
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
//	is in map指的是两个点的坐标都符合0-79的取值范围
	public LinkedList<myPoint> search(myPoint start,myPoint goal){
		/*@REQUIRES: (\all myPoint start,goal;start,goal is in map)
		 *@MODIFIES: route
		 *			 frontier
		 *			 came_from
		 *			 cost_sofar
		 *			 flow_sofar
		 *			 (myPoint)temp.priority 
		 *@EFFECTS: normal_behaviour:
		 *			if (start有邻居)        
		 *		 		寻找到start到goal之间的一条满足最短路径中最少流量的道路
		 *				并将预计经过的点以字符串链表的形式输出
		 */
//		地图连通性由测试者保证
		LinkedList<myPoint> route = new LinkedList<>();
		myPoint current;
		myPoint temp;
		int priority;
		int cost_new;
		int flow_new;
		frontier.clear();
		came_from.clear();
		cost_sofar.clear();
		flow_sofar.clear();
		
		frontier.add(start);
		came_from.put(start.toString(), start);//起始点的camefrom是自己
		cost_sofar.put(start.toString(), 0);
		flow_sofar.put(start.toString(), 0);
		
		while (!frontier.isEmpty()){
			current = frontier.poll();
			
			if (current.equals(goal)){
				break;
			}
			
			
			Iterator<myPoint> it = neighbours[current.getID()].getIt();
			while (it.hasNext()){
				temp = it.next();
				cost_new = cost_sofar.get(current.toString()) + 1;
				flow_new = flow_sofar.get(current.toString()) + 
						TaxiSystem.flow.get(""+current.getRow()+","+current.getCol()+","+temp.getRow()+","+temp.getCol());
				
				if (!cost_sofar.containsKey(temp.toString()) 
						|| cost_new < cost_sofar.get(temp.toString())){
					cost_sofar.put(temp.toString(), cost_new);
					flow_sofar.put(temp.toString(), flow_new);
					priority = cost_new + heuristic(goal,temp);
					temp.setPriority(priority);
					frontier.add(temp);
					came_from.put(temp.toString(), current);
					
				}
				else if (cost_new == cost_sofar.get(temp.toString()) 
						&& flow_new < flow_sofar.get(temp.toString())){
					priority = cost_new + heuristic(goal,temp);
					temp.setPriority(priority);
					frontier.add(temp);
					came_from.put(temp.toString(), current);
				}
			}
		}
		
		route.addFirst(goal);
		while (!came_from.get(goal.toString()).equals(start)){
			route.addFirst(came_from.get(goal.toString()));
			goal = came_from.get(goal.toString());
		}
		route.addFirst(start);
		return route;
	}
	
	private int heuristic(myPoint goal, myPoint now){
		/*@REQUIRES: (\all myPoint goal,now; goal and now are both in map)
		 *@MODIFIES: None
		 *@EFFECTS: normal_behaviour:
		 *			
		 *				计算当前位置和预计目标之间的曼哈顿距离
		 */
		return Math.abs(goal.getRow()-now.getRow())+Math.abs(goal.getCol()-now.getCol());
	}
	
	class Neighbours{
		private LinkedList<myPoint> neis;
		private Iterator<myPoint> iterator;
		public Neighbours() {
			/*@REQUIRES: None
			 *@MODIFIES: isReturned
			 *			 neis
			 *@EFFECTS:  normal_behaviour:
			 *			if (true)
			 *			 	初始化各种类内属性和变量
			 */
			neis = new LinkedList<>();
		}
		
		
		public void add(myPoint temp){
			/*@REQUIRES: (\all myPoint temp;temp is in the map)
			 *@MODIFIES: neis
			 *@EFFECTS: normal_behaviour:
			 *			添加一个节点到this的邻居节点中
			 */
			neis.add(temp);
		}
		
		public Iterator<myPoint> getIt(){
			/*@REQUIRES: None
			 *@MODIFIES:None
			 *@EFFECTS: normal_behaviour:
			 *			返回该节点邻居列表构成的迭代器
			 */
			iterator = neis.iterator();
			return iterator;
		}
		
		public LinkedList<myPoint> getNei(){
			/*@REQUIRES: None
			 *@MODIFIES: None
			 *@EFFECTS: normal_behaviour:
			 *			返回该节点的邻居列表
			 */
			return neis;
		}
	}
	
	public LinkedList<myPoint> getmyPointNeighbours(myPoint point){
		/*@REQUIRES: (\all myPoint point;point is in the map)
		 *@MODIFIES: None
		 *@EFFECTS: normal_behaviour:
		 *          返回指定节点point的邻居列表
		 */
		return neighbours[point.getRow()*map_size+point.getCol()].getNei();
	}
	public boolean isNeighbours(myPoint a, myPoint b){
		/*@REQUIRES: (\all myPoint a,b;a,b is in the map)
		 *@MODIFIES: None
		 *@EFFECTS: normal_behaviour:
		 *			if(a和b两个节点是邻居)
		 *				then return true;
		 *			else
		 *				then return false;
		 */
		Iterator<myPoint> it = neighbours[a.getRow()*map_size+a.getCol()].getIt();
		while (it.hasNext()){
			myPoint temp = it.next();
			if (temp.equals(b)){
				return true;
			}
		}
		return false;
	}
}
