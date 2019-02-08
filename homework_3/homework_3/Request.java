package homework_3;


public class Request {
	private long request_time;
	private String request_dir;
	private int target_flr;
	private String request_cat;
	private int NO;
	private boolean piggyback;
	
	
	public Request(String cat, String line, int num){
		request_cat = cat;
		NO = num;
		piggyback = false;
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
		if (request_cat.equals("FR"))
			return "["+ request_cat + "," + target_flr + "," + request_dir + "," + request_time + "]";
		else
			return "["+ request_cat + "," + target_flr + "," + request_time + "]";
	}
	
	

}
