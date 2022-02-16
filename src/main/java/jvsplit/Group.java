// MIT License
//
// Copyright (c) 2021 Florian
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
package jvsplit;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import jvsplit.utils.Base;
import jvsplit.utils.Currency;
import jvsplit.utils.Stamp;

public class Group extends Base {
    private String name;
    private String description;
    private Currency currency;
    private LinkedHashMap<Currency, Double> exchange_rates;
    private LinkedHashMap<String, Member> members;
    private ArrayList<Purchase> purchases;
    private ArrayList<Transfer> transfers;

    Group(String path) {
        this.exchange_rates = new LinkedHashMap<Currency, Double>();
        this.members = new LinkedHashMap<String, Member>();
        this.purchases = new ArrayList<Purchase>();
        this.transfers = new ArrayList<Transfer>();

        try {
            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new FileReader(path));

            // group
            Map<?, ?> gson_root = gson.fromJson(reader, Map.class);
            this.name = (String) gson_root.get("name");
            this.description = (String) gson_root.get("description");
            this.currency = Currency.valueOf((String) gson_root.get("currency"));
            this.setTime((String) gson_root.get("stamp"));

            // exchange rates
            Map<?, ?> gson_exchange_rates = (Map<?, ?>) gson_root.get("exchange_rates");
            for (Currency currency : Currency.values()) {
                if (gson_exchange_rates.containsKey(currency.name())) {
                    Double tmp = (Double) gson_exchange_rates.get(currency.name());
                    exchange_rates.put(currency, tmp);
                }
            }

            // members
            List<?> gson_members = (List<?>) gson_root.get("members");
            for (Object gson_member_it : gson_members) {
                Map<?, ?> gson_member = (Map<?, ?>) gson_member_it;

                String name = (String) gson_member.get("name");
                Member tmp = this.addMember(name);
                tmp.setTime((String) gson_member.get("stamp"));
            }

            // purchases
            List<?> gson_purchases = (List<?>) gson_root.get("purchases");
            for (Object gson_purchase_it : gson_purchases) {
                Map<?, ?> gson_purchase = (Map<?, ?>) gson_purchase_it;

                String purchaser = (String) gson_purchase.get("purchaser");
                List<?> recipients = (List<?>) gson_purchase.get("recipients");
                Double amount = (Double) gson_purchase.get("amount");
                Stamp date = new Stamp((String) gson_purchase.get("date"));
                String title = (String) gson_purchase.get("title");
                Currency currency = Currency.valueOf((String) gson_purchase.get("currency"));

                Purchase purchase = this.addPurchase(purchaser,
                        Base.apply(recipients, a -> ((Object) a).toString()),
                        amount, date, title, currency);
                purchase.setTime((String) gson_purchase.get("stamp"));
            }

            // transfers
            List<?> gson_transfers = (List<?>) gson_root.get("transfers");
            for (Object gson_transfers_it : gson_transfers) {
                Map<?, ?> gson_transfer = (Map<?, ?>) gson_transfers_it;

                String purchaser = (String) gson_transfer.get("purchaser");
                String recipient = (String) ((List<?>) gson_transfer.get("recipients")).get(0);
                Double amount = (Double) gson_transfer.get("amount");
                Stamp date = new Stamp((String) gson_transfer.get("date"));
                String title = (String) gson_transfer.get("title");
                Currency currency = Currency.valueOf((String) gson_transfer.get("currency"));

                Transfer transfer = this.addTransfer(purchaser, recipient,
                        amount, date, title, currency);
                transfer.setTime((String) gson_transfer.get("stamp"));
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Group(String name, String description,
            Currency currency, LinkedHashMap<Currency, Double> exchange_rates) {
        this.name = name;
        this.description = description;
        this.currency = currency;
        this.exchange_rates = exchange_rates;

        this.members = new LinkedHashMap<String, Member>();
        this.purchases = new ArrayList<Purchase>();
        this.transfers = new ArrayList<Transfer>();
    }

    Member addMember(String name) {
        if (name.isBlank()) {
            throw new RuntimeException("Empty member name provided!");
        }

        if (this.members.containsKey(name)) {
            throw new RuntimeException("Duplicate member name provided!");
        }

        Member member = new Member(name);
        this.members.put(name, member);

        return member;
    }

    Purchase addPurchase(String purchaser, List<String> recipients, Double amount,
            Stamp date, String title, Currency currency) {
        Purchase purchase = new Purchase(this, purchaser, recipients, amount, date, title, currency);
        this.purchases.add(purchase);

        return purchase;
    }

    Transfer addTransfer(String purchaser, String recipient, Double amount,
            Stamp date, String title, Currency currency) {
        Transfer transfer = new Transfer(this, purchaser, recipient, amount, date, title, currency);
        this.transfers.add(transfer);

        return transfer;
    }

    double exchange(double amount, Currency currency) {
        if (currency.equals(this.currency)) {
            return amount;
        } else {
            if (!this.exchange_rates.containsKey(currency)) {
                throw new RuntimeException("No valid exchange rate found!");
            }
            return amount / this.exchange_rates.get(currency);
        }
    }

    Member getMemberByName(String name) {
        if (!this.members.containsKey(name)) {
            throw new RuntimeException(String.format("No member with name \"%s\"!", name));
        }

        return this.members.get(name);
    }

    public List<Balance> getPendingBalances() {
        ArrayList<Balance> balances = new ArrayList<Balance>();

        // sorted members
        List<Member> members = Base.apply(this.members.values(), a -> a);
        Collections.sort(members, new Comparator<Member>() {
            @Override
            public int compare(Member m1, Member m2) {
                return Double.compare(m1.balance(), m2.balance());
            }
        });

        LinkedHashMap<Member, Double> bal_add = new LinkedHashMap<Member, Double>();
        for (Member member : members) {
            bal_add.put(member, 0.0);
        }

        for (Member sender : members) {
            ListIterator<Member> listIterator = members.listIterator(members.size());

            // Iterate in reverse.
            while (listIterator.hasPrevious()) {
                Member receiver = listIterator.previous();
                if (sender == receiver)
                    continue;

                Double sender_balance = sender.balance() + bal_add.get(sender);
                Double receiver_balance = receiver.balance() + bal_add.get(receiver);

                if (receiver_balance > 0.0) {
                    Double bal = Math.min(Math.abs(sender_balance), receiver_balance);
                    bal_add.put(sender, bal_add.get(sender) + bal);
                    bal_add.put(receiver, bal_add.get(receiver) - bal);

                    Balance balance = new Balance(this, sender.getName(),
                            receiver.getName(), bal, new Stamp(), this.currency);
                    balances.add(balance);
                }
            }
        }

        return balances;
    }

    public void print() {
        int length = 80;
        String mainrule = "=".repeat(length);
        String rule = "-".repeat(length);

        System.out.println(mainrule);
        System.out.println(String.format("Summary for group: %s", this.name));
        if (!this.description.isEmpty()) {
            System.out.println(this.description);
        }

        System.out.println(mainrule);
        System.out.println(String.format(" * Turnover: %.2f%s", this.turnover(), this.currency));

        if (!this.exchange_rates.isEmpty()) {
            System.out.println(rule);
            System.out.println("Exchange rates:");
            for (Map.Entry<Currency, Double> exchange_rate : this.exchange_rates.entrySet()) {
                System.out.println(String.format(" * 1%s -> %.2f%s", this.currency, exchange_rate.getValue(),
                        exchange_rate.getKey()));
            }

        }

        System.out.println(rule);
        System.out.println("Members:");
        for (Member member : this.members.values()) {
            System.out.println(String.format(" * %s", member.getName()));
        }

        System.out.println(rule);
        System.out.println("Purchases:");
        for (Purchase purchase : this.purchases) {
            System.out.println(String.format(" * %s", purchase));
        }

        System.out.println(rule);
        System.out.println("Transfers:");
        for (Transfer transfer : this.transfers) {
            System.out.println(String.format(" * %s", transfer));
        }

        System.out.println(rule);
        System.out.println("Pending balances:");
        for (Balance balance : this.getPendingBalances()) {
            System.out.println(String.format(" * %s", balance));
        }

        System.out.println(mainrule);
    }

    public void save(String path) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try {
            FileWriter writer = new FileWriter(path);
            gson.toJson(this.toDict(), writer);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public LinkedHashMap<String, Object> serialize() {
        LinkedHashMap<String, Object> hash_map = new LinkedHashMap<String, Object>();
        hash_map.put("name", this.name);
        hash_map.put("description", this.description);
        hash_map.put("currency", this.currency.name());
        hash_map.put("members", apply(this.members.values(), a -> a.toDict()));
        hash_map.put("purchases", apply(this.purchases, a -> a.toDict()));
        hash_map.put("transfers", apply(this.transfers, a -> a.toDict()));
        hash_map.put("exchange_rates", this.exchange_rates);
        return hash_map;
    }

    public double turnover() {
        double turnover = 0.0;
        for (Purchase purchase : this.purchases) {
            turnover += purchase.getAmount();
        }

        return turnover;
    }
}