package com.github.thorbenlindhauer.cluster.messagepassing;

import java.util.HashSet;
import java.util.Set;

import com.github.thorbenlindhauer.cluster.Cluster;
import com.github.thorbenlindhauer.cluster.Edge;
import com.github.thorbenlindhauer.exception.ModelStructureException;
import com.github.thorbenlindhauer.factor.DiscreteFactor;

/**
 * A message is assigned to an edge and its direction is specified by the source cluster
 * 
 * @author Thorben
 */
public class SumProductMessage implements Message {

  protected Cluster sourceCluster;
  protected DiscreteFactor potential;
  
  protected Edge edge;
  
  public SumProductMessage(Cluster cluster, Edge edge) {
    this.edge = edge;
    if (!edge.connects(cluster)) {
      throw new ModelStructureException("Invalid message: Cluster " + cluster + " is not involved in edge " + edge);
    }
    
    this.sourceCluster = cluster;
  }
  
  @Override
  public void update(MessagePassingContext messagePassingContext) {
    Set<Message> inMessages = new HashSet<Message>();
    Set<Edge> inEdges = sourceCluster.getOtherEdges(edge);
    
    for (Edge inEdge : inEdges) {
      inMessages.add(messagePassingContext.getMessage(inEdge, inEdge.getTarget(sourceCluster)));
    }
    
    potential = messagePassingContext.getJointDistribution(sourceCluster);
    
    // ignore null potentials
    for (Message inMessage : inMessages) {
      if (potential == null) {
        potential = inMessage.getPotential();
      } else if (inMessage.getPotential() != null) {
        potential = potential.product(inMessage.getPotential());
      }
    }
    
    if (potential != null) {
      potential = potential.marginal(edge.getScope());
    }
  }
  
  @Override
  public DiscreteFactor getPotential() {
    return potential;
  }
  
  @Override
  public Cluster getTargetCluster() {
    return edge.getTarget(sourceCluster);
  }
  
  @Override
  public Cluster getSourceCluster() {
    return sourceCluster;
  }

  @Override
  public Edge getEdge() {
    return edge;
  }
}