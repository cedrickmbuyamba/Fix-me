package org.snitchers.fix_me.router;

public class Main {
    public static void main(String[] args) {
        String host = "localhost";
        int brokerPort = 5000;
        int marketPort = 5001;

        Thread marketRouter = new Thread(new Router(host, marketPort));
        Thread brokerRouter = new Thread(new Router(host, brokerPort));

        marketRouter.start();
        brokerRouter.start();
    }
}
