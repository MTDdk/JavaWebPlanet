package net.javapla.jawn;


/**
 * Carries internal data for the duration of a request.
 *
 * @author Igor Polevoy: 1/5/12 6:27 PM
 */
@Deprecated
public class RequestContext {

//    private Map<String, Object> values = new HashMap<String, Object>();

    /**
     * User segments are values extracted from the URL if user segments were used on a URL,
     * They are available as regular parameters using param("name") inside controllers and filter.
     */
//    private Map<String, String> userSegments = new HashMap<String, String>();


//    protected Object get(String name){
//        return values.get(name);
//    }

//    protected Map<String, String> getUserSegments(){
//        return userSegments;
//    }

//    protected void set(String name, Object value){
//        values.put(name, value);
//    }


    // there can be only one wildcard as this will match rest of URI
//    private String wildCardName, wildCardValue;

    /*public String getWildCardName() {
        return wildCardName;
    }

    public String getWildCardValue() {
        return wildCardValue;
    }

    public void setWildCardName(String wildCardName) {
        this.wildCardName = wildCardName;
    }

    public void setWildCardValue(String wildCardValue) {
        this.wildCardValue = wildCardValue;
    }*/
    
}
