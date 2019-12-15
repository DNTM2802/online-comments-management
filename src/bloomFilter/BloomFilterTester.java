package bloomFilter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.lang.Math.*;
import bloomFilter.BloomFilter;

public class BloomFilterTester {

    static List<String> loadCSVtoArray(String filename, int column_user, int column_section) {
        List<String> dataset = new ArrayList<>();
        String line;
        String element;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filename));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] cols = line.split("\t");
                if (cols.length == 3 && !cols[column_user].equals("") && !cols[column_user].equals("A Traveler") && !cols[column_section].equals("")) {
                    element = cols[column_user] + cols[column_section];
                    dataset.add(element.replaceAll("\\s", ""));
                }
                //System.out.println(element);
                // b.insert(element.replaceAll("\\s",""));
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return dataset;
    }

    static String swap(String str, int i, int j) {
        if (j == str.length() - 1)
            return str.substring(0, i) + str.charAt(j)
                    + str.substring(i + 1, j) + str.charAt(i);

        return str.substring(0, i) + str.charAt(j)
                + str.substring(i + 1, j) + str.charAt(i)
                + str.substring(j + 1, str.length());
    }

    public static void main(String[] args) throws FileNotFoundException {

        int n;
        int line = 0;
        long start;
        long time;
        double p;

        Scanner sc = new Scanner(System.in);
        System.out.println("Insira a probabilidade máxima de erro aceitável para a existência de falsos positivos:");
        p = sc.nextDouble();
        sc.close();

        start = System.nanoTime();
        List<String> dataset = loadCSVtoArray("dataset_hotels.csv", 2, 0);
        time = System.nanoTime() - start;
        n = dataset.size();

        System.out.printf("A inserção dos %d elementos no array dataset demorou %3.2f segundos\n", n, time * Math.pow(10, -9));
        BloomFilter b = new BloomFilter(p, n);
        System.out.printf("Probabilidade de erro introduzida: %3.2f\n", p);
        System.out.printf("Número de elementos introduzidos: %d\n", n);

        System.out.println("---------------------------------------------------");

        Set<String> verifiedMatches = new HashSet<>();
        Set<String> notVerifiedMatches = new HashSet<>();
        start = System.nanoTime();
        int step = 100;
        int check_step = 100;
        for (line = 0; line < dataset.size(); line++) {
            b.insert(dataset.get(line));
            if (check_step % step == 0) {
                verifiedMatches.add(dataset.get(line));
                notVerifiedMatches.add(swap(dataset.get(line), 6, dataset.get(line).length() - 4));
            }
            check_step++;
        }


        time = System.nanoTime() - start;
        System.out.printf("A inserção dos %d elementos do dataset no BloomFilter demorou %3.2f segundos\n", n, time * Math.pow(10, -9));
		System.out.println("---------------------");
		System.out.println("---------------------");
        System.out.println("Testes");
		System.out.println("---------------------");
        int true_count = 0;
        int false_positives = 0;
        for (String match : verifiedMatches) {
            System.out.printf("user+hotel=%s pertence? (TRUE...): %s\n", match, b.isMember(match));
            if (b.isMember(match)) {
                true_count++;
            }
        }

		System.out.println("---------------------");

        for (String no_match : notVerifiedMatches) {
            System.out.printf("user+hotel=%s pertence? (FALSE...): %s\n", no_match, b.isMember(no_match));
            if (b.isMember(no_match)) {
                false_positives++;
            }
        }
        System.out.println("---------------------");
        System.out.printf("False negatives (0 EXPECTED...): %d\n", verifiedMatches.size() - true_count);
        System.out.printf("False positives ( +/- %3.2f%% EXPECTED...): %3.2f%%\n", p, (double) false_positives / (double) notVerifiedMatches.size());
        System.out.println("---------------------");


    }
}
