/* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.github.thorbenlindhauer.cluster.ep;

import java.util.HashSet;
import java.util.Set;

import com.github.thorbenlindhauer.cluster.Cluster;
import com.github.thorbenlindhauer.factor.Factor;
import com.github.thorbenlindhauer.factor.FactorSet;
import com.github.thorbenlindhauer.factor.FactorUtil;
import com.github.thorbenlindhauer.variable.Scope;

/**
 * @author Thorben
 *
 */
public class DefaultPotentialResolver<T extends Factor<T>> implements ClusterPotentialResolver<T> {

  protected Cluster<T> cluster;

  public DefaultPotentialResolver(Cluster<T> cluster) {
    this.cluster = cluster;
  }

  @Override
  public FactorSet<T> project(FactorSet<T> additionalFactors, Scope projectionScope) {
    Set<T> factors = new HashSet<T>(cluster.getFactors());

    if (additionalFactors != null) {
      factors.addAll(additionalFactors.getFactors());
    }

    FactorSet<T> result = new FactorSet<T>();
    result.add(FactorUtil.jointDistribution(factors).marginal(projectionScope));
    return result;
  }



}
