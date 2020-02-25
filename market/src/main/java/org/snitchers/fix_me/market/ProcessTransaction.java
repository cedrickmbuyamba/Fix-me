package org.snitchers.fix_me.market;

import org.snitchers.fix_me.utilities.*;

import java.util.ArrayList;

import static org.snitchers.fix_me.utilities.MessagePrefix.SOH;

public class ProcessTransaction implements Handler<String> {

    @Override
    public void handle(String s){
        String responseMessage;
        ArrayList<String> values = splitForMe(s.split(SOH));

        if (values.get(3).equals("buy") || values.get(3).equals("sell")) {
            if (!itemIsSold(values.get(4))) {
                responseMessage = MessageGenerator.createTransactionFix(Integer.parseInt(values.get(0)), Integer.parseInt(values.get(1)), Integer.parseInt(values.get(2)), "Rejected", "Item not available in the market");
            } else if (values.get(3).equals("buy") && !validateQuantity(values.get(5), values.get(4))) {
                responseMessage = MessageGenerator.createTransactionFix(Integer.parseInt(values.get(0)), Integer.parseInt(values.get(1)), Integer.parseInt(values.get(2)), "Rejected", "The stock is not enough");
            } else if (!validatePrice(values.get(6), values.get(4))) {
                responseMessage = MessageGenerator.createTransactionFix(Integer.parseInt(values.get(0)), Integer.parseInt(values.get(1)), Integer.parseInt(values.get(2)), "Rejected", "Please check the Item price");
            } else if (values.get(3).equals("sell") && !validateCapital(values.get(6), values.get(5))) {
                responseMessage = MessageGenerator.createTransactionFix(Integer.parseInt(values.get(0)), Integer.parseInt(values.get(1)), Integer.parseInt(values.get(2)), "Rejected", "Market can not handle your order, try again");
            } else {
                responseMessage = processTransaction(values);
            }
        } else {
            responseMessage = MessageGenerator.createTransactionFix(Integer.parseInt(values.get(0)), Integer.parseInt(values.get(1)), Integer.parseInt(values.get(2)), "Rejected", "Unknownorder");
        }

        Market.message = responseMessage;
    }

    private ArrayList<String> splitForMe(String[] split) {
        ArrayList<String> values = new ArrayList<>();

        values.add(split[0].substring(4));//target
        values.add(split[6].substring(3));//sender
        values.add(split[5].substring(3));//sequence number
        values.add(split[3].substring(3));//order type
        values.add(split[8].substring(3));//item
        values.add(split[7].substring(3));//quantity
        values.add(split[9].substring(3));//price

        return values;
    }

    private String processTransaction(ArrayList<String> values) {
        /**
         * set new quantity
         * set new market cash
         */
        StringBuilder newItem = new StringBuilder();

        for (int i = 0; i < Market.market.size(); i++) {
            String s = Market.market.get(i);
            String[] split = s.split("\\|");

            if(split[0].equals(values.get(4))){
                newItem.append(split[0]).append("|");
                if (values.get(3).equals("buy"))
                    newItem.append(Integer.parseInt(split[1]) - Integer.parseInt(values.get(5))).append("|");
                else
                    newItem.append(Integer.parseInt(split[1]) + Integer.parseInt(values.get(5))).append("|");
                newItem.append(split[2]);

                Market.market.remove(s);
                Market.market.add(newItem.toString());
                break;
            }
        }

        return MessageGenerator.createTransactionFix(Integer.parseInt(values.get(0)), Integer.parseInt(values.get(1)), Integer.parseInt(values.get(2)), "Executed", "transaction executed successfully");
    }

    private boolean itemIsSold(String item) {
        for (String s: Market.market) {
            if(s.split("\\|")[0].equals(item))
                return true;
        }
        return false;
    }

    private boolean validateQuantity(String quantity, String item) {
        for (String s: Market.market) {
            if(s.split("\\|")[0].equals(item) && Integer.parseInt(s.split("\\|")[1]) >= Integer.parseInt(quantity))
                return true;
        }
        return false;
    }

    private boolean validatePrice(String price, String item) {
        for (String s: Market.market) {
            if(s.split("\\|")[0].equals(item) && Integer.parseInt(s.split("\\|")[2]) == Integer.parseInt(price))
                return true;
        }
        return false;
    }

    private boolean validateCapital(String price, String quantity) {
        return true;
    }
}