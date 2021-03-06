package com.pluribus.vcf.pagefactory;
import com.jcabi.log.Logger;
import com.pluribus.vcf.helper.PageInfra;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.pluribus.vcf.helper.PageInfra;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.util.concurrent.TimeUnit;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;


public class VCFIaIndexPage extends PageInfra {


	@FindBy(how = How.CSS, using = "a.list-group-item.category.ia-dashboard-menu")
	WebElement dashboardIcon;
	
	@FindBy(how = How.CSS, using = "a.list-group-item.category.ia-config-menu")
	WebElement configIcon;
	
	@FindBy(how = How.CSS, using = "a.list-group-item.category.ia-tag-menu")
	WebElement tagIcon;
	
	@FindBy(how= How.CSS, using = "button.btn.btn-primary.btn-xs")
	WebElement addButton;
	
	@FindBy(how= How.CSS, using = "button.btn.btn-default.btn-sm")
	WebElement switchDropDown;
    
	@FindBy(how= How.CSS, using = "a#taggingOptions.btn.btn-default.dropdown-toggle")
	WebElement tagOptions;
	
	@FindBy(how = How.NAME, using = "username")
	WebElement userName;
	
	@FindBy(how = How.NAME, using = "password")
	WebElement password;

	@FindBy(how = How.NAME, using = "ok")
	WebElement okButton;

	@FindBy(how = How.CSS, using = "div.metric-value.ng-binding")
	WebElement countIcons;
	
	@FindBy(how = How.CSS, using = "input[type = 'text']")
	WebElement searchBox;
	
	/* Field names used for webdriver findElement*/
	String iframeTag = "iframe";
	String switchListName = "ul.dropdown-menu";
	String insightCountWidget =  "div.metric-value.ng-binding";
	String inputTagName = "input";
	String srchString = "a[title=";
	String collectorListId = "span.label-text";
	String collectorAddButtons = "button.btn.btn-sm.btn-primary";
	String uploadTagStr = "Upload Tags";
	String clearTagStr = "Clear Tags";
	String fileUpload = "div.holder span"; 
	String countIconsId = "div.metric-value.ng-binding";
	String okButtonId = "ok";
	
	public VCFIaIndexPage(WebDriver driver) {
		super(driver);
	}
	
	public List getSwitchList() {
		List<WebElement> rows = new ArrayList();
		rows = driver.findElements(By.cssSelector(switchListName));
		return rows;
	}
	
	public void applySearchFilter(String searchString) {
		waitForElementVisibility(searchBox,100);
		setValue(searchBox,searchString);
		WebElement searchItem = driver.findElement(By.cssSelector(srchString+"'"+searchString+"'"));
		if(searchItem.isDisplayed()) {
			searchItem.click();
		}
	}
	
	public List<WebElement> getInsightCount() {
		List<WebElement> rows = new ArrayList();
		dashboardIcon.click();
		waitForElementVisibility(driver.findElement(By.tagName(iframeTag)),1000);
		driver.switchTo().frame(driver.findElement(By.tagName(iframeTag)));	
		//retryingFindClick(By.cssSelector(countIconsId));
		waitForElementVisibility(countIcons,180);
		rows = driver.findElements(By.cssSelector(insightCountWidget));
		return rows;
	}
	
	public int getConnectionCount() {
		int connCount = 0;
		List <WebElement> rows = getInsightCount();
			if(!rows.isEmpty()) {
				String connOutput = rows.get(0).getText();
				if(StringUtils.contains(connOutput, ',')) {
					connOutput = StringUtils.remove(connOutput, ',');
				}
				connCount = Integer.parseInt(connOutput);
			}
			driver.switchTo().defaultContent();
		return connCount;
	}
	
	public int getAppCount() {
		int connCount = 0;
		List <WebElement> rows = getInsightCount();
			if(!rows.isEmpty()) {
				String connOutput = rows.get(1).getText();	
				if(StringUtils.contains(connOutput, ',')) {
					connOutput = StringUtils.remove(connOutput, ',');
				}
				connCount = Integer.parseInt(connOutput);
			}
			driver.switchTo().defaultContent();	
		return connCount;
	}

