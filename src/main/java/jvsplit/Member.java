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

import java.util.ArrayList;
import java.util.LinkedHashMap;

import jvsplit.utils.Base;

public class Member extends Base {
    private String name;
    private ArrayList<Purchase> participations;

    Member(String name) {
        this.name = name;
        this.participations = new ArrayList<Purchase>();
    }

    public void addParticipation(Purchase purchase) {
        this.participations.add(purchase);
    }

    public double balance() {
        double balance = 0.0;

        for (Purchase participation : this.participations) {
            if (participation.isPurchaser(this.name)) {
                balance += participation.getAmount();
            }

            if (participation.isRecipient(this.name)) {
                balance -= participation.getAmountPerMember();
            }
        }

        return balance;
    }

    public String getName() {
        return name;
    }

    public void removePurchase(Purchase participation) {
        this.participations.remove(participation);
    }

    @Override
    public LinkedHashMap<String, Object> serialize() {
        LinkedHashMap<String, Object> hash_map = new LinkedHashMap<String, Object>();
        hash_map.put("name", this.name);
        return hash_map;
    }
}