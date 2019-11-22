package io;

/**
 *
 * @author Matt
 */
public abstract class FileRequirements {
    private final String reqDesc;
    private final FileType type;
    
    public FileRequirements(String desc, FileType fileType){
        reqDesc = desc;
        type = fileType;
    }
    
    public final String getReqDesc(){
        return reqDesc;
    }
    
    public final FileType getReqType(){
        return type;
    }
    
    public abstract String reformatFile(String fileText);
}
