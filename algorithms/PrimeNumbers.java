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
            
             boolean isPrime[] = new boolean[max_num/2 + 1];
             Arrays.fill(isPrime,true);
            
             //все должно делиться на числа до корня
             for (int i=3; i < Math.sqrt(max_num) + 1; i = i + 2) {
                    if (isPrime[(i-1)/2]) {
                           //помечаем числа кратные простому
                           for (int j=i*i; j <= max_num; j+=i) {
                                  isPrime[(j-1)/2] = false;
                                  cnt_iterat++;
                        }
                    } else {
                           cnt_iterat++;
                    }
                         
             }
             //довыводим массив с корня до конца
             //1 3 5 7 13 19 25 31 37 43 49 55 61 67 73 79 85 91 97 101
             for (int i=0; i < isPrime.length; i++) {
                    //if(isPrime[i]) System.out.print(i*2+1 + " ");
                    if(isPrime[i]) System.out.print(i + " ");
                    cnt_iterat++;
             }
             System.out.println("(" +cnt_iterat+ ")");
       } //print_eratosfen2
 
       public static void main(String[] args) {
             print_n2(1000);
             print_sqrt(1000);
             print_eratosfen(1000);
             print_eratosfen2(100);
       }
 
}