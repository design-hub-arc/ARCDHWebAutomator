package io;

import java.io.File;
import java.util.Arrays;

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

    public boolean validateFile(File f) throws Exception{
        String name = f.getName();
        int idx = name.lastIndexOf('.');
        String ext = (idx == -1) ? "" : name.substring(idx + 1);
        if(!Arrays.stream(type.getExtensions()).anyMatch((ex)->ex.equalsIgnoreCase(ext))){
            throw new Exception("Wrong file type: " + ext + ". File must be one of the following: " + Arrays.toString(type.getExtensions()));
        }
        return true;
    };
    
    public abstract String reformatFile(String fileText);

}
