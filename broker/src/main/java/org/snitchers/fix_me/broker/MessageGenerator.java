package org.snitchers.fix_me.broker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static org.snitchers.fix_me.utilities.MessagePrefix.*;

class MessageGenerator {


    static String createRegisterFix(int _senderCompID){
//        int _senderCompID = broker.tempId;
        return createHead(_senderCompID, createRegisterMessage());
    }

    static String createLogonFix(int _senderCompID){
        return createHead(_senderCompID, createLogonMessage(_senderCompID));
    }

    static String createTradeFix(ArrayList<String> tradeDetails, int _senderCompID){
        return createHead(_senderCompID, createTradeMessage(tradeDetails, _senderCompID));
    }

    private static String createHead(int _senderCompID, String fixMessage){
        StringBuilder fixMessageHead = new StringBuilder();
        int length = fixMessage.length();

        fixMessageHead.append(userID).append(_senderCompID).append("|")
                .append(fixVersion)
                .append(bodyLength).append(length).append("|")
                .append(fixMessage);

        String checkSumValue = createCheckSum(fixMessageHead.toString());

        fixMessageHead.append(checkSum).append(checkSumValue);
        return fixMessageHead.toString().replace("|", SOH);
    }

    private static String createRegisterMessage() {
        String message = msgType + "register" + "|" +
                time + new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss").format(new Date()) + "|";
        return message.replace("|", SOH);
    }

    private static String createLogonMessage(int _senderCompID) {
        String message = msgType + "logon" + "|" +
                sender + _senderCompID + "|" +
                time + new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss").format(new Date()) + "|";
        return message.replace("|", SOH);
    }

    private static String createTradeMessage(ArrayList<String> tradeDetails, int _senderCompID){
        StringBuilder message = new StringBuilder();
        String type = tradeDetails.get(1), _targetCompID = tradeDetails.get(0), instrument = tradeDetails.get(2), iQuantity = tradeDetails.get(3), iPrice = tradeDetails.get(4), sequence = tradeDetails.get(5);

        message.append(msgType).append(type).append("|")
                .append(sender).append(_senderCompID).append("|")
                .append(seqNum).append(sequence).append("|")
                .append(receiver).append(_targetCompID).append("|")
                .append(quantity).append(iQuantity).append("|")
                .append(product).append(instrument).append("|")
                .append(price).append(iPrice).append("|")
                .append(time).append(new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss").format(new Date())).append("|");
        return message.toString();
    }

    private static String createCheckSum(String message){
        byte[] bytes = message.getBytes();
        return String.format("%03d", bytes.length % 256);
    }
}
