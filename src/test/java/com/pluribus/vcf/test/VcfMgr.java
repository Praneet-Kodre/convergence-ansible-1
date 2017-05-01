package com.pluribus.vcf.test;

import com.pluribus.vcf.helper.TestSetup;
import com.pluribus.vcf.helper.PageInfra;
import com.pluribus.vcf.helper.SwitchMethods;
import com.pluribus.vcf.pagefactory.VCFLoginPage;
import com.pluribus.vcf.pagefactory.VCFManagerPage;
import com.pluribus.vcf.pagefactory.VCFHomePage;
import com.pluribus.vcf.pagefactory.VCFIaIndexPage;

import static org.testng.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;
import org.testng.annotations.Parameters;

public class VcfMgr extends TestSetup{
	private VCFHomePage home1;
	private VCFManagerPage vcfMgr1;
	private VCFLoginPage login;
	private String vcfUserName = "admin";
	
	@BeforeClass(alwaysRun = true)
	public void init() throws Exception{
		login = new VCFLoginPage(getDriver());
		home1 = new VCFHomePage(getDriver());
		vcfMgr1 = new VCFManagerPage(getDriver());
	}
	
	@Parameters({"password"})
    @Test(groups = {"smoke","regression"}, description = "Login to VCF as admin  and Change Password")
    public void loginAdmin(@Optional("test123")String password) {
        login.firstlogin(vcfUserName,password);
        login.waitForLogoutButton();
        login.logout();
    }
	
	 @Parameters({"password"})  
	    @Test(groups = {"smoke","regression"},dependsOnMethods = {"loginAdmin"},description = "Login to VCF as test123 After Password Change")
	    public void loginTest123(@Optional("test123")String password) {
	        login.login(vcfUserName, password);
	        login.waitForLogoutButton();
	        assertEquals(getTitle(), "Pluribus Networks VCFcenter");
	        home1.gotoVCFMgr();
	}
	
	
/*
	@Parameters({"switchName", "password"})
	@Test(groups={"smoke","regression"},dependsOnMethods={"logintoVcfMgr"},description="Add seed switch")
	public void addSeedSwitchTest(String switchName,@Optional("test123") String password) throws Exception {
		if(vcfMgr1.addSeedSwitch(switchName,password)) {
			com.jcabi.log.Logger.info("vcfMgraddSeedSwitch","Configured and verified seed switch addition");
		} else {
			com.jcabi.log.Logger.error("cfMgraddSeedSwitch","Seed switch configuration failed");
		}
	}

	@Parameters({"vcfIp"})
	@Test(groups={"smoke","regression"},dependsOnMethods={"loginTest123"},description="Clean ZTP")
	public void cleanZtpTest(String vcfIp) {
		if(vcfMgr1.terminateAndCleanZtp(vcfIp)) {
			com.jcabi.log.Logger.info("vcfMgrconfig","Terminated previous instances of ansible and cleaned ZTP");
		} else {
			com.jcabi.log.Logger.error("vcfMgrconfig","Terminating prev instance failed");
		}
		//home1.gotoVCFMgr();
	}
*/	
	@Parameters({"hostFile", "csvFile", "password"})
	@Test(groups={"smoke","regression"},dependsOnMethods={"loginTest123"},description="Configure playbook 1")
	public void vcfMgrConfig1(String hostFile, String csvFile, @Optional("test123") String password) throws Exception {
		File file1 = new File(hostFile);
		if(file1.exists()) {
			if(vcfMgr1.launchZTP(hostFile,csvFile,password)) {
				com.jcabi.log.Logger.info("vcfMgrconfig","Fabric creation playbook was configured successfully");
			} else {
				com.jcabi.log.Logger.error("vcfMgrconfig","Fabric creation playbook was not configured successfully");
			}
		} else {
			com.jcabi.log.Logger.error("vcfMgrconfig", "File doesn't exist");
		}
	}
	
	/*
	@Parameters({"playbook2"})
	@Test(groups={"smoke","regression"},dependsOnMethods={"vcfMgrconfig1"},description="Configure playbook 2")
	public void vcfMgrConfig2(String playbook2) {
		if(vcfMgr1.launchPlaybook(playbook2)) {
			com.jcabi.log.Logger.info("vcfMgrconfig","Playbook "+playbook2+"was configured successfully");
		} else {
			com.jcabi.log.Logger.error("vcfMgrconfig","Playbook "+playbook2+"was not configured successfully");
		}
	}
	*/
}
