import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import megamu.mesh.*; 
import controlP5.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class VoronoiTool extends PApplet {

/* OpenProcessing Tweak of *@*http://www.openprocessing.org/sketch/66376*@* */
/* !do not delete the line above, required for linking your tweak if you re-upload */

//IMPORT LIBRARIES



//Voronoi Initialization
float[][] points;
Voronoi voronoi;
MPolygon[] regions;

//Interactivity Variables
boolean selected = false;
int index = 0;
int selectDistance = 20;

//Triangulation Variables
ArrayList<PVector> triangleList = new ArrayList<PVector>();
boolean addToList = false;
boolean triangleMode = false;

//////////////////////------------------------------ INITIAL SETUP -------------------------\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
public void setup()
{
  
  UISetup();
  GenerateVoronoi();
}

public void UISetup()
{
  ControlP5 controlP5 = new ControlP5(this);
  controlP5.addButton("Restart",0,5,5,40,30);
}

public void draw()
{
  background(255,255,255);
  
  if(triangleMode == false)
  {
    DrawVoronoi();
  }
  if(triangleMode == true)
  {
    DrawTriangles();
  }
}

///////////////////////---------------------------- VORONOI SETUP LOGIC -----------------------\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
//Generates the points for the Voronoi
public void GenerateVoronoi()
{
   //Setup the Points
  int numPoints = 0;
  points = new float[numPoints][2];
  voronoi = new Voronoi(points);
  regions = voronoi.getRegions(); 
}

//Draw the Voronoi to the screen, taking in voronoi and regions
public void DrawVoronoi()
{
  //Triangulates specifically the Voronoi
  TriangulateVoronoi();
  
  //Draws an ellipse on all Manipulatable Points
  for(int p=0;p<points.length;p++)
  {
    fill(0,255,0);
    ellipse(points[p][0],points[p][1],5,5); 
  }
}

public void DrawTriangles()
{
   for (int t=0;t<triangleList.size();t++)
   {
      //ShiftTriangle(triangleList,triangleList.get(t).x,triangleList.get(t).y,perturbX,perturbY,t);
      PVector xy1 = new PVector(triangleList.get(t).x, triangleList.get(t).y);
      PVector xy2 = new PVector(triangleList.get((t+1)%triangleList.size()).x, triangleList.get((t+1)%triangleList.size()).y);
      PVector xy3 = new PVector(triangleList.get((t+2)%triangleList.size()).x, triangleList.get((t+2)%triangleList.size()).y);
      PVector shadeXY = new PVector(triangleList.get((t+3)%triangleList.size()).x, triangleList.get((t+3)%triangleList.size()).y);
      float x1 = xy1.x;
      float y1 = xy1.y;
      float x2 = xy2.x;
      float y2 = xy2.y;
      float x3 = xy3.x;
      float y3 = xy3.y;
      stroke(0);
      float shade = shadeXY.x;
      fill(shade);
      triangle(x1,y1,x2,y2,x3,y3);
      
      fill(0,255,0);
      ellipse(x1,y1,5,5);
      ellipse(x2,y2,5,5);
      ellipse(x3,y3,5,5);
      
      t+= 3;
    }
}
////////////-------------- UI BUTTON FUNCTIONS --------------------\\\\\\\\\\
public void controlEvent(ControlEvent theEvent)
{
  if (theEvent.controller().getName() == "Restart")
  {
    points = new float[0][2];
    voronoi = new Voronoi(points);
    regions = voronoi.getRegions();
  }
}


//////////////////////---------------------- INTERACTIVITY FUNCTIONS FOR MOUSE CONTROLS  -------------------------\\\\\\\\\\\\\\\\\\\\\\\\\
public void mousePressed()
{
  if(mouseButton == RIGHT)
  {
      points = (float[][])append(points, new float[]{mouseX, mouseY});
  } 
}

public void mouseMoved() 
{
  // if myRegions is null then mesh is not ready
  if (regions==null) return;
  
  //Initializes the Voronoi cells (keeps them updated when created)
  if(triangleMode == false)
  {
    voronoi = new Voronoi(points);
    regions = voronoi.getRegions();
  }
  if(triangleMode == true)
  {
    DrawTriangles();
  }
}
//////////////////////////------------------------------ WORKS FOR WHEN MODIFYING VORONOI -----------------------------------\\\\\\\\\\\\\\\\\
public void mouseDragged() 
{
  // if myRegions is null then mesh is not ready
  if (regions==null) return;

  for (int i=0;i<points.length;i++)
  {
    if (mouseX > points[i][0] - selectDistance && mouseX < points[i][0] + selectDistance && mouseY > points[i][1] - selectDistance && mouseY < points[i][1] + selectDistance)
    {
      if (mousePressed == true)
      {
        if (mouseButton == LEFT)
        {        
          selected = true;
          index = i;
        }
      }
    }
  }
  if (selected == true)
  {
    points[index][0]=mouseX;
    points[index][1]=mouseY;
  }
  mouseMoved();
}

////////////------------- HANDLES ALL THE TRIANGULATION, INITIAL TRIANGULATION IS BASED ON VORONOI CELL --------------\\\\\\\\\\\\\\\
public void TriangulateVoronoi()
{
  for(int r=0;r<regions.length;r++)
  {
    stroke(0);
    regions[r].draw(this);
    float[][] coordinates = regions[r].getCoords();
    
    for(int c=0;c<coordinates.length;c++)
    {
      float startX = coordinates[c][0];
      float startY = coordinates[c][1];
      float endX = coordinates[(c+1)%coordinates.length][0];
      float endY = coordinates[(c+1)%coordinates.length][1];
      //Used to determine the fill color
      PVector v1 = new PVector(startX - points[r][0], startY - points[r][1]);
      PVector v2 = new PVector(startX - endX, startY - endY);
      float a = PVector.angleBetween(v1, v2);
      float fillLength = v1.mag() * sin(a);
      float fillColor = fillLength/200 * 255;
      
      fill(fillColor);
      triangle(points[r][0],points[r][1],startX,startY,endX,endY);
      
      //Triangulate(points[r][0],points[r][1],startX,startY,endX,endY,fillColor);
      if(addToList == true) { AddToTriangleList(points[r][0],points[r][1],startX,startY,endX,endY,fillColor); }
    }
  }  
}

public void AddToTriangleList(float x1, float y1, float x2, float y2, float x3, float y3,float fillC)
{
  PVector pv1 = new PVector(x1, y1);
  PVector pv2 = new PVector(x2, y2);
  PVector pv3 = new PVector(x3, y3);
  PVector pv4 = new PVector(fillC,0);
  triangleList.add(pv1); 
  triangleList.add(pv2); 
  triangleList.add(pv3);
  triangleList.add(pv4);
}




/*
////-------- TRIANGULATE SKETCH WITHOUT USING VORONOI ONCE INITIAL VORONOI CREATED ---------\\\\\
void Triangulate(float x1, float y1, float x2, float y2, float x3, float y3, float fillC)
{  
  //Find the centroid of the given triangle
  float centX = (x1 + x2 + x3)/3;
  float centY = (y1 + y2 + y3)/3;
  
  //Setup for cutting a triangle into 3 triangles (a third of the way along each side)
  PVector pvXY1 = new PVector(x1, y1);
  PVector pvXY2 = new PVector(x2, y2);
  PVector pvXY3 = new PVector(x3, y3);
  float dist1 = pvXY1.dist(pvXY2); //dist between 1 & 2
  float dist2 = pvXY1.dist(pvXY3); //dist between 1 & 3
  float dist3 = pvXY2.dist(pvXY3); //dist between 2 & 3
  
  //Solve for x and y     P = a * p1 + (1 - a) * P0    a = total distance / distance you want
  float a12 = dist1 / dist1 * 1/3;
  float a21 = dist1 / dist1 * 2/3;
  float a13 = dist2 / dist2 * 1/3;
  float a31 = dist2 / dist2 * 2/3;
  float a23 = dist3 / dist3 * 1/3;
  float a32 = dist3 / dist3 * 2/3;
  
  //Finds all the new points of the triangles created (chopping a single triangle into 9 tri's)
  float xP1 = a12 * pvXY1.x + (1 - a12) * pvXY2.x;
  float yP1 = a12 * pvXY1.y + (1 - a12) * pvXY2.y;
  float xP2 = a21 * pvXY1.x + (1 - a21) * pvXY2.x;
  float yP2 = a21 * pvXY1.y + (1 - a21) * pvXY2.y;
  float xP3 = a13 * pvXY1.x + (1 - a13) * pvXY3.x;
  float yP3 = a13 * pvXY1.y + (1 - a13) * pvXY3.y;
  float xP4 = a31 * pvXY1.x + (1 - a31) * pvXY3.x;
  float yP4 = a31 * pvXY1.y + (1 - a31) * pvXY3.y;
  float xP5 = a23 * pvXY2.x + (1 - a23) * pvXY3.x;
  float yP5 = a23 * pvXY2.y + (1 - a23) * pvXY3.y;
  float xP6 = a32 * pvXY2.x + (1 - a32) * pvXY3.x;
  float yP6 = a32 * pvXY2.y + (1 - a32) * pvXY3.y;
  
  stroke(0);
  fill(fillC);triangle(centX, centY, xP1, yP1, xP2, yP2);
  fill(fillC);triangle(centX, centY, xP3, yP3, xP4, yP4);
  fill(fillC);triangle(centX, centY, xP5, yP5, xP6, yP6);
  fill(fillC);triangle(centX, centY, xP1, yP1, xP6, yP6);
  fill(fillC);triangle(centX, centY, xP2, yP2, xP4, yP4);
  fill(fillC);triangle(centX, centY, xP3, yP3, xP5, yP5);
  fill(fillC);triangle(pvXY1.x, pvXY1.y, xP2, yP2, xP4, yP4);
  fill(fillC);triangle(pvXY2.x, pvXY2.y, xP1, yP1, xP6, yP6);
  fill(fillC);triangle(pvXY3.x, pvXY3.y, xP3, yP3, xP5, yP5);
  
  if(addToList == true)
  {
    AddToTriangleList(centX, centY, xP1, yP1, xP2, yP2);
    AddToTriangleList(centX, centY, xP3, yP3, xP4, yP4);
    AddToTriangleList(centX, centY, xP5, yP5, xP6, yP6);
    AddToTriangleList(centX, centY, xP1, yP1, xP6, yP6);
    AddToTriangleList(centX, centY, xP2, yP2, xP4, yP4);
    AddToTriangleList(centX, centY, xP3, yP3, xP5, yP5);
    AddToTriangleList(pvXY1.x, pvXY1.y, xP2, yP2, xP4, yP4);
    AddToTriangleList(pvXY2.x, pvXY2.y, xP1, yP1, xP6, yP6);
    AddToTriangleList(pvXY3.x, pvXY3.y, xP3, yP3, xP5, yP5);
  }
}
*/
  public void settings() {  size(400,400); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "VoronoiTool" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
