package com.github.psychotherapist.externalsorting;

import com.github.psychotherapist.externalsorting.utils.Utils;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

class StringSorterTest {
    @Test
    void testStringsSortingInFile() {
        int linesNumber  = 100;
        int lineLength = 50;
        long memoryLimitBytes = 1024;

        String generatedFile = "generated.tmp";
        String sortedFile = "sorted.tmp";
        try {
            Path inFilePath = Paths.get(generatedFile);
            Path outFilePath = Paths.get(sortedFile);

            Files.deleteIfExists(inFilePath);
            FileGenerator.generateStrings(linesNumber, lineLength, generatedFile);

            Files.deleteIfExists(Paths.get(sortedFile));

            Comparator<String> naturalOrder = Comparator.naturalOrder();

            StringSorterInterface sorter = ArbitraryLengthStringSorter.getInstance(memoryLimitBytes, naturalOrder);
            StringSorter.sortStringsInFile(generatedFile, sortedFile, sorter);

            assertTrue(Files.exists(outFilePath));

            assertTrue(allStringsFromInExistInOut(inFilePath, outFilePath));

            assertTrue(orderingIsCorrect(outFilePath, naturalOrder));

            Comparator<String> reversedOrder = Comparator.reverseOrder();

            assertFalse(orderingIsCorrect(outFilePath, reversedOrder));

            Files.delete(inFilePath);
            Files.delete(outFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean allStringsFromInExistInOut(Path inFilePath, Path outFilePath) throws IOException {
        try (BufferedReader readerIn = Files.newBufferedReader(inFilePath)) {
            String lineReadFromIn;
            boolean readLinesFound = true;
            while(readLinesFound && (lineReadFromIn = readerIn.readLine()) != null) {
                try (BufferedReader readerOut = Files.newBufferedReader(outFilePath)) {
                    String lineReadFromOut;
                    boolean currentLineFound = false;
                    while (!currentLineFound && (lineReadFromOut = readerOut.readLine()) != null) {
                        if (lineReadFromIn.equals(lineReadFromOut)) {
                            currentLineFound = true;
                        }
                    }
                    readLinesFound = currentLineFound;
                }
            }
            return readLinesFound;
        }
    }

    private boolean orderingIsCorrect(Path filePath, Comparator<String> comparator) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String previousLine = null;
            String currentLine;
            while((currentLine = reader.readLine()) != null) {
                if (!Utils.isGrowingSequence(previousLine, currentLine, comparator)) {
                    return false;
                }
                previousLine = currentLine;
            }
            return true;
        }
    }
}