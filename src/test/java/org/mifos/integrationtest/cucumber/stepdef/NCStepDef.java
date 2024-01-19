package org.mifos.integrationtest.cucumber.stepdef;

import static com.google.common.truth.Truth.assertThat;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import java.util.UUID;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifos.integrationtest.common.TransferHelper;
import org.mifos.integrationtest.common.Utils;
import org.mifos.integrationtest.config.NetflixConductorConfig;
import org.springframework.beans.factory.annotation.Autowired;

public class NCStepDef extends BaseStepDef {

    @Autowired
    NetflixConductorConfig netflixConductorConfig;

    @When("I make a call to nc server health API with expected status 200")
    public void ncHealthAPICall() {
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        BaseStepDef.response = RestAssured.given(requestSpec).baseUri(netflixConductorConfig.conductorServerContactPoint).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(200).build()).when().get(netflixConductorConfig.healthEndpoint).andReturn()
                .asString();
    }

    @Then("I get the value of Healthy as true in response")
    public void checkHealthyState() throws JSONException {
        JSONObject response = new JSONObject(BaseStepDef.response);
        assertThat(response).isNotNull();
        String healthStatus = response.getString("healthy");
        assertThat(healthStatus).isEqualTo("true");
    }

    @And("I have the request body for transfer")
    public void iHaveRequestBody() throws JSONException {
        String payerIdentifier = BaseStepDef.payerIdentifier;
        JSONObject collectionRequestBody;

        if (payerIdentifier != null) {
            collectionRequestBody = TransferHelper.getTransferRequestBody(payerIdentifier);
        } else {
            collectionRequestBody = TransferHelper.getTransferRequestBody();
        }

        BaseStepDef.requestBody = collectionRequestBody;
        logger.info(String.valueOf(BaseStepDef.requestBody));
    }

    @When("I call the channel transfer API with client correlation id and expected status of {int}")
    public void channelTransferAPICall(int expectedStatus) {
        RequestSpecification requestSpec = Utils.getDefaultSpec(BaseStepDef.tenant);
        requestSpec.header(Utils.X_CORRELATIONID, UUID.randomUUID());
        BaseStepDef.response = RestAssured.given(requestSpec).baseUri("http://dpga-connector-chanel.sandbox.fynarfin.io/")
                .body(BaseStepDef.requestBody.toString()).expect().spec(new ResponseSpecBuilder().expectStatusCode(expectedStatus).build())
                .when().post(channelConnectorConfig.transferEndpoint).andReturn().asString();
    }

    @When("I call the get workflow API in  with workflow id as path variable")
    public void getWorkflow() {
        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec.param("includeTasks", false);
        BaseStepDef.response = RestAssured.given(requestSpec).baseUri(netflixConductorConfig.conductorServerContactPoint).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(200).build()).when()
                .get(netflixConductorConfig.workflowEndpoint + "/" + BaseStepDef.transactionId).andReturn().asString();
    }

    @Then("I should get valid status")
    public void checkStatus() throws JSONException {
        JSONObject response = new JSONObject(BaseStepDef.response);
        assertThat(response).isNotNull();
        String status = response.getString("status");
        assertThat(status).isAnyOf("COMPLETED", "TERMINATED", "RUNNING");
    }

    @When("I call the get transfer API in ops app with transactionId as parameter")
    public void iCallTheTransferAPIWithTransactionId() throws InterruptedException {
        RequestSpecification requestSpec = Utils.getDefaultSpec(BaseStepDef.tenant);
        if (authEnabled) {
            requestSpec.header("Authorization", "Bearer " + BaseStepDef.accessToken);
        }
        requestSpec.queryParam("transactionId", BaseStepDef.transactionId);

        Thread.sleep(5000);

        BaseStepDef.response = RestAssured.given(requestSpec).baseUri(operationsAppConfig.dpgOperationAppContactPoint).expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(200).build()).when().get(operationsAppConfig.transfersEndpoint).andReturn()
                .asString();

        logger.info(BaseStepDef.transactionId);
        logger.info("Get Transfer Response: " + BaseStepDef.response);
    }

    @Then("I should get transfer state as completed")
    public void assertValues() throws JSONException {
        JsonObject jsonObject = JsonParser.parseString(BaseStepDef.response).getAsJsonObject();
        String status = jsonObject.getAsJsonArray("content").get(0).getAsJsonObject().get("status").getAsString();
        assertThat(status).isAnyOf("COMPLETED", "TERMINATED");
    }

    @Then("I verify that the current balance is {long}")
    public void isAccountBalanceValid(long expectedBalance) {
        assertThat(BaseStepDef.currentBalance).isEqualTo(expectedBalance);
    }

}
