package org.example.management.system.model.constant;

/**
 * 属性访问器
 */
public class StringPropertyAccessor implements PropertyAccessor<String> {
    private final String property;
    public StringPropertyAccessor(String property) {
        this.property = property;
    }
    @Override
    public String getProperty() {
        return property;
    }
}
