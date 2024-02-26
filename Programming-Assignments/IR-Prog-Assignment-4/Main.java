package com.ovgu.irproject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) throws IOException, ParseException {
        //DocumentPreProcessing progAssig1 = new DocumentPreProcessing();
        //progAssig1.executor();
        //MyIndexer progAssig2 = new MyIndexer();
        //progAssig2.executor();
        //IRAssignmentP03 progAssig3 = new IRAssignmentP03();
        //progAssig3.executor();

        IRAssignment04 progAssig4 = new IRAssignment04();
        progAssig4.executor();
        }

}