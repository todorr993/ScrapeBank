package com.company;



import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {


        Scanner scanner=new Scanner(System.in);
        System.out.println("Please provide output directory or enter 'no' and output files will be created in project directory");
        String outputPath= scanner.nextLine();


        if (outputPath.equalsIgnoreCase("no"))
        {
            outputPath=Paths.get("").toAbsolutePath().toString()+"\\OutputFiles";
            try {
                Files.createDirectories(Paths.get(outputPath));
            }catch (IOException e)
            {
                System.out.println("error while creating directory");
            }
        }


        WebScrapeManager manager = new WebScrapeManager(outputPath);
        manager.webScrape();

        }
    }


