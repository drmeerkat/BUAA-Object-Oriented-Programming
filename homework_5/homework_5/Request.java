package homework_5;

import java.text.DecimalFormat;

public class Request {
	private long request_time;
	private String request_dir;
	private int target_flr;
	private String request_cat;
	private int NO;
	private boolean piggyback;
	private int ele_id;
	
	public Request(String cat, String line, int num, long req_time){
		request_cat = cat;
		NO = num;
		piggyback = false;
		request_time = req_time;
		String [] data = line.split("[\\(\\),#]");
		if (cat.equals("FR")){
			request_dir = data[3];
			target_flr = Integer.parseInt(data[2]);
			ele_id = 0;
		}
		else if(cat.equals("ER")) {
			target_flr = Integer.parseInt(data[4]);
			request_dir = "";
			ele_id = Integer.parseInt(data[3]);
		}
		
		else{
			System.out.println("Unknown reuest catgory");
			System.exit(0);
		}
	}
	
	
	
	public boolean getPiggyback(){
		return piggyback;
	}
	
	public void setPiggyback(){
		piggyback = true;
	}
	
	public int getNO(){
		return NO;
	}
	
	public long getTime(){
		return request_time;
	}

	public int getFlr(){
		return target_flr;
	}
	
	public String getDR_dir(){
		return request_dir;
	}
	
	public String getCat(){
		return request_cat;
	}
//	Overload ToString
	public String toString(){
		DecimalFormat df = new DecimalFormat("#.0");
		if (request_cat.equals("FR"))
			return "(" + request_cat + "," + target_flr + "," + request_dir + "), " + df.format((double)request_time/1000);
		else
			return "(" + request_cat + "," + "#" + ele_id + "," + target_flr + "), " + df.format((double)request_time/1000);
	}

	public int getEle_id() {
		return ele_id;
	}


}
