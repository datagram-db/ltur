/*
 * LTURResult.java
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

import java.util.HashSet;
import java.util.Map;

/**
 * This class provides the result of the satisfiability
 */
public class LTURResult {

    /**
     * This boolean variable is set to true if the KB is completely coherent with respect to the formulated hypotheses.
     * If that's the case, no contradictions are detected. If the variable is set to false, then the KB itself or
     * the KB joinlty with the queries contain a contradiction
     */
    private final boolean satisfiability;

    /**
     * Set of clauses that are satisfied by the given KB+query
     */
    private HashSet<GraphClause> satisfiedClauses;

    /**
     * Set of clauses that are not satisfied by the given KB+query
     */
    private HashSet<GraphClause> unsatisfiedClauses;

    /**
     * Set of atoms that provide the minimal ground atom assignment that satisfy KB+Query. This means that all the
     * remaining atoms (that do not appear in the minimalInconsistentAtomSets) can be set either to true or to false
     */
    private HashSet<Atom> minimalConsistentAssigment;

    /**
     * This set contains the set of the minimal inconsistent atoms that lead to some errors during the inference steps.
     */
    private HashSet<HashSet<Atom>> minimalInsonsistentAtomSets;

    public boolean isSatisfiable() {
        return satisfiability;
    }

    public LTURResult(boolean satisfiability) {
        this.satisfiability = satisfiability;
    }

    public void setSatisfiedClauses(HashSet<GraphClause> satisfiedClauses) {
        this.satisfiedClauses = satisfiedClauses;
    }

    public void setUnsatisfiedClauses(HashSet<GraphClause> unsatisfiedClauses) {
        this.unsatisfiedClauses = unsatisfiedClauses;
    }

    public void setMinimalConsistentAssigment(ValMap minimalConsistentAssigment) {
        if (this.minimalConsistentAssigment == null) {
            this.minimalConsistentAssigment = new HashSet<>();
        } else {
            this.minimalConsistentAssigment.clear();
        }
        for (Map.Entry<Atom, Integer> x : minimalConsistentAssigment.entrySet()) {
            if (x.getValue() == 0) {
                this.minimalConsistentAssigment.add(x.getKey().negate());
            } else {
                this.minimalConsistentAssigment.add(x.getKey());
            }
        }
    }

    public void setMinimalInsonsistentAtomSets(HashSet<HashSet<Atom>> minimalInsonsistentAtomSets) {
        this.minimalInsonsistentAtomSets = minimalInsonsistentAtomSets;
    }

    @Override
    public String toString() {
        return "LTURResult{" +
                "\n\tsatisfiability := " + satisfiability +
                ",\n\tsatisfiedClauses := " + satisfiedClauses +
                ",\n\tunsatisfiedClauses := " + unsatisfiedClauses +
                ",\n\tminimalConsistentAssigment := " + minimalConsistentAssigment +
                ",\n\tminimalInsonsistentAtomSets := " + minimalInsonsistentAtomSets +
                "\n}";
    }
}
