package com.github.psychotherapist.externalsorting;

import com.github.psychotherapist.externalsorting.utils.Utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.PriorityQueue;

public class ArbitraryLengthStringSorter implements StringSorterInterface {
    private final long memoryLimitBytes;
    private final Comparator<String> comparator;

    static ArbitraryLengthStringSorter getInstance(long memoryLimitBytes) {
        Comparator<String> naturalOrder = Comparator.naturalOrder();
        return getInstance(memoryLimitBytes, naturalOrder);
    }

    static ArbitraryLengthStringSorter getInstance(long memoryLimitBytes, Comparator<String> comparator) {
        return new ArbitraryLengthStringSorter(memoryLimitBytes, comparator);
    }

    private ArbitraryLengthStringSorter(long memoryLimitBytes, Comparator<String> comparator) {
        this.memoryLimitBytes = memoryLimitBytes;
        this.comparator = comparator;
    }

    @Override
    public void sortStrings(InputStream is, OutputStream os) throws IOException {
        String[] tempFiles1 = new String[] { "arb.tmp.1", "arb.tmp.2" };
        String[] tempFiles2 = new String[] { "arb.tmp.3", "arb.tmp.4" };

        String[][] tmpFiles = new String[][] {
                new String[] { "arb.tmp.1", "arb.tmp.2" },
                new String[] { "arb.tmp.3", "arb.tmp.4" }
        };

        int inFilesIndex = 0;
        int outFilesIndex = 1;

        doFirstPass(is, tempFiles1);
        String[] inFiles = tempFiles1;
        String[] outFiles = tempFiles2;
        while (doOnePass(tmpFiles[inFilesIndex], tmpFiles[outFilesIndex]) > 1) {
            inFilesIndex = inFilesIndex ^ 1;
            outFilesIndex   = outFilesIndex ^ 1;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(tmpFiles[outFilesIndex][0]));
             PrintWriter pw = new PrintWriter(os))
        {
            String line;
            String lineSeparator = "";
            while ((line = br.readLine()) != null) {
                pw.print(lineSeparator);
                pw.print(line);
                if (lineSeparator.isEmpty()) {
                    lineSeparator = System.lineSeparator();
                }
            }
        }

        for(String[] files : tmpFiles) {
            for (String file : files) {
                Files.delete(Paths.get(file));
            }
        }
    }

    /**
     * Divides original file into two files with sorted sequences of strings
     * of max length that still can be sorted in memory
     * @param is
     * @param outFiles
     */
    private void doFirstPass(InputStream is, String[] outFiles) {
        String tempFile = "temp.tmp";
        String lastReadLine = null;
        int currentOutputIndex = 0;

        try {
            OutputStream[] tempOuts = new OutputStream[2];
            for(int i = 0; i < 2; ++i) {
                tempOuts[i] = Files.newOutputStream(Paths.get(outFiles[i]));
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            lastReadLine = br.readLine();
            while(lastReadLine != null) {
                try (PrintWriter pw = new PrintWriter(tempFile)) {
                   lastReadLine = writeChunkToFile(br, pw, lastReadLine);
                }

                try (InputStream fis = Files.newInputStream(Paths.get(tempFile))) {
                    sortInMemory(fis, tempOuts[currentOutputIndex]);
                    currentOutputIndex ^= 1;
                }
            }

            for(int i = 0; i < 2; ++i) {
                tempOuts[i].close();
            }

            Files.delete(Paths.get(tempFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private long doOnePass(String[] inFiles, String[] outFiles) {
        long sortedSequenceNumber = 0;
        try {
            BufferedReader[] ins = new BufferedReader[] {
                    new BufferedReader(Files.newBufferedReader(Paths.get(inFiles[0]))),
                    new BufferedReader(Files.newBufferedReader(Paths.get(inFiles[1])))
            };

            PrintWriter[] outs = new PrintWriter[] {
                    new PrintWriter(outFiles[0]),
                    new PrintWriter(outFiles[1])
            };

            int currentOutIndex = 0;

            String firstFileLine = ins[0].readLine();
            String secondFileLine = ins[1].readLine();

            while (firstFileLine != null || secondFileLine != null) {
                String firstPrevLine = null;
                String secondPrevLine = null;

                while (Utils.isGrowingSequence(firstPrevLine, firstFileLine, comparator) &&
                        Utils.isGrowingSequence(secondPrevLine, secondFileLine, comparator)) {
                    if (firstFileLine.compareTo(secondFileLine) <= 0) {
                        outs[currentOutIndex].println(firstFileLine);
                        firstPrevLine = firstFileLine;
                        firstFileLine = ins[0].readLine();
                    } else {
                        outs[currentOutIndex].println(secondFileLine);
                        secondPrevLine = secondFileLine;
                        secondFileLine = ins[1].readLine();
                    }
                }

                while(Utils.isGrowingSequence(firstPrevLine, firstFileLine, comparator)) {
                    outs[currentOutIndex].println(firstFileLine);
                    firstPrevLine = firstFileLine;
                    firstFileLine = ins[0].readLine();
                }

                while(Utils.isGrowingSequence(secondPrevLine, secondFileLine, comparator)) {
                    outs[currentOutIndex].println(secondFileLine);
                    secondPrevLine = secondFileLine;
                    secondFileLine = ins[1].readLine();
                }

                currentOutIndex ^= 1;
                sortedSequenceNumber++;
            }

            for(BufferedReader br : ins) {
                br.close();
            }
            for(PrintWriter bw : outs) {
                bw.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sortedSequenceNumber;
    }

    private void sortInMemory(InputStream is, OutputStream os) {
        PriorityQueue<String> heap = new PriorityQueue<>(comparator);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while((line = br.readLine()) != null) {
                heap.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        PrintWriter pw = new PrintWriter(os);
        while(!heap.isEmpty()) {
            pw.println(heap.poll());
        }
        pw.flush();
    }

    private String writeChunkToFile(BufferedReader br, PrintWriter pw, String lastStringRead) throws IOException {
        pw.println(lastStringRead);
        long currentBytesRead = lastStringRead.getBytes().length;

        while((lastStringRead = br.readLine()) != null && currentBytesRead + lastStringRead.getBytes().length < memoryLimitBytes) {
            pw.println(lastStringRead);
            currentBytesRead += lastStringRead.getBytes().length;
        }
        pw.flush();
        return lastStringRead;
    }
}
