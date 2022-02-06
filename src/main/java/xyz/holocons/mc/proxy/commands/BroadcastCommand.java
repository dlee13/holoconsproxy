package xyz.holocons.mc.proxy.commands;

import com.velocitypowered.api.command.RawCommand;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public final class BroadcastCommand implements RawCommand {

    private final ProxyServer proxy;

    public BroadcastCommand(final ProxyServer proxy) {
        this.proxy = proxy;
    }

    @Override
    public void execute(final Invocation invocation) {
        final var message = Component.text('[', NamedTextColor.YELLOW)
                .append(Component.text("Broadcast", NamedTextColor.DARK_RED))
                .append(Component.text("] ", NamedTextColor.YELLOW))
                .append(Component.text(invocation.arguments(), NamedTextColor.DARK_GREEN));

        proxy.getAllPlayers().forEach(player -> player.sendMessage(message, MessageType.SYSTEM));
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source() instanceof ConsoleCommandSource;
    }
}
