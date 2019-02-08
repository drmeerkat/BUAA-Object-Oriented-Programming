package homework_11;

import java.awt.Point;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*Overview
 * 记录出租车请求的各种信息
 * 输出该请求被处理的过程
 * 协助调度系统进行抢单调度。
 */

public class Request {
	private int id;
	private myPoint dst;
	private myPoint src;
	private int time_left;
	private long start_time;
	private boolean airing;
	private LinkedList<Integer> order_takenTaxis;
	private LinkedList<Integer> start_taxis;
	private LinkedList<String> info;
	private static final int range = 2; //左右偏移
	private int[] ranges;
	
	public Request(AtomicInteger request_count, String line, long start){
		/*@REQUIRES: (\all AtomicInteger request_count; 0<=request_count<=INTMAX;
		 * 			  \all String line; line != null;
		 * 		      \all long start; 0<= start <= MAXLONG
		 * )
		 *@MODIFIES:time_left
		 *			id
		 *			src
		 *			dst
		 *			start_time
		 *			airing
		 *			info
		 *			ranges
		 *			order_takenTaxis
		 *@EFFECTS: normal_behaviour:
		 *			初始化各种类内属性和变量
		 */
		time_left = 3000;
		this.id = request_count.get();
		toRequest(line);
		start_time = start;
		setAiring(false);
		order_takenTaxis = new LinkedList<>();//taxi id 1-100   0 represents no taxi
		start_taxis = new LinkedList<>();
		info = new LinkedList<>();
		ranges = new int[4];
		ranges[0] = (src.getRow()-range < 0)?0:src.getRow()-range;
		ranges[1] = (src.getRow()+range > Readin.getMapSize())?Readin.getMapSize():src.getRow()+range;
		ranges[2] = (src.getCol()-range < 0)?0:src.getCol()-range;
		ranges[3] = (src.getCol()+range > Readin.getMapSize())?Readin.getMapSize():src.getCol()+range;
	}
	
	public boolean repOK(){
		/*
		 * @REQUIRES:	None;
		 * @MODIFIES:	None;
		 * @EFFECTS:	Checks if "this" is legal.
		 */
		if (order_takenTaxis == null||start_taxis == null||info == null
				||ranges == null||dst == null||src == null)
			return false;
		return true;
	}
	
	public void addTaken(int id){
		/*@REQUIRES: (\all int id;0<= id <= INTMAX)
		 *@MODIFIES: order_takenTaxis
		 *@EFFECTS: normal_behaviour:
		 *			向已抢单出租车列表添加一个id
		 */
		order_takenTaxis.addLast(id);
	}
	
	public boolean isTakenEmpty(){
		/*@REQUIRES: None
		 *@MODIFIES: None
		 *@EFFECTS: normal_behaviour:
		 *			if (order_takenTaxis.isEmpty())
		 *				then return true
		 *			else
		 *				then return false
		 */
		return order_takenTaxis.isEmpty();
	}
	
	public boolean isStartEmpty(){
		/*@REQUIRES: None
		 *@MODIFIES: None
		 *@EFFECTS: normal_behaviour:
		 *			if (start_taxis.isEmpty())
		 *				then return true
		 *			else
		 *				then return false
		 */
		return start_taxis.isEmpty();
	}
	
	public int firstTake(){
		/*@REQUIRES: None
		 *@MODIFIES: order_takenTaxis
		 *@EFFECTS: normal_behaviour:
		 *			获得并返回order_takenTaxis列表的第一个元素
		 */
		return order_takenTaxis.getFirst();
	}
	
	public boolean isTaken(int id){
		/*@REQUIRES: (\all int id;0<=id<=INTMAX)
		 *@MODIFIES: None
		 *@EFFECTS: normal_behaviour:
		 *			if(已抢单列表中能够有输入的这个出租车编号)
		 *				then return true
		 *			else 
		 *				then return false
		 */
		return order_takenTaxis.contains(id);
	}
	
	public void addStart(int id) {
		/*@REQUIRES: (\all int id;0<=id<=INTMAX)
		 *@MODIFIES: None
		 *@EFFECTS: normal_behaviour:
		 *			向请求发出时刻就在范围里的出租车列表添加一个id
		 */
		start_taxis.add(id);
	}
	
	public boolean refresh(){
		/*@REQUIRES: None
		 *@MODIFIES: airing
		 *			 time_left
		 *@EFFECTS: normal_behaviour:
		 *			更新倒计时以判断请求抢单时间窗是否该关闭了
		 */
		tiktok();
		if (time_left == 0){
			setAiring(false);
			return true;
		}
		return false;
	}
	
	public void tiktok(){
		/*@REQUIRES: None
		 *@MODIFIES: None
		 *@EFFECTS: normal_behaviour:
		 *			更新倒计时
		 */
		if (airing && time_left>0){
			time_left -= 100;
		}
	}
	
	
	public boolean isInrage(myPoint point){
		/*@REQUIRES: (\all myPoint point;point is in map)
		 *@MODIFIES: None
		 *@EFFECTS: normal_behaviour:
		 *			判断给定的点是否在请求发出的4*4区域内
		 */
		if (point.getRow()<=ranges[1] && point.getRow()>=ranges[0] 
				&& point.getCol()>=ranges[2] && point.getCol()<=ranges[3]){
			return true;
		}
		else return false;
	}
	
