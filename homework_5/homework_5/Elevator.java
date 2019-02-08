 package homework_5;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Elevator implements Elerun_interface{
	private double time;
	private int current_fr;
	private String state;
	private int [] button;
	private Request [] re_button;
	private int id;
	private int distance;
	
	
	public Elevator(int i) {
		setId(i);
		setDistance(0);
		time = 0;
		current_fr = 1;
		state = "";
		button = new int[] {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
		re_button = new Request[20];
	}
//	overload
	public String toString(){
		DecimalFormat decimalFormat = new DecimalFormat("0.0");
		return "(" + current_fr + "," + state + "," + String.valueOf(decimalFormat.format(time))+")";
	} 
	

	
	public void setState(String sta){
		state = sta;
	}
	
	public double getTime(){
		return time;
	}
	
	public void setTime(double n_time){
		time = n_time;
	}
	
	public void addTime(double run_time){
		time += run_time;
	}
	
	
	public int getCurrentfr(){
		return current_fr;
	}
	
	public void setCurrentfr(String dir){
		if (dir.equals("UP")){
			if (current_fr < 20) current_fr += 1;
			else{
				System.out.println("FATAL ERROR:cannot go higher");
				System.exit(0);
			}
		}
		
		else if (dir.equals("DOWN")){
			if (current_fr > 1) current_fr -= 1;
			else {
				System.out.println("FATAL ERROR:cannot go lower");
				System.exit(0);
			}
		}
		
		else if (dir.equals("STILL")){
		}
			
		else{
			System.out.println("FATAL ERROR:cannot refresh the fr");
			System.exit(0);
		}
	}
	
	public void pressButton(int k, Request button_request){
		button[k-1] = 1;
		re_button[k-1] = button_request;
	}
	
	public int getButton(int k){
		return button[k-1];
	}
	
	public Request getRequest(int k){
		return re_button[k-1];
	}
	
	
	public void unlock(int k){
		if (button[k-1] == 1){
			button[k-1] = 0;
			re_button[k-1] = null;
		}
			
		else{
			System.out.println("FATAL ERROR3: the button isn't lightend");
			System.exit(0);
		}
	}
	
//	previous homework use this
	public void run(double run_time, String dir, int target_flr){
		DecimalFormat decimalFormat = new DecimalFormat("0.0");
		double stop_time = time + run_time - 1;
		String state = dir;
		current_fr = target_flr;
		addTime(run_time);
		if (state.equals("STILL")){
			System.out.println("(" + target_flr + "," + state + "," + String.valueOf(decimalFormat.format(stop_time+1)) + ")");
		}
		else{
			System.out.println("(" + target_flr + "," + state + "," + String.valueOf(decimalFormat.format(stop_time)) + ")");
		}
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getDistance() {
		return distance;
	}
	public void setDistance(int distance) {
		this.distance = distance;
	}
	public void addDistace(){
		this.distance += 1;
	}
	
}

