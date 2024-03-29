package xyz.holocons.mc.proxy;

import org.slf4j.Logger;

import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;

import xyz.holocons.mc.proxy.commands.BroadcastCommand;
import xyz.holocons.mc.proxy.commands.ConnectCommand;
import xyz.holocons.mc.proxy.commands.PurgeLogsCommand;

@Plugin(id = "holoconsproxy", name = "HoloCons Proxy", version = "1.0-SNAPSHOT",
        url = "https://holocons.xyz", authors = { "dlee13" })
public final class VelocityPlugin {

    private static final MinecraftChannelIdentifier CMIB_CHANNEL = MinecraftChannelIdentifier.create("cmib", "fromproxy");

    private final ProxyServer proxy;
    private final Logger logger;

    @Inject
    public VelocityPlugin(final ProxyServer proxy, final Logger logger) {
        this.proxy = proxy;
        this.logger = logger;
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        final var commandManager = proxy.getCommandManager();
        commandManager.register("broadcast", new BroadcastCommand(proxy));
        commandManager.register("connect", new ConnectCommand(proxy));
        commandManager.register("purgelogs", new PurgeLogsCommand(logger));
        logger.info("Registered commands");
    }

    @Subscribe
    public void onServerPostConnect(ServerPostConnectEvent event) {
        final var player = event.getPlayer();
        final var previousServer = event.getPreviousServer();
        final var currentServer = player.getCurrentServer();

        if (currentServer.isPresent()) {
            var out = ByteStreams.newDataOutput();
            out.writeUTF("CMIServerSwitchEvent");
            out.writeUTF(player.getUniqueId().toString());
            out.writeUTF(player.getUsername());
            out.writeUTF(previousServer == null ? "" : previousServer.getServerInfo().getName());
            out.writeUTF(currentServer.get().getServerInfo().getName());

            for (var server : proxy.getAllServers()) {
                if (!server.getPlayersConnected().isEmpty() || server == previousServer) {
                    server.sendPluginMessage(CMIB_CHANNEL, out.toByteArray());
                }
            }
        }
    }
}
