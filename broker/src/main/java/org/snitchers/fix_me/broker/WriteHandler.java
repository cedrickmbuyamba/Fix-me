package org.snitchers.fix_me.broker;

import org.snitchers.fix_me.utilities.Gui;
import org.snitchers.fix_me.utilities.Handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

import static org.snitchers.fix_me.broker.MessageGenerator.*;

public class WriteHandler implements Handler<SelectionKey> {
    private Gui brokerGui;
    private Broker broker;
    private ArrayList<String> tradeDetails;
    WriteHandler(Gui brokerGui, Broker broker) {
        this.brokerGui = brokerGui;
        this.broker = broker;
    }

    @Override
    public void handle(SelectionKey selectionKey) throws IOException {
        StringBuilder msg = new StringBuilder();
        if (broker.id == 0){
            String brokerID = brokerGui.getBrokerID();
            if(brokerID.equals("register")){
                String fixMessage = createRegisterFix(broker.tempId);
                msg.append(fixMessage);
                this.brokerGui.removeLogin();
            } else {
                try {
                    String fixMessage = createLogonFix(Integer.parseInt(brokerID));
                    if (brokerID.equals("100000")){
                        throw new NumberFormatException();
                    }
                    msg.append(fixMessage);
                    broker.id = Integer.parseInt(brokerID);
                } catch (NumberFormatException ex){
                    brokerGui.appendChat("The id format is incorrect, please correct it!!!");
                    return;
                }
            }
        } else {
            try {
                tradeDetails = brokerGui.getTradeDetails();
                Integer.parseInt(tradeDetails.get(0));
                Integer.parseInt(tradeDetails.get(3));
                Integer.parseInt(tradeDetails.get(4));
                Integer.parseInt(tradeDetails.get(5));
                String fixMessage = createTradeFix(tradeDetails, broker.id);
                msg.append(fixMessage);
            } catch (NumberFormatException ex){
                brokerGui.appendChat("All fields must be filled ...");
                return;
            } finally {
                tradeDetails.clear();
            }
        }

        SocketChannel sc = (SocketChannel) selectionKey.channel();
        ByteBuffer bb = ByteBuffer.wrap(msg.toString().getBytes());
        sc.write(bb);
        selectionKey.interestOps(SelectionKey.OP_READ);
    }
}
