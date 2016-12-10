package Hash;

import java.lang.Math;
import java.util.BitSet;

public class BloomFilter {

  private long data;
  
  private int hash_nums;
  public long hashMask;

  BloomFilter(int num_bits, int num_hashs) {
    this.hash_nums = num_hashs;
    
    this.hashMask = (long)( 1L << num_bits ) -1 ;
    
    this.data = 0L;
  } //BloomFilter

  //номер бита в битовом массиве
  public long hashCode(String s, int hash_num) {
    long result = 1;
    for (int i = 0; i < s.length(); ++i) {
      //1 = (1 * 1 + 58)
      //1 = ( 0001 * 0001 + 11 0001 ) & 1111 1111 1111 1111 
      result = (hash_num * result + s.charAt(i)) & this.hashMask;
    }
    
    return result % Long.SIZE;
  }
  
  public void setBit(long index) {
	  this.data = this.data | (1L << index );
  } //setbit
  
  public long getBit(long index) {
	  return ( this.data >>> index )  & 1;
  } //getBit
  
  public void add(String s) {
	System.out.println(s);
	for(int i = 1; i <= hash_nums; i++) {
	  long index = hashCode(s, i);
	  System.out.print(i+" ) [" + index + "] ");
	  setBit(index);
	  dump();
	}
  } //add
  
  public boolean test(String s) {
    for(int i = 1; i <= hash_nums; i++) {
      long index = hashCode(s, i);
      
      if( getBit(index) == 0L ) return false;
    }
    
    return true;
  } //test
  
  public void dump(long _data) {
	  System.out.println(_data + " = " + Long.toBinaryString(_data | (this.hashMask + 1)).substring(1));
  } //dump
  
  public void dump() {
	  dump(this.data);
  } //dump

  public static void main(String[] args) {
    BloomFilter bf = new BloomFilter(63, 3);
    
    String[] arr = new String[100];
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
    bf.dump();
  }

}
