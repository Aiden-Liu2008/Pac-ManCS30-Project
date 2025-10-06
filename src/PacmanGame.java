import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// 枚举定义
enum GhostState {
    ALIVE, VULNERABLE, DEAD, COMING_ALIVE
}

// 迷宫生成器
class MazeGenerator {
    // 将常量改为public
    public static final int WALL = 1;
    public static final int PATH = 0;
    public static final int PELLET = 2;
    public static final int POWER_PELLET = 3;
    
    private int width, height;
    private int[][] maze;
    private Random random;
    
    public MazeGenerator(int width, int height) {
        this.width = width;
        this.height = height;
        this.maze = new int[width][height];
        this.random = new Random();
    }
    
    // 生成更复杂的经典Pac-Man风格迷宫
    public int[][] generateClassicMaze() {
        // 初始化所有为墙
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                maze[x][y] = WALL;
            }
        }
        
        // 创建更复杂的主通道系统
        createComplexMainChannels();
        
        // 创建更多房间和走廊
        createComplexRoomsAndCorridors();
        
        // 添加额外的迷宫元素
        createAdditionalMazeFeatures();
        
        // 确保边界是墙
        createBoundaryWalls();
        
        // 放置豆子
        placePellets();
        
        return maze;
    }
    
    private void createComplexMainChannels() {
        // 创建更多水平主通道
        for (int x = 3; x < width - 3; x++) {
            if (height / 6 < height) maze[x][height / 6] = PATH;
            if (height / 3 < height) maze[x][height / 3] = PATH;
            if (height / 2 < height) maze[x][height / 2] = PATH;
            if (2 * height / 3 < height) maze[x][2 * height / 3] = PATH;
            if (5 * height / 6 < height) maze[x][5 * height / 6] = PATH;
        }
        
        // 创建更多垂直主通道
        for (int y = 3; y < height - 3; y++) {
            if (width / 6 < width) maze[width / 6][y] = PATH;
            if (width / 3 < width) maze[width / 3][y] = PATH;
            if (width / 2 < width) maze[width / 2][y] = PATH;
            if (2 * width / 3 < width) maze[2 * width / 3][y] = PATH;
            if (5 * width / 6 < width) maze[5 * width / 6][y] = PATH;
        }
        
        // 创建对角线通道增加复杂度
        createDiagonalPaths();
    }
    
    private void createDiagonalPaths() {
        // 添加一些对角线路径增加迷宫复杂度
        for (int i = 0; i < width / 4; i++) {
            int x = 5 + i * 4;
            int y = 5 + i * 3;
            if (x < width - 5 && y < height - 5) {
                createDiagonalCorridor(x, y, 8, true);
            }
            
            x = width - 10 - i * 4;
            y = 5 + i * 3;
            if (x > 5 && y < height - 5) {
                createDiagonalCorridor(x, y, 8, false);
            }
        }
    }
    
    private void createDiagonalCorridor(int startX, int startY, int length, boolean rightDown) {
        int x = startX, y = startY;
        
        for (int i = 0; i < length; i++) {
            if (x >= 2 && x < width - 2 && y >= 2 && y < height - 2) {
                // 创建3x3的走廊区域
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        if (x + dx >= 0 && x + dx < width && y + dy >= 0 && y + dy < height) {
                            maze[x + dx][y + dy] = PATH;
                        }
                    }
                }
                
                if (rightDown) {
                    x++;
                    y++;
                } else {
                    x--;
                    y++;
                }
            }
        }
    }
    
    private void createComplexRoomsAndCorridors() {
        // 创建更多房间
        createRoom(2, 2, width / 3 - 4, height / 3 - 4);
        createRoom(width / 3 + 2, 2, 2 * width / 3 - 4, height / 3 - 4);
        createRoom(2 * width / 3 + 2, 2, width - 4, height / 3 - 4);
        
        createRoom(2, height / 3 + 2, width / 3 - 4, 2 * height / 3 - 4);
        createRoom(2 * width / 3 + 2, height / 3 + 2, width - 4, 2 * height / 3 - 4);
        
        createRoom(2, 2 * height / 3 + 2, width / 3 - 4, height - 4);
        createRoom(width / 3 + 2, 2 * height / 3 + 2, 2 * width / 3 - 4, height - 4);
        createRoom(2 * width / 3 + 2, 2 * height / 3 + 2, width - 4, height - 4);
        
        // 创建更多连接走廊
        createComplexCorridors();
        
        // 创建中心区域
        createCenterArea();
    }
    
    private void createCenterArea() {
        // 创建复杂的中心区域
        int centerX = width / 2;
        int centerY = height / 2;
        int size = Math.min(width, height) / 3;
        
        // 创建中心十字
        for (int x = centerX - size / 2; x <= centerX + size / 2; x++) {
            if (x >= 0 && x < width && centerY >= 0 && centerY < height) {
                maze[x][centerY] = PATH;
            }
        }
        for (int y = centerY - size / 2; y <= centerY + size / 2; y++) {
            if (centerX >= 0 && centerX < width && y >= 0 && y < height) {
                maze[centerX][y] = PATH;
            }
        }
        
        // 在中心创建小房间
        int roomSize = size / 2;
        for (int x = centerX - roomSize / 2; x <= centerX + roomSize / 2; x++) {
            for (int y = centerY - roomSize / 2; y <= centerY + roomSize / 2; y++) {
                if (x >= 0 && x < width && y >= 0 && y < height) {
                    if (x == centerX - roomSize / 2 || x == centerX + roomSize / 2 || 
                        y == centerY - roomSize / 2 || y == centerY + roomSize / 2) {
                        maze[x][y] = WALL; // 房间边界
                    } else {
                        maze[x][y] = PATH; // 房间内部
                    }
                }
            }
        }
    }
    
    private void createRoom(int startX, int startY, int endX, int endY) {
        for (int x = startX; x < endX && x < width; x++) {
            for (int y = startY; y < endY && y < height; y++) {
                if (x == startX || x == endX - 1 || y == startY || y == endY - 1) {
                    maze[x][y] = WALL; // 房间边界
                } else {
                    maze[x][y] = PATH; // 房间内部
                }
            }
        }
        
        // 在房间的多个位置开门
        createDoors(startX, startY, endX, endY);
    }
    
    private void createDoors(int startX, int startY, int endX, int endY) {
        // 在房间的每个边开1-2个门
        int doors = random.nextInt(2) + 1;
        
        // 上边
        for (int i = 0; i < doors; i++) {
            int doorX = startX + random.nextInt(endX - startX - 2) + 1;
            if (doorX < width && startY < height) {
                maze[doorX][startY] = PATH;
                if (doorX + 1 < width) maze[doorX + 1][startY] = PATH;
            }
        }
        
        // 下边
        for (int i = 0; i < doors; i++) {
            int doorX = startX + random.nextInt(endX - startX - 2) + 1;
            if (doorX < width && endY - 1 < height) {
                maze[doorX][endY - 1] = PATH;
                if (doorX + 1 < width) maze[doorX + 1][endY - 1] = PATH;
            }
        }
        
        // 左边
        for (int i = 0; i < doors; i++) {
            int doorY = startY + random.nextInt(endY - startY - 2) + 1;
            if (startX < width && doorY < height) {
                maze[startX][doorY] = PATH;
                if (doorY + 1 < height) maze[startX][doorY + 1] = PATH;
            }
        }
        
        // 右边
        for (int i = 0; i < doors; i++) {
            int doorY = startY + random.nextInt(endY - startY - 2) + 1;
            if (endX - 1 < width && doorY < height) {
                maze[endX - 1][doorY] = PATH;
                if (doorY + 1 < height) maze[endX - 1][doorY + 1] = PATH;
            }
        }
    }
    
    private void createComplexCorridors() {
        // 创建更多随机走廊
        for (int i = 0; i < width * height / 20; i++) { // 增加走廊数量
            int x = random.nextInt(width - 4) + 2;
            int y = random.nextInt(height - 4) + 2;
            
            if (maze[x][y] == WALL) {
                // 随机选择走廊方向和长度
                int length = random.nextInt(8) + 4; // 增加走廊长度
                int direction = random.nextInt(4);
                
                createCorridor(x, y, length, direction);
            }
        }
        
        // 创建环形走廊
        createCircularCorridors();
    }
    
    private void createCircularCorridors() {
        // 创建一些环形走廊增加复杂度
        for (int i = 0; i < 3; i++) {
            int centerX = random.nextInt(width - 10) + 5;
            int centerY = random.nextInt(height - 10) + 5;
            int radius = random.nextInt(5) + 3;
            
            createCircularPath(centerX, centerY, radius);
        }
    }
    
    private void createCircularPath(int centerX, int centerY, int radius) {
        // 创建简单的圆形路径
        for (int angle = 0; angle < 360; angle += 15) {
            double rad = Math.toRadians(angle);
            int x = (int)(centerX + radius * Math.cos(rad));
            int y = (int)(centerY + radius * Math.sin(rad));
            
            if (x >= 1 && x < width - 1 && y >= 1 && y < height - 1) {
                maze[x][y] = PATH;
                
                // 创建路径宽度
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        if (x + dx >= 0 && x + dx < width && y + dy >= 0 && y + dy < height) {
                            maze[x + dx][y + dy] = PATH;
                        }
                    }
                }
            }
        }
    }
    
    private void createCorridor(int startX, int startY, int length, int direction) {
        int x = startX, y = startY;
        
        for (int i = 0; i < length; i++) {
            if (x >= 1 && x < width - 1 && y >= 1 && y < height - 1) {
                // 创建更宽的走廊
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        if (x + dx >= 0 && x + dx < width && y + dy >= 0 && y + dy < height) {
                            maze[x + dx][y + dy] = PATH;
                        }
                    }
                }
                
                switch (direction) {
                    case 0: x++; break; // 右
                    case 1: x--; break; // 左
                    case 2: y++; break; // 下
                    case 3: y--; break; // 上
                }
            }
        }
    }
    
    private void createAdditionalMazeFeatures() {
        // 添加迷宫死胡同增加挑战性
        createDeadEnds();
        
        // 添加迷宫陷阱区域
        createTrapAreas();
    }
    
    private void createDeadEnds() {
        // 创建一些死胡同
        for (int i = 0; i < width * height / 30; i++) {
            int x = random.nextInt(width - 4) + 2;
            int y = random.nextInt(height - 4) + 2;
            
            if (maze[x][y] == PATH) {
                // 检查是否可以作为死胡同起点
                int openSides = 0;
                if (x > 0 && maze[x-1][y] == PATH) openSides++;
                if (x < width - 1 && maze[x+1][y] == PATH) openSides++;
                if (y > 0 && maze[x][y-1] == PATH) openSides++;
                if (y < height - 1 && maze[x][y+1] == PATH) openSides++;
                
                if (openSides == 1) { // 只有一个开口，适合作为死胡同起点
                    createDeadEnd(x, y, random.nextInt(3) + 2);
                }
            }
        }
    }
    
    private void createDeadEnd(int startX, int startY, int length) {
        int x = startX, y = startY;
        int lastX = x, lastY = y;
        
        // 找到唯一的开口方向
        int dirX = 0, dirY = 0;
        if (x > 0 && maze[x-1][y] == PATH) { dirX = -1; dirY = 0; }
        else if (x < width - 1 && maze[x+1][y] == PATH) { dirX = 1; dirY = 0; }
        else if (y > 0 && maze[x][y-1] == PATH) { dirX = 0; dirY = -1; }
        else if (y < height - 1 && maze[x][y+1] == PATH) { dirX = 0; dirY = 1; }
        
        // 向相反方向创建死胡同
        dirX = -dirX;
        dirY = -dirY;
        
        for (int i = 0; i < length; i++) {
            int newX = x + dirX;
            int newY = y + dirY;
            
            if (newX >= 1 && newX < width - 1 && newY >= 1 && newY < height - 1) {
                maze[newX][newY] = PATH;
                lastX = x;
                lastY = y;
                x = newX;
                y = newY;
            } else {
                break;
            }
        }
        
        // 在死胡同尽头放置能量豆
        if (x != startX || y != startY) {
            maze[x][y] = POWER_PELLET;
        }
    }
    
    private void createTrapAreas() {
        // 创建一些陷阱区域，这些区域有多个入口但中心是墙
        for (int i = 0; i < 5; i++) {
            int centerX = random.nextInt(width - 8) + 4;
            int centerY = random.nextInt(height - 8) + 4;
            
            // 创建环形陷阱
            for (int dx = -3; dx <= 3; dx++) {
                for (int dy = -3; dy <= 3; dy++) {
                    if (Math.abs(dx) == 3 || Math.abs(dy) == 3) {
                        int x = centerX + dx;
                        int y = centerY + dy;
                        if (x >= 0 && x < width && y >= 0 && y < height) {
                            maze[x][y] = PATH;
                        }
                    } else if (Math.abs(dx) <= 2 && Math.abs(dy) <= 2) {
                        int x = centerX + dx;
                        int y = centerY + dy;
                        if (x >= 0 && x < width && y >= 0 && y < height) {
                            maze[x][y] = WALL;
                        }
                    }
                }
            }
            
            // 在陷阱中开几个入口
            for (int j = 0; j < 3; j++) {
                int side = random.nextInt(4);
                int entranceX = centerX, entranceY = centerY;
                
                switch (side) {
                    case 0: entranceX -= 3; break; // 左
                    case 1: entranceX += 3; break; // 右
                    case 2: entranceY -= 3; break; // 上
                    case 3: entranceY += 3; break; // 下
                }
                
                if (entranceX >= 0 && entranceX < width && entranceY >= 0 && entranceY < height) {
                    maze[entranceX][entranceY] = PATH;
                }
            }
        }
    }
    
    private void createBoundaryWalls() {
        for (int x = 0; x < width; x++) {
            maze[x][0] = WALL;
            maze[x][height - 1] = WALL;
        }
        for (int y = 0; y < height; y++) {
            maze[0][y] = WALL;
            maze[width - 1][y] = WALL;
        }
    }
    
    private void placePellets() {
        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {
                if (maze[x][y] == PATH) {
                    // 在交叉路口放置能量豆
                    if (isIntersection(x, y)) {
                        maze[x][y] = POWER_PELLET;
                    } else {
                        maze[x][y] = PELLET;
                    }
                }
            }
        }
    }
    
    private boolean isIntersection(int x, int y) {
        int pathCount = 0;
        if (x > 0 && maze[x-1][y] != WALL) pathCount++;
        if (x < width - 1 && maze[x+1][y] != WALL) pathCount++;
        if (y > 0 && maze[x][y-1] != WALL) pathCount++;
        if (y < height - 1 && maze[x][y+1] != WALL) pathCount++;
        
        return pathCount >= 3;
    }
}

