package pages;

/**
 *
 * @author Matt
 */
public class TestPage extends AbstractPageTemplate{
    
    public TestPage(){
        super("https://www.arc.losrios.edu/", "https://ps.losrios.edu/psp/student/?cmd=login&languageCd=ENG&");
    }
    @Override
    public void inputQuery(char[] query) {
        
    }

    @Override
    public char[] readQueryResult() {
        return new char[]{'d', 'o', ' ', 'n', 'o', 't', 'h', 'i', 'n', 'g', '\n'};
    }
    
    public static void main(String[] args){
        TestPage p = new TestPage();
        char[] file = new char[]{
            'a', 'b', 'c', '\n',
            'd', 'e', 'f', '\n'
        };
        p.run(file);
    }
}
