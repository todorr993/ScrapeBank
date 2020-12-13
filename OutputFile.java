package com.company;

import org.apache.commons.io.filefilter.IOFileFilter;
import org.eclipse.jetty.util.IO;

import java.io.IOException;
import java.util.List;

public interface OutputFile {
    public void createFile() throws IOException;
    public void writeToFile(List<String> list) throws IOException;
    public void writeToFile(String string) throws IOException;
    public void closeConnection() throws IOException;
}
