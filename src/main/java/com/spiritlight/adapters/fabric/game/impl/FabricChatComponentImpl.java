package com.spiritlight.adapters.fabric.game.impl;

import com.spiritlight.adapters.fabric.game.FabricChatComponent;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class FabricChatComponentImpl implements FabricChatComponent, Text {

    private final Text representingEntity;

    public FabricChatComponentImpl(Text representingEntity) {
        this.representingEntity = Objects.requireNonNull(representingEntity);
    }

    @Override
    public Style getStyle() {
        return representingEntity.getStyle();
    }

    @Override
    public TextContent getContent() {
        return representingEntity.getContent();
    }

    @Override
    public List<Text> getSiblings() {
        return representingEntity.getSiblings();
    }

    @Override
    public OrderedText asOrderedText() {
        return representingEntity.asOrderedText();
    }

    @Override
    public @NotNull Text getRepresentativeEntity() {
        return representingEntity;
    }
}
