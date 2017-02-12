package Hash;

import java.io.*;
import java.util.HashMap;

interface idistinct {
	public void set(String word);
	public int get();
}

class RealDist implements idistinct {
    HashMap<String, Integer> words;
   
    public RealDist() {
          words = new HashMap<String, Integer>();
    }
   
    public void set(String word) {
          if( !words.containsKey(word)) {
                 words.put(word, 1);
          } else {
                 words.put(word, words.get(word) + 1);
          }
    } //
   
    public int get() {
          return words.size();
    }
   
    public int getWordCount(String word) {
          return words.get(word);
    }
   
} //RealDist

//https://habrahabr.ru/post/119852/
class AproxDist implements idistinct {
    int hashes[];
   
    public AproxDist() {
          hashes = new int[256];
          for(int i = 0; i < hashes.length; i++) hashes[i] = 0;
    }
   
    public static int fnv1a(String text) {
        int hash = 1092394437;
        for (int i = 0; i < text.length(); ++i) {
            hash ^= text.charAt(i);
            hash += (hash << 1) + (hash << 4) + (hash << 7) + (hash << 8) + (hash << 24);
        }
        return hash >>> 0;
    }
   
    //позиция первого ненулевого бита справа
    public static int rank(int hash, int max_rank) {
        int r = 1;
        while ((hash & 1) == 0 && r <= max_rank) {
           r++;
           //смещаем вправо, пока не дойдет до hash & 1 == 1
           hash >>>= 1;
        }
        return r;
    }
   
    public static double log2(double x) {
          return Math.log(x) / Math.log(2);
    }
   
    public void set(String word) {
		int hash = fnv1a(word);
		int k = hash >>> 24; //убираем 24 бита из 32 справа - остается 8 левых (=256 разных значений)
		hashes[k] = Math.max(hashes[k], rank(hash, 24)); //если коллизия, то выберем наибольший ранк
    } //count
    
    public int get_log() {
        double max_rank = 0;
       
        for(int i = 0; i < hashes.length; i++) {
        	max_rank = Math.max(hashes[i], max_rank);
        }
        
        return (int) Math.pow(2, max_rank);
  }
    
    public double get_loglog() {
    	double count = 0;
       
        for(int i = 0; i < hashes.length; i++) {
      	  count += 1 / Math.pow(2, hashes[i]);
        }
       
        return 47072.7126712022335488 / count;
  }
   
    public int get() { //super_loglog
          long pow_2_32 = 4294967296L;
         
          double E = get_loglog();
          
          //коррекция
          if (E <= 640) {
	         int V = 0;
	         for (int i = 0; i < 256; i++) {
		         if (hashes[i] == 0) {
		                 V++;
		         }
	         }
	         if (V > 0) {
	        	 E = 256 * Math.log((256 / (double)V));
	         }
	         
	     } else if (E > 1/30 * pow_2_32) {
	          E = -pow_2_32 * Math.log(1 - E / pow_2_32);
	     }
          //конец коррекции
          
          return (int) Math.round(E);
    }

} //AproxDist

public class Distinct {
    public static void main(String[] args) throws IOException {
        //System.out.println(Long.toBinaryString( fnv1a("abyssinian") ));
       
        //наибольший ранг =~ числу уникальных значений
       
       
    	RealDist rd = new RealDist();
    	AproxDist ad = new AproxDist();
    	
    	BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\007\\workspace\\oracle_sort\\src\\Hash\\book.txt"));
    	try {
    	    String line = br.readLine();

    	    while (line != null) {
    	    	String[] split = line.split(" ");
    	    	for (String word : split) {
    	    		ad.set(word);
    	    		rd.set(word);
				}
    	    	
    	        line = br.readLine();
    	    }
    	} finally {
    	    br.close();
    	}
    	/*ad.set("aardvark");
    	ad.set("abyssinian");
    	ad.set("zoology");
    	ad.set("zoology");*/
    	
    	System.out.println("real distinct = " + rd.get());
        System.out.println("aprox distinct(log) = " + ad.get_log());
        System.out.println("aprox distinct(loglog) = " + Math.round( ad.get_loglog() ));
        System.out.println("aprox distinct(superlolog) = " + ad.get());
        
        /*
         * real distinct = 3737
			aprox distinct(log) = 16384
			aprox distinct(loglog) = 3965
			aprox distinct(superlolog) = 3965
         * */
  }
}
