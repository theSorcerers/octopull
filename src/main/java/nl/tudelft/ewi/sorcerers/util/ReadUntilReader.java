package nl.tudelft.ewi.sorcerers.util;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;

public class ReadUntilReader extends FilterReader {
	private boolean open;
	private char[] delimiter;

	public ReadUntilReader(Reader inputReader, char[] delimiter) {
		super(inputReader);
		this.open = true;
		this.delimiter = delimiter;
	}

	private int block_read(char[] b, int off, int len) throws IOException {
		int current_offset = 0;
		int read;
		
		do {
			read = super.read(b, off + current_offset, len - current_offset);
			if (read > 0) {
				current_offset += read;
			}
		} while (read != -1 && current_offset < len);
		
		if (current_offset == 0) {
			return -1;
		} else {
			return current_offset;
		}
	}

	@Override
	public int read() throws IOException {
		if (!open) return -1;
		
		super.mark(delimiter.length);
		try {
			char[] arr = new char[delimiter.length];
			int len = block_read(arr, 0, delimiter.length);
			if (len == delimiter.length && Arrays.equals(arr, delimiter)) {
				return -1;
			}
		} finally {
			super.reset();
		}
		int read = super.read();
		System.out.print((char) read);
		return read;
	}
	
	@Override
	public int read(char[] b) throws IOException {
		return this.read(b, 0, b.length);
	}
	
	@Override
	public int read(char[] b, int off, int len) throws IOException {
		// TODO more efficient implementation
		int read = 0, res = 0;
		while (read < len && res != -1) {
			res = this.read();
			if (res != -1) {
				b[read++] = (char) res;
			}
		}
		if (read == 0) {
			return -1;
		} else {
			return read;
		}
	}
	
	@Override
	public boolean markSupported() {
		// TODO should this support marks?
		return false;
	}
	
	@Override
	public void close() throws IOException {
		if (open) {
			int read;
			do {
				read = this.read();
			} while (read != -1);
			
			this.skip(delimiter.length);
			
			open = false;
		}
	}
}
