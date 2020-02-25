package org.snitchers.fix_me.broker;

import org.snitchers.fix_me.utilities.Gui;
import org.snitchers.fix_me.utilities.Handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class ConnectHandler implements Handler<SelectionKey> {
    private Gui brokerGui;

    ConnectHandler(Gui brokerGui) {
        this.brokerGui = brokerGui;
    }

    @Override
    public void handle(SelectionKey selectionKey) throws IOException {
        SocketChannel sc = (SocketChannel) selectionKey.channel();

        while (sc.isConnectionPending()) {
            sc.finishConnect();
        }
        this.brokerGui.appendChat("Connected to the central Component ...");
//        selectionKey.interestOps(SelectionKey.OP_WRITE);
        selectionKey.interestOps(SelectionKey.OP_READ);
    }
}
