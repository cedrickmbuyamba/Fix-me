package org.snitchers.fix_me.market;

import java.util.ArrayList;
import java.util.Random;

class MarketGenerator {
    private static String[] items = {"laptop", "Books", "Sptapler", "pen", "orange", "banana", "apple", "mango", "guava", "pear", "peach", "watermelon", "cherry", "grape"};
    private static int[] prices = {20, 50, 34, 39, 50, 100, 66, 78, 80, 43, 16, 17, 18, 19};
    private int[] quantity = {200, 250, 130, 100, 140, 320, 156, 540, 230, 450, 21, 22, 23, 24};

    ArrayList<String> createMarket(){
        Random indexer = new Random();
        ArrayList<String> market = new ArrayList<>();
        ArrayList<Integer> itemIndex = getIndexes();

        for (Integer index: itemIndex) {
            String item = items[index] + "|" +
                    quantity[indexer.nextInt(14)] + "|" +
                    prices[indexer.nextInt(14)];
            market.add(item);
        }
        return market;
    }

    private ArrayList<Integer> getIndexes() {
        ArrayList<Integer> index = new ArrayList<>();
        Random indexer = new Random();
        int i;
        for(;index.size() < 8;){
            i = indexer.nextInt(14);
            if(!index.contains(i)){
                index.add(i);
            }
        }
        return index;
    }
}
