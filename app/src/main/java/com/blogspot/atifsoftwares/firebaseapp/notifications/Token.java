package com.blogspot.atifsoftwares.firebaseapp.notifications;

public class Token {
    /*An FCM Token, or much commonly known as a registrationToken.
    An ID issued by the GCMconnection servers to the client app that allows it to receive messages*/

    String token;

    public Token(String token) {
        this.token = token;
    }

    public Token() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
