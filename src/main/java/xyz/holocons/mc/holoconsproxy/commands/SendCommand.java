package xyz.holocons.mc.holoconsproxy.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.ProxyServer;

public class SendCommand implements SimpleCommand {

    private final ProxyServer proxy;

    public SendCommand(ProxyServer proxy) {
        this.proxy = proxy;
    }

    @Override
    public void execute(final Invocation invocation) {
        if (invocation.arguments().length != 2) {
            return;
        }

        proxy.getServer(invocation.arguments()[1]).ifPresent(target -> proxy.getPlayer(invocation.arguments()[0])
                .ifPresent(player -> player.createConnectionRequest(target).fireAndForget()));
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source() instanceof ConsoleCommandSource;
    }
}
