package com.github.psychotherapist.externalsorting.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTestWithNaturalOrder {
    @Test
    void testIsGrowingSequenceWithNull() {
        assertTrue(Utils.isGrowingSequence(null, "test"));
        assertTrue(Utils.isGrowingSequence(null, ""));
        assertFalse(Utils.isGrowingSequence("test", null));
        assertFalse(Utils.isGrowingSequence("", null));
        assertFalse(Utils.isGrowingSequence(null, null));
    }

    @Test
    void testIsGrowingSequenceWithoutNull() {
        assertTrue(Utils.isGrowingSequence("test", "test"));
        assertTrue(Utils.isGrowingSequence("Test", "test"));
        assertTrue(Utils.isGrowingSequence("", "test"));
        assertFalse(Utils.isGrowingSequence("test", "Test"));
        assertFalse(Utils.isGrowingSequence("test", ""));
    }
}