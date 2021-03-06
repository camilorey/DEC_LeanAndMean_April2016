/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package viewers;

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
import processing.core.PImage;
import processing.core.PVector;
import saito.objloader.BoundingBox;
import utils.GeometricUtils;

/**
 *
 * @author Camilo Rey
 */
public class MeshViewer {
 protected PApplet parent;
 protected float[] modelWHD;
 protected int[] fillColor;
 protected int[] strokeColor;
 protected int strokeWeight;
 protected float primalVertexSize;
 protected float dualVertexSize;
 
 public MeshViewer(){
  this.parent = null;
  this.modelWHD = null;
  this.fillColor = null;
  this.strokeColor = null;
  this.strokeWeight = 1;
  this.primalVertexSize = 5;
  this.dualVertexSize = 3;
 }
 public MeshViewer(PApplet parent){
  this.parent = parent;
  this.modelWHD = new float[]{parent.width/2,parent.height/2,parent.height/2};
  this.fillColor = new int[]{150,150,150,0};
  this.strokeColor = new int[]{0,0,0};
  this.strokeWeight = 1;
  this.primalVertexSize = 5;
  this.dualVertexSize = 3;
 }
 public void fill(int r, int g, int b){
  this.fillColor = new int[]{r,g,b,0};
  parent.fill(r,g,b);
 }
 public void stroke(int r, int g, int b){
  this.strokeColor = new int[]{r,g,b};
  parent.stroke(r,g,b);
 }
 public void strokeWeight(int strokeWeight){
  strokeWeight = strokeWeight;
  parent.strokeWeight(strokeWeight);
 }
 public void setModelSize(float w, float h, float d){
  this.modelWHD = new float[]{w/2,h/2,d/2};
 }
 public void setModelScale(float scale, BoundingBox bbox){
  this.modelWHD = new float[]{bbox.getWHD().x*scale,bbox.getWHD().y*scale,bbox.getWHD().z*scale};
 }
 public void setPrimalVertexSize(float primalVertexSize) {
  this.primalVertexSize = primalVertexSize;
 }
 public void setDualVertexSize(float dualVertexSize) {
  this.dualVertexSize = dualVertexSize;
 }
 public float getPrimalVertexSize() {
  return primalVertexSize;
 }
 public float getDualVertexSize() {
  return dualVertexSize;
 }
 public PVector scalePVector(PVector p){
  return new PVector(p.x*modelWHD[0],p.y*modelWHD[1],p.z*modelWHD[2]);
 }
 public void plotBoundingBox(){
  parent.noFill();
  parent.stroke(0);
  parent.box(2*modelWHD[0],2*modelWHD[1],2*modelWHD[2]);
 }
 public void plotComplex(DEC_Complex complex, DEC_GeometricContainer container, int dimension, char type){
  DEC_Iterator iter = complex.createIterator(dimension, type);
  while(iter.hasNext()){
   try{
    if(type == 'p'){
     DEC_PrimalObject op = (DEC_PrimalObject) iter.next();
     drawObject(op, container,false);
    }else{
     DEC_DualObject od = (DEC_DualObject) iter.next();
     drawObject(od,container,false);
    }
   }catch(DEC_Exception ex){
     System.out.println("something went wrong plotting complex");
     ex.printStackTrace();
   }
  }
 }
 public void plotComplex(DEC_Complex complex,DEC_GeometricContainer container,int dimension, char type, PImage textureImage){
  DEC_Iterator iter = complex.createIterator(dimension, type);
  while(iter.hasNext()){
   try{
    if(type == 'p'){
     DEC_PrimalObject op = (DEC_PrimalObject) iter.next();
     if(op.dimension()==2){
      drawObject(op, container, false, textureImage);
     }else{
      drawObject(op, container,false);
     }
    }else{
     DEC_DualObject od = (DEC_DualObject) iter.next();
     drawObject(od,container,false);
    }
   }catch(DEC_Exception ex){
     System.out.println("something went wrong plotting complex with texture");
     ex.printStackTrace();
   }
  }
 }
 public void plotComplex(DEC_Complex complex, DEC_GeometricContainer container, int dimension, char type, ScalarField field, boolean interpolating){
  DEC_Iterator iter = complex.createIterator(dimension, type);
  while(iter.hasNext()){
   try{
    if(type == 'p'){
     DEC_PrimalObject op = (DEC_PrimalObject) iter.next();
     drawObject(op, container, false,field,interpolating);
    }else{
     DEC_DualObject od = (DEC_DualObject) iter.next();
     drawObject(od, container, false,field,interpolating);
    }
   }catch(DEC_Exception ex){
     System.out.println("something went wrong plotting scalar field over complex");
     ex.printStackTrace();
   }
  }
 }
 public void plotComplex(DEC_Complex complex, DEC_GeometricContainer container, int dimension, char type, VectorField vectorField, float arrowLength){
  DEC_Iterator iter = complex.createIterator(dimension, type);
  while(iter.hasNext()){
   try{
    if(type == 'p'){
     DEC_PrimalObject op = (DEC_PrimalObject) iter.next();
     if(op.dimension()==vectorField.getDimension() && vectorField.getType()=='p'){
      PVector fieldMag = vectorField.getDirections().get(op);
      parent.stroke(255*vectorField.getMagnitudeLookUpTable().get(op.getVectorContent("CENTER")),255,255);
      drawLineOverObject(op, fieldMag, arrowLength);
     }
    }else{
     DEC_DualObject od = (DEC_DualObject) iter.next();
     if(od.dimension()==vectorField.getDimension() && vectorField.getType()=='d'){
      PVector fieldMag = vectorField.getDirections().get(od);
      parent.stroke(255*vectorField.getMagnitudeLookUpTable().get(od.getVectorContent("CENTER")),255,255);
      drawLineOverObject(od, fieldMag, arrowLength);
     }
    }
   }catch(DEC_Exception ex){
     System.out.println("something went wrong plotting vector field over complex");
     ex.printStackTrace();
   }
  }
 }
 public void drawObject(DEC_Object object, DEC_GeometricContainer container, boolean selected)throws DEC_Exception{
  ArrayList<PVector> verts = container.getGeometricContent(object);
  if(verts.size() == 1){
   if(object instanceof DEC_PrimalObject){
    drawVertex(verts.get(0),'p',selected || object.isBorder());
   }else if(object instanceof DEC_DualObject){
    drawVertex(verts.get(0),'d',selected || object.isBorder());
   }
  }else if(verts.size() == 2){
   if(object instanceof DEC_PrimalObject){
    drawPrimalEdge(verts.get(0),verts.get(1),selected||object.isBorder());
   }else if(object instanceof DEC_DualObject){
    drawDualEdge(verts.get(0),verts.get(1),object.getVectorContent("CENTER"),selected || object.isBorder());
   }
  }else{
   if(object instanceof DEC_PrimalObject){
    ArrayList<PVector> faceNormals = new ArrayList<PVector>();
    faceNormals.add(object.getVectorContent("NORMAL_1"));
    faceNormals.add(object.getVectorContent("NORMAL_2"));
    faceNormals.add(object.getVectorContent("NORMAL_3"));
    drawPrimalFace(verts, faceNormals,selected);
   }else if(object instanceof DEC_DualObject){
    PVector faceCenter = object.getVectorContent("CENTER");
    PVector faceNormal = object.getVectorContent("NORMAL_0");
    if(((DEC_DualObject)object).getExtraGeometricContent()!= null){
     drawDualFace(((DEC_DualObject)object).getExtraGeometricContent(), faceCenter,selected || object.isBorder());
    }else{
     drawDualFace(verts, faceCenter,selected || object.isBorder());
    }
   }
  }
 }
 public void drawObject(DEC_Object object, DEC_GeometricContainer container, boolean selected, PImage textureImage)throws DEC_Exception{
  ArrayList<PVector> verts = container.getGeometricContent(object);
  if(verts.size() == 1){
   if(object instanceof DEC_PrimalObject){
    drawVertex(verts.get(0),'p',selected || object.isBorder());
   }else if(object instanceof DEC_DualObject){
    drawVertex(verts.get(0),'d',selected || object.isBorder());
   }
  }else if(verts.size() == 2){
   if(object instanceof DEC_PrimalObject){
    drawPrimalEdge(verts.get(0),verts.get(1),selected||object.isBorder());
   }else if(object instanceof DEC_DualObject){
    drawDualEdge(verts.get(0),verts.get(1),object.getVectorContent(0),selected || object.isBorder());
   }
  }else{
   if(object instanceof DEC_PrimalObject){
    ArrayList<PVector> faceNormals = new ArrayList<PVector>();
    ArrayList<PVector> faceTexels = new ArrayList<PVector>();
    faceNormals.add(object.getVectorContent("NORMAL_1"));
    faceNormals.add(object.getVectorContent("NORMAL_2"));
    faceNormals.add(object.getVectorContent("NORMAL_3"));
    faceTexels.add(object.getVectorContent("UV_0"));
    faceTexels.add(object.getVectorContent("UV_1"));
    faceTexels.add(object.getVectorContent("UV_2"));
    drawPrimalFace(textureImage, verts, faceNormals, faceTexels, selected || object.isBorder());
   }else if(object instanceof DEC_DualObject){
    PVector faceCenter = object.getVectorContent("CENTER");
    PVector faceNormal = object.getVectorContent("NORMAL_0");
    if(((DEC_DualObject)object).getExtraGeometricContent()!= null){
     drawDualFace(((DEC_DualObject)object).getExtraGeometricContent(), faceCenter,selected || object.isBorder());
    }else{
     drawDualFace(verts, faceCenter,selected || object.isBorder());
    }
   }
  }
 }
 public void drawObject(DEC_Object object, DEC_GeometricContainer container, boolean selected, ScalarField field, boolean interpolating) throws DEC_Exception {
  ArrayList<PVector> verts = container.getGeometricContent(object);
  if (verts.size() == 1) {
   if (object instanceof DEC_PrimalObject) {
    if (object.dimension() == field.getDimension() && field.getType() == 'p') {
     parent.fill(255*field.getValueLookUpTable().get(object.getVectorContent("CENTER")),255,255);
    } else {
     parent.fill(0);
    }
    drawVertex(verts.get(0), 'p', selected || object.isBorder());
   }else if (object instanceof DEC_DualObject) {
    if (object.dimension() == field.getDimension() && field.getType() == 'd') {
     parent.fill(255*field.getValueLookUpTable().get(object.getVectorContent("CENTER")),255,255);
    } else {
     parent.fill(0);
    }
    drawVertex(verts.get(0), 'd', selected || object.isBorder());
   }
  } else if (verts.size() == 2) {
   if (object instanceof DEC_PrimalObject) {
    if (object.dimension() == field.getDimension() && field.getType() == 'p') {
     parent.stroke(255*field.getValueLookUpTable().get(object.getVectorContent("CENTER")),255,255);
    } else {
     parent.stroke(0);
    }
    drawPrimalEdge(verts.get(0), verts.get(1), selected || object.isBorder());
   } else if (object instanceof DEC_DualObject) {
    if (object.dimension() == field.getDimension() && field.getType() == 'd') {
     parent.stroke(255*field.getValueLookUpTable().get(object.getVectorContent("CENTER")),255,255);
    }else {
     parent.stroke(0);
    }
    drawDualEdge(verts.get(0), verts.get(1), object.getVectorContent("CENTER"), selected || object.isBorder());
   }
  } else if (object instanceof DEC_PrimalObject) {
   ArrayList<PVector> faceNormals = new ArrayList<PVector>();
   faceNormals.add(object.getVectorContent("NORMAL_1"));
   faceNormals.add(object.getVectorContent("NORMAL_2"));
   faceNormals.add(object.getVectorContent("NORMAL_3"));
   if (object.dimension() == field.getDimension() && field.getType() == 'p') {
    if (interpolating) {
     ArrayList<Float> hues = new ArrayList<Float>();
     for (int i = 0; i < verts.size(); i++) {
      float vertHue = field.getVertexLookUpTable().get(verts.get(i)).floatValue();
      hues.add(vertHue);
     }
     drawPrimalFace(verts, faceNormals, selected, hues);
    }else {
      parent.fill(255*field.getValueLookUpTable().get(object.getVectorContent("CENTER")),255,255);
      drawPrimalFace(verts, faceNormals, selected);
     }
   }else {
    parent.fill(150);
    drawPrimalFace(verts, faceNormals, selected);
   }
  }else if (object instanceof DEC_DualObject) {
   PVector faceCenter = object.getVectorContent("CENTER");
   PVector faceNormal = object.getVectorContent("NORMAL_0");
   if(field.getDimension()==object.dimension() && field.getType()=='d'){
    parent.fill(255*field.getValueLookUpTable().get(faceCenter).floatValue(),255,255);
   }else{
    parent.fill(150);
   }
   if (((DEC_DualObject) object).getExtraGeometricContent() != null) {
   drawDualFace(((DEC_DualObject)object).getExtraGeometricContent(), faceCenter,selected || object.isBorder());
   } else {
    drawDualFace(verts, faceCenter,selected || object.isBorder());
   }
  }
 }
 public void drawLineOverObject(DEC_Object object, PVector direction, float lineLength) throws DEC_Exception{
  PVector c = object.getVectorContent("CENTER");
  PVector d = PVector.mult(direction,lineLength);
  parent.line(c.x*modelWHD[0], c.y*modelWHD[1], c.z*modelWHD[2], 
              c.x*modelWHD[0]+d.x, c.y*modelWHD[1]+d.y, c.z*modelWHD[2]+d.z);
 }
 public void drawVertex(PVector pos,char type,boolean selected){
  float radius = type =='p'? primalVertexSize:dualVertexSize;
  parent.noStroke();
  parent.pushMatrix();
   parent.translate(pos.x*modelWHD[0],pos.y*modelWHD[1],pos.z*modelWHD[2]);
   if(selected){
    parent.box(3.0f*radius);
   }else{
    parent.box(radius);
   }
  parent.popMatrix();
 }
 public void drawVertex(PVector pos,char type,boolean selected, float hue){
  float radius = type =='p'? primalVertexSize:dualVertexSize;
  parent.noStroke();
  parent.fill(255*hue,255,255);
  parent.pushMatrix();
   parent.translate(pos.x*modelWHD[0],pos.y*modelWHD[1],pos.z*modelWHD[2]);
   if(selected){
    parent.box(3.0f*radius);
   }else{
    parent.box(radius);
   }
  parent.popMatrix();
 }
 public void drawPrimalEdge(PVector p1, PVector p2,boolean selected,float hue){
  if(selected){
   parent.strokeWeight(5);
  }else{
   parent.strokeWeight(1);
  }
  parent.stroke(255*hue,255,255);
  parent.line(p1.x*modelWHD[0],p1.y*modelWHD[1],p1.z*modelWHD[2],
              p2.x*modelWHD[0],p2.y*modelWHD[1],p2.z*modelWHD[2]);
 }
 public void drawDualEdge(PVector p1, PVector p2, PVector c, boolean selected, float hue){
  if(selected){
   parent.strokeWeight(5);
  }else{
   parent.strokeWeight(1);
  }
  parent.stroke(255*hue,255,255);
  parent.line(p1.x*modelWHD[0],p1.y*modelWHD[1],p1.z*modelWHD[2],
              c.x*modelWHD[0],c.y*modelWHD[1],c.z*modelWHD[2]);
  parent.line(c.x*modelWHD[0],c.y*modelWHD[1],c.z*modelWHD[2],
              p2.x*modelWHD[0],p2.y*modelWHD[1],p2.z*modelWHD[2]);
 }
 public void drawPrimalEdge(PVector p1, PVector p2,boolean selected){
  if(selected){
   parent.strokeWeight(5);
  }else{
   parent.strokeWeight(1);
  }
  parent.line(p1.x*modelWHD[0],p1.y*modelWHD[1],p1.z*modelWHD[2],
              p2.x*modelWHD[0],p2.y*modelWHD[1],p2.z*modelWHD[2]);
 }
 public void drawDualEdge(PVector p1, PVector p2, PVector c, boolean selected){
  if(selected){
   parent.strokeWeight(5);
  }else{
   parent.strokeWeight(1);
  }
  parent.line(p1.x*modelWHD[0],p1.y*modelWHD[1],p1.z*modelWHD[2],
              c.x*modelWHD[0],c.y*modelWHD[1],c.z*modelWHD[2]);
  parent.line(c.x*modelWHD[0],c.y*modelWHD[1],c.z*modelWHD[2],
              p2.x*modelWHD[0],p2.y*modelWHD[1],p2.z*modelWHD[2]);
 }
 public void drawVector(PVector pos, PVector vec, float arrowLength){
  PVector w = vec.normalize(null);
  w.mult(arrowLength);
  parent.line(pos.x*modelWHD[0],pos.y*modelWHD[1],pos.z*modelWHD[2],
              pos.x*modelWHD[0]+w.x,pos.y*modelWHD[1]+w.y,pos.z*modelWHD[2]+w.z);
 }
 public void drawPrimalFace(ArrayList<PVector> verts, ArrayList<PVector> normals, boolean selected){
  if(selected){
   parent.strokeWeight(3);
   parent.stroke(255,255,255);
  }else{
   parent.noStroke();
  }
  parent.beginShape();
  for(int i=0;i<verts.size();i++){
   parent.normal(normals.get(i).x,normals.get(i).y,normals.get(i).z);
   parent.vertex(verts.get(i).x*modelWHD[0],verts.get(i).y*modelWHD[1],verts.get(i).z*modelWHD[2]);
  }
  parent.endShape(PApplet.CLOSE);
 }
 public void drawPrimalFace(ArrayList<PVector> verts, ArrayList<PVector> normals, boolean selected, float hue){
  if(selected){
   parent.strokeWeight(3);
   parent.stroke(255,255,255);
  }else{
   parent.noStroke();
  }
  parent.fill(255*hue,255,255);
  parent.beginShape();
  for(int i=0;i<verts.size();i++){
   parent.normal(normals.get(i).x,normals.get(i).y,normals.get(i).z);
   parent.vertex(verts.get(i).x*modelWHD[0],verts.get(i).y*modelWHD[1],verts.get(i).z*modelWHD[2]);
  }
  parent.endShape(PApplet.CLOSE);
 }
 public void drawPrimalFace(ArrayList<PVector> verts, ArrayList<PVector> normals, boolean selected, ArrayList<Float> hue){
  if(selected){
   parent.strokeWeight(3);
   parent.stroke(255,255,255);
  }else{
   parent.noStroke();
  }
  parent.beginShape();
  for(int i=0;i<verts.size();i++){
   parent.fill(255*hue.get(i).floatValue(),255,255);
   parent.normal(normals.get(i).x,normals.get(i).y,normals.get(i).z);
   parent.vertex(verts.get(i).x*modelWHD[0],verts.get(i).y*modelWHD[1],verts.get(i).z*modelWHD[2]);
  }
  parent.endShape(PApplet.CLOSE);
 }
 public void drawPrimalFace(PImage textureImage, ArrayList<PVector> verts, ArrayList<PVector> normals, ArrayList<PVector> texels, boolean selected){
  if(selected){
   parent.strokeWeight(3);
   parent.stroke(255,255,255);
  }else{
   parent.noStroke();
  }
  parent.beginShape();
  parent.texture(textureImage);
  for(int i=0;i<verts.size();i++){
   parent.normal(normals.get(i).x,normals.get(i).y,normals.get(i).z);
   parent.vertex(verts.get(i).x*modelWHD[0],verts.get(i).y*modelWHD[1],verts.get(i).z*modelWHD[2],texels.get(i).x,texels.get(i).y);
  }
  parent.endShape(PApplet.CLOSE);
 }
 public void drawDualFace(ArrayList<PVector> verts, PVector faceCenter,boolean selected){
  if(verts.size()>0){
   int N = verts.size();
   PVector c = new PVector(verts.get(0).x*modelWHD[0],verts.get(0).y*modelWHD[1],verts.get(0).z*modelWHD[2]);
   for(int i=1;i<N;i++){ 
    float u = (float) i / (float) N;
    PVector v1 = new PVector(verts.get(i).x*modelWHD[0],verts.get(i).y*modelWHD[1],verts.get(i).z*modelWHD[2]);
    PVector v2 = null;
    if(i<N-1){
     v2 = new PVector(verts.get(i+1).x*modelWHD[0],verts.get(i+1).y*modelWHD[1],verts.get(i+1).z*modelWHD[2]);
    }else{
     v2 = new PVector(verts.get(1).x*modelWHD[0],verts.get(1).y*modelWHD[1],verts.get(1).z*modelWHD[2]);
    }
    parent.stroke(0);
    parent.strokeWeight(4);
    parent.line(v1.x,v1.y,v1.z,v2.x,v2.y,v2.z);
    parent.strokeWeight(1);
    parent.line(c.x,c.y,c.z,v1.x,v1.y,v1.z);
    parent.line(c.x,c.y,c.z,v2.x,v2.y,v2.z);
   }
   if(selected){
    parent.noFill();
    parent.stroke(255,255,255);
    parent.strokeWeight(3);
    parent.beginShape();
    for(int i=1;i<N;i++){
     PVector v1 = new PVector(verts.get(i).x*modelWHD[0],verts.get(i).y*modelWHD[1],verts.get(i).z*modelWHD[2]);
     parent.vertex(v1.x,v1.y,v1.z);
    }
    parent.endShape(PApplet.CLOSE);
   }
  }
 }
}
