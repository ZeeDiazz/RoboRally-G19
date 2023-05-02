package dk.dtu.compute.se.pisd.roborally.fileaccess;

import com.google.gson.JsonElement;

public interface ISerializable {
    
    public JsonElement serialize();
    public ISerializable deserialize(JsonElement element);
    
    
    
}
