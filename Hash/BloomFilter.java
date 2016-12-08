package Hash;

import java.lang.Math;
import java.util.BitSet;

public class BloomFilter {

  private BitSet data;
  private byte[] data1;
  
  private int hash_nums;
  public int hashMask;

  BloomFilter(int num_bits, int num_hashs) {
    this.hash_nums = num_hashs;
    
    this.data = new BitSet(1 << num_bits);
    this.data1 = new byte[(int)Math.floor( num_bits / 8 )];
    
    this.hashMask = (1 << num_bits) - 1;
  } //BloomFilter

  public int hashCode(String s, int hash_num) {
    int result = 1;
        for (int i = 0; i < s.length(); ++i) {
          //1 = (1 * 1 + 58)
          //1 = ( 0001 * 0001 + 11 0001 ) & 1111 1111 1111 1111 1111 1111 1111 1111
          result = (hash_num * result + s.charAt(i)) & this.hashMask;
        }
        
        return result;
  }
  
  public void add(String s) {
    for(int i = 1; i <= hash_nums; i++) {
      int index = hashCode(s, i);
      
      data.set(index);
      System.out.println(index/8);
      //data1[(int)Math.floor( index / 8 )] = (byte) (data1[index / 8] | (1 << index % 8 ));
      //bits[Math.floor(index / 32)] = bits[Math.floor(index / 32)] | ( 1 << (index % 32) );
    }
  } //add
  
  public boolean test(String s) {
    for(int i = 1; i <= hash_nums; i++) {
      int index = hashCode(s, i);
      
      if( !data.get(index) ) return false;
    }
    
    return true;
  } //test
  
  public void dump() {
	  System.out.println(data);
	  System.out.println("---");
	  System.out.println(data1);
  } //dump

  public static void main(String[] args) {
    BloomFilter bf = new BloomFilter(30, 3);
    bf.add("a0");
    
    /*String[] arr = new String[10];
    for(int i = 0; i < 3; i++) {
    	arr[i] = "a" + i;
    	bf.add(arr[i]);
    }
    
    for(int i = 0; i < arr.length; i++) {
    	if(arr[i] != null) {
	    	arr[i] = "a" + i;
	    	System.out.println(arr[i] + "=" + bf.test(arr[i]));
    	}
    }
    System.out.println("b0=" + bf.test("b0"));
    bf.dump();*/
  }

}
