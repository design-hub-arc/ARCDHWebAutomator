package automations;

import io.CsvParser;
import io.FileRequirements;
import io.FileType;
import org.openqa.selenium.By;
import util.HtmlTable;

/**
 *
 * @author Matt
 */
public class PurchaseOrderInfoAutomation extends AbstractPeopleSoftAutomation{
    private static final String DESC = "Querys the PeopleSoft purchase order information page";
    private static final String[] HEADERS = new String[]{
        "Req ID",
        "PO ID"
    };
    
    private static final FileRequirements FILE_REQ = new FileRequirements(
        "Input file should be the file downloaded from running the PSReports Requisitions automation", FileType.CSV
    );
    
    public PurchaseOrderInfoAutomation() {
        super(
            "PSReports Purchase Order Information",
            DESC,
            "https://psreports.losrios.edu/PurchaseOrderInformation.asp", 
            "https://psreports.losrios.edu/PurchaseOrderInformationQ.asp"
        );
        setFileReq(FILE_REQ);
    }

    @Override
    public void inputQuery(String query) {
        String[] params = query.split(",");
        awaitFindElement(By.xpath("//input[@name='PurchaseOrderNumber']")).sendKeys(params[1]);
        awaitFindElement(By.xpath("//input[@name='RequisitionNumber']")).sendKeys(params[0]);
        awaitFindElement(By.name("B1")).click();
    }

    @Override
    public String readQueryResult() {
        HtmlTable t = new HtmlTable(awaitFindElement(By.xpath("//table[@border=1]")));
        return t.toCsv();
    }

    @Override
    public void afterReadingQuery() {
        awaitFindElement(By.xpath("//a[@href='PurchaseOrderInformation.asp'")).click();
    }

    @Override
    public String formatFile(String fileText) {
        return new CsvParser(HEADERS).reformat(fileText, false);
    }
}
