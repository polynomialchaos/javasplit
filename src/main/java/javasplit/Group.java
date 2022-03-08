// MIT License
//
// Copyright (c) 2022 Florian Eigentler
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
package javasplit;

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

import javasplit.utils.Base;
import javasplit.utils.Currency;
import javasplit.utils.TimeStamp;
import javasplit.utils.Utils;

/**
 * Group class.
 */
public final class Group extends Base {
    private String name;
    private String description;
    private Currency currency;
    private LinkedHashMap<Currency, Double> exchange_rates = new LinkedHashMap<Currency, Double>();
    private LinkedHashMap<String, Member> members = new LinkedHashMap<String, Member>();
    private ArrayList<Purchase> purchases = new ArrayList<Purchase>();
    private ArrayList<Transfer> transfers = new ArrayList<Transfer>();

    /**
     * Initialize a Group object with name, description and currency.
     */
    public Group(String name, String description, Currency currency) {
        this.name = name;
        this.description = description;
        this.currency = currency;
    }

    /**
     * Initialize a Group object from Json file path string.
     */
    public Group(String path) {
        try {
            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new FileReader(path));

            // group
            Map<?, ?> gson_root = gson.fromJson(reader, Map.class);
            name = (String) gson_root.get("name");
            description = (String) gson_root.get("description");
            currency = Currency.valueOf((String) gson_root.get("currency"));
            setTime((String) gson_root.get("stamp"));

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
                Member tmp = addMember(name);
                tmp.setTime((String) gson_member.get("stamp"));
            }

            // purchases
            List<?> gson_purchases = (List<?>) gson_root.get("purchases");
            for (Object gson_purchase_it : gson_purchases) {
                Map<?, ?> gson_purchase = (Map<?, ?>) gson_purchase_it;

                String purchaser = (String) gson_purchase.get("purchaser");
                List<?> recipients = (List<?>) gson_purchase.get("recipients");
                Double amount = (Double) gson_purchase.get("amount");
                TimeStamp date = new TimeStamp((String) gson_purchase.get("date"));
                String title = (String) gson_purchase.get("title");
                Currency currency = Currency.valueOf((String) gson_purchase.get("currency"));

                Purchase purchase = addPurchase(title, purchaser,
                        Utils.convertAll(recipients, a -> ((Object) a).toString()),
                        amount, currency, date);
                purchase.setTime((String) gson_purchase.get("stamp"));
            }

