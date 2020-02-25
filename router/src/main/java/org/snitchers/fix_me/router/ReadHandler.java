package org.snitchers.fix_me.router;

import org.snitchers.fix_me.utilities.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.snitchers.fix_me.router.MessageGenerator.*;
import static org.snitchers.fix_me.utilities.MessagePrefix.SOH;

public class ReadHandler extends DecoratedHandler implements Handler<SelectionKey> {
    private static int id = DB_Handler.id;
    private Gui routerGui;
    private Handler<String> messageHandler;
    private ExecutorService executor = Executors.newFixedThreadPool(20);

    ReadHandler(ConcurrentMap<SocketChannel, Integer> brokers, Gui routerGui, Handler<String> messageHandler) {
        super(brokers);
        this.routerGui = routerGui;
        this.messageHandler = messageHandler;
    }

    @Override
    public void handle(SelectionKey selectionKey) throws IOException {
        SocketChannel sc = (SocketChannel) selectionKey.channel();
        ByteBuffer bb = ByteBuffer.allocate(1024);
        sc.read(bb);
        String result = new String(bb.array()).trim();

        if (result.length() <= 0) {
            brokers.remove(sc);
            sc.close();
            routerGui.appendChat("Connection closed... \nRouter will keep running. Try running another client to re-establish connection");
            return;
        }

        String type = (result.split(MessagePrefix.SOH)[3]).substring(3);

        if (type.equalsIgnoreCase("register")){
            brokers.put(sc, ++id);
            Router.messages.add(createRegisterResponse(id));
            routerGui.appendChat(result);
            routerGui.appendChat("Registered user: [" + sc.getRemoteAddress() + " - " + id + "]");
            selectionKey.interestOps(SelectionKey.OP_WRITE);
            Router.db_handler.insertClient(id, 10000);
        } else if(type.equalsIgnoreCase("logon")) {
            int id = Integer.parseInt(result.split(SOH)[0].substring(4));

            routerGui.appendChat(result);
            if(brokers.containsValue(id)) {
                brokers.put(sc, id);
                Router.messages.add(createRejectFix(id, 100000, "Client already logged in"));
                routerGui.appendChat("Client : [" + sc.getRemoteAddress() + " - " + id + "] already logged in");
            } else if (Router.db_handler.checkClientId(id) && (id != 100000 || sc.getLocalAddress().toString().contains("1:5001"))) {
                brokers.put(sc, id);
                Router.messages.add(createLogonResponse(id));
                routerGui.appendChat("Client : [" + sc.getRemoteAddress() + " - " + id + "] logged in");
            } else {
                brokers.put(sc, id);
                Router.messages.add(createRejectFix(id, 100000, "The Id does not exist, restart the programme and insert the correct ID"));
                routerGui.appendChat("Client : [" + sc.getRemoteAddress() + " - " + id + "] failed to log in");
            }
            selectionKey.interestOps(SelectionKey.OP_WRITE);
            Router.db_handler.insertFixMessage(id, result);
        } else {
            executor.submit(() ->
            {
                try {
                    messageHandler.handle(result);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            routerGui.appendChat("Message received: " + result + " - from - " +result.split(SOH)[0] + "  Message length = " + result.length());
            selectionKey.interestOps(SelectionKey.OP_WRITE);
        }
    }
}
