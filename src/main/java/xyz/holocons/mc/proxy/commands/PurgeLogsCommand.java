package xyz.holocons.mc.proxy.commands;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ConsoleCommandSource;

public final class PurgeLogsCommand implements SimpleCommand {

    private final Logger logger;

    public PurgeLogsCommand(final Logger logger) {
        this.logger = logger;
    }

    @Override
    public void execute(final Invocation invocation) {
        final var logNamePattern = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}-\\d\\.log\\.gz$");

        var deleted = 0;
        try (var directoryStream = Files.newDirectoryStream(Path.of("logs"))) {
            final var daysOld = Integer.parseInt(invocation.arguments()[0]);
            final var maxKeptTime = FileTime.from(Instant.now().minus(daysOld, ChronoUnit.DAYS));

            for (var logPath : directoryStream) {
                if (logNamePattern.matcher(logPath.getFileName().toString()).matches()
                        && maxKeptTime.compareTo(PurgeLogsCommand.accessedTime(logPath)) > 0) {
                    logPath.toFile().delete();
                    deleted++;
                }
            }
        } catch (Exception e) {
        }
        logger.info("Deleted {} old log files", deleted);
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source() instanceof ConsoleCommandSource;
    }

    private static FileTime accessedTime(final Path path) throws Exception {
        final var attributes = Files.readAttributes(path, BasicFileAttributes.class);
        return Collections
                .max(List.of(attributes.lastAccessTime(), attributes.lastModifiedTime(), attributes.creationTime()));
    }
}
