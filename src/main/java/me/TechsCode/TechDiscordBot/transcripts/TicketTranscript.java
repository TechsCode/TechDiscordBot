package me.TechsCode.TechDiscordBot.transcripts;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.TechsCode.TechDiscordBot.module.modules.TicketModule;
import me.TechsCode.TechDiscordBot.util.PasswordGenerator;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.*;
import java.util.function.Consumer;

public class TicketTranscript {

    private static final int AT_A_TIME = 100;

    private final TextChannel channel;
    private final TicketTranscriptOptions options;
    private final List<Message> messages;

    private final String id, password;

    private final boolean fetchAll;

    public TicketTranscript(TextChannel channel, TicketTranscriptOptions options) {
        this.channel = channel;
        this.options = options;
        this.messages = new ArrayList<>();
        this.fetchAll = options.getMessagesAmount() == -1;

        this.id = UUID.randomUUID().toString();
        this.password = PasswordGenerator.generateRandomPassword(10);
    }

    public static TicketTranscript buildTranscript(TextChannel channel, TicketTranscriptOptions options) {
        return new TicketTranscript(channel, options);
    }

    public void build(Consumer<JsonObject> consumer) {
        fetch(m -> {
            JsonObject object = new JsonObject();

            JsonArray array = new JsonArray();
            m.forEach(msg -> array.add(buildMessage(msg)));

            object.add("creator", buildMember(TicketModule.getMemberFromTicket(channel)));
            object.addProperty("name", this.channel.getName());
            object.addProperty("id", this.id);
            object.addProperty("password", this.password);

            object.add("members", buildMembers());
            object.add("messages", array);

            consumer.accept(object);
        });
    }

    private JsonObject buildMessage(Message message) {
        JsonObject object = new JsonObject();

        TicketTranscriptMessageType type = getType(message);
        object.addProperty("type", type.name().toLowerCase());

        if(canType(TicketTranscriptMessageType.MESSAGE)) {
            object.addProperty("message", message.getContentDisplay());

            JsonObject author = new JsonObject();

            author.addProperty("name", message.getAuthor().getName());
            author.addProperty("discriminator", message.getAuthor().getDiscriminator());
            author.addProperty("tag", message.getAuthor().getAsTag());
            author.addProperty("avatar", message.getAuthor().getEffectiveAvatarUrl());
            author.addProperty("bot", message.getAuthor().isBot());
            author.addProperty("system", message.getAuthor().isSystem());
            author.addProperty("color", message.getMember() == null ? null : message.getMember().getColorRaw());

            object.add("author", author);

            object.addProperty("created", message.getTimeCreated().toEpochSecond() * 1000);

            if(message.getTimeEdited() != null)
                object.addProperty("edited", message.getTimeEdited().toEpochSecond() * 1000);
        }

        if(canType(TicketTranscriptMessageType.ATTACHMENT) && message.getAttachments().size() > 0) {
            JsonArray attachments = new JsonArray();

            message.getAttachments().forEach(a -> {
                JsonObject attachment = new JsonObject();

                attachment.addProperty("id", a.getId());
                attachment.addProperty("name", a.getFileName());
                attachment.addProperty("extension", a.getFileExtension());
                attachment.addProperty("size", a.getSize());
                attachment.addProperty("url", a.getUrl());
                attachment.addProperty("proxyUrl", a.getProxyUrl());
                attachment.addProperty("contentType", a.getContentType());

                if(a.getHeight() != -1)
                    attachment.addProperty("height", a.getHeight());
                if(a.getWidth() != -1)
                    attachment.addProperty("width", a.getWidth());

                attachments.add(attachment);
            });

            object.add("attachments", attachments);
        }

        if(canType(TicketTranscriptMessageType.EMBED) && message.getEmbeds().size() > 0) {
            JsonArray embeds = new JsonArray();

            message.getEmbeds().forEach(e -> {
                JsonObject embed = new JsonObject();
                //TODO: Add thumbnail / image.

                embed.addProperty("type", e.getType().name().toLowerCase());
                embed.addProperty("title", e.getTitle());
                embed.addProperty("url", e.getUrl());
                embed.addProperty("color", e.getColorRaw());
                embed.addProperty("description", e.getDescription());
                embed.addProperty("unix", e.getTimestamp() == null ? null : e.getTimestamp().toEpochSecond() * 1000);

                if(e.getAuthor() != null) {
                    JsonObject author = new JsonObject();

                    author.addProperty("name", e.getAuthor().getName());
                    author.addProperty("url", e.getAuthor().getUrl());
                    author.addProperty("icon", e.getAuthor().getIconUrl());
                    author.addProperty("iconProxy", e.getAuthor().getProxyIconUrl());

                    embed.add("author", author);
                }

                if(e.getFields().size() > 0) {
                    JsonArray fields = new JsonArray();

                    e.getFields().forEach(f -> {
                        JsonObject field = new JsonObject();

                        field.addProperty("name", f.getName());
                        field.addProperty("value", f.getValue());
                        field.addProperty("inline", f.isInline());

                        fields.add(field);
                    });

                    embed.add("fields", fields);
                }

                embeds.add(embed);
            });

            object.add("embeds", embeds);
        }

        return object;
    }

