package org.snitchers.fix_me.market;

import org.snitchers.fix_me.utilities.Gui;
import org.snitchers.fix_me.utilities.Handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class WriteHandler implements Handler<SelectionKey> {
    private Gui marketGui;
    private Market market;

    public WriteHandler(Gui marketGui, Market market) {
        this.marketGui = marketGui;
        this.market = market;
    }

    @Override
    public void handle(SelectionKey selectionKey) throws IOException {
        StringBuilder msg = new StringBuilder();

        if (market.id == 0){
            market.id = 100000;
            msg.append(MessageGenerator.createLogonFix(market.id));
        } else {
            msg.append(Market.message);
            Market.message = "";
        }

        System.out.println(msg);

        SocketChannel sc = (SocketChannel) selectionKey.channel();
        ByteBuffer bb = ByteBuffer.wrap(msg.toString().getBytes());
        sc.write(bb);
        selectionKey.interestOps(SelectionKey.OP_READ);
    }
}
