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
package org.pagstract.view.template.parser.scanner;

import java.util.ArrayList;
import java.util.List;

/**
 * A compiled token pattern.
 */
public final class TokenMatchPattern {
    final static short INIT_STATE  =  0;

    final short/*nextState*/ _transitionStates[/*currState*/][/*input-char*/];
   
    /**
     * Compile a set of Tokens to the TokenMatch pattern.
     */
    public static TokenMatchPattern compile(String matchTokens[]) {
        List/*<short[]>*/ transitionList = new ArrayList();
        transitionList.add(new short[256]); // state '0'
        for (int i=0; i < matchTokens.length; ++i) {
            byte[] token = matchTokens[i].getBytes();
            buildTransitions(transitionList, token, i);
        }
        
        int s = transitionList.size();
        short transitionStates[][] = (short[][]) transitionList.toArray(new short[s][]);
        return new TokenMatchPattern(transitionStates);
    }

    private static void buildTransitions(List transitionList, byte token[],
                                         int tokenNumber) 
    {
        int currentState = INIT_STATE;

        for (int i=0; i < token.length; ++i) {
            if (currentState < 0) {
                throw new IllegalArgumentException("ambigous: not a DFA");
            }
            int character = token[i] & 0xff;
            short[] transitionArray =(short[])transitionList.get(currentState);
            short nextState = transitionArray[character];
            if (token.length - 1 == i) { // is last -> final state.
                transitionArray[character] = (short) (- tokenNumber - 1);
            }
            else if (nextState == INIT_STATE) { // No such state yet..
                transitionList.add(new short[256]);
                nextState = (short) (transitionList.size() - 1);
                transitionArray[character] = nextState;
            }
            /*
            System.err.println("Char: " + ((char) character) + " -> "
                               + transitionArray[character]);
            */
            currentState = nextState;
        }
    }

    private TokenMatchPattern(short states[][] ) {
        _transitionStates = states;
    }

    /**
     * accessor for the state array - the TokenMatcher accesss this.
     */
    short[][] getStates() {
        return _transitionStates;
    }

    public final static void main(String argv[]) {
        String tok[] = {"<pma:list ", "<pma:äöü", "<input ", "<a " };
        TokenMatchPattern pattern = compile(tok);

        String testString = "hallo<pma:lisblub hallo<pma:äöüxyz";
        byte[] input = testString.getBytes();
        int state = INIT_STATE;
        for (int i=0; i < input.length && state >= 0; ++i) {
            System.err.print(testString.charAt(i));
            int b = input[i] & 0xff;
            state = pattern._transitionStates[state][b];
            System.err.print("->" + state + " ");
        }
        if (state < 0) {
            int tokNum = - state - 1;
            System.err.println("found match." + state + "; " + tok[tokNum]);
        }
        else {
            System.err.println("no match");
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
