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
package com.github.thorbenlindhauer.network;

import java.util.HashSet;
import java.util.Set;

import com.github.thorbenlindhauer.exception.ModelStructureException;
import com.github.thorbenlindhauer.factor.DiscreteFactor;
import com.github.thorbenlindhauer.factor.TableBasedDiscreteFactor;
import com.github.thorbenlindhauer.variable.Scope;
import com.github.thorbenlindhauer.variable.Variable;

public class DiscreteFactorBuilderImpl implements DiscreteFactorBuilder<DiscreteModelBuilder> {

  protected DiscreteModelBuilderImpl modelBuilder;
  protected Scope graphScope;
  protected Set<Variable> factorVariables;

  public DiscreteFactorBuilderImpl(DiscreteModelBuilderImpl modelBuilder, Scope scope) {
    this.modelBuilder = modelBuilder;
    this.graphScope = scope;
    this.factorVariables = new HashSet<Variable>();
  }

  public DiscreteFactorBuilder<DiscreteModelBuilder> scope(String... variableIds) {
    for (String variableId : variableIds) {
      Variable variable = graphScope.getVariable(variableId);

      if (variable == null) {
        throw new ModelStructureException("Variable " + variableId + " not defined in scope of graph.");
      }

      factorVariables.add(variable);
    }

    return this;
  }

  public DiscreteModelBuilder basedOnTable(double[] table) {
    Scope factorScope = new Scope(factorVariables);
    DiscreteFactor factor = new TableBasedDiscreteFactor(factorScope, table);
    modelBuilder.addFactor(factor);

    return modelBuilder;
  }

}
