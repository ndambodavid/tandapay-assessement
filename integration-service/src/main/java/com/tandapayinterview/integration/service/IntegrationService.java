package com.tandapayinterview.integration.service;

import com.tandapayinterview.integration.mapper.PaymentRequestMapper;
import com.tandapayinterview.integration.model.PaymentRequest;
import com.tandapayinterview.integration.repository.PaymentRequestRepository;
import com.tandapayinterview.integration.request.GatewayRequest;
import com.tandapayinterview.integration.response.AsyncGwResponse;
import com.tandapayinterview.integration.response.AuthResponse;
import com.tandapayinterview.integration.response.SyncGwResponse;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class IntegrationService {

    private final PaymentRequestRepository paymentRequestRepository;
    private final PaymentRequestMapper paymentRequestMapper;

    private final String consumerKey = "YOUR_CONSUMER_KEY";
    private final String consumerSecret = "YOUR_CONSUMER_SECRET";

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
            String accessToken = String.valueOf(getAccessToken());
            System.out.println("Access Token: " + accessToken);

            GatewayRequest gatewayRequest = getGatewayRequest(paymentRequest);

            // send b2c payment request
            sendPaymentRequest(accessToken, gatewayRequest).subscribe(( response -> {
                System.out.println("Payment Response: " + response);
                
                // update payment request status
                mergePaymentRequest(paymentRequest, response);
                paymentRequestRepository.save(paymentRequest);

            }));

        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }

    /**
     * build gateway request payload
     * @param paymentRequest payment request instance
     */
    private static @NotNull GatewayRequest getGatewayRequest(PaymentRequest paymentRequest) {
        String securityCredential = "o3bz3tfnyOJ4vPBlHoxjwiBBVcCENwD+XMayuDgXE7zE38qhPCaD4/7/zJvTS5jWiuKFYGk4mLZGOZykNDxbV7+G30jNw9LC9F3b4gPIHRM5KIzmYgYoVB1pfahItMD66SxRhoUr4KKRW4sZg7Vz+naoLm4nNXBmCpASRY2hQJUzb5yIbI98xMRWrZpEHr8ubdVs4APGyBinEuteqYGNhEQetGWuyo/95CnE8W3A+MORPvvYJyQZuXm4JgGzPB/JtROQDHu1IU8bbUsJ3hWXyFOM8VCzlBo3wtMeyz+H2Kp0zEbae0OLjGpdo/nitZwpl615yloUaKxq+Sw4dFk1jQ==";

        return new GatewayRequest(
                paymentRequest.getPaymentId(),
                "testapi",
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
     * update payment Request information with information from Payment Gateway
     * @param asyncGwResponse gateway response with information from
     */
    public void updatePayment(AsyncGwResponse asyncGwResponse) {
        // fetch payment request from log
        var paymentRequest = this.paymentRequestRepository.findById(asyncGwResponse.getResult().getOriginatorConversationID())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Cannot update payment:: No payment found with the provided ID: %s", asyncGwResponse.getResult().getOriginatorConversationID())
                ));

        // update payment request resultDesc, status and reference
        mergePaymentRequest(paymentRequest, asyncGwResponse);
        paymentRequestRepository.save(paymentRequest);

        //TODO: publish payment response message to gateway-response-topic if gateway response is a success

        // delete payment request instance if payment is success
        if (asyncGwResponse.getResult().getResultCode() == 0) {
            paymentRequestRepository.deleteById(asyncGwResponse.getResult().getOriginatorConversationID());
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
                .map(AuthResponse::getAccess_token);
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
}
