package com.github.psychotherapist.externalsorting.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Utils {
    public static int getLineLength(String inputFilePath) throws IOException {
        int result;
        try (BufferedReader br = new BufferedReader(new FileReader(inputFilePath)))
        {
            String line = br.readLine();
            result = line.getBytes().length;
            br.close();
        }
        return result;
    }

    public static boolean isGrowingSequence(String prev, String current) {
        return current != null && (prev == null || current.compareTo(prev) >= 0);
    }
}
