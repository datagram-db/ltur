/*
 * ValMap.java
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

package it.giacomobergami.ltur.structure;

import it.giacomobergami.ltur.structure.atoms.Atom;
import it.giacomobergami.ltur.structure.clauses.GraphClause;

import java.util.ArrayList;
import java.util.HashMap;

public class ValMap extends HashMap<Atom, Integer> {

    @Override
    public boolean containsKey(Object a) {
        if (! (a instanceof  Atom)) return false;
        return super.containsKey(((Atom)a).rawAtom());
    }

    @Override
    public Integer put(Atom key, Integer value) {
        if (containsKey(key) && !get(key).equals(value))
            throw new RuntimeException("Unexpected error: overwriting map values");
        return super.put(key.rawAtom(), value);
    }

    @Override
    public Integer remove(Object a) {
        if (! (a instanceof Atom)) return null;
        return super.remove(((Atom)a).rawAtom());
    }

    public GraphClause updateWithUnsatisfaction(GraphClause gc) {
        ArrayList<Atom> gcaUpdated = new ArrayList<>(gc.getVariables());
        for (Entry<Atom, Integer> x : entrySet()) {
            if (x.getValue() == 1) {
                gcaUpdated.remove(x.getKey().negate());
            }
        }
        if (gcaUpdated.size() == 1) {
            Atom last = gcaUpdated.get(0);
            if (last.isNegated()) {
                put(last.rawAtom(), 1);
            } else {
                put(last, 0);
            }
            return null;
        } else {
            return gc.createUpdatedForLTUREvaluation(gcaUpdated);
        }
    }

    public GraphClause updateWithSatisfaction(GraphClause gc) {
        ArrayList<Atom> gcaUpdated = new ArrayList<>(gc.getVariables());
        for (Entry<Atom, Integer> x : entrySet()) {
            if (x.getValue() == 1) {
                gcaUpdated.remove(x.getKey().negate());
            }
        }
        if (gcaUpdated.size() == 1) {
            Atom last = gcaUpdated.get(0);
            if (last.isNegated()) {
                put(last.rawAtom(), 0);
            } else {
                put(last, 1);
            }
            return null;
        } else {
            return gc.createUpdatedForLTUREvaluation(gcaUpdated);
        }
    }

}
