/*
 * LTURGraph.java
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

import it.giacomobergami.ltur.structure.atoms.IAtom;
import it.giacomobergami.ltur.structure.clauses.GraphClause;

public class LTURGraph {
    public DirectedGraph<IAtom, GraphClause> dg;

    public LTURGraph() {
        dg = new DirectedGraph<>();
    }

    public void clear() {
        dg.clear();
    }
}
