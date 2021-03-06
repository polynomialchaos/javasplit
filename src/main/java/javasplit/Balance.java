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

import javasplit.utils.Currency;
import javasplit.utils.TimeStamp;

/**
 * Balance class for a temporary transfer (not linked).
 */
public class Balance extends Transfer {
    /**
     * Initialize a Balance object.
     */
    public Balance(Group group, String purchaser, String recipient,
            Double amount, Currency currency, TimeStamp date) {
        super(group, "Pending balance", purchaser, recipient, amount, currency, date);
    }

    /**
     * Do not link a Balance.
     */
    @Override
    protected void link() {
        // do not link
    }

    /**
     * Convert Balance to a linked Transfer.
     *
     * @return A Transfer object.
     */
    public Transfer toTransfer() {
        String recipient = recipients.values().iterator().next().getName();
        return group.addTransfer(title, purchaser.getName(), recipient,
                amount, currency, date);
    }

    /**
     * Nothing to unlink in a Balance.
     */
    @Override
    protected void unlink() {
        // nothing to unlink
    }
}