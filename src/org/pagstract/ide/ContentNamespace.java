/*
 * $Id$
 * (c) Copyright 2003 pagstract development team.
 *
 * This file is part of pagstract (http://www.pagstract.org/).
 *
 * Pagstract is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 *
 * Please see COPYING for the complete licence.
 */
package org.pagstract.ide;

import java.awt.Color;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.Border;

import org.pagstract.model.ActionModel;
import org.pagstract.model.ActionModelAdapter;
import org.pagstract.model.ComboboxModel;
import org.pagstract.model.DataModel;
import org.pagstract.model.SelectionDescriptor;
import org.pagstract.model.TextFieldModel;
import org.pagstract.view.namespace.Namespace;

/**
 * A Namespace that turns an abstract Namespace (that only contains names)
 * to a concrete Namespace with actual values.
 */
public class ContentNamespace implements Namespace, ComponentIcons {
    private final Map _content;
    private final Namespace _abstractNamespace;
    private final ContentEditPanel _parentComponent;

    public ContentNamespace(Namespace abstractNs,
                            ContentEditPanel parent) 
    {
        _abstractNamespace = abstractNs;
        _content = new HashMap();
        _parentComponent = parent;
    }

    //-- concrete implementation
    public Namespace getSubNamespace(String name) 
        throws NoSuchElementException {
        if (!isNamespace(name)) {
            throw new NoSuchElementException("no such namespace: '" + name + "'");
        }
        Namespace result = null;
        if (!_content.containsKey(name)) {
            //System.err.println("create subnamespace: " + name);
            Namespace abstractSub = _abstractNamespace.getSubNamespace(name);
            ContentEditPanel namedParent = new ContentEditPanel();
            Border blackline = BorderFactory.createLineBorder(Color.black);
            namedParent.setBorder(BorderFactory
                                  .createTitledBorder(blackline, name));
            _parentComponent.addSingleComponent(namedParent);
            result = new ContentNamespace(abstractSub, namedParent);
            _content.put(name, result);
        }
        else {
            result = (Namespace) _content.get(name);
        }
        return result;
    }

    /**
     * returns the object with the given Name. If this namespace
     * does not handle concrete object, this throws an 
     * UnsupportedOperationException.
     */
    public Object getNamedObject(String name) 
        throws UnsupportedOperationException {
        Accessor result = null;
        if (!_content.containsKey(name)) {
            //System.err.println("create: " + name);
            boolean iterate = isIteratableObject(name);
            result = createObjectFor(name, iterate);
            _content.put(name, result);
        }
        else {
            Object nsContent = _content.get(name);
            if (nsContent instanceof Accessor) {
                result = (Accessor) nsContent;
            }
            else {
                /*
                System.err.println("ACHTUNG: while accessing '" 
                                   + name + "': "
                                   + _content.getClass()
                                   + "->" + _content);
                */
                // ungetypt zurück..
                return nsContent;
            }
        }
        return result.getContent();
    }

    public Iterator/*<Namespace>*/ getNamespaceIterator(String name)
        throws UnsupportedOperationException {
        if (!_content.containsKey(name)) {
            return null;
        }
        return null;
    }

    //-- abstract Namespace implementation just delegates
    /**
     * returns a set of available names within this Namespace.
     */
    public Set/*<String>*/ availableNames() {
        return _abstractNamespace.availableNames();
    }

    /**
     * predicate to check if a given name exists within this namespace.
     */
    public boolean containsName(String name) {
        return _abstractNamespace.containsName(name);
    }

    /**
     * returns the type of the object that will be returned for the
     * given name. This is only defined for non-namespaces.
     */
    public Class getNamedObjectType(String name) {
        return _abstractNamespace.getNamedObjectType(name);
    }

    /**
     * checks if a named object within this namespace is a
     * namespace. If so, they can be accessed with 
     * {@link #getSubNamespace(String)}.
     */
    public boolean isNamespace(String name) {
        return _abstractNamespace.isNamespace(name);
    }

    /**
     * predicate to check, if the named object can be iterated.
     */
    public boolean isIteratableObject(String name) {
        return _abstractNamespace.isIteratableObject(name);
    }

    private interface Accessor {
        Object getContent();
    }

