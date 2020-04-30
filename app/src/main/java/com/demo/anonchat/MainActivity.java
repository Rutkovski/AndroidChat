package com.demo.anonchat;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    static String myName;
    static Server server;
    RecyclerView chatWindow;
    Button sendButton;
    EditText inputMessage;
    MessageController controller;
    TextView onlineUser;


    protected void getUsername(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter your name");//Задали заголовок
        final EditText input = new EditText(this);// создаем поле для ввода
        builder.setView(input);// отразить в диалоге
        builder.setPositiveButton("save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               myName = input.getText().toString();
               server.sendName(myName);
            }
        });
        builder.show();





    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        chatWindow =findViewById(R.id.chatWindow);
        sendButton=findViewById(R.id.sendMessage);
        inputMessage =findViewById(R.id.inputMessage);
        onlineUser = findViewById(R.id.onLine);

        controller = new MessageController();

        controller.setIncomingLayout(R.layout.incoming_message)
                .setOutgoingLayout(R.layout.message)
                .setMessageTextId(R.id.messageText)
                .setMessageTimeId(R.id.messageDate)
                .setUserNameId(R.id.userName)
                .appendTo(chatWindow,this);



        server = new Server(new Consumer<Pair<String, String>>() {
            @Override
            public void accept(final Pair<String, String> pair) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        controller.addMessage(
                                new MessageController.Message(pair.second, pair.first, false)

                        );

                    }
                });
            }
        }, new Consumer<Integer>() {
            @Override
            public void accept(final Integer userCount) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onlineUser.setText("Пользователей онлайн: "+userCount);

                    }
                });

            }
        }, new Consumer<String>() {
            @Override
            public void accept(final String s) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast(s);
                    }
                });


            }
        }
        );
        server.connect();
        getUsername();


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = inputMessage.getText().toString();
                controller.addMessage(
                        new MessageController.Message(text,
                                myName,
                                true));
                inputMessage.setText("");
                server.sendMessage(text);
            }
        });

    }
    public void showToast (String userName){
        Context context = this;
        CharSequence text = userName+" подключился к чату";
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        toast.show();

    }



}
