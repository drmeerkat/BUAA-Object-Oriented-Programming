package homework_5;

public class Task {
	private long finishtime;
	private String things;
	private int flr_change;
	
	public Task(long fin,String thi,int flrc){
		setFinishtime(fin);
		setThings(thi);
		setFlr_change(flrc);
	}

	public long getFinishtime() {
		return finishtime;
	}

	public void setFinishtime(long finishtime) {
		this.finishtime = finishtime;
	}

	public String getThings() {
		return things;
	}

	public void setThings(String things) {
		this.things = things;
	}

	public int getFlr_change() {
		return flr_change;
	}

	public void setFlr_change(int flr_change) {
		this.flr_change = flr_change;
	}
	
	
}
