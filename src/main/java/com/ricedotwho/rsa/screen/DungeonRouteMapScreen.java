package com.ricedotwho.rsa.screen;

import com.ricedotwho.rsa.module.impl.dungeon.autoroutes.AutoRoutes;
import com.ricedotwho.rsm.component.impl.location.Island;
import com.ricedotwho.rsm.component.impl.location.Location;
import com.ricedotwho.rsm.component.impl.map.Map;
import com.ricedotwho.rsm.component.impl.map.handler.Dungeon;
import com.ricedotwho.rsm.component.impl.map.handler.DungeonInfo;
import com.ricedotwho.rsm.component.impl.map.map.Door;
import com.ricedotwho.rsm.component.impl.map.map.Room;
import com.ricedotwho.rsm.component.impl.map.map.RoomState;
import com.ricedotwho.rsm.component.impl.map.map.Tile;
import com.ricedotwho.rsm.component.impl.map.map.UniqueRoom;
import com.ricedotwho.rsm.data.Pair;
import com.ricedotwho.rsm.utils.Accessor;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.class_11908;
import net.minecraft.class_11909;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_437;

public class DungeonRouteMapScreen extends class_437 implements Accessor {
   private static final int PANEL_BACKGROUND = 0x66FFFFFF;
   private static final int ROUTEABLE_BORDER = -11937851;
   private static final int CURRENT_ROOM_BORDER = -657414;
   private static final int HOVER_BORDER = -11930;
   private static final int ROOM_OUTLINE = 570425344;
   private static final int TEXT = -1446155;
   private static final int SUBTLE_TEXT = -6839372;
   private static final int INACTIVE_TEXT = -9537916;
   private final AutoRoutes autoRoutes;
   private final List<DungeonRouteMapScreen.RoomHitbox> hitboxes = new ArrayList<>();

   public DungeonRouteMapScreen(AutoRoutes autoRoutes) {
      super(class_2561.method_43470("Route Map"));
      this.autoRoutes = autoRoutes;
   }

   public void method_25394(class_332 context, int mouseX, int mouseY, float delta) {
      DungeonRouteMapScreen.MapLayout layout = this.createLayout();
      this.hitboxes.clear();
      this.drawPanel(context, layout);
      List<UniqueRoom> renderableRooms = this.getRenderableRooms();
      Set<UniqueRoom> routeableRooms = renderableRooms.stream().filter(this.autoRoutes::hasStartNode).collect(Collectors.toSet());
      Room currentRoom = Map.getCurrentRoom();
      if (Location.getArea().is(Island.Dungeon) && !Dungeon.isInBoss()) {
         this.renderTiles(context, layout);
         Room hoveredRoom = this.findHoveredRoom(mouseX, mouseY);
         this.renderRoomHighlights(context, layout, currentRoom, hoveredRoom, renderableRooms, routeableRooms);
         this.renderRoomLabels(context, layout, renderableRooms, routeableRooms);
         this.renderFooter(context, layout, hoveredRoom, routeableRooms);
         super.method_25394(context, mouseX, mouseY, delta);
      } else {
         context.method_27534(
            this.field_22793, class_2561.method_43470("Open this map inside a dungeon room."), this.field_22789 / 2, this.field_22790 / 2, -1446155
         );
         super.method_25394(context, mouseX, mouseY, delta);
      }
   }

   private void drawPanel(class_332 context, DungeonRouteMapScreen.MapLayout layout) {
      int panelLeft = layout.mapX() - 20;
      int panelTop = layout.mapY() - 20;
      int panelRight = layout.mapX() + layout.mapWidth() + 20;
      int panelBottom = layout.mapY() + layout.mapHeight() + 20;
      context.method_25294(panelLeft, panelTop, panelRight, panelBottom, PANEL_BACKGROUND);
      context.method_27534(this.field_22793, class_2561.method_43470("Dungeon Route Map"), this.field_22789 / 2, panelTop + 6, -1446155);
      context.method_27534(
         this.field_22793, class_2561.method_43470("Left click a room to path to its route start."), this.field_22789 / 2, panelTop + 18, -6839372
      );
   }

