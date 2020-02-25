package org.snitchers.fix_me.router;

import org.snitchers.fix_me.utilities.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentMap;

public class AcceptHandler extends DecoratedHandler implements Handler<SelectionKey> {
    private Gui routerGui;
    private int id = 100000;

    AcceptHandler(ConcurrentMap<SocketChannel, Integer> brokers, Gui routerGui) {
        super(brokers);
        this.routerGui = routerGui;
    }

    @Override
    public void handle(SelectionKey selectionKey) throws IOException {
        ServerSocketChannel ssc = (ServerSocketChannel) selectionKey.channel();
        SocketChannel sc = ssc.accept();
        routerGui.appendChat("Connection Accepted: " + sc.getLocalAddress());
        sc.configureBlocking(false);

        sc.register(selectionKey.selector(), SelectionKey.OP_WRITE);
        ByteBuffer bb = ByteBuffer.wrap(String.valueOf(++id).getBytes());
        sc.write(bb);
        sc.register(selectionKey.selector(), SelectionKey.OP_READ);
    }
}