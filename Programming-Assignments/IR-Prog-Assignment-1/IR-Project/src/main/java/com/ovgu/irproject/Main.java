package com.ovgu.irproject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Do you want to enter your own input? [Y/N]");
        Scanner sc = new Scanner(System.in);
        String mode = sc.next();
        String input = "";
        if (mode.equalsIgnoreCase("y") ||  mode.equalsIgnoreCase("yes")) {
            System.out.println("Please enter a string input for document pre-processing");
            Scanner scNew = new Scanner(System.in);
            String temp;
            while (!(temp = scNew.nextLine()).equals("")) {
                input = input + "\n" + temp;
            }
        } else {
            System.out.println("Proceeding with default input document");
            InputStream inputDoc = Main.class.getClassLoader().getResourceAsStream("input.txt");
            input = IOUtils.toString(inputDoc, StandardCharsets.UTF_8);
        }
        System.out.println("Input document : " + input);
        DocumentPreProcessing docPreProcessing = new DocumentPreProcessing();
        List<String> standardOutput = docPreProcessing.StdTokenizer(input, Boolean.FALSE, Boolean.FALSE);
        List<String> standardStopWordCaseNotIgnored = docPreProcessing.StdTokenizer(input, Boolean.TRUE, Boolean.FALSE);
        List<String> standardStopWordIgnoreCase = docPreProcessing.StdTokenizer(input, Boolean.TRUE, Boolean.TRUE);
        List<String> whitespaceOutput = docPreProcessing.wsTokenizer(input);
        List<String> docAnalyzerOutput = docPreProcessing.docAnalyzer(input);
        System.out.println("Standard tokenizer output: " + standardOutput);
        System.out.println("Standard tokenizer with stop word filter and ignoreCase=false (case sensitive): " + standardStopWordCaseNotIgnored);
        System.out.println("Standard tokenizer with stop word filter and ignoreCase=true: " + standardStopWordIgnoreCase);
        System.out.println("Whitespace tokenizer output : " + whitespaceOutput);
        System.out.println("Analyser output : " + docAnalyzerOutput);
    }
}