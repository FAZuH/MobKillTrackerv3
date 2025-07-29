package com.spiritlight.adapters.fabric.entity.impl;

import com.spiritlight.adapters.fabric.entity.FabricPlayer;
import com.spiritlight.adapters.fabric.game.FabricChatComponent;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;

public class FabricPlayerImpl implements FabricPlayer {

    private final PlayerEntity entity;

    public FabricPlayerImpl(PlayerEntity entity) {
        this.entity = entity;
    }

    @Override
    public @NotNull PlayerEntity getRepresentativeEntity() {
        return entity;
    }

    @Override
    public void sendMessage(FabricChatComponent text) {
        entity.sendMessage(text, false);
    }

    @Override
    public FabricChatComponent getName() {
        return FabricChatComponent.of(entity.getName());
    }

    @Override
    public FabricChatComponent getDisplayName() {
        return FabricChatComponent.of(entity.getDisplayName());
    }

    @Override
    public FabricChatComponent getCustomName() {
        return FabricChatComponent.of(entity.getCustomName());
    }

    @Override
    public double getX() {
        return entity.getX();
    }

    @Override
    public double getY() {
        return entity.getY();
    }

    @Override
    public double getZ() {
        return entity.getZ();
    }

    @Override
    public String toString() {
        return String.valueOf(entity);
    }
}
