package nl.tudelft.ewi.sorcerers.util;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.junit.Test;

public class ReadUntilInputReaderTest {
	private Reader inputReaderFromString(String string) {
		return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(string.getBytes())));
	}
	
	@Test
	public void should_read_like_normal_stream() throws IOException {
		Reader verifyReader = inputReaderFromString("some_test_string\r\nabc");
		Reader inputReader = inputReaderFromString("some_test_string\r\nabc");
		ReadUntilReader ruReader = new ReadUntilReader(inputReader, "\u0000".toCharArray());
		try {
			int read = -1;
			do {
				read = verifyReader.read();
				assertEquals(read, ruReader.read());
			} while (read != -1);
		} finally {
			verifyReader.close();
			ruReader.close();
			inputReader.close();
		}
	}
	
	@Test
	public void should_end_at_delimiter() throws IOException {
		String input = "a\000b\000\001\002c";
		Reader inputReader = inputReaderFromString(input);
		ReadUntilReader ruReader = new ReadUntilReader(inputReader, "\000\001\002".toCharArray());
		try {
			assertEquals('a', ruReader.read());
			assertEquals('\000', ruReader.read());
			assertEquals('b', ruReader.read());
			assertEquals(-1, ruReader.read());
		} finally {
			ruReader.close();
			inputReader.close();
		}
	}
	
	@Test
	public void should_end_block_at_delimiter() throws IOException {
		String input = "a\000b\000\001\002cdefefefefe";
		Reader inputReader = inputReaderFromString(input);
		ReadUntilReader ruReader = new ReadUntilReader(inputReader, "\000\001\002".toCharArray());
		try {
			char[] read = new char[6];
			assertEquals(3, ruReader.read(read));
			assertEquals(-1, ruReader.read());
		} finally {
			ruReader.close();
			inputReader.close();
		}
	}
	
	@Test
	public void should_end_block_offset_at_delimiter() throws IOException {
		String input = "a\000b\000\001\002cdefefefefe";
		Reader inputReader = inputReaderFromString(input);
		ReadUntilReader ruReader = new ReadUntilReader(inputReader, "\000\001\002".toCharArray());
		try {
			char[] read = new char[6];
			assertEquals(3, ruReader.read(read, 1, 4));
			assertEquals(-1, ruReader.read());
		} finally {
			ruReader.close();
			inputReader.close();
		}
	}
	
	@Test
	public void should_skip_leftovers_at_close() throws IOException {
		String input = "beforeXXXafter\r\n";
		Reader inputReader = inputReaderFromString(input);
		ReadUntilReader ruReader = new ReadUntilReader(inputReader, "XXX".toCharArray());
		BufferedReader reader = new BufferedReader(inputReader);
		try {
			ruReader.close();
			assertEquals("after", reader.readLine());
		} finally {
			reader.close();
		}
	}
	
	@Test
	public void should_skip_leftovers_once_at_close() throws IOException {
		String input = "beforeXXXafter\r\n";
		Reader inputReader = inputReaderFromString(input);
		ReadUntilReader ruReader = new ReadUntilReader(inputReader, "XXX".toCharArray());
		BufferedReader reader = new BufferedReader(inputReader);
		try {
			ruReader.close();
			ruReader.close();
			assertEquals("after", reader.readLine());
		} finally {
			reader.close();
		}
	}
}
