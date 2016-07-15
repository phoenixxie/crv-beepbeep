package ca.uqac.lif.crv;

import ca.uqac.lif.cep.Connector;
import ca.uqac.lif.cep.Pullable;
import ca.uqac.lif.cep.input.TokenFeeder;
import ca.uqac.lif.cep.io.StreamReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class MarQAuctionBidding {

    private static class Item {
        private String name;
        private int minPrice;
        private int currentBid;
        private int date;

        public Item(String name, int minPrice, int date) {
            this.name = name;
            this.minPrice = minPrice;
            this.currentBid = 0;
            this.date = date;
        }

        public String getName() {
            return name;
        }

        public int getMinPrice() {
            return minPrice;
        }

        public int getDate() {
            return date;
        }

        public int getCurrentBid() {

            return currentBid;
        }

        public void setCurrentBid(int currentBid) {
            this.currentBid = currentBid;
        }
    }

    public static void main(String[] args) throws FileNotFoundException {

        if (args.length != 1) {
            return;
        }

        String filepath = args[0];

        StreamReader sr = new StreamReader(new FileInputStream(filepath));
        MarQTokenFeeder tf = new MarQTokenFeeder();

        Connector.connect(sr, tf);

        Pullable p = tf.getPullableOutput(0);

        HashMap<String, Item> items = new HashMap<>();
        HashMap<Integer, HashSet<String>> itemDate = new HashMap<>();
        HashSet<String> doneitems = new HashSet<>();

        int currDate = 0;

        while (p.hasNext() == Pullable.NextStatus.YES) {
            Object obj = p.pull();
            if (obj instanceof TokenFeeder.NoToken) {
                continue;
            }

            MarQTokenFeeder.Token token = (MarQTokenFeeder.Token) obj;
            Item item;
            HashSet<String> itemSet;
            switch (token.func) {
                case "event":
                    break;
                case "create_auction": // create_auction(item,minimum,period)
                    if (items.containsKey(token.args[0]) || doneitems.contains(token.args[0])) {
                        // After the auction is over no events related to the item are allowed.
                        // An item can only be placed for auction once.
                        System.out.println("STATUS: Violated");
                        System.exit(0);
                        return;
                    }
                    int finDate = currDate + Integer.parseInt(token.args[2]);
                    item = new Item(token.args[0], Integer.parseInt(token.args[1]), finDate);
                    items.put(token.args[0], item);

                    itemSet = itemDate.get(finDate);
                    if (itemSet == null) {
                        itemSet = new HashSet<>();
                        itemDate.put(finDate, itemSet);
                    }
                    itemSet.add(item.getName());

                    break;
                case "bid": // bid(item,amount)
                    item = items.get(token.args[0]);
                    if (item == null) {
                        System.out.println("STATUS: Violated");
                        System.exit(0);
                        return;
                    }
                    int bid = Integer.parseInt(token.args[1]);
                    if (item.getCurrentBid() >= bid) {
                        // Each bid on the item must be strictly larger than the previous bid
                        System.out.println("STATUS: Violated");
                        System.exit(0);
                        return;
                    }
                    item.setCurrentBid(bid);
                    break;
                case "sold": // sold(item)
                    item = items.get(token.args[0]);
                    if (item == null) {
                        System.out.println("STATUS: Violated");
                        System.exit(0);
                        return;
                    }

                    if (item.getCurrentBid() < item.getMinPrice()) {
                        System.out.println("STATUS: Violated");
                        System.exit(0);
                        return;
                    }

                    itemSet = itemDate.get(item.getDate());
                    if (itemSet == null) {
                        System.out.println("STATUS: Violated");
                        System.exit(0);
                        return;
                    }

                    if (!itemSet.remove(item.getName())) {
                        System.out.println("STATUS: Violated");
                        System.exit(0);
                        return;
                    }

                    items.remove(item.getName());
                    doneitems.add(item.getName());
                    break;
                case "endOfDay":
                    ++currDate;
                    itemSet = itemDate.get(currDate);
                    if (itemSet == null) {
                        break;
                    }

                    for (String itemName : itemSet) {
                        if (null == items.remove(itemName)) {
                            System.out.println("STATUS: Violated");
                            System.exit(0);
                            return;
                        }
                        doneitems.add(itemName);
                    }
                    itemSet.remove(currDate);

                    break;
            }

        }

        System.out.println("STATUS: Satisfied");
    }

}
