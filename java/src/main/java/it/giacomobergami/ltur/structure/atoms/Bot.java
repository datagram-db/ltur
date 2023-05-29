/*
 * Bot.java
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

/**
 * This class represents the falsehood
 */
public class Bot implements IAtom {

    private Bot() {}
    private static Bot t = new Bot();
    public static Bot instance() {
        return t;
    }
    @Override
    public boolean isTop() {
        return false;
    }

    @Override
    public boolean isBot() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Bot);
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String toString() {
        return "⊥";
    }
}
