package com.ne0nx3r0.betteralias.alias;

import com.ne0nx3r0.betteralias.BetterAlias;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


// Helper methods
public class AliasManager {
    private static BetterAlias plugin;
    private HashMap<String, Alias> aliases;

    public AliasManager(BetterAlias plugin) {
        AliasManager.plugin = plugin;

        this.loadAliases();
    }

    public final boolean loadAliases() {
        this.aliases = new HashMap<String, Alias>();

        File configFile = new File(plugin.getDataFolder(), "aliases.yml");

        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            copy(plugin.getResource("aliases.yml"), configFile);
        }

        FileConfiguration yml = YamlConfiguration.loadConfiguration(configFile);

        Set<String> aliasList = yml.getKeys(false);

        if (aliasList.isEmpty()) {
            plugin.getLogger().log(Level.WARNING, "No aliases found in aliases.yml");

            return false;
        }

        for (String sAlias : aliasList) {
            Alias alias = new Alias(
                    sAlias,
                    yml.getBoolean(sAlias + ".caseSensitive", false),
                    yml.getString(sAlias + ".permission", null));

            for (String sArg : yml.getConfigurationSection(sAlias).getKeys(false)) {
                List<AliasCommand> commandsList = new ArrayList<AliasCommand>();

                if (!sArg.equalsIgnoreCase("permission") && !sArg.equalsIgnoreCase("caseSensitive")) {
                    int iArg;

                    if (sArg.equals("*")) {
                        iArg = -1;
                    } else {
                        iArg = Integer.parseInt(sArg);
                    }

                    List<String> sArgLines = new ArrayList<String>();

                    if (yml.isList(sAlias + "." + sArg)) {
                        sArgLines = yml.getStringList(sAlias + "." + sArg);
                    } else {
                        sArgLines.add(yml.getString(sAlias + "." + sArg));
                    }

                    for (String sArgLine : sArgLines) {
                        AliasCommandTypes type = AliasCommandTypes.PLAYER;

                        int waitTime = 0;

                        if (sArgLine.contains(" ")) {
                            String sType = sArgLine.substring(0, sArgLine.indexOf(" "));

                            if (sType.equalsIgnoreCase("console")) {
                                type = AliasCommandTypes.CONSOLE;

                                sArgLine = sArgLine.substring(sArgLine.indexOf(" ") + 1);
                            } else if (sType.equalsIgnoreCase("player_as_op")) {
                                type = AliasCommandTypes.PLAYER_AS_OP;

                                sArgLine = sArgLine.substring(sArgLine.indexOf(" ") + 1);
                            } else if (sType.equalsIgnoreCase("reply")) {
                                type = AliasCommandTypes.REPLY_MESSAGE;

                                sArgLine = sArgLine.substring(sArgLine.indexOf(" ") + 1);
                            } else if (sType.equalsIgnoreCase("wait")) {
                                String[] sArgLineParams = sArgLine.split(" ");

                                try {
                                    waitTime = Integer.parseInt(sArgLineParams[1]);
                                } catch (Exception e) {
                                    plugin.getLogger().log(Level.WARNING, "Invalid wait time for command {0} in alias {1}, skipping line",
                                            new Object[]{sArgLine, sAlias});

                                    continue;
                                }

                                if (sArgLineParams[2].equalsIgnoreCase("reply")) {
                                    type = AliasCommandTypes.WAIT_THEN_REPLY;

                                    sArgLine = sArgLine.replace(sArgLineParams[0] + " " + sArgLineParams[1] + " " + sArgLineParams[2] + " ", "");
                                } else if (sArgLineParams[2].equalsIgnoreCase("console")) {
                                    type = AliasCommandTypes.WAIT_THEN_CONSOLE;

                                    sArgLine = sArgLine.replace(sArgLineParams[0] + " " + sArgLineParams[1] + " " + sArgLineParams[2] + " ", "");
                                } else {
                                    type = AliasCommandTypes.WAIT;

                                    sArgLine = sArgLine.replace(sArgLineParams[0] + " " + sArgLineParams[1] + " ", "");
                                }
                            }
                        }

                        sArgLine = this.replaceColorCodes(sArgLine);

                        commandsList.add(new AliasCommand(sArgLine, type, waitTime));
                    }

                    alias.setCommandsFor(iArg, commandsList);
                }
            }

            this.aliases.put(sAlias, alias);
        }

