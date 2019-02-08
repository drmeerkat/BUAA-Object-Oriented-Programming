package homework_2;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Elevator {
	private double time;
	private int current_fr;
	private int [] button;
	private static long MAX_VALUE = 4294967295L;
	
	public Elevator() {
		time = 0;
		current_fr = 1;
//		target_fr = 0;
		button = new int[] {0,0,0,0,0,0,0,0,0,0};
	}
	
	public boolean ele_request(String line){
		Pattern find_request = Pattern.compile("\\(ER,(\\d\\d?),(\\d{1,11})\\)");
		Matcher match_request = find_request.matcher(line);
		
		if (match_request.matches()) {
			if (Integer.parseInt(match_request.group(1)) > 10 || Integer.parseInt(match_request.group(1)) < 1 
				|| Long.parseLong(match_request.group(2)) > MAX_VALUE){
				return false;
			}
			return true;
		}
			
		else return false;
	}
	
	public double getTime(){
		return time;
	}
	
	public void setTime(double n_time){
		if (n_time > time) time = n_time;
		else System.out.println("Time is not updated");
	}
	
	public void addTime(double run_time){
		time += run_time;
	}
	
	
	public int getCurrentfr(){
		return current_fr;
	}
	
	public void pressButton(int k){
		button[k-1] = 1;
	}
	
	public int getButton(int k){
		return button[k-1];
	}
	
	public void unlock(int k){
		if (button[k-1] == 1)
			button[k-1] = 0;
		else{
			System.out.println("FATAL ERROR: the button isn't lightend");
			System.exit(0);
		}
	}
	
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
	
}

