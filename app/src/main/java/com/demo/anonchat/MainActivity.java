package com.demo.anonchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    RecyclerView chatWindow;
    Button sendButton;
    EditText inputMessage;
    MessageController controller;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chatWindow =findViewById(R.id.chatWindow);
        sendButton=findViewById(R.id.sendMessage);
        inputMessage =findViewById(R.id.inputMessage);
        controller = new MessageController();
        controller.setIncomingLayout(R.layout.incoming_message)
                .setOutgoingLayout(R.layout.message)
                .setMessageTextId(R.id.messageText)
                .setMessageTimeId(R.id.messageDate)
                .setUserNameId(R.id.userName)
                .appendTo(chatWindow,this);


        final Server server = new Server(new Consumer<Pair<String, String>>() {
            @Override
            public void accept(final Pair<String, String> pair) {
               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       controller.addMessage(
                               new MessageController.Message(
                                       pair.second,
                                       pair.first,
                                       false));

                   }
               });

            }
        });

        server.connect();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = inputMessage.getText().toString();
                controller.addMessage(
                        new MessageController.Message(text,
                                "Никто",
                                true));
                inputMessage.setText("");
                server.sendMessage(text);
            }
        });

        Context context = getApplicationContext();
        CharSequence text = "Hello toast!";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

    }
}
