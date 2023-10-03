package io.github.moehreag.nvgmc;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;

public class LWJGL2WindowPropertiesProvider implements WindowPropertiesProvider {
	@Override
	public int getHeight() {
		return MinecraftClient.getInstance().height;
	}

	@Override
	public int getWidth() {
		return MinecraftClient.getInstance().width;
	}

	@Override
	public float getScaleFactor() {
		return new Window(MinecraftClient.getInstance()).getScaleFactor();
	}


}
