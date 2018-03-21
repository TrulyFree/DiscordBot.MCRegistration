package io.github.trulyfree.discordbot.mcreg;

import co.templex.oauth.SuccessfulOAuthResponse;
import de.btobastian.javacord.entities.User;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

public interface RegistrationListener {
    void onRegister(@NonNull @NotNull User user,
                    @NonNull @NotNull SuccessfulOAuthResponse response);

    void onDeregister(@NonNull @NotNull User user);
}
