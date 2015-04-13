package nl.tudelft.ewi.sorcerers;

import java.io.Reader;
import java.util.List;

import nl.tudelft.ewi.sorcerers.model.Warning;

import org.jvnet.hk2.annotations.Contract;

@Contract
public interface LogParser {
	List<Warning> parse(Reader r);
}