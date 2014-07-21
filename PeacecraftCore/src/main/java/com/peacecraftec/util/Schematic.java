package com.peacecraftec.util;

import org.spacehq.opennbt.NBTIO;
import org.spacehq.opennbt.tag.builtin.*;

import java.io.*;

public class Schematic {
	public static Schematic load(File file) {
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
			return load(in);
		} catch(IOException e) {
			System.err.println("Failed to load schematic \"" + file.getName() + "\".");
			e.printStackTrace();
			return null;
		} finally {
			if(in != null) {
				try {
					in.close();
				} catch(IOException e) {
				}
			}
		}
	}

	public static Schematic load(InputStream in) {
		CompoundTag root = null;
		try {
			root = (CompoundTag) NBTIO.readTag(new DataInputStream(in));
		} catch(IOException e) {
			System.err.println("Failed to load schematic.");
			e.printStackTrace();
			return null;
		}

		int width = root.<IntTag>get("Width").getValue();
		int height = root.<IntTag>get("Height").getValue();
		int length = root.<IntTag>get("Length").getValue();
		String materials = root.<StringTag>get("Materials").getValue();
		byte blocks[] = root.<ByteArrayTag>get("Blocks").getValue();
		byte data[] = root.<ByteArrayTag>get("Data").getValue();
		ListTag entities = root.get("Entities");
		ListTag tileEntities = root.get("TileEntities");
		CompoundTag ents[] = new CompoundTag[entities.size()];
		CompoundTag tileEnts[] = new CompoundTag[tileEntities.size()];
		for(int index = 0; index < entities.size(); index++) {
			ents[index] = entities.get(index);
		}

		for(int index = 0; index < tileEntities.size(); index++) {
			tileEnts[index] = tileEntities.get(index);
		}

		if(materials.equals("Classic")) {
			for(int index = 0; index < blocks.length; index++) {
				if(blocks[index] >= 21 && blocks[index] <= 36) {
					if(blocks[index] == 21) {
						data[index] = 14;
					} else if(blocks[index] == 22) {
						data[index] = 1;
					} else if(blocks[index] == 23) {
						data[index] = 4;
					} else if(blocks[index] == 24) {
						data[index] = 5;
					} else if(blocks[index] == 25) {
						data[index] = 13;
					} else if(blocks[index] == 26) {
						data[index] = 9;
					} else if(blocks[index] == 27) {
						data[index] = 3;
					} else if(blocks[index] == 28) {
						data[index] = 9;
					} else if(blocks[index] == 29) {
						data[index] = 11;
					} else if(blocks[index] == 30) {
						data[index] = 10;
					} else if(blocks[index] == 31) {
						data[index] = 10;
					} else if(blocks[index] == 32) {
						data[index] = 2;
					} else if(blocks[index] == 33) {
						data[index] = 6;
					} else if(blocks[index] == 34) {
						data[index] = 15;
					} else if(blocks[index] == 35) {
						data[index] = 7;
					} else if(blocks[index] == 36) {
						data[index] = 0;
					}

					blocks[index] = 35;
				}
			}
		}

		return new Schematic(width, height, length, blocks, data, ents, tileEnts);
	}

	public static void save(Schematic schematic, File file) {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			save(schematic, out);
		} catch(IOException e) {
			System.err.println("Failed to save schematic \"" + file.getName() + "\".");
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

	public static void save(Schematic schematic, OutputStream out) {
		CompoundTag root = new CompoundTag("Schematic");
		root.put(new IntTag("Width", schematic.getWidth()));
		root.put(new IntTag("Height", schematic.getHeight()));
		root.put(new IntTag("Length", schematic.getLength()));
		root.put(new StringTag("Materials", "Alpha"));
		root.put(new ByteArrayTag("Blocks", schematic.getBlocks()));
		root.put(new ByteArrayTag("Data", schematic.getData()));
		ListTag entities = new ListTag("Entities", CompoundTag.class);
		ListTag tileEntities = new ListTag("TileEntities", CompoundTag.class);
		for(CompoundTag tag : schematic.getEntities()) {
			entities.add(tag);
		}

		for(CompoundTag tag : schematic.getTileEntities()) {
			tileEntities.add(tag);
		}

		root.put(entities);
		root.put(tileEntities);
		try {
			NBTIO.writeTag(new DataOutputStream(out), root);
		} catch(IOException e) {
			System.err.println("Failed to save schematic.");
			e.printStackTrace();
		}
	}

	private int width;
	private int height;
	private int length;
	private byte blocks[];
	private byte data[];
	private CompoundTag entities[];
	private CompoundTag tileEntities[];

	public Schematic(int width, int height, int length, byte blocks[], byte data[], CompoundTag entities[], CompoundTag tileEntities[]) {
		this.width = width;
		this.height = height;
		this.length = length;
		this.blocks = blocks;
		this.data = data;
		this.entities = entities;
		this.tileEntities = tileEntities;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public int getLength() {
		return this.length;
	}

	public byte[] getBlocks() {
		return this.blocks;
	}

	public byte[] getData() {
		return this.data;
	}

	public CompoundTag[] getEntities() {
		return this.entities;
	}

	public CompoundTag[] getTileEntities() {
		return this.tileEntities;
	}

	public byte getBlock(int x, int y, int z) {
		return this.blocks[y * this.width * this.length + z * this.width + x];
	}

	public void setBlock(int x, int y, int z, byte block) {
		this.blocks[y * this.width * this.length + z * this.width + x] = block;
	}

	public byte getData(int x, int y, int z) {
		return this.data[y * this.width * this.length + z * this.width + x];
	}

	public void setData(int x, int y, int z, byte data) {
		this.data[y * this.width * this.length + z * this.width + x] = data;
	}
}
