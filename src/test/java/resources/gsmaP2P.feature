@amsIntegration @govtodo
Feature: GSMA Outbound Transfer test

  Scenario: GSMA Withdrawal Transfer testx (Payer Debit only)
    Given I have Fineract-Platform-TenantId as "gorilla"
    When I create a set of debit and credit party
    When I call the create payer client endpoint
    Then I call the create savings product endpoint
    When I call the create savings account endpoint
    Then I call the debit interop identifier endpoint with MSISDN
    Then I approve the deposit with command "approve"
    When I activate the account with command "activate"
    Then I call the deposit account endpoint with command "deposit" for amount 100
    Given I have Fineract-Platform-TenantId as "lion"
    When I call the create payer client endpoint
    Then I call the create savings product endpoint
    When I call the create savings account endpoint
    Then I call the credit interop identifier endpoint with MSISDN
    Then I approve the deposit with command "approve"
    When I activate the account with command "activate"
    Then I call the deposit account endpoint for "payer" with command "deposit" for amount 100
    Given I have tenant as "paymentBB2"
    Then I call the balance api for payer balance
    Given I have tenant as "paymentBB2"
    When I can create GSMATransferDTO with different payer and payee
    Then I call the GSMATransfer endpoint with expected status of 200
    And I should be able to parse transactionId from response
    Given I have tenant as "paymentBB2"
    When I call the transfer query endpoint with transactionId and expected status of 200
#    Then I will sleep for 1000 millisecond
    Given I have tenant as "paymentBB2"
    Then I call the balance api for payer balance after debit

  Scenario: GSMA Deposit Transfer test
    Given I have Fineract-Platform-TenantId as "gorilla"
    When I create a set of debit and credit party
    When I call the create payer client endpoint
    Then I call the create savings product endpoint
    When I call the create savings account endpoint
    Then I call the debit interop identifier endpoint with MSISDN
    Then I approve the deposit with command "approve"
    When I activate the account with command "activate"
    Then I call the deposit account endpoint for "payee" with command "deposit" for amount 100
    Given I have Fineract-Platform-TenantId as "lion"
    When I call the create payer client endpoint
    Then I call the create savings product endpoint
    When I call the create savings account endpoint
    Then I call the credit interop identifier endpoint with MSISDN
    Then I approve the deposit with command "approve"
    When I activate the account with command "activate"
    Then I call the deposit account endpoint with command "deposit" for amount 100
    Given I have tenant as "payerFSP"
    Then I call the balance api for payee balance
    Given I have tenant as "payerFSP"
    When I can create GSMATransferDTO with different payer and payee
    Then I call the GSMATransfer Deposit endpoint with expected status of 200
#    Then I will sleep for 1000 millisecond
    Given I have tenant as "payerFSP"
    Then I call the balance api for payee balance after credit

  Scenario: GSMA Deposit-Withdrawal Transfer test
    Given I have Fineract-Platform-TenantId as "gorilla"
    When I create a set of debit and credit party
    When I call the create payer client endpoint
    Then I call the create savings product endpoint
    When I call the create savings account endpoint
    Then I call the debit interop identifier endpoint with MSISDN
    Then I approve the deposit with command "approve"
    When I activate the account with command "activate"
    Then I call the deposit account endpoint for "payer" with command "deposit" for amount 100
    Given I have Fineract-Platform-TenantId as "lion"
    When I call the create payer client endpoint
    Then I call the create savings product endpoint
    When I call the create savings account endpoint
    Then I call the credit interop identifier endpoint with MSISDN
    Then I approve the deposit with command "approve"
    When I activate the account with command "activate"
    Then I call the deposit account endpoint for "payee" with command "deposit" for amount 100
    Given I have tenant as "paymentBB2"
    Then I call the balance api for payer balance
    Given I have tenant as "payerFSP"
    Then I call the balance api for payee balance
    Given I have tenant as "paymentBB2"
    When I can create GSMATransferDTO with different payer and payee
    Then I call the GSMATransfer endpoint with expected status of 200
    And I should be able to parse transactionId from response
    Given I have tenant as "paymentBB2"
    When I call the transfer query endpoint with transactionId and expected status of 200
#    Then I will sleep for 5000 millisecond
    Given I have tenant as "paymentBB2"
    Then I call the balance api for payer balance after debit
    Given I have tenant as "payerFSP"
    Then I call the balance api for payee balance after credit

  @batch-teardown
  Scenario: Bulk Transfer with GSMA
    Given I have Fineract-Platform-TenantId as "gorilla"
    When I create and setup a "payer" with account balance of 100
    Given I have tenant as "paymentBB2"
    Then I call the balance api for payer balance
    When I create and setup a "payee" with id "1" and account balance of 10
    Given I have tenant as "payerFSP"
    Then I call the balance api for payee "1" balance
    Then Create a csv file with file name "batchTransactionGsma.csv"
    Then add row to csv with current payer and payee, payment mode as "gsma" and transfer amount 10 and id 0
    When I create and setup a "payee" with id "2" and account balance of 20
    Given I have tenant as "payerFSP"
    Then I call the balance api for payee "2" balance
    Then add row to csv with current payer and payee, payment mode as "gsma" and transfer amount 5 and id 1
    When I create and setup a "payee" with id "3" and account balance of 30
    Given I have tenant as "payerFSP"
    Then I call the balance api for payee "3" balance
    Then add last row to csv with current payer and payee, payment mode as "gsma" and transfer amount 1 and id 2
    Given I have tenant as "paymentBB2"
    And I have the demo csv file "batchTransactionGsma.csv"
    And I generate clientCorrelationId
    And I have private key
    And I generate signature
    When I call the batch transactions endpoint with expected status of 202
    And I am able to parse batch transactions response
    And I fetch batch ID from batch transaction API's response
    When I call the batch summary API for gsma with expected status of 200 with total 3 txns
    Then I should get non empty response
    Then I am able to parse batch summary response
    And Status of transaction is "COMPLETED"
    And I should have matching total txn count and successful txn count in response
    Given I have tenant as "paymentBB2"
    Then I call the balance api for payer balance after debit
    Given I have tenant as "payerFSP"
    Then I call the balance api for payee with id "1" balance after credit
    Given I have tenant as "payerFSP"
    Then I call the balance api for payee with id "2" balance after credit
    Given I have tenant as "payerFSP"
    Then I call the balance api for payee with id "3" balance after credit
