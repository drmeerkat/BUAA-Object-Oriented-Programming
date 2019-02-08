package homework_7;

import java.awt.Point;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



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
	
	public Request(int id, String line, long start){
		time_left = 3000;
		this.id = id;
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
	
	public void addTaken(int id){
		order_takenTaxis.addLast(id);
	}
	
	public boolean isTakenEmpty(){
		return order_takenTaxis.isEmpty();
	}
	
	public boolean isStartEmpty(){
		return start_taxis.isEmpty();
	}
	
	public int firstTake(){
		return order_takenTaxis.getFirst();
	}
	
	public boolean isTaken(int id){
		return order_takenTaxis.contains(id);
	}
	
	public void addStart(int id) {
		start_taxis.add(id);
	}
	
	public boolean refresh(){
		tiktok();
		if (time_left == 0){
			setAiring(false);
			return true;
		}
		return false;
	}
	
	public void tiktok(){
		if (airing && time_left>0){
			time_left -= 100;
		}
	}
	
	
	public boolean isInrage(myPoint point){
		if (point.getRow()<=ranges[1] && point.getRow()>=ranges[0] 
				&& point.getCol()>=ranges[2] && point.getCol()<=ranges[3]){
			return true;
		}
		else return false;
	}
	
	@Override
	public String toString(){
		return "NO." + id +" Start time: " +start_time+" Src: " +getSrc()+ " Dst: "+getDst()+" Ranges: ("+ ranges[0]+","+ ranges[1]+","+ ranges[2]+","+ ranges[3]+")";
	}
	
	public String WTFtaken(){
		String out = "";
		Iterator<Integer> iterator = order_takenTaxis.iterator();
		while (iterator.hasNext()){
			out += iterator.next();
			out += " ";
		}
		return out;
	}
	
	public String WTFstart(){
		String out = "";
		Iterator<Integer> iterator = start_taxis.iterator();
		while (iterator.hasNext()){
			out += iterator.next();
			out += " ";
		}
		return out;
	}
	
	private void toRequest(String line){
		Pattern find_request = Pattern.compile("\\[CR,\\((\\d\\d*),(\\d\\d*)\\),\\((\\d\\d*),(\\d\\d*)\\)\\]");
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
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public myPoint getDst() {
		return dst;
	}


	public myPoint getSrc() {
		return src;
	}
	
	public Point getGuiSrc() {
		return new Point(src.getRow(), src.getCol());
	}
	
	public Point getGuiDst() {
		return new Point(dst.getRow(), dst.getCol());
	}

	public int getTime_left() {
		return time_left;
	}

	public void setTime_left(int time_left) {
		this.time_left = time_left;
	}

	public long getStart_time() {
		return start_time;
	}

	public void setStart_time(long clock) {
		this.start_time = clock;
	}



	public boolean isAiring() {
		return airing;
	}



	public void setAiring(boolean airing) {
		this.airing = airing;
	}

	public LinkedList<String> getInfo() {
		return info;
	}

	public void addInfo(String start_info) {
		this.info.add(start_info);
	}
	
	public boolean isSame(Request test_re){
		return basicinfo().equals(test_re.basicinfo());
	}
	
	public String basicinfo(){
		return " Start time: " +start_time+" Src: " +getSrc()+ " Dst: "+getDst()+" Ranges: ("+ ranges[0]+","+ ranges[1]+","+ ranges[2]+","+ ranges[3]+")";
	}
}
