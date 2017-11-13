package com.github.psychotherapist.externalsorting;

import com.sun.istack.internal.Nullable;

import java.io.*;

public class StringGenerator {
    public static void main(String[] args) throws IOException {
        if (args.length < 2 || 3 < args.length) {
            echoHelp();
            return;
        }
        //TODO parse arguments properly
        int linesNumber = Integer.parseInt(args[0]);
        int lineLength = Integer.parseInt(args[1]);

        //TODO handle the case if file exists

        OutputStream os = null;
        try {
            if (args.length > 2) {
                os = new FileOutputStream(new File(args[2]));
            }
        } catch (FileNotFoundException | SecurityException e) {
            e.printStackTrace();
        }

        generateFile(linesNumber, lineLength, os);

        if (os != null) {
            os.close();
        }
    }

    public static void generateFile(int linesNumber, int lineLength, @Nullable OutputStream os) {
        //TODO Init properly with line length
        StringGeneratorInterface lineGen = FixedLengthRandomStringGenerator.getInstance(lineLength);
        if (os == null) {
            os = System.out;
        }
        PrintWriter pw = new PrintWriter(os);
        for (int i = 0; i < linesNumber - 1; ++i) {
            pw.println(lineGen.getLine());
        }
        pw.print(lineGen.getLine());
        pw.flush();
    }

    private static void echoHelp() {
        System.out.println("Error, invalid arguments");
        System.out.println("Usage: StringGenerator linesNumber lineLength [ outFilePath ]");
        System.out.println("Default out: stdout");
    }
}