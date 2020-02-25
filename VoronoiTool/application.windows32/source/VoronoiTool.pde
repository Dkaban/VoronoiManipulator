/* OpenProcessing Tweak of *@*http://www.openprocessing.org/sketch/66376*@* */
/* !do not delete the line above, required for linking your tweak if you re-upload */

//IMPORT LIBRARIES
import megamu.mesh.*;
import controlP5.*;

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
void setup()
{
  size(400,400);
  UISetup();
  GenerateVoronoi();
}

void UISetup()
{
  ControlP5 controlP5 = new ControlP5(this);
  controlP5.addButton("Restart",0,5,5,40,30);
}

void draw()
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
void GenerateVoronoi()
{
   //Setup the Points
  int numPoints = 0;
  points = new float[numPoints][2];
  voronoi = new Voronoi(points);
  regions = voronoi.getRegions(); 
}

//Draw the Voronoi to the screen, taking in voronoi and regions
void DrawVoronoi()
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

void DrawTriangles()
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
