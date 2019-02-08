package homework_10;

/*Overview
 * 地图点类型，记录了坐标和优先级，并提供查询接口和相关属性的修改方法
 */

public class myPoint {
	private int row;
	private int col;
	private int priority;
	
	public myPoint(int x, int y){
		/*@REQUIRES: (\all int x,y;0<= x,y <= 79)
		 *@MODIFIES: this.row
		 *			 this.col
		 *			 this.priority
		 *@EFFECTS: 初始化点的坐标和优先级
		 */
		this.row = x;
		this.col = y;
		setPriority(0);
	}
	
	public boolean repOK(){
		/*
		 * @REQUIRES:	None;
		 * @MODIFIES:	None;
		 * @EFFECTS:	Checks if "this" is legal
		 */
		if (!(row<80&&row>=0)||!(col<80&&col>=0))
			return false;
		return true;
	}
	
	public boolean equals(myPoint test){
		/*@REQUIRES: (\all myPoint test;test != null)
		 *@MODIFIES: None
		 *@EFFECTS: 检测this是否和test代表同一个点
		 */
		return row == test.getRow()&&col == test.getCol();
	}
	
	public int getRow() {
		/*@REQUIRES: None
		 *@MODIFIES: None
		 *@EFFECTS: 返回this的行数，0-79
		 */
		return row;
	}
	
	public int getCol() {
		/*@REQUIRES: None
		 *@MODIFIES: None
		 *@EFFECTS: 返回this的列数，0-79
		 */
		return col;
	}
	
	public int getID(){
		/*@REQUIRES: None
		 *@MODIFIES: None
		 *@EFFECTS: 返回this在所有地图点构成的长度为6400的数组中的索引值
		 */
		return row*Readin.getMapSize()+col;
	}
	
	@Override
	public String toString(){
		/*@REQUIRES: None
		 *@MODIFIES: None
		 *@EFFECTS: 返回this的字符串格式详细信息
		 */
		return "("+row+","+col+")";
	}
	

	public int getPriority() {
		/*@REQUIRES: None
		 *@MODIFIES: None
		 *@EFFECTS: 返回this的优先级
		 */
		return priority;
	}

	public void setPriority(int priority) {
		/*@REQUIRES: (\all int priority; 0<= priority <= MAXINT)
		 *@MODIFIES: this.priority
		 *@EFFECTS: 将this的优先级设置为输入的新数值
		 */
		this.priority = priority;
	}
}
