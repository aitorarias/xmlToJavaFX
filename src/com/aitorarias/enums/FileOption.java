package com.aitorarias.enums;

public enum FileOption {
	
	SAVE(1),
	LOAD(2);
	
	private int code;
	
	private FileOption(int code) {
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
	
	public FileOption toEnum(int cod) {
		for(FileOption option : FileOption.values()) {
			if(option.getCode() == cod) {
				return option;
			}
		}
		
		throw new IllegalArgumentException("Invalid code");
	}
}
