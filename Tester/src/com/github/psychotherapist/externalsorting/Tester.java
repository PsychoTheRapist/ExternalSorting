package com.github.psychotherapist.externalsorting;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.Scanner;

public class Tester {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int linesNumber = in.nextInt();
        int lineLength = in.nextInt();

        String generatedFileName = "generated_strings";
        String sortedFileName = "sorted_strings";

        try {
            FileGenerator.generateStrings(linesNumber, lineLength, generatedFileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int memLimit = 100;

        Comparator<String> naturalOrder = Comparator.naturalOrder();
        StringSorterInterface sorter = ArbitraryLengthStringSorter.getInstance(memLimit);
        StringSorter.sortStringsInFile(generatedFileName, sortedFileName, sorter);
    }
}
