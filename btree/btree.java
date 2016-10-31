package btree;

public class btree {
	
    //кол-во строк в блоке
    private static final int rows_block = 5;

    //начало дерева
    private Block root;
    
    //высота дерева
    private int blevel;
    
    //кол-во строк в индексе
    private int num_rows;
    
    //block - блок данных со строками
    private static final class Block {
    	//???? кол-во строк в блоке
        private int cnt;
        //массив строк в блоке
        private Entry[] rows = new Entry[rows_block];

        //создать блок с K элементов
        private Block(int k) {
            cnt = k;
        }
    } //Node
	
    //элемент дерева
    private static class Entry {
    	//ключ - ссылка на плоские данные
        private Comparable rowid;
        //индексируемое значение
        private Object val;
        private Block next;
        public Entry(Comparable rowid, Object val, Block next) {
            this.rowid  = rowid;
            this.val  = val;
            this.next = next;
        }
    } //Entry
    
    public btree() {
        root = new Block(0);
    }
    
    //кол-во элементов в индексе
    public int count() {
        return num_rows;
    }
    
    //высота дерева -1
    public int blevel() {
        return blevel - 1;
    }

	public static void main(String[] args) {
		//http://www.juliandyke.com/Presentations/Presentations.html
		//http://www.sql.ru/forum/661061/indeksy-b-tree-posmotret-cherez-dump-bloka-pravednik-au?mid=7139186#7139186
		System.out.println("test");

	}

} //btree
