import java.io.*;
import java.util.*;

public class WordDocumentMatrixConstruction {

    public static void main(String[] args) {
        String directoryPath = "C:/Users/MM/Downloads/data"; //เป็นที่อยู่ของไฟล์ data ครับ ผมไม่แน่ใจว่าถ้าอาจารย์โหลดไป  ไฟล์ data จะไปอยู่ที่ไหน เลย keep code ไว้แบบนี้ครับ

        List<String> fileContents = readFiles(directoryPath);
        List<List<String>> tokenizedDocuments = tokenizeDocuments(fileContents);
        Map<String, Map<String, Integer>> wordDocumentMatrix = constructMatrix(tokenizedDocuments);

        exportMatrixToCSV(wordDocumentMatrix, directoryPath);
    }

    public static List<String> readFiles(String directoryPath) {
        List<String> fileContents = new ArrayList<>();
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            stringBuilder.append(line).append("\n");
                        }
                        fileContents.add(stringBuilder.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return fileContents;
    }

    public static List<List<String>> tokenizeDocuments(List<String> fileContents) {
        List<List<String>> tokenizedDocuments = new ArrayList<>();
        for (String content : fileContents) {
            List<String> tokens = Arrays.asList(content.split("\\s+"));
            List<String> normalizedTokens = new ArrayList<>();
            for (String token : tokens) {
                String normalizedToken = token.toLowerCase().replaceAll("[^a-zA-Z]", "");
                if (!normalizedToken.isEmpty()) {
                    normalizedTokens.add(normalizedToken);
                }
            }
            tokenizedDocuments.add(normalizedTokens);
        }
        return tokenizedDocuments;
    }

    public static Map<String, Map<String, Integer>> constructMatrix(List<List<String>> tokenizedDocuments) {
        Map<String, Map<String, Integer>> wordDocumentMatrix = new HashMap<>();
        for (int i = 0; i < tokenizedDocuments.size(); i++) {
            String documentId = "Document" + (i + 1);
            Map<String, Integer> wordFrequency = new HashMap<>();
            for (String word : tokenizedDocuments.get(i)) {
                wordFrequency.put(word, wordFrequency.getOrDefault(word, 0) + 1);
            }
            wordDocumentMatrix.put(documentId, wordFrequency);
        }
        return wordDocumentMatrix;
    }

    public static void exportMatrixToCSV(Map<String, Map<String, Integer>> wordDocumentMatrix, String directoryPath) {
        Set<String> uniqueWords = getUniqueWords(wordDocumentMatrix);

        List<String> sortedWords = new ArrayList<>(uniqueWords);
        Collections.sort(sortedWords);

        try (PrintWriter writer = new PrintWriter(new File(directoryPath + "/output.csv"))) {
            writer.print("Word");
            for (String documentId : wordDocumentMatrix.keySet()) {
                writer.print("," + documentId);
            }
            writer.println();

            for (String word : sortedWords) {
                writer.print(word);
                for (Map<String, Integer> wordFrequency : wordDocumentMatrix.values()) {
                    writer.print("," + wordFrequency.getOrDefault(word, 0));
                }
                writer.println();
            }

            System.out.println("Word-document matrix exported to CSV file: " + directoryPath + "/output.csv");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Set<String> getUniqueWords(Map<String, Map<String, Integer>> wordDocumentMatrix) {
        Set<String> uniqueWords = new HashSet<>();
        for (Map<String, Integer> wordFrequency : wordDocumentMatrix.values()) {
            uniqueWords.addAll(wordFrequency.keySet());
        }
        return uniqueWords;
    }
}
