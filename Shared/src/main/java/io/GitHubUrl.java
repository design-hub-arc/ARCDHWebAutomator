package io;

/**
 * The GitHubUrl class is used to form the URL
 * to the contents of a file on a GitHub repository.
 * 
 * @author Matt Crow
 */
public class GitHubUrl {
    private final String repoOwner;
    private final String repository;
    private final String branch;
    private final String filePath;
    
    /**
     * 
     * @param repositoryOwner the name of the repository's owner, as it appears in URLs
     * @param repositoryName the name of the repository, as per above
     * @param branchName 
     * @param path the entire path to the file, relative to the root directory in GitHub.
     * For example, if the path to a file on the internet is 
     * <pre>
     * https://github.com/design-hub-arc/ARCDHWebAutomator/blob/indev/Shared/src/main/java/io/GitHubUrl.java
     * </pre>
     * You could reference the contents of this file using
     * <pre> new GitHubUrl("design-hub-arc", "ARCDHWebAutomator", "indev", "Shared/src/main/java/io/GitHubUrl.java");</pre>
     * Note that you should not have a '/' at the beginning of the path parameter.
     */
    public GitHubUrl(String repositoryOwner, String repositoryName, String branchName, String path){
        repoOwner = repositoryOwner;
        repository = repositoryName;
        branch = branchName;
        filePath = path;
    }
    
    public String getOwner(){
        return repoOwner;
    }
    
    public String getRepo(){
        return repository;
    }
    
    public String getBranch(){
        return branch;
    }
    
    public String getFilePath(){
        return filePath;
    }
    
    @Override
    public String toString(){
        return String.format("https://raw.githubusercontent.com/%s/%s/%s/%s", repoOwner, repository, branch, filePath);
    }
}
