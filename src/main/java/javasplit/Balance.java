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

import javasplit.utils.Currency;
import javasplit.utils.Stamp;

public class Balance extends Transfer {
    Balance(Group group, String purchaser, String recipient, Double amount,
            Stamp date, Currency currency) {
        super(group, purchaser, recipient, amount, date, "Pending balance", currency);
    }

    public void toTransfer() {
        String recipient  = this.recipients.values().iterator().next().getName();
        this.group.addTransfer(this.purchaser.getName(), recipient, this.amount,
            this.date, this.title, this.currency);
    }

    @Override
    protected void link() {
        // do not link
    }

    @Override
    protected void unlink() {
        // nothing to unlink
    }
}