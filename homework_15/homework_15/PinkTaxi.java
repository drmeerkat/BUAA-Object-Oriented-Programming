package homework_15;

import java.util.ListIterator;

public class PinkTaxi extends Taxi implements Iterable<String> {
	/*Overview
	 * 继承父类Taxi并扩展出新的返回双向迭代器查询历史运行记录的功能
	 * 抽象函数：该类出租车isnew为真
	 */
	public PinkTaxi(int id, Findpath gps) {
		/*@REQUIRES: (\all int id;0 < id <=100);
		 *           (\all Findpath gps; gps != null)
		 *@MODIFIES: this
		 *		     isnew
		 *@EFFECTS: initialize the new taxi
		 */
		super(id, gps);
		isnew = true;
	}

	public boolean repOK() {
		/*
		 * @REQUIRES:	None;
		 * @MODIFIES:	None;
		 * @EFFECTS:	Checks if "this" is legal.
		 */
		if (!isnew || !super.repOK())
			return false;
		return true;
	}

	@Override
	public ListIterator<String> iterator() {
		/*@REQUIRES: None
		 *@MODIFIES: None
		 *@EFFECTS: 返回该车历史纪录迭代器
		 */
		return this.getInfo().listIterator();
	}

}
