package io;

/**
 *
 * @author Matt
 */
public class FileRequirements {
    private final String reqDesc;
    private final FileType type;
    
    public static final FileRequirements NO_REQ = new FileRequirements("Any file", FileType.ANY);
    
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
}
