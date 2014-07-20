package com.peacecraftec.storage.yaml;

import com.peacecraftec.storage.MapBasedStorage;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.representer.Representer;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class YamlStorage extends MapBasedStorage {

	private static Class<? extends SafeConstructor> constructor = SafeConstructor.class;
	private static Class<? extends Representer> representer = Representer.class;
	
	public static void setParameters(Class<? extends SafeConstructor> constructor, Class<? extends Representer> representer) {
		YamlStorage.constructor = constructor;
		YamlStorage.representer = representer;
	}
	
	private static SafeConstructor createConstructor() {
		try {
			return constructor.getDeclaredConstructor().newInstance();
		} catch(Exception e) {
			System.err.println("Could not create YAML constructor.");
			e.printStackTrace();
			return null;
		}
	}
	
	private static Representer createRepresenter() {
		try {
			return representer.getDeclaredConstructor().newInstance();
		} catch(Exception e) {
			System.err.println("Could not create YAML representer.");
			e.printStackTrace();
			return null;
		}
	}
	
	private Yaml yaml;
	
	public YamlStorage() {
		super();
		DumperOptions options = new DumperOptions();
		options.setIndent(4);
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		this.yaml = new Yaml(createConstructor(), createRepresenter(), options);
	}
	
	public YamlStorage(String path) {
		super(path);
		DumperOptions options = new DumperOptions();
		options.setIndent(4);
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		this.yaml = new Yaml(createConstructor(), createRepresenter(), options);
	}
	
	public YamlStorage(File file) {
		super(file);
		DumperOptions options = new DumperOptions();
		options.setIndent(4);
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		this.yaml = new Yaml(createConstructor(), createRepresenter(), options);
	}

	@Override
	public void load(InputStream in) {
		this.root = (Map<String, Object>) this.yaml.load(in);
		if(this.root == null) this.root = new HashMap<String, Object>();
	}
	
	@Override
	public void save(OutputStream out) {
		OutputStreamWriter writer = null;
		try {
			writer = new OutputStreamWriter(out, "UTF-8");
			this.yaml.dump(this.root, writer);
		} catch (IOException e) {
			System.err.println("Failed to save config file to stream!");
			e.printStackTrace();
		} finally {
			if(writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
				}
			}
		}
	}

}
