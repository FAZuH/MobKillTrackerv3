package com.spiritlight.adapters.fabric.entity;

import com.spiritlight.adapters.fabric.entity.impl.FabricPlayerImpl;
import com.spiritlight.adapters.fabric.game.FabricChatComponent;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;

public interface FabricPlayer extends FabricEntity {

    void sendMessage(FabricChatComponent text);

    @Override @NotNull PlayerEntity getRepresentativeEntity();

    static FabricPlayer from(PlayerEntity entity) {
        return new FabricPlayerImpl(entity);
    }
}
