package xyz.holocons.mc.proxy.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.ProxyServer;

public final class SendCommand implements SimpleCommand {

    private final ProxyServer proxy;

    public SendCommand(final ProxyServer proxy) {
        this.proxy = proxy;
    }

    @Override
    public void execute(final Invocation invocation) {
        if (invocation.arguments().length != 2) {
            return;
        }

        proxy.getPlayer(invocation.arguments()[0]).ifPresent(player -> proxy.getServer(invocation.arguments()[1])
                .ifPresent(targetServer -> player.createConnectionRequest(targetServer).fireAndForget()));
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source() instanceof ConsoleCommandSource;
    }
}
