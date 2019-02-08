package homework_2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Request {
	private long request_time;
	private String request_dir;
	private int target_flr;
	private String request_cat;
	
	
	public Request(String cat, String line){
		request_cat = cat;
		String [] data = line.split("[\\(\\),]");
		if (cat.equals("FR")){
			request_time = Long.parseLong(data[4]);
			request_dir = data[3];
			target_flr = Integer.parseInt(data[2]);
		}
		else if(cat.equals("ER")) {
			request_time = Long.parseLong(data[3]);
			target_flr = Integer.parseInt(data[2]);
			request_dir = "";
		}
		
		else{
			System.out.println("Unknown reuest catgory");
			System.exit(0);
		}
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
	
	

}
