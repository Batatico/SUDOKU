package cl.ucn.disc.hpc.sudoku;

public class PossibleNumber {

    private int[] PossibleNum;

    //se arma un arreglo con todos los posibles casos que puede estar en la casilla
    public PossibleNumber() {
        this.PossibleNum = new int [9];
        fillList();
    }
    public void fillList(){
        for(int i = 0; i < 9; i++){
            this.PossibleNum[i] = i + 1;
        }
    }

    public int getNum(int index){

        return PossibleNum[index];
    }
    public int compareNumber(int n){

        for (int i = 0; i < PossibleNum.length; i++) {
            if(PossibleNum[i] == n){
                return i;
            }
        }
        return -1;
    }

    public void deleteNumber(int n){

        PossibleNum[n] = 0;
    }

}
