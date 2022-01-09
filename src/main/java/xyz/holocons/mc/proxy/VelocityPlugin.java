package xyz.holocons.mc.proxy;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import java.util.Optional;
import org.slf4j.Logger;
import xyz.holocons.mc.proxy.commands.SendCommand;

@Plugin(id = "holoconsproxy", name = "HoloCons Proxy", version = "1.0-SNAPSHOT",
        url = "https://holocons.xyz", authors = { "dlee13" })
public class VelocityPlugin {

    private static final MinecraftChannelIdentifier CMIB_CHANNEL = MinecraftChannelIdentifier.create("cmib", "fromproxy");

    private final ProxyServer proxy;
    private final Logger logger;

    @Inject
    public VelocityPlugin(ProxyServer proxy, Logger logger) {
        this.proxy = proxy;
        this.logger = logger;
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        CommandManager commandManager = proxy.getCommandManager();
        commandManager.register("send", new SendCommand(proxy));
        logger.info("Registered commands");
    }

    @Subscribe
    public void onServerPostConnect(ServerPostConnectEvent event) {
        final Player player = event.getPlayer();
        final RegisteredServer previousServer = event.getPreviousServer();
        final Optional<ServerConnection> currentServer = player.getCurrentServer();

        if (currentServer.isPresent()) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("CMIServerSwitchEvent");
            out.writeUTF(player.getUniqueId().toString());
            out.writeUTF(player.getUsername());
            out.writeUTF(previousServer == null ? "" : previousServer.getServerInfo().getName());
            out.writeUTF(currentServer.get().getServerInfo().getName());

            for (RegisteredServer server : proxy.getAllServers()) {
                if (!server.getPlayersConnected().isEmpty() || server == previousServer) {
                    server.sendPluginMessage(CMIB_CHANNEL, out.toByteArray());
                    logger.info(String.format("Sent CMIServerSwitchEvent to %s", server.getServerInfo().getName()));
                }
            }
        }
    }
}
