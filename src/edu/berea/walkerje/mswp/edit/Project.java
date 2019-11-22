package edu.berea.walkerje.mswp.edit;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;

import edu.berea.walkerje.mswp.EAssetType;
import edu.berea.walkerje.mswp.IAsset;
import edu.berea.walkerje.mswp.IJSONSerializable;
import edu.berea.walkerje.mswp.gfx.Spritesheet;
import edu.berea.walkerje.mswp.gfx.Tilesheet;
import edu.berea.walkerje.mswp.play.GameLevel;

public class Project implements IJSONSerializable{
	private static Project currentProject = null;
	
	//Projects contain named collections of all
	@SuppressWarnings("unchecked")//unchecked, useless warning in this case.
	private HashMap<String, IAsset>[] assets = new HashMap[EAssetType.values().length];
	
	private String name = "Unnamed";
	private File projectDir;
	
	/**
	 * Private constructor for creating a new project.
	 */
	private Project() {
		setCurrentProject(this);
		for(int i = 0; i < assets.length; i++) {
			assets[i] = new HashMap<String, IAsset>();
		}
	}
	
	/**
	 * Constructs and deserializes the project with the specified "project.json" file.
	 * @param projectFile
	 */
	public Project(File projectFile) {
		setCurrentProject(this);
		this.projectDir = projectFile.getParentFile();
		for(int i = 0; i < assets.length; i++) {
			assets[i] = new HashMap<String, IAsset>();
		}
		
		FileReader reader;
		try {
			reader = new FileReader(projectFile);
			JsonObject obj = (JsonObject) Jsoner.deserialize(reader);
			reader.close();
			deserialize(obj);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Adds the specified asset to this project.
	 * @param asset
	 */
	public void addAsset(IAsset asset) {
		assets[asset.getAssetType().ordinal()].put(asset.getAssetName(), asset);
	}
	
	/**
	 * Returns an asset with the specified type and name.
	 * @param type of the asset
	 * @param name of the asset
	 * @return the asset
	 */
	public IAsset getAsset(EAssetType type, String name) {
		return assets[type.ordinal()].get(name);
	}
	
	/**
	 * Returns the array of all assets owned by this project.
	 * @return
	 */
	public HashMap<String, IAsset>[] getAssets(){
		return assets;
	}

	@Override
	public void deserialize(JsonObject src) {
		name = (String)src.get("name");
		for(EAssetType aType : EAssetType.values()) {
			if(aType == EAssetType.LEVEL)
				continue;//Defer level loading until after all assets are loaded.
			
			final String aTypeName = aType.name().toLowerCase();
			JsonObject assetField = (JsonObject)src.get(aTypeName);
			
			if(assetField == null)
				continue;
			
			assetField.forEach((k,v)->{
				JsonObject serialObj = (JsonObject)v;
				switch(aType) {
				case TILESHEET:
					addAsset(Tilesheet.projectRead(projectDir, k, serialObj));
					break;
				case SPRITESHEET:
					addAsset(Spritesheet.projectRead(projectDir, k, serialObj));
					break;
				case TILE_STAMP:
					addAsset(TileStamp.projectRead(projectDir, k, serialObj));
				default:
					//TODO: Finish
				}
			});
		}
		
		JsonArray levelList = (JsonArray)src.get("levels");
		if(levelList != null) {
			levelList.forEach((e)->{
				String name = (String)e;
				try(FileReader r = new FileReader(new File(projectDir, String.format("levels/%s.json", e)))){
					JsonObject obj = (JsonObject)Jsoner.deserialize(r);
					GameLevel level = GameLevel.projectRead(projectDir, name, obj);
					assets[EAssetType.LEVEL.ordinal()].put(name, level);
				} catch (Exception e1) {
					e1.printStackTrace();}
			});
		}
	}

	/**
	 * Serializes this project to the specified JSON object.
	 * --sidenote, in retrospect I think the IJSONSerializable is a bit unnecessary.
	 */
	@Override
	public void serialize(JsonObject dest) {
		dest.put("name", name);
		for(EAssetType aType : EAssetType.values()) {
			if(aType == EAssetType.LEVEL)
				continue;//Defer level loading until after all assets are loaded.
			
			final String aTypeName = aType.name().toLowerCase();
			JsonObject assetField = new JsonObject();
			
			assets[aType.ordinal()].forEach((k,v)->{
				JsonObject obj = new JsonObject();
				v.serializeProject(projectDir, obj);
				assetField.put(v.getAssetName(), obj);
			});
			
			dest.put(aTypeName, assetField);
		}
		
		if(!assets[EAssetType.LEVEL.ordinal()].isEmpty()) {
			final File levelDir = new File(projectDir, "levels");
			JsonArray levelNameList = new JsonArray();
			assets[EAssetType.LEVEL.ordinal()].forEach((k,v)->{
				GameLevel level = (GameLevel)v;
				levelNameList.add(k);
				
				final File levelFile = new File(levelDir, String.format("%s.json", k));
				levelFile.getParentFile().mkdirs();
				try(FileWriter levelFileWriter = new FileWriter(levelFile)){
					JsonObject fileObj = new JsonObject();
					level.serializeProject(projectDir, fileObj);
					fileObj.toJson(levelFileWriter);
					levelFileWriter.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			dest.put("levels", levelNameList);
		}
	}
	
	/**
	 * Returns the name of this project.
	 * @return
	 */
	public String getProjectName() {
		return name;
	}
	
	/**
	 * Saves the specified project.
	 * @param p
	 * @throws IOException
	 */
	public static void write(Project p) throws IOException {
		JsonObject obj = new JsonObject();
		p.serialize(obj);
		FileWriter writer = new FileWriter(new File(p.projectDir, "project.json"));
		obj.toJson(writer);
		writer.close();
	}
	
	/**
	 * Reads the specified project.
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws JsonException
	 */
	public static Project read(File file) throws IOException, JsonException {
		try(FileReader reader = new FileReader(file)){
			Project p = new Project(file);
			JsonObject obj = (JsonObject) Jsoner.deserialize(reader);
			p.deserialize(obj);
			reader.close();
			return p;
		}
	}
	
	/**
	 * Creates an instance of a project with the specified name and root directory
	 * @param name of the project
	 * @param projDir , root of the project.
	 * @return
	 */
	public static Project createInstance(String name, File projDir) {
		Project p = new Project();
		p.projectDir = projDir;
		p.name = name;
		return p;
	}
	
	/**
	 * TODO: FIXME, singleton style is bad practice. Implies only one project open in the editor, which might not be true in the future.
	 * @return the current active project.
	 */
	public static Project getCurrentProject() {
		return currentProject;
	}
	
	/**
	 * Sets the current active project.
	 * @param p
	 */
	public static void setCurrentProject(Project p) {
		currentProject = p;
	}
}