package io.github.moehreag.nvgmc;

import net.minecraft.client.MinecraftClient;

public class LWJGL3WindowPropertiesProvider implements WindowPropertiesProvider {
	@Override
	public int getHeight() {
		return MinecraftClient.getInstance().getWindow().getHeight();
	}

	@Override
	public int getWidth() {
		return MinecraftClient.getInstance().getWindow().getWidth();
	}
}
