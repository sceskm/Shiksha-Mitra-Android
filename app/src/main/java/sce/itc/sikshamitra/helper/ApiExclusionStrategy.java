package sce.itc.sikshamitra.helper;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class ApiExclusionStrategy implements ExclusionStrategy {

    private final String currentGroup;

    public ApiExclusionStrategy(String group) {
        this.currentGroup = group;
    }

    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        ApiGroup group = f.getAnnotation(ApiGroup.class);
        if (group != null) {
            return !group.value().equals(currentGroup);
        }
        return false;
    }

    @Override
    public boolean shouldSkipClass(Class<?> aClass) {
        return false;
    }
}
