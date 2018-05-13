/*
 * GraphClause.java
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

package it.giacomobergami.ltur.structure.clauses;

import it.giacomobergami.ltur.structure.atoms.Atom;
import it.giacomobergami.ltur.structure.ValMap;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Rewritten Horn Clause in disjunctive normal form. The original horn clause is preserved
 */
public class GraphClause {

    /**
     * disjuncted atoms componing the clause
     */
    private ArrayList<Atom> variables;
    /**
     * Original representation
     */
    private HornClause logic;
    /**
     * Handler containing the only positive atom within the clause
     */
    private Atom positive;
    
    GraphClause(ArrayList<Atom> variables, HornClause logic, Atom positive) {
        this.variables = variables;
        this.logic = logic;
        this.positive = positive;
    }

    public GraphClause() {
        variables = new ArrayList<>();
        positive = null;
    }

    /**
     * Add an atom to the clause
     * @param a
     */
    public void add(Atom a) {
        variables.add(a);
        if (!a.isNegated()) {
            if (positive !=null)
                throw  new RuntimeException("Unexpected error: a disjuncted representation of the clause " +
                        "must contain only one single positive atom.");
            positive = a;
        }
    }

    /**
     * Returns the number of the negated atoms within the clause. This method is useful to LTUR
     * @return
     */
    public int v() {
        int count = 0;
        int n = variables.size();
        for (int i = 0; i<n; i++) {
            if (variables.get(i).isNegated()) count++;
        }
        return count;
    }

    /**
     * Associates the Horn clause to the current element
     * @param hornClause
     * @return
     */
    public GraphClause addOriginal(HornClause hornClause) {
        this.logic = hornClause;
        return this;
    }

    @Override
    public String toString() {
        return variables.stream().map(Atom::toString).collect(Collectors.joining("∨")) +" ["+logic.toString()+"]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GraphClause that = (GraphClause) o;
        return Objects.equals(variables, that.variables) &&
                Objects.equals(logic, that.logic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variables, logic);
    }

    /**
     * Initializes the set of graph clauses from which initialize the algorihtm
     * @param cgc   Clauses collection that are going to be represented by the graph
     * @return
     */
    public static Set<GraphClause> getInitialSet(Collection<GraphClause> cgc) {
        return getInitialSet(new HashSet<>(), cgc);
    }

    public static Set<GraphClause> getInitialSet(Set<GraphClause> sgc, GraphClause x) {
        if (x.v() == 0) sgc.add(x);
        return sgc;
    }

    private static Set<GraphClause> getInitialSet(Set<GraphClause> sgc, Collection<GraphClause> cgc) {
        cgc.forEach(x -> getInitialSet(sgc, x));
        return sgc;
    }


    /**
     * Initializes the value map for the LTUR algorithm
     * @param map
     */
    public void initializeMap(ValMap map) {
        int n = variables.size();
        for (int i = 0; i<n; i++) {
            map.put(variables.get(0).rawAtom(), 0);
        }
    }

    public boolean hasNoPositiveVariable() {
        return v() == variables.size();
    }

    /**
     * Returns the positive variable if it exists. Null otherwise
     * @return
     */
    public Atom positiveVariable() {
        return positive;
    }

    /**
     * Returns the disjuncted representation of the Horn clauses
     * @return
     */
    public ArrayList<Atom> getVariables() {
        return variables;
    }

    /**
     * Returns the atoms composing the clause (independently from its negation or not)
     * @return
     */
    public Collection<Atom> basicVariables() {
        HashSet<Atom> baisc = new HashSet<>();
        for (Atom a : variables) {
            baisc.add(a.rawAtom());
        }
        return baisc;
    }

    /**
     * Updates the clause during the clause evaluation, by removing the atoms for which we already know the assignment
     * @param a     New sets of un-resolved atoms
     * @return      Updated clause
     */
    public GraphClause createUpdatedForLTUREvaluation(ArrayList<Atom> a) {
        return new GraphClause(a, logic, positive);
    }
}
