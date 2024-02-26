
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.store.RAMDirectory;


import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public class IR_Assignment_P03 {
	static int doc_count=0;
	static FieldType ft=new FieldType();
	private static void addDoc(IndexWriter w, String Main) throws IOException {
		Document doc_obj = new Document();
		ft.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS); // storing required statistics
		ft.setStored(true);
		ft.setStoreTermVectors(true);
		ft.setStoreTermVectorPositions(true);
		ft.setStoreTermVectorPayloads(true);
		ft.setStoreTermVectorOffsets(true);
		doc_obj.add(new Field("Main", Main, ft));
		w.addDocument(doc_obj);
		doc_count++;
		}
	
	public static Analyzer analyzer() throws IOException{
		Analyzer analyser = CustomAnalyzer.builder()
                .withTokenizer(StandardTokenizerFactory.class)
                .addTokenFilter(LowerCaseFilterFactory.class)
                .addTokenFilter(BiwordFilterFactory.class)
                .build();
		return analyser;
	}
	
	
	public static List<String> analyze(String str, Analyzer analyzer) throws IOException{
		 List<String> result_str = new ArrayList<String>();
		 TokenStream token_stream  = analyzer.tokenStream(null, new StringReader(str));
	     token_stream.reset();
	     while (token_stream.incrementToken()) {
	       result_str.add(token_stream.getAttribute(CharTermAttribute.class).toString());
	     }
	     token_stream.close();
	     token_stream.reset();
	     token_stream.end();
		return result_str;
	}
	
	public static void main(String[] args) throws IOException, ParseException {
		String qn_sentence="Today is sunny. She is a sunny girl. To be or not to be. She is in Berlin today. Sunny Berlin! Berlin is always exciting!";
		List<String> result_new = analyze(qn_sentence, analyzer());
		System.out.println("P03 Part (a): \n");
		System.out.println("Given sentence:\n" + qn_sentence + "\n" +"Result after analyzing with biword tokenizer, standard tokenizer and lowercase filter, is given below:\n" +result_new+ "\n--------------------------------------------------------------------------\n");
		System.out.println("P03 Part (b): \n");
		System.out.println("Output of Assignment 4.3 (a): \n");
		System.out.println("Example of a document which will be returned for a query of 'New York University' but is actually a false positive which should not be returned, is given below:");
		System.out.println("\"Most students from New York who apply to York University in Toronto are accepted for their high grades in academics and extra-curricular activities.\"");
		List<String> result2 = analyze("Most students from New York who apply to York University in Toronto are accepted for their high grades in academics and extra-curricular activities.", analyzer());
		System.out.println("Above example analyzed with biword tokenizer, standard tokenizer and lowercase filter, is given below::\n" +result2+ "\n--------------------------------------------------------------------------\n");
		
		Directory index = new RAMDirectory(); //creates a new directory in ram for saving index
		IndexWriterConfig config = new IndexWriterConfig(analyzer());
		IndexWriter writer = new IndexWriter(index, config); // IndexWriter adds document to index
		
		//adding docs to the directory with IndexWriter by using addDoc()
		addDoc(writer, "Most students from New York who apply to York University in Toronto are accepted for their high grades in academics and extra-curricular activities.");
		writer.close();
		
		String querystr1 = "New York University";
		Query q = new QueryParser("Main", analyzer()).parse(querystr1);
				
		int hitsOnPage = 10;
		IndexReader reader = DirectoryReader.open(index);	//reader to read index
		IndexSearcher searcher = new IndexSearcher(reader); 
		TopScoreDocCollector collector = TopScoreDocCollector.create(hitsOnPage);
		searcher.search(q, collector); //search for query and store all matches
		ScoreDoc[] matches = collector.topDocs().scoreDocs;

		System.out.println("Given query: " + querystr1+ "---> Analyzed with biword tokenizer, standard tokenizer and lowercase filter, is given below: \n"+ analyze(querystr1, analyzer()));
		System.out.println();

		System.out.println("Given query string: " + querystr1 );
		System.out.println("Found " + matches.length + " matches.");
		//find all the docs with query match
		for (int i = 0; i < matches.length; ++i) {
			int docId = matches[i].doc;
			Document d = searcher.doc(docId);
			System.out.println((i + 1) + ". " + d.get("Main"));
		}
		System.out.println("False Positive Case identified");
	}	
}
