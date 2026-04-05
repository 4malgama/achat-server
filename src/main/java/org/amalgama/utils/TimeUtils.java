package org.amalgama.utils;

public class TimeUtils {

    public static long getUnixTime() {
        return System.currentTimeMillis() / 1000;
    }

    public static long daysToSeconds(long days) {
        return days * 86400;
    }

    public static long parseStringToUnix(String time) {
        String[] parts = time.split("[dhms]");
        long seconds = 0;
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (part.isEmpty()) continue;
            long value = Long.parseLong(part);
            switch (i) {
                case 0: seconds += value * 86400; break;
                case 1: seconds += value * 3600; break;
                case 2: seconds += value * 60; break;
                case 3: seconds += value; break;
            }
        }
        return seconds;
    }
}
