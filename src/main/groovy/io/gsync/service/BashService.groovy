package io.gsync.service

import org.springframework.stereotype.Service

@Service
public class BashService {

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
        println "$ANSI_BLUE\$ >$file.name ${cmd}$ANSI_RESET"

        Process shell = new ProcessBuilder("bash", "-c", cmd).directory(file).start();
        shell.waitFor()

        if (shell.exitValue()) {
            throw new RuntimeException(shell.err.text)
        }

        return shell.in.text
    }
}
