package com.tandapayinterview.integration.service;

import com.tandapayinterview.integration.kafka.CoreResponsePayload;
import com.tandapayinterview.integration.kafka.CoreResponseProducer;
import com.tandapayinterview.integration.model.PaymentRequest;
import com.tandapayinterview.integration.repository.PaymentRequestRepository;
import com.tandapayinterview.integration.request.CheckTransactionStatusRequest;
import com.tandapayinterview.integration.request.GatewayRequest;
import com.tandapayinterview.integration.response.AsyncGwResponse;
import com.tandapayinterview.integration.response.AuthResponse;
import com.tandapayinterview.integration.response.CheckTransactionStatusResponse;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.Objects;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
@Slf4j
public class IntegrationService {

    private final PaymentRequestRepository paymentRequestRepository;
    private final CoreResponseProducer coreResponseProducer;

    private final String consumerKey = "uhHoA1d6V0N718IZWthUBEhd1gwD6GiAuePAembL1xqJBbxI";
    private final String consumerSecret = "bVPFr82qvN5GoSd7NXjCLTV4cpY63qJBVuaxj4pWqPiuoNz36TnhfgqFdRgX4m5u";
    private static final String securityCredential = "o3bz3tfnyOJ4vPBlHoxjwiBBVcCENwD+XMayuDgXE7zE38qhPCaD4/7/zJvTS5jWiuKFYGk4mLZGOZykNDxbV7+G30jNw9LC9F3b4gPIHRM5KIzmYgYoVB1pfahItMD66SxRhoUr4KKRW4sZg7Vz+naoLm4nNXBmCpASRY2hQJUzb5yIbI98xMRWrZpEHr8ubdVs4APGyBinEuteqYGNhEQetGWuyo/95CnE8W3A+MORPvvYJyQZuXm4JgGzPB/JtROQDHu1IU8bbUsJ3hWXyFOM8VCzlBo3wtMeyz+H2Kp0zEbae0OLjGpdo/nitZwpl615yloUaKxq+Sw4dFk1jQ==";
    private static final String initiatorName = "linmik420@gmail.com";

    String authUrl = "https://sandbox.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials";
    String credentials = consumerKey + ":" + consumerSecret;


