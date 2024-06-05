package org.amalgama.utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Objects;

public class TokenUtils {
    public static final String JWT_SECRET = "~AmalgamaLab~";

    public static String newJWT(Long id) {
        JSONObject header = new JSONObject();
        header.put("alg", "HS256");
        header.put("typ", "JWT");
        JSONObject payload = new JSONObject();
        payload.put("id", id);
        payload.put("exp", System.currentTimeMillis() / 1000 + 60 * 60 * 24 * 7);   //1 week
        String message = CryptoUtils.getBase64(header.toJSONString()) + "." + CryptoUtils.getBase64(payload.toJSONString());
        String signature = CryptoUtils.getSHA256(message + JWT_SECRET);
        return message + "." + signature;
    }

    public static Long parseJWT(String token) throws ParseException {
        String[] parts = token.split("\\.");
        JSONParser parser = new JSONParser();
        if (parts.length != 3)
            return null;
        if (!Objects.equals(CryptoUtils.getSHA256(parts[0] + "." + parts[1] + JWT_SECRET), parts[2]))
            return null;
        JSONObject header = (JSONObject) parser.parse(new String(CryptoUtils.fromBase64(parts[0])));
        JSONObject payload = (JSONObject) parser.parse(new String(CryptoUtils.fromBase64(parts[1])));
        if (!header.get("alg").equals("HS256") || !header.get("typ").equals("JWT"))
            return null;
        if (System.currentTimeMillis() / 1000 > (long) payload.get("exp"))
            return null;
        return (long) payload.get("id");
    }
}
