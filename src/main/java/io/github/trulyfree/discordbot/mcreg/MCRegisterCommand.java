package io.github.trulyfree.discordbot.mcreg;

import co.templex.oauth.OAuthResponse;
import co.templex.oauth.SuccessfulOAuthResponse;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.entities.message.embed.EmbedBuilder;
import io.github.trulyfree.discordbot.cmd.Command;
import io.github.trulyfree.plugins.annotation.Plugin;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

@AllArgsConstructor
@FieldDefaults(makeFinal = true,
               level = AccessLevel.PRIVATE)
public class MCRegisterCommand implements Command {
    MCRegistrationPlugin mcRegistrationPlugin;

    @Override
    public void onMatch(@NonNull @NotNull final Message message,
                        @Nullable final String s) {
        if (s == null) {
            SuccessfulOAuthResponse response = mcRegistrationPlugin.getConfig().getRegistry().get(message.getAuthor().getId());
            if (response != null) {
                message.reply(String.format(
                        "%s is registered as Minecraft user %s.",
                        message.getAuthor().getMentionTag(),
                        response.getUsername()
                ));
            } else {
                message.reply(String.format(
                        "No Minecraft user associated with %s.",
                        message.getAuthor().getMentionTag()
                ));
            }
        } else {
            mcRegistrationPlugin.getConfig().getRegistry().compute(
                    message.getAuthor().getId(),
                    (id, existing) -> {
                        if (existing != null) {
                            message.reply(String.format(
                                    "Already have an entry for user %s.",
                                    message.getAuthor().getMentionTag()
                            ));
                            return existing;
                        } else {
                            try {
                                OAuthResponse response = mcRegistrationPlugin.getQuerier().query(s);
                                if (response instanceof SuccessfulOAuthResponse) {
                                    message.reply(String.format(
                                            "Successfully registered %s as %s.",
                                            message.getAuthor().getMentionTag(),
                                            ((SuccessfulOAuthResponse) response).getUsername()
                                    ));
                                    mcRegistrationPlugin.getRegistrationListeners().forEach(listener -> {
                                        try {
                                            listener.onRegister(
                                                    message.getAuthor(),
                                                    (SuccessfulOAuthResponse) response
                                            );
                                        } catch (Throwable throwable) {
                                            throwable.printStackTrace();
                                        }
                                    });
                                    return (SuccessfulOAuthResponse) response;
                                } else {
                                    message.reply(String.format(
                                            "Token validation for %s failed: %s",
                                            message.getAuthor().getMentionTag(),
                                            response.getMessage()
                                    ));
                                }
                            } catch (IOException e) {
                                message.reply(String.format(
                                        "Failed to query mc-oauth: %s",
                                        e.getClass().getName()
                                ));
                            }
                        }
                        return null;
                    }
            );
        }
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Language("RegExp")
    @NonNull
    @NotNull
    @Override
    public String getCommandMatchingRegex() {
        return "[0-9]{6}";
    }

    @NonNull
    @NotNull
    @Override
    public String getCommand() {
        return "mcreg";
    }

    @NonNull
    @NotNull
    @Override
    public Plugin getProvider() {
        return MCRegistrationPlugin.class.getAnnotation(Plugin.class);
    }

    @Nullable
    @Override
    public String onHelpRequest(@Nullable final Message message) {
        if (message == null) {
            return "Authenticates you via Minecraft across all instances of this bot. Login to srv.mc-oauth.net on Minecraft to fetch a validation token.";
        } else {
            message.reply(
                    "",
                    new EmbedBuilder().setTitle("MC Register Syntax").addField(
                            "Info",
                            onHelpRequest(null),
                            true
                    ).addField(
                            "Syntax",
                            "mcreg <token>",
                            true
                    )
            );
            return null;
        }
    }
}
