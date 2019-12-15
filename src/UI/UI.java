package UI;

import bloomFilter.BloomFilter;
import minHash.minHash2;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class UI {

    public static void main(String[] args) {


        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("-------------");
            System.out.println("INTERFACE DE UTILIZADOR");
            System.out.println("-------------");
            System.out.println("1.) Verificar match USER-HOTEL (Bloom filter)");
            System.out.println("2.) Encontrar similaridades entre comentários (MinHash) (INCLUI UTILIZAÇÃO CONJUNTA MINHASH + BLOOMFILTER");
            System.out.println("3.) Encontrar similaridades entre utilizadoers (MinHash)");
            System.out.println("4.) Sair");
            System.out.println("-------------");
            System.out.println("Insira opção: ");

            int op = sc.nextInt();

            switch (op) {

                case 1:
                    BloomFilterMenu(sc);
                    break;
                case 2:
                    commentsSimilaritiesMenu(sc);
                    break;
                case 3:
                    usersSimilaritiesMenu(sc);
                    break;
                case 4:
                    System.exit(0);
                    break;

                default:
                    System.out.println("\nOpção inválida.");
                    break;
            }
        }

    }


    public static void BloomFilterMenu(Scanner sc) {

        double p;
        int n;
        long start;
        long time;
        System.out.println("A carregar dataset...");
        List<String> dataset = loadCSVtoArray("dataset_hotels.csv", 2, 0);
        n = dataset.size();
        System.out.println("\nInsira a probabilidade máxima de erro aceitável para a existência de falsos positivos:");
        p = sc.nextDouble();
        BloomFilter b = new BloomFilter(p, n);
        System.out.println("A carregar dataset...");

        while (true) {
            String user;
            String hotel;
            String element;
            System.out.println("------------------------");
            System.out.println("VERIFICAR EXISTÊNCIA DE PAR USER-HOTEL");
            System.out.println("------------------------");
            System.out.println("1.) Adicionar par USER-HOTEL ");
            System.out.println("2.) Verificar existência de match USER-HOTEL ");
            System.out.println("3.) Voltar");
            System.out.println("---------------------------");
            System.out.println("-------------");
            System.out.println("Insira opção: ");

            int op = sc.nextInt();

            switch (op) {

                case 3:
                    return;
                case 1:
                    System.out.println("User?");
                    user = sc.nextLine();
                    sc.nextLine();
                    System.out.println("Hotel?");
                    hotel = sc.nextLine();
                    element = user + hotel;
                    b.insert(element.replaceAll("\\s", ""));
                    System.out.println("User-hotel inserido com sucesso.");
                    break;
                case 2:
                    System.out.println("User?");
                    user = sc.nextLine();
                    sc.nextLine();
                    System.out.println("Hotel?");
                    hotel = sc.nextLine();
                    element = user + hotel;
                    boolean isMember = b.isMember(element);
                    if (isMember) {
                        System.out.println("O utilizador inserido comentou o hotel inserido.");
                    } else {
                        System.out.println("O utilizador inserido não comentou o hotel inserido.");
                    }
                    break;
                default:
                    System.out.println("\nOpção inválida.");
                    break;
            }
        }
    }


    public static void commentsSimilaritiesMenu(Scanner sc) {

            long start;
            long time;
            boolean print_sims;
            System.out.println("------------------------");
            System.out.println("VERIFICAÇÃO DE COMENTÁRIOS SIMILARES");
            System.out.println("------------------------");
            System.out.println();
            minHash2 minHash_comments = new minHash2();
            System.out.println("A carregar comentários do csv para o dataset...");
            List<String> comments = loadCommentsFromCSV("dataset_hotels.csv", 1, 2, 0);
            System.out.println("\nA carregar dataset no MinHash...");
            System.out.println();

            start = System.nanoTime();
            for (String comment : comments) {
                minHash_comments.computeSignature(comment);
            }
            comments = null;
            System.out.println("\nComentários adicionados ao MinHash!");
            System.out.println("A carregar users e places do csv para o dataset...");
            List<String> dataset = loadCSVtoArray("dataset_hotels.csv", 2, 0);
            int n = dataset.size();
            System.out.println("\nInsira a probabilidade máxima de erro aceitável para a existência de falsos positivos no Bloom filter:");
            double p = sc.nextDouble();
            BloomFilter b = new BloomFilter(p, n);
            System.out.println("A carregar dataset para o Bloom filter...");

            for (String s : dataset) {
                b.insert(s);
            }

            time = System.nanoTime() - start;
            System.out.printf("Número de documentos adicionados (comentários) ao MinHash: %d\n", minHash_comments.getDocsNumber());
            System.out.printf("Número de documentos adicionados (user-place) ao Bloom filter: %d\n", n);
            System.out.printf("CPU time: %3.2f\n", time * Math.pow(10, -9));
            System.out.println();

        while (true) {

            System.out.println("OPÇÕES DO MIN HASH:");
            System.out.println("1.) Verificar pares de comentarios com similaridade a inserir (s/ LSH) (MUITO LENTO)");
            System.out.println("2.) Verificar pares de comentarios com similaridade >= 80% (c/ LSH) (RÁPIDO)");
            System.out.println("3.) Adicionar um comentário ao Min Hash (MINHASH + BLOOM FILTER)");
            System.out.println("4.) Voltar");
            System.out.println("-------------");
            System.out.println("Insira opção: ");

            int op = sc.nextInt();

            switch (op) {
                case 1:
                    System.out.println("Insira similaridade (0-1):");
                    float sim = sc.nextFloat();
                    sc.nextFloat();
                    System.out.println("Deseja imprimir os pares similares? (1 - true, 0 - false)");
                    print_sims = sc.nextBoolean();
                    sc.nextBoolean();
                    System.out.println("A obter comentários similares...");
                    start = System.nanoTime();
                    int similarities = minHash_comments.getSimilaritiesAboveThreshold(sim, print_sims);
                    time = System.nanoTime() - start;
                    System.out.printf("Número de comentários c/ similaricade acima de %f pc: %d\n", sim, similarities);
                    if (print_sims) {
                        System.out.printf("CPU time (+ impressão dos pares): %3.2f\n", time * Math.pow(10, -9));
                    } else {
                        System.out.printf("CPU time: %3.2f\n", time * Math.pow(10, -9));
                    }
                    break;
                case 2:
                    System.out.println("Deseja imprimir os pares similares? (1 - true, 0 - false)");
                    print_sims = sc.nextBoolean();
                    sc.nextBoolean();
                    System.out.println("A obter comentários similares...");
                    start = System.nanoTime();
                    int similaritieslsh_comments = minHash_comments.getSimilaritiesAboveThresholdLSH((float) 0.8, print_sims);
                    time = System.nanoTime() - start;
                    System.out.printf("\nNúmero de pares de comentários c/ similaricade acima de 80%% (c/ LSH): %d\n", similaritieslsh_comments);
                    System.out.printf("CPU time: %3.2f\n", time * Math.pow(10, -9));
                    System.out.println();

                    break;
                case 3:
                    System.out.println("Insira o USERNAME:");
                    String username = sc.nextLine();
                    sc.nextLine();
                    System.out.println("Insira o HOTEL a comentar:");
                    String hotel = sc.nextLine();
                    System.out.println("Insira o comentário:");
                    String comentario = sc.nextLine();
                    String element = username + hotel;
                    if (b.isMember(element.replaceAll("\\s", ""))) {
                        System.out.println("O USERNAME inserido já comentou este HOTEL!");
                    } else {
                        b.insert(element.replaceAll("\\s", ""));
                        minHash_comments.computeSignature(comentario.replaceAll("\\s", ""));
                        System.out.println("Comentário adicionado com sucesso!");
                    }
                    break;
                case 4:
                    return;
                default:
                    System.out.println("\nOpção inválida.");
                    break;

            }
        }
    }

    public static void usersSimilaritiesMenu(Scanner sc) {

        long start;
        long time;
        boolean print_sims;
        System.out.println("------------------------");
        System.out.println("VERIFICAÇÃO DE CONJUNTOS DE HOTEIS SIMILARES");
        System.out.println("------------------------");
        System.out.println();
        minHash2 minHash_SetsPlaces = new minHash2();
        System.out.println("A extrair conjuntos de hoteis de utilizadores do csv...");
        Map<String, Set<String>> SetsPlaces = loadSetsPlacesFromCSV("dataset_hotels.csv", 1, 2, 0);
        System.out.println("\nA adicionar conjuntos de hoteis de utilizadores ao MinHash...");
        start = System.nanoTime();
        int places_counter = 0;
        for (Set<String> places : SetsPlaces.values()) {
            minHash_SetsPlaces.computeSignature(places);
            places_counter += places.size();
        }
        time = System.nanoTime() - start;
        System.out.printf("Número de documentos adicionados (conjuntos de hoteis): %d\n", minHash_SetsPlaces.getDocsNumber());
        System.out.printf("CPU time: %3.2f\n", time * Math.pow(10, -9));
        System.out.println();

        while (true) {

            System.out.println("SELECIONE TIPO DE VERIFICAÇÃO:");
            System.out.println("1.) Conjuntos de hoteis com similaridade a inserir (s/ LSH) (LENTO)");
            System.out.println("2.) Conjuntos de hoteis com similaridade >= 80% (c/ LSH) (RÁPIDO)");
            System.out.println("3.) Voltar");
            System.out.println("-------------");
            System.out.println("Insira opção: ");

            int op = sc.nextInt();

            switch (op) {
                case 1:
                    System.out.println("Insira similaridade (0-1):");
                    float sim = sc.nextFloat();
                    System.out.println("Deseja imprimir os ids dos conjuntos similares? (1 - true, 0 - false)");
                    print_sims = sc.nextBoolean();
                    System.out.println("A obter pares de conjuntos de hoteis similares...");
                    start = System.nanoTime();
                    int similarities = minHash_SetsPlaces.getSimilaritiesAboveThreshold(sim, print_sims);
                    time = System.nanoTime() - start;
                    System.out.printf("Número de pares conjuntos de hoteis c/ similaricade acima de %3.2f pc: %d\n", sim, similarities);
                    if (print_sims) {
                        System.out.printf("CPU time (+ impressão dos pares conjuntos): %3.2f\n", time * Math.pow(10, -9));
                    } else {
                        System.out.printf("CPU time: %3.2f\n", time * Math.pow(10, -9));
                    }
                    break;
                case 2:
                    System.out.println("Deseja imprimir os ids dos conjuntos similares? (1 - true, 0 - false)");
                    print_sims = sc.nextBoolean();
                    System.out.println("A obter pares de conjuntos de hoteis similares...");
                    start = System.nanoTime();
                    int similaritieslsh_comments = minHash_SetsPlaces.getSimilaritiesAboveThresholdLSH((float) 0.8, print_sims);
                    time = System.nanoTime() - start;
                    if (print_sims) {
                        System.out.printf("CPU time (+ impressão dos pares conjuntos): %3.2f\n", time * Math.pow(10, -9));
                    } else {
                        System.out.printf("CPU time: %3.2f\n", time * Math.pow(10, -9));
                    }

                    break;
                case 3:
                    return;
                default:
                    System.out.println("\nOpção inválida.");
                    break;

            }
        }
    }

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
}
