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

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import javasplit.utils.Base;
import javasplit.utils.Currency;
import javasplit.utils.InputScanner;
import javasplit.utils.Stamp;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "JavaSplit", mixinStandardHelpOptions = true,
    version = "1.0.0",
    description = "A simple Java package for money pool split development.")
class JavaSplit implements Callable<Integer> {

    @Option(names = { "-m", "--member" }, description = "Add member(s) to the group")
    boolean add_member;

    @Option(names = { "-p", "--purchase" }, description = "Add purchase(s) to the group")
    boolean add_purchase;

    @Option(names = { "-t", "--transfer" }, description = "Add transfer(s) to the group")
    boolean add_transfer;

    @Parameters(index = "0", arity = "0..1", description = "The path to a group file")
    String group_path;

    @Override
    public Integer call() throws Exception {

        InputScanner scanner = new InputScanner();
        Group group;
        if (group_path != null) {
            group = new Group(group_path);
        } else {
            String inp_title = scanner.get("Group title", "Untitled", a -> a);
            String inp_description = scanner.get("Group description", "", a -> a);
            Currency inp_currency = scanner.get("Group currency", Currency.Euro.name(),
                    Base.forEach_r(List.of(Currency.values()), a -> a.name()),
                    a -> Currency.valueOf(a));

            group = new Group(inp_title, inp_description, inp_currency);
        }

        // add member(s)
        if (add_member || group.getNumberOfMembers() == 0) {
            while (true) {
                String inp_name = scanner.get("Member name (Enter to continue)", "", a -> a);

                if (inp_name.isBlank()) {
                    break;
                } else {
                    group.addMember(inp_name);
                }
            }
        }

        // add purchase(s)
        if (add_purchase) {
            if (group.getNumberOfMembers() == 0) {
                throw new RuntimeException("No members have been defined!");
            }

            List<String> members = group.getMemberNames();
            while (true) {
                String inp_title = scanner.get("Purchase title", "Untitled", a -> a);
                String inp_purchaser = scanner.get("Purchaser", members.get(0), members, a -> a);
                List<String> inp_recipients = scanner.get("Purchase recipients (seperated by ;)",
                        String.join(";", members), members,
                        a -> (List<String>) Arrays.asList(a.split(";")));
                Double inp_amount = scanner.get("Purchase amount",
                        a -> Double.parseDouble(a));
                Currency inp_currency = scanner.get("Purchase currency", Currency.Euro.name(),
                        Base.forEach_r(List.of(Currency.values()), a -> a.name()),
                        a -> Currency.valueOf(a));
                Stamp inp_date = scanner.get("Purchase date", new Stamp().toString(),
                        a -> new Stamp(a));

                group.addPurchase(inp_purchaser, inp_recipients,
                        inp_amount, inp_date, inp_title, inp_currency);

                if (!scanner.get("Add another purchase", "n", List.of("n", "y"),
                        a -> a.toLowerCase() == "y")) {
                    break;
                }
            }
        }

        // add transfer(s)
        if (add_transfer) {
            if (group.getNumberOfMembers() == 0) {
                throw new RuntimeException("No members have been defined!");
            }

            List<String> members = group.getMemberNames();
            while (true) {
                String inp_title = scanner.get("Transfer title", "Untitled", a -> a);
                String inp_purchaser = scanner.get("Purchaser", members.get(0), members, a -> a);
                String inp_recipient = scanner.get("Transfer recipient", members.get(0), members,
                        a -> a);
                Double inp_amount = scanner.get("Transfer amount",
                        a -> Double.parseDouble(a));
                Currency inp_currency = scanner.get("Transfer currency", Currency.Euro.name(),
                        Base.forEach_r(List.of(Currency.values()), a -> a.name()),
                        a -> Currency.valueOf(a));
                Stamp inp_date = scanner.get("Transfer date", new Stamp().toString(),
                        a -> new Stamp(a));

                group.addTransfer(inp_purchaser, inp_recipient,
                        inp_amount, inp_date, inp_title, inp_currency);

                if (!scanner.get("Add another transfer", "n", List.of("n", "y"),
                        a -> a.toLowerCase() == "y")) {
                    break;
                }
            }
        }

        // print the group stats
        group.print();

        // store the group in the existing file or create a new one
        String file_path = group_path;
        if (group_path == null) {
            String tmp = group.getName().toLowerCase().replace(" ", "_");
            file_path = scanner.get("Provide file name", tmp, a -> a);
        }

        group.save(file_path);
        scanner.finish();

        return 0;
    }

    // this example implements Callable, so parsing, error handling and handling
    // user
    // requests for usage help or version help can be done with one line of code.
    public static void main(String... args) {
        int exitCode = new CommandLine(new JavaSplit()).execute(args);
        System.exit(exitCode);
    }
}