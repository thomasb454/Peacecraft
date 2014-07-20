package com.peacecraftec.module.lang;

import com.peacecraftec.module.ModuleManager;
import com.peacecraftec.storage.GenericFileStorage;
import com.peacecraftec.storage.Storage;
import com.peacecraftec.storage.yaml.YamlStorage;

import java.io.InputStream;

@SuppressWarnings("unused")
public class Language {

	private static final char COLOR_PREFIX = '\u00A7';
	
	private LanguageManager manager;
	private Storage data;
	private String name;

	public Language(ModuleManager manager, String locale) {
		this.manager = manager.getLanguageManager();
		InputStream stream = manager.getResource("languages/" + locale + ".yml");
		if(stream != null) {
			this.data = new YamlStorage();
			((GenericFileStorage) this.data).load(stream);
		}
		
		this.name = locale;
	}
	
	public String getName() {
		return this.name;
	}

	public boolean isEmpty() {
		return this.data == null;
	}

	public String translate(String key) {
		if(this.isEmpty() || !this.data.contains(key)) {
			if(this.getName().equals(this.manager.getDefault().getName())) {
				return key;
			} else {
				return this.manager.getDefault().translate(key);
			}
		}
		
		char[] chars = this.data.getString(key).toCharArray();
		for(int i = 0; i < chars.length - 1; i++) {
			if(chars[i] == '&' && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(chars[i + 1]) > -1) {
				chars[i] = COLOR_PREFIX;
				chars[i + 1] = Character.toLowerCase(chars[i + 1]);
			}
		}

        return new String(chars);
	}

	public String translate(String key, Object... replace) {
		if(replace == null || replace.length == 0) {
			return this.translate(key);
		}
		
		for(int i = 0; i < replace.length; i++) {
			String s = replace[i].toString();
			if(s.contains(String.valueOf(COLOR_PREFIX)) && !(s.startsWith(COLOR_PREFIX + "r") && s.endsWith(COLOR_PREFIX + "r"))) {
				s = COLOR_PREFIX + "r" + s + COLOR_PREFIX + "r";
			}
			
			replace[i] = s;
		}
		
		return String.format(this.translate(key), replace);
	}

}
