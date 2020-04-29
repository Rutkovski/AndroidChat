package com.demo.anonchat;

import android.util.Log;
import android.util.Pair;

import androidx.core.util.Consumer;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private WebSocketClient client;


    private Map<Long, String> names = new ConcurrentHashMap<>();
    private Consumer<Pair<String,String>> onMessageReceived; //Коллбэки
    private Consumer<Integer> count;


    public Server(Consumer<Pair<String, String>> onMessageReceived) {
        this.onMessageReceived = onMessageReceived;
    }




    public void connect() {
        //Выполняем подключение к серверу
        //35.214.1.221:
        URI address = null;
        try {
             address = new URI("ws://35.214.1.221:8881");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }


        client = new WebSocketClient(address) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.i("SERVER", "Connected to server");
                sendName("Rut");
            }

            @Override
            public void onMessage(String json) {
                Log.i("SERVER","Got json from server: "+ json);
                int type = Protocol.getTyp(json);
                if (type == Protocol.MESSAGE){
                    displayIncoming(Protocol.unpackMessage(json));
                }
                if (type == Protocol.USER_STATUS){
                   updateStatus(Protocol.unpackStatus(json));

                }

            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.i("SERVER","Connection closed");

            }

            @Override
            public void onError(Exception ex) {
                Log.i("SERVER","ERROR:"+ ex.getLocalizedMessage());

            }
        };

        client.connect();

    }

    public void sendName(String name){
        Protocol.Username username = new Protocol.Username(name);
        if (client != null && client.isOpen() ){
        client.send(Protocol.pacName(username));
        }
    }

    public void sendMessage(String text){
        Protocol.Message mess = new Protocol.Message(text);
        if (client != null&& client.isOpen()) {
            client.send(Protocol.pacMessage(mess));
        }

    }

    private void updateStatus (Protocol.UserStatus status){
        // Запомнить что такой-то пользователь имеет такой-то статус (онлайн или офлайн)
       Protocol.User user = status.getUser();
       // При подключении кладем, при отключении удаляем
        if (status.isConnected()){
            names.put(user.getId(), user.getName());



        }
        else {
            names.remove(user.getId());


        }

    }

    private void displayIncoming(Protocol.Message message){
        String name = names.get(message.getSender());
        if (name == null){
            name = "Unnamed";
        }

        onMessageReceived.accept(
                new Pair<>(name, message.getEncodedText()) // Отправляем в MainActivity пришедшее сообщение


        );


    }




}
