package Hash;

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
   
    public int get() {
          double count = 0;
          int pow_2_32 = (0xFFFFFFFF + 1);
         
          for(int i = 0; i < hashes.length; i++) {
                 count = count + 1 / Math.pow(2, hashes[i]);
          }
         
          double E = 0.7182725932495458 * 256 * 256 / count;
         
          if (E <= 5/2 * 256) {
         int V = 0;
         for (int i = 0; i < 256; i++) {
         if (hashes[i] == 0) {
                 V++;
         }
         }
         if (V > 0) {
         E = 256 * Math.log(256 / V);
         }
     } else if (E > 1/30 * pow_2_32) {
          E = -pow_2_32 * Math.log(1 - E / pow_2_32);
     }
         
          return (int) Math.round(count);
    }

} //AproxDist

public class Distinct {
    public static void main(String[] args) {
        //System.out.println(Long.toBinaryString( fnv1a("abyssinian") ));
       
        //наибольший ранг =~ числу уникальных значений
       
        //System.out.println(m); //256
        //System.out.println(alpha_m); //0.7182725932495458
        //System.out.println(k_comp); //24
       
       
        //AproxDist ad = new AproxDist();
        RealDist ad = new RealDist();
        ad.set("aardvark");
        ad.set("abyssinian");
        ad.set("zoology");
        ad.set("abyssinian");
        System.out.println(ad.get());
        System.out.println(ad.getWordCount("abyssinian"));         
  }
}
