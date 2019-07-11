package com.gigs2go.framework;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Expects and sends a Json String<br>
 *  
 * @author Tim
 *
 */
public class CommandPayload extends Payload {
	private static final Logger LOG = LoggerFactory.getLogger(CommandPayload.class);

	private String cmd;
	private Map<String, String> values = new HashMap<>();;

	public CommandPayload(String topic, byte[] input) {
		super(topic, input);
	}

	public CommandPayload(String topic, String input) {
		super(topic);
		String[] fields = input.split(" ");
		this.cmd = fields[0];
		LOG.info("Cmd is '{}'", this.cmd);
		String[] nameVal;
		for (int i = 1; i < fields.length; i++) {
			nameVal = fields[i].split("=");
			values.put(nameVal[0], nameVal[1]);
		}
	}

	@Override
	public byte[] getBytes() {
		byte[] result = super.getBytes();
		if (result == null) {
			StringBuilder tmp = new StringBuilder(cmd);
			for (Map.Entry<String, String> entry : values.entrySet()) {
				tmp.append(' ').append(entry.getKey()).append('=').append(entry.getValue());
			}
			result = tmp.toString().getBytes();
			super.setBytes(result);
		}
		return result;
	}

	public String getCmd() {
		return cmd;
	}

	public String getValue(String name) {
		return values.get(name);
	}

	@Override
	public String toString() {
		return "CommandPayload [cmd=" + cmd + ", values=" + values + ", bytes='" + new String(getBytes()) + "', topic="
				+ getTopic() + ", type=" + getType() + "]";
	}
}
