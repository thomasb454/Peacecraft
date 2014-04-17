package com.peacecraftec.storage.json;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.peacecraftec.storage.MapBasedStorage;

public class JsonStorage extends MapBasedStorage {
	
	private Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	public JsonStorage() {
		super();
	}
	
	public JsonStorage(String path) {
		super(path);
	}
	
	public JsonStorage(File file) {
		super(file);
	}
	
	@Override
	public void load(InputStream in) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder build = new StringBuilder();
			String line = null;
			while((line = reader.readLine()) != null) {
				if(build.length() != 0) {
					build.append("\n");
				}
				
				build.append(line);
			}
			
			this.root = this.gson.fromJson(build.toString(), TypeToken.get(Map.class).getType());
			if(this.root == null) {
				this.root = new HashMap<String, Object>();
			}
		} catch(IOException e) {
			System.err.println("Failed to load from input stream!");
			e.printStackTrace();
		} finally {
			if(reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
	@Override
	public void save(OutputStream out) {
		OutputStreamWriter writer = null;
		try {
			writer = new OutputStreamWriter(out, "UTF-8");
			writer.write(this.gson.toJson(this.root));
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
