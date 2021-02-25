package eci.arsw.covidanalyzer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A Camel Application
 */
public class CovidAnalyzerTool {

    private ResultAnalyzer resultAnalyzer;
    private TestReader testReader;
    private int amountOfFilesTotal;
    private AtomicInteger amountOfFilesProcessed;
    private final int NUMBER_THREADS = 5;
    private static List<ThreadCovidAnalyzer> threads = new ArrayList<>();

    public CovidAnalyzerTool() {
        resultAnalyzer = new ResultAnalyzer();
        testReader = new TestReader();
        amountOfFilesProcessed = new AtomicInteger();
    }

    public void processResultData() {
        amountOfFilesProcessed.set(0);
        List<File> resultFiles = getResultFileList();
        amountOfFilesTotal = resultFiles.size();
        int fraction;
        for (int i = 0; i < NUMBER_THREADS; i++) {
            fraction = ((amountOfFilesTotal / NUMBER_THREADS) * i);
            if (i == NUMBER_THREADS - 1) {
                threads.add(new ThreadCovidAnalyzer(amountOfFilesProcessed, resultFiles, testReader, resultAnalyzer,
                        fraction, amountOfFilesTotal));
            } else {
                threads.add(new ThreadCovidAnalyzer(amountOfFilesProcessed, resultFiles, testReader, resultAnalyzer,
                        fraction, fraction + (amountOfFilesTotal / NUMBER_THREADS)));
            }
        }

        for (ThreadCovidAnalyzer thread : threads) {
            thread.start();
        }

        /*
         * for (File resultFile : resultFiles) { List<Result> results =
         * testReader.readResultsFromFile(resultFile); for (Result result : results) {
         * resultAnalyzer.addResult(result); } amountOfFilesProcessed.incrementAndGet();
         * }
         */
    }

    private List<File> getResultFileList() {
        List<File> csvFiles = new ArrayList<>();
        try (Stream<Path> csvFilePaths = Files.walk(Paths.get("src/main/resources/"))
                .filter(path -> path.getFileName().toString().endsWith(".csv"))) {
            csvFiles = csvFilePaths.map(Path::toFile).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return csvFiles;
    }

    public Set<Result> getPositivePeople() {
        return resultAnalyzer.listOfPositivePeople();
    }

    public static void showMessage( CovidAnalyzerTool covidAnalyzerTool){
        String message = "Processed %d out of %d files.\nFound %d positive people:\n%s";
        Set<Result> positivePeople = covidAnalyzerTool.getPositivePeople();
        String affectedPeople = positivePeople.stream().map(Result::toString).reduce("", (s1, s2) -> s1 + "\n" + s2);
        message = String.format(message, covidAnalyzerTool.amountOfFilesProcessed.get(), covidAnalyzerTool.amountOfFilesTotal, positivePeople.size(), affectedPeople);
        System.out.println(message);
    }

    /**
     * A main() so we can easily run these routing rules in our IDE
     */
    public static void main(String... args) throws Exception {
        CovidAnalyzerTool covidAnalyzerTool = new CovidAnalyzerTool();
        Thread processingThread = new Thread(() -> covidAnalyzerTool.processResultData());
        processingThread.start();
        while (true) {
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            if (line.contains("exit")) {
                break;
            }
            if (line.isEmpty()) {
                for (ThreadCovidAnalyzer thread : threads) {
                    if(thread.getRun()){
                        System.out.println("Thread STOP!");   
                    }
                    else {
                        System.out.println("Thread RUN!");
                    }
                    thread.setRun(!thread.getRun());
                }
                if(!threads.get(0).getRun()){
                    showMessage(covidAnalyzerTool);
                }
            }
            
        }
    }

}

