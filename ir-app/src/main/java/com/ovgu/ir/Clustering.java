package com.ovgu.ir;

import au.com.bytecode.opencsv.CSVWriter;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

public class Clustering {

    private static Set<String> terms = new HashSet<>();
    static int[] docsid;

    static ArrayList<String> clust1 = new ArrayList<>();
    static ArrayList<String> clust2 = new ArrayList<>();
    static ArrayList<String> clust3 = new ArrayList<>();
    static ArrayList<String> clust4 = new ArrayList<>();
    static ArrayList<String> clust5 = new ArrayList<>();

    static HashMap<String, Float> docRank = new HashMap<>();

    static IndexSearcher searcher;
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

    static RealVector getTermFrequencies(IndexReader reader, int docId)
            throws IOException {
        Terms vector = reader.getTermVector(docId, "Main");
        double n=reader.getDocCount("Main");
        TermsEnum termsEnum = null;
        termsEnum = vector.iterator();
        Map<String, Integer> frequencies = new HashMap<>();
        RealVector rvector = new ArrayRealVector(terms.size());
        BytesRef text = null;
        ArrayList<Term> v=new ArrayList<Term>();
        ArrayList<Long> g=new ArrayList<Long>();
        while ((text = termsEnum.next()) != null) {
            String term = text.utf8ToString();
            int freq = (int) termsEnum.totalTermFreq();
            Term termInstance = new Term("Main", term);
            frequencies.put(term, freq);
            v.add(termInstance);
            g.add(termsEnum.totalTermFreq());
        }
        int i = 0;
        //int j=0;
        double idf=0.0;
        double tf=0.0;
        double tfidf=0.0;
        for (String term1 : terms) {
            if(frequencies.containsKey(term1)) {
                Term termm = new Term("Main", term1);
                int index=v.indexOf(termm);
                Term termInstance=v.get(index);
                tf=g.get(index);
                double docCount = reader.docFreq(termInstance);
                double z=n/docCount;
                idf=Math.log10(z);
                tfidf=tf*idf;
            } else {
                tfidf=0.0;
            }
            rvector.setEntry(i++, tfidf);
        }
        return rvector;
    }

    public static void cluster(int x) throws Exception {
        BufferedReader breader = null;
        breader = new BufferedReader(new FileReader(
                "/Users/supriyaupadhyaya/ovgu/Info-Retrival/ir-app/src/main/resources/output/tests.arff"));
        Instances Train = new Instances(breader);
        //Train.setClassIndex(Train.numAttributes() - 1); // comment out this line
        SimpleKMeans kMeans = new SimpleKMeans();
        kMeans.setSeed(10);
        kMeans.setPreserveInstancesOrder(true);
        kMeans.setNumClusters(x);
        kMeans.buildClusterer(Train);
        int[] assignments = kMeans.getAssignments();
        int i2 = 0;
        HashMap<String, Integer> clust = new HashMap<>();
        for (int clusterNum : assignments) {
            System.out.printf("Instance %d -> Cluster %d", i2, clusterNum);

            Document docu = searcher.doc(docsid[i2]);
            clust.put(docu.get("Topic"), clusterNum);

            i2++;
        }
        breader.close();
        /*
        for (String name: clust.keySet()) {
            String key = name.toString();
            String value = clust.get(name).toString();
            System.out.println(key + " " + value);
        }
        */

        for (String name: clust.keySet()) {
            String key = name.toString();
            int value = clust.get(name);
            //("cluster name " + clust.get(name));
            if(value==0) {
                //System.out.println("key item" + key);
                clust1.add(key);
            }else if(value==1) {
                clust2.add(key);
            }else if(value==2) {
                clust3.add(key);
            }else if(value==3){
                clust4.add(key);
            }else if(value==4) {
                clust5.add(key);
            }
        }

    }

