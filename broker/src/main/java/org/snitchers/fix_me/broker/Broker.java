package org.snitchers.fix_me.broker;

import org.snitchers.fix_me.utilities.Gui;
import org.snitchers.fix_me.utilities.Handler;

import javax.swing.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Broker implements Runnable{
    private final static Gui brokerGui = new Gui("Fix-me Broker");
    private final String host;
    private final int port;
    int tempId;
    int id;

    Broker(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void run() {

        SwingUtilities.invokeLater(brokerGui::run);
        brokerGui.appendChat("");
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
        socketChannel.register(selector, SelectionKey.OP_CONNECT);

        Handler<SelectionKey> connectHandler = new ConnectHandler(brokerGui);
        Handler<SelectionKey> readHandler = new ReadHandler(brokerGui, this);
        Handler<SelectionKey> writeHandler = new WriteHandler(brokerGui, this);
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