        return true;
    }

    public boolean sendAliasCommands(Alias alias, CommandSender cs, String commandString) {
        Player player = null;

        if (cs instanceof Player) {
            player = (Player) cs;
        }

        List<String> args = new ArrayList<String>();

        Pattern regex = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");

        Matcher regexMatcher = regex.matcher(commandString);

        while (regexMatcher.find()) {
            if (regexMatcher.group(1) != null) {
                // Add double-quoted string without the quotes
                args.add(regexMatcher.group(1));
            } else if (regexMatcher.group(2) != null) {
                // Add single-quoted string without the quotes
                args.add(regexMatcher.group(2));
            } else {
                // Add unquoted word
                args.add(regexMatcher.group());
            }
        }

        if (alias.hasCommandFor(args.size())) {
            for (AliasCommand ac : alias.getCommands(args.size())) {
                String sAliasCommand = ac.command;

                Matcher m = Pattern.compile("!([0-9a-zA-Z~*]+)").matcher(sAliasCommand);

                StringBuffer sb = new StringBuffer(sAliasCommand.length());

                while (m.find()) {
                    String text = m.group(0).substring(1);

                    if (text.equalsIgnoreCase("name")) {
                        if (player != null) {
                            text = player.getName();
                        } else {
                            cs.sendMessage("[BetterAlias] " + ChatColor.RED + "A parameter of this alias requires a player.");

                            cs.sendMessage("[BetterAlias] Line: " + ac.command);

                            return true;
                        }
                    } else if (text.equalsIgnoreCase("handItemName")) {
                        if (player != null) {
                            text = player.getItemInHand().getType().name();
                        } else {
                            cs.sendMessage("[BetterAlias] " + ChatColor.RED + "A parameter of this alias requires a player.");

                            cs.sendMessage("[BetterAlias] Line: " + ac.command);

                            return true;
                        }
                    } else if (text.equalsIgnoreCase("handItemID")) {
                        if (player != null) {
                            text = new Integer(player.getItemInHand().getTypeId()).toString();
                        } else {
                            cs.sendMessage("[BetterAlias] " + ChatColor.RED + "A parameter of this alias requires a player.");

                            cs.sendMessage("[BetterAlias] Line: " + ac.command);

                            return true;
                        }
                    } else if (text.equalsIgnoreCase("oppositeGameMode")) {
                        if (player != null) {
                            text = player.getGameMode().equals(GameMode.SURVIVAL) ? "creative" : "survival";
                        } else {
                            cs.sendMessage("[BetterAlias] " + ChatColor.RED + "A parameter of this alias requires a player.");

                            cs.sendMessage("[BetterAlias] Line: " + ac.command);

                            return true;
                        }
                    } else if (text.equalsIgnoreCase("*")) {
                        //ltrim emulation
                        while(commandString.length() > 0 && commandString.substring(0,1).equals(" ")){
                            commandString = commandString.substring(1);
                        }

                        text = commandString;
                    } else if (text.length() >= 2 && text.substring(1, 2).equalsIgnoreCase("p")) {
                        int iParam = -1;

                        try {
                            iParam = Integer.parseInt(text.substring(0, 1));
                        } catch (Exception ex) {
                        }

                        if (iParam > -1 && args.size() >= iParam) {
                            String sPlayerName = args.get(iParam - 1).toLowerCase();

                            text = "BetterAliasPlayerNotFound";

                            for (Player p : plugin.getServer().getOnlinePlayers()) {
                                if (p.getName().toLowerCase().contains(sPlayerName)) {
                                    text = p.getName();

                                    break;
                                }
                            }
                        }
                    } else {
                        int iParam = -1;

                        try {
                            iParam = Integer.parseInt(text);
                        } catch (Exception ex) {
                        }

                        if (iParam > -1 && args.size() >= iParam) {
                            text = args.get(iParam - 1);
                        } else {
                            text = "";
                        }
                    }

                    m.appendReplacement(sb, Matcher.quoteReplacement(text));
                }

                m.appendTail(sb);

                String sNewCommand = sb.toString();

                if (ac.type.equals(AliasCommandTypes.REPLY_MESSAGE)) {
                    cs.sendMessage(sNewCommand);
                } else if (ac.type.equals(AliasCommandTypes.WAIT_THEN_REPLY)) {
                    final CommandSender csWait = cs;
                    final String message = sNewCommand;

                    plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
                        public void run() {
                            csWait.sendMessage(message);
                        }

                    }, ac.waitTime);
                } else if (ac.type.equals(AliasCommandTypes.WAIT_THEN_CONSOLE)) {
                    if (player != null) {
                        plugin.getServer().getScheduler().runTaskLater(plugin, new waitConsoleCommand(sNewCommand.substring(1),
                                "[BetterAlias] " + ChatColor.AQUA + "Running console command for " + player.getName() + ": " + sNewCommand), ac.waitTime);
                    } else {
                        plugin.getServer().getScheduler().runTaskLater(plugin, new waitConsoleCommand(sNewCommand.substring(1),
                                "[BetterAlias] " + ChatColor.AQUA + "Running: " + sNewCommand), ac.waitTime);
                    }
                } else if (ac.type.equals(AliasCommandTypes.WAIT)) {
                    if (player != null) {
                        plugin.getServer().getScheduler().runTaskLater(plugin, new waitPlayerCommand(sNewCommand, player.getName()), ac.waitTime);
                    } else {
                        plugin.getServer().getScheduler().runTaskLater(plugin, new waitConsoleCommand(sNewCommand.substring(1),
                                "[BetterAlias] " + ChatColor.AQUA + "Running: " + sNewCommand), ac.waitTime);
                    }
                } else if (ac.type.equals(AliasCommandTypes.PLAYER_AS_OP) && player != null) {
                    AliasManager.plugin.getLogger().log(
                            Level.INFO,
                            "[BetterAlias] {0}Running player_as_op command for {1}: {2}",
                            new Object[]{ChatColor.AQUA, player.getName(), sNewCommand}
                    );

                    if (player.isOp() == false) {
                        try {
                            player.setOp(true);
                            AliasManager.plugin.getServer().dispatchCommand(player, sNewCommand.substring(1));
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            player.setOp(false);
                        }
                    } else {
                        AliasManager.plugin.getServer().dispatchCommand(player, sNewCommand.substring(1));
                    }
                } else if (ac.type.equals(AliasCommandTypes.CONSOLE)
                        || player == null) {
                    if (player != null) {
                        plugin.getLogger().log(Level.INFO,
                                "[BetterAlias] {0}Running console command for {1}: {2}",
                                new Object[]{ChatColor.AQUA, player.getName(), sNewCommand});
                    } else {
                        cs.sendMessage("[BetterAlias] " + ChatColor.AQUA + "Running: " + sNewCommand);
                    }

                    plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), sNewCommand.substring(1));
                } else {
                    player.chat(sNewCommand);
                }
            }

            return true;
        }

        return false;
    }

    public Collection<Alias> getAliasMatches(String sCommand) {
        String sCommandLower = sCommand.toLowerCase()+" ";

        ArrayList<Alias> aliasMatches = new ArrayList<Alias>();

        for (Alias alias : this.aliases.values()) {
            if (alias.caseSensitive) {
                if (sCommand.startsWith(alias.command+" ")) {
                    aliasMatches.add(alias);
                }
            } 
            else if (sCommandLower.startsWith(alias.command+" ")) {
                aliasMatches.add(alias);
            }
        }

        return aliasMatches;
    }

    // Delayed tasks
    private static class waitConsoleCommand implements Runnable {
        private final String message;
        private final String command;

        public waitConsoleCommand(String command, String message) {
            this.message = message;
            this.command = command;
        }

        public void run() {
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command);
        }
    }

    private static class waitPlayerCommand implements Runnable {
        private final String playerName;
        private final String command;

        public waitPlayerCommand(String command, String playerName) {
            this.playerName = playerName;
            this.command = command;
        }

        public void run() {
            Player p = plugin.getServer().getPlayer(playerName);

            if (p != null) {
                p.chat(command);
            }
        }
    }

    public void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String replaceColorCodes(String str) {
        for (ChatColor cc : ChatColor.values()) {
            str = str.replace("&" + cc.name(), cc.toString());
        }

        return str;
    }
}