    private JsonArray buildMembers() {
        JsonArray array = new JsonArray();
        HashMap<String, List<JsonObject>> byRoles = new HashMap<>();

        channel.getMembers().forEach(member -> {
            JsonObject m = buildMember(member);
            String role = m.get("role").getAsString();

            if(byRoles.containsKey(role)) {
                byRoles.put(role, Collections.singletonList(m));
            } else {
                List<JsonObject> byRolesM = new ArrayList<>();
                byRolesM.add(m);

                byRoles.put(role, byRolesM);
            }
        });

        byRoles.forEach((role, members) -> {
            JsonObject object = new JsonObject();

            JsonArray ms = new JsonArray();
            members.forEach(ms::add);

            object.addProperty("role", role);
            object.add("members", ms);

            array.add(object);
        });

        return array;
    }

    private JsonObject buildMember(Member member) {
        JsonObject object = new JsonObject();
        if(member == null)
            return object;

        object.addProperty("name", member.getUser().getName());
        object.addProperty("staff", member.getRoles().stream().anyMatch(r -> r.getName().equals("Staff")));
        object.addProperty("avatar", member.getUser().getEffectiveAvatarUrl());
        object.addProperty("role", member.getRoles().size() == 0 ? "Member" : member.getRoles().get(0).getName());
        object.addProperty("color", member.getColorRaw());

        return object;
    }

    private TicketTranscriptMessageType getType(Message message) {
        return message.getEmbeds().size() > 0 ? TicketTranscriptMessageType.EMBED : (message.getAttachments().size() > 0 ? TicketTranscriptMessageType.ATTACHMENT : TicketTranscriptMessageType.MESSAGE);
    }

    private boolean canType(TicketTranscriptMessageType type) {
        return options.getTypes().length == 0 || Arrays.asList(options.getTypes()).contains(type);
    }

    private void fetch(Consumer<List<Message>> consumer) {
        fetchMore(null, consumer);
    }

    private void fetchMore(Message message, Consumer<List<Message>> consumer) {
        channel.getHistoryBefore(message == null ? channel.getLatestMessageIdLong() : message.getIdLong(), fetchAll ? AT_A_TIME : Math.min(getMessagesLeft(), AT_A_TIME)).queue(m -> {
            messages.addAll(m.getRetrievedHistory());

            if(fetchAll && m.size() < AT_A_TIME || !fetchAll && messages.size() >= options.getMessagesAmount()) {
                consumer.accept(messages);
            } else {
                fetchMore(m.getRetrievedHistory().get(m.size() - 1), consumer);
            }
        });
    }

    public String getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public String getUrl() {
        return "https://transcripts.techscode.com/" + this.id + "/" + this.password;
    }

    private int getMessagesLeft() {
        return options.getMessagesAmount() - messages.size();
    }

    public TextChannel getChannel() {
        return channel;
    }

    public TicketTranscriptOptions getOptions() {
        return options;
    }

    public List<Message> getMessages() {
        return messages;
    }
}
