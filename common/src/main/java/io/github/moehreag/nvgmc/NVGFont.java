/*
 * Sol Client - an open source Minecraft client
 * Copyright (C) 2021-2023  TheKodeToad and Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.moehreag.nvgmc;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.function.Supplier;

import lombok.Getter;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.system.MemoryUtil;

public final class NVGFont implements AutoCloseable {

	private final int handle;
	private final ByteBuffer buffer;
	@Getter
	private int size = 8;

	NVGFont(long ctx, InputStream in) throws IOException {
		buffer = mallocAndRead(in);
		handle = NanoVG.nvgCreateFontMem(ctx, "", buffer, false);
	}

	private ByteBuffer mallocAndRead(InputStream in) throws IOException {
		try (ReadableByteChannel channel = Channels.newChannel(in)) {
			ByteBuffer buffer = MemoryUtil.memAlloc(8192);

			while (channel.read(buffer) != -1)
				if (buffer.remaining() == 0)
					buffer = MemoryUtil.memRealloc(buffer, buffer.capacity() + buffer.capacity() * 3 / 2);

			buffer.flip();

			return buffer;
		}
	}

	public void withSize(int size, Runnable action) {
		int oldSize = this.size;
		this.size = size;
		action.run();
		this.size = oldSize;
	}

	// ew
	@SuppressWarnings("unchecked")
	public <T> T withSize(int size, Supplier<T> action) {
		Object[] result = new Object[1];
		withSize(size, (Runnable) () -> result[0] = action.get());
		return (T) result[0];
	}


	public void bind(long ctx) {
		NanoVG.nvgFontFaceId(ctx, handle);
		NanoVG.nvgFontSize(ctx, size);
	}

	@Override
	public void close() {
		MemoryUtil.memFree(buffer);
	}

	public float getWidth(long ctx, String string) {
		bind(ctx);
		float[] bounds = new float[4];
		NanoVG.nvgTextBounds(ctx, 0, 0, string, bounds);
		return bounds[2];
	}

	public float getLineHeight(long ctx) {
		bind(ctx);
		float[] ascender = new float[1];
		float[] descender = new float[1];
		float[] lineh = new float[1];
		NanoVG.nvgTextMetrics(ctx, ascender, descender, lineh);
		return lineh[0];
	}

	public float renderString(long ctx, String string, float x, float y) {
		return NanoVG.nvgText(ctx, x, y + getLineHeight(ctx), string);
	}

}
