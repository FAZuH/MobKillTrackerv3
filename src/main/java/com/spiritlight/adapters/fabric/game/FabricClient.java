package com.spiritlight.adapters.fabric.game;

import com.spiritlight.adapters.fabric.AdaptableEntity;
import com.spiritlight.adapters.fabric.entity.FabricEntity;
import com.spiritlight.adapters.fabric.entity.FabricPlayer;
import com.spiritlight.adapters.fabric.game.impl.FabricClientImpl;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.client.MinecraftClient;

public interface FabricClient extends AdaptableEntity<MinecraftClient> {

    FabricPlayer getPlayer();

    List<FabricEntity> getEntities(Predicate<FabricEntity> filter);

    default List<FabricEntity> getEntities(Class<?> clazz) {
        return getEntities(e -> clazz.isAssignableFrom(e.getClass()));
    }

    default List<FabricEntity> getEntities() {
        return getEntities(e -> true);
    }

    static FabricClient of(MinecraftClient client) {
        return new FabricClientImpl(client);
    }

    static FabricClient getInstance() {
        return new FabricClientImpl(MinecraftClient.getInstance());
    }
}