    /**
     * Initiate payment request to payment gateway and log payement request
     * @param  paymentRequest payment request instance
     */
    @Async
    public void sendPaymentRequestToGw(PaymentRequest paymentRequest) {

        try {
            // get access token from daraja auth api
            getAccessToken().subscribe(accessToken -> {
                System.out.println("Access Token: " + accessToken);

                // build gateway request
                GatewayRequest gatewayRequest = getGatewayRequest(paymentRequest);

                // send b2c payment request
                sendPaymentRequest(accessToken, gatewayRequest).subscribe(( response -> {
                    System.out.println("Payment Sync Response: " + response);

                    // update payment request status
                    mergePaymentRequest(paymentRequest, response);
                    paymentRequestRepository.save(paymentRequest);

                }));
            });


        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }

    /**
     * initiate check transaction status request to gateway
     * @param paymentRequest payment request instance
     */
    @Async
    public void sendCheckTransactionRequestToGw(PaymentRequest paymentRequest) {

        try {
            // get access token from daraja auth api
            getAccessToken().subscribe(accessToken -> {
                System.out.println("Access Token: " + accessToken);

                CheckTransactionStatusRequest checkTransactionStatusRequest = getCheckTransactionRequest(paymentRequest);

                // send b2c payment request
                sendTransactionStatusRequest(accessToken, checkTransactionStatusRequest).subscribe(( response -> {
                    System.out.println("Payment Sync Response: " + response);
                    log.info("Check Transaction Sync Response= < {} >", response);
                }));

            });

        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }

    /**
     * build gateway request payload
     * @param paymentRequest payment request instance
     */
    private static @NotNull GatewayRequest getGatewayRequest(PaymentRequest paymentRequest) {

        return new GatewayRequest(
                paymentRequest.getPaymentId(),
                initiatorName,
                securityCredential,
                "PromotionPayment",
                paymentRequest.getAmount().toString(),
                "600996",
                paymentRequest.getMobileNumber(),
                "christmas bonus promo",
                "https://mydomain.com/b2c/queue",
                "https://mydomain.com/b2c/result",
                "Christmas"
        );
    }

    /**
     * build check transaction request payload
     * @param paymentRequest payment request instance
     */
    private static @NotNull CheckTransactionStatusRequest getCheckTransactionRequest(PaymentRequest paymentRequest) {

        return new CheckTransactionStatusRequest(
                paymentRequest.getPaymentId(),
                initiatorName,
                securityCredential,
                "TransactionStatusQuery",
                paymentRequest.getReference(),
                "600996",
                "4",
                "ok",
                "https://mydomain.com/b2c/queue",
                "https://mydomain.com/b2c/result",
                "Christmas"
        );
    }

    /**
     * update payment Request information with information from Payment Gateway and send gateway update to core
     * @param asyncGwResponse gateway payment result response
     */
    public void handlePaymentRequestResponse(AsyncGwResponse asyncGwResponse) throws Exception {
        // fetch payment request from log
        var paymentRequest = this.paymentRequestRepository.findById(asyncGwResponse.getResult().getOriginatorConversationID())
                .orElseThrow(() -> new Exception(
                        format("Cannot update payment:: No payment found with the provided ID: %s", asyncGwResponse.getResult().getOriginatorConversationID())
                ));

        // update payment request resultDesc, status and reference
        mergePaymentRequest(paymentRequest, asyncGwResponse);
        paymentRequestRepository.save(paymentRequest);

        // publish payment response message to gateway-response-topic
        if (asyncGwResponse.getResult().getResultCode() == 0) {
            coreResponseProducer.sendGatewayResponseToCore(
                    new CoreResponsePayload(
                            asyncGwResponse.getResult().getOriginatorConversationID(),
                            asyncGwResponse.getResult().getTransactionID(),
                            "success"
                    )
            );
        }else {
            coreResponseProducer.sendGatewayResponseToCore(
                    new CoreResponsePayload(
                            asyncGwResponse.getResult().getOriginatorConversationID(),
                            asyncGwResponse.getResult().getTransactionID(),
                            "failed"
                    )
            );
        }

        // delete payment request instance if payment is success
        if (asyncGwResponse.getResult().getResultCode() == 0) {
            paymentRequestRepository.deleteById(asyncGwResponse.getResult().getOriginatorConversationID());
        }
    }

    /**
     * update payment Request information with information from Payment Gateway and send gateway update to core
     * @param transactionStatusResponse gateway transaction status response
     */
    public void handleTransactionStatusResponse(CheckTransactionStatusResponse transactionStatusResponse) throws Exception {
        // fetch payment request from log
        var paymentRequest = this.paymentRequestRepository.findById(transactionStatusResponse.getResult().getOriginatorConversationID())
                .orElseThrow(() -> new Exception(
                        format("Cannot update payment:: No payment found with the provided ID: %s", transactionStatusResponse.getResult().getOriginatorConversationID())
                ));

        // update payment request resultDesc, status and reference

        if (transactionStatusResponse.getResult().getResultType() == 0) {

            // publish payment response message to gateway-response-topic
            coreResponseProducer.sendGatewayResponseToCore(
                    new CoreResponsePayload(
                            transactionStatusResponse.getResult().getOriginatorConversationID(),
                            transactionStatusResponse.getResult().getTransactionID(),
                            "success"
                    )
            );

            // delete payment request instance
            paymentRequestRepository.deleteById(transactionStatusResponse.getResult().getOriginatorConversationID());

        } else {
            // update log
            paymentRequest.setResultDesc(transactionStatusResponse.getResult().getResultDesc());
            paymentRequest.setReference(transactionStatusResponse.getResult().getTransactionID());

            paymentRequestRepository.save(paymentRequest);
        }

    }

    /**
     * update request based on gateway response
     * @param paymentRequest Payment Request Model
     * @param asyncGwResponse response from gateway
     */
    private void mergePaymentRequest(PaymentRequest paymentRequest, AsyncGwResponse asyncGwResponse) {

        // check if payment request was successfull; set result description from responnse
        if (Objects.equals(asyncGwResponse.getResponseCode(), "0")) {
            if (StringUtils.isNotBlank(asyncGwResponse.getResponseDescription())) {
                paymentRequest.setResultDesc(asyncGwResponse.getResponseDescription());
            }

            // update payment request status to submitted
            paymentRequest.setStatus("submitted");
        }

        // set status to failed if request submission failed
        if (Objects.isNull(asyncGwResponse.getResponseCode())) {
            paymentRequest.setStatus("failed");
        }

        // check if the request to gateway was successful; update result description with response result description
        if (asyncGwResponse.getResult() != null) {
            if (asyncGwResponse.getResult().getResultCode() == 0) {
                paymentRequest.setResultDesc(asyncGwResponse.getResult().getResultDesc());

                // update payment request status to success
                paymentRequest.setStatus("success");
                
                // set the reference to transactionId from GW response
                paymentRequest.setReference(asyncGwResponse.getResult().getTransactionID());
                
            } else {
                paymentRequest.setResultDesc(asyncGwResponse.getResult().getResultDesc());

                // update payment request status to failed
                paymentRequest.setStatus("failed");

                // set the reference to transactionId from GW response
                paymentRequest.setReference(asyncGwResponse.getResult().getTransactionID());
            }
        }
    }

    /**
     * get access token from daraja api
     */
    private Mono<String> getAccessToken() {

        var encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

        WebClient client = WebClient.builder()
                .baseUrl(authUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + encodedCredentials)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        return client.get()
                .retrieve()
                .bodyToMono(AuthResponse.class)
                .map(AuthResponse::getAccessToken);
    }

    /**
     * submit payment request to gateway
     * @param accessToken daraja api access token
     * @param gatewayRequest daraja api payment request
     */
    public static Mono<AsyncGwResponse> sendPaymentRequest(String accessToken, GatewayRequest gatewayRequest) {
        String paymentUrl = "https://sandbox.safaricom.co.ke/mpesa/b2c/v3/paymentrequest";

        WebClient client = WebClient.builder()
                .baseUrl(paymentUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        return client.post()
                .body(Mono.just(gatewayRequest), PaymentRequest.class)
                .retrieve()
                .bodyToMono(AsyncGwResponse.class);
    }

    /**
     * submit check transaction status request to gateway
     * @param accessToken daraja api access token
     * @param checkTransactionStatusRequest daraja api transaction status request
     */
    public static Mono<CheckTransactionStatusResponse> sendTransactionStatusRequest(String accessToken, CheckTransactionStatusRequest checkTransactionStatusRequest) {
        String paymentUrl = "https://sandbox.safaricom.co.ke/mpesa/transactionstatus/v1/query";

        WebClient client = WebClient.builder()
                .baseUrl(paymentUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        return client.post()
                .body(Mono.just(checkTransactionStatusRequest), CheckTransactionStatusRequest.class)
                .retrieve()
                .bodyToMono(CheckTransactionStatusResponse.class);
    }
}
