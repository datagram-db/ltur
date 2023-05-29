/*
 * LTUR.java
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

package it.giacomobergami.ltur;

import com.google.common.collect.HashMultimap;
import it.giacomobergami.ltur.structure.ValMap;
import it.giacomobergami.ltur.structure.graph.LTURGraph;
import it.giacomobergami.ltur.structure.LTURResult;
import it.giacomobergami.ltur.structure.atoms.Atom;
import it.giacomobergami.ltur.structure.atoms.Bot;
import it.giacomobergami.ltur.structure.atoms.IAtom;
import it.giacomobergami.ltur.structure.atoms.Top;
import it.giacomobergami.ltur.structure.clauses.GraphClause;
import it.giacomobergami.ltur.structure.clauses.HornClause;

import java.util.*;
import java.util.function.BiFunction;

/**
 * Hypothesis: facts are always assumed true. The KB only contains the (grounded) rules that connect with
 * assertions or not to given atoms. The atoms (facts) per se are never completely false or true.
 *
 * This inference algorithm will be used to store the outcome of the learning phase stating whether the patterns within
 * the data are negative or not.
 */
public class LTUR {

    private Set<GraphClause> S;
    LTURGraph graph;
    HashSet<GraphClause> gcs;
    HashMap<GraphClause, Integer> vMap;
    ValMap val;

    public LTUR() {
        gcs = new HashSet<>();
        graph = new LTURGraph();
        vMap = new HashMap<>();
        val = new ValMap();
    }

    protected void initialize(Collection<HornClause> clauses) {
        gcs.clear();
        graph.clear();
        vMap.clear();
        val.clear();
        graph.dg.addNode(Top.instance());
        graph.dg.addNode(Bot.instance());
        for (HornClause hc : clauses) {
            GraphClause gc = hc.asGraphClause();
            Collection<Atom> bv = gc.basicVariables();
            bv.forEach(graph.dg::addNode);
            Atom pos = gc.positiveVariable();
            if (pos == null) {
                for (Atom a : bv) {
                    graph.dg.putEdgeValue(a, Bot.instance(), gc);
                }
            } else {
                if (bv.size() == 1) {
                    graph.dg.putEdgeValue(Top.instance(), pos, gc);
                } else {
                    for (Atom a : gc.getVariables()) {
                        if (a.isNegated())
                            graph.dg.putEdgeValue(a.rawAtom(), pos, gc);
                    }
                }
            }
            gcs.add(gc);
            vMap.put(gc, gc.v());
            //gc.initializeMap(val);
        }
        S = GraphClause.getInitialSet(gcs);
    }

    public void update(HornClause hc) {
        GraphClause gc = hc.asGraphClause();
        gcs.add(gc);
        vMap.put(gc, 0);
        //gc.initializeMap(val);
        S = GraphClause.getInitialSet(S, gc);
    }

    /**
     *
     * @param kb            Clauses representing the Knowledge Base
     * @param clauses       Clauses representing the actual query
     * @return              Satisfiability information
     */
    public LTURResult query(Collection<HornClause> kb, HornClause... clauses) {

        // Adding the clauses to the KB and initializing LTUR
        // TODO: please note that we can always add only the clauses of interest for LTUR and then remove them.
        LTURResult result;
        for (HornClause x : clauses) {
            kb.add(x);
        }
        initialize(kb);
        boolean satisfiability;
        HashSet<GraphClause> satisfied = new HashSet<>(gcs);
        HashSet<Atom> expectedAtoms = new HashSet<>();
        for (HornClause x : clauses) {
            satisfied.remove(x.asGraphClause());
        }
        HashSet<GraphClause> unsatisfied = new HashSet<>();
        HashSet<HashSet<Atom>> minimalAtomInconsistency = new HashSet<>();

        // Running the actual ltur
        satisfiability = ltur(true, satisfied, expectedAtoms, unsatisfied);
        result = new LTURResult(satisfiability);
        result.setSatisfiedClauses(satisfied);
        result.setUnsatisfiedClauses(unsatisfied);

        // Tries to infer the assignments from the set of satisfied and unsatisfied clauses
        // This map has then to be reduced
        setMaximumMap(satisfied, unsatisfied);

        ArrayList<Atom> toRemove = new ArrayList<>();
        for (Map.Entry<Atom, Integer> aI : val.entrySet()) {
            if ((aI.getValue() == 0 && expectedAtoms.contains(aI.getKey().rawAtom())) ||
                    (aI.getValue() == 1 && expectedAtoms.contains(aI.getKey().rawAtom().negate()))) {
                HashSet<Atom> mis = new HashSet<>();
                mis.add(aI.getKey());
                mis.add(aI.getKey().negate());
                minimalAtomInconsistency.add(mis);
                toRemove.add(aI.getKey());
            }
        }
        toRemove.forEach(val::remove);
        for (HornClause hc : clauses) {
            GraphClause gc = hc.asGraphClause();
            ArrayList<Atom> vars = gc.getVariables();
            if (vars.size() == 1) {
                Atom a = vars.get(0);
                boolean aNegated = a.isNegated();
                Atom raw = a.rawAtom();
                Integer mapValue = val.get(raw);
                if (mapValue == null) {
                    if (!(toRemove.contains(raw) || toRemove.contains(raw.negate())))
                        val.put(raw, aNegated ? 0 : 1);
                } else {
                    if (!mapValue.equals(aNegated ? 0 : 1)) {
                        HashSet<Atom> mis = new HashSet<>();
                        mis.add(raw);
                        mis.add(raw.negate());
                        minimalAtomInconsistency.add(mis);
                        val.remove(raw);
                    }
                }
            }
        }

        result.setMinimalConsistentAssigment(val);
        result.setMinimalInsonsistentAtomSets(minimalAtomInconsistency);
        return result;
    }

