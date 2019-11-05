package pages;

import io.QueryFileReader;
import java.io.IOException;
import util.SafeString;

/**
 *
 * @author Matt
 */
public class TestPage extends AbstractPageTemplate{
    
    public TestPage(){
        super("https://www.arc.losrios.edu/", "https://ps.losrios.edu/psp/student/?cmd=login&languageCd=ENG&");
    }
    @Override
    public void inputQuery(SafeString query) {
        
    }

    @Override
    public SafeString readQueryResult() {
        return new SafeString(new char[]{'d', 'o', ' ', 'n', 'o', 't', 'h', 'i', 'n', 'g', '\n'});
    }
    
    public static void main(String[] args) throws IOException{
        TestPage p = new TestPage();
        char[] file = new QueryFileReader().readStream(TestPage.class.getResourceAsStream("/testFile.csv")).toCharArray();
        p.run(file);
    }
}
