import java.util.ArrayList;
import java.util.Random;
import java.awt.Color;

public class Block {
 private int xCoord;
 private int yCoord;
 private int size; // height/width of the square
 private int level; // the root (outer most block) is at level 0
 private int maxDepth;
 private Color color;

 private Block[] children; // {UR, UL, LL, LR}

 public static Random gen = new Random();


 /*
  * These two constructors are here for testing purposes.
  */
 public Block() {}

 public Block(int x, int y, int size, int lvl, int  maxD, Color c, Block[] subBlocks) {
  this.xCoord=x;
  this.yCoord=y;
  this.size=size;
  this.level=lvl;
  this.maxDepth = maxD;
  this.color=c;
  this.children = subBlocks;
 }



 /*
  * Creates a random block given its level and a max depth.
  *
  * xCoord, yCoord, size, and highlighted should not be initialized
  * (i.e. they will all be initialized by default)
  */
 public Block(int lvl, int maxDepth) {

  this.level = lvl;
  this.maxDepth = maxDepth;

  if (lvl < maxDepth) {

   double randomValue = gen.nextDouble();

   if (randomValue < Math.exp(-0.25 * level)) {

    this.children = new Block[4];

    for (int i = 0; i < 4; i++) {

     this.children[i] = new Block(lvl + 1, maxDepth);

    }

   }

   else {

    this.children = new Block[0];
    int randomIndex = gen.nextInt(GameColors.BLOCK_COLORS.length);
    this.color = GameColors.BLOCK_COLORS[randomIndex];

   }

  }

  else {

   this.children = new Block[0];
   int randomIndex = gen.nextInt(GameColors.BLOCK_COLORS.length);
   this.color = GameColors.BLOCK_COLORS[randomIndex];

  }

 }


 /*
  * Updates size and position for the block and all of its sub-blocks, while
  * ensuring consistency between the attributes and the relationship of the
  * blocks.
  *
  *  The size is the height and width of the block. (xCoord, yCoord) are the
  *  coordinates of the top left corner of the block.
  */
 public void updateSizeAndPosition (int size, int xCoord, int yCoord) {

  this.size = size;
  this.xCoord = xCoord;
  this.yCoord = yCoord;

  if (this.size < 0 || (this.size % 2 > 0 && this.size != 1)){

   throw new IllegalArgumentException("Improper Size");

  }

  if(this.size == 1 && this.level < this.maxDepth){

   throw new IllegalArgumentException("Improper Size");

  }

  if (children.length == 4) {

   int subBlockSize = size / 2;

   for (int i = 0; i < 2; i++) {

    int childX = xCoord + (i % 2) * subBlockSize;
    int childY = yCoord + (i / 2) * subBlockSize;

    if(i == 0){

     children[1].updateSizeAndPosition(subBlockSize, childX, childY);

    }

    else{

     children[0].updateSizeAndPosition(subBlockSize, childX, childY);

    }

   }

   for (int i = 2; i < 4; i++) {

    int childX = xCoord + (i % 2) * subBlockSize;
    int childY = yCoord + (i / 2) * subBlockSize;
    children[i].updateSizeAndPosition(subBlockSize, childX, childY);

   }

  }

 }


 /*
  * Returns a List of blocks to be drawn to get a graphical representation of this block.
  *
  * This includes, for each undivided Block:
  * - one BlockToDraw in the color of the block
  * - another one in the FRAME_COLOR and stroke thickness 3
  *
  * Note that a stroke thickness equal to 0 indicates that the block should be filled with its color.
  *
  * The order in which the blocks to draw appear in the list does NOT matter.
  */
 public ArrayList<BlockToDraw> getBlocksToDraw() {

  ArrayList<BlockToDraw> blocksToDraw = new ArrayList<>();

  if (children.length == 0) {

   BlockToDraw square = new BlockToDraw(this.color, this.xCoord, this.yCoord, this.size, 0);

   BlockToDraw border = new BlockToDraw(GameColors.FRAME_COLOR, this.xCoord, this.yCoord, this.size, 3);

   blocksToDraw.add(square);
   blocksToDraw.add(border);

  }

  else {

   for (Block b: children) {

    blocksToDraw.addAll(b.getBlocksToDraw());

   }

  }

  return blocksToDraw;

 }

