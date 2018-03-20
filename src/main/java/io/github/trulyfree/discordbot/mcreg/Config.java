package io.github.trulyfree.discordbot.mcreg;

import co.templex.oauth.SuccessfulOAuthResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@FieldDefaults(makeFinal = true,
               level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class Config {
    Registry registry;

    public Config() {
        registry = new Registry();
    }

    public Map<String, SuccessfulOAuthResponse> getRegistry() {
        return Collections.unmodifiableMap(registry);
    }

    public static class Registry extends ConcurrentHashMap<String, SuccessfulOAuthResponse> {
    }
}
