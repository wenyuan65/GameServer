package com.panda.game.tools.aoi;

import java.util.*;

// 表示一个区域
public class AOINode {

    private int x1;
    private int x2;
    private int y1;
    private int y2;

    // 十字指针
    private AOINode preX;
    private AOINode nextX;
    private AOINode preY;
    private AOINode nextY;
    private AOINode headX;
    private AOINode headY;

    private Map<Integer, AOIEntity> entityMap;

    public AOINode(){}

    public AOINode(int x1, int x2, int y1, int y2) {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
    }

    public void addEntity(AOIEntity entity) {
        if (entityMap == null) {
            entityMap = new HashMap<>();
        }

        entityMap.put(entity.getId(), entity);
    }

    public void removeEntity(AOIEntity entity) {
        if (entityMap == null) {
            return;
        }
        entityMap.remove(entity.getId());
    }

    public Collection<AOIEntity> getAllEntityList() {
        return entityMap != null ? entityMap.values() : Collections.emptyList();
    }

    public int size() {
        return entityMap != null ? entityMap.size() : 0;
    }

    public int getX1() {
        return x1;
    }

    public void setX1(int x1) {
        this.x1 = x1;
    }

    public int getX2() {
        return x2;
    }

    public void setX2(int x2) {
        this.x2 = x2;
    }

    public int getY1() {
        return y1;
    }

    public void setY1(int y1) {
        this.y1 = y1;
    }

    public int getY2() {
        return y2;
    }

    public void setY2(int y2) {
        this.y2 = y2;
    }

    public AOINode getPreX() {
        return preX;
    }

    public void setPreX(AOINode preX) {
        this.preX = preX;
    }

    public AOINode getNextX() {
        return nextX;
    }

    public void setNextX(AOINode nextX) {
        this.nextX = nextX;
    }

    public AOINode getPreY() {
        return preY;
    }

    public void setPreY(AOINode preY) {
        this.preY = preY;
    }

    public AOINode getNextY() {
        return nextY;
    }

    public void setNextY(AOINode nextY) {
        this.nextY = nextY;
    }

    public AOINode getHeadX() {
        return headX;
    }

    public void setHeadX(AOINode headX) {
        this.headX = headX;
    }

    public AOINode getHeadY() {
        return headY;
    }

    public void setHeadY(AOINode headY) {
        this.headY = headY;
    }
}