    //-- bastelkram..
    private static class ObjectAccessor implements Accessor {
        private final Object _object;
        public ObjectAccessor(final Object o, boolean iterate)
        {
            if (iterate) {
                _object = new Object[] { o, o };
            }
            else {
                _object = o;
            }
        }

        public Object getContent() {
            return _object;
        }
    }
    
    private static class TextFieldAccessor implements Accessor {
        private final JTextField _field;
        public TextFieldAccessor(boolean iterate) {
            _field = new JTextField();
            Dimension pref = _field.getPreferredSize();
            Dimension max = new Dimension(400, (int) pref.getHeight());
            _field.setPreferredSize(max);
            _field.setMaximumSize(max);
        }
        
        public JComponent getComponent() {
            return _field;
        }

        public Object getContent() {
            return _field.getText();
        }
    }

    private static class TextFieldModelAccessor implements Accessor {
        private final JTextField _field;
        private final TextFieldModel _model;

        public TextFieldModelAccessor() {
            _field = new JTextField();
            Dimension pref = _field.getPreferredSize();
            Dimension max = new Dimension(400, (int) pref.getHeight());
            _field.setPreferredSize(max);
            _field.setMaximumSize(max);
            _model = new TextFieldModel() {
                    public void setText(String val) {
                        _field.setText(val);
                    }
                    
                    public String getText() {
                        return _field.getText();
                    }
                };
        }
        
        public JComponent getComponent() {
            return _field;
        }

        public Object getContent() {
            return _model;
        }
    }

    private Accessor createObjectFor(String name, boolean iterate) {
        Class cls = getNamedObjectType(name);
        if (cls!=null && ActionModel.class.isAssignableFrom(cls)) {
            return new ObjectAccessor(new ActionModelAdapter("http://foo/",
                                                             "GET"),
                                      iterate);
        }
        else if (cls!=null && TextFieldModel.class.isAssignableFrom(cls)) {
            TextFieldModelAccessor tfma = new TextFieldModelAccessor();
            JLabel label = new JLabel(name);
            label.setIcon( INPUT_ICON );
            Dimension pref = label.getPreferredSize();
            if (pref.getWidth() < 60) {
                pref.setSize(60, pref.getHeight());
                label.setPreferredSize(pref);
            }
            _parentComponent.addTwoComponents(label,
                                              tfma.getComponent());
            return tfma;
        }
        else if (cls!=null && DataModel.class.isAssignableFrom(cls)) {
            SD s = new SD();
            for (int i=0; i < 5; ++i) {
                s.addPair("foo-"+i, "bar-"+i);
            }
            s.setValue("foo-3");
            return new ObjectAccessor(s, iterate);
        }
        else {
            TextFieldAccessor tfa = new TextFieldAccessor(iterate);
            JLabel label = new JLabel(name);
            label.setIcon( VALUE_ICON );
            Dimension pref = label.getPreferredSize();
            if (pref.getWidth() < 60) {
                pref.setSize(60, pref.getHeight());
                label.setPreferredSize(pref);
            }
            _parentComponent.addTwoComponents(label,
                                              tfa.getComponent());
            return tfa;
        }
    }

    public static class SD implements ComboboxModel {
        List	m_lst;
        String	m_value;

        public SD() {
            m_lst= new LinkedList();
        }

        public void addPair( final String value,  final String label) {
            m_lst.add( new SelectionDescriptor() {
                    public String getValue() { return value;}
                    public String getLabel() { return label;}
                } );
        }

        public Iterator/*<SelectionDescriptor>*/ getSelectionDescriptors() {
            return m_lst.iterator();
        }

        public String getValue() { return m_value;}
        public void setValue( String value) { m_value= value;}

        public boolean isEnabled() {
            return true;
        }
        
        /**
         * a boolean switching the visibility of this component.
         */
        public boolean isVisible() {
            return true;
        }
    }
}

/* Emacs: 
 * Local variables:
 * c-basic-offset: 4
 * tab-width: 8
 * indent-tabs-mode: nil
 * compile-command: "ant -emacs -find build.xml compile"
 * End:
 * vi:set tabstop=8 shiftwidth=4 nowrap: 
 */
