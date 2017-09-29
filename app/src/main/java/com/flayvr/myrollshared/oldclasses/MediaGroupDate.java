package com.flayvr.myrollshared.oldclasses;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonDeserializationContext;

import java.io.Serializable;
import java.util.Date;

public interface MediaGroupDate extends Serializable, Comparable<MediaGroupDate>
{
    public abstract boolean after(MediaGroupDate mediagroupdate);

    public abstract Date getLastDate();

    public abstract boolean inTheSameDay(MediaGroupDate mediagroupdate);

    public class MediaGroupDateJsonAdapater implements JsonDeserializer<MediaGroupDate>, JsonSerializer<MediaGroupDate>
    {
        private static final String CLASSNAME = "CLASSNAME";
        private static final String INSTANCE = "INSTANCE";
        private Gson gson;

        public MediaGroupDate deserialize(JsonElement jsonelement, Type type, JsonDeserializationContext jsondeserializationcontext)
        {
            JsonObject jsonobject;
            String s;
            Object obj;
            try
            {
                jsonobject = jsonelement.getAsJsonObject();
                s = ((JsonPrimitive)jsonobject.get(CLASSNAME)).getAsString();
                if(s.contains("Single")) {
                    if(s.contains("Range"))
                        obj = MediaGroupRangeDate.class;
                    else
                        obj = Class.forName(s);
                } else
                    obj = MediaGroupSingleDate.class;
                return (MediaGroupDate)gson.fromJson(jsonobject.get(INSTANCE), ((Class) (obj)));
            }
            catch(ClassNotFoundException classnotfoundexception)
            {
                classnotfoundexception.printStackTrace();
                throw new JsonParseException(classnotfoundexception.getMessage());
            }
        }

        public JsonElement serialize(MediaGroupDate mediagroupdate, Type type, JsonSerializationContext jsonserializationcontext)
        {
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("CLASSNAME", mediagroupdate.getClass().getCanonicalName());
            jsonobject.add("INSTANCE", gson.toJsonTree(mediagroupdate));
            return jsonobject;
        }

        public MediaGroupDateJsonAdapater()
        {
            gson = new Gson();
        }
    }
}
