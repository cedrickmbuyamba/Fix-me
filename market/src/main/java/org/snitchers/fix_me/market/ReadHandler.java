package org.snitchers.fix_me.market;

import org.snitchers.fix_me.utilities.Gui;
import org.snitchers.fix_me.utilities.Handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import static org.snitchers.fix_me.utilities.MessagePrefix.SOH;

public class ReadHandler implements Handler<SelectionKey> {
    private Gui marketGui;

    ReadHandler(Gui marketGui) {
        this.marketGui = marketGui;
    }

    @Override
    public void handle(SelectionKey selectionKey) throws IOException {
        ProcessTransaction processTransaction = new ProcessTransaction();
        SocketChannel sc = (SocketChannel) selectionKey.channel();
        ByteBuffer bb = ByteBuffer.allocate(1024);
        sc.read(bb);
        String result = new String(bb.array()).trim();
        System.out.println(result);
        if (result.length() <= 0) {
            sc.close();
            marketGui.appendChat("The Connection is lost... Try restarting the connection ...");
            return;
        }

        if (result.split(SOH).length == 1) {
            Market.tempId = Integer.parseInt(result);
            selectionKey.interestOps(SelectionKey.OP_WRITE);
        } else if (!result.split(SOH)[3].substring(3).equalsIgnoreCase("loggedIn")){
            processTransaction.handle(result);
            marketGui.appendAvail(Market.market);
            selectionKey.interestOps(SelectionKey.OP_WRITE);
        }
        marketGui.appendChat("Message received from Central component: " + result);
        bb.clear();
    }
}