   private void renderTiles(class_332 context, DungeonRouteMapScreen.MapLayout layout) {
      List<UniqueRoom> renderableRooms = this.getRenderableRooms();

      for (int gridY = 0; gridY <= 10; gridY++) {
         for (int gridX = 0; gridX <= 10; gridX++) {
            Tile tile = DungeonInfo.getDungeonList()[gridY * 11 + gridX];
            if (tile != null) {
               DungeonRouteMapScreen.TileRect rect = this.getTileRect(layout, gridX, gridY, tile);
               if (rect.width() > 0 && rect.height() > 0) {
                  if (tile instanceof Door door) {
                     int color = this.adjustStateColor(door.getColor().getRGB(), door.getState());
                     context.method_25294(rect.x1(), rect.y1(), rect.x2(), rect.y2(), color);
                     this.drawBorder(context, rect.x1(), rect.y1(), rect.x2(), rect.y2(), 570425344);
                  } else if (tile instanceof Room room
                     && !room.isSeparator()
                     && room.getUniqueRoom() != null
                     && room.getUniqueRoom().getMainRoom() != null
                     && !"Unknown".equalsIgnoreCase(room.getUniqueRoom().getName())) {
                     int color = this.adjustStateColor(room.getColor().getRGB(), room.getState());
                     context.method_25294(rect.x1(), rect.y1(), rect.x2(), rect.y2(), color);
                     this.hitboxes.add(new DungeonRouteMapScreen.RoomHitbox(room, rect));
                  }
               }
            }
         }
      }

      this.renderJoinedRooms(context, layout, renderableRooms);
   }

   private void renderRoomHighlights(
      class_332 context,
      DungeonRouteMapScreen.MapLayout layout,
      Room currentRoom,
      Room hoveredRoom,
      List<UniqueRoom> renderableRooms,
      Set<UniqueRoom> routeableRooms
   ) {
      UniqueRoom currentUnique = currentRoom == null ? null : currentRoom.getUniqueRoom();
      UniqueRoom hoveredUnique = hoveredRoom == null ? null : hoveredRoom.getUniqueRoom();

      for (UniqueRoom uniqueRoom : renderableRooms) {
         int borderColor = 0;
         if (currentUnique == uniqueRoom) {
            borderColor = -657414;
         }

         if (hoveredUnique == uniqueRoom) {
            borderColor = -11930;
         }

         if (borderColor != 0) {
            for (Room tile : uniqueRoom.getTiles()) {
               if (!tile.isSeparator()) {
                  Pair<Integer, Integer> arrayPos = tile.getArrayPosition();
                  DungeonRouteMapScreen.TileRect rect = this.getTileRect(layout, (Integer)arrayPos.getFirst(), (Integer)arrayPos.getSecond(), tile);
                  if (rect.width() > 0 && rect.height() > 0) {
                     this.drawBorder(context, rect.x1(), rect.y1(), rect.x2(), rect.y2(), borderColor);
                  }
               }
            }
         }
      }
   }

   private void renderRoomLabels(class_332 context, DungeonRouteMapScreen.MapLayout layout, List<UniqueRoom> renderableRooms, Set<UniqueRoom> routeableRooms) {
      for (UniqueRoom uniqueRoom : renderableRooms) {
         Room mainRoom = uniqueRoom.getMainRoom();
         if (mainRoom != null) {
            DungeonRouteMapScreen.TileRect bounds = this.getRoomBounds(layout, uniqueRoom);
            if (bounds != null) {
               int centerX = bounds.x1() + bounds.width() / 2;
               int centerY = bounds.y1() + bounds.height() / 2 - 4;
               int maxWidth = Math.max(24, bounds.width() - 10);
               String label = this.fitRoomName(uniqueRoom.getName(), maxWidth);
               if (!label.isEmpty()) {
                  int color = routeableRooms.contains(uniqueRoom) ? -1446155 : -6839372;
                  if (mainRoom.getState() == RoomState.UNDISCOVERED || mainRoom.getState() == RoomState.UNOPENED) {
                     color = -9537916;
                  }

                  context.method_25300(this.field_22793, label, centerX, centerY, color);
               }
            }
         }
      }
   }

