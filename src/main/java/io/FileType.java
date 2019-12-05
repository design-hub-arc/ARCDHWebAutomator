package io;


/**
 *
 * @author Matt
 */
public enum FileType {
    CSV("Comma Separated Values", new String[]{"csv"}),
    EXE("Executable", new String[]{"exe", "dmg", "app"}),
    DIR("Directory", new String[]{"directory", "folder"}),
    ANY("Any file", new String[]{});
    
    private final String name;
    private final String[] extensions;
    
    private FileType(String n, String[] possibleExtensions){
        name = n;
        extensions = possibleExtensions.clone();
    }
    
    public final String getName(){
        return name;
    }
    public final String[] getExtensions(){
        return extensions;
    }
}
