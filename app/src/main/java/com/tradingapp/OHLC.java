package com.tradingapp;

import java.util.Arrays;
import java.util.List;

/**
 * Created by rishabhjain on 3/5/18.
 */

public class OHLC {
    private long timestamp;
    private float open;
    private float high;
    private float low;
    private float close;
    private long volume;

    public OHLC(String timestamp, String open, String high, String low, String close, String volume) {
        this.timestamp = Long.parseLong(timestamp);
        this.open = Float.parseFloat(open);
        this.high = Float.parseFloat(high);
        this.low = Float.parseFloat(low);
        this.close = Float.parseFloat(close);
        this.volume = Long.parseLong(volume);
    }

    public long getTimestamp() {
        return timestamp;
    }

    public float getOpen() {
        return open;
    }

    public float getHigh() {
        return high;
    }

    public float getLow() {
        return low;
    }

    public float getClose() {
        return close;
    }

    public long getVolume() {
        return volume;
    }

    public static OHLC convertStringToOHLC(String value) {
        List<String> items = Arrays.asList(value.split(","));
        if (items.get(0).substring(0,1).equals("\"")) {
            return new OHLC(items.get(0).substring(1), items.get(1), items.get(2), items.get(3), items.get(4), items.get(5));
        } else {
            return new OHLC(items.get(0), items.get(1), items.get(2), items.get(3), items.get(4), items.get(5));
        }

    }
}
