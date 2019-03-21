
// Enums por seguir con el convenio Java
package com.aitorarias.enums;

public enum FileOption {
	// 1. GUARDAR
	// 2. IMPORTAR
	SAVE(1),
	LOAD(2);
	// https://docs.oracle.com/javase/tutorial/essential/io/file.html
	private int code;
	
	private FileOption(int code) {
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
	
	public FileOption toEnum(int cod) {
		// Referencia: https://stackoverflow.com/questions/7230252/java-cast-long-to-enum-type-issue
		for(FileOption option : FileOption.values()) {
			if(option.getCode() == cod) {
				return option;
			}
		}
		// Lanza nuevo error 
		throw new IllegalArgumentException("Codigo invalido");
	}
}