	@Override
	public String toString(){
		/*
		 *@EFFECTS: normal_behaviour:
		 *			返回请求的字符串详细信息
		 */
		return "NO." + id +" Start time: " +start_time+" Src: " +getSrc()+ " Dst: "+getDst()+" Ranges: ("+ ranges[0]+","+ ranges[1]+","+ ranges[2]+","+ ranges[3]+")";
	}
	
	public String WTFtaken(){
		/*
		 *@EFFECTS: normal_behaviour:
		 *			返回最终抢单的完整出租车列表
		 */
		String out = "";
		Iterator<Integer> iterator = order_takenTaxis.iterator();
		while (iterator.hasNext()){
			out += iterator.next();
			out += " ";
		}
		return out;
	}
	
	public String WTFstart(){
		/*
		 *@EFFECTS: normal_behaviour:
		 *			返回请求发出时刻就抢单的出租车列表
		 */
		String out = "";
		Iterator<Integer> iterator = start_taxis.iterator();
		while (iterator.hasNext()){
			out += iterator.next();
			out += " ";
		}
		return out;
	}
	
	private void toRequest(String line){
		/*@REQUIRES: (\all String line;line != null)
		 *@MODIFIES: src
		 *			 dst
		 *@EFFECTS: normal_behaviour:
		 *			if (match_request.matches())
		 *				then 根据请求内容初始化this的起点和重点myPoint
		 *			else 
		 *				then 输出提示信息错误请求
		 */
		Pattern find_request = Pattern.compile("\\[CR,\\((\\d\\d?),(\\d\\d?)\\),\\((\\d\\d?),(\\d\\d?)\\)\\]");
		Matcher match_request = find_request.matcher(line);
		
		if (match_request.matches()){
			src = new myPoint(Integer.parseInt(match_request.group(1)), Integer.parseInt(match_request.group(2)));
			dst = new myPoint(Integer.parseInt(match_request.group(3)), Integer.parseInt(match_request.group(4)));
		}
		else{
			System.out.println("FAILED: " + line);
		}
	}
	

	public int getId() {
		/*
		 *@EFFECTS: normal_behaviour:
		 *			获得请求编号
		 */
		return id;
	}

	public void setId(int id) {
		/*
		 *@EFFECTS: normal_behaviour:
		 *			设置请求编号
		 */
		this.id = id;
	}

	public myPoint getDst() {
		/*
		 *@EFFECTS: normal_behaviour:
		 *		获得请求的目的地
		 */
		return dst;
	}


	public myPoint getSrc() {
		/*
		 *@EFFECTS: normal_behaviour:
		 *			获得请求的出发地
		 */
		return src;
	}
	
	public Point getGuiSrc() {
		/*
		 *@EFFECTS: normal_behaviour:
		 *			获得请求的出发地，返回格式为gui可以使用的point类型
		 */
		return new Point(src.getRow(), src.getCol());
	}
	
	public Point getGuiDst() {
		/*@REQUIRES:
		 *@MODIFIES:
		 *@EFFECTS: normal_behaviour:
		 *			获得请求的目的地，返回格式为gui可以使用的point类型
		 */
		return new Point(dst.getRow(), dst.getCol());
	}

	public int getTime_left() {
		/*
		 *@EFFECTS: normal_behaviour:
		 *			获得倒计时剩余时间
		 */
		return time_left;
	}

	public void setTime_left(int time_left) {
		/*
		 *@EFFECTS: normal_behaviour:
		 *			设置倒计时
		 */
		this.time_left = time_left;
	}

	public long getStart_time() {
		/*
		 *@EFFECTS: normal_behaviour:
		 *			获得请求发出的时间
		 */
		return start_time;
	}

	public void setStart_time(long clock) {
		/*
		 *@EFFECTS: normal_behaviour:
		 *			设置请求发出的时间
		 */
		this.start_time = clock;
	}



	public boolean isAiring() {
		/*
		 *@EFFECTS: normal_behaviour:
		 *			返回请求当前是否处于抢单时间状态
		 */
		return airing;
	}



	public void setAiring(boolean airing) {
		/*
		 *@EFFECTS: normal_behaviour:
		 *			设置请求为抢单时间状态
		 */
		this.airing = airing;
	}

	public LinkedList<String> getInfo() {
		/*
		 *@EFFECTS: normal_behaviour:
		 *			获得该请求执行过程中记录的所有log
		 */
		return info;
	}

	public void addInfo(String start_info) {
		/*@REQUIRES: (\all String start_info;start_info != null)
		 *@MODIFIES: info
		 *@EFFECTS: normal_behaviour:
		 *			向请求执行记录中添加一条记录
		 */
		this.info.add(start_info);
	}
	
	public boolean isSame(Request test_re){
		/*@REQUIRES: (\all Request test_re;test_re != null)
		 *@MODIFIES: None
		 *@EFFECTS: normal_behaviour:
		 *			判断输入的请求和this是不是同一个请求
		 */
		return basicinfo().equals(test_re.basicinfo());
	}
	
	public String basicinfo(){
		/*
		 *@EFFECTS: normal_behaviour:
		 *			输出this的详细基础信息
		 */
		return " Start time: " +start_time+" Src: " +getSrc()+ " Dst: "+getDst()+" Ranges: ("+ ranges[0]+","+ ranges[1]+","+ ranges[2]+","+ ranges[3]+")";
	}
}
