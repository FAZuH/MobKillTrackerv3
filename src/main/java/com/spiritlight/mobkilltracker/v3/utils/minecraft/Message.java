package com.spiritlight.mobkilltracker.v3.utils.minecraft;

import com.spiritlight.mobkilltracker.v3.Main;
import com.spiritlight.mobkilltracker.v3.enums.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public class Message {
    public static final String TITLE =
            Color.GOLD
                    + "["
                    + Color.GREEN
                    + "MKT "
                    + Color.YELLOW
                    + "v3"
                    + Color.GOLD
                    + "] "
                    + Color.RESET;

    public static void info(String s) {
        send(s, Color.WHITE);
    }

    public static void warn(String s) {
        send(s, Color.YELLOW);
    }

    public static void error(String s) {
        send(s, Color.RED);
    }

    public static void fatal(String s) {
        send(s, Color.DARK_RED);
    }

    public static void debug(String s) {
        if (Main.configuration.isLogging()) send(s, Color.MAGENTA);
    }

    public static void debugv(String s) {
        if (Main.configuration.doLogValid() || Main.configuration.isLogging())
            send(s, Color.MAGENTA);
    }

    public static void send(String s) {
        send0(Text.literal(TITLE + s));
    }

    public static void send(String... s) {
        if (s == null) throw new NullPointerException();
        for (String str : s) send(str);
    }

    public static void send(String s, Color color) {
        send(color + s);
    }

    public static void sendRaw(String s) {
        send0(Text.literal(s));
    }

    public static void sendRaw(Text component) {
        send0(component);
    }

    public static String formatJson(String s) {
        return s.replace("{", Color.AQUA + "{" + Color.GOLD)
                .replace("}", Color.AQUA + "}" + Color.GOLD)
                .replace("[", Color.RESET + "[" + Color.GOLD)
                .replace("]", Color.RESET + "]" + Color.GOLD)
                .replace(",", Color.RESET + "," + Color.GOLD)
                .replace(":", Color.RESET + ":" + Color.AQUA)
                .replace("'", Color.YELLOW + "'" + Color.RESET)
                .replace("\"", Color.GREEN + "\"" + Color.GOLD);
    }

    private static void send0(Text content) {
        if (MinecraftClient.getInstance().player == null) return;
        // In Fabric, we can just send it directly or use client executor
        MinecraftClient.getInstance()
                .execute(
                        () -> {
                            if (MinecraftClient.getInstance().player != null) {
                                MinecraftClient.getInstance().player.sendMessage(content, false);
                            }
                        });
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(String s) {
        return new Builder(s);
    }

    public static Text of(String content) {
        return Text.literal(content);
    }

    public static class Builder {
        private MutableText component;

        public Builder() {
            this("");
        }

        public Builder(String content) {
            this.component = Text.literal(content);
        }

        public Builder addClickEvent(ClickEvent.Action action, String value) {
            this.component =
                    this.component.styled(
                            style -> style.withClickEvent(new ClickEvent(action, value)));
            return this;
        }

        public Builder addHoverEvent(HoverEvent.Action<Text> action, Text value) {
            this.component =
                    this.component.styled(
                            style -> style.withHoverEvent(new HoverEvent(action, value)));
            return this;
        }

        public Builder addHoverEvent(HoverEvent.Action<Text> action, String value) {
            this.component =
                    this.component.styled(
                            style ->
                                    style.withHoverEvent(
                                            new HoverEvent(action, Text.literal(value))));
            return this;
        }

        public Builder addSibling(Text component) {
            this.component = this.component.append(component);
            return this;
        }

        public Builder appendText(String value) {
            this.component = this.component.append(Text.literal(value));
            return this;
        }

        public Builder setStyle(Style style) {
            this.component = this.component.fillStyle(style);
            return this;
        }

        public Text get() {
            return component;
        }

        public Text build() {
            return this.get();
        }
    }
}
