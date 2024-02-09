package org.mifos.integrationtest.cucumber.stepdef;

import static com.google.common.truth.Truth.assertThat;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import java.util.UUID;
import org.mifos.integrationtest.config.TenantConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * This class contains the generic step def which is or can be common for multiple test cases. <br>
 * It's very important to not write any payment schema specific code here.
 *
 * @author danishjamal
 */
public class GenericStepDef extends BaseStepDef {

    @Value("${tenants[0].value}") // PaymentBB1
    private String paymentBB1;

    @Value("${tenants[1].value}") // PaymentBB2
    private String paymentBB2;

    @Value("${tenants[2].value}") // PayerFSP
    private String payerFSP;


    @Autowired
    TenantConfig tenantConfig;

    @Value("${global_wait_time_ms}")
    private int globalWaitTime;

    @And("I have tenant as {string}")
    public void setTenantAnd(String tenant) {
        switch (tenant.toLowerCase()) {
            case "paymentbb1":
                setPaymentBB1(paymentBB1);
            logger.info("Tenant1  : {}",paymentBB1);
                break;
            case "paymentbb2":
                setPaymentBB2(paymentBB2);
            case "payerfsp":
                setPayerFSP(payerFSP);
            break;
            default:
                throw new IllegalArgumentException("Invalid tenant: " + tenant);

        }
    }

    private void setPaymentBB1(String tenant) {
        scenarioScopeState.tenant = tenant;
        assertThat(scenarioScopeState.tenant).isNotEmpty();    }

    private void setPaymentBB2(String tenant) {
        scenarioScopeState.tenant = tenant;
        assertThat(scenarioScopeState.tenant).isNotEmpty();
    }

    private void setPayerFSP(String tenant) {
        scenarioScopeState.tenant = tenant;
        assertThat(scenarioScopeState.tenant).isNotEmpty();
    }

    // @And("I have BB1 tenant")
    // public void setPaymentBB2And() {
    // String paymentBB2 = TenantConfig.getPaymentBB2();
    // setPaymentBB2(paymentBB2);
    // logger.info("Tenant is here: {}",paymentBB2);
    // }

    // private void setPaymentBB2(String paymentBB2) {
    // scenarioScopeState.paymentBB2 = paymentBB2;
    // }

    @Then("I should get non empty response")
    public void nonEmptyResponseCheck() {
        assertNonEmpty(scenarioScopeState.response);
    }

    @And("I will sleep for {int} millisecond")
    public void iWillSleepForSecs(int time) throws InterruptedException {
        Thread.sleep(time + globalWaitTime);
    }

    @And("I store this time as start time")
    public void storeCurrentTime() {
        scenarioScopeState.dateTime = getCurrentDateInFormat();
    }

    private void setTenant(String tenant) {
        scenarioScopeState.tenant = tenant;
        assertThat(scenarioScopeState.tenant).isNotEmpty();
        scenarioScopeState.clientCorrelationId = UUID.randomUUID().toString();
    }

    private void assertNonEmpty(String data) {
        assertThat(data).isNotNull();
    }

}
