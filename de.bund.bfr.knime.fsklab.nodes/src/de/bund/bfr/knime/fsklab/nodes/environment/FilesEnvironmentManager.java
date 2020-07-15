package de.bund.bfr.knime.fsklab.nodes.environment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import org.apache.commons.io.FileUtils;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

/**
 * FilesEnvironmentManager handles working directories made out of referenced files. If the
 * references files array is null, empty or the files do not exist,
 * {@link FilesEnvironmentManager#getEnvironment()} returns an empty optional File.
 * 
 * @author Miguel de Alba, BfR, Berlin.
 */
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class FilesEnvironmentManager implements EnvironmentManager {
  
  private final String[] files;
  
  public FilesEnvironmentManager() {
    this(new String[0]);
  }
  
  public FilesEnvironmentManager(String[] files) {
    this.files = files;
  }

  @Override
  public Optional<Path> getEnvironment() {
   
    if (files == null || files.length == 0)
      return Optional.empty();
    
    for (String filePath : files) {
      if (Files.notExists(Paths.get(filePath)))
          return Optional.empty();
    }
    
    try {
      Path environment = Files.createTempDirectory("workingDirectory");
      for (String filePath : files) {
        Path sourcePath = Paths.get(filePath);
        Path targetPath = environment.resolve(sourcePath.getFileName());
        Files.copy(Paths.get(filePath), targetPath);
      }
      
      return Optional.of(environment);
    } catch (IOException e) {
      return Optional.empty();
    }
  }
  
  @Override
  public void deleteEnvironment(Path path) {
    FileUtils.deleteQuietly(path.toFile());
  }
}