    public static JSONArray getCluster(String q, int x) throws Exception {
        RealVector v1=null;
        RealVector v2=null;
        RealVector v3=null;
        RealVector v4=null;
        RealVector v5=null;
        RealVector v6=null;
        RealVector v7=null;
        RealVector v8=null;
        RealVector v9=null;
        RealVector v10=null;
//from user
        Analyzer analyzer = new EnglishAnalyzer();
        Similarity simType = new BM25Similarity();
        Directory index = FSDirectory.open(Paths.get("/Users/supriyaupadhyaya/ovgu/Info-Retrival/ir-app/src/main/resources/output"));
        docsid = new int[10];
        clust1.clear();
        clust2.clear();
        clust3.clear();
        clust4.clear();
        clust5.clear();

        IndexReader reader = DirectoryReader.open(index);	//reader to read the index

        searcher = new IndexSearcher(reader);
        searcher.setSimilarity(simType);
        String queryString = q;
        //from user
        QueryParser parser = new QueryParser("Main", analyzer);
        Query query = parser.parse(queryString);

        TopScoreDocCollector docCollcetor = TopScoreDocCollector.create(10);
        searcher.search(query, docCollcetor);
        System.out.println("ranking: ***********");

        ScoreDoc[] docs = docCollcetor.topDocs().scoreDocs;
        System.out.println("collector" + docs.length);
        for (int j = 0; j < docs.length && j < 10; j++) {
            Document docu = searcher.doc(docs[j].doc);
            System.out.println("doc value" + docu);
            docsid[j] = docs[j].doc;
            System.out.println("<"+ docs[j].score+ ">"+" : "+ "<"+ docu.get("Topic")+">");
            docRank.put(docu.get("Topic"), docs[j].score);
        }

        addTerms(reader, docsid[0]);
        addTerms(reader, docsid[1]);
        addTerms(reader, docsid[2]);
        addTerms(reader, docsid[3]);
        addTerms(reader, docsid[4]);
        addTerms(reader, docsid[5]);
        addTerms(reader, docsid[6]);
        addTerms(reader, docsid[7]);
        addTerms(reader, docsid[8]);
        addTerms(reader, docsid[9]);



        v1 = getTermFrequencies(reader, docsid[0]);
        v2 = getTermFrequencies(reader, docsid[1]);
        v3 = getTermFrequencies(reader, docsid[2]);
        v4 = getTermFrequencies(reader, docsid[3]);
        v5 = getTermFrequencies(reader, docsid[4]);
        v6 = getTermFrequencies(reader, docsid[5]);
        v7 = getTermFrequencies(reader, docsid[6]);
        v8 = getTermFrequencies(reader, docsid[7]);
        v9 = getTermFrequencies(reader, docsid[8]);
        v10 = getTermFrequencies(reader, docsid[9]);

        double[] arr1 = v1.toArray();
        double[] arr2 = v2.toArray();
        double[] arr3 = v3.toArray();
        double[] arr4 = v4.toArray();
        double[] arr5 = v5.toArray();
        double[] arr6 = v6.toArray();
        double[] arr7 = v7.toArray();
        double[] arr8 = v8.toArray();
        double[] arr9 = v9.toArray();
        double[] arr10 = v10.toArray();

        String s1 = Arrays.toString(arr1);
        s1=s1.substring(1, s1.length()-1);
        String[] ss1 = s1.split(", ");

        String s2 = Arrays.toString(arr2);
        s2=s2.substring(1, s2.length()-1);
        String[] ss2 = s2.split(", ");

        String s3 = Arrays.toString(arr3);
        s3=s3.substring(1, s3.length()-1);
        String[] ss3 = s3.split(", ");

        String s4 = Arrays.toString(arr4);
        s4=s4.substring(1, s4.length()-1);
        String[] ss4 = s4.split(", ");

        String s5 = Arrays.toString(arr5);
        s5=s5.substring(1, s5.length()-1);
        String[] ss5 = s5.split(", ");

        String s6 = Arrays.toString(arr6);
        s6=s6.substring(1, s6.length()-1);
        String[] ss6 = s6.split(", ");

        String s7 = Arrays.toString(arr7);
        s7=s7.substring(1, s7.length()-1);
        String[] ss7 = s7.split(", ");

        String s8 = Arrays.toString(arr8);
        s8=s8.substring(1, s8.length()-1);
        String[] ss8 = s8.split(", ");

        String s9 = Arrays.toString(arr9);
        s9=s9.substring(1, s9.length()-1);
        String[] ss9 = s9.split(", ");

        String s10 = Arrays.toString(arr10);
        s10=s10.substring(1, s10.length()-1);
        String[] ss10 = s10.split(", ");






        File file2 = new File("/Users/supriyaupadhyaya/ovgu/Info-Retrival/ir-app/src/main/resources/output/test.csv");
        try {
            // create FileWriter object with file as parameter
            FileWriter outputfile = new FileWriter(file2);

            // create CSVWriter object filewriter object as parameter
            CSVWriter writer2 = new CSVWriter(outputfile);

            // adding header to csv
            String[] header = new String[ss1.length];
            int a=0;
            for(int i=0;i<header.length;i++) {
                a=a+1;
                header[i]=String.valueOf(a);
            }
            writer2.writeNext(header);

            writer2.writeNext(ss1);
            writer2.writeNext(ss2);
            writer2.writeNext(ss3);
            writer2.writeNext(ss4);
            writer2.writeNext(ss5);
            writer2.writeNext(ss6);
            writer2.writeNext(ss7);
            writer2.writeNext(ss8);
            writer2.writeNext(ss9);
            writer2.writeNext(ss10);



            // closing writer connection
            writer2.close();
        }
        catch (IOException e) {

            e.printStackTrace();
        }






        CSVLoader loader = new CSVLoader();
        loader.setSource(new File("/Users/supriyaupadhyaya/ovgu/Info-Retrival/ir-app/src/main/resources/output/test.csv"));
        Instances data = loader.getDataSet();

        // save ARFF
        ArffSaver saver = new ArffSaver();
        saver.setInstances(data);
        saver.setFile(new File("/Users/supriyaupadhyaya/ovgu/Info-Retrival/ir-app/src/main/resources/output/tests.arff"));
        saver.writeBatch();

        cluster(x);
        ArrayList<ArrayList<String>> output = new ArrayList();
        //System.out.println(clust1 + "output1");
        output.add(clust1);
        output.add(clust2);
        output.add(clust3);
        output.add(clust4);
        output.add(clust5);

        //System.out.println(clust1 + " c1" + clust2 + "c2" + clust3 + "c3" + clust4 + "c4" + clust5 + "c5");

        JSONArray response = new JSONArray();
        Integer i = 0;
        for( ArrayList<String> blob : output){
            JSONObject cluster = new JSONObject();
            JSONArray clusterMembers = new JSONArray();
            String name = "Cluster " + i.toString(i);
            //System.out.println(name);
            //System.out.println(blob + "blob");
            String s = "";
            if ( !blob.isEmpty()) {
                for (String member : blob) {
                    //System.out.println(member);
                    JSONObject doc = new JSONObject();
                    doc.put("name", member);
                    for (Map.Entry<String, Float> set : docRank.entrySet()) {
                        if (set.getKey().equalsIgnoreCase(member)) {
                            doc.put("value", set.getValue());
                        }
                    }
                    clusterMembers.add(doc);
                }

                cluster.put("name", name);
                cluster.put("data", clusterMembers);
                response.add(cluster);
            }
            i++;

        }
        return response;

    }
}
