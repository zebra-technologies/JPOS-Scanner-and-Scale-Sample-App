package JposTest.src;

import jpos.JposException;

@FunctionalInterface
interface ReadableString {

    String Get() throws JposException;
}

@FunctionalInterface
interface WritableString {

    void Set(String value) throws JposException;
}

@FunctionalInterface
interface ReadableBoolean {

    Boolean Get() throws JposException;
}

@FunctionalInterface
interface WritableBoolean {

    void Set(boolean value) throws JposException;
}

@FunctionalInterface
interface ReadableByte {

    byte[] Get() throws JposException;
}

@FunctionalInterface
interface WritableByte {

    void Set(byte[] value) throws JposException;
}

@FunctionalInterface
interface WritableLong {

    void Set(long value) throws JposException;
}

@FunctionalInterface
interface ReadableLong {

    long Get() throws JposException;
}

@FunctionalInterface
interface WritableInt {

    void Set(int value) throws JposException;
}

@FunctionalInterface
interface ReadableInt {

    int Get() throws JposException;
}

/**
 * This class is used to bind properties of scanner and scale. It uses function
 * interfaces to hold set and get methods of each Scanner and Scale property.
 *
 *
 */
public class PropertyBinder {

    public final String type;
    public final String propertyName;
    public final boolean editable;                    //true if the selected property has a 'setProperty' method

    private ReadableString stringGetter;
    private ReadableBoolean booleanGetter;
    private ReadableByte byteGetter;
    private ReadableLong longGetter;
    private ReadableInt intGetter;

    private WritableString stringSetter;
    private WritableBoolean booleanSetter;
    private WritableByte byteSetter;
    private WritableLong longSetter;
    private WritableInt intSetter;

    /**
     * Constructor creates instances of Properties with set and get methods
     * which accepts Integer values.
     *
     * @param propertyname: name of the property
     * @param returnType : a String value. Indicates the type of the return
     * variable.
     * @param getter ; getProperty method
     * @param setter : setProperty method
     * @param editable : true if a setProperty method exists. false otherwise.
     */
    public PropertyBinder(String propertyname, String returnType, ReadableInt getter, WritableInt setter, boolean editable) {
        this.propertyName = propertyname;
        this.intGetter = getter;
        this.intSetter = setter;
        this.editable = editable;
        this.type = returnType;
    }

    /**
     * Constructor creates instances of Properties with set and get methods
     * which accepts String values.
     *
     * @param propertyName: name of the property
     * @param ReturnType : String value -"string"
     * @param getter ; getProperty method
     * @param setter : setProperty method
     * @param editable : true if a setProperty method exists. false otherwise.
     */
    public PropertyBinder(String propertyName, String ReturnType, ReadableString getter, WritableString setter, boolean editable) {
        this.propertyName = propertyName;
        this.stringGetter = getter;
        this.stringSetter = setter;
        this.editable = editable;
        this.type = ReturnType;
    }

    /**
     * Constructor creates instances of Properties with set and get methods
     * which accepts Boolean values.
     *
     * @param propertyName: name of the property
     * @param ReturnType : String value -"boolean"
     * @param getter ; getProperty method
     * @param setter : setProperty method
     * @param editable : true if a setProperty method exists. false otherwise.
     */
    public PropertyBinder(String propertyName, String ReturnType, ReadableBoolean getter, WritableBoolean setter, boolean editable) {
        this.propertyName = propertyName;
        this.booleanGetter = getter;
        this.booleanSetter = setter;
        this.editable = editable;
        this.type = ReturnType;
    }

    /**
     * Constructor creates instances of Properties with set and get methods
     * which accepts Byte arrays.
     *
     * @param propertyName: name of the property
     * @param returnType : String value - "byte"
     * @param getter ; getProperty method
     * @param setter : setProperty method
     * @param editable : true if a setProperty method exists. false otherwise.
     */
    public PropertyBinder(String propertyName, String returnType, ReadableByte getter, WritableByte setter, boolean editable) {
        this.propertyName = propertyName;
        this.byteSetter = setter;
        this.byteGetter = getter;
        this.editable = editable;
        this.type = returnType;
    }

    /**
     * Constructor creates instances of Properties with set and get methods
     * which accepts long type values.
     *
     * @param propertyName: name of the property
     * @param returnType : String value - "byte"
     * @param getter ; getProperty method
     * @param setter : setProperty method
     * @param editable : true if a setProperty method exists. false otherwise.
     */
    public PropertyBinder(String propertyName, ReadableLong getter, WritableLong setter, String returnType, boolean editable) {
        this.propertyName = propertyName;
        this.type = returnType;
        this.longGetter = getter;
        this.longSetter = setter;
        this.editable = editable;
    }

    /**
     * Returns the type of variable allowed by the getProperty and setProperty
     * methods of each property. Returning String value can either be 'String',
     * 'int', 'byte' or 'boolean'.
     *
     * @return variable type of set and get methods
     */
    public String getType() {
        return type;
    }

    /**
     * returns the Property Name which is a String value
     *
     * @return the name of the property
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * returns a boolean value which indicates the availability of a setProperty
     * method of the selected property
     *
     * @return true if the selected property has a setProperty method. false if
     * not.
     */
    public boolean isEditable() {
        return editable;
    }

    /**
     * This method is used if the getProperty method returns a String value. It
     * returns the return value of the getProperty method, which is a String.
     *
     * @return String value
     * @throws JposException
     */
    public String getString() throws JposException {
        return stringGetter.Get();
    }

    /**
     * This method is used if the getProperty method returns a Boolean value. It
     * returns the return value of the getProperty method, which is a Boolean.
     *
     * @return boolean value
     * @throws JposException
     */
    public boolean getBoolean() throws JposException {
        return booleanGetter.Get();
    }

    /**
     * This method is used if the getProperty method returns a Byte array. It
     * returns the return value of the getProperty method, which is a byte
     * array.
     *
     * @return byte[]
     * @throws JposException
     */
    public byte[] getByte() throws JposException {
        return byteGetter.Get();
    }

    /**
     * This method is used if the getProperty method returns a long value. It
     * returns the return value of the getProperty method.
     *
     * @return long value
     * @throws JposException
     */
    public long getLong() throws JposException {
        return longGetter.Get();
    }

    /**
     * This method is used if the getProperty method returns an Integer value.
     * It returns the return value of the getProperty method.
     *
     * @return integer value
     * @throws JposException
     */
    public int getInt() throws JposException {
        return intGetter.Get();
    }

    /**
     * This method is used to set a property value with a Boolean value.
     * Requires a boolean value as the input parameter.
     *
     * @param setValue : boolean value
     * @throws JposException
     */
    public void setBoolean(boolean setValue) throws JposException {
        booleanSetter.Set(setValue);
    }

    /**
     * This method is used to set a property value with a long value. Requires a
     * long value as the input parameter.
     *
     * @param setValue : long value
     * @throws JposException
     */
    public void setLong(long setValue) throws JposException {
        longSetter.Set(setValue);
    }

    /**
     * This method is used to set a property value with a Integer value.
     * Requires an Integer value as the input parameter.
     *
     * @param setValue : Integer value
     * @throws JposException
     */
    public void setInt(int setValue) throws JposException {
        intSetter.Set(setValue);
    }

    @Override
    public String toString() {
        return this.propertyName;
    }

}
