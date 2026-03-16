package com.intellifix.orchestrator.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class IntellifixUtils {

    public static Long extractSimId(String simIdStr) {
        try {
            return Long.parseLong(simIdStr);
        } catch (NumberFormatException e) {
            log.error("Failed to parse Simulation Id : {}", simIdStr);
            return null;
        }
    }

    public static Integer extractSeqNum(String fixMsg) {
        try {
            Pattern pattern = Pattern.compile("34=(\\d+)");
            Matcher matcher = pattern.matcher(fixMsg);
            if (matcher.find()) {
                return Integer.parseInt(matcher.group(1));
            }
        } catch (Exception e) {
            log.warn("Failed to extract sequence number from FIX message");
        }
        return null;
    }

    public static Map<String, Object> parseFixMessage(String fixMsg) {
        Map<String, Object> parsed = new HashMap<>();
        if (fixMsg == null)
            return parsed;

        String[] tags = fixMsg.split("\u0001");
        for (String pair : tags) {
            String[] kv = pair.split("=");
            if (kv.length == 2) {
                parsed.put(kv[0], kv[1]);
            }
        }
        // If no SOH, try regex for = pattern
        if (parsed.isEmpty()) {
            Pattern pattern = Pattern.compile("(\\d+)=([^\\cA\\cM\\cJ]+)");
            Matcher matcher = pattern.matcher(fixMsg);
            while (matcher.find()) {
                parsed.put(matcher.group(1), matcher.group(2));
            }
        }
        return parsed;
    }
}
