/*
 * Copyright (c) 2014 Max Schrimpf
 *
 * This file is part of the parallel computing term paper for the Zurich university of applied sciences.
 *
 * It is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package ch.zhaw.parallelComputing.model.sentiment;

import ch.zhaw.mapreduce.KeyValuePair;
import ch.zhaw.mapreduce.ReduceEmitter;
import ch.zhaw.mapreduce.ReduceInstruction;

import java.util.Iterator;
import java.util.logging.Logger;

public class DateAvgReducer implements ReduceInstruction {
    private static final Logger LOG = Logger.getLogger(DateAvgReducer.class.getName());

    @Override
    public void reduce(ReduceEmitter emitter, String key, Iterator<KeyValuePair> values) {
        LOG.entering(getClass().getName(), "reduce");

        Double[] tmp = new Double[]{0.0, 0.0};

        while (values.hasNext()) {
            KeyValuePair pair = values.next();
            tmp[0]++;
            tmp[1] += Double.parseDouble(pair.getValue());
        }

        emitter.emit("" + tmp[1] / tmp[0]);
    }
}

