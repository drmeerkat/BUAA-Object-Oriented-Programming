package homework_2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Floor {
	private int [] up_lock;
	private int [] down_lock;
	private static long MAX_VALUE = 4294967295L;
	public Floor(){
		up_lock = new int[] {0,0,0,0,0,0,0,0,0,0};
		down_lock = new int[] {0,0,0,0,0,0,0,0,0,0};
	}
	
	public void setUplock(int k){
		up_lock[k-1] = 1;
	}
	
	public void setDownlock(int k){
		down_lock[k-1] = 1;
	}
	
	public int getUplock(int k){
		return up_lock[k-1];
	}
	
	public int getDownlock(int k){
		return down_lock[k-1];
	}
	
	public boolean getLock(int k, String dir){
		if (dir.equals("UP"))
			return getUplock(k) == 1;
		else if (dir.equals("DOWN"))
			return getDownlock(k) == 1;
		else {
			System.out.println("FATAL ERROR 4");
			System.exit(0);
			return false;
		}
	}
	
	public void unlockUp(int k){
		if (up_lock[k-1] == 1)
			up_lock[k-1] = 0;
		else{
			System.out.println("FATAL ERROR: the button isn't lightend");
			System.exit(0);
		}
	}
	
	public void unlockDown(int k){
		if (down_lock[k-1] == 1)
			down_lock[k-1] = 0;
		else{
			System.out.println("FATAL ERROR: the button isn't lightend");
			System.exit(0);
		}
	}
	
	public boolean flr_request(String line){
		int temp;
		Pattern find_request = Pattern.compile("\\(FR,(\\d\\d?),((?:UP)|(?:DOWN)),(\\d{1,11})\\)");
		Matcher match_request = find_request.matcher(line);
		if (match_request.matches()) {
			temp = Integer.parseInt(match_request.group(1));
//			System.out.println(match_request.group(3) + match_request.group(2) + match_request.group(1));
			if (temp > 10 || temp < 1 
			  || Long.parseLong(match_request.group(3)) > MAX_VALUE
			  ||(temp == 10 && match_request.group(2).equals("UP"))
			  ||(temp == 1 && match_request.group(2).equals("DOWN")) ){
					return false;
				}
			return true;
		}
		else return false;
	}
}
