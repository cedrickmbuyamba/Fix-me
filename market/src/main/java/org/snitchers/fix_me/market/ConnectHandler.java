package org.snitchers.fix_me.market;

import org.snitchers.fix_me.utilities.Gui;
import org.snitchers.fix_me.utilities.Handler;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class ConnectHandler implements Handler<SelectionKey> {
    private Gui marketGui;

    public ConnectHandler(Gui marketGui) {
        this.marketGui = marketGui;
    }

    @Override
    public void handle(SelectionKey selectionKey) throws IOException {
        SocketChannel sc = (SocketChannel) selectionKey.channel();

        while (sc.isConnectionPending()) {
            sc.finishConnect();
        }
        this.marketGui.appendChat("Connected to the Central Component");
        selectionKey.interestOps(SelectionKey.OP_READ);
    }
}
