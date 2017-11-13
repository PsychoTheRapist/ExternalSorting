package com.github.psychotherapist.externalsorting;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class Tester {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int linesNumber = in.nextInt();
        int lineLength = in.nextInt();

        String generatedFileName = "generated";

        try (FileOutputStream fos = new FileOutputStream(generatedFileName))
        {
            StringGenerator.generateFile(linesNumber, lineLength, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        StringSorter.sortStringsInFile(generatedFileName, null, 100);
    }
}
