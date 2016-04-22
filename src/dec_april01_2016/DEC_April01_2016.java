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
import fieldsAndForms.ScalarField;
import fieldsAndForms.VectorField;
import java.util.ArrayList;
import java.util.HashMap;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import processing.event.MouseEvent;
import readers.OBJMeshReader;
import viewers.MeshViewer;
import utils.GeometricUtils;
import utils.SparseVector;

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
 //String modelName = "male.obj";
 String modelName = "hand.obj";
 //String modelName = "head.obj";
 //String modelName = "toroTriangulatoTexturizado.obj";
 //String modelName = "toroTrianguladoTexturizado_MasDetalle.obj";
 //String modelName = "bunnyWithNormals.obj";
 boolean withTexture;
 long startTime,endTime;
 OBJMeshReader myReader;
 DEC_GeometricContainer myContainer;
 DEC_Complex myComplex;
 //-----------------------------------------------------------------
 //-------------TEXTURE MAP HANDLER---------------------------------
 //-----------------------------------------------------------------
 PImage texture;
 //-----------------------------------------------------------------
 //----------------FIELDS AND FORMS---------------------------------
 //-----------------------------------------------------------------
 ScalarField myField;
 VectorField myVectorField;
 public void setup(){
  size(1500,800,P3D);
  colorMode(HSB);
  textureMode(NORMAL);
  texture = loadImage("hombre2.png");
  withTexture = false;
  showComplexOption = new boolean[]{true,false,false,false,false,false};
  selectionType = new boolean[]{true,false,false,false,false,false};
  selectedItem = new int[]{0,0,0,0,0,0};
  complexLimits = new int[]{0,0,0,0,0,0};
  loadComplex(modelName, "BARYCENTRIC",withTexture);
  createHodgeStar();
 }
 public void mouseDragged(){
  rotX += (mouseX-pmouseX)*0.01f;
  rotY -= (mouseY-pmouseY)*0.01f;
 }
 public void keyPressed(){
  if(key == '8'){
   loadComplex(modelName, "BARYCENTRIC",withTexture);
  }
  if(key == '9'){
   loadComplex(modelName, "INCENTRIC",withTexture);
  }
  if(key == '0'){
   loadComplex(modelName, "CIRCUMCENTRIC",withTexture);
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
  //-----------------------------------------------------------------------
 }
 public void mouseWheel(MouseEvent e){
  zTranslation += 10*e.getCount();
 }
 public void loadComplex(String fileName, String centerType,boolean withTexture){
  myViewer = new MeshViewer(this);
  myReader = new OBJMeshReader(fileName, this);
  startTime = System.currentTimeMillis();
  System.out.println("------------------------------------------------------");
  System.out.println("-------OBJ model loading using OBJMeshReader----------");
  System.out.println("------------------------------------------------------");
  myReader.loadModel(centerType,withTexture);
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
   System.out.println("Euler Characteristic: "+(myComplex.numPrimalVertices()-myComplex.numPrimalEdges()+myComplex.numPrimalFaces()));
  }catch(DEC_Exception ex){
   System.out.println("something went wrong trying to create Complex");
  }
 }
 public void createHodgeStar(){
  SparseVector vertexHodge = new SparseVector(myComplex.numPrimalEdges());
  DEC_Iterator edgeIterator = myComplex.createIterator(1, 'p');
  while(edgeIterator.hasNext()){
   DEC_PrimalObject primalEdge = (DEC_PrimalObject) edgeIterator.next();
   DEC_DualObject dualEdge = myComplex.dual(primalEdge);
   try{
    float volPrimal = primalEdge.volume(myContainer);
    float volDual = dualEdge.volume(myContainer);
    float hodgeContent = volDual/volPrimal;
    println(primalEdge.getIndex()+":"+hodgeContent);
    vertexHodge.set(primalEdge.getIndex(), hodgeContent);
   }catch(DEC_Exception ex){
    println("something went wrong trying to calculate volumes");
    ex.printStackTrace();
   }
  }
 }
 public void showComplex(){
  if(showComplexOption[2]){
   fill(200);
   myViewer.plotComplex(myComplex, myContainer, 2, 'p');
   //myViewer.plotComplex(myComplex, myContainer, 2, 'p',myField, true);
   //myViewer.plotComplex(myComplex, myContainer, 2, 'p', myVectorField, 20);
  }
  if(showComplexOption[5]){
   fill(200);
   myViewer.plotComplex(myComplex, myContainer, 2, 'd');
   //myViewer.plotComplex(myComplex, myContainer, 2, 'd',myField, false);
   //myViewer.plotComplex(myComplex, myContainer, 2, 'd', myVectorField, 20);
  }
  if(showComplexOption[1]){
   stroke(0);
   strokeWeight(1);
   myViewer.plotComplex(myComplex, myContainer, 1, 'p');
   //myViewer.plotComplex(myComplex, myContainer, 1, 'p',myField, false);
   //myViewer.plotComplex(myComplex, myContainer, 1, 'p', myVectorField, 20);
  }
  if(showComplexOption[4]){
   stroke(0);
   strokeWeight(1);
   myViewer.plotComplex(myComplex, myContainer, 1, 'd');
   //myViewer.plotComplex(myComplex, myContainer, 1, 'd',myField, false);
   //myViewer.plotComplex(myComplex, myContainer, 1, 'd', myVectorField, 20);
  }
  if(showComplexOption[0]){
   fill(255,255,255);
   myViewer.plotComplex(myComplex, myContainer, 0, 'p');
   //myViewer.plotComplex(myComplex, myContainer, 0, 'p',myField, false);
   //myViewer.plotComplex(myComplex, myContainer, 0, 'p', myVectorField, 20);
  }
  if(showComplexOption[3]){
   fill(100,255,255);
   myViewer.plotComplex(myComplex, myContainer, 0, 'd');
   //myViewer.plotComplex(myComplex, myContainer, 0, 'd',myField, false);
   //myViewer.plotComplex(myComplex, myContainer, 0, 'd', myVectorField, 20);
  }
 }
 public void drawContent(){
  myViewer.strokeWeight(1);
  myViewer.plotBoundingBox();
  showComplex();
  //drawCurvatureForm();
  //showNeighborhood(numElement);
 }
 public void draw(){
  background(255);
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
 /**
  * @param args the command line arguments
  */
 public static void main(String[] args) {
  // TODO code application logic here
  PApplet.main(new String[]{dec_april01_2016.DEC_April01_2016.class.getName()});
 }
 
}
