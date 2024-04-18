// DialogflowManager.java
package com.example.chatbot.helper;

import android.content.Context;
import android.util.Log;

import com.example.chatbot.App;
import com.example.chatbot.R;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.dialogflow.v2.*;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;

public class DialogflowManager {
    private static final String AGENT_NAME = "test-agent";
    private SessionsClient sessionsClient;
    private SessionName sessionName;

    private static final String PROJECT_ID = "test-agent-funw";

    public DialogflowManager(Context context) throws Exception {
        try {

            InputStream stream = context.getResources().openRawResource(R.raw.chat1);
            GoogleCredentials credentials = GoogleCredentials.fromStream(stream);
            String projectId = ((ServiceAccountCredentials) credentials).getProjectId();

            SessionsSettings.Builder settingsBuilder = SessionsSettings.newBuilder();
            SessionsSettings sessionsSettings = settingsBuilder.setCredentialsProvider(
                    FixedCredentialsProvider.create(credentials)).build();
            sessionsClient = SessionsClient.create(sessionsSettings);
            String uuid = UUID.randomUUID().toString();
            sessionName = SessionName.of(projectId, uuid);
        } catch (Exception e) {
            Log.e("send_message dialog",e.getMessage()+"");
            throw new IOException("Error initializing DialogflowManager: " + e.getMessage());
        }
    }

    private String generateSessionId() {
        return UUID.randomUUID().toString();
    }

    public String sendQuery(String message) {
        try {
            if (sessionName == null) {
                throw new IllegalStateException("SessionName is not initialized");
            }
            TextInput.Builder textInput = TextInput.newBuilder()
                    .setText(message)
                    .setLanguageCode("en-US");

            QueryInput queryInput = QueryInput.newBuilder()
                    .setText(textInput)
                    .build();

            DetectIntentRequest detectIntentRequest = DetectIntentRequest.newBuilder()
                    .setSession(sessionName.toString())
                    .setQueryInput(queryInput)
                    .build();

            DetectIntentResponse response = sessionsClient.detectIntent(detectIntentRequest);
            QueryResult queryResult = response.getQueryResult();
            Log.d("send_message",queryResult.getFulfillmentText());
            return queryResult.getFulfillmentText();
        } catch (Exception e) {
            //e.printStackTrace();
            Log.e("send_message", Objects.requireNonNull(e.getMessage()));

            return "Error: " + e.getMessage();
        }
    }
}
