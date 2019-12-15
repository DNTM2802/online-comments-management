package minHash;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class minHashTester {

    private static List<String> loadCommentsFromCSV(String filename, int comment_column, int user_column, int place_column) {
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
            int counter = 0;
            assert br != null;
            while ((line = br.readLine()) != null && counter < 20000) {
                String[] cols = line.split("\t");
                if (cols.length == 3 && !cols[user_column].equals("") && !cols[place_column].equals("")) {
                    element = cols[comment_column];
                    dataset.add(element);
                    counter++;
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return dataset;
    }

    private static Map<String, Set<String>> loadSetsPlacesFromCSV(String filename, int comment_column, int user_column, int place_column) {
        HashMap<String, Set<String>> setsPlaces = new HashMap<String, Set<String>>();
        String line;
        String element;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filename));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            int user_counter = 0;
            assert br != null;
            int counter = 0;
            /* Read first line */
            while ((line = br.readLine()) != null && counter < 20000) {
                counter++;
                /* Split the line by columns */
                String[] cols = line.split("\t");
                if (cols.length == 3 && !cols[user_column].equals("") && !cols[user_column].equals("A Traveler") && !cols[place_column].equals("")) {

                    if (!setsPlaces.containsKey(cols[user_column])) {
                        user_counter++;
                        Set<String> places = new HashSet<>();
                        places.add(cols[place_column]);
                        setsPlaces.put(cols[user_column], places);
                    } else {

                        Set<String> places = setsPlaces.get(cols[user_column]);
                        places.add(cols[place_column]);
                        setsPlaces.put(cols[user_column], places);
                    }

                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return setsPlaces;
    }

    public static void main(String[] args) {
    long start;
    long time1, time2, time3, time4, time5, time6, time7, time8;
        System.out.println("-----------------------------------------------------------------");
        System.out.println("1 - VERIFICAÇÃO DE COMENTÁRIOS SIMILARES");
        System.out.println("-----------------------------------------------------------------");
        System.out.println();


        boolean print_sims = false; // MUDAR PARA TRUE PARA IMPRIMIR OS PARES

        minHash2 minHash_comments = new minHash2();
        System.out.println("A extrair comentários do csv...");
        List<String> comments = loadCommentsFromCSV("dataset_hotels.csv", 1, 2, 0);
        System.out.println("\nA adicionar comentários ao MinHash...");
        System.out.println();
        start = System.nanoTime();
        for (String comment : comments) {
            minHash_comments.computeSignature(comment);
        }
        time7 = System.nanoTime() - start;
        System.out.printf("Número de documentos adicionados (comentários): %d\n", minHash_comments.getDocsNumber());
        System.out.printf("CPU time: %3.2f\n",time7*Math.pow(10,-9));
        System.out.println();

        System.out.println("\nA obter conjuntos similares...");
        start = System.nanoTime();
        int similarities05_comments = minHash_comments.getSimilaritiesAboveThreshold((float) 0.5, print_sims);
        time4 = System.nanoTime() - start;

        System.out.printf("Número de comentários c/ similaricade acima de 50%%: %d\n", similarities05_comments);
        System.out.printf("CPU time: %3.2f\n",time4*Math.pow(10,-9));
        System.out.println("----------------------------------------------------");

        System.out.println("\nA obter conjuntos similares...");
        start = System.nanoTime();
        int similarities08_comments = minHash_comments.getSimilaritiesAboveThreshold((float) 0.8, print_sims);
        time5 = System.nanoTime() - start;

        System.out.printf("Número de comentários c/ similaricade acima de 80%%: %d\n", similarities08_comments);
        System.out.printf("CPU time: %3.2f\n",time5*Math.pow(10,-9));
        System.out.println("----------------------------------------------------");

        System.out.println("A obter comentários similares...");
        start = System.nanoTime();
        int similaritieslsh_comments = minHash_comments.getSimilaritiesAboveThresholdLSH((float) 0.8, print_sims);
        time6 = System.nanoTime() - start;

        System.out.printf("\nNúmero de pares de comentários c/ similaricade acima de 80%% (c/ LSH): %d\n", similaritieslsh_comments);
        System.out.printf("CPU time: %3.2f\n",time6*Math.pow(10,-9));

        System.out.println();

        minHash_comments = null;


        System.out.println("-----------------------------------------------------------------");
        System.out.println("2 - VERIFICAÇÃO DE CONJUNTOS DE HOTEIS SIMILARES");
        System.out.println("-----------------------------------------------------------------");
        System.out.println();

        minHash2 minHash_SetsPlaces = new minHash2();

        System.out.println("A extrair conjuntos de hoteis de utilizadores do csv...");
        Map<String, Set<String>> SetsPlaces = loadSetsPlacesFromCSV("dataset_hotels.csv", 1, 2, 0);

        System.out.println("\nA adicionar conjuntos de hoteis de utilizadores ao MinHash...");

        start = System.nanoTime();
        int places_counter = 0;
        for (Set<String> places : SetsPlaces.values()){
            minHash_SetsPlaces.computeSignature(places);
            places_counter+=places.size();
        }
        time8 = System.nanoTime() - start;

        System.out.printf("\nNúmero de documentos adicionados (conjuntos de hoteis): %d\n", minHash_SetsPlaces.getDocsNumber());
        System.out.printf("CPU time: %3.2f\n",time8*Math.pow(10,-9));
        System.out.println();

        double med_places = (double) places_counter / (double) minHash_SetsPlaces.getDocsNumber();
        System.out.printf("Número de hoteis visitados por pessoa (média): %1.2f\n", med_places);

        System.out.println("\nA obter conjuntos similares...");
        start = System.nanoTime();
        int similarities05_setsPlaces = minHash_SetsPlaces.getSimilaritiesAboveThreshold((float) 0.5, print_sims);
        time1 = System.nanoTime() - start;

        System.out.printf("Número de conjuntos c/ similaricade acima de 50%% (s/ LSH): %d\n", similarities05_setsPlaces);
        System.out.printf("CPU time: %3.2f\n",time1*Math.pow(10,-9));
        System.out.println("----------------------------------------------------");

        System.out.println("A obter conjuntos similares...");
        start = System.nanoTime();
        int similarities08_setsPlaces = minHash_SetsPlaces.getSimilaritiesAboveThreshold((float) 0.8, print_sims);
        time2 = System.nanoTime() - start;

        System.out.printf("Número de conjuntos c/ similaricade acima de 80%% (s/ LSH): %d\n", similarities08_setsPlaces);
        System.out.printf("CPU time: %3.2f\n",time2*Math.pow(10,-9));
        System.out.println("----------------------------------------------------");

        System.out.println("A obter conjuntos similares...");
        start = System.nanoTime();
        int similaritieslsh_setsPlaces = minHash_SetsPlaces.getSimilaritiesAboveThresholdLSH((float) 0.8, print_sims);
        time3 = System.nanoTime() - start;

        System.out.printf("Número de conjuntos c/ similaricade acima de 80%% (c/ LSH): %d\n", similaritieslsh_setsPlaces);
        System.out.printf("CPU time: %3.2f\n",time3*Math.pow(10,-9));
        System.out.println("----------------------------------------------------");


    }
}
