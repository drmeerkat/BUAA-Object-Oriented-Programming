package homework_5;

public interface Elerun_interface {
	public String toString();
	public double getTime();
	public void setTime(double n_time);
	public int getCurrentfr();
	public void setCurrentfr(String dir);
	public void pressButton(int k, Request button_request);
	public int getButton(int k);
	public void unlock(int k);
}
