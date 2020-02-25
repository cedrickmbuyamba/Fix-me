package org.snitchers.fix_me.utilities;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

class Scratch {
    /**
     *
     * @param args
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws IOException
     *
     * 8 - Fix protocol version
     * 9 - Body length
     * 10 - checksum
     * 14 - Total number of shares filled
     * 34 - sequence number
     * 35 - message type | UA = logon | UD = single order | 1 = connection accepted | U3 = reject | U5 = logout/market unavailable
     * 37 - order id
     * 38 - quantity
     * 44 - share price
     * 49 - SenderCompID - Assigned value used to identify the sender in a FIX session.
     * 55 - instrument
     * 56 - TargetCompID - Assigned value used to identify the receiver in a FIX session.
     * 98 - message encryption scheme, always 0 is supported for now
     * 141 - sequence numbers reset, the value should be Y
     * 553 - username
     * 554 - password
     */

    public static final String fixVersion = "8=FIX.4.4|";
    public static final String bodyLength = "9=";
    public static final String msgType = "35=";
    public static final String seqNum = "34=";
    public static final String quantity = "38=";
    public static final String price = "44=";
    public static final String sender = "49=";
    public static final String product = "55=";
    public static final String receiver = "56=";
    public static final String checkSum = "10=";
    public static final String time = "52=";
    public static final String SOH = "\u0001";
    public static final String encryption = "98=0|";
    public static final String seqReset = "141=Y|";
    public static final String userID = "553=";

    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        System.out.println("");
    }
}