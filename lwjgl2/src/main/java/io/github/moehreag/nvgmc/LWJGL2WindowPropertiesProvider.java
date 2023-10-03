package io.github.moehreag.nvgmc;

import net.minecraft.client.MinecraftClient;

public class LWJGL2WindowPropertiesProvider implements WindowPropertiesProvider {
	@Override
	public int getHeight() {
		return MinecraftClient.getInstance().height;
	}

	@Override
	public int getWidth() {
		return MinecraftClient.getInstance().width;
	}
}
