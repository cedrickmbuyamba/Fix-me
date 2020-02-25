package org.snitchers.fix_me.router;

import org.snitchers.fix_me.utilities.*;
import javax.swing.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Router implements Runnable {
    public static ConcurrentMap<SocketChannel, Integer> brokers = new ConcurrentHashMap<>();
    public static ArrayList<String> messages = new ArrayList<>();

    private final static Gui routerGui = new Gui("Fix-me Router");
    private final String host;
    private final int port;
    static DB_Handler db_handler = new DB_Handler();

    public Router(String host, int port){
        this.host = host;
        this.port = port;
    }

    @Override
    public void run() {
        SwingUtilities.invokeLater(routerGui::run);
        routerGui.appendChat("");

        try {
            handle();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void handle() throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(this.host, this.port));
        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        Handler<SelectionKey> acceptHandler = new AcceptHandler(brokers, routerGui);
        Handler<String> messageHandler = new MessageHandler(brokers);
        Handler<SelectionKey> readHandler = new ReadHandler(brokers, routerGui, messageHandler);
        Handler<SelectionKey> writeHandler = new WriteHandler(brokers, this);

        routerGui.appendChat(String.format("%s Connection available, The connection is pending...", this.port == 5000 ? "Broker" : "Market"));
        SelectionKey key = null;

        while (true) {
            if (selector.select() <= 0)
                continue;
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectedKeys.iterator();
            while (iterator.hasNext()) {
                key = iterator.next();
                iterator.remove();

                if (key.isAcceptable()) {
                    acceptHandler.handle(key);
                } else if (key.isReadable()) {
                    readHandler.handle(key);
                } else if (key.isWritable()){
                    writeHandler.handle(key);
                }
            }
        }
    }
}