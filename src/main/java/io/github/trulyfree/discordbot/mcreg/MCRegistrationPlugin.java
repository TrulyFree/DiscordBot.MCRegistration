package io.github.trulyfree.discordbot.mcreg;

import co.templex.oauth.Querier;
import com.google.gson.JsonSyntaxException;
import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.listener.Listener;
import io.github.trulyfree.discordbot.Bot;
import io.github.trulyfree.discordbot.BotPlugin;
import io.github.trulyfree.discordbot.cmd.CommandPlugin;
import io.github.trulyfree.discordbot.config.ConfigurationPlugin;
import io.github.trulyfree.discordbot.config.ConfiguredBotPlugin;
import io.github.trulyfree.plugins.annotation.Plugin;
import io.github.trulyfree.plugins.plugin.PluginBuilder;
import io.github.trulyfree.plugins.plugin.PluginManager;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@FieldDefaults(makeFinal = true,
               level = AccessLevel.PRIVATE)
@Plugin(name = "minecraft_registrar",
        builder = MCRegistrationPlugin.Builder.class,
        manager = Bot.class,
        dependsOn = {
                ConfigurationPlugin.class,
                CommandPlugin.class
        })
public class MCRegistrationPlugin implements ConfiguredBotPlugin<Config> {
    @Getter Querier querier = new Querier();
    Config config;

    private MCRegistrationPlugin(final PluginManager<? super BotPlugin> pluginManager) {
        AtomicReference<Config> temp = new AtomicReference<>();
        if (!pluginManager.findPlugin(
                ConfigurationPlugin.class,
                configurationPlugin -> {
                    try {
                        temp.set(configurationPlugin.getConfig(this));
                    } catch (FileNotFoundException | JsonSyntaxException e) {
                        temp.set(new Config());
                    }
                }
        )) {
            throw new IllegalArgumentException("ConfigurationPlugin must be defined and accessible for CommandPlugin to run.");
        }
        config = temp.get();
        if (!pluginManager.findPlugin(
                CommandPlugin.class,
                commandPlugin -> {
                    commandPlugin.registerCommand(new MCDeregisterCommand(this));
                    commandPlugin.registerCommand(new MCRegisterCommand(this));
                }
        )) {
            throw new IllegalArgumentException("CommandPlugin must be defined and accessible for CommandPlugin to run.");
        }
    }

    @Override
    public void setAPI(@NonNull @NotNull final DiscordAPI discordAPI) {
    }

    @Override
    public void shutdown() {
    }

    @NonNull
    @NotNull
    @Override
    public List<Listener> getListeners() {
        return Collections.emptyList();
    }

    @NonNull
    @NotNull
    @Override
    public Config getConfig() {
        return config;
    }

    @NonNull
    @NotNull
    @Override
    public Class<? extends Config> getConfigClass() {
        return Config.class;
    }

    public static class Builder implements PluginBuilder<BotPlugin> {
        @NonNull
        @NotNull
        @Override
        public MCRegistrationPlugin build(@NonNull @NotNull final PluginManager<? super BotPlugin> pluginManager) {
            return new MCRegistrationPlugin(pluginManager);
        }
    }
}
