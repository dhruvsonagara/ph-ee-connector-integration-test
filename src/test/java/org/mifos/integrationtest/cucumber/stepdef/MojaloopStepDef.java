package org.mifos.integrationtest.cucumber.stepdef;

import static com.google.common.truth.Truth.assertThat;
import static io.restassured.config.EncoderConfig.encoderConfig;

import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.java.en.Then;
import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.mifos.integrationtest.common.Utils;
import org.mifos.integrationtest.config.MojaloopConfig;
import org.springframework.beans.factory.annotation.Autowired;

public class MojaloopStepDef extends BaseStepDef {

    @Autowired
    MojaloopConfig mojaloopConfig;

    @Autowired
    MojaloopDef mojaloopDef;

    @Then("I add {string} to als")
    public void addUsersToALS(String client) throws JsonProcessingException {

        String clientIdentifierId;
        String fspId;
        if (client.equals("payer")) {
            clientIdentifierId = BaseStepDef.payerIdentifier;
            fspId = mojaloopConfig.payerFspId;
        } else {
            clientIdentifierId = BaseStepDef.payeeIdentifier;
            fspId = mojaloopConfig.payeeFspId;
        }

        RequestSpecification requestSpec = Utils.getDefaultSpec();
        requestSpec.header("FSPIOP-Source", fspId);
        requestSpec.header("Date", getCurrentDateInFormat());
        requestSpec.header("Accept", "application/vnd.interoperability.participants+json;version=1");
        // requestSpec.header("Content-Type", "application/vnd.interoperability.participants+json;version=1.0");

        String endpoint = mojaloopConfig.addUserToAlsEndpoint;
        endpoint = endpoint.replaceAll("\\{\\{identifierType\\}\\}", "MSISDN");
        endpoint = endpoint.replaceAll("\\{\\{identifier\\}\\}", clientIdentifierId);

        String requestBody = mojaloopDef.setBodyAddAlsUser(fspId);

        logger.info(mojaloopConfig.mojaloopBaseurl);
        logger.info(requestBody);
        logger.info(endpoint);

        String response = RestAssured.given(requestSpec).baseUri(mojaloopConfig.mojaloopBaseurl)
                .config(RestAssured.config().encoderConfig(encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false)))
                .body(requestBody).contentType("application/vnd.interoperability.participants+json;version=1.0").expect()
                .spec(new ResponseSpecBuilder().expectStatusCode(202).build()).when().post(endpoint).andReturn().asString();

        logger.info(response);
        assertThat(response).isNotNull();
    }

}
