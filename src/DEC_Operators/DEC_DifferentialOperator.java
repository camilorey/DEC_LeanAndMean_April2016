/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DEC_Operators;

import complex.DEC_Complex;
import complex.DEC_DualObject;
import complex.DEC_Object;
import complex.DEC_PrimalObject;
import exceptions.DEC_Exception;
import java.util.ArrayList;
import utils.SparseMatrix;
import utils.SparseVector;

/**
 *
 * @author laptop
 */
public class DEC_DifferentialOperator {
 
 protected SparseMatrix differentialMatrix;
 
 public DEC_DifferentialOperator() {
  differentialMatrix = new SparseMatrix();
 }
 public void calculateOperator(DEC_Complex complex,int dimension, char type) throws DEC_Exception{
  SparseMatrix boundaryMatrix; 
  if(dimension == 1 && type == 'p'){
   boundaryMatrix = new SparseMatrix(complex.numPrimalEdges(),complex.numPrimalVertices());
   for(int i=0;i<complex.numPrimalEdges();i++){
    SparseVector boundAsVector = new SparseVector(complex.numPrimalVertices());
    DEC_PrimalObject edge = complex.getPrimalObject(1, i);
    ArrayList<DEC_Object> edgeBound = edge.boundary();
    for(int j=0;j<edgeBound.size();j++){
     DEC_PrimalObject vert = new DEC_PrimalObject(edgeBound.get(j));
     int index = complex.primalObjectIndexSearch(vert);
     if(index !=-1){
      boundAsVector.set(index, (float) vert.getOrientation());
     }
    }
    boundaryMatrix.setRow(i, boundAsVector);
   }
   differentialMatrix = boundaryMatrix.transpose();
  }else if(dimension == 2 && type =='p'){
   boundaryMatrix = new SparseMatrix(complex.numPrimalFaces(),complex.numPrimalEdges());
   for(int i=0;i<complex.numPrimalFaces();i++){
    SparseVector boundAsVector = new SparseVector(complex.numPrimalEdges());
    DEC_PrimalObject face = complex.getPrimalObject(2, i);
    ArrayList<DEC_Object> faceBound = face.boundary();
    for(int j=0;j<faceBound.size();j++){
     DEC_PrimalObject edge = new DEC_PrimalObject(faceBound.get(j));
     int index = complex.primalObjectIndexSearch(edge);
     if(index != -1){
      boundAsVector.set(index, (float) edge.getOrientation());
     }
    }
    boundaryMatrix.setRow(i, boundAsVector);
   }
   differentialMatrix = boundaryMatrix.transpose();
  }else if(dimension == 1 && type == 'd'){
   boundaryMatrix = new SparseMatrix(complex.numDualEdges(),complex.numDualVertices());
   for(int i=0;i<complex.numDualEdges();i++){
    SparseVector boundAsVector = new SparseVector(complex.numDualVertices());
    DEC_DualObject dualEdge = complex.getDualObject(1, i);
    ArrayList<DEC_Object> edgeBound = dualEdge.boundary();
    for(int j=0;j<edgeBound.size();j++){
     DEC_DualObject vert = new DEC_DualObject(edgeBound.get(j),'v');
     int index = complex.dualObjectIndexSearch(vert);
     if(index !=-1){
      boundAsVector.set(index, vert.getOrientation());
     }
    }
    boundaryMatrix.setRow(i, boundAsVector);
   }
   differentialMatrix = boundaryMatrix.transpose();
  }else if(dimension == 2 && type == 'd'){
   boundaryMatrix = new SparseMatrix(complex.numDualFaces(),complex.numDualEdges());
   for(int i=0;i<complex.numDualFaces();i++){
    SparseVector boundAsVector = new SparseVector(complex.numDualEdges());
    DEC_DualObject dualFace = complex.getDualObject(2, i);
    ArrayList<DEC_Object> faceBound = dualFace.boundary();
    for(int j=0;j<faceBound.size();j++){
     DEC_DualObject edge = new DEC_DualObject(faceBound.get(j),'e');
     int index = complex.dualObjectIndexSearch(edge);
     if(index !=-1){
      boundAsVector.set(index, edge.getOrientation());
     }
    }
    boundaryMatrix.setRow(i, boundAsVector);
   }
   differentialMatrix = boundaryMatrix.transpose();
  }else{
   throw new DEC_Exception("undefined boundary requested");
  }
 }
}
