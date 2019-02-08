package homework_1;

public class term implements Comparable<term>{
	private int coef;
	private int expo;
	
	public term() {}
	public term(String poly_sign, String c, String n) {
		coef = Integer.parseInt(poly_sign+"1") * Integer.parseInt(c);
		expo = Integer.parseInt(n);
//		System.out.println("the new term is ("+ coef + "," + expo + ")");
	}
	
	public int getCoef(){
		return coef;
	}
	
	public int getExpo(){
		return expo;
	}
	
	public void setCoef(int new_coef){
		coef = new_coef;
	}
	
	@Override
	public int compareTo(term next){
		return (this.getExpo() > next.getExpo())? 1
			  :(this.getExpo() < next.getExpo())?-1
			  :0;
	}
	
	public String getTermstring(){
		return "("+ String.valueOf(coef) + "," + String.valueOf(expo) + ")";
	}
}
