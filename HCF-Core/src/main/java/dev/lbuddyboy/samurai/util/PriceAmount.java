package dev.lbuddyboy.samurai.util;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Getter
public class PriceAmount {

    public static Map<Character, Integer> CHAR_ZERO = new HashMap<>();

    private int amount;

    public PriceAmount(String amount) {
        boolean hasChar = false;
        for (Map.Entry<Character, Integer> entry : CHAR_ZERO.entrySet()) {
            if (!amount.contains(entry.getKey().toString())) continue;

            String value = amount.split(String.valueOf(entry.getKey()))[0];
            this.amount = Integer.parseInt(value) * Integer.parseInt("1" + StringUtils.repeat("0", entry.getValue()));
            hasChar = true;
        }
        if (!hasChar) this.amount = Integer.parseInt(amount);
    }

    public PriceAmount(int amount) {
        this.amount = amount;
    }

    static {
        CHAR_ZERO.put('k', 3);
        CHAR_ZERO.put('m', 6);
        CHAR_ZERO.put('b', 9);
        CHAR_ZERO.put('t', 12);
    }

}