 /*
  * This method is provided and you should NOT modify it.
  */
 public BlockToDraw getHighlightedFrame() {
  return new BlockToDraw(GameColors.HIGHLIGHT_COLOR, this.xCoord, this.yCoord, this.size, 5);
 }



 /*
  * Return the Block within this Block that includes the given location
  * and is at the given level. If the level specified is lower than
  * the lowest block at the specified location, then return the block
  * at the location with the closest level value.
  *
  * The location is specified by its (x, y) coordinates. The lvl indicates
  * the level of the desired Block. Note that if a Block includes the location
  * (x, y), and that Block is subdivided, then one of its sub-Blocks will
  * contain the location (x, y) too. This is why we need lvl to identify
  * which Block should be returned.
  *
  * Input validation:
  * - this.level <= lvl <= maxDepth (if not throw exception)
  * - if (x,y) is not within this Block, return null.
  */
 public Block getSelectedBlock(int x, int y, int lvl) {

  if (lvl < level || lvl > maxDepth) {

   throw new IllegalArgumentException("Invalid Level");

  }

  else if (lvl == level || children.length == 0) {

   return this;

  }

  int xQ = (x >= this.xCoord + (this.size/2)) ? this.xCoord + (this.size/2) : this.xCoord;

  int yQ = (y >= this.yCoord + (this.size/2)) ? this.yCoord + (this.size/2) : this.yCoord;

  for (int i = 0; i < 4; i++) {

   if (xQ == this.children[i].xCoord && yQ == this.children[i].yCoord) {

    return this.children[i].getSelectedBlock(x, y, lvl);

   }

  }

  return null;

 }




 /*
  * Swaps the child Blocks of this Block.
  * If input is 1, swap vertically. If 0, swap horizontally.
  * If this Block has no children, do nothing. The swap
  * should be propagate, effectively implementing a reflection
  * over the x-axis or over the y-axis.
  *
  */
 public void reflect(int direction) {

  if (direction < 0 || direction > 1) {

   throw new IllegalArgumentException("Invalid direction");

  }

  if (direction == 0) {

   switchBlocks(0, 3);
   switchBlocks(1, 2);

  }

  else {

   switchBlocks(0, 1);
   switchBlocks(2, 3);

  }

  for (int i = 0; i < 4; i++) {

   Block b = children[i];

   if (b.children.length > 0) {

    b.reflect(direction);

   }

  }

 }



 /*
  * Rotate this Block and all its descendants.
  * If the input is 1, rotate clockwise. If 0, rotate
  * counterclockwise. If this Block has no children, do nothing.
  */
 public void rotate(int direction) {

  if(direction < 0 || direction > 1) {

   throw new IllegalArgumentException("Invalid direction");

  }

  if (children.length == 4) {

   for (Block b : children) {

    b.rotate(direction);

   }

   if (direction == 0) {

    switchBlocks(0,1);
    switchBlocks(0,3);
    switchBlocks(2,3);

   }

   else {

    switchBlocks(0,3);
    switchBlocks(0,1);
    switchBlocks(1,2);

   }

  }

 }

 private void switchBlocks(int a, int b) {

  this.updateSizeAndPosition(size, xCoord, yCoord);

  Block temp = children[a];
  children[a] = children[b];
  children[b] = temp;

  int temp1 = children[a].xCoord;
  int temp2 = children[a].yCoord;

  children[a].xCoord = children[b].xCoord;
  children[a].yCoord = children[b].yCoord;

  children[b].xCoord = temp1;
  children[b].yCoord = temp2;

 }

