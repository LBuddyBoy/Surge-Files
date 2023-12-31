package dev.lbuddyboy.samurai.team.commands.staff;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;

@CommandAlias("resetforceinvites")
@CommandPermission("op")
public class ResetForceInvitesCommand extends BaseCommand {

    @Subcommand("all")
    @Description("Reset all teams force invites to the max.")
    public static void resetforceinvites_all(Player sender) {
        ConversationFactory factory = new ConversationFactory(Samurai.getInstance()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {

            public String getPromptText(ConversationContext context) {
                return (ChatColor.GREEN  + "Are you sure you want to reset all force invites? Type §byes§a to confirm or §cno§a to quit.");
            }

            @Override
            public Prompt acceptInput(ConversationContext cc, String s) {
                if (s.equalsIgnoreCase("yes")) {
                    for (Team team : Samurai.getInstance().getTeamHandler().getTeams()) {
                        team.setForceInvites(Team.MAX_FORCE_INVITES);
                    }

                    Samurai.getInstance().getServer().broadcastMessage(ChatColor.RED.toString() + ChatColor.BOLD + "All force invites have been reset!");
                    cc.getForWhom().sendRawMessage(ChatColor.RED.toString() + ChatColor.BOLD + "All force invites have been reset!");
                    return (Prompt.END_OF_CONVERSATION);
                }

                if (s.equalsIgnoreCase("no")) {
                    cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Resetting cancelled.");
                    return (Prompt.END_OF_CONVERSATION);
                }

                cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Unrecognized response. Type §b/yes§a to confirm or §c/no§a to quit.");
                return (Prompt.END_OF_CONVERSATION);
            }

        }).withEscapeSequence("/no").withLocalEcho(false).withTimeout(10).thatExcludesNonPlayersWithMessage("Go away evil console!");

        Conversation con = factory.buildConversation(sender);
        sender.beginConversation(con);
    }


    @Default
    @Description("Reset a team's force invites to the max.")
    @CommandCompletion("@team")
    public static void resetforceinvites(Player sender, Team team) {
        team.setForceInvites(Team.MAX_FORCE_INVITES);
        sender.sendMessage(ChatColor.GREEN + team.getName() + " now has " + team.getForceInvites() + " force invites.");
    }

}
