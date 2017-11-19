package com.github.psychotherapist.externalsorting;

import com.sun.istack.internal.Nullable;

import java.io.*;

public class FileGenerator {
    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            echoHelp();
            return;
        }
        int linesNumber = Integer.parseInt(args[0]);
        int lineLength = Integer.parseInt(args[1]);
        String outFileName = args[2];

        generateStrings(linesNumber, lineLength, outFileName);

    }

    public static void generateStrings(int linesNumber, int lineLength, String outFilePath) throws FileNotFoundException {
        StringGeneratorInterface lineGen = FixedLengthRandomStringGenerator.getInstance(lineLength);
        String lineSeparator = "";
        try (PrintWriter pw = new PrintWriter(outFilePath)) {
            for (int i = 0; i < linesNumber; ++i) {
                pw.print(lineSeparator);
                pw.print(lineGen.getLine());
                if (lineSeparator.isEmpty()) {
                    lineSeparator = System.lineSeparator();
                }
            }
        }
    }

    private static void echoHelp() {
        System.out.println("Error, invalid arguments");
        System.out.println("Usage: FileGenerator linesNumber lineLength outFilePath");
    }
}