            // transfers
            List<?> gson_transfers = (List<?>) gson_root.get("transfers");
            for (Object gson_transfers_it : gson_transfers) {
                Map<?, ?> gson_transfer = (Map<?, ?>) gson_transfers_it;

                String purchaser = (String) gson_transfer.get("purchaser");
                String recipient = (String) ((List<?>) gson_transfer.get("recipients")).get(0);
                Double amount = (Double) gson_transfer.get("amount");
                TimeStamp date = new TimeStamp((String) gson_transfer.get("date"));
                String title = (String) gson_transfer.get("title");
                Currency currency = Currency.valueOf((String) gson_transfer.get("currency"));

                Transfer transfer = addTransfer(title, purchaser, recipient,
                        amount, currency, date);
                transfer.setTime((String) gson_transfer.get("stamp"));
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    /**
     * Adds a member by name to the group.
     *
     * @return A Member object.
     */
    public Member addMember(String name) {
        if (name.isBlank()) {
            throw new RuntimeException("Empty member name provided!");
        }

        if (members.containsKey(name)) {
            throw new RuntimeException("Duplicate member name provided!");
        }

        Member member = new Member(name);
        members.put(name, member);

        return member;
    }

    /**
     * Adds a purchase to the group.
     *
     * @return A Purchase object.
     */
    public Purchase addPurchase(String title, String purchaser, List<String> recipients,
            Double amount, Currency currency, TimeStamp date) {
        Purchase purchase = new Purchase(
                this, title, purchaser, recipients, amount, currency, date);
        purchases.add(purchase);

        return purchase;
    }

    /**
     * Adds a transfer to the group.
     *
     * @return A Transfer object.
     */
    public Transfer addTransfer(String title, String purchaser, String recipient,
            Double amount, Currency currency, TimeStamp date) {
        Transfer transfer = new Transfer(
                this, title, purchaser, recipient, amount, currency, date);
        transfers.add(transfer);

        return transfer;
    }

    /**
     * Gets the exchange amount based on given currency.
     *
     * @return A double value.
     */
    public double exchange(double amount, Currency currency) {
        if (this.currency.equals(currency)) {
            return amount;
        } else {
            if (!exchange_rates.containsKey(currency)) {
                throw new RuntimeException("No valid exchange rate found!");
            }
            return amount / exchange_rates.get(currency);
        }
    }

    /**
     * Gets the currency on the group.
     *
     * @return A Currency object.
     */
    public Currency getCurrency() {
        return currency;
    }

    /**
     * Gets a member by name.
     *
     * @return A Member object.
     */
    public Member getMemberByName(String name) {
        if (!members.containsKey(name)) {
            throw new RuntimeException(String.format("No member with name \"%s\"!", name));
        }

        return members.get(name);
    }

    /**
     * Gets member names.
     *
     * @return A List of type string.
     */
    public List<String> getMemberNames() {
        return Utils.convertAll(members.entrySet(), a -> a.getKey());
    }

    /**
     * Gets the group name
     *
     * @return A string.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the number of members.
     *
     * @return A integer value.
     */
    public int getNumberOfMembers() {
        return members.size();
    }

    /**
     * Gets the list of pending balances.
     *
     * @return A List of type Balance.
     */
    public List<Balance> getPendingBalances() {
        ArrayList<Balance> balances = new ArrayList<Balance>();

        // sorted members
        List<Member> members = Utils.convertAll(this.members.values(), a -> a);
        Collections.sort(members, new Comparator<Member>() {
            @Override
            public int compare(Member m1, Member m2) {
                return Double.compare(m1.getBalance(), m2.getBalance());
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

                Double sender_balance = sender.getBalance() + bal_add.get(sender);
                Double receiver_balance = receiver.getBalance() + bal_add.get(receiver);

                if (receiver_balance > 0.0) {
                    Double bal = Math.min(Math.abs(sender_balance), receiver_balance);
                    bal_add.put(sender, bal_add.get(sender) + bal);
                    bal_add.put(receiver, bal_add.get(receiver) - bal);

                    Balance balance = new Balance(
                            this, sender.getName(), receiver.getName(),
                            bal, currency, new TimeStamp());
                    balances.add(balance);
                }
            }
        }

        return balances;
    }

    /**
     * Gets the group turnover.
     *
     * @return A double value.
     */
    public double getTurnover() {
        double turnover = 0.0;
        for (Purchase purchase : this.purchases) {
            turnover += purchase.getAmount();
        }

        return turnover;
    }

    /**
     * Prints the group objects.
     */
    public void print() {
        int length = 80;
        String mainrule = "=".repeat(length);
        String rule = "-".repeat(length);

        System.out.println(mainrule);
        System.out.println(String.format("Summary for group: %s", name));
        if (!description.isEmpty()) {
            System.out.println(description);
        }

        System.out.println(mainrule);
        System.out.println(String.format(" * Turnover: %.2f%s", getTurnover(), currency));

        if (!exchange_rates.isEmpty()) {
            System.out.println(rule);
            System.out.println("Exchange rates:");
            for (Map.Entry<Currency, Double> exchange_rate : exchange_rates.entrySet()) {
                System.out.println(String.format(" * 1%s -> %.2f%s", currency, exchange_rate.getValue(),
                        exchange_rate.getKey()));
            }

        }

        System.out.println(rule);
        System.out.println("Members:");
        for (Member member : members.values()) {
            System.out.println(String.format(" * %s", member.getName()));
        }

        System.out.println(rule);
        System.out.println("Purchases:");
        for (Purchase purchase : purchases) {
            System.out.println(String.format(" * %s", purchase));
        }

        System.out.println(rule);
        System.out.println("Transfers:");
        for (Transfer transfer : transfers) {
            System.out.println(String.format(" * %s", transfer));
        }

        System.out.println(rule);
        System.out.println("Pending balances:");
        for (Balance balance : getPendingBalances()) {
            System.out.println(String.format(" * %s", balance));
        }

        System.out.println(mainrule);
    }

    /**
     * Sets the exchange rate for a given currency
     */
    public void setExchangeRate(Currency currency, Double rate) {
        exchange_rates.put(currency, rate);
    }

    /**
     * Saves the group dictionary to a path.
     */
    public void save(String path) {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            FileWriter writer = new FileWriter(path);
            gson.toJson(toDict(), writer);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    /**
     * Serializes the object.
     *
     * @return A LinkedHashMap of type String and Object.
     */
    @Override
    protected LinkedHashMap<String, Object> serialize() {
        LinkedHashMap<String, Double> exchange_rates = new LinkedHashMap<String, Double>();
        for (Map.Entry<Currency, Double> entry : this.exchange_rates.entrySet()) {
            exchange_rates.put(entry.getKey().name(), entry.getValue());
        }

        LinkedHashMap<String, Object> hash_map = new LinkedHashMap<String, Object>();
        hash_map.put("name", name);
        hash_map.put("description", description);
        hash_map.put("currency", currency.name());
        hash_map.put("members", Utils.convertAll(members.values(), a -> a.toDict()));
        hash_map.put("purchases", Utils.convertAll(purchases, a -> a.toDict()));
        hash_map.put("transfers", Utils.convertAll(transfers, a -> a.toDict()));
        hash_map.put("exchange_rates", exchange_rates);

        return hash_map;
    }

    /**
     * Converts to an equivalent string.
     *
     * @return A string.
     */
    @Override
    public String toString() {
        return String.format("%s [%s] %s (%s)", name, description, currency, super.toString());
    }
}