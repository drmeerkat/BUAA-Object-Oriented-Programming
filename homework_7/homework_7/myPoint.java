package homework_7;

public class myPoint {
	private int row;
	private int col;
	private int priority;
	
	public myPoint(int x, int y){
		this.row = x;
		this.col = y;
		setPriority(0);
	}
	
	public boolean equals(myPoint test){
		return row == test.getRow()&&col == test.getCol();
	}
	
	public int getRow() {
		return row;
	}
	
	public int getCol() {
		return col;
	}
	
	public int getID(){
		return row*Readin.getMapSize()+col;
	}
	
	@Override
	public String toString(){
		return "("+row+","+col+")";
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}
}
