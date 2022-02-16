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
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.function.Function;

import javasplit.utils.Base;
import javasplit.utils.Currency;
import javasplit.utils.Stamp;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "JavaSplit", mixinStandardHelpOptions = true, version = "JavaSplit 1.0", description = "A simple Java package for money pool split development.")
class JavaSplit implements Callable<Integer> {
    Scanner scanner;

    @Option(names = { "-m", "--member" }, description = "Add a member(s) to the group")
    boolean add_member;

    @Option(names = { "-p", "--purchase" }, description = "Add a purchase(s) to the group")
    boolean add_purchase;

    @Option(names = { "-t", "--transfer" }, description = "Add transfer(s) to the group")
    boolean add_transfer;

    @Parameters(index = "0", arity = "0..1", description = "The path to a group file")
    String group_path;

    @Override
    public Integer call() throws Exception { // your business logic goes here...
        // load or create a group
        Group group;
        scanner = new Scanner(System.in); // Create a Scanner object

        if (group_path != null) {
            group = new Group(group_path);
        } else {
            String inp_title = user_input("Group title", "Untitled", null, a -> a);
            String inp_description = user_input("Group description", "", null, a -> a);
            Currency inp_currency = user_input("Group currency", Currency.Euro.name(),
                    Base.forEach_r(List.of(Currency.values()), a -> a.name()),
                    a -> Currency.valueOf(a));

            group = new Group(inp_title, inp_description, inp_currency);
        }

        // add member(s)
        if (add_member || group.getNumberOfMembers() == 0) {
            while (true) {
                String inp_name = user_input("Member name (Enter to continue)", "", null, a -> a);

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
                String inp_title = user_input("Purchase title", "Untitled", null, a -> a);
                String inp_purchaser = user_input("Purchaser", members.get(0), members, a -> a);
                List<String> inp_recipients = user_input("Purchase recipients (seperated by ;)",
                    String.join(";", members), members,
                    a -> (List<String>) Arrays.asList(a.split(";")));
                Double inp_amount = user_input("Purchase amount", null, null, a -> Double.parseDouble(a));
                Currency inp_currency = user_input("Purchase currency", Currency.Euro.name(),
                Base.forEach_r(List.of(Currency.values()), a -> a.name()),
                a -> Currency.valueOf(a));
                Stamp inp_date = user_input("Purchase date", new Stamp().toString(), null, a -> new Stamp(a));

                group.addPurchase(inp_purchaser, inp_recipients,
                    inp_amount, inp_date, inp_title, inp_currency);

                if (!user_input("Add another purchase", "n", List.of("n", "y"), a -> a == "y")) {
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
                String inp_title = user_input("Transfer title", "Untitled", null, a -> a);
                String inp_purchaser = user_input("Purchaser", members.get(0), members, a -> a);
                String inp_recipient = user_input("Transfer recipient", members.get(0), members, a -> a);
                Double inp_amount = user_input("Transfer amount", null, null, a -> Double.parseDouble(a));
                Currency inp_currency = user_input("Transfer currency", Currency.Euro.name(),
                Base.forEach_r(List.of(Currency.values()), a -> a.name()),
                a -> Currency.valueOf(a));
                Stamp inp_date = user_input("Transfer date", new Stamp().toString(), null, a -> new Stamp(a));

                group.addTransfer(inp_purchaser, inp_recipient,
                    inp_amount, inp_date, inp_title, inp_currency);

                if (!user_input("Add another transfer", "n", List.of("n", "y"), a -> a == "y")) {
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
            file_path = user_input("Provide file name", tmp, null, a -> a);
        }

        group.save(file_path);
        scanner.close();

        return 0;
    }

    // this example implements Callable, so parsing, error handling and handling
    // user
    // requests for usage help or version help can be done with one line of code.
    public static void main(String... args) {
        int exitCode = new CommandLine(new JavaSplit()).execute(args);
        System.exit(exitCode);
    }

    public <T> T user_input(String description, String default_value,
            List<String> options, Function<? super String, ? extends T> functor) {

        String des_str = description;
        if (default_value != null) {
            des_str = String.format("%s [%s]", des_str, default_value);
        }

        if (options != null) {
            des_str = String.format("%s (%s)", des_str, String.join(",", Base.forEach_r(options, a -> a)));
        }

        System.out.print(String.format("%s: ", des_str));
        String user_input = scanner.nextLine(); // Read user input

        if (user_input.isBlank() && default_value == null) {
            throw new RuntimeException("Required input not provided!");
        }

        return functor.apply(user_input.isBlank() ? default_value : user_input);
    }
}