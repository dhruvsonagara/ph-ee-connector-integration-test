package org.mifos.integrationtest.cucumber.stepdef;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Value;

import java.util.UUID;

import static com.google.common.truth.Truth.assertThat;

/**
 * This class contains the generic step def which is or can be common for multiple test cases. <br>
 * It's very important to not write any payment schema specific code here.
 *
 * @author danishjamal
 */
public class GenericStepDef extends BaseStepDef {

    @Value("${global_wait_time_ms}")
    private int globalWaitTime;

    @And("I have tenant as {string}")
    public void setTenantAnd(String tenant) {
        setTenant(tenant);
    }

    @Then("I should get non empty response")
    public void nonEmptyResponseCheck() {
        assertNonEmpty(BaseStepDef.response);
    }

    @And("I will sleep for {int} millisecond")
    public void iWillSleepForSecs(int time) throws InterruptedException {
        Thread.sleep(time + globalWaitTime);
    }

    @And("I store this time as start time")
    public void storeCurrentTime() {
        BaseStepDef.dateTime = getCurrentDateInFormat();
    }

    private void setTenant(String tenant) {
        BaseStepDef.tenant = tenant;
        assertThat(BaseStepDef.tenant).isNotEmpty();
        BaseStepDef.clientCorrelationId = UUID.randomUUID().toString();
    }

    private void assertNonEmpty(String data) {
        assertThat(data).isNotNull();
    }

}
