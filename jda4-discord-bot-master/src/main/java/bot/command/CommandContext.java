package bot.command;

import bot.handlers.CommandHandler;
import bot.utils.BotUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public class CommandContext {

    private final GuildMessageReceivedEvent event;
    private final CommandHandler cmdHandler;
    private final String prefix;
    private final String invoke;
    private final List<String> args;

    public CommandContext(GuildMessageReceivedEvent event, List<String> args, String invoke, String prefix, CommandHandler cmdHandler) {
        this.event = event;
        this.cmdHandler = cmdHandler;
        this.prefix = prefix;
        this.invoke = invoke;
        this.args = args;
    }

    public GuildMessageReceivedEvent getEvent() {
        return this.event;
    }

    public CommandHandler getCmdHandler() {
        return this.cmdHandler;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getInvoke() {
        return this.invoke;
    }

    public List<String> getArgs() {
        return this.args;
    }

    public Guild getGuild() {
        return this.getEvent().getGuild();
    }

    public String getGuildId() {
        return this.getEvent().getGuild().getId();
    }

    public TextChannel getChannel() {
        return this.getEvent().getChannel();
    }

    public Message getMessage() {
        return this.getEvent().getMessage();
    }

    public Member getMember() {
        return this.getEvent().getMember();
    }

    public Member getSelfMember() {
        return this.getGuild().getSelfMember();
    }

    public JDA getJDA() {
        return this.getEvent().getJDA();
    }

    public User getAuthor() {
        return this.getEvent().getAuthor();
    }

    public String getArgsJoined() {
        return String.join(" ", this.getArgs());
    }

    public String getArgsJoinedAfter(int after) {
        int i = 0;

        List<String> args = new ArrayList<>();

        for (String arg : this.getArgs()) {
            if (i > after) {
                args.add(arg);
                continue;
            }
            i++;
        }

        return String.join(" ", args);
    }

    public void reply(String message) {
        BotUtils.sendMsg(this.getChannel(), message);
    }

    public void reply(MessageEmbed embed) {
        BotUtils.sendEmbed(this.getChannel(), embed);
    }

    public void replyWithSuccess(String message) {
        BotUtils.sendSuccessWithMessage(this.getMessage(), message);
    }

    public void replyWithError(String message) {
        BotUtils.sendErrorWithMessage(this.getMessage(), message);
    }

    public void replyError(String message) {
        BotUtils.sendErrorEmbed(this.getChannel(), message);
    }

}
