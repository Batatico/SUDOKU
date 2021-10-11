package cl.ucn.disc.hpc.sudoku;
import ch.qos.logback.classic.Logger;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final Logger log = (Logger) LoggerFactory.getLogger(Main.class);
    // sudoku
    private static int[][] sudoku;
    // posibles casos
    private static SudokuMatrix possible;



    // Funcion para leer el txt con el sudoku a resolver
    public static boolean readTxt() throws FileNotFoundException {

        try{
            BufferedReader read = new BufferedReader(new FileReader("Sudoku.txt"));
            int posCol = -1;
            String text;

            while ((text = read.readLine()) != null){

                String [] textosinespacio = text.split(" ");

                //la primera vez que se lee el archivo este contendra el tamanio del sudoku, por ende aca se inicilizan
                if(posCol == -1){

                    int tam = Integer.parseInt(textosinespacio[0]);
                    sudoku = new int [tam][tam];
                    possible = new SudokuMatrix(tam);
                }else{
                    //se empieza a rellenar la matriz con los datos del archivo
                    for(int i = 0 ; i < textosinespacio.length; i++){
                        int numero = Integer.parseInt(textosinespacio[i]);
                        sudoku[i][posCol] = numero;
                        //si en la casilla hay un 0 se crea un NumeroSudoku para poder buscar los posibles valores
                        if(numero == 0){
                            PossibleNumber sudokuNum = new PossibleNumber();
                            possible.setNumSudoku(i,posCol,sudokuNum);
                        }
                    }

                }
                posCol++;
            }
            return true;
        }catch (Exception exception){
            return  false;
        }
    }

    /**
     * Busca entre todas las casillas por columna, el posible numero que no se repite, dejando por decarte a este como
     * unica solucion
     */
    public static int[] searchInCol(){

        //recorre por fila
        for (int i = 0; i < possible.getTam(); i++) {
            //recorre dentro de la fila por columna
            for (int j = 0; j < possible.getTam(); j++) {
                //mientras exista un valor posible
                if(possible.getSolSudoku(i,j) != null){
                    //recorre todos los valores posibles
                    for (int k = 0; k < 9; k++) {
                        int posibleUnico = possible.getSolSudoku(i,j).getNum(k);
                        boolean flag = true;
                        //mientras el posible numero no sea 0
                        if(posibleUnico != 0){
                            //recorre todos los de la columna buscando si existe algun otro cuadro con el mismo posible valor
                            for (int l = 0; l < possible.getTam(); l++) {
                                //mientras la casilla sea distinta a null y no sea la misma con la que se compara
                                if(possible.getSolSudoku(i,l) != null && possible.getSolSudoku(i,l) != possible.getSolSudoku(i,j)){
                                    //recorre los posibles datos que tiene la casilla con la que se compara
                                    for (int m = 0; m < 9; m++) {
                                        int comparador = possible.getSolSudoku(i,l).getNum(m);
                                        if(comparador == posibleUnico){
                                            flag = false;
                                            break;
                                        }
                                    }
                                }
                                if(!flag){
                                    break;
                                }
                            }
                            if(flag){
                                int [] unique = {i,j,posibleUnico};
                                return unique;
                            }
                        }
                    }
                }
            }
        }
        int [] none = {-1};
        return none;
    }

    /**
     * Busca entre todas las casillas por fila, el posible numero que no se repite, dejando por descarte este como solucion
     * @return
     */
    public static int[] searchInFil(){

        //recorre por fila
        for (int i = 0; i < possible.getTam(); i++) {
            //recorre dentro de la fila por columna
            for (int j = 0; j < possible.getTam(); j++) {
                //mientras exista un valor posible
                if(possible.getSolSudoku(j,i) != null){
                    //recorre todos los valores posibles
                    for (int k = 0; k < 9; k++) {
                        int posibleUnico = possible.getSolSudoku(j,i).getNum(k);
                        boolean flag = true;
                        //mientras el posible numero no sea 0
                        if(posibleUnico != 0){
                            //recorre todos los de la fila buscando si existe algun otro cuadro con el mismo posible valor
                            for (int l = 0; l < possible.getTam(); l++) {
                                //mientras la casilla sea distinta a null y no sea la misma con la que se compara
                                if(possible.getSolSudoku(l,i) != null && possible.getSolSudoku(l,i) != possible.getSolSudoku(j,i)){
                                    //recorre los posibles datos que tiene la casilla con la que se compara
                                    for (int m = 0; m < 9; m++) {
                                        int comparador = possible.getSolSudoku(l,i).getNum(m);
                                        if(comparador == posibleUnico){
                                            flag = false;
                                            break;
                                        }
                                    }
                                }
                                if(!flag){
                                    break;
                                }
                            }
                            if(flag){
                                int [] unique = {j,i,posibleUnico};
                                return unique;
                            }
                        }
                    }
                }
            }
        }
        int [] none = {-1};
        return none;
    }

    /**
     * Busca en la matriz de posibles, alguna casillo donde exista un solo numero como posible, una vez que lo encuentra,
     * lo retorna y su posicion, de caso contrario retorna -1
     */

    public static int [] buscarPosibleSolo(){

        for (int i = 0; i < possible.getTam(); i++) {
            for (int j = 0; j < possible.getTam(); j++) {
                int cont = 0;
                int [] numSolo = new int [3];
                if(possible.getSolSudoku(j,i) != null){
                    for (int k = 0; k < 9; k++) {
                        if(possible.getSolSudoku(j,i).getNum(k) != 0){
                            cont++;
                            numSolo [0] = j;
                            numSolo [1] = i;
                            numSolo [2] = possible.getSolSudoku(j,i).getNum(k);
                        }
                    }
                    if(cont == 1){
                        return numSolo;
                    }
                }
            }
        }
        int [] vacio = {-1};
        return vacio;
    }

    /**
     * Va avanzando por la fila, eliminando todos los numeros que coincidan con n
     */
    public static void deleteFil(int n, int posFil){

        for (int i = 0; i < possible.getTam(); i++) {
            if(possible.getSolSudoku(posFil, i) != null){
                int deletePos = possible.getSolSudoku(posFil,i).compareNumber(n);
                if( deletePos != -1){
                    possible.getSolSudoku(posFil,i).deleteNumber(deletePos);
                }
            }
        }
    }

    public static void deleteCol(int n, int posCol){

        for (int i = 0; i < possible.getTam(); i++) {
            if(possible.getSolSudoku(i, posCol) != null) {
                int deletePos = possible.getSolSudoku(i, posCol).compareNumber(n);
                if (deletePos != -1) {
                    possible.getSolSudoku(i, posCol).deleteNumber(deletePos);
                }
            }
        }
    }

    /**
     * Elimina todos los numeros que sean iguales a numeroComparar que se encuentren en el cuadrante

     */
    public static void deleteSquare(int n, int col, int fil){

        double auxCol = col;
        double auxFil = fil;

        while(auxCol%3 != 0 || auxFil%3 != 0){

            if(auxCol%3 != 0){
                auxCol--;
            }
            if(auxFil%3 != 0){
                auxFil--;
            }
        }

        for(int i = (int)auxCol; i < auxCol + 3; i++){
            for (int j = (int) auxFil; j < auxFil + 3; j++) {
                if(possible.getSolSudoku(j,i) != null) {
                    int posNum = possible.getSolSudoku(j,i).compareNumber(n);
                    if (posNum != -1) {
                        possible.getSolSudoku(j,i).deleteNumber(posNum);
                    }
                }
            }
        }
    }

    /**
     * Soluciona el sudoku que es ingresado por pantalla

     */
    public static void main(String[] args) throws FileNotFoundException, InterruptedException {

        final StopWatch time = StopWatch.createStarted();
        //lectura del archivo, en caso de que no lo encuentre no entra al if y muestra el mensaje
        if(readTxt()){
            int cores = 5;
            final ExecutorService executorService = Executors.newFixedThreadPool(cores);

            //imprime el sudoku inicial
            for (int i = 0; i < sudoku.length; i++) {
                for (int j = 0; j < sudoku.length; j++) {
                    System.out.print(sudoku[j][i]+" ");
                }
                System.out.println();
            }


            //Recorre la matriz sudoku buscando cuales numeros son diferentes a 0 para poder eliminarlos de los posibles
            for(int i = 0; i < sudoku.length; i++){
                for (int j = 0; j < sudoku.length; j++) {
                    if(sudoku[j][i] != 0){
                        executorService.submit(new eliminar(i, j, sudoku[j][i]));
                    }
                }
            }
            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.MILLISECONDS);
            int cantDeMov = 0;

            while (cantDeMov != sudoku.length*sudoku.length){

                final ExecutorService executorService1 = Executors.newFixedThreadPool(cores);

                //Busca y va eliminando en todos los posibles numeros el posible que solamente tiene una opcion,
                //debido a que ese es la opcion
                for(int x = 0; x < possible.getTam()*possible.getTam();x++){
                    int [] numeroEliminar = buscarPosibleSolo();
                    if(numeroEliminar[0] != -1){
                        possible.setSolSudoku(numeroEliminar[0], numeroEliminar[1]);
                        sudoku[numeroEliminar[0]][numeroEliminar[1]]= numeroEliminar[2];
                        executorService1.submit(new eliminar(numeroEliminar[1], numeroEliminar[0], numeroEliminar[2]));
                    }
                }
                executorService1.shutdown();
                executorService1.awaitTermination(1, TimeUnit.MILLISECONDS);

                int [] eliminarFila = searchInFil();
                //Busca al unico que puede ser en toda la fila, si es distinto a -1 significa que existe uno para eliminar
                if(eliminarFila[0] != -1){
                    final ExecutorService executorService2 = Executors.newFixedThreadPool(cores);
                    possible.setSolSudoku(eliminarFila[0], eliminarFila[1]);
                    sudoku[eliminarFila[0]][eliminarFila[1]]= eliminarFila[2];
                    executorService2.submit(new eliminar(eliminarFila[1], eliminarFila[0], eliminarFila[2]));
                    executorService2.shutdown();
                    executorService2.awaitTermination(1, TimeUnit.MILLISECONDS);
                }

                int [] eliminarColumna = searchInCol();
                //Busca al unico que puede ser en toda la columna, si es distinto a -1 significa que existe uno para eliminar
                if(eliminarColumna[0] != -1){
                    final ExecutorService executorService3 = Executors.newFixedThreadPool(cores);
                    possible.setSolSudoku(eliminarColumna[0], eliminarColumna[1]);
                    sudoku[eliminarColumna[0]][eliminarColumna[1]]= eliminarColumna[2];
                    executorService3.submit(new eliminar(eliminarColumna[1], eliminarColumna[0], eliminarColumna[2]));
                    executorService3.shutdown();
                    executorService3.awaitTermination(1, TimeUnit.MILLISECONDS);
                }

                cantDeMov++;
            }
        }else{
            log.debug("Sudoku no encontrado");
        }
        log.debug("demoro {} EN HALLAR LA SOLUCION",time);

        //imprime el sudoku final
        for (int i = 0; i < sudoku.length; i++) {
            for (int j = 0; j < sudoku.length; j++) {
                System.out.print(sudoku[j][i]+" ");
            }
            System.out.println();
        }
    }

    private static class eliminar implements Runnable{

        private int posCol;
        private int posFil;
        private int n;

        eliminar(int col, int fil, int n){
            this.posCol = col;
            this.posFil = fil;
            this.n = n;
        }

        @Override
        public void run() {
            deleteCol(n,posCol);
            deleteFil(n,posFil);
            deleteSquare(n,posCol,posFil);
        }
    }


}
