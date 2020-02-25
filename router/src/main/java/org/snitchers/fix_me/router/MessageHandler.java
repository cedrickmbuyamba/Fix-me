package org.snitchers.fix_me.router;

import org.snitchers.fix_me.utilities.Handler;

import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentMap;

import static org.snitchers.fix_me.router.MessageGenerator.createRejectFix;
import static org.snitchers.fix_me.utilities.MessagePrefix.SOH;

public class MessageHandler implements Handler<String> {

    private ConcurrentMap<SocketChannel, Integer> brokers;

    MessageHandler(ConcurrentMap<SocketChannel, Integer> brokers){
        this.brokers = brokers;
    }

    @Override
    public void handle(String s) {
        String[] splitFixMessage = s.split(SOH);
        boolean executed = splitFixMessage[3].substring(3).equals("Executed");
        boolean rejected = splitFixMessage[3].substring(3).equals("Rejected");

        //check if the message is valid fix message if not prepare reject message
        if (!executed && !rejected) {
            if (!checkSum(splitFixMessage)) {
                s = createRejectFix(Integer.parseInt(splitFixMessage[0].substring(4)), Integer.parseInt(splitFixMessage[5].substring(3)), "Incorrect input or message tampered with");
            } else if (!marketAvailable(splitFixMessage[6].substring(3))){
                s = createRejectFix(Integer.parseInt(splitFixMessage[0].substring(4)), Integer.parseInt(splitFixMessage[5].substring(3)), "This market you are trying to trade with is not available");
            }
        }
        Router.db_handler.insertFixMessage(Integer.parseInt(splitFixMessage[0].substring(4)), s);
        Router.messages.add(s);
    }

    private boolean checkSum(String[] split){
        StringBuilder headMessage = new StringBuilder();
        int checksum = Integer.parseInt(split[11].substring(3));

        for (int i = 0; i < split.length - 1; i++){
            headMessage.append(split[i]).append("|");
        }

        byte[] bytes = headMessage.toString().getBytes();

        return bytes.length % 256 == checksum;
    }

    private boolean marketAvailable(String marketID) {
        return brokers.containsValue(Integer.parseInt(marketID)) && marketID.equals("100000");
    }

}