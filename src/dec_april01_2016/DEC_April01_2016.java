/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dec_april01_2016;
import com.sun.glass.events.KeyEvent;
import complex.DEC_Complex;
import complex.DEC_DualObject;
import complex.DEC_Iterator;
import complex.DEC_Object;
import complex.DEC_PrimalObject;
import containers.DEC_GeometricContainer;
import exceptions.DEC_Exception;
import forms.DiscreteForm;
import java.util.ArrayList;
import java.util.HashMap;
import processing.core.PApplet;
import static processing.core.PApplet.println;
import static processing.core.PConstants.LEFT;
import static processing.core.PConstants.RIGHT;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import processing.event.MouseEvent;
import readers.OBJMeshReader;
import viewers.MeshViewer;
import static processing.core.PApplet.println;
import static processing.core.PApplet.println;
import static processing.core.PApplet.println;
import utils.GeometricUtils;
/**
 *
 * @author laptop
 */
public class DEC_April01_2016 extends PApplet{
//-----------------------------------------------------------------
 //----------------MOTION CONTROL-----------------------------------
 //-----------------------------------------------------------------
 public float rotX=0;
 public float rotY=0;
 public float xTranslation = 0;
 public float yTranslation = 0;
 public float zTranslation = 0;
 //-----------------------------------------------------------------
 //-----------------MODEL CONTROL-----------------------------------
 //-----------------------------------------------------------------
 public boolean[] showComplexOption;
 public boolean[] selectionType;
 public int[] selectedItem;
 public int[] complexLimits;
 public MeshViewer myViewer;
 //-----------------------------------------------------------------
 //----------------MODEL READER-------------------------------------
 //-----------------------------------------------------------------
 //String modelName = "hombre2Triangulado.obj";
 String modelName = "toroTriangulatoTexturizado.obj";
 long startTime,endTime;
 OBJMeshReader myReader;
 DEC_GeometricContainer myContainer;
 DEC_Complex myComplex;
 //-----------------------------------------------------------------
 //----------MODEL VISUALIZATION PARAMETERS-------------------------
 //-----------------------------------------------------------------
 int numElement = 0;
 //-----------------------------------------------------------------
 //----------MODEL VISUALIZATION PARAMETERS-------------------------
 //-----------------------------------------------------------------
 DiscreteForm curvatureForm;
 HashMap<PVector,Double> curvatureLookUpTable;
 //-----------------------------------------------------------------
 //-------------TEXTURE MAP HANDLER---------------------------------
 //-----------------------------------------------------------------
 boolean show2DInformation = false;
 PImage textureMap;
 public void setup(){
  size(1500,800,P3D);
  colorMode(HSB);
  showComplexOption = new boolean[]{true,false,false,false,false,false};
  selectionType = new boolean[]{true,false,false,false,false,false};
  selectedItem = new int[]{0,0,0,0,0,0};
  complexLimits = new int[]{0,0,0,0,0,0};
  loadComplex(modelName, "BARYCENTRIC");
  try{
   textureMap = createComplexUVMap(); 
   curvatureForm = createCurvatureForm();
   curvatureLookUpTable = curvatureForm.createLookUpTable(myComplex);
   println("curvature form size: "+curvatureLookUpTable.keySet().size());
  }catch(DEC_Exception ex){
    println("something went wrong trying to create the complex texture map");
    ex.printStackTrace();
  }
 }
 public void mouseDragged(){
  rotX += (mouseX-pmouseX)*0.01f;
  rotY -= (mouseY-pmouseY)*0.01f;
 }
 public void keyPressed(){
  if(key == '8'){
   loadComplex(modelName, "BARYCENTRIC");
  }
  if(key == '9'){
   loadComplex(modelName, "INCENTRIC");
  }
  if(key == '0'){
   loadComplex(modelName, "CIRCUMCENTRIC");
  }
  if(key=='z' || key=='Z'){
   saveFrame(modelName+"_pruebaDEC.png");
  }
  //-----------------------------------------------------------------
  //----------------MOTION CONTROL-----------------------------------
  //-----------------------------------------------------------------
  if(key =='a' || key == 'A'){
   xTranslation -= 10;
  }
  if(key =='d' || key == 'D'){
   xTranslation +=10;
  }
  if(key == 'w' || key == 'W'){
   yTranslation -=10;
  }
  if(key =='s' || key == 'S'){
   yTranslation +=10;
  }
  //-----------------------------------------------------------------
  //----------------MODEL CONTROL-----------------------------------
  //-----------------------------------------------------------------
  char[] showComplexKeyOptions = new char[]{'1','2','3','4','5','6'};
  for(int i=0;i<showComplexKeyOptions.length;i++){
   if(key==showComplexKeyOptions[i]){
    if(!showComplexOption[i]){
     showComplexOption[i] = true;
    }else{
     showComplexOption[i] = false;
    }
   }
  }
  //-----------------------------------------------------------------
  //--------------SELECTION TYPE-------------------------------------
  //-----------------------------------------------------------------
  int[] selectionKeyOptions = new int[]{KeyEvent.VK_F1,KeyEvent.VK_F2,KeyEvent.VK_F3,
                                       KeyEvent.VK_F4,KeyEvent.VK_F5,KeyEvent.VK_F6};
  for(int i=0;i<showComplexKeyOptions.length;i++){
   if(keyCode == selectionKeyOptions[i]){
    selectionType[i] = true;
    for(int j=0;j<selectionType.length;j++){
     if(i!=j){
      selectionType[j] = false;
     }
    }
   }
  }
  if(keyCode == LEFT){
   for(int i=0;i<selectedItem.length;i++){
    if(selectionType[i]){
     if(selectedItem[i]>0){
      selectedItem[i]--;
     }
    }
   }
   numElement--;
   if(numElement <0){
    numElement = myComplex.numDualEdges()-1;
   }
  }
  if(keyCode == RIGHT){
   for(int i=0;i<selectedItem.length;i++){
    if(selectionType[i]){
     if(selectedItem[i]<complexLimits[i]){
      selectedItem[i]++;
     }
    }
   }
   numElement++;
   if(numElement> myComplex.numDualEdges()-1){
    numElement = 0;
   }
  }
  //-----------------------------------------------------------------------
  if(key == 't' || key == 'T'){
   if(!show2DInformation){
    show2DInformation = true;
   }else{
    show2DInformation = false;
   }
  }
 }
 public void mouseWheel(MouseEvent e){
  zTranslation += 10*e.getCount();
 }
 public void loadComplex(String fileName, String centerType){
  myViewer = new MeshViewer(this);
  myReader = new OBJMeshReader(fileName, this);
  startTime = System.currentTimeMillis();
  System.out.println("------------------------------------------------------");
  System.out.println("-------OBJ model loading using OBJMeshReader----------");
  System.out.println("------------------------------------------------------");
  myReader.loadModel(centerType);
  myViewer.setModelScale(100, myReader.getModelBoundingBox());
  endTime = System.currentTimeMillis();
  System.out.println(" OBJ loading finished. Elapsed time: "+(endTime-startTime));
  System.out.println("------------------------------------------------------");
  myContainer = new DEC_GeometricContainer();
  System.out.println("------------------------------------------------------");
  System.out.println("---Geometric Container creation from OBJMeshReader-----");
  startTime = System.currentTimeMillis();
  myContainer.setContent(myReader);
  endTime = System.currentTimeMillis();
  System.out.println("------------------------------------------------------");
  myContainer.printContainerInfo();
  System.out.println("------------------------------------------------------");
  System.out.println(" Geometric Container finished. Elapsed time: "+(endTime-startTime));
  System.out.println("------------------------------------------------------");
  try{
   System.out.println("------------------------------------------------------"); 
   myComplex = new DEC_Complex(); 
   startTime = System.currentTimeMillis();
   System.out.println("---DEC Complex creation from Geometric Container------");
   myComplex.setComplex(myContainer,myReader);
   endTime = System.currentTimeMillis();
   myComplex.printComplexInformation();
   System.out.println("DEC Complex creation finished. Elapsed time: "+(endTime-startTime));
   System.out.println("------------------------------------------------------");
   complexLimits = new int[]{myComplex.numPrimalVertices(),myComplex.numPrimalEdges(),myComplex.numPrimalFaces(),
                             myComplex.numDualVertices(),myComplex.numDualEdges(),myComplex.numDualFaces()};
  }catch(DEC_Exception ex){
   System.out.println("something went wrong trying to create Complex");
  }
 }
 public void loadComplex(String fileName){
  myViewer = new MeshViewer(this);
  myReader = new OBJMeshReader(fileName, this);
  startTime = System.currentTimeMillis();
  System.out.println("------------------------------------------------------");
  System.out.println("-------OBJ model loading using OBJMeshReader----------");
  System.out.println("------------------------------------------------------");
  myReader.loadModel();
  myViewer.setModelScale(100, myReader.getModelBoundingBox());
  endTime = System.currentTimeMillis();
  System.out.println(" OBJ loading finished. Elapsed time: "+(endTime-startTime));
  System.out.println("------------------------------------------------------");
  myContainer = new DEC_GeometricContainer();
  System.out.println("------------------------------------------------------");
  System.out.println("---Geometric Container creation from OBJMeshReader-----");
  startTime = System.currentTimeMillis();
  myContainer.setContent(myReader);
  endTime = System.currentTimeMillis();
  System.out.println("------------------------------------------------------");
  myContainer.printContainerInfo();
  System.out.println("------------------------------------------------------");
  System.out.println(" Geometric Container finished. Elapsed time: "+(endTime-startTime));
  System.out.println("------------------------------------------------------");
  try{
   System.out.println("------------------------------------------------------"); 
   myComplex = new DEC_Complex(); 
   startTime = System.currentTimeMillis();
   System.out.println("---DEC Complex creation from Geometric Container------");
   myComplex.setComplex(myContainer,myReader);
   endTime = System.currentTimeMillis();
   myComplex.printComplexInformation();
   System.out.println("DEC Complex creation finished. Elapsed time: "+(endTime-startTime));
   System.out.println("------------------------------------------------------");
   complexLimits = new int[]{myComplex.numPrimalVertices(),myComplex.numPrimalEdges(),myComplex.numPrimalFaces(),
                             myComplex.numDualVertices(),myComplex.numDualEdges(),myComplex.numDualFaces()};
  }catch(DEC_Exception ex){
   System.out.println("something went wrong trying to create Complex");
  }
 }
 public DiscreteForm createCurvatureForm(){
  DEC_Iterator iter = myComplex.createIterator(0,'p');
  DiscreteForm curvatureForm = new DiscreteForm(myComplex.numPrimalVertices());
  while(iter.hasNext()){
   DEC_PrimalObject v = (DEC_PrimalObject) iter.next();
   try{
    float angleSum = 0;
    PVector vertexCenter = v.getVectorContent(0);
    PVector vertexNormal = v.getVectorContent(1);
    DEC_Iterator neighborhood = myComplex.objectNeighborhood(v);
    String neighbors ="";
    ArrayList<PVector> neighCenters = new ArrayList<PVector>();
    while(neighborhood.hasNext()){
     DEC_PrimalObject neighVertex = (DEC_PrimalObject) neighborhood.next();
     neighCenters.add(neighVertex.getVectorContent(0));
    }
    for(int i=0;i<neighCenters.size();i++){
     PVector A = neighCenters.get(i);
     PVector B = neighCenters.get((i+1)%neighCenters.size());
     double angle = GeometricUtils.angleBetweenVectors(vertexCenter, vertexNormal, A, B);
     angleSum+=angle;
    }
    println(degrees(angleSum));
    curvatureForm.put(v, 2*Math.PI-angleSum);
   }catch(DEC_Exception ex){
    println("something went wrong trying to calculate angle defect");
   }
  }
  return curvatureForm;
 }
 public PGraphics createComplexUVMap() throws DEC_Exception{
  PGraphics uvSpace = createGraphics(width/2,height,P2D);
  DEC_Iterator iter = myComplex.createIterator(2,'p');
  uvSpace.beginDraw();
  uvSpace.colorMode(HSB);
  uvSpace.background(0);
  uvSpace.stroke(255);
  while(iter.hasNext()){
   DEC_PrimalObject face = (DEC_PrimalObject) iter.next();
   //vector content 5,6,7
   PVector[] uvs = new PVector[]{face.getVectorContent(5),face.getVectorContent(6),face.getVectorContent(7)};
   float u = face.getIndex() / (float) myComplex.numPrimalFaces();
   uvSpace.fill(255*u,255,255);
   uvSpace.beginShape();
    for(int i=0;i<uvs.length;i++){
     uvSpace.vertex(uvs[i].x*uvSpace.width,uvs[i].y*uvSpace.height);
    }
   uvSpace.endShape(PApplet.CLOSE);
  }
  uvSpace.endDraw();
  return uvSpace;
 }
 public void drawComplex(int dimension, char type){
  DEC_Iterator iter = myComplex.createIterator(dimension, type);
  while(iter.hasNext()){
   try{
    if(type == 'p'){
     DEC_PrimalObject op = (DEC_PrimalObject) iter.next();
     myViewer.drawObject(op, myContainer,false);
    }else{
     DEC_DualObject od = (DEC_DualObject) iter.next();
     myViewer.drawObject(od,myContainer,false);
    }
   }catch(DEC_Exception ex){
     System.out.println("something went wrong plotting object");
   }
  }
 }
 public void showNeighborhood(int numElement){
  try{
   DEC_PrimalObject selectedObject = myComplex.getPrimalObject(0, numElement);
   DEC_Iterator neighborhoodIterator = myComplex.objectNeighborhood(selectedObject);
   fill(255,255,255);
   myViewer.drawObject(selectedObject, myContainer, true);
   while(neighborhoodIterator.hasNext()){
    fill(120,255,255);
    myViewer.drawObject(neighborhoodIterator.next(), myContainer, true);
   }
  }catch(DEC_Exception exception){
    System.out.println("something went wrong trying to draw neighborhood: ");
    exception.printStackTrace();
  }
 }
 public void showComplex(){
  if(showComplexOption[2]){
   fill(200);
   drawComplex(2,'p'); 
  }
  if(showComplexOption[5]){
   fill(200);
   drawComplex(2,'d');
  }
  if(showComplexOption[1]){
   stroke(0);
   strokeWeight(1);
   drawComplex(1,'p');
  }
  if(showComplexOption[4]){
   stroke(0);
   strokeWeight(1);
   drawComplex(1,'d');
  }
  if(showComplexOption[0]){
   fill(255,255,255);
   drawComplex(0,'p');
  }
  if(showComplexOption[3]){
   fill(100,255,255);
   drawComplex(0,'d');
  }
 }
 public void drawContent(){
  //myViewer.strokeWeight(1);
  //myViewer.drawBoundingBox();
  showComplex();
  drawCurvatureForm();
  //showNeighborhood(numElement);
 }
 public void drawCurvatureForm(){
  DEC_Iterator iter = myComplex.createIterator(2, 'p');
  noStroke();
  while(iter.hasNext()){
   DEC_Object face = (DEC_PrimalObject) iter.next();
   try{
    ArrayList<PVector> verts = myContainer.getGeometricContent(face);
    ArrayList<Double> hueValues = new ArrayList<Double>();
    for(int i=0;i<verts.size();i++){
     hueValues.add(curvatureLookUpTable.get(verts.get(i)));
    }
    beginShape();
    for(int i=0;i<verts.size();i++){
     PVector w = myViewer.scalePVector(verts.get(i));
     fill(255*hueValues.get(i).floatValue(),255,255);
     vertex(w.x,w.y,w.z);
    }
    endShape(CLOSE);
   }catch(DEC_Exception ex){
     println("cannot draw form");
   }
  }
 }
 public void draw(){
  background(255);
  if(show2DInformation){
   if(textureMap!=null){
    image(textureMap,0,0,width/2,height);
    fill(255);
    text("complex UV coordinates",10,10);
   }
  }else{
   lights();
   translate(width/2,height/2,zTranslation);
   rotateX(rotY);
   rotateY(rotX);
   pushMatrix();
    translate(0,yTranslation,0);
    pushMatrix();
     translate(xTranslation,0,0);
     drawContent();
    popMatrix();
   popMatrix();
  }
 }
 /**
  * @param args the command line arguments
  */
 public static void main(String[] args) {
  // TODO code application logic here
  PApplet.main(new String[]{dec_april01_2016.DEC_April01_2016.class.getName()});
 }
 
}
