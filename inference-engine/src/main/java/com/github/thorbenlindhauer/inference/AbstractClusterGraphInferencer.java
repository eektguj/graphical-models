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
package com.github.thorbenlindhauer.inference;

import java.util.ArrayList;
import java.util.List;

import com.github.thorbenlindhauer.cluster.Cluster;
import com.github.thorbenlindhauer.cluster.ClusterGraph;
import com.github.thorbenlindhauer.cluster.messagepassing.Message;
import com.github.thorbenlindhauer.cluster.messagepassing.MessageListener;
import com.github.thorbenlindhauer.cluster.messagepassing.MessagePassingContext;
import com.github.thorbenlindhauer.cluster.messagepassing.MessagePassingContextFactory;
import com.github.thorbenlindhauer.exception.ModelStructureException;
import com.github.thorbenlindhauer.factor.Factor;
import com.github.thorbenlindhauer.inference.loopy.ClusterGraphCalibrationContext;
import com.github.thorbenlindhauer.inference.loopy.ClusterGraphCalibrationContextFactory;
import com.github.thorbenlindhauer.variable.Scope;

//TODO: think about interface "IncrementalInferencer" that allows to submit observations incrementally
public abstract class AbstractClusterGraphInferencer<T extends Factor<T>> {

  protected static final int MAX_ITERATIONS_PER_EDGE = 10;

  protected ClusterGraph<T> clusterGraph;
  protected boolean messagesPropagated = false;
  protected MessagePassingContext<T> messagePassingContext;
  protected ClusterGraphCalibrationContext<T> calibrationContext;
  protected List<MessageListener<T>> messagePassingListeners;

  public AbstractClusterGraphInferencer(ClusterGraph<T> clusterGraph, MessagePassingContextFactory messageContextFactory, ClusterGraphCalibrationContextFactory<T> calibrationContextFactory) {
    this.clusterGraph = clusterGraph;
    this.messagePassingContext = messageContextFactory.newMessagePassingContext(clusterGraph);
    this.calibrationContext = calibrationContextFactory.buildCalibrationContext(clusterGraph, messagePassingContext);

    this.messagePassingListeners = new ArrayList<MessageListener<T>>();
    this.messagePassingListeners.add(messagePassingContext);
    this.messagePassingListeners.add(calibrationContext);
  }

  protected T getClusterFactorContainingScope(Scope scope) {
    ensureMessagesPropagated();

    for (Cluster<T> cluster : clusterGraph.getClusters()) {
      if (cluster.getScope().contains(scope)) {
        return messagePassingContext.getClusterPotential(cluster).marginal(scope);
      }
    }

    throw new ModelStructureException("There is no cluster that contains scope " + scope + " entirely and "
        + "queries spanning multiple clusters are not yet implemented");
  }

  protected void ensureMessagesPropagated() {
    if (!messagesPropagated) {
      propagateMessages();
    }
  }

  protected void propagateMessages() {
    int currentIteration = 0;
    Message<T> nextMessage = calibrationContext.getNextUncalibratedMessage();

    // TODO: write log if calibration ends due to max iterations reached
    while (nextMessage != null && currentIteration < MAX_ITERATIONS_PER_EDGE * clusterGraph.getEdges().size()) {
      nextMessage.update(messagePassingContext);
      notifyListeners(MessageListener.UPDATE_EVENT, nextMessage);

      nextMessage = calibrationContext.getNextUncalibratedMessage();
      currentIteration++;
    }

    messagesPropagated = true;
  }

  protected void notifyListeners(String event, Message<T> nextMessage) {
    for (MessageListener<T> listener : messagePassingListeners) {
      listener.notify(event, nextMessage);
    }
  }

  public void addMessageListener(MessageListener<T> messageListener) {
    this.messagePassingListeners.add(messageListener);
  }
}
