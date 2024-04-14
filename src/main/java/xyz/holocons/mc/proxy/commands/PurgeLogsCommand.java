package xyz.holocons.mc.proxy.commands;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

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
        final var directory = Path.of("logs");
        final var matcher = directory.getFileSystem().getPathMatcher("regex:^\\d{4}-\\d{2}-\\d{2}-\\d+\\.log\\.gz$");
        final var filter = new DirectoryStream.Filter<Path>() {

            @Override
            public boolean accept(final Path entry) throws IOException {
                return matcher.matches(entry.getFileName());
            }
        };

        var deleted = 0;
        try (var directoryStream = Files.newDirectoryStream(directory, filter)) {
            final var daysOld = Integer.parseInt(invocation.arguments()[0]);
            final var staleTime = FileTime.from(Instant.now().minus(daysOld, ChronoUnit.DAYS));

            for (var path : directoryStream) {
                if (PurgeLogsCommand.isFileStale(path, staleTime)) {
                    path.toFile().delete();
                    deleted++;
                }
            }
        } catch (Exception e) {
        }
        logger.info("Deleted {} stale log files", deleted);
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source() instanceof ConsoleCommandSource;
    }

    private static boolean isFileStale(final Path path, final FileTime staleTime) throws Exception {
        final var attributes = Files.readAttributes(path, BasicFileAttributes.class);
        return staleTime.compareTo(attributes.lastModifiedTime()) > 0;
    }
}