	public boolean isCollectorConfigured(String switchName) {
		boolean isColl = false;
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.MILLISECONDS);
		boolean exists = (driver.findElements(By.cssSelector(collectorListId)).size() != 0);
		//List<WebElement> collCount = driver.findElements(By.cssSelector(collectorListId));
		driver.manage().timeouts().implicitlyWait(100, TimeUnit.SECONDS);
		if(exists) {
				if(driver.findElement(By.cssSelector(collectorListId)).getText().contains(switchName)) {
					isColl = true;
					com.jcabi.log.Logger.info("collectorConfigured","Collector List:"+driver.findElement(By.cssSelector(collectorListId)).getText());				 } 
		}
		return isColl;
	}
	
	public boolean addCollector(String switchName, String user, String pwd) {
		boolean status = false;
		configIcon.click();
		status = isCollectorConfigured(switchName);
		if(status==false) {	
			try {
			Thread.sleep(5000);
			}catch(Exception e){
				System.out.println(e.toString());			
			}
			int i = 0;
			List<WebElement> rows = driver.findElements(By.cssSelector(collectorAddButtons));
			for (WebElement row: rows) {
				if(rows.get(i).getText().contains("Add Netvisor Collector")) {
					retryingFindClick(rows.get(i));
					//rows.get(i).click();
				}
				i++;
			}
			waitForElementVisibility(switchDropDown,1000);
			switchDropDown.click();
			rows = getSwitchList();
				for (WebElement row : rows) {
					if(row.getText().contains(switchName)) {
						row.click();
						break;
					}
				}
				okButton.click();
				WebDriverWait myWaitVar = new WebDriverWait(driver,100);
				myWaitVar.until(ExpectedConditions.elementToBeClickable (By.cssSelector(collectorListId)));
				status = isCollectorConfigured(switchName);
		}
		return status;
	}
	public void gotoIADashboard() {
		dashboardIcon.click();
		waitForElementVisibility(driver.findElement(By.tagName(iframeTag)),1000);
	}
	/*
	public void uploadFile(WebElement fileUpload, String filePath, WebElement okButton) {
		WebElement element = driver.findElement(fileUpload);
		element.sendKeys(filePath);
		okButton.click();
	}
	*/
	public static void setClipboardData(String string) {
		//StringSelection is a class that can be used for copy and paste operations.
		StringSelection stringSelection = new StringSelection(string);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
	}
	
	public void uploadTag(String fileLocation) throws Exception{
		File file1 = new File(fileLocation);
		if(file1.exists()) {
			System.out.println("File exists"+file1.toString());
			System.out.println("File path"+file1.toPath().toString());
		}
	    
		tagIcon.click();
		waitForElementVisibility(tagOptions,100);
		tagOptions.click();
		WebElement uploadTags = findAnchorTags(uploadTagStr);
		uploadTags.click();
		waitForElementVisibility(driver.findElement(By.cssSelector(fileUpload)),100);
	    WebElement element = driver.findElement(By.cssSelector(fileUpload));
		((RemoteWebElement) element).setFileDetector(new LocalFileDetector()); 
		//element.sendKeys(fileLocation); 
		Thread.sleep(10000);
		element.click(); //Click on fileUpload
		//Robot impl 1
		/*
		setClipboardData(fileLocation);
		Robot robot = new Robot();
		robot.delay(10000);
		
		robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_CONTROL);
        robot.delay(10000);
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
        robot.delay(10000);
        */
        //Robot impl 2 
		
		Robot robot = new Robot();
		File file = new File(fileLocation);
		
		StringSelection s = new StringSelection(file.getAbsolutePath());
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(s, null);
		System.out.println(Toolkit.getDefaultToolkit().getSystemClipboard().getContents(s).toString());
		robot.keyPress(KeyEvent.VK_META);
	    robot.keyPress(KeyEvent.VK_TAB);
	    robot.keyRelease(KeyEvent.VK_META);
	    robot.keyRelease(KeyEvent.VK_TAB);
	    robot.delay(500);
	    robot.keyPress(KeyEvent.VK_META);
	    robot.keyPress(KeyEvent.VK_SHIFT);
	    robot.keyPress(KeyEvent.VK_G);
	    robot.keyRelease(KeyEvent.VK_META);
	    robot.keyRelease(KeyEvent.VK_SHIFT);
	    robot.keyRelease(KeyEvent.VK_G);
	    robot.keyPress(KeyEvent.VK_META);
	    robot.keyPress(KeyEvent.VK_V);
	    robot.keyRelease(KeyEvent.VK_META);
	    robot.keyRelease(KeyEvent.VK_V);
	    robot.keyPress(KeyEvent.VK_ENTER);
	    robot.keyRelease(KeyEvent.VK_ENTER);
	    robot.delay(500);
	    //robot.keyPress(KeyEvent.VK_ENTER);
	    //robot.keyRelease(KeyEvent.VK_ENTER);
	    //robot.delay(500);
	    robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_CONTROL);
        robot.delay(1000);
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
        robot.delay(1000);
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_CONTROL);
        robot.delay(1000);
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
        
        
        waitForElementToClick(By.name(okButtonId),100);
		waitForElementVisibility(okButton,100);
		// To click on the submit button (Not the browse button)
		 okButton.click();	
		//String checkText = driver.findElement(By.id("message")).getText();
		//Assert.assertEquals("File uploaded successfully", checkText);	
	}
	
	/*
	static void DropFile(IWebElement target, String filePath, int offsetX, int offsetY) {
	    if(!File.Exists(filePath))
	        throw new FileNotFoundException(filePath);

	    IWebDriver driver = ((RemoteWebElement)target).WrappedDriver;
	    IJavaScriptExecutor jse = (IJavaScriptExecutor)driver;
	    WebDriverWait wait = new WebDriverWait(driver, TimeSpan.FromSeconds(30));

	    string JS_DROP_FILE = @"
	        var target = arguments[0],
	            offsetX = arguments[1],
	            offsetY = arguments[2],
	            document = target.ownerDocument || document,
	            window = document.defaultView || window;

	        var input = document.createElement('INPUT');
	        input.type = 'file';
	        input.style.display = 'none';
	        input.onchange = function () {
	          target.scrollIntoView(true);

	          var rect = target.getBoundingClientRect(),
	              x = rect.left + (offsetX || (rect.width >> 1)),
	              y = rect.top + (offsetY || (rect.height >> 1)),
	              dataTransfer = { files: this.files };

	          ['dragenter', 'dragover', 'drop'].forEach(function (name) {
	            var evt = document.createEvent('MouseEvent');
	            evt.initMouseEvent(name, !0, !0, window, 0, 0, 0, x, y, !1, !1, !1, !1, 0, null);
	            evt.dataTransfer = dataTransfer;
	            target.dispatchEvent(evt);
	          });

	          setTimeout(function () { document.body.removeChild(input); }, 25);
	        };
	        document.body.appendChild(input);
	        return input;
	        ";

	    IWebElement input =  (IWebElement)jse.ExecuteScript(JS_DROP_FILE, target, offsetX, offsetY);
	    input.SendKeys(filePath);
	    wait.Until(ExpectedConditions.StalenessOf(input));
	}
	*/
	public WebElement findAnchorTags(String anchorText) {
		List <WebElement> anchorTags = driver.findElements(By.cssSelector("a"));
		WebElement returnRow = null;
		for (WebElement row:anchorTags) {
			if(row.getText().equalsIgnoreCase(anchorText)) {
				returnRow = row;
				break;
			}
		}
		return returnRow;
	}
	
}
