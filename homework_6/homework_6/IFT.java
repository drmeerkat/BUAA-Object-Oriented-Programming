package homework_6;


public class IFT {
	private String trigger;
	private String route;
	private String task;
	private String parent_route;
	private int ID;
	
	public IFT(int trigger, String route, int task, String parent_route, int id){
		this.ID = id;
		this.trigger = String.valueOf(trigger);
		this.setRoute(route);
		this.task = String.valueOf(task);
		this.setParent_route(parent_route);
	}
	
	public String getTrigger() {
		return trigger;
	}

	@Override
	public String toString(){
		return this.ID +" "+ this.trigger + " " + this.route + " " + this.task;
	}

	public String getParent_route() {
		return parent_route;
	}

	public void setParent_route(String parent_route) {
		this.parent_route = parent_route;
	}

	public int getID() {
		return ID;
	}

	public String getRoute() {
		return route;
	}

	public void setRoute(String route) {
		this.route = route;
	}

	public String getTask() {
		return task;
	}
	
}
