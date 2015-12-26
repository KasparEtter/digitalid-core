package net.digitalid.service.core.converter.xdf.serializer.value;

import net.digitalid.service.core.block.Block;
import net.digitalid.utility.system.converter.Converter;
import net.digitalid.utility.system.converter.Format;
import net.digitalid.utility.system.converter.Serializer;
import net.digitalid.utility.system.converter.exceptions.StoringException;

public class ObjectSerializer implements Serializer<Block> {
    
    @Override
    public Block store(Object object, Format<Block> format, String fieldName, String clazzName) throws StoringException {
        Block serializedObject = Converter.store(object, format, fieldName + "." + clazzName);
        return serializedObject;
    }
    
}
