package io.github.trulyfree.discordbot.mcreg;

import co.templex.oauth.SuccessfulOAuthResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.concurrent.ConcurrentHashMap;

@FieldDefaults(makeFinal = true,
               level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class Config {
    @Getter(AccessLevel.PACKAGE) Registry registry;

    public Config() {
        registry = new Registry();
    }

    public static class Registry extends ConcurrentHashMap<String, SuccessfulOAuthResponse> {
    }
}
