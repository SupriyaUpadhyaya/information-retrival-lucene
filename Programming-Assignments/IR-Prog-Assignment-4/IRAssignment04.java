package com.ovgu.irproject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;

public class IRAssignment04 {
    static FieldType fieldType = new FieldType();
    private static Set<String> terms = new HashSet<>();
    private static RealVector V1, V2, V3, V4, V5, V6 = null;
    static int count=0;

    private static void addDoc(IndexWriter w, String Main) throws IOException {
        Document doc = new Document();

        fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS); //enable storing the reuired statistics
        fieldType.setStored(true);
        fieldType.setStoreTermVectors(true);
        fieldType.setStoreTermVectorPositions(true);
        fieldType.setStoreTermVectorPayloads(true);
        fieldType.setStoreTermVectorOffsets(true);
        doc.add(new Field("Main", Main, fieldType));
        w.addDocument(doc);
        count++;
    }

    public static double getCosineSimilarity(RealVector V1, RealVector V2) throws IOException {
        return (V1.dotProduct(V2)) / (V1.getNorm() * V2.getNorm());
    }

    public static double getDotSimilarity(RealVector V1, RealVector V2) throws IOException {
        return V1.dotProduct(V2);
    }

    public static double getEuclideanSimilarity(RealVector V1, RealVector V2) throws IOException {
        return V1.getDistance(V2);
    }

    static RealVector getTermFrequencies(IndexReader reader, int docId) throws IOException {
        Terms vector = reader.getTermVector(docId, "Main");
        double n = reader.getDocCount("Main");
        TermsEnum termsEnum = null;
        termsEnum = vector.iterator();
        Map<String, Integer> frequencies = new HashMap<>();
        RealVector realVector = new ArrayRealVector(terms.size());
        BytesRef text = null;
        ArrayList<Term> arr1 = new ArrayList<Term>();
        ArrayList<Long> arr2 = new ArrayList<Long>();
        while ((text = termsEnum.next()) != null) {
            String term = text.utf8ToString();
            int freq = (int) termsEnum.totalTermFreq();
            Term termInstance = new Term("Main", term);
            frequencies.put(term, freq);
            arr1.add(termInstance);
            arr2.add(termsEnum.totalTermFreq());
        }
        int i = 0;
        double idf, tf, tfidf = 0.0;
        for (String s : terms) {
            if(frequencies.containsKey(s)) {
                Term term = new Term("Main", s);
                int index=arr1.indexOf(term);
                Term termInstance=arr1.get(index);
                tf=arr2.get(index);
                double docCount = reader.docFreq(termInstance);
                double z=n/docCount;
                idf=Math.log10(z);
                tfidf=tf*idf;
            } else {
                tfidf=0.0;
            }
            realVector.setEntry(i++, tfidf);
        }
        return realVector;
    }


    static void addTerms(IndexReader reader, int docId) throws IOException {
        Terms vector = reader.getTermVector(docId, "Main");
        TermsEnum termsEnum = null;
        termsEnum = vector.iterator();
        BytesRef text = null;
        while ((text = termsEnum.next()) != null) {
            String term = text.utf8ToString();
            terms.add(term);
        }
    }
    public static void executor() throws IOException, ParseException {
        //analyzer only for lowercase and none letters characters
        Analyzer analyzer = CustomAnalyzer.builder()
                .withTokenizer("standard")
                .addTokenFilter("lowercase")
                .build();

        Directory index = new RAMDirectory(); //makes a new directory in the ram for storing the index
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(index, config); // making IndexWriter to add document to the index
        String[] corpus = {"today is sunny", "she is a sunny girl", "to be or not to be", "she is in berlin today", "sunny berlin sunny", "berlin is always exciting"};
        System.out.println("Given corpus:");
        System.out.println("-------------");
        for(int i=1; i <= corpus.length; i++){
            System.out.println("Document" + i + " : "+ corpus[i-1]);
            addDoc(writer, corpus[i-1]);
        }
        writer.close();

        IndexReader reader = DirectoryReader.open(index);	//reader to read the index
        for(int i=0; i < corpus.length; i++){
            addTerms(reader, i);
        }

        V1 = getTermFrequencies(reader, 0);
        V2 = getTermFrequencies(reader, 1);
        V3 = getTermFrequencies(reader, 2);
        V4 = getTermFrequencies(reader, 3);
        V5 = getTermFrequencies(reader, 4);
        V6 = getTermFrequencies(reader, 5);

        double EuclideanSimilarity = getEuclideanSimilarity(V1,V2);
        double dotSimilarity = getDotSimilarity(V1,V2);
        double cosineSimilarity = getCosineSimilarity(V1,V2);

        System.out.println("\nPart a. 1) :");
        System.out.println("--------");
        System.out.println("For Document1 and Document 1 :");
        System.out.println("Euclidean distance = "+ EuclideanSimilarity);
        System.out.println("Dot product = "+ dotSimilarity);
        System.out.println("Cosine similarity = "+ cosineSimilarity);

        RealVector V7 = new ArrayRealVector(terms.size());

        System.out.println("\n2) Given query: 'to sunny girl'. Similarity score: ");
        System.out.println("---------------------------------------------------");
        int i=0;
        String query[] = {"to", "sunny", "girl"};
        for(String term:terms) {
            int w = 0;
            for(int j = 0; j<query.length; j++) {
                if(term.equals(query[j])) {
                    w = 1;
                }
            }
            V7.setEntry(i++, w);
        }

        double[] cosSimi = new double[6];
        cosSimi[0] = getCosineSimilarity(V7,V1);
        cosSimi[1] = getCosineSimilarity(V7,V2);
        cosSimi[2] = getCosineSimilarity(V7,V3);
        cosSimi[3] = getCosineSimilarity(V7,V4);
        cosSimi[4] = getCosineSimilarity(V7,V5);
        cosSimi[5] = getCosineSimilarity(V7,V6);

        for(int j = 1; j <= corpus.length; j++){
            System.out.println("For Document " + j + " : " + cosSimi[j-1]);
        }

        double rank[]= cosSimi;
        Arrays.sort(rank);


        System.out.println("\nRanking: ");
        System.out.println("-----------");
        for(int j = cosSimi.length-1,k=1;j>=0;j--) {
            boolean bool=false;
            for(int n=0;bool==false;n++){
                if(cosSimi[n]==rank[j]) {
                    System.out.println("Rank "+k+" = "+corpus[n]);
                    cosSimi[n]=Integer.MAX_VALUE;
                    if(j>0) {
                        if(rank[j]!=rank[j-1]) {
                            k++;
                        }
                    }
                    bool=true;
                }
            }
        }


        //b) vsm
        System.out.println("\nPart b:");
        System.out.println("-------");
        System.out.println("Relevant documents while querying the document2 'She is a sunny girl' using vector space model - Ranking below:");


        RealVector V8 = new ArrayRealVector(terms.size());
        int l=0;
        String q2[] = {"she","is","a", "sunny", "girl"};
        for(String term:terms) {
            int w=0;
            for(int j=0;j<q2.length;j++) {
                if(term.equals(q2[j])) {
                    w=1;
                }
            }
            V8.setEntry(l++, w);
        }

        double[] cosine = new double[6];

        cosine[0] = getCosineSimilarity(V8,V1);
        cosine[1] = getCosineSimilarity(V8,V2);
        cosine[2] = getCosineSimilarity(V8,V3);
        cosine[3] = getCosineSimilarity(V8,V4);
        cosine[4] = getCosineSimilarity(V8,V5);
        cosine[5] = getCosineSimilarity(V8,V6);

        double rank1[]= cosine;
        Arrays.sort(rank1);

        for(int j = cosine.length-1;j>=0;j--) {
            if(rank1[j]==0.0) {
                break;
            }
            boolean bool=false;
            for(int n=0;bool==false;n++){
                if(cosine[n]==rank1[j]) {
                    System.out.println(rank1[j]+" : "+corpus[n]);
                    cosine[n]=Integer.MAX_VALUE;
                    bool=true;
                }
            }
        }


        //b) BM25
        System.out.println("\nRelevant documents while querying document1 'She is a sunny girl' using the BM25 model - Ranking below :");

        IndexSearcher searcher = new IndexSearcher(reader);
        searcher.setSimilarity(new BM25Similarity());
        String queryString = "She is a sunny girl";
        QueryParser parser = new QueryParser("Main", analyzer);
        Query query1 = parser.parse(queryString);

        TopScoreDocCollector docCollcetor = TopScoreDocCollector.create(10);
        searcher.search(query1, docCollcetor);

        ScoreDoc[] docs = docCollcetor.topDocs().scoreDocs;
        for (int j = 0; j < docs.length && j < 10; j++) {
            Document doc = searcher.doc(docs[j].doc);
            System.out.println(docs[j].score+" : "+ doc.get("Main"));
        }
        reader.close();

    }
}
