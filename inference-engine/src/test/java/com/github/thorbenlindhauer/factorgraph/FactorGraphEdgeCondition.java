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
package com.github.thorbenlindhauer.factorgraph;

import org.assertj.core.api.Condition;

import com.github.thorbenlindhauer.factor.Factor;

public class FactorGraphEdgeCondition<T extends Factor<T>> extends Condition<FactorGraphEdge<T>> {

  protected FactorGraphNode<T> node1;
  protected FactorGraphNode<T> node2;

  public FactorGraphEdgeCondition(FactorGraphNode<T> node1, FactorGraphNode<T> node2) {
    this.node1 = node1;
    this.node2 = node2;
  }

  @Override
  public boolean matches(FactorGraphEdge<T> edge) {
    return edge.connects(node1) && edge.connects(node2);
  }
}
