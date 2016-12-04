package Hash;

public class BloomFilter {

	private static final int MAX_HASHES = 8;
	private static long[] byteTable;
	private static final long HSTART = 0xBB40E64DA205B064L;
	private static final long HMULT = 7664345821815920749L;

	BloomFilter() {
		byteTable = new long[256 * MAX_HASHES];
		long h = 0x544B2FBACAAF1684L;
		for (int i = 0; i < byteTable.length; i++) {
			for (int j = 0; j < 31; j++) {
				h = (h >>> 7) ^ h;  //сдвиг вправо значение >> количество (при смещении за пределы - бит теряется, на новом месте появляется 0) 
				h = (h << 11) ^ h;  // ^ = XOR
				h = (h >>> 10) ^ h; //1 сдвиг вправо = деление на 2 с откидывнием остатка, влево - умножение на 2 (пока есть место)
			}
			byteTable[i] = h;
		}
	} //BloomFilter

	public long hashCode(String s, int hcNo) {
		long h = HSTART;
		final long hmult = HMULT;
		final long[] ht = byteTable;
		int startIx = 256 * hcNo;
		for (int len = s.length(), i = 0; i < len; i++) {
			char ch = s.charAt(i);
			h = (h * hmult) ^ ht[startIx + (ch & 0xff)];
		    h = (h * hmult) ^ ht[startIx + ((ch >>> 8) & 0xff)];
		}
		return h;
	}

	public static void main(String[] args) {
		BloomFilter bf = new BloomFilter();
		
		System.out.println(bf.hashCode("1",1));
		System.out.println(bf.hashCode("1",1));
		System.out.println(bf.hashCode("1",2));
	}

}