   private void renderFooter(class_332 context, DungeonRouteMapScreen.MapLayout layout, Room hoveredRoom, Set<UniqueRoom> routeableRooms) {
      int footerY = layout.mapY() + layout.mapHeight() + 18;
      String footer = "ESC to close";
      int color = -6839372;
      if (hoveredRoom != null && hoveredRoom.getUniqueRoom() != null && hoveredRoom.getUniqueRoom().getMainRoom() != null) {
         UniqueRoom uniqueRoom = hoveredRoom.getUniqueRoom();
         boolean hasRoute = routeableRooms.contains(uniqueRoom);
         footer = uniqueRoom.getName() + (hasRoute ? " | click to route" : " | no start node saved");
         color = hasRoute ? -1446155 : -9537916;
      }

      context.method_25300(this.field_22793, footer, this.field_22789 / 2, footerY, color);
   }

   private Room findHoveredRoom(double mouseX, double mouseY) {
      return this.hitboxes
         .stream()
         .filter(hitbox -> hitbox.contains(mouseX, mouseY))
         .map(DungeonRouteMapScreen.RoomHitbox::room)
         .filter(room -> room.getUniqueRoom() != null && room.getUniqueRoom().getMainRoom() != null)
         .min(Comparator.comparingInt(room -> room.isSeparator() ? 1 : 0))
         .orElse(null);
   }

   private List<UniqueRoom> getRenderableRooms() {
      return DungeonInfo.getUniqueRooms()
         .stream()
         .filter(room -> room != null && room.getMainRoom() != null && !"Unknown".equalsIgnoreCase(room.getName()))
         .sorted(Comparator.comparing(UniqueRoom::getName, String.CASE_INSENSITIVE_ORDER))
         .toList();
   }

   private void renderJoinedRooms(class_332 context, DungeonRouteMapScreen.MapLayout layout, List<UniqueRoom> renderableRooms) {
      for (UniqueRoom uniqueRoom : renderableRooms) {
         for (Room room : uniqueRoom.getTiles()) {
            if (!room.isSeparator()) {
               Pair<Integer, Integer> arrayPos = room.getArrayPosition();
               if (this.findTileInRoom(uniqueRoom, (Integer)arrayPos.getFirst() + 2, (Integer)arrayPos.getSecond()) != null) {
                  this.fillJoin(context, layout, room, (Integer)arrayPos.getFirst(), (Integer)arrayPos.getSecond(), true);
               }

               if (this.findTileInRoom(uniqueRoom, (Integer)arrayPos.getFirst(), (Integer)arrayPos.getSecond() + 2) != null) {
                  this.fillJoin(context, layout, room, (Integer)arrayPos.getFirst(), (Integer)arrayPos.getSecond(), false);
               }
            }
         }
      }
   }

   private Room findTileInRoom(UniqueRoom uniqueRoom, int arrayX, int arrayY) {
      for (Room tile : uniqueRoom.getTiles()) {
         if (!tile.isSeparator()) {
            Pair<Integer, Integer> pos = tile.getArrayPosition();
            if ((Integer)pos.getFirst() == arrayX && (Integer)pos.getSecond() == arrayY) {
               return tile;
            }
         }
      }

      return null;
   }

