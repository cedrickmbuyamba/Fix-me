package org.snitchers.fix_me.router;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.snitchers.fix_me.utilities.MessagePrefix.*;

class MessageGenerator {
    static String createRegisterResponse(int _newID){
        return createHead(_newID, createRegisterMessage(_newID));
    }

    static String createLogonResponse(int _senderCompID){
        return createHead(_senderCompID, createLogonMessage(_senderCompID));
    }

    static String createRejectFix(int _targetCompID, int seqNumber, String reason){
        return createHead(_targetCompID, createRejectMessage(_targetCompID, seqNumber, reason));
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

    private static String createRegisterMessage(int _newID) {
        String message = msgType + "registered" + "|" +
                userID + _newID + "|" +
                time + new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss").format(new Date()) + "|";
        return message.replace("|", SOH);
    }

    private static String createLogonMessage(int _senderCompID) {
        String message = msgType + "loggedIn" + "|" +
                sender + _senderCompID + "|" +
                time + new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss").format(new Date()) + "|";
        return message.replace("|", SOH);
    }

    private static String createRejectMessage(int _targetCompID, int seqNumber, String reason){

        String message = msgType + "Rejected|" +
                sender + "Server|" +
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
