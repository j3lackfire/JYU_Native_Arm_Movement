package Logic;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;


/**
 * Created by Le Pham Minh Duc on 6/13/2017.
 */

public class SavedValue {
    public long timeStamp;

    public float acceX;
    public float acceY;
    public float acceZ;

    public float gyroX;
    public float gyroY;
    public float gyroZ;

    //constructor with default value
    public SavedValue() {
        timeStamp = -1;

        acceX = -1;
        acceY = -1;
        acceZ = -1;

        gyroX = -1;
        gyroY = -1;
        gyroZ = -1;
    }

    public void setTimeStamp(long _time) {
        timeStamp = _time;
    }

    public void setAcce(float x,float y, float z) {
        acceX = x;
        acceY = y;
        acceZ = z;
    }

    public void setGyro(float x,float y, float z) {
        gyroX = x;
        gyroY = y;
        gyroZ = z;
    }

    //return this class as a json
    //somehow, the function name getJsonString is used by the lib, so if we have the function name
    //getJsonString, it will cause error. This is quite stupid since in C#, the compiler will WARN you if you do something stupid like this
    //but well, JAVA
    public String myGetJsonString() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(JsonMethod.ALL, JsonAutoDetect.Visibility.ANY);

        try {
            return mapper.writeValueAsString(this);
        }
        catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Error return file as JSON, please contact developer !";
    }

    public String myGetJsonStringPretty() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(JsonMethod.ALL, JsonAutoDetect.Visibility.ANY);

        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        }
        catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Error return file as JSON, please contact developer !";

    }

    //get Json String from a class
    public static String toJson(SavedValue s) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(JsonMethod.ALL, JsonAutoDetect.Visibility.ANY);

        try {
            return mapper.writeValueAsString(s);
        }
        catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Error return file as JSON, please contact developer !";
    }

    //get this class from a JSON string
    public static SavedValue fromJson(String jsonString) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(JsonMethod.ALL, JsonAutoDetect.Visibility.ANY);
        try  {
            return mapper.readValue(jsonString, SavedValue.class);
        }
        catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
