package xyz.holocons.mc.proxy.commands;

import com.velocitypowered.api.command.RawCommand;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.text.minimessage.MiniMessage;

public final class BroadcastCommand implements RawCommand {

    private static final String PREFIX = "<yellow>[<dark_red>Broadcast</dark_red>]</yellow> ";

    private final ProxyServer proxy;

    public BroadcastCommand(final ProxyServer proxy) {
        this.proxy = proxy;
    }

    @Override
    public void execute(final Invocation invocation) {
        final var message = MiniMessage.miniMessage().deserialize(PREFIX + invocation.arguments());

        proxy.getAllPlayers().forEach(player -> player.sendMessage(message, MessageType.SYSTEM));
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source() instanceof ConsoleCommandSource;
    }
}
