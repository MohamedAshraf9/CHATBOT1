// MainActivity.java
package com.example.chatbot;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatbot.helper.DialogflowManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerViewChat;
    EditText editTextMessage;
    ImageButton btnSend;

    List<Message> messageList;
    MessageAdapter messageAdapter;

    private DialogflowManager dialogflowManager;
    private static final int REQUEST_CAMERA_PERMISSION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }

        recyclerViewChat = findViewById(R.id.recyclerViewChat);
        editTextMessage = findViewById(R.id.editTextMessage);
        btnSend = findViewById(R.id.send_btn);
        messageList = new ArrayList<>();

        try {
            dialogflowManager = new DialogflowManager(this);
        } catch (Exception e) {
            Toast.makeText(this, "Error initializing DialogflowManager", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        messageAdapter = new MessageAdapter(messageList, this);
        recyclerViewChat.setAdapter(messageAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        recyclerViewChat.setLayoutManager(llm);

        btnSend.setOnClickListener((v) -> {
            String message = editTextMessage.getText().toString().trim();
            if (!message.isEmpty()) {
                addToChat(message, Message.SENT_BY_USER);
                sendMessageToBot(message);
            } else {
                Toast.makeText(MainActivity.this, "Please enter a message", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessageToBot(String message) {
        if (dialogflowManager != null) {
            String botReply = dialogflowManager.sendQuery(message);
            addToChat(botReply, Message.SENT_BY_BOT);
        } else {
            Toast.makeText(this, "Error: DialogflowManager is not initialized", Toast.LENGTH_SHORT).show();
        }
    }

    public void addToChat(String message, String sentBy) {
        runOnUiThread(() -> {
            messageList.add(new Message(message, sentBy));
            messageAdapter.notifyDataSetChanged();
            recyclerViewChat.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                } else {
                    // Permission denied
                    Toast.makeText(this, "Camera permission is required to use this feature", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
