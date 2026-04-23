package com.app.trashmasters.SageMaker;


import com.app.trashmasters.bin.model.Bin;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sagemakerruntime.SageMakerRuntimeClient;
import software.amazon.awssdk.services.sagemakerruntime.model.InvokeEndpointRequest;
import software.amazon.awssdk.services.sagemakerruntime.model.InvokeEndpointResponse;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class SageMakerService {

    private final SageMakerRuntimeClient sageMakerClient;
    private final ObjectMapper objectMapper;

    // Make sure this matches your live AWS endpoint exactly!
    private final String ENDPOINT_NAME = "trashmasters-prediction-v6";

    public SageMakerService(SageMakerRuntimeClient sageMakerClient, ObjectMapper objectMapper) {
        this.sageMakerClient = sageMakerClient;
        this.objectMapper = objectMapper;
    }

    public Double predictFutureFillLevel(Map<String, Object> binData) {
        try {
            // 1. Convert the Map of data into a JSON string
            String payload = objectMapper.writeValueAsString(binData);
            SdkBytes body = SdkBytes.fromString(payload, StandardCharsets.UTF_8);

            // 2. Build the request to AWS
            InvokeEndpointRequest request = InvokeEndpointRequest.builder()
                    .endpointName(ENDPOINT_NAME)
                    .contentType("application/json")
                    .accept("application/json")
                    .body(body)
                    .build();

            // 3. Fire the request and wait for the AI to answer
            InvokeEndpointResponse response = sageMakerClient.invokeEndpoint(request);
            String responseJson = response.body().asUtf8String();

            // 4. Extract the predicted percentage from the JSON reply
            Map<String, Object> result = objectMapper.readValue(responseJson, Map.class);
            return (Double) result.get("predictedFillPercentage");

        } catch (Exception e) {
            System.err.println("❌ Failed to connect to SageMaker: " + e.getMessage());
            return null; // Handle gracefully if AWS is down or the endpoint is deleted
        }
    }

//    private final String ENDPOINT_NAME = "trashmasters-prediction-v4"; // AWS Endpoint Name
//    private final SageMakerRuntimeClient runtimeClient;
//
//    public SageMakerService() {
//        // Initializes the client using your default AWS credentials on your machine
//        this.runtimeClient = SageMakerRuntimeClient.builder()
//                .region(Region.US_WEST_2) // Make sure this matches your AWS Learner Lab region!
//                .build();
//    }
//
//    public Double getPrediction(Bin bin, int hoursAhead, double currentTempF) {
//        LocalDateTime now = LocalDateTime.now();
//
//        // 1. This MUST match the exact order of features in Python notebook:
//        // 'calculatedFillLevel', 'hours_ahead', 'dayOfWeek', 'hourOfDay', 'temperatureF', 'lat', 'lon'
//        String payload = String.format("%.1f,%d,%d,%d,%.1f,%f,%f",
//                bin.getFillLevel() != null ? bin.getFillLevel() : 0.0,
//                hoursAhead,
//                now.getDayOfWeek().getValue(),
//                now.getHour(),
//                currentTempF,
//                bin.getLocation().getLat(),
//                bin.getLocation().getLon()
//        );
//
//        // 2. Wrap it up for AWS
//        SdkBytes body = SdkBytes.fromString(payload, StandardCharsets.UTF_8);
//        InvokeEndpointRequest request = InvokeEndpointRequest.builder()
//                .endpointName(ENDPOINT_NAME)
//                .contentType("text/csv")
//                .body(body)
//                .build();
//
//        // 3. Call SageMaker and read the response
//        try {
//            InvokeEndpointResponse response = runtimeClient.invokeEndpoint(request);
//            String responseString = response.body().asUtf8String();
//
//            // XGBoost returns a string like "85.4321", so we parse it to a Double
//            return Double.parseDouble(responseString.trim());
//
//        } catch (Exception e) {
//            System.err.println("SageMaker prediction failed: " + e.getMessage());
//            return null; // Return null if AWS is down so we don't crash the whole app
//        }
//    }
}