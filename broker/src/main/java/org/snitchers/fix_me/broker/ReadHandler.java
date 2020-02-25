package org.snitchers.fix_me.broker;

import org.snitchers.fix_me.utilities.Gui;
import org.snitchers.fix_me.utilities.Handler;
import org.snitchers.fix_me.utilities.MessagePrefix;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import static org.snitchers.fix_me.utilities.MessagePrefix.SOH;

public class ReadHandler implements Handler<SelectionKey> {
    private Gui brokerGui;
    private Broker broker;

    ReadHandler(Gui brokerGui, Broker broker) {
        this.brokerGui = brokerGui;
        this.broker = broker;
    }

    @Override
    public void handle(SelectionKey selectionKey) throws IOException {
        SocketChannel sc = (SocketChannel) selectionKey.channel();
        ByteBuffer bb = ByteBuffer.allocate(1024);
        sc.read(bb);
        //if read failed return a message
        String result = new String(bb.array()).trim();

        if (result.length() <= 0) {
            sc.close();
            brokerGui.appendChat("The Connection is lost... Try restarting the connection ...");
            return;
        }

        if (result.split(SOH).length == 1){
            broker.tempId = Integer.parseInt(result);
        } else if (result.split(SOH)[3].substring(3).equals("registered")){
            broker.id = Integer.parseInt(result.split(SOH)[4].substring(4));//assign id to the broker after registering
        } else if (result.split(SOH)[3].substring(3).equals("loggedIn")){
            this.brokerGui.removeLogin();
        }
        brokerGui.appendChat("Message received from Server: " + result);
        selectionKey.interestOps(SelectionKey.OP_WRITE);
        bb.clear();
    }
}
