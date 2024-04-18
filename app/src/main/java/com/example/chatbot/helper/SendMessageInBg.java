package com.example.chatbot.helper;

import android.util.Log;
import androidx.annotation.NonNull;
import com.example.chatbot.interfaces.BotReply;
import com.google.cloud.dialogflow.v2.DetectIntentRequest;
import com.google.cloud.dialogflow.v2.DetectIntentResponse;
import com.google.cloud.dialogflow.v2.QueryInput;
import com.google.cloud.dialogflow.v2.SessionName;
import com.google.cloud.dialogflow.v2.SessionsClient;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SendMessageInBg {

    private final SessionName session;
    private final SessionsClient sessionsClient;
    private final QueryInput queryInput;
    private final String TAG = "SendMessageInBg";
    private final BotReply botReply;

    public SendMessageInBg(BotReply botReply, SessionName session, SessionsClient sessionsClient,
                           QueryInput queryInput) {
        this.botReply = botReply;
        this.session = session;
        this.sessionsClient = sessionsClient;
        this.queryInput = queryInput;
    }

    public void executeInBackground() {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                DetectIntentRequest detectIntentRequest =
                        DetectIntentRequest.newBuilder()
                                .setSession(session.toString())
                                .setQueryInput(queryInput)
                                .build();
                DetectIntentResponse response = sessionsClient.detectIntent(detectIntentRequest);
                // Handle return response here
                botReply.callback(response);
            } catch (Exception e) {
                Log.d(TAG, "Error executing background task: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}

