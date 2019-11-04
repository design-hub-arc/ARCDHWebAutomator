package pages;

import util.SafeString;

/**
 * @author Matt Crow
 */
public abstract class AbstractPageTemplate {
    private final String inputURL;
    private final String resultURL;
    private final SafeString result;
    
    public AbstractPageTemplate(String inputUrl, String resultUrl){
        if(!inputUrl.startsWith("/")){
            inputUrl = "/" + inputUrl;
        }
        if(!resultUrl.startsWith("/")){
            resultUrl = "/" + resultUrl;
        }
        inputURL = inputUrl;
        resultURL = resultUrl;
        result = new SafeString();
    }
    
    
    
    public void clean(){
        result.clearValue();
    }
    
    public void run(char[] queryFile){
        
    }
    
    public abstract void inputQuery(char[] query);
    public abstract char[] readQueryResult();
}
