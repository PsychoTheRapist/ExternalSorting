package com.github.psychotherapist.externalsorting;

import java.io.*;
import java.util.PriorityQueue;

class FixedLengthStringSorter implements StringSorterInterface {
    private final static int BYTES_IN_MB = 1024;

    private final int maxLinesInMemory;

    private FixedLengthStringSorter(long stringBytesLength, long memoryLimitMB) {
        //this.maxLinesInMemory = (int) (memoryLimitMB * BYTES_IN_MB / stringBytesLength);
        maxLinesInMemory = 3;
    }

    static FixedLengthStringSorter getInstance(int stringBytesLength, int memoryLimitMB) {
        return new FixedLengthStringSorter(stringBytesLength, memoryLimitMB);
    }

    /**
     * Reads lines from {@code is}, sorts them and writes the result to {@code os}.
     * Implements basic two-way external sorting
     *
     * @param is
     * @param os
     * @throws IOException
     */
    @Override
    public void sortStrings(InputStream is, OutputStream os) throws IOException {
        String[] tempFiles1 = new String[] { "temp1", "temp2" };
        String[] tempFiles2 = new String[] { "temp3", "temp4" };

        int stringsTotal = doFirstPass(is, tempFiles1);

        int sortedSequenceLength = maxLinesInMemory;
        String[] inFiles = tempFiles1;
        String[] outFiles = tempFiles2;

        while(sortedSequenceLength < stringsTotal) {
            doOnePass(inFiles, outFiles, sortedSequenceLength);
            inFiles = inFiles == tempFiles1 ? tempFiles2 : tempFiles1;
            outFiles = outFiles == tempFiles1 ? tempFiles2 : tempFiles1;
            sortedSequenceLength *= 2;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(inFiles[0]));
            PrintWriter pw = new PrintWriter(os))
        {
            String line;
            while ((line = br.readLine()) != null) {
                pw.println(line);
            }
        }
    }

    /***
     * Reads {@code maxLinesInMemory} lines from {@code is}
     * sorts them and writes to two files in {@code outFiles}
     * @param is
     * @param outFiles
     * @return
     * @throws IOException
     */
    private int doFirstPass(InputStream is, String[] outFiles) throws IOException {
        LineNumberReader reader = new LineNumberReader(new InputStreamReader(is));
        int stringsTotal = 0;
        try (PrintWriter fw1 = new PrintWriter(new FileWriter(outFiles[0]));
             PrintWriter fw2 = new PrintWriter(new FileWriter(outFiles[1]))) {

            String line;
            PriorityQueue<String> buffer = new PriorityQueue<>(maxLinesInMemory);
            PrintWriter out = fw1;

            while ((line = reader.readLine()) != null) {
                stringsTotal++;
                buffer.add(line);
                if (reader.getLineNumber() % maxLinesInMemory == 0) {
                    while (!buffer.isEmpty()) {
                        out.println(buffer.poll());
                    }
                    out = out == fw1 ? fw2 : fw1;
                }
            }
            while (!buffer.isEmpty()) {
                out.println(buffer.poll());
            }

            fw1.close();
            fw2.close();
        }
        return stringsTotal;
    }

    /**
     * Merges two files from {@code inFiles} containing sorted sequences of length {@code sortedSequenceLength}
     * to new files containg sorted sequences with doubled length
     * @param inFiles
     * @param outFiles
     * @param sortedSequenceLength
     */
    private void doOnePass(String[] inFiles, String[] outFiles, int sortedSequenceLength) {
        try {
            BufferedReader[] ins = new BufferedReader[] {
                    new BufferedReader(new FileReader(inFiles[0])),
                    new BufferedReader(new FileReader(inFiles[1]))
            };

            PrintWriter[] outs = new PrintWriter[] {
                    new PrintWriter(new FileWriter(outFiles[0])),
                    new PrintWriter(new FileWriter(outFiles[1]))
            };

            int currentOutIndex = 0;


            String firstFileLine = ins[0].readLine();
            String secondFileLine = ins[1].readLine();

            while (firstFileLine != null) {
                int firstFileLinesRead = 0;
                int secondFileLinesRead = 0;

                while(firstFileLinesRead < sortedSequenceLength &&
                        secondFileLinesRead < sortedSequenceLength) {
                    if (firstFileLine != null && (secondFileLine == null ||
                                                    firstFileLine.compareTo(secondFileLine) <= 0)) {
                        outs[currentOutIndex].println(firstFileLine);
                        firstFileLine = ins[0].readLine();
                        firstFileLinesRead++;
                    } else if (secondFileLine != null) {
                        outs[currentOutIndex].println(secondFileLine);
                        secondFileLine = ins[1].readLine();
                        secondFileLinesRead++;
                    }
                    if (secondFileLine == null) {
                        secondFileLinesRead = sortedSequenceLength;
                    }
                    if (firstFileLine == null) {
                        firstFileLinesRead = sortedSequenceLength;
                    }
                }

                while (firstFileLinesRead < sortedSequenceLength) {
                    if (firstFileLine != null) {
                        outs[currentOutIndex].println(firstFileLine);
                        firstFileLinesRead++;
                    } else {
                        firstFileLinesRead = sortedSequenceLength;
                    }
                    firstFileLine = ins[0].readLine();
                }

                while (secondFileLinesRead < sortedSequenceLength) {
                    if (secondFileLine != null) {
                        outs[currentOutIndex].println(secondFileLine);
                        secondFileLinesRead++;
                    } else {
                        secondFileLinesRead = sortedSequenceLength;
                    }
                    secondFileLine = ins[1].readLine();
                }

                currentOutIndex = (currentOutIndex + 1) % 2;
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
    }
}
