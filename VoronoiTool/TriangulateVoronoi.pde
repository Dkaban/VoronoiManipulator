
////////////------------- HANDLES ALL THE TRIANGULATION, INITIAL TRIANGULATION IS BASED ON VORONOI CELL --------------\\\\\\\\\\\\\\\
void TriangulateVoronoi()
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

void AddToTriangleList(float x1, float y1, float x2, float y2, float x3, float y3,float fillC)
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
