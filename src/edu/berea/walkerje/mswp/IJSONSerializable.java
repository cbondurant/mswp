package edu.berea.walkerje.mswp;

import com.github.cliftonlabs.json_simple.JsonObject;

public interface IJSONSerializable {
	/**
	 * Deserializes this object from the specified source JSON object.
	 * @param src
	 */
	public void deserialize(JsonObject src);
	
	/**
	 * Serializes this object into the specified destination JSON object.
	 * @param dest
	 */
	public void serialize(JsonObject dest);
}