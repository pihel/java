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
    	return cnt / TABLE_SIZE;
    } //getLoadFactor
    
    //кол-во проб хэш массива для получения элкмента
    public double getAvgProbeCnt() {
    	//кол-во проб ~= (loadFactor) / (1 - loadFactor)
    	
    	return getLoadFactor()  / (1 - getLoadFactor() );
    } //getAvgProbeCnt

    public Value get(int key) {    	
          int hash = (key % TABLE_SIZE);
          int initialHash = -1;
          while (hash != initialHash && table.get(hash) != null && table.get(hash).key != key) {
        	  if (initialHash == -1) {
        		  initialHash = hash;
              }
              hash = (hash + 1) % TABLE_SIZE;
          }
          if (table.get(hash) == null || hash == initialHash) {
                return null;
    	  } else {
                return table.get(hash).value;
          }
    } //get

    public void put(int key, Value value) {
          int hash = (key % TABLE_SIZE);
          int initialHash = -1;
          int indexOfDeletedEntry = -1;
          while (hash != initialHash && table.get(hash) != null && table.get(hash).key != key) {
                if (initialHash == -1) {
                    initialHash = hash;
                }
                hash = (hash + 1) % TABLE_SIZE;
          }
          if (initialHash != hash) {
                if (table.get(hash) != null && table.get(hash).key == key) {
                	table.get(hash).value = value;
                } else {
                	table.set(hash, new HashEntry(key, value) );
                }
          }
    } //put
	
	public static void main(String[] args) {
		System.out.println("test");
		
		Hash h = new Hash(16);
		for(int i = 0; i < 16; i++) {
			h.put(i, -i);
		}
		for(int i = 0; i < 16; i++) {
			System.out.print ( h.get(i) + ", " );
		}
		
	} //main

}
