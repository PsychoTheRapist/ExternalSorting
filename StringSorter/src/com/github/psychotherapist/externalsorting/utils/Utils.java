package com.github.psychotherapist.externalsorting.utils;

import com.sun.istack.internal.Nullable;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;

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
        Comparator<String> naturalOrder = Comparator.naturalOrder();
        return isGrowingSequence(prev, current, naturalOrder);
    }

    public static boolean isGrowingSequence(String prev, String current, Comparator<String> comparator) {
        return current != null && (prev == null || comparator.compare(current, prev) >= 0);
    }
}
