package com.ovgu.irproject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Boolean.TRUE;

public class DocumentPreProcessing {
    public static List<String> StdTokenizer(String input, Boolean stopWordFilter, Boolean caseSensitivity) throws IOException {
        List<String> output = new ArrayList<>();
        StandardTokenizer st = new StandardTokenizer();
        st.setReader(new StringReader(input));
        CharArraySet stopWordsFromfile = new CharArraySet(2, caseSensitivity);
        if ( stopWordFilter == TRUE){
            InputStream inputDoc = Main.class.getClassLoader().getResourceAsStream("stopwords.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputDoc));
            LineIterator it = IOUtils.lineIterator(reader);
            while (it.hasNext()) {
                stopWordsFromfile.add(it.nextLine());
            }
            it.close();
        }
        StopFilter sf = new StopFilter(st, stopWordsFromfile);
        sf.reset();
        while (sf.incrementToken()) {
            output.add(sf.getAttribute(CharTermAttribute.class).toString());
        }
        sf.close();
        return output;
    }

    public static List<String> wsTokenizer(String input) throws IOException {
        List<String> output = new ArrayList<>();
        WhitespaceTokenizer wt = new WhitespaceTokenizer();
        wt.setReader(new StringReader(input));
        wt.reset();
        while (wt.incrementToken()){
            output.add(wt.getAttribute(CharTermAttribute.class).toString());
        }
        wt.close();
        return output;
    }

    public static List<String> docAnalyzer(String input) throws IOException {
        List<String> output = new ArrayList<>();
        Analyzer analyzer = CustomAnalyzer.builder()
                .withTokenizer("standard")
                .addTokenFilter("lowercase")
                .addTokenFilter("stop", "ignoreCase", "false", "words", "stopwords.txt", "format", "wordset")
                .addTokenFilter("porterstem")
                .build();
        TokenStream ts = analyzer.tokenStream(null, new StringReader(input));
        ts.reset();
        while (ts.incrementToken()){
            output.add(ts.getAttribute(CharTermAttribute.class).toString());
        }
        ts.close();
        return output;
    }

}
