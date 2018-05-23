package algorithms;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

//запись с ключ-значение
class SSItem {
	Integer key;
	String value;
	//+ идентификатор последовательности вставки
	Integer lsn;
	
	SSItem(Integer key,	String value, Integer lsn) {
		this.key = key;
		this.value = value;
		this.lsn = lsn;
	}
	
	//получить элемент в виде форматированной строки
	String get() {
		return //String.format("%10s", key).replace(' ', '0') +
				//String.format("%10s", lsn).replace(' ', '0') + 
				String.format("%10s", value.length()).replace(' ', '0') + 
				value;
	} //get
	
	void dump() {
		System.out.println(get());
		
	}
} //SSItem

//индекс над таблицей с дамыми
class SSTableIndex {
	//метаданные:
	//минимальный и максимальный ключи в таблице
	Integer min_key;
	Integer max_key;
	//минимальный и максимальный порядковый lsn
	Integer min_lsn;
	Integer max_lsn;
	
	//если таблица на диске, то путь до файла
	String path;
	
	//ключ - смещение
	HashMap<Integer, Integer> keys = new HashMap<Integer, Integer>();
	
	//добавить ключ в индекс
	void add(Integer k) {
		//также обновляем метаданные
		max_lsn = LSMTree.glsn;
		if(min_lsn == null) min_lsn = max_lsn;
		if(min_key == null || k < min_key) min_key = k;
		if(max_key == null || k > max_key) max_key = k;
		//добавление идет в память в хэш таблицу, на первом этапе смещения в файле нет
		keys.put(k, 0);
	}
	
	//получить значение ключа из открытого файла
	String getByPath(Integer key, RandomAccessFile file) throws IOException {
		//получаем смещение в файле для ключа из индекса
		Integer offset = keys.get(key);
		
		//смещаемся в файле
		file.seek(offset);
		
		//резервируем 10 байт под переменную с длинной значения
		byte[] lenb = new byte[10];
		file.read(lenb, 0, 10);
		
		Integer len = Integer.parseInt(new String(lenb, StandardCharsets.UTF_8));
	
		file.seek(offset + 10);
		
		//считываем значение
		byte[] valb = new byte[len];
		file.read(valb, 0, len);
		
		return new String(valb, StandardCharsets.UTF_8);
	} //getByPath
	
	//получить значение ключа с диска
	String getByPath(Integer key) throws IOException {
		if(!keys.containsKey(key)) return null;
		
		RandomAccessFile file = new RandomAccessFile(path, "r");
		
		String val = getByPath(key, file);
		
		file.close();

		return val;
	}
	
	void dump() {
		System.out.println("SSTableIndex start >>>");
		System.out.println("min_key = " + min_key);
		System.out.println("max_key = " + max_key);
		System.out.println("min_lsn = " + min_lsn);
		System.out.println("max_lsn = " + max_lsn);
		System.out.println("path = " + path);
		System.out.println("size = " + keys.size());		
		
		for(Entry<Integer, Integer> entry : keys.entrySet()) {
			System.out.print(entry.getKey() + ".offst=" + entry.getValue() + "; ");
		}
		System.out.println("");
		System.out.println("SSTableIndex end <<< ");
		System.out.println("");
	} //dump
	
} //SSTableIndex

//простая реализация MemSSTable в виде ассоциотивного массива, в статье делали btree
class MemSSTable {	
	SSTableIndex indx;
	
	MemSSTable() {
		indx = new SSTableIndex();
	}
	
	//из-за хэша нет поиска по диапазону
	HashMap<Integer, SSItem> itms = new HashMap<Integer, SSItem>();
	
	//простой вариант без фоновых заданий и асинхронных вызовов
	//будет фриз при достижении лимита, т.к. таблица скидывается на диск
	void SaveToDisk() throws FileNotFoundException, UnsupportedEncodingException {
		indx.path = "sstable_" + indx.max_lsn + ".dat";
		PrintWriter writer = new PrintWriter(indx.path, "UTF-8");
		Integer pad = 0;
		//последовательно пишем 10 байт с длинной значения и само значение
		for(Entry<Integer, SSItem> entry : itms.entrySet()) {
			SSItem itm = entry.getValue();
			String val = itm.get();
			writer.print( val );
			//регистрируем в индексе смещения в файле
			indx.keys.put(itm.key, pad);
			pad = pad + val.length();
		}
		writer.close();
		
		LSMTree.indexes.add(indx);
	} //SaveToDisk
	
	//добавить новый элемент
	void add(Integer k, String v) {
		//увеличиваем глобальный счетчик операций glsn
		LSMTree.glsn++;
		
		//если размер превышает, 
		if(itms.size() >= LSMTree.max_sstable_size) {
			try {
				//то сохраняем таблицу на диск
				//в реальных движках используется упреждающая фоновая запись
				//когда папять заполнена на N% (n<100), то данные скидываются на диск заранее, чтобы избежать фриза при сбросе памяти и записи на диск
				SaveToDisk();
			} catch (FileNotFoundException | UnsupportedEncodingException e) {
				e.printStackTrace();
				return;
			}
			//очищаем данные под новые значения
			indx = new SSTableIndex();
			itms = new HashMap<Integer, SSItem>();
		}
		
		
		indx.add(k);
		
		SSItem itm = new SSItem(k, v, indx.max_lsn);
		
		//в моей реализации, при повторе ключ перезаписывается
		//т.е. транзакционность и многоверсионность тут не поддерживается
		itms.put(k,  itm);
		
	} //add
	
