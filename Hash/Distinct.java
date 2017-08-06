package Hash;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;

interface iDistinct {
	public void set(String word);
	public int get();
}

class RealDist implements iDistinct {
	HashMap<String, Integer> words;

	public RealDist() {
		words = new HashMap<String, Integer>();
	}

	public void set(String word) {
		if (!words.containsKey(word)) {
			words.put(word, 1);
		} else {
			words.put(word, words.get(word) + 1);
		}
	} //set

	public int get() {
		return words.size();
	} //get

	public int getWordCount(String word) {
		return words.get(word);
	} //getWordCount

} // RealDist

// https://habrahabr.ru/post/119852/
class AproxDist implements iDistinct {
	int hashes[];

	public AproxDist() {
		hashes = new int[256];
		Arrays.fill(hashes, 0);
	} //AproxDist

	public static int fnv1a(String text) {
		int hash = 0x811c9dc5;
		for (int i = 0; i < text.length(); ++i) {
			hash ^= (text.charAt(i) & 0xff);
		    hash *= 16777619;
		}
		return hash >>> 0;
	} //fnv1a

	// позиция первого ненулевого бита справа
	public static int rank(int hash, int max_rank) {
		int r = 1;
		while ((hash & 1) == 0 && r <= max_rank) {
			r++;
			// смещаем вправо, пока не дойдет до hash & 1 == 1
			hash >>>= 1;
		}
		return r;
	} //rank

	public static double log2(double x) {
		return Math.log(x) / Math.log(2);
	} //log2

	public void set(String word) {
		int hash = fnv1a(word);
		// убираем 24 бита из 32 справа - остается 8 левых
		// (=256 разных значений)
		int k = hash >>> 24;
		
		// если коллизия, то берем наибольший ранк
		hashes[k] = Math.max(hashes[k], rank(hash, 24)); 
	} // count

	//самый простой вариант - 2 в степени максимальный ранк
	public int get_log() {
		double max_rank = 0;

		for (int i = 0; i < hashes.length; i++) {
			max_rank = Math.max(hashes[i], max_rank);
		}

		return (int) Math.pow(2, max_rank);
	} //get_log

	//по формуле из статьи
	public double get_loglog() {
		double count = 0;

		for (int i = 0; i < hashes.length; i++) {
			count += 1 / Math.pow(2, hashes[i]);
		}

		return 47072.7126712022335488 / count;
	} //get_loglog

	//по формуле из статьи + коррекция
	public int get() { 
		long pow_2_32 = 4294967296L;

		double E = get_loglog();

		// коррекция
		if (E <= 640) {
			int V = 0;
			for (int i = 0; i < 256; i++) {
				if (hashes[i] == 0) {
					V++;
				}
			}
			if (V > 0) {
				E = 256 * Math.log((256 / (double) V));
			}

		} else if (E > 1 / 30 * pow_2_32) {
			E = -pow_2_32 * Math.log(1 - E / pow_2_32);
		}
		// конец коррекции

		return (int) Math.round(E);
	} //get

} // AproxDist

public class Distinct {
	public static void main(String[] args) throws IOException {

		RealDist rd = new RealDist();
		AproxDist ad = new AproxDist();

		BufferedReader br = new BufferedReader(
				new FileReader("C:\\Users\\007\\workspace\\oracle_sort\\src\\Hash\\book.txt"));
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
		/*
		 * ad.set("aardvark"); 
		 * ad.set("abyssinian"); 
		 * ad.set("zoology");
		 * ad.set("zoology");
		 */

		System.out.println("real distinct = " + rd.get()); //3737
		System.out.println("aprox distinct(log) = " + ad.get_log()); //16384
		System.out.println("aprox distinct(loglog) = " + Math.round(ad.get_loglog())); //3676
		System.out.println("aprox distinct(superlolog) = " + ad.get()); //3676
	}
}
