package minHash;

import java.util.ArrayList;
import java.util.*;
import java.io.*;

public class minHash2 {
    private int k = 300; // Number of hash functions
    private int p = 2147483647; //Large prime
    private ArrayList<int[]> minhash;
    LSH lsh = new LSH();
    int charsPerString = 5;
    int[] hashes_a = new int[k];
    int[] hashes_b = new int[k];


    public minHash2() {
        Random rand = new Random();
        for (int i = 0; i < k; i++) {
            this.hashes_a[i] = rand.nextInt(this.p-1) + 1;
            this.hashes_b[i] = rand.nextInt(this.p-1) + 1;
        }
        this.minhash = new ArrayList<int[]>();
    }

    public int getDocsNumber() {
        return this.minhash.size();
    }

    public void computeSignature(String comment) {
        comment = comment.replaceAll("\\s", "");
        ArrayList<String> shinglesList = new ArrayList<String>();
        String[] shingles;
        if (comment.length() < 6){
            shingles = new String[1];
            shingles[0] = comment;
        } else {
            shingles = new String[comment.length() - charsPerString];
            for (int i = 0; i < comment.length() - charsPerString; i++) {
                shingles[i] = comment.substring(i, i + charsPerString);
            }
        }

        for (String value : shingles) {
            if (!shinglesList.contains(value)) {
                shinglesList.add(value);
            }
        }
        int[] signature = new int[k];
        for (int i = 0; i < k; i++) {
            double min = Double.POSITIVE_INFINITY;
            for (String shingle : shinglesList) {
                int hash = shingle.hashCode();
                hash = universalHash(hash, this.hashes_a[i], this.hashes_b[i]);
                if (hash < min) min = hash;
            }
            signature[i] = (int) min;
        }
        this.minhash.add(signature);
        this.lsh.addSigToLSH(signature);
    }

    public void computeSignature(Set<String> placesSet) {
        int[] signature = new int[k];
        for (int i = 0; i < k; i++) {
            double min = Double.POSITIVE_INFINITY;
            for (String place : placesSet) {
                int hash = place.hashCode();
                hash = universalHash(hash, this.hashes_a[i], this.hashes_b[i]);
                if (hash < min) min = hash;
            }
            signature[i] = (int) min;
        }
        this.minhash.add(signature);
        this.lsh.addSigToLSH(signature);
    }

    public int getSimilaritiesAboveThresholdLSH(double threshold, boolean print_sims) {
        int counter = 0;
        ArrayList<int[]> candidatesPairs = this.lsh.getCandidates();
        for (int[] candidatesPair : candidatesPairs) {
            int column1_index = candidatesPair[0];
            int column2_index = candidatesPair[1];
            int[] column1 = this.minhash.get(column1_index);
            int[] column2 = this.minhash.get(column2_index);
            double similarity = minHashSimillarity(column1, column2);
            if (similarity >= threshold) {
                if (print_sims) {
                    System.out.printf("Par: [%d, %d] - Similaridade: %4.3f\n",column1_index, column2_index,similarity);
                }
                counter++;
            }
        }
        return counter;
    }

    public int getSimilaritiesAboveThreshold(double threshold, boolean print_sims) {
        int counter = 0;
        for(int i = 0; i < this.minhash.size(); i++) {
            for(int j = i+1; j < this.minhash.size(); j++) {
                int sum = 0;
                for(int m = 0; m < this.k; m++) {
                    if(this.minhash.get(j)[m] == this.minhash.get(i)[m]) {
                        sum++;
                    }
                }
                double similarity = (double)(sum) / (double)(k);
                if(similarity >= threshold) {
                    if (print_sims) {
                        System.out.printf("Par: [%d, %d] - Similaridade: %4.3f\n",i,j,similarity);
                    }
                    counter++;
                }
            }
        }
        return counter;
    }

    private double minHashSimillarity(int[] column1, int[] column2) {
        int counter = 0;
        for (int i = 0; i < column1.length; i++) {
            if (column1[i] == column2[i]) counter++;
        }
        return (double) counter / (double) this.k;
    }


    private int universalHash(int x, int a, int b) {
        long m = a * x + b;
        long r = Math.abs(m % this.p);
        //System.out.printf("result: %d\n", r);
        return (int) r;
    }

    public void printMinHashMatrix() {
        for (int i = 0; i < k; i++) {
            for (int[] signature : this.minhash) {
                System.out.printf("%6d", signature[i]);
            }
            System.out.println();
        }
    }

}

