/**
* Opt4J is free software: you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License as published by the Free
* Software Foundation, either version 3 of the License, or (at your option) any
* later version.
* 
* Opt4J is distributed in the hope that it will be useful, but WITHOUT ANY
* WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
* A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
* details.
* 
* You should have received a copy of the GNU Lesser General Public License
* along with Opt4J. If not, see http://www.gnu.org/licenses/.
*/

package org.opt4j.benchmarks.wfg;

import java.util.ArrayList;
import java.util.List;

import org.opt4j.benchmarks.K;
import org.opt4j.benchmarks.M;

import com.google.inject.Inject;

/**
* The {@link WFG6} benchmark function.
* 
* @author lukasiewycz
* 
*/
public class WFG6 extends WFGEvaluator {

/**
* Constructs a {@link WFG6} benchmark function.
* 
* @param k
*            the position parameters
* @param M
*            the number of objectives
*/
@Inject
public WFG6(@K int k, @M int M) {
	super(k, M);
}

public static List<Double> t2(final List<Double> y, final int k, final int M) {
	final int n = y.size();

	assert (k >= 1);
	assert (k < n);
	assert (M >= 2);
053                    assert (k % (M - 1) == 0);
054    
055                    List<Double> t = new ArrayList<Double>();
056    
057                    for (int i = 1; i <= M - 1; i++) {
058                            final int head = (i - 1) * k / (M - 1);
059                            final int tail = i * k / (M - 1);
060    
061                            final List<Double> y_sub = y.subList(head, tail);
062    
063                            t.add(WFGTransFunctions.r_nonsep(y_sub, k / (M - 1)));
064                    }
065    
066                    final List<Double> y_sub = y.subList(k, n);
067    
068                    t.add(WFGTransFunctions.r_nonsep(y_sub, n - k));
069    
070                    return t;
071            }
072    
073            /*
074             * (non-Javadoc)
075             * 
076             * @see org.opt4j.benchmark.wfg.WFGEvaluator#f(java.util.List)
077             */
078            @Override
079            public List<Double> f(List<Double> y) {
080                    y = WFG1.t1(y, k);
081                    y = WFG6.t2(y, k, M);
082    
083                    return WFG4.shape(y);
084            }
085    }




























































