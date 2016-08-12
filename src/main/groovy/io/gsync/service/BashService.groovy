package io.gsync.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
public class BashService {

    private static final Logger logger = LoggerFactory.getLogger(BashService.class);

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLUE = "\u001B[34m";

    def call(List<String> commands, File file) {
        def result = "";

        commands.each {
            result += this.call(it as String, file) + System.lineSeparator()
        }

        return result
    }

    def String call(String cmd, File file) {
        logger.info "$ANSI_BLUE\$ >$file.name ${cmd}$ANSI_RESET"

        Process shell = new ProcessBuilder("bash", "-c", cmd).directory(file).start();
        shell.waitFor()

        if (shell.exitValue()) {
            throw new BashServiceException(shell.err.text)
        }

        def result = shell.in.text
        logger.info(result)
        return result
    }
}
