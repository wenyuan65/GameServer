package com.panda.game.tools.aoi;

import java.util.*;

public class AOIMap {

    private TreeMap<Integer, AOINode> headXMap = new TreeMap<>();
    private TreeMap<Integer, AOINode> headYMap = new TreeMap<>();

    // 单元格的长宽
    private int w;
    private int h;

    public AOIMap(int w, int h) {
        this.w = w;
        this.h = h;
    }

    public void addEntity(AOIEntity entity) {
        // 计算当前entity在哪个格子
        int x1 = (entity.getX() / w) * w;
        int y1 = (entity.getY() / h) * h;
        int x2 = x1 + w;
        int y2 = y1 + h;

        AOINode headNodeX = headXMap.get(x1);
        AOINode headNodeY = headYMap.get(y1);
        if (headNodeX == null) { // 创建x轴的头节点
            headNodeX = initHeadNodeForX(x1, x2);
        }
        if (headNodeY == null) {
            headNodeY = initHeadNodeForY(y1, y2);
        }

        boolean found = false;
        AOINode preY = headNodeX;
        AOINode p = headNodeX.getNextY();
        while (p != null) {
            if (p.getY1() == y1) {
                found = true;
                break;
            } else if (p.getY1() < y1) {
                preY = p;
            }
            p = p.getNextY();
        }

        if (found) {
            p.addEntity(entity);
            return;
        }

        AOINode preX = headNodeY;
        p = headNodeY.getNextX();
        while (p != null) {
            if (p.getX1() < x1) {
                preX = p;
            }
            p = p.getNextX();
        }

        AOINode node = new AOINode(x1, x2, y1, y2);
        node.setHeadX(headNodeX);
        node.setHeadY(headNodeY);

        // 设置y轴指针
        node.setPreY(preY);
        node.setNextY(preY.getNextY());

        if (preY.getNextY() != null) {
            preY.getNextY().setPreY(node);
        }
        preY.setNextY(node);

        // 设置X轴指针
        node.setPreX(preX);
        node.setNextX(preX.getNextX());
        if (preX.getNextX() != null) {
            preX.getNextX().setPreX(node);
        }
        preX.setNextX(node);

        node.addEntity(entity);
    }

    public void removeEntity(AOIEntity entity) {
        AOINode node = foundNode(entity);
        if (node == null) {
            System.out.println("单位不存在2");
            return;
        }

        node.removeEntity(entity);

        if (node.size() == 0) {
            removeEmptyNode(node);
        }
    }

    private AOINode foundNode(AOIEntity entity) {
        // 计算当前entity在哪个格子
        int x1 = (entity.getX() / w) * w;
        int y1 = (entity.getY() / h) * h;

        AOINode headNodeX = headXMap.get(x1);
        AOINode headNodeY = headYMap.get(y1);
        if (headNodeX == null || headNodeY == null) {
            System.out.println("单位不存在1");
            return null;
        }

        AOINode node = null;
        AOINode p = headNodeX.getNextY();
        while (p != null) {
            if (p.getY1() == y1) {
                node = p;
                break;
            }
            p = p.getNextY();
        }

        return node;
    }

    public List<AOIEntity> getAllNeighbors(AOIEntity entity, int gridNum) {
        AOINode node = foundNode(entity);
        if (node == null) {
            System.out.println("单位不存在2");
            return Collections.emptyList();
        }
        if (gridNum == 0) {
            return new ArrayList<>(node.getAllEntityList());
        }

        // 计算当前entity在哪个格子
        int x = (entity.getX() / w) * w;
        int y = (entity.getY() / h) * h;
        int x1 = x - w * gridNum;
        int x2 = x + w * gridNum;
        int y1 = y - h * gridNum;
        int y2 = y + h * gridNum;

//        List<AOIEntity> result = new LinkedList<>();
        List<AOIEntity> result = new ArrayList<>(1024);
        List<AOINode> candidateHeadNodeList = new ArrayList<>();
        AOINode headX = node.getHeadX();
        candidateHeadNodeList.add(headX);

        AOINode p = headX.getPreX();
        while (p != null && p.getX1() >= x1) {
            candidateHeadNodeList.add(p);
            p = p.getPreX();
        }
        p = headX.getNextX();
        while (p != null && p.getX1() <= x2) {
            candidateHeadNodeList.add(p);
            p = p.getNextX();
        }

        for (AOINode candidateHeadXNode : candidateHeadNodeList) {
            AOINode nextY = candidateHeadXNode.getNextY();
            while (nextY != null) {
                if (nextY.getY1() >= y1 && nextY.getY1() <= y2) {
                    result.addAll(nextY.getAllEntityList());
                }

                nextY = nextY.getNextY();
            }
        }

        return result;
    }

