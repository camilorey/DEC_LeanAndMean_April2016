/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package viewers;

import complex.DEC_DualObject;
import complex.DEC_Object;
import complex.DEC_PrimalObject;
import containers.DEC_GeometricContainer;
import exceptions.DEC_Exception;
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
 public void drawBoundingBox(){
  parent.noFill();
  parent.stroke(0);
  parent.box(2*modelWHD[0],2*modelWHD[1],2*modelWHD[2]);
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
    drawDualEdge(verts.get(0),verts.get(1),object.getVectorContent(0),selected || object.isBorder());
   }
  }else{
   if(object instanceof DEC_PrimalObject){
    ArrayList<PVector> faceNormals = new ArrayList<PVector>();
    faceNormals.add(object.getVectorContent(2));
    faceNormals.add(object.getVectorContent(3));
    faceNormals.add(object.getVectorContent(4));
    drawPrimalFace(verts, faceNormals,selected);
   }else if(object instanceof DEC_DualObject){
    PVector faceCenter = object.getVectorContent(0);
    PVector faceNormal = object.getVectorContent(1);
    ArrayList<PVector> faceNormals = new ArrayList<PVector>();
    for(int i=2;i<object.vectorContentSize();i++){
     faceNormals.add(object.getVectorContent(i));
    }
    if(object.getExtraGeometricContent()!= null){
     //System.out.println(" drawing with extra content");
     drawDualFaceWithExtraContent(verts,object.getExtraGeometricContent() , faceCenter, faceNormal, selected || object.isBorder());
    }else{
     drawDualFace(verts, faceCenter,faceNormal ,faceNormals,selected || object.isBorder());
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
    faceNormals.add(object.getVectorContent(2));
    faceNormals.add(object.getVectorContent(3));
    faceNormals.add(object.getVectorContent(4));
    faceTexels.add(object.getVectorContent(5));
    faceTexels.add(object.getVectorContent(6));
    faceTexels.add(object.getVectorContent(7));
    drawPrimalFace(textureImage, verts, faceNormals, faceTexels, selected || object.isBorder());
   }else if(object instanceof DEC_DualObject){
    PVector faceCenter = object.getVectorContent(0);
    PVector faceNormal = object.getVectorContent(1);
    ArrayList<PVector> faceNormals = new ArrayList<PVector>();
    for(int i=2;i<object.vectorContentSize();i++){
     faceNormals.add(object.getVectorContent(i));
    }
    if(object.getExtraGeometricContent()!= null){
     drawDualFaceWithExtraContent(verts,object.getExtraGeometricContent() , faceCenter, faceNormal, selected || object.isBorder());
    }else{
     drawDualFace(verts, faceCenter,faceNormal ,faceNormals,selected || object.isBorder());
    }
   }
  }
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
 public void drawDualFaceWithExtraContent(ArrayList<PVector> verts,ArrayList<PVector> extra ,PVector faceCenter, PVector faceNormal,boolean selected){
  ArrayList<PVector> totalVerts = new ArrayList<PVector>();
  for(int i=0;i<verts.size();i++){
   totalVerts.add(verts.get(i));
  }
  for(int i=0;i<extra.size();i++){
   totalVerts.add(extra.get(i));
  }
  ArrayList<PVector> sortedTotal = GeometricUtils.sortPoints(totalVerts, faceNormal, faceCenter);
  int M = sortedTotal.size();
  parent.noStroke();
  for(int i=0;i<M;i++){
   PVector c = new PVector(faceCenter.x*modelWHD[0],faceCenter.y*modelWHD[1],faceCenter.z*modelWHD[2]);
   PVector v1 = new PVector(sortedTotal.get(i).x*modelWHD[0],sortedTotal.get(i).y*modelWHD[1],sortedTotal.get(i).z*modelWHD[2]);
   PVector v2 = new PVector(sortedTotal.get((i+1)%M).x*modelWHD[0],sortedTotal.get((i+1)%M).y*modelWHD[1],sortedTotal.get((i+1)%M).z*modelWHD[2]);
   parent.beginShape();  
     parent.vertex(c.x,c.y,c.z);
     parent.vertex(v1.x,v1.y,v1.z);
     parent.vertex(v2.x,v2.y,v2.z);
    parent.endShape(PApplet.CLOSE);
  }
 }
 public void drawDualFace(ArrayList<PVector> verts, PVector faceCenter,PVector faceNormal,ArrayList<PVector> normals,boolean selected){
  if(verts.size()==normals.size()){
   int N = verts.size();
   parent.noStroke();
   for(int i=0;i<N;i++){
    PVector c = new PVector(faceCenter.x*modelWHD[0],faceCenter.y*modelWHD[1],faceCenter.z*modelWHD[2]);
    PVector v1 = new PVector(verts.get(i).x*modelWHD[0],verts.get(i).y*modelWHD[1],verts.get(i).z*modelWHD[2]);
    PVector v2 = new PVector(verts.get((i+1)%N).x*modelWHD[0],verts.get((i+1)%N).y*modelWHD[1],verts.get((i+1)%N).z*modelWHD[2]);
    PVector n = faceNormal;
    PVector n1 = normals.get(i);
    PVector n2 = normals.get((i+1)%N);
    parent.beginShape();
     parent.normal(n.x, n.y, n.z);
     parent.vertex(c.x,c.y,c.z);
     parent.normal(n1.x, n1.y, n1.z);
     parent.vertex(v1.x,v1.y,v1.z);
     parent.normal(n2.x, n2.y, n2.z);
     parent.vertex(v2.x,v2.y,v2.z);
    parent.endShape(PApplet.CLOSE);
   }
   if(selected){
    parent.noFill();
    parent.stroke(255,255,255);
    parent.strokeWeight(3);
    parent.beginShape();
    for(int i=0;i<N;i++){
     PVector v1 = new PVector(verts.get(i).x*modelWHD[0],verts.get(i).y*modelWHD[1],verts.get(i).z*modelWHD[2]);
     parent.vertex(v1.x,v1.y,v1.z);
    }
    parent.endShape(PApplet.CLOSE);
   }
  }
 }
}
