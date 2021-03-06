package io.github.trulyfree.discordbot.mcreg;

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

@AllArgsConstructor
@FieldDefaults(makeFinal = true,
               level = AccessLevel.PRIVATE)
public class MCDeregisterCommand implements Command {
    MCRegistrationPlugin mcRegistrationPlugin;

    @Override
    public void onMatch(@NonNull @NotNull final Message message,
                        @Nullable final String s) {
        if (mcRegistrationPlugin.getConfig().getRegistry().remove(message.getAuthor().getId()) != null) {
            message.reply(String.format(
                    "Successfully deregistered user %s.",
                    message.getAuthor().getMentionTag()
            ));
            mcRegistrationPlugin.getRegistrationListeners().forEach(listener -> {
                try {
                    listener.onDeregister(message.getAuthor());
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            });
        } else {
            message.reply(String.format(
                    "Didn't find an oauth entry for user %s.",
                    message.getAuthor().getMentionTag()
            ));
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
        return "";
    }

    @NonNull
    @NotNull
    @Override
    public String getCommand() {
        return "mcdereg"; // TODO
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
            return "Deregisters your Minecraft account from this Discord account.";
        } else {
            message.reply(
                    "",
                    new EmbedBuilder().setTitle("MC Deregister Syntax").addField(
                            "Info",
                            onHelpRequest(null),
                            true
                    ).addField(
                            "Syntax",
                            "mcdereg",
                            true
                    )
            );
            return null;
        }
    }
}
