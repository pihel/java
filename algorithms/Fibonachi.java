package algorithms;

public class Fibonachi {
	
	public static void callback_fib(int indx, int val) {
		System.out.print(val + " ");
	}
	
	public static void iterat_fib(int indx) {		
		int r = 0;
		int r_1 = 0;
		int r_2 = 0;
		
		if(indx >= 0) callback_fib(indx, 0);//0
		if(indx > 0) callback_fib(indx, 1);//1
		
		if(indx > 1) r_1 = 1;
		
		for(int i = 2; i < indx; i++) {
			r = r_1 + r_2;
			
			callback_fib(indx, r);
			
			r_2 = r_1;
			r_1 = r;
		}
	} //iterat_fib
	
	public static int recur_fib(int indx) {
		if(indx < 1) return 0;
		if(indx < 2) return 1;
		
		return recur_fib(indx - 1) + recur_fib(indx - 2);
	} //recur_fib
	
	public static void main(String[] args) {
		for(int i = 0; i < 20; i++) {
			System.out.print(recur_fib(i) + " ");
		}
		System.out.println(" ");
		iterat_fib(20);
	} //main
} //Fibonachi
