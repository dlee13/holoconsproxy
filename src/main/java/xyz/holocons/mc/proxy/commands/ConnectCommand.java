package xyz.holocons.mc.proxy.commands;

import java.util.List;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.ProxyServer;

public final class ConnectCommand implements SimpleCommand {

    private final ProxyServer proxy;

    public ConnectCommand(final ProxyServer proxy) {
        this.proxy = proxy;
    }

    @Override
    public void execute(final Invocation invocation) {
        if (invocation.arguments().length == 0) {
            return;
        }

        proxy.getServer(invocation.arguments()[0]).ifPresent(server -> {
            if (invocation.arguments().length == 1) {
                proxy.getAllPlayers().forEach(player -> player.createConnectionRequest(server).fireAndForget());
            } else {
                List.of(invocation.arguments()).subList(1, invocation.arguments().length)
                        .forEach(argument -> proxy.getPlayer(argument)
                                .ifPresent(player -> player.createConnectionRequest(server).fireAndForget()));
            }
        });
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source() instanceof ConsoleCommandSource;
    }
}
