package com.spiritlight.adapters.fabric.game;

import com.spiritlight.adapters.fabric.AdaptableEntity;
import com.spiritlight.adapters.fabric.game.impl.FabricChatComponentImpl;
import net.minecraft.text.Text;

public interface FabricChatComponent extends Text, AdaptableEntity<Text> {

    static FabricChatComponent of(Text text) {
        return new FabricChatComponentImpl(text);
    }

    static FabricChatComponent of(String string) {
        return of(Text.of(string));
    }
}
