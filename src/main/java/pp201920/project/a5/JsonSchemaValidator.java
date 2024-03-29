package pp201920.project.a5;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion.VersionFlag;
import com.networknt.schema.ValidationMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The class validates and checks the JSON files of the program 
 * 
 * @author Sultan Balawal
 * @author Alberto Nicoletti
 * @author René Zorzi
 * 
 * @version 1.0
 */ 

public class JsonSchemaValidator {

    /**
     *These are the properties/attributes of the JsonSchemaValidator class.
     *
     * activitySchema and analysisSchema are the constant for the schemas required by the program
     * logging   the Logger to check for the validation
     * logger    the MyLogger class to check for the validation
     */

    private final JsonSchema activitySchema,analysisSchema;
    final Logger logging = LogManager.getLogger();
    final MyLogger logger = new MyLogger(logging);

    /**
     * This is the constructor of the JsonSchemaValidator class.
     * The constructor takes a string containing the path to the
     * resources and reads the activity and analysis json schema files.
     * Then the {@link ObjectMapper} deserializes the json of the two
     * json schemas and assigns it to their respective {@link JsonNode}.
     * Once the {@link JsonNode JsonNodes} have their value the
     * {@link JsonSchemaFactory} extracts the schema from the two nodes
     * and assigns it to the activitySchema and analysisSchema which are
     * objects of the {@link JsonSchema} class.
     * 
     * @param pathToResources string containing the path where the schema.json are stored
     */
    public JsonSchemaValidator(String pathToResources){
        ObjectMapper mapper = new ObjectMapper();
        JsonNode activityNode = null, analysisNode = null;

        try{
            activityNode = mapper.readTree(readFile(pathToResources + "activity.schema.json"));
            analysisNode = mapper.readTree(readFile(pathToResources + "analysis.schema.json"));

        }catch(IOException e){
            logger.error("Something went wrong while reading the schema json");
            e.printStackTrace();

        }

        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(VersionFlag.V7);

        activitySchema = factory.getSchema(activityNode);
        analysisSchema = factory.getSchema(analysisNode);
    }

    /**
     *
    * The Method validates the schema by checking every single node in the Json schema.
    *
    * @param json  the Json String parameter
    * @param option the option node to validate in the Schema
    * @return boolean
    */
    public boolean validateSchema(String json, int option){
        Set<ValidationMessage> errors;
        JsonNode node = null;

        try{
            node = (new ObjectMapper()).readTree(json);
        }catch(IOException e){
            logger.error("Failed to read " + json);
            logger.error(e);
        }

        if(option == 0)
            errors = activitySchema.validate(node);
        else
            errors = analysisSchema.validate(node);

        if(errors.size() != 0)
            logger.error(errors);

        return errors.size() == 0;
    }

    /**
     * The method return the string for the file that have been read by the Program.
     *
     * @param path   the path parameter to read the files
     * @return String the method returns the file text to be read by the program
     */
    private String readFile(String path){
        StringBuilder fileContent = new StringBuilder();
        File file = new File(path);
        
        try(FileReader fileReader = new FileReader(file)){
            BufferedReader reader = new BufferedReader(fileReader);
            String line = reader.readLine();

            while(line != null){
                fileContent.append(line);
                line = reader.readLine();
            }

            reader.close();

        } catch (IOException e) {
            logger.error("Failed to read " + path);
            logger.error(e);
        }
        
        return fileContent.toString();
    }

}