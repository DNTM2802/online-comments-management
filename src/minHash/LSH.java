package minHash;

import java.util.*;

public class LSH {

    // LSH MADE TO SELECT CANDIDATES FROM MINHASH WITH 300 HASH FUNCTIONS

    private int bands = 20;
    private int rows = 15;
    private int minHashFunctions;
    private ArrayList<int[]> lsh_matrix;
    private ArrayList<int[]> candidates;
    private int p = 2147483647; //Large prime
    int[] hashes_a = new int[bands];
    int[] hashes_b = new int[bands];

    public LSH(int rows) {
        this.lsh_matrix = new ArrayList<int[]>();
        this.candidates = new ArrayList<int[]>();
        this.bands = (int)((double)minHashFunctions / (double)rows);
        this.rows = rows;
        this.minHashFunctions=300;
        Random rand = new Random();
        for (int i = 0; i < this.bands; i++) {
            this.hashes_a[i] = rand.nextInt(this.p-1) + 1;
            this.hashes_b[i] = rand.nextInt(this.p-1) + 1;
        }
    }

    public LSH() {
        this.minHashFunctions=300;
        this.lsh_matrix = new ArrayList<int[]>();
        this.candidates = new ArrayList<int[]>();
        Random rand = new Random();
        for (int i = 0; i < this.bands; i++) {
            this.hashes_a[i] = rand.nextInt(this.p-1) + 1;
            this.hashes_b[i] = rand.nextInt(this.p-1) + 1;
        }
    }

    private int hash2(int x, int a, int b) {
        long m = a * x + b;
        long r = Math.abs(m % this.p);
        return (int) r;
    }
    public void addSigToLSH(int[] minHash_column) {
        int start = 0;
        int end = this.rows;
        int[] lsh_column = new int[bands];
        for (int k = 0; k < bands; k++) {
            int[] slice = Arrays.copyOfRange(minHash_column, start, end);
            int hash = Arrays.hashCode(slice);
            hash = hash2(hash, this.hashes_a[k], this.hashes_b[k]);
            lsh_column[k] = hash;
            start += this.rows;
            end += this.rows;
        }
        this.lsh_matrix.add(lsh_column);
        for (int x = 0; x < this.lsh_matrix.size() - 1; x++) {
            for (int h = 0; h < this.getBands(); h++)
                if (lsh_matrix.get(x)[h] == lsh_column[h]) {
                    int[] pair = new int[2];
                    pair[0] = x;
                    pair[1] = this.lsh_matrix.size() - 1;
                    if (Integer.compare(pair[0], pair[1]) != 0) {
                        //System.out.printf("Pair: [%d, %d]", pair[0], pair[1]);
                        this.candidates.add(pair);
                    }
                    break;
                }
        }
        //System.out.println();
    }

    public int getBands() {
        return this.bands;
    }

    public ArrayList<int[]> getLSHmatrix() {
        return lsh_matrix;
    }

    public void printLSHMatrix() {
        for (int i = 0; i < this.bands; i++) {
            for (int[] signature : this.lsh_matrix) {
                System.out.printf("%15d", signature[i]);
            }
            System.out.println();
        }
    }

    public ArrayList<int[]> getCandidates() {
        return candidates;
    }


}
