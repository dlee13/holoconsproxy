package xyz.holocons.mc.proxy.commands;

import com.velocitypowered.api.command.RawCommand;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public final class BroadcastCommand implements RawCommand {

    private final ProxyServer proxy;

    public BroadcastCommand(final ProxyServer proxy) {
        this.proxy = proxy;
    }

    @Override
    public void execute(final Invocation invocation) {
        final var message = Component.text('[', TextColor.color(0xFFFF55))
                .append(Component.text("Broadcast", TextColor.color(0xAA0000)))
                .append(Component.text("] ", TextColor.color(0xFFFF55)))
                .append(Component.text(invocation.arguments(), TextColor.color(0x00AA00)));

        proxy.getAllPlayers().forEach(player -> player.sendMessage(message, MessageType.SYSTEM));
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source() instanceof ConsoleCommandSource;
    }
}
