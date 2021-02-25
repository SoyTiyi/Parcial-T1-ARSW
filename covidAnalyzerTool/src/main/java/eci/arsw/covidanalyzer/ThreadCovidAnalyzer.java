package eci.arsw.covidanalyzer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadCovidAnalyzer extends Thread {

    private boolean run;
    private AtomicInteger amountOfFilesProcessed;
    private List<File> resultFiles;
    private TestReader testReader;
    private ResultAnalyzer resultAnalyzer;
    private int first;
    private int last;

    public ThreadCovidAnalyzer(AtomicInteger amountOfFilesProcessed, List<File> resultFiles, TestReader testReader,
            ResultAnalyzer resultAnalyzer, int first, int last) {
        this.amountOfFilesProcessed = amountOfFilesProcessed;
        this.resultFiles = resultFiles;
        this.testReader = testReader;
        this.resultAnalyzer = resultAnalyzer;
        this.first = first;
        this.last = last;
        this.run = true;
    }

    @Override
    public void run() {
        for (int i = first; i < last; i++) {
            List<Result> results = testReader.readResultsFromFile(resultFiles.get(i));
            for (Result result : results) {
                synchronized (this) {
                    while (!run) {
                        try {
                            this.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                resultAnalyzer.addResult(result);
            }
            amountOfFilesProcessed.incrementAndGet();
        }
    }

    public synchronized void pauseState(boolean run){
        this.run = run;
        if(run){
            this.notifyAll();
        }
    }

    public boolean getRun(){
        return this.run;
    }

}