   private void fillJoin(class_332 context, DungeonRouteMapScreen.MapLayout layout, Room room, int arrayX, int arrayY, boolean horizontal) {
      DungeonRouteMapScreen.TileRect rect = this.getTileRect(layout, arrayX, arrayY, room);
      int color = this.adjustStateColor(room.getColor().getRGB(), room.getState());
      if (horizontal) {
         context.method_25294(rect.x2(), rect.y1(), rect.x2() + layout.connectorSize(), rect.y2(), color);
      } else {
         context.method_25294(rect.x1(), rect.y2(), rect.x2(), rect.y2() + layout.connectorSize(), color);
      }
   }

   private DungeonRouteMapScreen.TileRect getRoomBounds(DungeonRouteMapScreen.MapLayout layout, UniqueRoom uniqueRoom) {
      int minX = Integer.MAX_VALUE;
      int minY = Integer.MAX_VALUE;
      int maxX = Integer.MIN_VALUE;
      int maxY = Integer.MIN_VALUE;

      for (Room tile : uniqueRoom.getTiles()) {
         if (!tile.isSeparator()) {
            Pair<Integer, Integer> arrayPos = tile.getArrayPosition();
            DungeonRouteMapScreen.TileRect rect = this.getTileRect(layout, (Integer)arrayPos.getFirst(), (Integer)arrayPos.getSecond(), tile);
            if (rect.width() > 0 && rect.height() > 0) {
               minX = Math.min(minX, rect.x1());
               minY = Math.min(minY, rect.y1());
               maxX = Math.max(maxX, rect.x2());
               maxY = Math.max(maxY, rect.y2());
            }
         }
      }

      return minX == Integer.MAX_VALUE ? null : new DungeonRouteMapScreen.TileRect(minX, minY, maxX, maxY);
   }

   private DungeonRouteMapScreen.TileRect getTileRect(DungeonRouteMapScreen.MapLayout layout, int gridX, int gridY, Tile tile) {
      int baseX = layout.mapX() + (gridX >> 1) * layout.step();
      int baseY = layout.mapY() + (gridY >> 1) * layout.step();
      boolean xEven = (gridX & 1) == 0;
      boolean yEven = (gridY & 1) == 0;
      if (tile instanceof Door) {
         int doorSpan = layout.doorSpan();
         int doorwayOffset = (layout.roomSize() - doorSpan) / 2;
         if (xEven && !yEven) {
            return new DungeonRouteMapScreen.TileRect(
               baseX + doorwayOffset, baseY + layout.roomSize(), baseX + doorwayOffset + doorSpan, baseY + layout.roomSize() + layout.connectorSize()
            );
         } else {
            return !xEven && yEven
               ? new DungeonRouteMapScreen.TileRect(
                  baseX + layout.roomSize(), baseY + doorwayOffset, baseX + layout.roomSize() + layout.connectorSize(), baseY + doorwayOffset + doorSpan
               )
               : new DungeonRouteMapScreen.TileRect(baseX, baseY, baseX, baseY);
         }
      } else if (xEven && yEven) {
         return new DungeonRouteMapScreen.TileRect(baseX, baseY, baseX + layout.roomSize(), baseY + layout.roomSize());
      } else if (!xEven && !yEven) {
         return new DungeonRouteMapScreen.TileRect(
            baseX, baseY, baseX + layout.roomSize() + layout.connectorSize(), baseY + layout.roomSize() + layout.connectorSize()
         );
      } else {
         return !xEven
            ? new DungeonRouteMapScreen.TileRect(
               baseX + layout.roomSize(), baseY, baseX + layout.roomSize() + layout.connectorSize(), baseY + layout.roomSize()
            )
            : new DungeonRouteMapScreen.TileRect(
               baseX, baseY + layout.roomSize(), baseX + layout.roomSize(), baseY + layout.roomSize() + layout.connectorSize()
            );
      }
   }

