package org.snitchers.fix_me.broker;

public class Main {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 5000;
        Broker broker = new Broker(host, port);
        broker.run();
    }
}
