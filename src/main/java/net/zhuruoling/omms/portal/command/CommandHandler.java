package net.zhuruoling.omms.portal.command;

import net.zhuruoling.omms.client.command.Command;
import net.zhuruoling.omms.client.server.session.ClientInitialSession;
import net.zhuruoling.omms.client.server.session.ClientSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.net.InetAddress;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class CommandHandler {
    private ClientSession session = null;
    private ClientInitialSession initialSession = null;
    private boolean isConnected = false;
    private Logger logger = LoggerFactory.getLogger("CommandHandler");
    private HashMap<String,List<String>> whitelistMap = new HashMap<>();
    public CommandHandler() {
    }

    public void handle(String line){
        String[] command = line.split(" ");
        if (command[0] == "exit"){
            if (isConnected){
                try {
                    session.close();
                } catch (IOException | NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException |
                         BadPaddingException | InvalidKeyException e) {
                    e.printStackTrace();
                }
                isConnected = false;
                logger.info("Disconnected.");
            }
            System.exit(0);
        }
        if (Objects.equals(command[0], "connect")){
            try {
                initialSession = new ClientInitialSession(InetAddress.getByName(command[1]),Integer.parseInt(command[2]));
                session = initialSession.init(Integer.parseInt(command[3]));
                logger.info("Successfully connected to %s:%s (code:%s)".formatted(command[1],command[2],command[3]));
                //TODO:get information from server
                var message = session.send(new Command("WHITELIST_LIST",new String[]{}));
                if (message.getMsg() == "NO_WHITELIST"){
                    logger.info("No whitelists added to this server.");
                }
                else {
                    var whitelists = Arrays.stream(message.getLoad()).toList();
                    whitelists.forEach(it -> {
                        try {
                            var result = session.send(new Command("WHITELIST_GET",new String[]{it}));
                            if (!Objects.equals(result.getMsg(), "OK")){
                                logger.error("Failed to fetch whitelist %s".formatted(it));
                            }
                            whitelistMap.put(it, Arrays.stream(result.getLoad()).toList());
                        } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException |
                                 BadPaddingException | InvalidKeyException | IOException e) {
                           e.printStackTrace();
                        }
                    });
                }
                isConnected = true;
            }
            catch (Exception e){
                e.printStackTrace();
                return;
            }
            return;
        }
        if (isConnected){
            try {
                switch (command[0]){
                    case "end" -> {
                        session.close();
                        isConnected = false;
                        logger.info("Disconnected.");
                    }
                    case "whitelist" -> {
                        switch (command[1]) {
                            case "list" -> whitelistMap.forEach((k, v) -> {
                                logger.info("whitelist %s has those players:".formatted(k));
                                logger.info("   %s".formatted(v.toString()));
                            });
                            case "add" -> {

                            }
                        }
                    }
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        else {
            logger.error("Central server not connected!");
        }
    }
}
