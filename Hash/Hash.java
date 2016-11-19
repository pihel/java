package Hash;

import java.util.ArrayList;

//хэш группа

public class Hash<Value> {
	
	class HashEntry {
	    public int key;
	    public Value value;

	    public HashEntry(int key, Value value) {
	          this.key = key;
	          this.value = value;
	    } //HashEntry
	} //HashEntry
	
	 //inital capacity  - начальная загрузка
	private int TABLE_SIZE;
	
	//число элементов в хэщ таблице
	private int cnt;
	
	//максимальная заполненность хэш таблицы - после этого нужно перестраивать
	private double max_load_factor = 0.75;
	 
	ArrayList<HashEntry> table;

    public Hash(int cnt_elements) {
    	
    	//число хэш блоков = число элементов / максимальную загрузку
    	TABLE_SIZE = (int) (cnt_elements / max_load_factor );
    	
    	//TABLE_SIZE = 10;
    	
    	//резервируем память: начальный и конечный размер массива = TABLE_SIZE
        table = new ArrayList<HashEntry>(TABLE_SIZE);
        
        for (int i = 0; i < TABLE_SIZE; i++) {
        	//table[i] = null;
        	table.add(i, null);
        }
    } //Hash
    
    
    public Hash(HashEntry[] tbl, int hash_area_size) {
    	//кол-во хэш групп
    	int group_cnt = tbl.length / hash_area_size + 1;
    	
    	Hash grp = new Hash(group_cnt);
    } //Hash
    
    //заполненность хэш таблицы
    public double getLoadFactor() {
    	//= число элементов / число хэш блоков
    	return (double)cnt / (double)TABLE_SIZE;
    } //getLoadFactor
    
    //кол-во проб хэш массива для получения элкмента
    public double getAvgProbeCnt() {
    	//кол-во проб ~= (loadFactor) / (1 - loadFactor)
    	
    	return getLoadFactor()  / (1 - getLoadFactor() );
    } //getAvgProbeCnt
    
    public int getCnt() {
    	return cnt;
    } //getCnt
    
    public int getTblSize() {
    	return TABLE_SIZE;
    } //getTblSize
    
    
    //получить хэш ключ по ключу поиска
    private int getHash(int key) {
    	//хэш ключ таблицы
        int hash = (key % TABLE_SIZE);
        //первое значение ключа хэширования
        int initialHash = -1;
        
        //если хэш нашелся в таблице, но ключ не наш - это коллизия
        //или там уже лежит ключ от другой коллизии
        //двигаемся дальше, пока не найдем наш ключ
        while (hash != initialHash && table.get(hash) != null && table.get(hash).key != key) {
        	
        	//запишем хэш колллизии, чтобы не заходить в нее повторно для этого ключа
			if (initialHash == -1) {
				initialHash = hash;
		    }
			
			//смещаемся на ключ вправо по модулю TABLE_SIZE
			//т.е. если дошли до правого края, то возвращаемся к первому
		    hash = (hash + 1) % TABLE_SIZE;
        }
        
        //если прошли по кругу, то ключ не найден
        if(hash == initialHash) return -1;
        
        return hash;
    	
    } //getHash

    public Value get(int key) {
    	//хэш ключ таблицы
        int hash = getHash(key);
          
        //значение по хэшу
        if (hash == -1 || table.get(hash) == null) {
        	return null;
    	} else {
    		return table.get(hash).value;
        }
    } //get

    public void put(int key, Value value) {
    	//хэш ключ таблицы
        int hash = getHash(key);
		
		//если хэш для вставки нашелся
		if (hash != -1) {
			//повторная вставка существующего ключа - переписываем значение
	        if (table.get(hash) != null && table.get(hash).key == key) {
	        	//TODO????
	        	table.get(hash).value = value;
	        } else {
	        	//новый элемент
	        	table.set(hash, new HashEntry(key, value) );
	        	cnt++;
	        }
		}
    } //put
	
	public static void main(String[] args) {
		System.out.println("test");
		
		Hash h = new Hash(16);
		/*h.put(1,1);
		h.put(1,1);
		h.put(11,11);
		h.put(2,2);
		
		System.out.print ( h.get(111) );*/
		
		for(int i = 0; i < 16; i++) {
			h.put(i, -i);
		}
		for(int i = 0; i < 16; i++) {
			System.out.print ( h.get(i) + ", " );
		}
		System.out.println ( " " );
		System.out.println ( "cnt = " + h.getCnt() );
		System.out.println ( "tbl size = " + h.getTblSize() );		
		System.out.println ( "load factor = " + h.getLoadFactor() );
		System.out.println ( "avg probes = " + h.getAvgProbeCnt() );
		
	} //main

}
