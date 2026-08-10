package com.supermap;

import com.supermap.config.CommandProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@EnableConfigurationProperties(CommandProperties.class)
@RequiredArgsConstructor
@Component
@Slf4j
public class CommandExecutor {

    private final CommandProperties commandProperties;

    private final ThreadPoolTaskExecutor commandThreadPoolExecutor;

    public CommandResult execute(List<String> command) {
        Process process = null;

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            // 不要 redirectErrorStream(true)
            // stdout 和 stderr 分开，方便判断错误
            processBuilder.redirectErrorStream(false);

            log.debug("执行 command: {}", String.join(" ", command));
            process = processBuilder.start();
            Process currentProcess = process;

            CompletableFuture<String> stdoutFuture = CompletableFuture.supplyAsync(
                    () -> read(currentProcess.getInputStream()),
                    commandThreadPoolExecutor
            );
            CompletableFuture<String> stderrFuture = CompletableFuture.supplyAsync(
                    () -> read(currentProcess.getErrorStream()),
                    commandThreadPoolExecutor
            );

            boolean finished = process.waitFor(commandProperties.getCommandTimeout().toMillis(), TimeUnit.MILLISECONDS);

            if (!finished) {
                process.destroyForcibly();

                stdoutFuture.cancel(true);
                stderrFuture.cancel(true);

                throw new CommandException("command timeout: " + String.join(" ", command));
            }

            int exitCode = process.exitValue();

            String stdout = stdoutFuture.join();
            String stderr = stderrFuture.join();

            CommandResult result = new CommandResult(exitCode, stdout, stderr);

            if (!result.success()) {
                throw new CommandException(buildErrorMessage(command, result));
            }

            return result;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            process.destroyForcibly();

            throw new CommandException("command interrupted", e);
        } catch (IOException e) {
            throw new CommandException("Failed to execute command: " + String.join(" ", command), e);
        }
    }

    private String read(InputStream inputStream) {
        try (inputStream) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new CommandException("Failed to read process output", e);
        }
    }

    private String buildErrorMessage(List<String> command, CommandResult result) {
        return """
                command failed.
                command: %s
                exitCode: %s
                stdout:
                %s
                stderr:
                %s
                """.formatted(
                String.join(" ", command),
                result.exitCode(),
                result.stdout(),
                result.stderr());
    }

}