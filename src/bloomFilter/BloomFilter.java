package bloomFilter;

import java.util.*;
import java.lang.Math.*;

public class BloomFilter {

	private ArrayList<Integer> c = new ArrayList<>();
	private int k;				//number of hashes
	private int m;
	
	//initialize bloom filter
	public BloomFilter(double p, int n) {
		this.m = (int) Math.ceil((n * Math.log(p)) / Math.log(1 / Math.pow(2, Math.log(2))));
		this.k = (int) Math.round((m / n) * Math.log(2));
		for (int i=0; i<m; i++) {
			c.add(i,0);
		}
		System.out.printf("Bloom filter inicializado\n");
		System.out.printf("Tamanho ideal do BloomFilter calculado: %d\n", m);
		System.out.printf("Número de hash functions ideal calculado: %d\n", k);
	}
	
	public int hash(String elemento, int seed) {		
		long hash =0;
		for (int i= 0; i < elemento.length(); i++) {
			hash= seed * (hash << 5)  + elemento.charAt(i);
		}
		
		hash = hash % this.m;
		return (int) (hash>=0 ? hash : hash + m);
	}

	
	//insert items in the filter
	public void insert(String element) {
		for (int i=0; i< this.k; i++) {
			int key = hash(element, i);
			//normal bloom filter
			this.c.set(key,1);
		}
		
	}
	
	//check if element is member of the filter
	public boolean isMember(String element) {
		for (int i=0; i< this.k; i++) {
			int key = hash(element, i);
			if (this.c.get(key)==0) {
				return false;
			} 
		}
		return true;	

	};
	
	//print items
	public void listItems() {
		for (int k=0; k<c.size(); k++) {
			System.out.printf( "%d: %d \n", k, this.c.get(k));
		}
	}

	
	public int getK() {
		return k;
	}

	public int getSize() {
		return m;
	}
	
}