   private DungeonRouteMapScreen.MapLayout createLayout() {
      int available = Math.min(this.field_22789 - 120, this.field_22790 - 180);
      int roomSize = Math.max(18, available / 8);

      int connectorSize;
      for (connectorSize = Math.max(4, roomSize / 4); roomSize * 6 + connectorSize * 5 > available && roomSize > 18; connectorSize = Math.max(4, roomSize / 4)) {
         roomSize--;
      }

      int mapWidth = roomSize * 6 + connectorSize * 5;
      int mapX = (this.field_22789 - mapWidth) / 2;
      int mapY = (this.field_22790 - mapWidth) / 2;
      return new DungeonRouteMapScreen.MapLayout(mapX, mapY, roomSize, connectorSize, mapWidth);
   }

   private void drawBorder(class_332 context, int x1, int y1, int x2, int y2, int color) {
      context.method_25294(x1, y1, x2, y1 + 1, color);
      context.method_25294(x1, y2 - 1, x2, y2, color);
      context.method_25294(x1, y1, x1 + 1, y2, color);
      context.method_25294(x2 - 1, y1, x2, y2, color);
   }

   private int adjustStateColor(int color, RoomState state) {
      if (state == RoomState.UNDISCOVERED) {
         return this.multiplyColor(color, 0.35F);
      } else {
         return state == RoomState.UNOPENED ? this.multiplyColor(color, 0.55F) : color;
      }
   }

   private int multiplyColor(int color, float factor) {
      int alpha = color >>> 24;
      int red = (int)((color >> 16 & 0xFF) * factor);
      int green = (int)((color >> 8 & 0xFF) * factor);
      int blue = (int)((color & 0xFF) * factor);
      return alpha << 24 | red << 16 | green << 8 | blue;
   }

   private String fitRoomName(String name, int maxWidth) {
      if (this.field_22793.method_1727(name) <= maxWidth) {
         return name;
      } else {
         String initials = this.toInitials(name);
         if (!initials.isEmpty() && this.field_22793.method_1727(initials) <= maxWidth) {
            return initials;
         } else {
            String trimmed = name;

            while (!trimmed.isEmpty() && this.field_22793.method_1727(trimmed + "...") > maxWidth) {
               trimmed = trimmed.substring(0, trimmed.length() - 1);
            }

            return trimmed.isEmpty() ? "" : trimmed + "...";
         }
      }
   }

   private String toInitials(String name) {
      StringBuilder builder = new StringBuilder();

      for (String part : name.split(" ")) {
         if (!part.isEmpty()) {
            builder.append(part.charAt(0));
         }
      }

      return builder.toString();
   }

   public boolean method_25402(class_11909 click, boolean doubled) {
      if (click.method_74245() == 0) {
         Room hoveredRoom = this.findHoveredRoom(click.comp_4798(), click.comp_4799());
         if (hoveredRoom != null && hoveredRoom.getUniqueRoom() != null) {
            return this.autoRoutes.routeToRoomStart(hoveredRoom.getUniqueRoom());
         }
      }

      return super.method_25402(click, doubled);
   }

   public boolean method_25404(class_11908 input) {
      int keyCode = input.comp_4795();
      if (keyCode == 256) {
         this.method_25419();
         return true;
      } else {
         return super.method_25404(input);
      }
   }

   public void method_25419() {
      this.field_22787.method_1507(null);
   }

   public boolean method_25421() {
      return false;
   }

   private record MapLayout(int mapX, int mapY, int roomSize, int connectorSize, int mapWidth) {
      private int step() {
         return this.roomSize + this.connectorSize;
      }

      private int mapHeight() {
         return this.mapWidth;
      }

      private int doorSpan() {
         return Math.max(6, this.roomSize / 3);
      }
   }

   private record RoomHitbox(Room room, DungeonRouteMapScreen.TileRect rect) {
      private boolean contains(double mouseX, double mouseY) {
         return mouseX >= this.rect.x1() && mouseX <= this.rect.x2() && mouseY >= this.rect.y1() && mouseY <= this.rect.y2();
      }
   }

   private record TileRect(int x1, int y1, int x2, int y2) {
      private int width() {
         return this.x2 - this.x1;
      }

      private int height() {
         return this.y2 - this.y1;
      }
   }
}
