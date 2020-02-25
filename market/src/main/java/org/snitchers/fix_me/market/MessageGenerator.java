package org.snitchers.fix_me.market;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.snitchers.fix_me.utilities.MessagePrefix.*;

class MessageGenerator {

    static String createLogonFix(int _senderCompID){
        return createHead(_senderCompID, createLogonMessage(_senderCompID));
    }

    static String createTransactionFix(int _targetCompID, int _senderCompID, int seqNumber, String instruction, String reason){
        return createHead(_targetCompID, createTransactionMessage(_targetCompID, _senderCompID, seqNumber, instruction, reason));
    }

    private static String createHead(int _senderCompID, String fixMessage){
        StringBuilder fixMessageHead = new StringBuilder();
        int length = fixMessage.length();

        fixMessageHead.append(userID).append(_senderCompID).append("|")
                .append(fixVersion)
                .append(bodyLength).append(length).append("|")
                .append(fixMessage)
                .append(checkSum).append(createCheckSum(fixMessageHead.toString())).append("|");
        return fixMessageHead.toString().replace("|", SOH);
    }

    private static String createLogonMessage(int _senderCompID) {
        String message = msgType + "logon" + "|" +
                sender + _senderCompID + "|" +
                time + new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss").format(new Date()) + "|";
        return message.replace("|", SOH);
    }

    private static String createTransactionMessage(int _targetCompID, int _senderCompID, int seqNumber, String instruction, String reason){

        String message = msgType + instruction + "|" +
                sender + _senderCompID + "|" +
                seqNum + seqNumber + "|" +
                receiver + _targetCompID + "|" +
                res + reason + "|" +
                time + new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss").format(new Date()) + "|";
        return message.replace("|", SOH);
    }

    private static String createCheckSum(String message){
        byte[] bytes = message.getBytes();
        return String.format("%03d", bytes.length % 256);
    }
}

