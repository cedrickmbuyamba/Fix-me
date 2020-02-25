package org.snitchers.fix_me.market;

import org.snitchers.fix_me.utilities.Gui;
import org.snitchers.fix_me.utilities.Handler;

import javax.swing.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;

public class Market implements Runnable{
    private final static Gui marketGui = new Gui("Fix-me Market");
    static String message;
    private final String host;
    private final int port;
    static int tempId;
    int id;
    static ArrayList<String> market = new MarketGenerator().createMarket();

    Market(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void run() {
        SwingUtilities.invokeLater(marketGui::run);
        marketGui.appendChat("\n");
        marketGui.appendAvail(market);

        try {
            handle();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void handle() throws IOException {
        Selector selector = Selector.open();
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(new InetSocketAddress(this.host, this.port));
        socketChannel.register(selector, SelectionKey.OP_CONNECT | SelectionKey.OP_READ | SelectionKey.OP_WRITE);

        Handler<SelectionKey> connectHandler = new ConnectHandler(marketGui);
        Handler<SelectionKey> readHandler = new ReadHandler(marketGui);
        Handler<SelectionKey> writeHandler = new WriteHandler(marketGui, this);
        SelectionKey key = null;

        while (true) {
            if (selector.select() > 0) {
                Iterator iterator = selector.selectedKeys().iterator();

                while (iterator.hasNext()) {
                    key = (SelectionKey) iterator.next();
                    iterator.remove();
                }

                assert key != null;
                if (key.isConnectable()){
                    try {
                        connectHandler.handle(key);
                    } catch (Exception ex) {
                        key.cancel();
                        break;
                    }
                } else if (key.isReadable()){
                    readHandler.handle(key);
                } else if (key.isWritable()){
                    writeHandler.handle(key);
                }
            }
        }
        socketChannel.close();
    }
}
