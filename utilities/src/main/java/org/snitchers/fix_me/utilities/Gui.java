package org.snitchers.fix_me.utilities;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

@Getter
@Setter
public class Gui extends JFrame {
    private GuiLogMessages chatMsgLog;
    private GuiLogMessages dbMsgLog;

    private JScrollPane chatMsgScroll;
    private JScrollPane dbMsgScroll;

    private JTextArea id;
    private final JButton login = new JButton("Login");
    private final JButton trade = new JButton("Purchase");

    private String brokerID;
    private ArrayList<String> tradeDetails = new ArrayList<>();

    public Gui(String title) {

        super(title);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setBounds(850, 400, 750, 627);
        setLayout(null);


        // initialize the gui and set
        chatMsgLog = new GuiLogMessages();
        chatMsgScroll = new JScrollPane(chatMsgLog, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        chatMsgScroll.setBounds(280, 10, 450, 580);

        dbMsgLog = new GuiLogMessages();
        dbMsgScroll = new JScrollPane(dbMsgLog, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        dbMsgScroll.setBounds(5, 10, 280, 580);

        if (title.equals("Fix-me Broker")){
            id = new JTextArea("Id needed");
            id.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
            id.setBounds(20,20, 100, 20);

            login.setBounds(130,20, 100, 20);
            login.addActionListener(actionEvent -> {
                synchronized (login) {
                    brokerID = id.getText();
                    login.notify();
                }
            });

            dbMsgLog.add(login);
            dbMsgLog.add(id);
        }
    }

    public String getBrokerID(){
        synchronized (login){
            try {
                login.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return this.brokerID;
        }
    }

    public void run() {
        add(chatMsgScroll);
//        if(this.getTitle().equals("Fix-me Broker"))
        add(dbMsgScroll);
        setVisible(true);
    }

    public void appendChat(String message) {
        chatMsgLog.append(message);

        chatMsgScroll.getViewport().setViewPosition(new Point(chatMsgScroll.getViewport().getViewPosition().x, chatMsgScroll.getViewport().getViewPosition().y + 70)
        );
        repaint();
    }

    private void appendTradeField() {
        JTextArea marketId = new JTextArea("Market ID");
        marketId.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        marketId.setBounds(20, 40, 220, 20);

        JTextArea orderType = new JTextArea("Item Order type");
        orderType.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        orderType.setBounds(20, 80, 220, 20);

        JTextArea item = new JTextArea("Item Product");
        item.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        item.setBounds(20, 120, 220, 20);

        JTextArea quantity = new JTextArea("Item Quantity");
        quantity.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        quantity.setBounds(20, 160, 220, 20);

        JTextArea price = new JTextArea("Item Price");
        price.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        price.setBounds(20, 200, 220, 20);

        JTextArea seq = new JTextArea("Item Number");
        seq.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        seq.setBounds(20, 240, 220, 20);

        trade.setBounds(20, 280, 220, 20);
        trade.addActionListener(actionEvent -> {
            synchronized (trade) {
                tradeDetails.add(marketId.getText());
                tradeDetails.add(orderType.getText());
                tradeDetails.add(item.getText());
                tradeDetails.add(quantity.getText());
                tradeDetails.add(price.getText());
                tradeDetails.add(seq.getText());
                trade.notify();
            }
        });

        dbMsgLog.add(marketId);
        dbMsgLog.add(item);
        dbMsgLog.add(orderType);
        dbMsgLog.add(quantity);
        dbMsgLog.add(price);
        dbMsgLog.add(seq);
        dbMsgLog.add(trade);
    }

    public ArrayList<String> getTradeDetails(){
        synchronized (trade){
            try {
                trade.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return this.tradeDetails;
        }
    }

    public void removeLogin(){
        id.setVisible(false);
        login.setVisible(false);
        dbMsgLog.remove(login);
        dbMsgLog.remove(id);
        appendTradeField();
        dbMsgLog.validate();
    }

    public void appendAvail(ArrayList<String> brokerMarket) {
        dbMsgLog.setText(null);
        dbMsgLog.setRows(brokerMarket.size());

        dbMsgLog.append("");
        dbMsgLog.append("Item|Quantity|price");

        for (String str : brokerMarket) {
            dbMsgLog.append(str);
        }

        dbMsgScroll.getViewport().setViewPosition(
                new Point(dbMsgScroll.getViewport().getViewPosition().x, brokerMarket.size() * 175)
        );
        repaint();
    }
}
