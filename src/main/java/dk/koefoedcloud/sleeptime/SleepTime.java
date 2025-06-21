package dk.koefoedcloud.sleeptime;

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

	private KeyBinding toggleKey;
	private boolean isOn = false;
	private final Identifier LAYER = Identifier.of("sleeptime.client.layer");

	@Override
	public void onInitializeClient() {
		MinecraftClient client = MinecraftClient.getInstance();
		registerKey();
		HudElementRegistry.addLast(LAYER, (drawContext, tickCounter) -> {
			if (!isOn) {
				return;
			}
			ClientPlayerEntity player = client.player;
			if (player == null) {
				return;
			}
			World world = player.getWorld();
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
			drawContext.drawText(client.textRenderer, min + ":" + sec, 1, 1, 0xFFFFFFFF, false);
		});
	}

	private void registerClientTick() {
		ClientTickEvents.END_CLIENT_TICK.register(minecraftClient -> {
			if (!toggleKey.wasPressed()) {
				return;
			}
			if (isOn) {
				isOn = false;
				return;
			}
			isOn = true;
		});
	}

	private void registerKey() {
        String KEY_CATEGORY = "key.category.dk.koefoedcloud.sleeptime";
        String KEY_TOGGLE = "key.dk.koefoedcloud.toggle";
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_TOGGLE,
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_O,
                KEY_CATEGORY
		));
		registerClientTick();
	}
}