 /*
  * Smash this Block.
  *
  * If this Block can be smashed,
  * randomly generate four new children Blocks for it.
  * (If it already had children Blocks, discard them.)
  * Ensure that the invariants of the Blocks remain satisfied.
  *
  * A Block can be smashed iff it is not the top-level Block
  * and it is not already at the level of the maximum depth.
  *
  * Return True if this Block was smashed and False otherwise.
  *
  */
 public boolean smash() {

  if (level == 0 || level < 0 || level == maxDepth) {

   return false;

  }

  int subBlockSize = size / 2;

  children = new Block[4];

  for (int i = 0; i < 4; i++) {

   int childX = xCoord + (i % 2) * subBlockSize;
   int childY = yCoord + (i / 2) * subBlockSize;

   children[i] = new Block(level + 1, maxDepth);
   children[i].updateSizeAndPosition(subBlockSize, childX, childY);

  }

  color = null;

  return true;

 }


 /*
  * Return a two-dimensional array representing this Block as rows and columns of unit cells.
  *
  * Return and array arr where, arr[i] represents the unit cells in row i,
  * arr[i][j] is the color of unit cell in row i and column j.
  *
  * arr[0][0] is the color of the unit cell in the upper left corner of this Block.
  */
 public Color[][] flatten() {

  int arraySize = (int) Math.pow(2, this.maxDepth - this.level);
  Color[][] flattened = new Color[arraySize][arraySize];

  if (this.children.length == 0 || arraySize == 1) {

   for (int a = 0; a < arraySize; a++) {

    for (int b = 0; b < arraySize; b++) {

     flattened[a][b] = this.color;

    }

   }

   return flattened;

  }

  else {

   int stepSize = arraySize / 2;

   Color[][] uR = this.children[0].flatten();
   Color[][] uL = this.children[1].flatten();
   Color[][] lL = this.children[2].flatten();
   Color[][] lR = this.children[3].flatten();


   for (int i = 0; i < stepSize; i++) {

    for (int j = 0; j < stepSize; j++) {

     flattened[i][j] = uL[i][j];
     flattened[i][j + stepSize] = uR[i][j];
     flattened[i + stepSize][j] = lL[i][j];
     flattened[i + stepSize][j + stepSize] = lR[i][j];

    }

   }

  }

  return flattened;

 }



 // These two get methods have been provided. Do NOT modify them.
 public int getMaxDepth() {
  return this.maxDepth;
 }

 public int getLevel() {
  return this.level;
 }


 /*
  * The next 5 methods are needed to get a text representation of a block.
  * You can use them for debugging. You can modify these methods if you wish.
  */
 public String toString() {
  return String.format("pos=(%d,%d), size=%d, level=%d"
          , this.xCoord, this.yCoord, this.size, this.level);
 }

 public void printBlock() {
  this.printBlockIndented(0);
 }

 private void printBlockIndented(int indentation) {
  String indent = "";
  for (int i=0; i<indentation; i++) {
   indent += "\t";
  }

  if (this.children.length == 0) {
   // it's a leaf. Print the color!
   String colorInfo = GameColors.colorToString(this.color) + ", ";
   System.out.println(indent + colorInfo + this);
  } else {
   System.out.println(indent + this);
   for (Block b : this.children)
    b.printBlockIndented(indentation + 1);
  }
 }

 private static void coloredPrint(String message, Color color) {
  System.out.print(GameColors.colorToANSIColor(color));
  System.out.print(message);
  System.out.print(GameColors.colorToANSIColor(Color.WHITE));
 }

 public void printColoredBlock(){
  Color[][] colorArray = this.flatten();
  for (Color[] colors : colorArray) {
   for (Color value : colors) {
    String colorName = GameColors.colorToString(value).toUpperCase();
    if(colorName.length() == 0){
     colorName = "\u2588";
    }else{
     colorName = colorName.substring(0, 1);
    }
    coloredPrint(colorName, value);
   }
   System.out.println();
  }
 }

}
