package edu.berea.walkerje.mswp;

import java.io.File;

import com.github.cliftonlabs.json_simple.JsonObject;

public interface IAsset{
	public static final String BAD_ASSET_NAME = "NONAME";
	
	/**
	 * @return the type of this asset.
	 */
	public EAssetType getAssetType();
	
	/**
	 * Serialize this asset given project information.
	 * @param projectDir -- project root directory.
	 * @param obj -- JSON object to serialize into.
	 */
	public void serializeProject(File projectDir, JsonObject obj);
	
	/**
	 * Deserialize this asset given project information.
	 * @param projectDir -- project root directory.
	 * @param assetName -- name of the asset.
	 * @param obj -- JSON object to get read from.
	 */
	public void deserializeProject(File projectDir, String assetName, JsonObject obj);
	
	/**
	 * @return the name of this asset.
	 */
	default public String getAssetName() {
		return BAD_ASSET_NAME;
	}
}