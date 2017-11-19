package com.github.psychotherapist.externalsorting;

import com.sun.istack.internal.Nullable;

import java.io.*;
import java.util.Comparator;

public class StringSorter {
    public static void main(String[] args) {
        if (args.length != 3) {
            echoHelp();
            return;
        }

        String inputFile;
        inputFile = args[0];
        String outputFilePath = args[1];
        long memoryLimitBytes = Long.parseLong(args[2]);

        StringSorterInterface sorter = ArbitraryLengthStringSorter.getInstance(memoryLimitBytes);
        sortStringsInFile(inputFile, outputFilePath, sorter);
    }

    public static void sortStringsInFile(String inputFilePath, String outFilePath, StringSorterInterface sorter) {
        try (InputStream is = new FileInputStream(inputFilePath);
             OutputStream os = new FileOutputStream(outFilePath))
        {
            sorter.sortStrings(is, os);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void echoHelp() {
        System.out.println("Error, invalid arguments");
        System.out.println("Usage: StringSorter inFilePath outFilePath");
    }
}
