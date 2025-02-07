package de.bund.bfr.knime.fsklab.nodes.environment;


import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.workflow.NodeContext;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.util.FileUtil;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import de.bund.bfr.knime.fsklab.nodes.v1_9.ScriptHandler;
import de.bund.bfr.knime.fsklab.v1_9.FskPortObject;
import de.bund.bfr.metadata.swagger.Parameter;
import metadata.SwaggerUtil;


/**
 * 
 * File management class, that handles the storage of resource files generated by the Runner Node.
 * Paths to resource files are copied to a temporary folder. If the KNIME workflow is saved after
 * executing the Runner, the files are copied again to the /internal folder of the Runner node.
 * The temporary folder will be deleted after the copy process.
 *   
 * 
 * @author SchueleT ,BfR, Berlin.
 *
 */

@JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY)
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class GeneratedResourceFiles {

  /** paths of the generated resource files */
  private List<String> resourceFiles;

  /** Name of the node directory that contains the resource files
   *  If there is no internal folder (yet), the path will remain empty.
   *  Since the absolute path to the directory may change, we only store the node directory name
   *  and generate the path to the workflow on demand.
   *  [relative path to the workflow] + [/node_dir] + "/internal" 
   */
  private String directoryName;


  private String getBaseWorkflowDirectory() {
    return NodeContext.getContext().getWorkflowManager().getWorkingDir().toString();
  }

  /**
   * Main Constructor. Called by the Runner and FskObject nodes.
   * The point is to store the name of the node, by which the model was executed,
   * so all the files generated by the model can be found there.
   * The name (+ id) of the node is unique for the workflow, so storing the name without
   * the path is enough to reconstruct its absolute path later (e.g. in the Writer node). 
   */
  public GeneratedResourceFiles() {

    // initialize list of resource file paths
    this.resourceFiles = new ArrayList<>();

    // get the name (+ id) of the currently executed node:
    NodeContext nodeContext = NodeContext.getContext();

    // Node ID build like this: [workflow]:[parentNode]:[actualNode]
    // Node ID Example: 0:13:12
    String nId = nodeContext.getNodeContainer().getID().toString();
    String[] nodeIds= nId.split(":");

    // get name of node and id to create the name of the node directory (e.g. "FSK Runner (#12)" )
    String nodeName = nodeContext.getNodeContainer().getName() + " (#" + nodeIds[nodeIds.length - 1] + ")";

    // since a node could be nested in a metanode/container: get the name of parent nodes
    WorkflowManager wfm = nodeContext.getNodeContainer().getParent();
    for(int i = nodeIds.length - 2; i > 0 ; i--) {

      nodeName = wfm.getName()+" (#" + nodeIds[i] + ")" + File.separator + nodeName ;
      wfm = wfm.getParent();
    }


    this.directoryName = nodeName;



  }



  /**
   * Adds a file(absolute path) generated by the model execution (FSK Runner) to the list. 
   * */
  public void addResourceFile(File file){

    String resourceFolder = "GeneratedResources";

    try {

      File temp_dir = FileUtil.createTempDir(resourceFolder);
      final File resourceFile = new File(temp_dir.getAbsolutePath(),file.getName());

      FileUtil.copy(file, resourceFile );

      this.resourceFiles.add(resourceFile.getAbsolutePath());

    }catch(Exception e) {
      System.out.println("resource File Error: " + e);
    }

  }

  /**
   * Returns a list of file paths pointing to the files that were generated by the model previously.
   * The files might be in a temporary folder or in the /internal folder of the Runner node that created the files.
   *     
   * */ 
  public List<Path> getResourcePaths(){

    List<Path> resourcePaths = this.resourceFiles.stream().map(Paths::get).collect(Collectors.toList());

    
    // Validate the paths of the files:
    // Check if files are in temporary folder or in /internal folder of the Runner that created them.
    validatePaths(resourcePaths);

    return resourcePaths;
  }

  /**
   * Validate the paths of the generated resource files:
   * If the workflow hasn't been saved, all resource files are in the place where they were originally: temporary folder.
   * But if the workflow was saved, the filed were copied to the /internal folder of the executing Runner node. In that
   * case, the file locations are updated to point to the correct folder.
   *  
   * @param resourcePaths
   */
  private void validatePaths(List<Path> resourcePaths) {



    for(int i = 0; i < resourcePaths.size(); i++){
      Path oldPath = resourcePaths.get(i);

      if(Files.notExists(oldPath, LinkOption.NOFOLLOW_LINKS)) {
        String internalDir = getBaseWorkflowDirectory() + File.separator + this.directoryName + File.separator + "internal";
        Path newPath = Paths.get(internalDir + File.separator + oldPath.getFileName().toString());
        resourcePaths.set(i, newPath);
      }
    }
  }


  /**
   * Static method to take care of saving the generated files after a model has been executed.
   * 
   * If a model contains an output parameter with the type FILE, then this output parameter has the
   * file name as its value.
   * e.g.: Parameter myOutput >> myOutput = "output_1.csv"
   * 
   * This value is retrieved and a full path to the file is created. 
   * Then the file is "added" to the GeneratedResourceFiles object.   
   *
   * 
   * **/
  public static GeneratedResourceFiles saveGeneratedResourceFiles(FskPortObject fskObj, Optional<Path> workingDirectory,
      ExecutionContext exec, ScriptHandler handler) {

    List<Parameter> p = SwaggerUtil.getParameter(fskObj.modelMetadata);

    // get output parameters that have "file" as a data type 
    List<Parameter> outParams = p.stream()
        .filter(id -> id.getClassification().equals(Parameter.ClassificationEnum.OUTPUT)
            && id.getDataType().equals(Parameter.DataTypeEnum.FILE))
        .collect(Collectors.toList());

    GeneratedResourceFiles generatedResourceFiles = new GeneratedResourceFiles();
    List<String> output_files = new ArrayList<>();

    // evaluate the output parameters to get the name of the generated files.
    try {

      for(Parameter outParam : outParams) {

        String[] eval = handler.runScript(outParam.getId(), exec, true);
        output_files.addAll( Arrays.asList(eval));
      }

      // There must always be a working directory! 
      String wd = workingDirectory.get().toString();
      output_files.forEach(entry -> generatedResourceFiles.addResourceFile(new File(wd,entry)));

    }catch(Exception e) {
      System.out.println("Output Parameter is not a file: " + e);
    }

    return generatedResourceFiles;

  }
}
