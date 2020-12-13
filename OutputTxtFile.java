package com.company;


import java.io.*;
import java.nio.file.Path;
import java.util.List;

public class OutputTxtFile implements OutputFile {
    File outputFile;
    FileWriter fileWriter;

    public OutputTxtFile(Path path, String name){
        outputFile=new File(path+File.separator+name+".txt");
    }

    public void createFile(){
        try{
            fileWriter=new FileWriter(outputFile);
        }catch (IOException e){
            System.out.println("error while creating CSV file");
        }
    }

    public void writeToFile(List<String> list){
        int size = list.size();
        int i=0;
        try {
            for (String el : list) {
                fileWriter.append(el);
                i++;
                if(size!=i)
                    fileWriter.append(',');
            }

        }catch (IOException e)
        {
            System.out.println("error while writing to CSV file");
        }

    }

    public void writeToFile(String string)  {
        try {
            fileWriter.append(string);
        }catch (IOException e)
        {
            System.out.println("error while writing to CSV file");
        }
    }

    public void closeConnection(){
        try{
            fileWriter.flush();
            fileWriter.close();
        }catch (IOException e)
        {
            System.out.println("error while writing to CSV file");
        }
    }
}
