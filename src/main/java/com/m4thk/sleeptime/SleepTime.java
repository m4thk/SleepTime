package com.m4thk.sleeptime;

import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.lwjgl.glfw.GLFW;

public class SleepTime implements ClientModInitializer {

    private static final Identifier LAYER = Identifier.of("sleeptime", "client_layer");
    private static final KeyBinding.Category KEY_CATEGORY = new KeyBinding.Category(Identifier.of("sleeptime", "main"));
    private static final String KEY_TOGGLE = "key.sleeptime.toggle";

    private KeyBinding toggleKey;
    private boolean isOn = false;

    @Override
    public void onInitializeClient() {
        registerKey();
        registerHud();
        registerClientTick();
    }

    public void registerHud() {
        MinecraftClient client = MinecraftClient.getInstance();

        HudElementRegistry.addLast(LAYER, (drawContext, tickCounter) -> {
            if (!isOn) {
                return;
            }
            ClientPlayerEntity player = client.player;
            if (player == null) {
                return;
            }
            World world = player.getEntityWorld();
            if (world == null) {
                return;
            }
            if (world.isThundering()) {
                return;
            }
            long tick = world.isRaining() ? 12010 : 12542;
            long get = world.getTimeOfDay() % 24000L;
            long total = (tick - get) / 20;
            if (total <= 0) {
                return;
            }
            long min = total / 60;
            long sec = total % 60;
            String text = String.format("%02d:%02d", min, sec);
            drawContext.drawText(client.textRenderer, text, 1, 1, 0xFFFFFFFF, false);
        });
    }

    private void registerClientTick() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (toggleKey.wasPressed()) {
                isOn = !isOn;
            }
        });
    }

    private void registerKey() {
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_TOGGLE,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_O,
                KEY_CATEGORY
        ));
    }
}