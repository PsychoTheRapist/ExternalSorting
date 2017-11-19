package com.github.psychotherapist.externalsorting;

import com.sun.istack.internal.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface StringSorterInterface {
    void sortStrings(InputStream is, OutputStream os) throws IOException;
}
