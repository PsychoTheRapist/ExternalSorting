package com.github.psychotherapist.externalsorting;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class FileGeneratorTest {
    @Test
    void testLinesCountAndLength() {
        int linesToWrite = 1000;
        int lineLength = 99;
        String tmpFile = "testLinesCountAndLength.tmp";

        try {
            Path tempFilePath = Paths.get(tmpFile);
            Files.deleteIfExists(tempFilePath);
            FileGenerator.generateStrings(linesToWrite, lineLength, tmpFile);
            assertTrue(Files.exists(tempFilePath));
            String line;
            int linesRead = 0;
            BufferedReader reader = Files.newBufferedReader(tempFilePath);
            while((line = reader.readLine()) != null) {
                assertEquals(lineLength, line.length());
                linesRead++;
            }
            assertEquals(linesToWrite, linesRead);
            Files.delete(tempFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}