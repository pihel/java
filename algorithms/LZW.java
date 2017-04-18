package algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LZW {

	/*
	 * 1. создаем словарь из символов длинной = 1 
	 * 2. последовательно расширяем словарь на 1 символ признаком создания новой записи в словаре - смена статуса с есть в словаре на нет в словаре 
	 * 3. заменяем текст в строке на индексы из словаря
	 * 
	 */
	public static List<Integer> compress(String txt) {
		int dict_size = 255;

		List<Integer> result = new ArrayList<Integer>();
		HashMap<String, Integer> dict = new HashMap<String, Integer>();

		// изначально заполняем словарь алфавитом
		for (int i = 0; i < dict_size; i++) {
			dict.put(Character.toString((char) i), i);
		}

		String prev = "";
		String next = "";

		// делаем проход по строке
		for (int i = 0; i < txt.length(); i++) {
			next = prev + txt.charAt(i);

			// если строка есть в словаре, то переходим к следующей итерации
			if (dict.containsKey(next)) {
				prev = next;
			} else {
				// выводим строку
				result.add(dict.get(prev));

				// добавляем строку + новый символ в словарь
				dict.put(next, ++dict_size);

				// сбрасываем символ на текущий
				prev = Character.toString(txt.charAt(i));
			}
		}

		if (prev != "") {
			result.add(dict.get(prev));
		}

		return result;
	} // compress

	/*
	 * самоформирующийся словарь - вначале идут несжатые буквы, на основе
	 * которых формируются новые сочетания новое сочетание = прошлое значение +1
	 * буква из нового
	 */
	public static String decompress(List<Integer> compr) {
		int dict_size = 255;

		HashMap<Integer, String> dict = new HashMap<Integer, String>();

		// изначально заполняем словарь алфавитом
		for (int i = 0; i < dict_size; i++) {
			dict.put(i, Character.toString((char) i));
		}

		String entry = "";

		// первый символ всегда не сжат - просто кладем в выходной поток
		String prev = Character.toString((char) (int) compr.get(0));
		String result = prev;

		// проход по архиву
		for (int i = 1; i < compr.size(); i++) {
			entry = "";
			int k = compr.get(i);

			// берем строку из словаря
			if (dict.containsKey(k)) {
				entry = dict.get(k);
			} else if (k >= dict_size) {
				entry = prev + prev.charAt(0);
			} else {
				entry = Integer.toString(k);
			}
			result = result + entry;

			// добавляем в словарь прошлую строку + 1 буква из текущего словаря
			dict.put(++dict_size, prev + entry.charAt(0));

			// подменяем прошлое значение - текущим
			prev = entry;
		}

		return result;
	} // decompress

	public static void main(String[] args) {
		System.out.println("TATAGATCTTAATATA");
		List<Integer> compressed = compress("TATAGATCTTAATATA");
		System.out.println(compressed);
		String decompressed = decompress(compressed);
		System.out.println(decompressed);
	}

}