    private void removeEmptyNode(AOINode node) {
        AOINode preNode = node.getPreX();
        AOINode nextNode = node.getNextX();
        preNode.setNextX(nextNode);
        if (nextNode != null) {
            nextNode.setPreX(preNode);
        }

        preNode = node.getPreY();
        nextNode = node.getNextY();
        preNode.setNextY(nextNode);
        if (nextNode != null) {
            nextNode.setPreY(preNode);
        }

        // 删除头节点
        AOINode headX = node.getHeadX();
        AOINode headY = node.getHeadY();
        if (headX.getNextY() == null) {
            preNode = headX.getPreX();
            nextNode = headX.getNextX();
            if (preNode != null) {
                preNode.setNextX(nextNode);
            }
            if (nextNode != null) {
                nextNode.setPreX(preNode);
            }

            headXMap.remove(node.getX1());
        }
        if (headY.getNextX() == null) {
            preNode = headY.getPreY();
            nextNode = headY.getNextY();
            if (preNode != null) {
                preNode.setNextY(nextNode);
            }
            if (nextNode != null) {
                nextNode.setPreY(preNode);
            }

            headYMap.remove(node.getY1());
        }

        node.setPreX(null);
        node.setPreY(null);
        node.setNextX(null);
        node.setNextY(null);
        node.setHeadX(null);
        node.setHeadY(null);
    }

    private AOINode initHeadNodeForX(int x1, int x2) {
        AOINode headNodeX = new AOINode(); // 头节点
        headNodeX.setX1(x1);
        headNodeX.setX2(x2);

        Map.Entry<Integer, AOINode> preEntry = headXMap.lowerEntry(x1);
        if (preEntry != null) {
            AOINode preNode = preEntry.getValue();
            AOINode nextNode = preNode.getNextX();

            headNodeX.setPreX(preNode);
            headNodeX.setNextX(nextNode);

            if (nextNode != null) {
                nextNode.setPreX(headNodeX);
            }
            preNode.setNextX(headNodeX);
        } else {
            Map.Entry<Integer, AOINode> nextEntry = headXMap.higherEntry(x1);
            if (nextEntry != null) {
                AOINode nextNode = nextEntry.getValue();
                AOINode preNode = nextNode.getPreX();

                headNodeX.setNextX(nextNode);
                headNodeX.setPreX(preNode);

                if (preNode != null) {
                    preNode.setNextX(headNodeX);
                }
                nextNode.setPreX(headNodeX);
            }
        }

        headXMap.put(x1, headNodeX);
        return headNodeX;
    }

    private AOINode initHeadNodeForY(int y1, int y2) {
        AOINode headNodeY = new AOINode(); // 头节点
        headNodeY.setY1(y1);
        headNodeY.setY2(y2);

        Map.Entry<Integer, AOINode> preEntry = headYMap.lowerEntry(y1);
        if (preEntry != null) {
            AOINode preNode = preEntry.getValue();
            AOINode nextNode = preNode.getNextY();

            headNodeY.setPreY(preNode);
            headNodeY.setNextY(nextNode);

            if (nextNode != null) {
                nextNode.setPreY(headNodeY);
            }
            preNode.setNextY(headNodeY);
        } else {
            Map.Entry<Integer, AOINode> nextEntry = headYMap.higherEntry(y1);
            if (nextEntry != null) {
                AOINode nextNode = nextEntry.getValue();
                AOINode preNode = nextNode.getPreY();

                headNodeY.setNextY(nextNode);
                headNodeY.setPreY(preNode);

                nextNode.setPreY(headNodeY);
                if (preNode != null) {
                    preNode.setNextY(headNodeY);
                }
            }
        }

        headYMap.put(y1, headNodeY);

        return headNodeY;
    }

    public static void main(String[] args) {
        int w = 50;
        int minX = 0;
        int maxX = 1000;
        int minY = 0;
        int maxY = 1000;
        int N = 10000;
        Random rand = new Random();
        AOIMap map = new AOIMap(w, w);

        int sid = 0;
        long start = System.currentTimeMillis();
        Map<Integer, AOIEntity> entityMap = new HashMap<>();
        for (int id = 0; id < N; id++) {
            int x = rand.nextInt(maxX - minX) + minX;
            int y = rand.nextInt(maxY - minY) + minY;
            if (rand.nextBoolean()) { // 一半的点集中在(50, 100)的位置
                x = rand.nextInt(w) + w;
                y = rand.nextInt(w) + w;

                sid = id;
            }

            AOIEntity entity = new AOIEntity();
            entity.setId(id + 1);
            entity.setX(x);
            entity.setY(y);
            map.addEntity(entity);

            entityMap.put(entity.getId(), entity);
        }
        long end = System.currentTimeMillis();
        System.out.println("1.cost time:" + (end - start) + " ms");

        start = System.currentTimeMillis();
        int sum = 0;
        for (int i = 0; i < 100; i++) {
            List<AOIEntity> list = map.getAllNeighbors(entityMap.get(sid + 1), 1);
            sum += list.size();
        }
        System.out.println(sum);
        end = System.currentTimeMillis();
        System.out.println("2.cost time:" + (end - start) + " ms");

        start = System.currentTimeMillis();
        for (Map.Entry<Integer, AOIEntity> entry : entityMap.entrySet()) {
            map.removeEntity(entry.getValue());
        }
        end = System.currentTimeMillis();
        System.out.println("3.cost time:" + (end - start) + " ms");
    }

}
