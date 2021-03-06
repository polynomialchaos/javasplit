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

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

import javasplit.utils.Base;
import javasplit.utils.Currency;
import javasplit.utils.TimeStamp;
import javasplit.utils.Utils;

/**
 * Purchase class.
 */
public class Purchase extends Base {
    protected Group group;
    protected Member purchaser;
    protected LinkedHashMap<String, Member> recipients = new LinkedHashMap<String, Member>();
    protected Double amount;
    protected TimeStamp date;
    protected String title;
    protected Currency currency;

    /**
     * Initialize a Purchase object.
     */
    public Purchase(Group group, String title, String purchaser, List<String> recipients,
            Double amount, Currency currency, TimeStamp date) {
        this.group = group;
        this.title = title;
        setPurchaser(purchaser);
        setRecipients(recipients);
        this.amount = amount;
        this.currency = currency;
        this.date = date;

        link();
    }

    /**
     * Gets the amount in group currency.
     *
     * @return A double value.
     */
    public Double getAmount() {
        return group.exchange(amount, currency);
    }

    /**
     * Gets the amount per member in group currency.
     *
     * @return A double value.
     */
    public Double getAmountPerMember() {
        return getAmount() / numberOfRecipients();
    }

    /**
     * Checks the name to be the purchaser.
     *
     * @return A boolean flag.
     */
    public boolean isPurchaser(String name) {
        return purchaser.getName().equals(name);
    }

    /**
     * Checks the name to be a recipients.
     *
     * @return A boolean flag.
     */
    public boolean isRecipient(String name) {
        return recipients.containsKey(name);
    }

    /**
     * Link this Purchase in all members.
     */
    protected void link() {
        HashSet<Member> members = new HashSet<Member>(recipients.values());
        members.add(purchaser);

        for (Member member : members) {
            member.addParticipation(this);
        }
    }

    /**
     * Gets the number of recipients.
     *
     * @return A integer value.
     */
    public int numberOfRecipients() {
        return recipients.size();
    }

    /**
     * Serializes the object.
     *
     * @return A LinkedHashMap of type String and Object.
     */
    @Override
    protected LinkedHashMap<String, Object> serialize() {
        LinkedHashMap<String, Object> hash_map = new LinkedHashMap<String, Object>();
        hash_map.put("purchaser", purchaser.getName());
        hash_map.put("recipients", Utils.convertAll(recipients.keySet(), a -> a));
        hash_map.put("amount", amount);
        hash_map.put("currency", currency.name());
        hash_map.put("date", date.toString());
        hash_map.put("title", title);
        return hash_map;
    }

    /**
     * Set purchaser by name
     */
    private void setPurchaser(String purchaser) {
        this.purchaser = group.getMemberByName(purchaser);
    }

    /**
     * Set recipients by List of names.
     */
    private void setRecipients(List<String> recipients) {
        for (String recipient : recipients) {
            this.recipients.put(recipient, group.getMemberByName(recipient));
        }
    }

    /**
     * Converts to an equivalent string.
     *
     * @return A string.
     */
    @Override
    public String toString() {
        String tmp = String.format("%s (%s) %s: %.2f%s -> %s",
                title, date, purchaser.getName(), amount, currency,
                String.join(", ", recipients.keySet()));

        return tmp;
    }

    /**
     * Unlink this Purchase from all members.
     */
    protected void unlink() {
        HashSet<Member> members = new HashSet<Member>(recipients.values());
        members.add(purchaser);

        for (Member member : members) {
            member.removeParticipation(this);
        }
    }
}