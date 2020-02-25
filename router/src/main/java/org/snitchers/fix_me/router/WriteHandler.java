package org.snitchers.fix_me.router;

import org.snitchers.fix_me.utilities.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentMap;

import static org.snitchers.fix_me.utilities.MessagePrefix.SOH;

public class WriteHandler extends DecoratedHandler implements Handler<SelectionKey> {

    private Router router;

    WriteHandler(ConcurrentMap<SocketChannel, Integer> brokers, Router router) {
        super(brokers);
        this.router = router;
    }

    @Override
    public void handle(SelectionKey selectionKey) throws IOException {
        SocketChannel sc = (SocketChannel) selectionKey.channel();
        String msg, type;

        for(int i = 0; i < Router.messages.size(); i++){
            msg = Router.messages.get(i);
            type = msg.split(SOH)[3].substring(3);

            if (type.equalsIgnoreCase("registered") && brokers.containsKey(sc) && brokers.get(sc).equals(Integer.valueOf(msg.split(SOH)[0].substring(4)))) {
                ByteBuffer bb = ByteBuffer.wrap(msg.getBytes());
                sc.write(bb);
                //add message to db
//                router.db_handler.insertFixMessage("UYI");
                Router.messages.remove(msg);
                selectionKey.interestOps(SelectionKey.OP_READ);
                break;
            } else if (type.equalsIgnoreCase("loggedIn") && brokers.containsKey(sc) && brokers.get(sc).equals(Integer.valueOf(msg.split(SOH)[0].substring(4)))) {
                System.out.println(msg);
                ByteBuffer bb = ByteBuffer.wrap(msg.getBytes());
                sc.write(bb);
                //add message to db
                Router.messages.remove(msg);
                if (sc.getLocalAddress().toString().contains("1:5000"))
                    selectionKey.interestOps(SelectionKey.OP_READ);
                break;
            } else if(brokers.containsKey(sc) && brokers.get(sc).equals(Integer.valueOf(msg.split(SOH)[6].substring(3)))) {
                ByteBuffer bb = ByteBuffer.wrap(msg.getBytes());
                sc.write(bb);
                //add to db
                Router.messages.remove(msg);
                selectionKey.interestOps(SelectionKey.OP_READ);
                break;
            }
        }
    }
}
