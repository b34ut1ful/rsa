package com.ricedotwho.rsa.component.impl.pathfinding;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.class_2338;

public class PathNode {
   private final class_2338 pos;
   private final double heuristicCost;
   public int heapPosition;
   private PathNode parent;
   private float yaw = Float.MIN_VALUE;
   private float pitch = Float.MIN_VALUE;
   private int index;

   public PathNode(class_2338 pos, PathNode parent, Goal goal) {
      this.pos = pos;
      this.parent = parent;
      this.index = parent == null ? 0 : parent.index + 1;
      this.heapPosition = -1;
      this.heuristicCost = goal.heuristic(pos);
   }

   public boolean hasBeenScanned() {
      return this.yaw != Float.MIN_VALUE;
   }

   public boolean isOpen() {
      return this.heapPosition != -1;
   }

   @Override
   public int hashCode() {
      long hash = 3241L;
      hash = 3457689L * hash + this.pos.method_10263();
      hash = 8734625L * hash + this.pos.method_10264();
      hash = 2873465L * hash + this.pos.method_10260();
      return (int)hash;
   }

   public static int hashCode(class_2338 pos) {
      long hash = 3241L;
      hash = 3457689L * hash + pos.method_10263();
      hash = 8734625L * hash + pos.method_10264();
      hash = 2873465L * hash + pos.method_10260();
      return (int)hash;
   }

   public synchronized PathNode getParent() {
      return this.parent;
   }

   private void testOffset(Predicate<class_2338> predicate, List<class_2338> blocks, class_2338 pos) {
      if (predicate.test(pos)) {
         blocks.add(pos);
      }
   }

   public synchronized double getCost(float nodeCost) {
      return this.getMoveCost(nodeCost) + this.heuristicCost;
   }

   public synchronized double getMoveCost(float nodeCost) {
      return this.index * nodeCost;
   }

   public synchronized void updateParent(PathNode parent) {
      this.parent = parent;
      this.index = parent.index + 1;
   }

   @Override
   public synchronized boolean equals(Object obj) {
      PathNode other = (PathNode)obj;
      return this.pos.method_10263() == other.pos.method_10263()
         && this.pos.method_10264() == other.pos.method_10264()
         && this.pos.method_10260() == other.pos.method_10260();
   }

   public class_2338 getPos() {
      return this.pos;
   }

   public double getHeuristicCost() {
      return this.heuristicCost;
   }

   public float getYaw() {
      return this.yaw;
   }

   public void setYaw(float yaw) {
      this.yaw = yaw;
   }

   public float getPitch() {
      return this.pitch;
   }

   public void setPitch(float pitch) {
      this.pitch = pitch;
   }

   public int getIndex() {
      return this.index;
   }
}
