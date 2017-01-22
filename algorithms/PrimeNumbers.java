package algorithms;

import java.util.Arrays;
 
public class PrimeNumbers {
      
       public static void print_n2(int max_num) {
             int cnt_iterat = 0;       
             boolean divided = false;
            
             for(int i = 1; i<= max_num; i++) {
                    divided = false;
                    for(int j = 2; j < i; j++) {
                           cnt_iterat++;
                           if( i%j == 0 ) {
                                  divided = true;
                                  break;
                           } //if
                    } //for
                   
                    if(!divided) {
                           System.out.print(i + " ");
                    }                  
             } //for
             System.out.println("(" +cnt_iterat+ ")");
       } //print_n2
      
       public static void print_sqrt(int max_num) {
             int cnt_iterat = 0;
             boolean divided = false;
            
             for(int i = 1; i<= max_num; i++) {
 
                    if( i > 2 && i%2 == 0 ) {
                           cnt_iterat++;
                           continue; //пропустим четные
                    }
                   
                    divided = false;
                   
                    for(int j = 3; j < Math.sqrt(i) + 1; j = j + 2) {
                           cnt_iterat++;
                          
                           if( i%j == 0 ) {
                                  divided = true;
                                  break;
                           } //if
                    } //for
                   
                    if(!divided) {
                           System.out.print(i + " ");
                    }                  
             } //for
             System.out.println("(" +cnt_iterat+ ")");
       } //print_sqrt
      
       // Первое вычеркивание требует n/2 действий, второе — n/3, третье — n/5 и т. д.
       //O(N*log(log(N))),
       //память O(n)
       public static void print_eratosfen(int max_num) {
             int cnt_iterat = 0;
            
             boolean isPrime[] = new boolean[max_num + 1];
             Arrays.fill(isPrime,true);
            
             //все должно делиться на числа до корня
             for (int i=2; i < Math.sqrt(max_num) + 1; i++) {
                    if (isPrime[i]) {
                        //помечаем числа кратные простому
                        for (int j=i*i; j <= max_num; j+=i) {
		                    isPrime[j] = false;
		                    cnt_iterat++;
                        }
                    } else {
                           cnt_iterat++;
                    }
                         
             }
             System.out.print("1 2 ");
             //довыводим массив с корня до конца
             for (int i=3; i <= max_num; i = i + 2) {
                    if(isPrime[i]) System.out.print(i + " ");
                    cnt_iterat++;
             }
             System.out.println("(" +cnt_iterat+ ")");
       } //print_eratosfen
      
       //оптимизация без четных чисел. Память = O(n)/2; Сложность = O(N*log(log(N)))/2
       public static void print_eratosfen2(int max_num) {
             int cnt_iterat = 0;
            
             boolean isPrime[] = new boolean[max_num/2];
             Arrays.fill(isPrime,true);
            
             //все должно делиться на числа до корня (кроме четных)
             for (int i=3; i < Math.sqrt(max_num); i = i + 2) {
                    if (isPrime[(i-1)/2]) {
                       //помечаем числа кратные простому (кроме четных)
                    	//шагаем через раз, т.к. 2*нечетное = четное
                       for (int j=i*i; j <= max_num; j+=i*2) {
                            isPrime[(j-1)/2] = false;
                            cnt_iterat++;
                        }
                    } else {
                           cnt_iterat++;
                    }
                         
             }
             //довыводим массив с корня до конца
             System.out.print("1 2 ");
             for (int i=1; i < isPrime.length; i++) {
                    if(isPrime[i]) System.out.print(i*2+1 + " ");
                    cnt_iterat++;
             }
             System.out.println("(" +cnt_iterat+ ")");
       } //print_eratosfen2
       
       public static void fizzbuzz() {
    	   for(int i = 1; i<= 100; i++) {
    		   if(i%3 == 0 && i%5 == 0) System.out.print("FizzBuzz ");
    		   else if(i%3 == 0) System.out.print("Fizz ");
    		   else if(i%5 == 0) System.out.print("Buzz ");
    		   else System.out.print(i + " ");
    	   }
    	   System.out.println(" ");
       } //fizzbuzz
       
       public static void fizzbuzz2() {
    	   for(int i = 1; i<= 100; i++) {
    		   if(i%3 == 0) System.out.print("Fizz");
    		   if(i%5 == 0) System.out.print("Buzz");
    		   if(i%3 != 0 && i%5 != 0) System.out.print(i);
    		   System.out.print(" ");
    	   }
    	   System.out.println(" ");
       } //fizzbuzz2
 
       public static void main(String[] args) {
             print_n2			(100);
             print_sqrt			(100);
             print_eratosfen	(100);
             print_eratosfen2	(100);
             fizzbuzz();
             fizzbuzz2();
       }
 
}