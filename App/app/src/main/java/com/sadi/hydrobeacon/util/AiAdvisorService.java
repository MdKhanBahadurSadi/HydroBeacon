package com.sadi.hydrobeacon.util;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AiAdvisorService {
    private static final String API_KEY = "YOUR_DUMMY_GEMINI_API_KEY";
    private final GenerativeModelFutures model;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public AiAdvisorService() {
        GenerativeModel gm = new GenerativeModel("gemini-1.5-flash", API_KEY);
        model = GenerativeModelFutures.from(gm);
    }

    public interface AiResponseCallback {
        void onResponse(String advice);
        void onError(Throwable t);
    }

    public void getAdvice(double currentLevel, double dangerLevel, AiResponseCallback callback) {
        String prompt = String.format(
            "Current water level is %.2f meters. The danger level is %.2f meters. " +
            "Provide 3 short, actionable safety tips in Bengali for people living nearby. " +
            "Keep it concise and urgent if the level is high.", 
            currentLevel, dangerLevel
        );

        Content content = new Content.Builder()
                .addText(prompt)
                .build();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                callback.onResponse(result.getText());
            }

            @Override
            public void onFailure(Throwable t) {
                callback.onError(t);
            }
        }, executor);
    }
}
