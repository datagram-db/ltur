/*
 * HornClause.java
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

import java.util.Arrays;
import java.util.Objects;

/**
 * Logical formula as manually imputed by the user. This class provides the classical representation of the class
 */
public class HornClause {
    /**
     * Horn clause's body
     */
    private Atom[] body;
    /**
     * Horn clause's head. It must be a raw atom (non negated)
     */
    private Atom head;
    /**
     * Determines whether the clauses' head is negated or not.
     */
    public boolean isHeadNegated;

    /**
     * Side note: this constructor is made private to avoid the user to directly provide a negated headed.
     * Please use the static methods to create correct clauses
     * @param head
     * @param isHeadNegated
     * @param body
     */
    private HornClause(Atom head, boolean isHeadNegated, String... body) {
        if (body == null) this.body = new Atom[0];
        else {
            int n = body.length;
            this.body = new Atom[n];
            for (int i = 0; i<n; i++) {
                this.body[i] = new Atom(body[i]);
            }
        }
        this.head = head;
        this.isHeadNegated = isHeadNegated;
    }

    /**
     * This constructor is used to create facts within a graph (always true statements)
     * @param head      String representation of the atom
     * @return          The instantiated fact (clause)
     */
    public static HornClause fact(String head) {
        return new HornClause(new Atom(head), false);
    }

    /**
     * This constructor is used to create negated facts
     * @param head      String representation of the atom
     * @return          The instantiated fact (clause)
     */
    public static HornClause negatedFact(String head) {
        return new HornClause(new Atom(head), true);
    }

    /**
     * Classical horn rule, where the head is not negated
     * @param head  Non-negated head of the clause
     * @param body  Body containing no negations
     * @return      The instantiated clause
     */
    public static HornClause classicHornClause(String head, String... body) {
        return new HornClause(new Atom(head), false, body);
    }

    /**
     * Horn rule containing a negated head
     * @param head  Negated clause's hear
     * @param body  Body containing no negations
     * @return      The instantiated clause
     */
    public static HornClause negatedHeadHornClause(String head, String... body) {
        return new HornClause(new Atom(head), true, body);
    }

    /**
     * Converts the horn clause representation into graph clauses (that is a clause made of disjunctions)
     * @return
     */
    public GraphClause asGraphClause() {
        GraphClause gc = new GraphClause();
        for (Atom a : body) {
            gc.add(a.negate());
        }
        if (isHeadNegated) {
            gc.add(head.negate());
        } else {
            gc.add(head);
        }
        return gc.addOriginal(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HornClause that = (HornClause) o;
        return isHeadNegated == that.isHeadNegated &&
                Arrays.equals(body, that.body) &&
                Objects.equals(head, that.head);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(head, isHeadNegated);
        result = 31 * result + Arrays.hashCode(body);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i<body.length; i++) {
            sb.append(body[i].toString());
            if (i != body.length-1) sb.append(" ∧ ");
        }
        if (body.length > 0) {
            sb.append("⇒");
        }
        if (isHeadNegated) {
            sb.append(head.negate().toString());
        } else {
            sb.append(head.toString());
        }
        return sb.toString();
    }

}