// 游戏资源管理
class Asset {
    // 动画帧列表
    public List<BufferedImage> pacManFrames = new ArrayList<>();
    public List<BufferedImage> pacManDeathFrames = new ArrayList<>();
    public List<BufferedImage> vulnerableGhostFrames = new ArrayList<>();
    public List<BufferedImage> ephemeralVulnerableGhostFrames = new ArrayList<>();
    public List<BufferedImage> redGhostFrames = new ArrayList<>();
    public List<BufferedImage> pinkGhostFrames = new ArrayList<>();
    public List<BufferedImage> cyanGhostFrames = new ArrayList<>();
    public List<BufferedImage> orangeGhostFrames = new ArrayList<>();
    public List<BufferedImage> ghostEyesFrames = new ArrayList<>();
    public List<BufferedImage> pelletFrames = new ArrayList<>();
    public List<BufferedImage> powerPelletFrames = new ArrayList<>();
    
    public BufferedImage background;
    
    // 墙的颜色
    public Color wallColor = new Color(0, 0, 139); // 深蓝色
    public Color wallHighlightColor = new Color(30, 144, 255); // 道奇蓝高光
    
    // 常量
    public static final int VIRTUAL_WIDTH = 896;
    public static final int VIRTUAL_HEIGHT = 1056;
    public static final int MAP_HEIGHT = 992;
    public static final int SPRITE_SIZE = 64;
    public static final int PACMAN_FRAMES = 10;
    public static final int GHOST_FRAMES = 10;
    public static final int PELLET_FRAMES = 25;
    public static final int POWER_PELLET_FRAMES = 15;
    public static final int GHOST_EYES_FRAMES = 4;
    public static final int TILE_SIZE = 16;
    
