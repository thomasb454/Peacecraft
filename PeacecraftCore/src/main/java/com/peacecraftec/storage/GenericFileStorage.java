package com.peacecraftec.storage;

import java.io.*;

public abstract class GenericFileStorage extends GenericStorage {
    private File file;

    public GenericFileStorage() {
        this("");
    }

    public GenericFileStorage(String path) {
        this(path != null && !path.equals("") ? new File(path) : null);
    }

    public GenericFileStorage(File file) {
        this.file = file;
    }

    @Override
    public void load() {
        if(this.file == null) {
            return;
        }

        FileInputStream in = null;
        try {
            if(!this.file.exists()) {
                if(this.file.getParentFile() != null && !this.file.getParentFile().exists()) {
                    this.file.getParentFile().mkdirs();
                }

                this.file.createNewFile();
            }

            in = new FileInputStream(this.file);
            this.load(in);
        } catch(IOException e) {
            System.err.println("[PeacecraftCore] Failed to load from file " + this.file.getPath() + "!");
            e.printStackTrace();
        } finally {
            if(in != null) {
                try {
                    in.close();
                } catch(IOException e) {
                }
            }
        }
    }

    public abstract void load(InputStream in);

    @Override
    public void save() {
        if(this.file == null) {
            return;
        }

        FileOutputStream out = null;
        try {
            if(!this.file.exists()) {
                if(!this.file.getParentFile().exists()) {
                    this.file.getParentFile().mkdirs();
                }

                this.file.createNewFile();
            }

            out = new FileOutputStream(this.file);
            this.save(out);
        } catch(IOException e) {
            System.err.println("[PeacecraftCore] Failed to save config file to " + this.file.getPath() + "!");
            e.printStackTrace();
        } finally {
            if(out != null) {
                try {
                    out.close();
                } catch(IOException e) {
                }
            }
        }
    }

    public abstract void save(OutputStream out);
}
