package algorithms;

import java.io.*;
import java.util.*;

//очередь с приоритетами
class PriorQueue<T extends Comparable> {
	//на основе обычного ссылочного массива
	ArrayList<T> arr = new ArrayList<T>();
	
	//добавляем в конец = O(1)
	public void add(T obj) {
		arr.add(obj);
	}// add
	
	//извлекаем наименьший элемент = O(N)
	public T get() {
		if (arr.size() == 0)
			return null;
		
		//ищем наименьший элемент
		T prior = arr.get(0);
		for (int i = 1; i < arr.size(); i++) {
			if (arr.get(i).compareTo(prior) < 0) {
				prior = arr.get(i);
			}
		}
		//найденный элемент удаляем
		arr.remove(prior);
		
		return prior;
	}// add
	
	public int size() {
		return arr.size();
	}
} // PriorQueue

public class Huffman {
	//вершина дерева - вверху (наименьший путь) популярные записи, внизу редкие
	Node root;
	
	//таблицы соответствия = буква - битовая карта
	// [буква] = бинарный код
	String codeTable_in[] = new String[256];
	// [бинарный код] = буква
	HashMap<String, Character> codeTable_out = new HashMap<String, Character>();
	
	public Huffman(Node r) {
		this.root = r;
	}
	
	//ветвь дерева 
	static class Node implements Comparable<Node> {
		//буква
		public Character ch;
		//частота символа
		public Integer freq;
		//ветви ниже
		public Node left;
		public Node right;
		
		public Node(Character c, Integer f) {
			this.ch = c;
			this.freq = f;
		} // Node
		
		public Node(Node l, Node r) {
			//частота родителя = сумме частоты детей
			this.freq = l.freq + r.freq;
			this.left = l;
			this.right = r;
		} // Node
		
		@Override
		public int compareTo(Node h) {
			return this.freq - h.freq;
		} // compareTo
		
		public void dump(Huffman parrent) {
			if (this.left != null)
				this.left.dump(parrent);
			if (this.ch != null) {
				String pout = this.ch + " (" + this.freq + ")";
				if (parrent != null) {
					pout = pout + " = " + parrent.codeTable_in[(char) this.ch];
				}
				System.out.println(pout);
			}
			if (this.right != null)
				this.right.dump(parrent);
		} // dump
	} // node
	
	//частота символов в файле
	public static int[] getFreqFromFile(String file) throws IOException {
		int[] freq = new int[256];
		BufferedReader br = new BufferedReader(
				new FileReader(file));
		try {
			String line = br.readLine();
			while (line != null) {
				for (char c : line.toCharArray()) {
					//если это необходимые символы
					if (c > 0 && c < 256) {
						//увеличиваем счетчик кол-ва символов
						freq[c]++;
					}
				}
				line = br.readLine();
			}
		} finally {
			br.close();
		}
		return freq;
	} // getFreqFromFile
	
	//создаем таблицу соответствия буква - сжатая битовая карта
	public void makeCodeTable() {
		//прямая карта
		makeCodeTableIn(this.root, "");
		//обратая карта
		for (int i = 0; i < this.codeTable_in.length; i++) {
			codeTable_out.put(this.codeTable_in[(char) i], (char) i);
		}
	} // makeCodeTableIn
	
	protected void makeCodeTableIn(Node n, String code) {
		if (n == null)
			return;
		if (n.ch != null) {
			this.codeTable_in[(char) n.ch] = code;
		}
		//при переходе ниже левее увеличиваем битовую карту на 0 справа
		this.makeCodeTableIn(n.left, code + "0");
		//при перехода направо ниже увеличиваем битовую карту на 1 справа
		this.makeCodeTableIn(n.right, code + "1");
	} // makeCodeTableIn
	
	//сжатие текста
	public String compress(String txt) {
		String result = new String();
		
		//проходимся по каждому символу текста
		for (int i = 0; i < txt.length(); i++) {
			//если символ есть в таблице преобразования
			if (txt.charAt(i) < 256 && this.codeTable_in[txt.charAt(i)] != null) {
				result = result + this.codeTable_in[txt.charAt(i)];
			} else {
				//если нет, то просто вставляем символ
				result = result + txt.charAt(i);
			}
		}
		return result;
	} // compress
	
	//разжатие текста
	public String decompress(String txt) {
		String result = new String();
		String buf = "";
		
		//обходим каждый бит
		for (int i = 0; i < txt.length(); i++) {
			//накапливаем буфер битов
			buf = buf + txt.charAt(i);
			
			//если буфер есть в таблице соответствия
			if (codeTable_out.containsKey(buf)) {
				//берем из таблицы и сбрасываем буфер
				result = result + codeTable_out.get(buf);
				buf = "";
			} else if (txt.charAt(i) >= 256 || this.codeTable_in[txt.charAt(i)] == null) {
				// нет в таблице преобразования - выдаем как есть
				result = result + txt.charAt(i);
				buf = "";
			}
		}
		result = result + buf;
		return result;
	} // compress
	
	public void dump() {
		this.root.dump(this);
	} // dump
	
	public static void main(String[] args) throws IOException {
		int[] freq = getFreqFromFile("C:\\Users\\007\\Google Диск\\info\\blog-02-17-2017.xml");
		PriorQueue<Node> pq = new PriorQueue();
		for (int i = 0; i < freq.length; i++) {
			if (freq[i] > 0) {
				pq.add(new Node((char) i, freq[i]));
			}
		}
		while (pq.size() > 1) {
			Node l = pq.get();
			Node r = pq.get();
			pq.add(new Node(l, r));
		}
		
		Huffman h = new Huffman(pq.get());
		h.makeCodeTable();
		String test_string = "abcdeя f`d";
		String compr_string = h.compress(test_string);
		String decompr_string = h.decompress(compr_string);
		System.out.println(test_string);
		System.out.println(compr_string);
		System.out.println(decompr_string);
		// h.dump();
	} // main
} // Huffman