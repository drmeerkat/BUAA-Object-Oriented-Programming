package homework_1;
import java.util.regex.*;
import java.io.BufferedReader;  
import java.io.IOException;  
import java.io.InputStreamReader;
import java.util.Arrays;


public class match {
	public static void main(String[] args) {
		String inputpoly = "", poly_found = "";
		int len = 0, poly_count = 0, term_count = 0, i = 0, temp_coef, output_count = 0;
		String poly_sign = "+",term_sign1 = "+", term_sign2 = "+";
		term [] term_array = new term[1000];
		term [] output_array = new term[1000];
		
		
		for (i = 0;i < 1000;i++){
			term_array[i] = new term("+", "0", "0");
		}
		try {
			System.out.println("pleade input a line of poly:");
			BufferedReader line = new BufferedReader(new InputStreamReader(System.in));
			inputpoly = line.readLine();
			inputpoly = inputpoly.replace(" ", "");
			len = inputpoly.length();
			
//handle different kinds of errors
			if (len > 18100){
				System.out.println("your input is beyond the upper bound of poly's length");
			}
			
			String char_pattern = "[^\\d\\{\\}\\(\\)\\-\\+,]";
			Pattern find_char = Pattern.compile(char_pattern);
			Matcher match_char = find_char.matcher(inputpoly);
			if (match_char.find()){
				System.out.println("\n"+inputpoly);
				System.out.println("Your input has illegal characters like " + match_char.group(0) + " in position " + match_char.start() + ", please check again");
				System.exit(0);
			}
//			check the beginning
			if (inputpoly.charAt(0) != '{' && inputpoly.charAt(0) != '+' && inputpoly.charAt(0) != '-'){
				System.out.println("illegal input found at the begining");
				System.exit(0);
			}
			else if (inputpoly.charAt(0) == '{' && inputpoly.charAt(1) != '('){
				System.out.println("illegal input found at the begining");
				System.exit(0);
			}
			else if  ((inputpoly.charAt(0) == '+' || inputpoly.charAt(0) == '-')
					&&(inputpoly.charAt(1) != '{')){
				System.out.println("illegal input found at the begining");
				System.exit(0);
			}
//			check the end
			if (inputpoly.charAt(len-1) != '}' || inputpoly.charAt(len-2) != ')'){
				System.out.println("illegal input found at the end");
				System.exit(0);
			}
			System.out.println("\n"+inputpoly);
//			System.out.println("now the lenth of the input poly is " + len);
		} catch (Exception e) {
			// TODO: handle exception
//			e.printStackTrace();
			System.out.println("the input is not a legal poly");
			System.exit(0);
		}

//		match the poly and check if there are anymore errors
		try{
			String poly_pattern = "(\\+|\\-){0,1}\\{(?:,?\\([\\+\\-]{0,1}\\d{1,6},[\\+\\-]{0,1}\\d{1,6}\\)){1,50}\\}";
			Pattern find_poly = Pattern.compile(poly_pattern);
			Matcher match_poly = find_poly.matcher(inputpoly);
			
			String term_pattern = "\\((\\+|\\-){0,1}(\\d{1,6}),(\\+|\\-){0,1}(\\d{1,6})\\)";
			Pattern find_term = Pattern.compile(term_pattern);
		    if (!match_poly.find()){
		    	System.out.println("Match failed: your input is not legal");
		    	System.exit(0);
		    }
		    match_poly = find_poly.matcher(inputpoly);
			while (match_poly.find()){
				poly_found = match_poly.group(0);
				Matcher match_term = find_term.matcher(poly_found);
//		        System.out.println("start(): "+match_poly.start());
//		        System.out.println("end(): "+match_poly.end());
				System.out.println("Found poly: " + match_poly.group(0));
				poly_count += 1;
				if ((match_poly.group(1) == null && match_poly.group(0).charAt(1) == ',')
				   ||(match_poly.group(1) != null && match_poly.group(0).charAt(2) == ',')){
					System.out.println("no comma is allowed before the first term of a poly");
					System.exit(0);
				}
				if (poly_count == 51){
					System.out.println("Only inputs with less than 50 polynomials is allowed");
					System.exit(0);
				}
				if (match_poly.group(1) != null){
					poly_sign = match_poly.group(1);
				}
				System.out.println("Found poly's sign is " + poly_sign);
		
//				System.out.println("Found term's exponential is " + match_poly.group(2));
				while (match_term.find()){
					term_sign1 = (match_term.group(1) == null || match_term.group(1) == "+")? "":match_term.group(1);
					term_sign2 = (match_term.group(3) == null || match_term.group(3) == "+")? "":match_term.group(3);
					if (term_sign2.length() > 0 && term_sign2.charAt(0) == '-'){
						System.out.println("Error in term " + match_term.group(0) + ": Exponent should be a positive");
						System.exit(0);
					}				
					term_array[term_count] = new term(poly_sign, 
										 term_sign1+match_term.group(2), 
										 term_sign2+match_term.group(4));
//					System.out.println("the output term is " + term_array[term_count].getTermstring());
					term_count += 1;

					
				}
			}
			System.out.println("total count of term is " + term_count);
			Arrays.sort(term_array);
//			cal&generate the output
			for (i = 0;i < 1000;i++){
				temp_coef = term_array[i].getCoef();
				while (i != 999 && term_array[i].compareTo(term_array[i+1]) == 0){
					temp_coef += term_array[i+1].getCoef();
					i++;
				}
				if (temp_coef == 0){
					continue;
				}
				term_array[i].setCoef(temp_coef);
				output_array[output_count] = term_array[i];
				output_count += 1;
				
			}
			
			System.out.println("total output count of term is " + output_count);
			System.out.print("{");
			for (i = 0;i < output_count;i++){
				System.out.print(output_array[i].getTermstring());
				if (i != output_count - 1){
					System.out.print(",");
				}
			}
			System.out.print("}");
		}
		catch (Exception e) {
			// TODO: handle exception
			System.out.println("Some unknown error occured during calculate the polynomial");
			System.exit(0);
		}
	}
}
