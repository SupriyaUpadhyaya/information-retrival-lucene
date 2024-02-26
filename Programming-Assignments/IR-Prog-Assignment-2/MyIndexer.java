package com.ovgu.irproject;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;

public class MyIndexer {

    static FieldType fieldType = new FieldType();
    static int doc_count = 0;
    public static void executor() throws IOException, ParseException {
        Analyzer analyzer = CustomAnalyzer.builder()
                .withTokenizer("standard")
                .addTokenFilter("lowercase")
                .build();

        Directory index = new RAMDirectory();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(index, config);
        addDoc(writer, "Today is sunny");
        addDoc(writer, "She is a sunny girl");
        addDoc(writer, "To be or not to be");
        addDoc(writer, "She is in Berlin today");
        addDoc(writer, "Sunny Berlin");
        addDoc(writer, "Berlin is always exciting!");
        writer.close();

        String query = "sunny AND excited";
        Query q = new QueryParser("title", analyzer).parse(query);

        int hitsPerPage = 10;
        IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage);
        searcher.search(q, collector);
        ScoreDoc[] hits = collector.topDocs().scoreDocs;

        System.out.println("\n===========================================");
        System.out.println("P02 Part (a):");
        System.out.println("Output of Assignment 4.1(a):");
        System.out.println("===========================================");

        Fields fields = MultiFields.getFields(reader);
        Terms terms = fields.terms("title");
        TermsEnum iterator = terms.iterator();
        BytesRef byteRef = null;
        while((byteRef = iterator.next()) != null) {
            String term = new String(byteRef.bytes, byteRef.offset, byteRef.length);
            invertedIndex(term, reader);

        }

        System.out.println("\n\n===========================================");
        System.out.println("Output of Assignment 4.1(b):");
        System.out.println("===========================================");
        System.out.println("Query string: " + query );
        System.out.println("Found " + hits.length + " search_hits.");

        for (int i = 0; i < hits.length; ++i) {
            int docId = hits[i].doc;
            Document d = searcher.doc(docId);
            System.out.println((i + 1) + ". " + d.get("title"));
        }
        System.out.println("\n\n===========================================");
        System.out.println("P02 Part (b): ");
        System.out.println("===========================================");

        String query2 = "sunny";
        String query3 = "to";
        System.out.println("format: [tokenname:total frequency:doc frequency]->[docid:frequency:[positions]]->[docid:frequency:[positions]] \n");
        output(query2, reader);
        output(query3, reader);


    }

    public static void addDoc(IndexWriter writer, String title) throws IOException {
        Document doc = new Document();
        fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
        fieldType.setStored(true);
        fieldType.setStoreTermVectors(true);
        fieldType.setStoreTermVectorPositions(true);
        fieldType.setStoreTermVectorPayloads(true);
        fieldType.setStoreTermVectorOffsets(true);
        doc.add(new Field("title", title, fieldType));
        writer.addDocument(doc);
        doc_count++;
    }

    public static void invertedIndex(String querystr, IndexReader reader) throws IOException {
        Term termInstance = new Term("title", querystr);
        long termFreq = reader.totalTermFreq(termInstance);
        long docCount = reader.docFreq(termInstance);
        System.out.print("["+querystr+":"+docCount+"]");

        //iterate over all documents
        for(int i=0;i<doc_count;i++) {
            Terms termVector1 = reader.getTermVector(i, "title");
            TermsEnum iter1 = termVector1.iterator();
            BytesRef term_1 = null;
            PostingsEnum p = null;
            // iterate over all terms in each document
            // get term frequency of the each term and its positions
            while ((term_1 = iter1.next()) != null) {
                p = iter1.postings(p, PostingsEnum.ALL);
                if(term_1.equals(termInstance.bytes())) {
                    p.nextDoc();
                    long termFreq_1 = iter1.totalTermFreq();
                    final int pos = p.nextPosition();
                    System.out.print("->[");
                    System.out.print(i+1);
                    System.out.print("]");
                }
            }

        }
        System.out.println();
    }
    public static void output(String querystr, IndexReader reader) throws IOException {
        Term termInstance = new Term("title", querystr);
        long termFreq = reader.totalTermFreq(termInstance);
        long docCount = reader.docFreq(termInstance);
        System.out.print("["+querystr+":"+termFreq+":"+docCount+"]");

        //iterate over all documents
        for(int i=0;i<doc_count;i++) {
            Terms termVector1 = reader.getTermVector(i, "title");
            TermsEnum iter1 = termVector1.iterator();
            BytesRef term_1 = null;
            PostingsEnum p = null;
            // iterate over all terms in each document
            // get term frequency of the each term and its positions
            while ((term_1 = iter1.next()) != null) {
                p = iter1.postings(p, PostingsEnum.ALL);
                if(term_1.equals(termInstance.bytes())) {
                    p.nextDoc();
                    long termFreq_1 = iter1.totalTermFreq();
                    for(int k=0;k<termFreq_1;k++) {
                        final int pos = p.nextPosition();
                        int temp = i+1;
                        int temp2 = pos+1;
                        System.out.print("->[" + temp+":"+termFreq_1+":["+temp2+"]]");
                    }
                }
            }

        }
        System.out.println();
    }
}
