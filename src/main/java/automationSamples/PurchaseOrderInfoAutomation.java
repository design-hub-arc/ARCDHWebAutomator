package automationSamples;

import automationTools.QueryManager;
import automationTools.AbstractPeopleSoftAutomation;
import automationTools.ResultManager;
import io.CsvFileRequirements;
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
    
    private static final CsvFileRequirements FILE_REQ = new CsvFileRequirements(
        "Input file should be the file downloaded from running the PSReports Requisitions automation", HEADERS
    );
    
    public PurchaseOrderInfoAutomation() {
        super(
            "PSReports Purchase Order Information",
            DESC,
            new QueryManager("https://psreports.losrios.edu/PurchaseOrderInformation.asp", FILE_REQ),
            new ResultManager("https://psreports.losrios.edu/PurchaseOrderInformationQ.asp")
        );
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
}