    public void loadAssets() {
        try {
            createEnhancedGraphics();
        } catch (Exception e) {
            System.err.println("Error loading assets: " + e.getMessage());
            createEnhancedGraphics();
        }
    }
    
    private void createEnhancedGraphics() {
        // 创建增强的Pacman动画帧
        createPacmanAnimation();
        
        // 创建增强的幽灵动画帧
        createGhostAnimations();
        
        // 创建豆子动画
        createPelletAnimations();
        
        // 创建增强的背景
        createEnhancedBackground();
    }
    
    private void createPacmanAnimation() {
        // Pacman动画 - 嘴巴开合
        for (int i = 0; i < PACMAN_FRAMES; i++) {
            BufferedImage frame = new BufferedImage(SPRITE_SIZE, SPRITE_SIZE, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = frame.createGraphics();
            
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // 绘制Pacman身体
            g2d.setColor(Color.YELLOW);
            
            // 计算嘴巴开合角度 (30° 到 330°)
            int startAngle = 20 + (i * 25);
            int arcAngle = 320 - (i * 50);
            
            g2d.fillArc(2, 2, SPRITE_SIZE - 4, SPRITE_SIZE - 4, startAngle, arcAngle);
            
            // 添加高光
            g2d.setColor(new Color(255, 255, 200));
            g2d.fillOval(SPRITE_SIZE/3, SPRITE_SIZE/4, 4, 4);
            
            g2d.dispose();
            pacManFrames.add(frame);
        }
        
        // 死亡动画
        for (int i = 0; i < PACMAN_FRAMES; i++) {
            BufferedImage frame = new BufferedImage(SPRITE_SIZE, SPRITE_SIZE, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = frame.createGraphics();
            
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // 死亡动画 - 逐渐消失
            int alpha = 255 - (i * 25);
            if (alpha < 0) alpha = 0;
            
            g2d.setColor(new Color(255, 255, 0, alpha));
            g2d.fillOval(2, 2, SPRITE_SIZE - 4, SPRITE_SIZE - 4);
            
            g2d.dispose();
            pacManDeathFrames.add(frame);
        }
    }
    
    private void createGhostAnimations() {
        // 红色幽灵 - Blinky
        createGhostAnimation(redGhostFrames, Color.RED, "Blinky");
        
        // 粉色幽灵 - Pinky
        createGhostAnimation(pinkGhostFrames, Color.PINK, "Pinky");
        
        // 青色幽灵 - Inky
        createGhostAnimation(cyanGhostFrames, Color.CYAN, "Inky");
        
        // 橙色幽灵 - Clyde
        createGhostAnimation(orangeGhostFrames, Color.ORANGE, "Clyde");
        
        // 脆弱状态幽灵
        createVulnerableGhostAnimation();
        
        // 幽灵眼睛
        createGhostEyes();
    }
    
    private void createGhostAnimation(List<BufferedImage> frames, Color color, String name) {
        for (int i = 0; i < GHOST_FRAMES; i++) {
            BufferedImage frame = new BufferedImage(SPRITE_SIZE, SPRITE_SIZE, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = frame.createGraphics();
            
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // 幽灵身体
            g2d.setColor(color);
            g2d.fillRoundRect(4, 4, SPRITE_SIZE - 8, SPRITE_SIZE - 4, 20, 20);
            
            // 底部波浪效果
            g2d.fillRect(4, SPRITE_SIZE - 12, SPRITE_SIZE - 8, 8);
            
            // 底部锯齿
            int waveHeight = 4;
            for (int x = 6; x < SPRITE_SIZE - 6; x += 8) {
                int[] xPoints = {x, x+4, x+8};
                int[] yPoints = {SPRITE_SIZE - 12, SPRITE_SIZE - 12 - waveHeight, SPRITE_SIZE - 12};
                g2d.fillPolygon(xPoints, yPoints, 3);
            }
            
            // 眼睛
            g2d.setColor(Color.WHITE);
            g2d.fillOval(12, 12, 12, 12);
            g2d.fillOval(SPRITE_SIZE - 24, 12, 12, 12);
            
            // 瞳孔
            g2d.setColor(Color.BLUE);
            int pupilOffset = (i % 3) - 1; // 让瞳孔轻微移动
            g2d.fillOval(14 + pupilOffset, 14, 6, 6);
            g2d.fillOval(SPRITE_SIZE - 22 + pupilOffset, 14, 6, 6);
            
            // 添加名字标签
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 8));
            g2d.drawString(name, SPRITE_SIZE/2 - 12, SPRITE_SIZE - 4);
            
            g2d.dispose();
            frames.add(frame);
        }
    }
    
    private void createVulnerableGhostAnimation() {
        for (int i = 0; i < GHOST_FRAMES; i++) {
            BufferedImage frame = new BufferedImage(SPRITE_SIZE, SPRITE_SIZE, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = frame.createGraphics();
            
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // 脆弱状态 - 蓝色闪烁
            Color vulnerableColor = (i % 4 < 2) ? Color.BLUE : Color.WHITE;
            g2d.setColor(vulnerableColor);
            g2d.fillRoundRect(4, 4, SPRITE_SIZE - 8, SPRITE_SIZE - 4, 20, 20);
            
            // 底部波浪效果
            g2d.fillRect(4, SPRITE_SIZE - 12, SPRITE_SIZE - 8, 8);
            
            // 底部锯齿
            int waveHeight = 4;
            for (int x = 6; x < SPRITE_SIZE - 6; x += 8) {
                int[] xPoints = {x, x+4, x+8};
                int[] yPoints = {SPRITE_SIZE - 12, SPRITE_SIZE - 12 - waveHeight, SPRITE_SIZE - 12};
                g2d.fillPolygon(xPoints, yPoints, 3);
            }
            
            // 害怕的眼睛
            g2d.setColor(Color.WHITE);
            g2d.fillOval(12, 12, 16, 16);
            g2d.fillOval(SPRITE_SIZE - 28, 12, 16, 16);
            
            g2d.setColor(Color.BLACK);
            g2d.fillOval(16, 18, 8, 8);
            g2d.fillOval(SPRITE_SIZE - 24, 18, 8, 8);
            
            g2d.dispose();
            vulnerableGhostFrames.add(frame);
            ephemeralVulnerableGhostFrames.add(frame); // 简化版本
        }
    }
    
    private void createGhostEyes() {
        for (int i = 0; i < GHOST_EYES_FRAMES; i++) {
            BufferedImage frame = new BufferedImage(SPRITE_SIZE, SPRITE_SIZE, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = frame.createGraphics();
            
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // 眼睛
            g2d.setColor(Color.WHITE);
            g2d.fillOval(12, 12, 12, 12);
            g2d.fillOval(SPRITE_SIZE - 24, 12, 12, 12);
            
            // 瞳孔 - 根据方向变化
            int pupilX = 0, pupilY = 0;
            switch (i) {
                case 0: pupilX = 2; pupilY = 0; break; // 右
                case 1: pupilX = -2; pupilY = 0; break; // 左
                case 2: pupilX = 0; pupilY = 2; break; // 下
                case 3: pupilX = 0; pupilY = -2; break; // 上
            }
            
            g2d.setColor(Color.BLUE);
            g2d.fillOval(14 + pupilX, 14 + pupilY, 6, 6);
            g2d.fillOval(SPRITE_SIZE - 22 + pupilX, 14 + pupilY, 6, 6);
            
            g2d.dispose();
            ghostEyesFrames.add(frame);
        }
    }
    
    private void createPelletAnimations() {
        // 普通豆子
        for (int i = 0; i < PELLET_FRAMES; i++) {
            BufferedImage frame = new BufferedImage(SPRITE_SIZE, SPRITE_SIZE, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = frame.createGraphics();
            
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // 闪烁的豆子
            int size = 6 + (i % 3);
            int x = (SPRITE_SIZE - size) / 2;
            int y = (SPRITE_SIZE - size) / 2;
            
            g2d.setColor(Color.WHITE);
            g2d.fillOval(x, y, size, size);
            
            g2d.dispose();
            pelletFrames.add(frame);
        }
        
        // 能量豆
        for (int i = 0; i < POWER_PELLET_FRAMES; i++) {
            BufferedImage frame = new BufferedImage(SPRITE_SIZE, SPRITE_SIZE, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = frame.createGraphics();
            
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // 闪烁的能量豆
            int size = 12;
            int pulse = (i % 8) / 2;
            size += pulse * 2;
            
            int x = (SPRITE_SIZE - size) / 2;
            int y = (SPRITE_SIZE - size) / 2;
            
            g2d.setColor(Color.WHITE);
            g2d.fillOval(x, y, size, size);
            
            // 添加光晕效果
            g2d.setColor(new Color(255, 255, 255, 100));
            g2d.fillOval(x - 2, y - 2, size + 4, size + 4);
            
            g2d.dispose();
            powerPelletFrames.add(frame);
        }
    }
    
    private void createEnhancedBackground() {
        background = new BufferedImage(VIRTUAL_WIDTH, MAP_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = background.createGraphics();
        
        // 深蓝色背景
        g2d.setColor(new Color(0, 0, 50));
        g2d.fillRect(0, 0, VIRTUAL_WIDTH, MAP_HEIGHT);
        
        // 添加星空效果
        g2d.setColor(new Color(100, 100, 200, 50));
        for (int i = 0; i < 100; i++) {
            int x = (int)(Math.random() * VIRTUAL_WIDTH);
            int y = (int)(Math.random() * MAP_HEIGHT);
            int size = (int)(Math.random() * 3) + 1;
            g2d.fillOval(x, y, size, size);
        }
        
        g2d.dispose();
    }
}

// Pacman类
class Pacman {
    public int xTile, yTile;
    public int life;
    public double rotationAngle;
    public boolean isPowerUp;
    public boolean isDead;
    
    // 移动逻辑
    public int lastStep;
    public int requestKey;
    public int dx, dy;
    
    public Pacman() {
        this(0, 0);
    }
    
    public Pacman(int x, int y) {
        this.xTile = x;
        this.yTile = y;
        this.life = 3;
        this.rotationAngle = 0;
        this.isPowerUp = false;
        this.isDead = false;
        this.lastStep = 0;
        this.requestKey = 0;
        this.dx = 0;
        this.dy = 0;
    }
    
    public int getPixelX() {
        return xTile * Asset.TILE_SIZE;
    }
    
    public int getPixelY() {
        return yTile * Asset.TILE_SIZE;
    }
    
    public Rectangle getBounds() {
        int size = Asset.SPRITE_SIZE;
        return new Rectangle(getPixelX(), getPixelY(), size, size);
    }
}

// Ghost类
class Ghost {
    public int xTile, yTile;
    public GhostState ghostState;
    public List<BufferedImage> textureFrames;
    public int currentDirectionX, currentDirectionY;
    
    public Ghost(int x, int y, List<BufferedImage> textureFrames) {
        this.xTile = x;
        this.yTile = y;
        this.ghostState = GhostState.ALIVE;
        this.textureFrames = textureFrames;
        this.currentDirectionX = 0;
        this.currentDirectionY = 0;
    }
    
    public int getPixelX() {
        return xTile * Asset.TILE_SIZE;
    }
    
    public int getPixelY() {
        return yTile * Asset.TILE_SIZE;
    }
    
    public Rectangle getBounds() {
        int size = Asset.SPRITE_SIZE;
        return new Rectangle(getPixelX(), getPixelY(), size, size);
    }
}

// Pellet类
class Pellet {
    public int xTile, yTile;
    public boolean eaten;
    
    public Pellet(int x, int y) {
        this.xTile = x;
        this.yTile = y;
        this.eaten = false;
    }
    
    public int getPixelX() {
        return xTile * Asset.TILE_SIZE + Asset.TILE_SIZE / 2 - 4;
    }
    
    public int getPixelY() {
        return yTile * Asset.TILE_SIZE + Asset.TILE_SIZE / 2 - 4;
    }
    
    public Rectangle getBounds() {
        return new Rectangle(getPixelX(), getPixelY(), 8, 8);
    }
}

// PowerPellet类
class PowerPellet {
    public int xTile, yTile;
    public boolean eaten;
    
    public PowerPellet(int x, int y) {
        this.xTile = x;
        this.yTile = y;
        this.eaten = false;
    }
    
    public int getPixelX() {
        return xTile * Asset.TILE_SIZE + Asset.TILE_SIZE / 2 - 6;
    }
    
    public int getPixelY() {
        return yTile * Asset.TILE_SIZE + Asset.TILE_SIZE / 2 - 6;
    }
    
    public Rectangle getBounds() {
        return new Rectangle(getPixelX(), getPixelY(), 12, 12);
    }
}

// 游戏状态管理
class GameState {
    public Pacman pacman;
    public List<Ghost> ghosts = new ArrayList<>();
    public List<Pellet> pellets = new ArrayList<>();
    public List<PowerPellet> powerPellets = new ArrayList<>();
    public boolean[][] wall;
    
    public boolean shouldResetGame = false;
    
    // 动画帧计数器
    public int ghostCharacterFrame = 0;
    public int ghostEyeFrame = 0;
    public int pelletCharacterFrame = 0;
    public int powerPelletFrame = 0;
    public int pacmanCharacterFrame = 0;
    public int pacmanDeathAnimationFrame = 0;
    
    // 迷宫生成器
    private MazeGenerator mazeGenerator;
    
    public GameState() {
        int widthTiles = Asset.VIRTUAL_WIDTH / Asset.TILE_SIZE;
        int heightTiles = Asset.MAP_HEIGHT / Asset.TILE_SIZE;
        wall = new boolean[widthTiles][heightTiles];
        
        mazeGenerator = new MazeGenerator(widthTiles, heightTiles);
        generateMaze();
    }
    
    private void generateMaze() {
        int[][] mazeData = mazeGenerator.generateClassicMaze();
        
        for (int i = 0; i < mazeData.length; i++) {
            for (int j = 0; j < mazeData[0].length; j++) {
                // 使用MazeGenerator的公共常量
                wall[i][j] = (mazeData[i][j] == MazeGenerator.WALL);
                
                // 放置普通豆子
                if (mazeData[i][j] == MazeGenerator.PELLET) {
                    pellets.add(new Pellet(i, j));
                }
                // 放置能量豆
                else if (mazeData[i][j] == MazeGenerator.POWER_PELLET) {
                    powerPellets.add(new PowerPellet(i, j));
                }
            }
        }
        
        // 确保起始位置是空的
        if (18 < wall.length && 20 < wall[0].length) {
            wall[18][20] = false;
        }
        if (19 < wall.length && 20 < wall[0].length) {
            wall[19][20] = false;
        }
    }
    
    public boolean canMoveTo(int xTile, int yTile) {
        if (xTile < 0 || xTile >= wall.length || yTile < 0 || yTile >= wall[0].length) {
            return false;
        }
        
        int bottomTile = yTile + (Asset.SPRITE_SIZE / Asset.TILE_SIZE) - 1;
        int rightTile = xTile + (Asset.SPRITE_SIZE / Asset.TILE_SIZE) - 1;
        
        // 检查四个角
        if (bottomTile >= wall[0].length || rightTile >= wall.length) {
            return false;
        }
        
        return !(wall[xTile][yTile] || wall[xTile][bottomTile] ||
                wall[rightTile][bottomTile] || wall[rightTile][yTile]);
    }
}

// 主游戏类
public class PacmanGame extends JPanel implements ActionListener, KeyListener {
    private Asset asset;
    private GameState game;
    private Timer gameTimer;
    private float globalTimer = 0.0f;
    private float pelletEnabledTime = 0;
    
    // 图形效果 - 将网格颜色改为白色
    private boolean showGrid = true; // 默认显示网格
    private boolean showDebugInfo = true;
    private Color gridColor = Color.WHITE; // 改为白色
    
    // 常量
    private static final float CHARACTER_FRAME_SPEED = 0.04f;
    private static final float CHARACTER_MOVING_SPEED = 0.03f;
    private static final int GHOST_WARNING_TIME = 3;
    private static final int GHOST_VULNERABLE_TIME = 8;
    private static final int FPS = 60;
    
    // 屏幕缩放
    private float scale = 1.0f;
    private int screenOffsetX = 0;
    private int screenOffsetY = 0;
    
    public PacmanGame() {
        setPreferredSize(new Dimension(1024, 768));
        setBackground(Color.BLACK);
        
        asset = new Asset();
        game = new GameState();
        
        initializeGame();
        
        gameTimer = new Timer(1000 / FPS, this);
        gameTimer.start();
        
        addKeyListener(this);
        setFocusable(true);
        requestFocusInWindow();
        
        // 计算缩放和偏移
        calculateScaleAndOffset();
    }
    
    private void calculateScaleAndOffset() {
        Dimension size = getSize();
        float scaleX = (float) size.width / Asset.VIRTUAL_WIDTH;
        float scaleY = (float) size.height / Asset.VIRTUAL_HEIGHT;
        scale = Math.min(scaleX, scaleY);
        
        screenOffsetX = (int) ((size.width - Asset.VIRTUAL_WIDTH * scale) / 2);
        screenOffsetY = (int) ((size.height - Asset.VIRTUAL_HEIGHT * scale) / 2);
    }
    
    private void initializeGame() {
        asset.loadAssets();
        
        game.pacman = new Pacman(18, 20);
        
        // 初始化幽灵 - 使用新的幽灵图标
        game.ghosts.add(new Ghost(28, 23, asset.redGhostFrames));
        game.ghosts.add(new Ghost(28, 29, asset.pinkGhostFrames));
        game.ghosts.add(new Ghost(24, 29, asset.cyanGhostFrames));
        game.ghosts.add(new Ghost(32, 29, asset.orangeGhostFrames));
        
        // 豆子已经在迷宫生成时自动放置
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        updateGame();
        repaint();
    }
    
    private void updateGame() {
        globalTimer += 1.0f / FPS;
        
        // 更新动画帧
        game.pacmanCharacterFrame = getAnimationFrame(Asset.PACMAN_FRAMES);
        game.pacmanDeathAnimationFrame = getAnimationFrame(Asset.PACMAN_FRAMES, 3);
        game.ghostCharacterFrame = getAnimationFrame(Asset.GHOST_FRAMES);
        game.pelletCharacterFrame = getAnimationFrame(Asset.PELLET_FRAMES);
        game.powerPelletFrame = getAnimationFrame(Asset.POWER_PELLET_FRAMES, 2); // 能量豆闪烁更快
        
        updatePellets();
        updateGhosts();
        pacmanMoveTo();
        checkPacmanState();
        resetDeadGhosts();
    }
    
    private int getAnimationFrame(int totalFrames) {
        return getAnimationFrame(totalFrames, 1);
    }
    
    private int getAnimationFrame(int totalFrames, float speedMultiplier) {
        return (int)(globalTimer / (CHARACTER_FRAME_SPEED * speedMultiplier)) % totalFrames;
    }
    
    private void updatePellets() {
        // 检查普通豆子
        for (Pellet pellet : game.pellets) {
            if (!pellet.eaten && checkCollision(game.pacman.getBounds(), pellet.getBounds())) {
                pellet.eaten = true;
                // 普通豆子不会触发能量状态
            }
        }
        
        // 检查能量豆
        for (PowerPellet powerPellet : game.powerPellets) {
            if (!powerPellet.eaten && checkCollision(game.pacman.getBounds(), powerPellet.getBounds())) {
                powerPellet.eaten = true;
                pelletEnabledTime = globalTimer;
                game.pacman.isPowerUp = true;
            }
        }
        
        // 检查能量豆效果是否结束
        if (pelletEnabledTime > 0 && globalTimer - pelletEnabledTime > GHOST_VULNERABLE_TIME) {
            game.pacman.isPowerUp = false;
            pelletEnabledTime = 0;
        }
    }
    
    private void updateGhosts() {
        checkAllGhostState();
        
        // 简单的幽灵AI - 向Pacman移动
        for (Ghost ghost : game.ghosts) {
            if (ghost.ghostState != GhostState.DEAD) {
                moveGhostTowardPacman(ghost);
            }
        }
    }
    
    private void moveGhostTowardPacman(Ghost ghost) {
        int targetX = game.pacman.xTile;
        int targetY = game.pacman.yTile;
        
        int dx = 0, dy = 0;
        int nextX = 0, nextY = 0;
        int currentBestDistance = Integer.MAX_VALUE;
        
        // 尝试四个方向
        int[][] directions = {{0, 1}, {-1, 0}, {0, -1}, {1, 0}};
        
        for (int[] dir : directions) {
            int newX = ghost.xTile + dir[0];
            int newY = ghost.yTile + dir[1];
            
            // 不能向后走
            if (dir[0] == -ghost.currentDirectionX && dir[1] == -ghost.currentDirectionY) {
                continue;
            }
            
            if (game.canMoveTo(newX, newY)) {
                int distance = Math.abs(targetX - newX) + Math.abs(targetY - newY);
                if (distance < currentBestDistance) {
                    currentBestDistance = distance;
                    nextX = dir[0];
                    nextY = dir[1];
                }
            }
        }
        
        ghost.xTile += nextX;
        ghost.yTile += nextY;
        ghost.currentDirectionX = nextX;
        ghost.currentDirectionY = nextY;
        
        // 更新幽灵眼睛方向
        if (nextX == 1) game.ghostEyeFrame = 0; // 右
        else if (nextX == -1) game.ghostEyeFrame = 1; // 左
        else if (nextY == 1) game.ghostEyeFrame = 2; // 下
        else if (nextY == -1) game.ghostEyeFrame = 3; // 上
    }
    
    private void pacmanMoveTo() {
        // 检查步进计时
        int currentStep = (int)(globalTimer / CHARACTER_MOVING_SPEED);
        if (currentStep > game.pacman.lastStep) {
            game.pacman.lastStep = currentStep;
            
            // 根据请求的方向移动
            int newX = game.pacman.xTile + game.pacman.dx;
            int newY = game.pacman.yTile + game.pacman.dy;
            
            if (game.canMoveTo(newX, newY) && !game.pacman.isDead) {
                game.pacman.xTile = newX;
                game.pacman.yTile = newY;
            }
        }
    }
    
    private void checkPacmanState() {
        // 检查Pacman是否碰到幽灵
        for (Ghost ghost : game.ghosts) {
            if (ghost.ghostState == GhostState.ALIVE && 
                checkCollision(game.pacman.getBounds(), ghost.getBounds()) &&
                !game.pacman.isDead) {
                
                game.pacman.isDead = true;
                game.pacman.life--;
                game.pacmanDeathAnimationFrame = 0;
                
                if (game.pacman.life <= 0) {
                    // 游戏结束
                    JOptionPane.showMessageDialog(this, "Game Over!");
                    initializeGame();
                }
                break;
            }
        }
        
        // 检查死亡动画是否结束
        if (game.pacman.isDead && game.pacmanDeathAnimationFrame >= Asset.PACMAN_FRAMES - 1) {
            game.pacman.isDead = false;
            // 重置位置
            game.pacman.xTile = 18;
            game.pacman.yTile = 20;
            game.pacman.dx = 0;
            game.pacman.dy = 0;
        }
    }
    
    private void checkAllGhostState() {
        if (pelletEnabledTime == 0) return;
        
        float elapsed = globalTimer - pelletEnabledTime;
        
        if (elapsed >= GHOST_VULNERABLE_TIME) {
            for (Ghost ghost : game.ghosts) {
                ghost.ghostState = GhostState.ALIVE;
            }
            pelletEnabledTime = 0;
            return;
        }
        
        for (Ghost ghost : game.ghosts) {
            if (ghost.ghostState != GhostState.DEAD) {
                if (elapsed <= GHOST_VULNERABLE_TIME - GHOST_WARNING_TIME) {
                    ghost.ghostState = GhostState.VULNERABLE;
                } else {
                    ghost.ghostState = GhostState.COMING_ALIVE;
                }
                
                // 检查幽灵是否被吃掉
                if (ghost.ghostState == GhostState.VULNERABLE && 
                    checkCollision(game.pacman.getBounds(), ghost.getBounds())) {
                    ghost.ghostState = GhostState.DEAD;
                }
            }
        }
    }
    
    private void resetDeadGhosts() {
        for (Ghost ghost : game.ghosts) {
            if (ghost.ghostState == GhostState.DEAD) {
                // 简单重置到起始位置
                ghost.xTile = 28;
                ghost.yTile = 23;
                ghost.ghostState = GhostState.ALIVE;
            }
        }
    }
    
    private boolean checkCollision(Rectangle rect1, Rectangle rect2) {
        return rect1.intersects(rect2);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // 启用抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        
        // 应用缩放和偏移
        AffineTransform originalTransform = g2d.getTransform();
        g2d.translate(screenOffsetX, screenOffsetY);
        g2d.scale(scale, scale);
        
        drawGame(g2d);
        
        // 恢复变换
        g2d.setTransform(originalTransform);
        
        // 绘制UI元素（不受缩放影响）
        drawUI(g2d);
    }
    
    private void drawGame(Graphics2D g2d) {
        // 绘制背景
        if (asset.background != null) {
            g2d.drawImage(asset.background, 0, 0, Asset.VIRTUAL_WIDTH, Asset.MAP_HEIGHT, null);
        }
        
        // 绘制网格（调试用）- 现在使用白色
        if (showGrid) {
            drawGrid(g2d);
        }
        
        // 绘制墙壁
        drawWalls(g2d);
        
        // 绘制豆子
        drawPellets(g2d);
        
        // 绘制幽灵
        drawGhosts(g2d);
        
        // 绘制Pacman
        drawPacman(g2d);
    }
    
    private void drawGrid(Graphics2D g2d) {
        g2d.setColor(gridColor); // 现在使用白色
        for (int y = 0; y < Asset.MAP_HEIGHT; y += Asset.TILE_SIZE) {
            for (int x = 0; x < Asset.VIRTUAL_WIDTH; x += Asset.TILE_SIZE) {
                g2d.drawRect(x, y, Asset.TILE_SIZE, Asset.TILE_SIZE);
            }
        }
    }
    
    private void drawWalls(Graphics2D g2d) {
        // 使用新的墙颜色和样式
        for (int i = 0; i < game.wall.length; i++) {
            for (int j = 0; j < game.wall[0].length; j++) {
                if (game.wall[i][j]) {
                    // 主墙颜色
                    g2d.setColor(asset.wallColor);
                    g2d.fillRect(i * Asset.TILE_SIZE, j * Asset.TILE_SIZE, 
                                Asset.TILE_SIZE, Asset.TILE_SIZE);
                    
                    // 墙的高光效果
                    g2d.setColor(asset.wallHighlightColor);
                    g2d.fillRect(i * Asset.TILE_SIZE, j * Asset.TILE_SIZE, 
                                Asset.TILE_SIZE, 2);
                    g2d.fillRect(i * Asset.TILE_SIZE, j * Asset.TILE_SIZE, 
                                2, Asset.TILE_SIZE);
                    
                    // 墙的阴影效果
                    g2d.setColor(new Color(0, 0, 100));
                    g2d.fillRect(i * Asset.TILE_SIZE + Asset.TILE_SIZE - 2, j * Asset.TILE_SIZE, 
                                2, Asset.TILE_SIZE);
                    g2d.fillRect(i * Asset.TILE_SIZE, j * Asset.TILE_SIZE + Asset.TILE_SIZE - 2, 
                                Asset.TILE_SIZE, 2);
                }
            }
        }
    }
    
    private void drawPellets(Graphics2D g2d) {
        // 绘制普通豆子
        for (Pellet pellet : game.pellets) {
            if (!pellet.eaten) {
                if (game.pelletCharacterFrame < asset.pelletFrames.size()) {
                    BufferedImage frame = asset.pelletFrames.get(game.pelletCharacterFrame);
                    g2d.drawImage(frame, pellet.getPixelX(), pellet.getPixelY(), null);
                } else {
                    // 备用绘制
                    g2d.setColor(Color.WHITE);
                    g2d.fillOval(pellet.getPixelX(), pellet.getPixelY(), 8, 8);
                }
            }
        }
        
        // 绘制能量豆
        for (PowerPellet powerPellet : game.powerPellets) {
            if (!powerPellet.eaten) {
                if (game.powerPelletFrame < asset.powerPelletFrames.size()) {
                    BufferedImage frame = asset.powerPelletFrames.get(game.powerPelletFrame);
                    g2d.drawImage(frame, powerPellet.getPixelX(), powerPellet.getPixelY(), null);
                } else {
                    // 备用绘制
                    g2d.setColor(Color.WHITE);
                    g2d.fillOval(powerPellet.getPixelX(), powerPellet.getPixelY(), 12, 12);
                }
            }
        }
    }
    
    private void drawGhosts(Graphics2D g2d) {
        for (Ghost ghost : game.ghosts) {
            if (ghost.ghostState != GhostState.DEAD && !game.pacman.isDead) {
                List<BufferedImage> frames = getGhostFrames(ghost);
                
                if (game.ghostCharacterFrame < frames.size()) {
                    BufferedImage frame = frames.get(game.ghostCharacterFrame);
                    g2d.drawImage(frame, ghost.getPixelX(), ghost.getPixelY(), null);
                }
                
                // 绘制眼睛（除了脆弱状态）
                if (ghost.ghostState == GhostState.ALIVE && 
                    game.ghostEyeFrame < asset.ghostEyesFrames.size()) {
                    BufferedImage eyes = asset.ghostEyesFrames.get(game.ghostEyeFrame);
                    g2d.drawImage(eyes, ghost.getPixelX(), ghost.getPixelY(), null);
                }
            }
        }
    }
    
    private List<BufferedImage> getGhostFrames(Ghost ghost) {
        switch (ghost.ghostState) {
            case VULNERABLE:
                return asset.vulnerableGhostFrames;
            case COMING_ALIVE:
                return asset.ephemeralVulnerableGhostFrames;
            case ALIVE:
            default:
                return ghost.textureFrames;
        }
    }
    
    private void drawPacman(Graphics2D g2d) {
        List<BufferedImage> frames;
        int frameIndex;
        
        if (game.pacman.isDead) {
            frames = asset.pacManDeathFrames;
            frameIndex = game.pacmanDeathAnimationFrame;
        } else {
            frames = asset.pacManFrames;
            frameIndex = game.pacmanCharacterFrame;
        }
        
        if (frameIndex < frames.size()) {
            BufferedImage frame = frames.get(frameIndex);
            
            // 创建旋转变换
            AffineTransform originalTransform = g2d.getTransform();
            AffineTransform rotation = new AffineTransform();
            rotation.translate(game.pacman.getPixelX() + Asset.SPRITE_SIZE / 2, 
                             game.pacman.getPixelY() + Asset.SPRITE_SIZE / 2);
            rotation.rotate(Math.toRadians(game.pacman.rotationAngle));
            rotation.translate(-Asset.SPRITE_SIZE / 2, -Asset.SPRITE_SIZE / 2);
            
            g2d.setTransform(rotation);
            g2d.drawImage(frame, 0, 0, Asset.SPRITE_SIZE, Asset.SPRITE_SIZE, null);
            g2d.setTransform(originalTransform);
        }
    }
    
    private void drawUI(Graphics2D g2d) {
        if (showDebugInfo) {
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.PLAIN, 16));
            
            g2d.drawString("Pacman: X=" + game.pacman.xTile + " Y=" + game.pacman.yTile, 10, 20);
            g2d.drawString("Lives: " + game.pacman.life, 10, 40);
            g2d.drawString("Power Up: " + (game.pacman.isPowerUp ? "YES" : "NO"), 10, 60);
            g2d.drawString("Pellets: " + countUneatenPellets(), 10, 80);
        }
        
        // 绘制生命指示器
        drawLifeIndicator(g2d);
        
        // 绘制能量豆计时器
        if (game.pacman.isPowerUp) {
            drawPowerUpTimer(g2d);
        }
    }
    
    private void drawLifeIndicator(Graphics2D g2d) {
        int startX = getWidth() - 100;
        int y = 20;
        
        g2d.setColor(Color.WHITE);
        g2d.drawString("Lives:", startX, y);
        
        for (int i = 0; i < game.pacman.life; i++) {
            g2d.setColor(Color.YELLOW);
            g2d.fillArc(startX + 60 + i * 25, y - 15, 20, 20, 45, 270);
        }
    }
    
    private void drawPowerUpTimer(Graphics2D g2d) {
        float timeLeft = GHOST_VULNERABLE_TIME - (globalTimer - pelletEnabledTime);
        if (timeLeft < 0) timeLeft = 0;
        
        int barWidth = 200;
        int barHeight = 10;
        int x = (getWidth() - barWidth) / 2;
        int y = getHeight() - 30;
        
        // 背景
        g2d.setColor(Color.GRAY);
        g2d.fillRect(x, y, barWidth, barHeight);
        
        // 进度
        float progress = timeLeft / GHOST_VULNERABLE_TIME;
        int fillWidth = (int)(barWidth * progress);
        
        // 闪烁效果当时间快到时
        if (timeLeft <= GHOST_WARNING_TIME && ((int)(globalTimer * 10) % 2) == 0) {
            g2d.setColor(Color.WHITE);
        } else {
            g2d.setColor(Color.BLUE);
        }
        
        g2d.fillRect(x, y, fillWidth, barHeight);
        
        // 边框
        g2d.setColor(Color.WHITE);
        g2d.drawRect(x, y, barWidth, barHeight);
        
        // 文字
        g2d.drawString(String.format("Power: %.1fs", timeLeft), x, y - 5);
    }
    
    private int countUneatenPellets() {
        int count = 0;
        for (Pellet pellet : game.pellets) {
            if (!pellet.eaten) count++;
        }
        for (PowerPellet powerPellet : game.powerPellets) {
            if (!powerPellet.eaten) count++;
        }
        return count;
    }
    
    // 键盘输入处理
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        
        switch (key) {
            case KeyEvent.VK_W:
                game.pacman.requestKey = key;
                game.pacman.dx = 0;
                game.pacman.dy = -1;
                game.pacman.rotationAngle = 90;
                break;
            case KeyEvent.VK_S:
                game.pacman.requestKey = key;
                game.pacman.dx = 0;
                game.pacman.dy = 1;
                game.pacman.rotationAngle = 270;
                break;
            case KeyEvent.VK_A:
                game.pacman.requestKey = key;
                game.pacman.dx = -1;
                game.pacman.dy = 0;
                game.pacman.rotationAngle = 0;
                break;
            case KeyEvent.VK_D:
                game.pacman.requestKey = key;
                game.pacman.dx = 1;
                game.pacman.dy = 0;
                game.pacman.rotationAngle = 180;
                break;
            case KeyEvent.VK_G:
                showGrid = !showGrid;
                break;
            case KeyEvent.VK_I:
                showDebugInfo = !showDebugInfo;
                break;
            case KeyEvent.VK_R:
                initializeGame();
                break;
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {}
    
    @Override
    public void keyTyped(KeyEvent e) {}
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Pac-Man Game - Enhanced Complex Maze");
            PacmanGame game = new PacmanGame();
            
            frame.add(game);
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
