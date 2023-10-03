package io.github.moehreag.nvgmc;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.nanovg.NanoVGGL2;

public class NVGMC {
	private static long nvgContext;
	private static boolean initialized;
	private static WindowPropertiesProvider propertiesProvider;
	private static Boolean LWJGL2;

	public static boolean isLWJGL2(){
		if (LWJGL2 == null){
			try {
				Class.forName("org.lwjgl.opengl.Display");
				LWJGL2 = true;
			} catch (ClassNotFoundException e) {
				LWJGL2 = false;
			}
		}
		return LWJGL2;
	}

	private static void initNVG(){

		Class<?> providerClass;
		try {
			if (isLWJGL2()) {
				providerClass = Class.forName("io.github.moehreag.nvgmc.LWJGL2WindowPropertiesProvider");
			} else {
				providerClass = Class.forName("io.github.moehreag.nvgmc.LWJGL3WindowPropertiesProvider");
			}

			propertiesProvider = (WindowPropertiesProvider) providerClass.getConstructor().newInstance();
		} catch (Exception e){
			throw new IllegalStateException("Could not create WindowPropertiesProvider", e);
		}

		nvgContext = NanoVGGL2.nvgCreate(NanoVGGL2.NVG_ANTIALIAS);
		initialized = true;
	}

	public static NVGFont createFont(InputStream ttf) throws IOException {

		return new NVGFont(getNvgContext(), ttf);
	}

	private static long getNvgContext(){
		if (nvgContext == 0 || !initialized){
			initNVG();
		}
		return nvgContext;
	}

	public static void wrap(Consumer<Long> run){

		long ctx = getNvgContext();

		int width = propertiesProvider.getWidth();
		int height = propertiesProvider.getHeight();
		NanoVG.nvgBeginFrame(ctx, width,
				height,
				(float) width / height);

		run.accept(ctx);

		NanoVG.nvgEndFrame(ctx);
	}
}