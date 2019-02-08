package homework_5;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Floor {
	private int [] up_lock;
	private int [] down_lock;
	private Request [] up_re;
	private Request [] down_re;
	private int id;
	
	
	public Floor(int i){
		up_lock = new int[] {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
		down_lock = new int[] {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
		up_re = new Request [20];
		down_re = new Request [20];
		setId(i);
	}
	
	public void setUplock(int k, Request button_request){
		up_lock[k-1] = 1;
		up_re[k-1] = button_request;
	}
	
	public void setDownlock(int k, Request button_request){
		down_lock[k-1] = 1;
		down_re[k-1] = button_request;
	}
	
	public int getUplock(int k){
		return up_lock[k-1];
	}
	
	public Request getUprequest(int k){
		return up_re[k-1];
	}
	
	public int getDownlock(int k){
		return down_lock[k-1];
	}
	
	public Request getDownrequest(int k){
		return down_re[k-1];
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
		if (up_lock[k-1] == 1){
			up_lock[k-1] = 0;
			up_re[k-1] = null;
		}
		else{
			System.out.println("FATAL ERROR1: the button isn't lightend");
			System.exit(0);
		}
	}
	
	public void unlockDown(int k){
		if (down_lock[k-1] == 1){
			down_lock[k-1] = 0;
			down_re[k-1] = null;
		}
		else{
			System.out.println("FATAL ERROR2: the button isn't lightend");
			System.exit(0);
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
}
