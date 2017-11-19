package com.github.psychotherapist.externalsorting;

import java.util.Random;

class FixedLengthRandomStringGenerator implements StringGeneratorInterface {
    private final static char defaultSeparator = ' ';

    private final static String defaultSymbols;
    static {
        String latinLetters = "abcdefghijklmnopqrstuvwxy";
        String cyrillicLetters = "фбвгдеёжзийклмнопрстуфхцчшщьыъэюя";
        defaultSymbols = latinLetters + latinLetters.toUpperCase() + cyrillicLetters + cyrillicLetters.toUpperCase();
    }

    private final int lineLength;
    private final String symbolsSet;

    private final Random random;

    private FixedLengthRandomStringGenerator(int lineLength, String symbolsSet) {
        this.lineLength = lineLength;
        this.symbolsSet = symbolsSet;
        this.random = new Random();
    }

    static FixedLengthRandomStringGenerator getInstance(int lineLength) {
        return getInstance(lineLength, defaultSymbols);
    }

    static FixedLengthRandomStringGenerator getInstance(int lineLength, String symbolsSet) {
        return new FixedLengthRandomStringGenerator(lineLength, symbolsSet);
    }

    @Override
    public String getLine() {
        StringBuilder line = new StringBuilder();
        while (line.length() < lineLength) {
            int symbolsLeft = lineLength - line.length();
            int nextWordLength = random.nextInt(symbolsLeft) + 1;
            line.append(getWord(nextWordLength));
            if (line.length() < lineLength) {
                line.append(defaultSeparator);
            }
        }
        return line.toString();
    }

    private String getWord(int length) {
        StringBuilder word = new StringBuilder(length);
        for(int i = 0; i < length; ++i) {
            char nextChar = symbolsSet.charAt(random.nextInt(symbolsSet.length()));
            word.append(nextChar);
        }
        return word.toString();
    }
}
