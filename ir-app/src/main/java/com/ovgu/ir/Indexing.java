package com.ovgu.ir;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

public class Indexing {

    public static void indexing(String s1, String s2) throws IOException {
        String pathRead = s2;
        String pathWrite = s1;
        int docnum = 0;
        FieldType Main=new FieldType();
        FieldType Topic=new FieldType();

        File output = new File(pathWrite);
        File[] outputFiles = output.listFiles();
        for(File file: outputFiles)
            file.delete();

        Analyzer analyzer = new EnglishAnalyzer();
        Similarity simType = new BM25Similarity();
        Directory index = FSDirectory.open(Paths.get(pathWrite)); //makes a new directory in the ram for storing the index
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setSimilarity(simType);
        IndexWriter writer = new IndexWriter(index, config);
        File input = new File(pathRead);
        File[] inputFiles = input.listFiles();
        String docMain="";
        String docTitel="";
        for (File file : inputFiles) {
            docMain="";
            docTitel="";
            FileReader fileReader = new FileReader(file);
            BufferedReader br = new BufferedReader(fileReader);
            StringBuilder sb = new StringBuilder();


            String line = br.readLine();
            while (line != null) {
                // reading lines until the end of the file
                sb.append(line).append("\n");
                line = br.readLine();
            }
            docMain = sb.toString();
            docTitel = file.getName().substring(0, file.getName().length()-4);
            br.close();

            Document doc = new Document();
            Main.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS); //enable storing the reuired statistics
            Main.setStored(true);
            Main.setStoreTermVectors(true);
            Main.setStoreTermVectorPositions(true);
            Main.setStoreTermVectorPayloads(true);
            Main.setStoreTermVectorOffsets(true);
            Topic.setStored(true);
            doc.add(new Field("Main", docMain, Main));
            doc.add(new Field("Topic", docTitel, Topic));
            writer.addDocument(doc);
            docnum++;
        }
        writer.close();



    }
}