    private void setMaximumMap(HashSet<GraphClause> satisfied, HashSet<GraphClause> unsatisfied) {
        int valSize;
        do {
            valSize = val.size();
            setSatisMap(satisfied, val);
            setUnsatisMap(unsatisfied, val);
        } while (valSize != val.size());
    }

    private boolean ltur(boolean satisfiability, HashSet<GraphClause> satisfied, HashSet<Atom> expectedAtoms, HashSet<GraphClause> unsatisfied) {
        while (!S.isEmpty()) {
            GraphClause j = S.iterator().next();
            S.remove(j);
            Atom x_i = j.positiveVariable();
            if (x_i == null) {
                expectedAtoms.add(x_i.negate());
                satisfied.remove(j);
                unsatisfied.add(j);
                satisfiability = false;
            } else {
                // for every                    x_i -[h]-> y
                HashMultimap<IAtom, GraphClause> adj = graph.dg.adjacencyList(x_i);
                for (Map.Entry<IAtom, Collection<GraphClause>> y__hc : adj.asMap().entrySet()) {
                    IAtom y = y__hc.getKey();
                    for (GraphClause h : y__hc.getValue()) {
                        vMap.put(h, vMap.get(h)-1);
                        if (vMap.get(h) == 0) {
                            Integer valY = val.get(y);
                            if (y.isBot()) {
                                expectedAtoms.add(x_i.negate());
                                satisfied.remove(h);
                                unsatisfied.add(h);
                                satisfiability = false;
                            } else if ((!y.isTop()) && (valY == null || valY.equals(0))) {
                                S.add(h);
                                val.put(((Atom)y), 1);
                            }
                        }

                    }
                }
            }
        }
        return satisfiability;
    }

    private static void setValMap(HashSet<GraphClause> unsatisfied, ValMap val, BiFunction<ValMap, GraphClause, GraphClause> f) {
        HashSet<GraphClause> cp = new HashSet<>(unsatisfied);
        boolean isEqual = true;
        do {
            ArrayList<GraphClause> toRemove = new ArrayList<>();
            HashMap<GraphClause, GraphClause> oldToNew = new HashMap<>();
            for (GraphClause unsatis : unsatisfied) {
                GraphClause update = f.apply(val, unsatis);
                if (update == null)
                    toRemove.add(unsatis);
                else if (!unsatis.equals(update))
                    oldToNew.put(unsatis, update);
            }
            HashSet<GraphClause> step = new HashSet<>(unsatisfied);
            step.retainAll(toRemove);
            step.removeAll(oldToNew.keySet());
            step.addAll(oldToNew.values());
            if (step.equals(cp)) {
                isEqual = true;
            } else {
                cp = step;
                isEqual = false;
            }
        } while (!isEqual);
    }

    private static void setUnsatisMap(HashSet<GraphClause> unsatisfied, ValMap val) {
        setValMap(unsatisfied, val, ValMap::updateWithUnsatisfaction);
    }

    private static void setSatisMap(HashSet<GraphClause> unsatisfied, ValMap val) {
        setValMap(unsatisfied, val, ValMap::updateWithSatisfaction);
    }


}
