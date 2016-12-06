package Hash;

import java.lang.Math;
import java.util.BitSet;

public class BloomFilter {

	private final BitSet data;
	
	private int hash_nums;
	public int hashMask;

	BloomFilter(int num_bits, int num_hashs) {
		this.hash_nums = num_hashs;
		
		this.data = new BitSet(1 << num_bits);
		
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
		}
	} //add
	
	public boolean test(String s) {
		for(int i = 1; i <= hash_nums; i++) {
			int index = hashCode(s, i);
			
			//
		}
		
		return true;
	} //test

	public static void main(String[] args) {
		BloomFilter bf = new BloomFilter(30, 3);
		
		System.out.println(bf.hashMask);
		System.out.println("---");
		
		System.out.println(bf.hashCode("10",1));
		System.out.println(bf.hashCode("10",2));
		System.out.println(bf.hashCode("10",3));
		System.out.println(bf.hashCode("1234567890",1));
		System.out.println(bf.hashCode("1234567890111111111111111111111111111111111111111111111",2));
		System.out.println(bf.hashCode("123456789012222222221111111111111111",2));
	}

}
