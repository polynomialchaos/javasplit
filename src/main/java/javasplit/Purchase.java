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
package javasplit;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

import javasplit.utils.Base;
import javasplit.utils.Currency;
import javasplit.utils.Stamp;

public class Purchase extends Base {
    protected Group group;
    protected Member purchaser;
    protected LinkedHashMap<String, Member> recipients;
    protected Double amount;
    protected Stamp date;
    protected String title;
    protected Currency currency;

    Purchase(Group group, String purchaser, List<String> recipients, Double amount,
            Stamp date, String title, Currency currency) {
        this.group = group;
        this.setPurchaser(purchaser);
        this.setRecipients(recipients);
        this.amount = amount;
        this.date = date;
        this.title = title;
        this.currency = currency;

        this.link();
    }

    public Double getAmount() {
        return this.group.exchange(this.amount, this.currency);
    }

    public Double getAmountPerMember() {
        return this.getAmount() / this.numberOfRecipients();
    }

    public boolean isPurchaser(String name) {
        return this.purchaser.getName().equals(name);
    }

    public boolean isRecipient(String name) {
        return this.recipients.containsKey(name);
    }

    protected void link() {
        HashSet<Member> members = new HashSet<Member>(this.recipients.values());
        members.add(this.purchaser);

        for (Member member : members) {
            member.addParticipation(this);
        }
    }

    public int numberOfRecipients() {
        return this.recipients.size();
    }

    @Override
    public LinkedHashMap<String, Object> serialize() {
        LinkedHashMap<String, Object> hash_map = new LinkedHashMap<String, Object>();
        hash_map.put("purchaser", this.purchaser.getName());
        hash_map.put("recipients", Base.forEach_r(this.recipients.keySet(), a -> a));
        hash_map.put("amount", this.amount);
        hash_map.put("currency", this.currency.name());
        hash_map.put("date", this.date.toString());
        hash_map.put("title", this.title);
        return hash_map;
    }

    private void setPurchaser(String purchaser) {
        this.purchaser = this.group.getMemberByName(purchaser);
    }

    private void setRecipients(List<String> recipients) {
        this.recipients = new LinkedHashMap<String, Member>();
        for (String recipient : recipients) {
            this.recipients.put(recipient, this.group.getMemberByName(recipient));
        }
    }

    @Override
    public String toString() {
        String tmp = String.format("%s (%s) %s: %.2f%s -> %s", this.title, this.date,
                this.purchaser.getName(), this.amount, this.currency,
                String.join(", ", this.recipients.keySet()));

        return tmp;
    }

    protected void unlink() {
        HashSet<Member> members = new HashSet<Member>(this.recipients.values());
        members.add(this.purchaser);

        for (Member member : members) {
            member.removePurchase(this);
        }
    }
}