package cl.ucn.disc.hpc.sudoku;

import java.security.spec.PSSParameterSpec;

public class SudokuMatrix {

    private int tam;
    private PossibleNumber [][] sudokuSol;

    //se encarga de tener una matriz donde se guarda en cada casilla  un "PossibleNumber"
    public SudokuMatrix(int tam) {
        this.sudokuSol = new PossibleNumber[tam][tam];
        this.tam = tam;
    }

    public void setNumSudoku(int posFil, int posCol, PossibleNumber n) {
        this.sudokuSol [posFil][posCol] = n;
    }

    public PossibleNumber getSolSudoku(int posFil, int posCol) {
        return sudokuSol[posFil][posCol];
    }
    public int getTam() {
        return tam;
    }

    public void setSolSudoku(int posFil, int posCol){
        this.sudokuSol[posFil][posCol] = null;
    }
}
