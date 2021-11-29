package de.bund.bfr.knime.fsklab.nodes.environment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.commons.io.FileUtils;
import org.knime.core.util.FileUtil;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class AddedFilesEnvironmentManager implements EnvironmentManager {

  private EnvironmentManager manager;
  private String[] files;

  
  public AddedFilesEnvironmentManager() {
    this(new DefaultEnvironmentManager(), new String[0]);
  }
  public AddedFilesEnvironmentManager(EnvironmentManager manager) {
    this(manager, new String[0]);
  }
  
  public AddedFilesEnvironmentManager(EnvironmentManager manager, String[] files) {
    this.manager = manager;
    this.files = files;
  }
  
  
  public String[] getFiles() {
    return files;
  }
  public EnvironmentManager getManager() {
    return manager;
  }
  
  @Override
  public Optional<Path> getEnvironment() {
    Optional<Path> environment = manager.getEnvironment();
    
    if (files == null || files.length == 0)
      return environment;

 
    List<Path> filePaths = new ArrayList<>();
    try {
      for (String filePath : files) {
        filePaths.add(FileUtil.resolveToPath(FileUtil.toURL(filePath)));
      }

    }catch(Exception e) {
      return environment;
    }
    
    for (Path filePath : filePaths) {
      if (Files.notExists(filePath))
        return environment;
    }

    try {
      //Path environment = Files.createTempDirectory("workingDirectory");
      if(!environment.isPresent()) {
        environment = Optional.of(Files.createTempDirectory("workingDirectory"));
      }
        
      for (Path filePath : filePaths) {
        Path sourcePath = filePath;
        Path targetPath = environment.get().resolve(sourcePath.getFileName());
        Files.copy(filePath, targetPath);
      }
      
      return environment;
    } catch (IOException e) {
      return environment;
    }
  }

  @Override
  public void deleteEnvironment(Path path) {
    FileUtils.deleteQuietly(path.toFile());
  }
  
  
}
