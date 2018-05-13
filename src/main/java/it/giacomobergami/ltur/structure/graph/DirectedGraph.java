/*
 * DirectedGraph.java
 * This file is part of ltur
 *
 * Copyright (C) 2018 giacomo
 *
 * ltur is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * ltur is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ltur. If not, see <http://www.gnu.org/licenses/>.
 */

package it.giacomobergami.ltur.structure.graph;

import com.google.common.collect.HashMultimap;

import java.util.HashMap;

public class DirectedGraph<V,E> {

    public HashMap<V, HashMultimap<V, E>> graph;

    public DirectedGraph() {
        graph = new HashMap<>();
    }

    public void addNode(V top) {
        graph.putIfAbsent(top, HashMultimap.create());
    }

    public void putEdgeValue(V source, V destination, E edgeIdentifier) {
        addNode(source);
        addNode(destination);
        graph.get(source).put(destination, edgeIdentifier);
    }

    public HashMultimap<V, E> adjacencyList(V source) {
        return graph.getOrDefault(source, HashMultimap.create());
    }

    public void clear() {
        graph.clear();
    }
}
