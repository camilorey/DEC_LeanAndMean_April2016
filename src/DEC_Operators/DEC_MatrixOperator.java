/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DEC_Operators;

import complex.DEC_Complex;
import exceptions.DEC_Exception;
import utils.SparseMatrix;

/**
 *
 * @author laptop
 */
public class DEC_MatrixOperator {
 protected SparseMatrix operatorMatrix;
 
 public DEC_MatrixOperator(){
  operatorMatrix = new SparseMatrix();
 }
 public void calculateOperator(DEC_Complex complex,int dimension, char type) throws DEC_Exception{
  
 }
 public SparseMatrix getMatrix(){
  return operatorMatrix;
 }
}
