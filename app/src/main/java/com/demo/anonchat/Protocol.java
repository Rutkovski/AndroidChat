package com.demo.anonchat;

import com.google.gson.Gson;

public class Protocol {
    // USER_STATUS - сообщать в онлайне или оффлайне
    // MESSAGE - сообщение
    // USER_NAME - сообщаем имя серверу


    public final static int USER_STATUS = 1;
    public final static int MESSAGE = 2;
    public final static int USER_NAME = 3;

    // USER_STATUS          1 {connected: false, user {name: "Pol", id 1212312312} }
    // MESSAGE              2{ encodedText: "Hello" , sender 121233123 }
    // USER_NAME            3 { name: "Jon"}

    static class UserStatus {
        private boolean connected;
        private User user;

        public UserStatus() {
        }

        public boolean isConnected() {
            return connected;
        }

        public void setConnected(boolean connected) {
            this.connected = connected;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }
    }
    static class User {
        private String name;
        private long id;

        public User() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }
    }



    static class Username {
        private String name;

        public Username(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    static class Message{
        public final static int GROUP_CHAT = 1;
        private long sender; // id отправителя
        private String encodedText;
        private long receiver = GROUP_CHAT;

        public Message(String encodedText) {
            this.encodedText = encodedText;
        }

        public long getSender() {
            return sender;
        }

        public void setSender(long sender) {
            this.sender = sender;
        }

        public String getEncodedText() {
            return encodedText;
        }

        public void setEncodedText(String encodedText) {
            this.encodedText = encodedText;
        }

        public long getReceiver() {
            return receiver;
        }

        public void setReceiver(long receiver) {
            this.receiver = receiver;
        }
    }

// будем вызывать при отправке имени на сервер
    public static String pacName (Username name){
     Gson g = new Gson(); // поможет запаковать в gson
        return USER_NAME+g.toJson(name); // 3{ name: "Jon"}
    }


 // Как получать с сверверка прочую информацию
    //Статусы пользователей, имена пользователей и собственно сообщения


   // узнать что за сообщение к нам пришло
    public static int getTyp(String json){
        if (json == null || json.length() == 0)
            return -1;
        return Integer.parseInt(json.substring(0,1));
    }


    public static String pacMessage (Message mess){
        Gson g = new Gson();
        return MESSAGE+g.toJson(mess);
    }
    public static Message unpackMessage(String json){
        Gson g = new Gson();
        return g.fromJson(json.substring(1), Message.class);
    }

    public static UserStatus unpackStatus (String json) {
        Gson g = new Gson();
        return g.fromJson(json.substring(1), UserStatus.class);
    }

}
