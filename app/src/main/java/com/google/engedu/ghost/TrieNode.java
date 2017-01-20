/* Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.ghost;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class TrieNode {
    // A map from the next character in the alphabet to the trie node
    // containing those words
    private HashMap<Character, TrieNode> children;
    // If true, this node represents a complete word.
    private boolean isWord;

    public TrieNode() {
        children = new HashMap<>();
        isWord = false;
    }

    /**
     * Add the string as a child of this trie node.
     *
     * @param s String representing partial suffix of a word.
     */
    public void add(String s) {
        // TODO(you): add String s to this node.
        if (s.length() == 0) {
            isWord = true;
        } else {
            TrieNode childNode;
            if (children.containsKey(s.charAt(0))) {
                childNode = children.get(s.charAt(0));
            } else {
                childNode = new TrieNode();
                children.put(s.charAt(0), childNode);
            }
            childNode.add(s.substring(1));
        }
    }

    /**
     * Determine whether this node is part of a complete word for the string.
     *
     * @param s String representing partial suffix of a word.
     * @return
     */
    public boolean isWord(String s) {
        // TODO(you): determine whether this node is part of a complete word
        // for String s.
        if (s.length() == 0) {
            return isWord;
        } else {
            if (children.containsKey(s.charAt(0)))
                return children.get(s.charAt(0)).isWord(s.substring(1));
            else
                return false;
        }
    }

    /**
     * Find any complete word with this partial segment.
     *
     * @param s String representing partial suffix of a word.
     * @return
     */
    public String getAnyWordStartingWith(String s) {
        TrieNode node = this;
        StringBuilder word = new StringBuilder();
        int ind = 0;
        while (word.length() <= s.length() || !node.isWord) {
            if (node == null)
                return null;
            if (ind < s.length()) {
                word.append(s.charAt(ind));
                node = node.children.get(s.charAt(ind));
                ind++;
            } else {
                Set<Character> keys = node.children.keySet();
                if (keys.isEmpty())
                    return null;
                else {
                    int randInd = (int) (Math.random() * keys.size());
                    Iterator<Character> it = keys.iterator();
                    for (int i = 0; i < randInd - 1; i++)
                        it.next();
                    Character c = it.next();
                    word.append(c);
                    node = node.children.get(c);
                }
            }
        }
        return word.toString();
    }

    /**
     * Find a good complete word with this partial segment.
     * <p>
     * Definition of "good" left to implementor.
     *
     * @param s String representing partial suffix of a word.
     * @return
     */
    public String getGoodWordStartingWith(String s) {
        TrieNode node = this;
        TrieNode prefixNode;
        StringBuilder word = new StringBuilder(s);
        int ind = 0;
        int numChars = 0;
        while (ind < s.length()) {
            if (node == null)
                return null;
            node = node.children.get(s.charAt(ind));
            ind++;
        }
        prefixNode = node;
        int numTimes = 0;
        if (prefixNode.children.isEmpty())
            return null;
        while (!node.isWord || numChars % 2 != 0) {
            if (node.isWord) {
                node = prefixNode;
                numTimes++;
                if (numTimes > 100)
                    return word.toString();
                word = new StringBuilder(s);
                numChars = 0;
            }
            Set<Character> keys = node.children.keySet();
            if (keys.isEmpty())
                node = prefixNode;
            else {
                int randInd = (int) (Math.random() * keys.size());
                Iterator<Character> it = keys.iterator();
                for (int i = 0; i < randInd; i++)
                    it.next();
                Character c = it.next();
                word.append(c);
                node = node.children.get(c);
                numChars++;
            }
        }
        return word.toString();
    }
}