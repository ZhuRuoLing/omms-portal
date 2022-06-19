package net.zhuruoling.omms.portal.main;

import net.zhuruoling.omms.client.server.session.ClientInitialSession;
import net.zhuruoling.omms.client.server.session.ClientSession;
import net.zhuruoling.omms.portal.command.CommandHandler;
import org.jline.builtins.Completers;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    static final ClientInitialSession[] atomicReference = {null};
    static final ClientSession[] session = {null};
    private static Logger logger = LoggerFactory.getLogger("Main");
    private static boolean isConnected = false;


    public static void main(String[] args) throws Exception {
        Terminal terminal = TerminalBuilder.terminal();
        var completer = new Completers.TreeCompleter(
                Completers.TreeCompleter.node("connect"),
                Completers.TreeCompleter.node("end"),
                Completers.TreeCompleter.node("refresh"),
                Completers.TreeCompleter.node("whitelist",
                        Completers.TreeCompleter.node("list"),
                        Completers.TreeCompleter.node("add"),
                        Completers.TreeCompleter.node("remove"),
                        Completers.TreeCompleter.node("create"),
                        Completers.TreeCompleter.node("delete")
                ),
                Completers.TreeCompleter.node("permission",
                        Completers.TreeCompleter.node("list"),
                        Completers.TreeCompleter.node("add"),
                        Completers.TreeCompleter.node("remove"),
                        Completers.TreeCompleter.node("modify")
                ),
                Completers.TreeCompleter.node("run",
                        Completers.TreeCompleter.node("mcdreforged"),
                        Completers.TreeCompleter.node("minecraft"),
                        Completers.TreeCompleter.node("os")
                ),
                Completers.TreeCompleter.node("test"),
                Completers.TreeCompleter.node("exit")
        );

        LineReader lineReader = LineReaderBuilder.builder().terminal(terminal).completer(completer).build();
        var commandHandler = new CommandHandler();
        while (true) {
            String line = lineReader.readLine(">");
            logger.info("CONSOLE issued a command %s".formatted(line));
            commandHandler.handle(line);
        }

    }
}
