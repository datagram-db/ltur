/*
 * Atom.java
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

package it.giacomobergami.ltur.structure.atoms;

import java.util.Objects;

/**
 * Represents the manually imputed variable.
 */
public class Atom implements IAtom {
    /**
     * Name representing the atom
     */
    public final String name;

    /**
     * Sets if the atom is negated or not
     */
    public final boolean negated;

    /**
     * Defines an atom which is neither top nor bottom
     * @param name String univocally representing the atom
     */
    public Atom(String name) {
        this(name, false);
    }
    private Atom(String name, boolean value) {
        this.name = name;
        this.negated = value;
    }

    /**
     * Negates the atom: if the atom is negated, it returns a non-negated one, and viceversa
     * @return
     */
    public Atom negate() {
        return new Atom(this.name, !negated);
    }

    /**
     * Returns a non-negated atom
     * @return
     */
    public Atom rawAtom() {
        return new Atom(this.name, false);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Atom atom = (Atom) o;
        return Objects.equals(name, atom.name) &&
                Objects.equals(negated, atom.negated);
    }
    @Override
    public int hashCode() {
        return Objects.hash(name, negated)*2;
    }
    public boolean isNegated() {
        return negated;
    }

    @Override
    public String toString() {
        return (negated ? "¬" : "") + name;
    }

    @Override
    public boolean isTop() {
        return false;
    }

    @Override
    public boolean isBot() {
        return false;
    }
}
