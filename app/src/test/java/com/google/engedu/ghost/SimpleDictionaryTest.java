/*
 *  Copyright 2016 Google Inc. All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.google.engedu.ghost;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class SimpleDictionaryTest {

    SimpleDictionary dictionary;

    @Before
    public void createDictionary() {
        ArrayList<String> words = new ArrayList<>(Arrays.asList(new
                String[]{"alpha", "alphabet", "beast", "bet", "beta", "cake",
                "drone", "match", "math", "meet", "round", "zoo"}));
        dictionary = new SimpleDictionary(words, 10);
    }

    @Test
    public void testIsWord() {
        assertEquals(false, dictionary.isWord("zo"));
        assertEquals(true, dictionary.isWord("zoo"));
        assertEquals(true, dictionary.isWord("drone"));
    }

    @Test
    public void testGetAnyWordStartingWith() {
        assertEquals("zoo", dictionary.getAnyWordStartingWith("zo"));
        assertEquals(null, dictionary.getAnyWordStartingWith("alb"));
        assertEquals("cake", dictionary.getAnyWordStartingWith("c"));
    }

    @Test
    public void testGetGoodWordStartingWith() {
        assertEquals("match", dictionary.getGoodWordStartingWith("m"));
        assertEquals("alphabet", dictionary.getGoodWordStartingWith("al"));
        assertEquals("beta", dictionary.getGoodWordStartingWith("be"));
    }
}
