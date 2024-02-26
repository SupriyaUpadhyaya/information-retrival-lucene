
import java.util.Map;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class BiwordFilterFactory extends TokenFilterFactory {

    @SuppressWarnings({ "rawtypes", "unchecked" })
	public BiwordFilterFactory(Map args) {
        super(args);
    }

    @Override
    public TokenStream create(TokenStream stream) {
        return new BiwordFilter(stream);
    }

    }