package org.snitchers.fix_me.market;

public class Main {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 5001;
        Market market = new Market(host, port);
        market.run();
    }
}
