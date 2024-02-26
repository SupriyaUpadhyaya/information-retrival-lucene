
import java.io.IOException;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public class BiwordFilter extends TokenFilter {

	private CharTermAttribute charTermAttr;
	private static char[] buffer_1;
	private static int length_1;
	private static int iteration=0;
	
	  protected BiwordFilter(TokenStream ts) {
	    super(ts);
	    this.charTermAttr = addAttribute(CharTermAttribute.class);
	  }

	  @Override
	  public boolean incrementToken() throws IOException {
		 
	    if (!input.incrementToken()) {
	    	buffer_1=null;
	    	length_1=0;
	    	iteration=0;
	      return false;
	    }
	    if(iteration==0) {
	    length_1 = charTermAttr.length();
		buffer_1 = charTermAttr.buffer().clone();
	    }
	    if(iteration==0) {
	    	input.incrementToken();
	    }
		
	    int length2 = charTermAttr.length();
	    char[] buffer2 = charTermAttr.buffer().clone();
	    
	    int length=length_1+length2+1;
	    char[] newBuffer = new char[length];
	    
	    for (int i = 0; i < length_1+1; i++) {
	    	if(i<length_1) {
	    		newBuffer[i] = buffer_1[i];
	    	}else if(i==length_1) {
	    		newBuffer[i] = ' ';
	    	}
	    }
	    
	    for(int i=0;i<length2;i++) {
	    	newBuffer[i+length_1+1] = buffer2[i];
		}
	    charTermAttr.setEmpty();
	    charTermAttr.copyBuffer(newBuffer, 0, length);
	    iteration++;
	    buffer_1=buffer2.clone();
	    length_1=length2;
	    return true;
	  }

}