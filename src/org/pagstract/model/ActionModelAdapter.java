package org.pagstract.model;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ActionModelAdapter implements ActionModel {
    final Map     _params;
    String  _url;
    final String  _method;
    String _anchor;
    boolean _enabled;
    boolean _visible;

    /**
     * @param URL ???
     * @param METHOD ???
     * @param enabled ???
     */
    public ActionModelAdapter( String url, String method, boolean enabled) {
        _url= url;
        _method= method;
        _params= new HashMap();
        _enabled=enabled;
        _visible = true;
    }

    public ActionModelAdapter() {
        this((String) null, null, true);
    }

    /**
     * Ist das jetzt die entgültige Version?
     * @param URL ???
     * @param METHOD ???
     * @param enabled ???
     */
    public ActionModelAdapter( URL url, String method, boolean enabled) {
        this(url.toString(), method, enabled);
    }
    public ActionModelAdapter( URL url, String method) {
        this(url, method, true);
    }
    public ActionModelAdapter( String url, String method) {
        this(url, method, true);
    }

    public void addParam( String name, String value) {
        _params.put( name, value);
    }

    public String getUrl() { return _url;}
    public void setUrl(String url) { _url = url;}

    public String getAnchor() { return _anchor;}
    public void setAnchor(String anchor) { _anchor = anchor;}

    public String getMethod() { return _method;}

    public Iterator getParameterNames() { return _params.keySet().iterator();}

    public String getParameter( String name) { return (String)_params.get( name);}
    public boolean isEnabled() { 
        return _enabled; 
    }

    public boolean isVisible() { 
        return _visible; 
    }

    public void setEnabled(boolean e) { _enabled = e; }
    public void setVisible(boolean v) { _visible = v; }

    public String toString() {
        return "ActionModelAdapter: method="+_method+" enabled="+_enabled+" url="+_url+"\n"+_params;
    }

    /**
     * Utility method: copy the parameters into the target.
     *
     * @param target the ActionModelAdapter to copy parameters into.
     */
    public void copyParametersInto(ActionModelAdapter target) {
        target.setUrl(getUrl());
        target.setAnchor(getAnchor());
	Iterator params = _params.entrySet().iterator();
	
	while (params.hasNext()) {
	    Map.Entry entry = (Map.Entry) params.next();
	    target.addParam((String)entry.getKey(), (String)entry.getValue());
	}
    }
}
