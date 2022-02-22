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
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileReader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import javasplit.Group;
import javasplit.Member;
import javasplit.Purchase;
import javasplit.Transfer;
import javasplit.utils.Currency;
import javasplit.utils.Stamp;

public class TestGroup {
    private static String path_1 = "src/test/java/res/javasplit.json";
    private static String path_2 = "build/test_group.json";

    @Test
    void testGroup() {
        // Test: group construction with value
        Group group = new Group("JavaSplit",
                "A Java package for money pool split development.", Currency.Euro);
        assertTrue(group.getTurnover() == 0.0);
        group.setExchangeRate(Currency.USD, 1.19);
        group.setTime("23.06.2021 07:53:55");

        // Test: add members
        Member member_1 = group.addMember("member_1");
        member_1.setTime("23.06.2021 07:53:55");

        Member member_2 = group.addMember("member_2");
        member_2.setTime("23.06.2021 07:53:55");

        // Test: add purchases
        Purchase purchase = group.addPurchase("purchase_1", "member_1",
                List.of("member_1", "member_2"),
                100.0, Currency.Euro, new Stamp("23.06.2021 07:54:09"));
        purchase.setTime("23.06.2021 07:54:12");

        purchase = group.addPurchase("purchase_2", "member_1",
                List.of("member_2"),
                100.0, Currency.Euro, new Stamp("23.06.2021 07:54:21"));
        purchase.setTime("23.06.2021 07:54:22");

        purchase = group.addPurchase("purchase_3", "member_1",
                List.of("member_1", "member_2"),
                200.0, Currency.USD, new Stamp("23.06.2021 07:57:19"));
        purchase.setTime("23.06.2021 07:57:19");

        // Test: add purchases
        Transfer transfer = group.addTransfer("transfer_1", "member_1", "member_1",
                200.0, Currency.USD, new Stamp("23.06.2021 07:57:19"));
        transfer.setTime("23.06.2021 07:57:19");

        // Test: toDict()
        LinkedHashMap<String, Object> tmp = group.toDict();
        assertTrue(tmp.containsKey("stamp"));

        // Test: toString()
        group.print();
        System.out.println(group.toString());

        // Test: save
        group.save(path_2);

        // Test: compare
        try {
            Gson gson = new Gson();

            JsonReader reader_1 = new JsonReader(new FileReader(path_1));
            Map<?, ?> gson_root_1 = gson.fromJson(reader_1, Map.class);

            JsonReader reader_2 = new JsonReader(new FileReader(path_2));
            Map<?, ?> gson_root_2 = gson.fromJson(reader_2, Map.class);
            assertTrue(gson_root_1.equals(gson_root_2));

            reader_1.close();
            reader_2.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}