	//для таблицы в памяти просто достаем значение из хэша
	String getKey(Integer key) {
		if(itms.containsKey(key)) {
			return itms.get(key).value;
		}
		return null;
	} //getKey
	
} //SSTable

public class LSMTree {
	//via https://habr.com/company/mailru/blog/358210/
	
	static int glsn = 0;
	//максимальный размер, после которого таблица должна быть скинута на диск
	//для упрощения алгоритма = числу записей, а не размеру в байтах
	final static int max_sstable_size = 10; 
	
	//текущая таблица в памяти, куда вставляем данные
	MemSSTable MemTable;
	
	//все индексы, даже для таблиц на диске, хранятся в памяти
	static LinkedList<SSTableIndex> indexes = new LinkedList<SSTableIndex>();
	
	LSMTree() {
		MemTable = new MemSSTable();
	}
	
	//добавить запись
	public void add(Integer k, String v) {
		MemTable.add(k, v);
	}
	
	public void dump() {
		MemTable.indx.dump();
		for (SSTableIndex indx : indexes) {
			indx.dump();
		}
	} //dump
	
	//получить значение по ключу
	String getKey(Integer key) {
		//сперва смотрим в памяти
		String val = MemTable.getKey(key);
		
		if(val == null) {
			SSTableIndex indx_with_max_lsn = null;
			//потом таблицу по индексам, которая содержит наш ключ
			//если содержится в нескольких, то берем с максимальным lsn, т.к. это последнее изменение
			for (SSTableIndex indx : indexes) {
				Integer max_lsn = 0;
				if(key >= indx.min_key && key <= indx.max_key && max_lsn < indx.max_lsn ) {
					if(indx.keys.containsKey(key)) {
						max_lsn = indx.max_lsn;
						indx_with_max_lsn = indx;
					}
				}
			}
			//читаем из таблицы с диска
			if(indx_with_max_lsn != null) {
				try {
					return indx_with_max_lsn.getByPath(key);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return val;
	}
	
	//объединить несколько таблиц на диске в 1 большую
	void merge() throws IOException {
		Integer min_key = null;
		Integer max_key = null;
		Integer min_lsn = null;
		Integer max_lsn = null;
		
		//сортируем таблицы по убыванию lsn
		//чтобы вначале были самые свежие ключи
		Collections.sort(indexes, new Comparator<SSTableIndex>() {
			@Override
			public int compare(SSTableIndex o1, SSTableIndex o2) {
				if(o1.max_lsn > o2.max_lsn) {
					return -1;
				} else if(o1.max_lsn < o2.max_lsn) {
					return 1;
				}
				return 0;
			}
		});
		
		SSTableIndex merge_indx = new SSTableIndex();
		
		Integer pad = 0;
		merge_indx.path = "sstable_merge.dat";
		PrintWriter writer = new PrintWriter(merge_indx.path, "UTF-8");
		
		//пробегаемся по всем индексам, чтобы слить в 1
		for (SSTableIndex indx : indexes) {
			if(min_lsn == null || indx.min_lsn < min_lsn) min_lsn = indx.min_lsn;
			if(min_key == null || indx.min_key < min_key) min_key = indx.min_key;
			if(max_key == null || indx.max_key > max_key) max_key = indx.max_key;
			if(max_lsn == null || indx.max_lsn > max_lsn) max_lsn = indx.max_lsn;
			
			RandomAccessFile file = new RandomAccessFile(indx.path, "r");
			
			//т.к. данные в таблицах не упорядочены, это приводит к рандомным чтениям с диска
			//в реальности делают упорядочнный по ключу массив, чтобы делать быстрые последовательные чтения
			for(Entry<Integer, Integer> entry : indx.keys.entrySet()) {
				//оставляем запись только с максимальным lsn
				Integer key = entry.getKey();
				if(!merge_indx.keys.containsKey(key)) {
					String val = indx.getByPath(key, file);
					
					SSItem itm = new SSItem(key, val, 0);
					String itmval = itm.get();
					
					writer.print( itmval );
					merge_indx.keys.put(key, pad);
					pad = pad + itmval.length();					
				}
			}
			//записываем и удаляем старые файлы
			file.close();
			//delete
			File fd = new File(indx.path);
			fd.delete();
		}		
		
		merge_indx.min_lsn = min_lsn;
		merge_indx.min_key = min_key;
		merge_indx.max_key = max_key;
		merge_indx.max_lsn = max_lsn;
		
		writer.close();
		
		//переименовываем к обычному имени
		File fd = new File(merge_indx.path);
		merge_indx.path = "sstable_" + merge_indx.max_lsn + ".dat";
		File fdn = new File(merge_indx.path);
		fd.renameTo(fdn);
		
		indexes = new LinkedList<SSTableIndex>();
		indexes.add(merge_indx);
		
	} //merge

	public static void main(String[] args) {
		LSMTree tree = new LSMTree();
		
		for(int i = 0; i < 50; i++) {
			tree.add(i%20, "test"+i);
		}
		//tree.dump();
		System.out.println("key = 33; val = " + tree.getKey(33) );
		System.out.println("key = 10; val = " + tree.getKey(10) );
		System.out.println("key = 66; val = " + tree.getKey(66) );
		
		System.out.println("==============================================");
		try {
			tree.merge();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//tree.dump();
		System.out.println("key = 33; val = " + tree.getKey(33) );
		System.out.println("key = 10; val = " + tree.getKey(10) );
		System.out.println("key = 66; val = " + tree.getKey(66) );
	}

}
