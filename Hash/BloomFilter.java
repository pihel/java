package Hash;

import java.lang.Math;

public class BloomFilter {

	private byte[] byteTable;
	
	private int hash_nums;

	BloomFilter(int _hash_nums) {
		hash_nums = _hash_nums;
		byteTable = new byte[32 * hash_nums];
	} //BloomFilter

	public static int hashCode(String s, int hash_num) {
		int result = 1;
        for (int i = 0; i < s.length(); ++i) {
        	//1 = (1 * 1 + 58)
        	//1 = ( 0001 * 0001 + 11 0001 ) & 1111 1111 1111 1111 1111 1111 1111 1111
        	result = (hash_num * result + s.charAt(i)) & 0xFFFFFFFF;
        }
        
        return result;
	}
	
	public void add(String s) {
		for(int i = 1; i <= hash_nums; i++) {
			int index = hashCode(s, i);
			
			byteTable[(int)Math.floor(index / 32)] |= 1 << (index % 32);
		}
	} //add
	
	public boolean test(String s) {
		for(int i = 1; i <= hash_nums; i++) {
			int index = hashCode(s, i);
			
			if( ((byteTable[(int)Math.floor(index / 32)] >>> (index % 32)) & 1) == 0) {
				return false;
			}
		}
		
		return true;
	} //test

	public static void main(String[] args) {
		BloomFilter bf = new BloomFilter(3);
		
		System.out.println(bf.hashCode("10",1));
		System.out.println(bf.hashCode("10",2));
		System.out.println(bf.hashCode("10",3));
		System.out.println(bf.hashCode("1234567890",1));
		System.out.println(bf.hashCode("1234567890111111111111111111111111111111111111111111111",2));
		System.out.println(bf.hashCode("123456789012222222221111111111111111",2));
	}

}
