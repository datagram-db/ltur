/*
 * Main.java
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

import it.giacomobergami.ltur.structure.clauses.HornClause;

import java.util.ArrayList;

public class Main {

    public static void main(String args[]) {
        LTUR ltur = new LTUR();
        ArrayList<HornClause> alhc = new ArrayList<>();
        alhc.add(HornClause.classicHornClause("B", "A"));
        alhc.add(HornClause.negatedHeadHornClause("C", "B"));
        System.out.println(ltur.query(alhc, HornClause.fact("A"), HornClause.fact("C")));
    }

}
