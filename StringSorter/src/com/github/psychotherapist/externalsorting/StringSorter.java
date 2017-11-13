package com.github.psychotherapist.externalsorting;

import com.sun.istack.internal.Nullable;

import java.io.*;

public class StringSorter {
    private static final int MEM_LIMIT_MB = 100;

    public static void main(String[] args) {
        if (args.length < 1 || args.length > 2) {
            echoHelp();
            return;
        }

        //TODO Parse args proprly
        //TODO Add stdin support
        String inputFile;
        inputFile = args[0];

        String outputFilePath = null;
        if (args.length > 1) {
           outputFilePath = args[1];
        }

        sortStringsInFile(inputFile, outputFilePath, MEM_LIMIT_MB);
    }

    public static void sortStringsInFile(String inputFilePath, @Nullable String outFilePath, int memoryLimit) {
        try (InputStream is = new FileInputStream(inputFilePath);
             OutputStream os = outFilePath == null ? System.out : new FileOutputStream(outFilePath))
        {
            FixedLengthStringSorter sorter = FixedLengthStringSorter.getInstance(
                                                getLineLength(inputFilePath),
                                                memoryLimit);
            sorter.sortStrings(is, os == null ? System.out : os);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int getLineLength(String inputFilePath) throws IOException {
        int result;
        try (BufferedReader br = new BufferedReader(new FileReader(inputFilePath)))
        {
            String line = br.readLine();
            result = line.getBytes().length;
            br.close();
        }
        return result;
    }


    private static void echoHelp() {
        System.out.println("Error, invalid arguments");
        System.out.println("Usage: StringSorter inFilePath [ outFilePath ] ");
        System.out.println("Default out: stdout");
    }
}
