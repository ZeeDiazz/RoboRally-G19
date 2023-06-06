package dk.dtu.compute.se.pisd.roborally.online.mvc.saveload;

import com.google.gson.JsonElement;


/**
 * Interface for all the classes, that needs to be serialized into a json file, as well as being loaded from a json file
 *
 * @author Zigalow
 */
public interface Serializable {
    /**
     * @return A serialized json-element
     */
    JsonElement serialize();

    /**
     * @param element The json element that needs to be deserialized
     * @return An ISerializable object from given json element
     */

    Serializable deserialize(JsonElement element);

}