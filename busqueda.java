/**
 *
 * @author Jose Olivera
 */

import java.util.*;

// Clase para representar una posición en el grid
class Posicion {
    int x, y;

    // Constructor
    Posicion(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Método para comparar dos posiciones
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Posicion posicion = (Posicion) obj;
        return x == posicion.x && y == posicion.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    // Método para representar la posición como string
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}

// Clase genérica para representar un par de valores
class Par<K, V> {
    private K clave;
    private V valor;

    public Par(K clave, V valor) {
        this.clave = clave;
        this.valor = valor;
    }

    public K getClave() {
        return clave;
    }

    public V getValor() {
        return valor;
    }
}

public class BusquedaRobot {

    static final int TAMANO_GRID = 100;
    static final int[][] MOVIMIENTOS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // Izquierda, derecha, abajo, arriba

    static boolean[][] obstaculos = new boolean[TAMANO_GRID][TAMANO_GRID];

    static List<int[]> busquedaBFS(Posicion inicio, Posicion meta, int maxPasos) {
        Queue<Par<Posicion, List<int[]>>> cola = new LinkedList<>();
        cola.offer(new Par<>(inicio, new ArrayList<>()));

        boolean[][] visitado = new boolean[TAMANO_GRID][TAMANO_GRID];

        while (!cola.isEmpty()) {
            Par<Posicion, List<int[]>> actual = cola.poll();
            Posicion pos = actual.getClave();
            List<int[]> camino = actual.getValor();

            if (pos.equals(meta)) {
                return camino;
            }

            if (visitado[pos.x][pos.y] || camino.size() > maxPasos) {
                continue;
            }

            visitado[pos.x][pos.y] = true;

            for (int[] movimiento : MOVIMIENTOS) {
                int nuevoX = pos.x + movimiento[0];
                int nuevoY = pos.y + movimiento[1];

                if (esPosicionValida(nuevoX, nuevoY) && !obstaculos[nuevoX][nuevoY]) {
                    Posicion siguiente = new Posicion(nuevoX, nuevoY);
                    List<int[]> nuevoCamino = new ArrayList<>(camino);
                    nuevoCamino.add(movimiento);
                    cola.offer(new Par<>(siguiente, nuevoCamino));
                }
            }
        }
        return null;
    }

    static List<int[]> busquedaAEstrella(Posicion inicio, Posicion meta, int maxPasos) {
        PriorityQueue<Par<Posicion, List<int[]>>> cola = new PriorityQueue<>(
            Comparator.comparingInt(a -> distanciaManhattan(a.getClave(), meta) + a.getValor().size())
        );
        cola.offer(new Par<>(inicio, new ArrayList<>()));

        boolean[][] visitado = new boolean[TAMANO_GRID][TAMANO_GRID];

        while (!cola.isEmpty()) {
            Par<Posicion, List<int[]>> actual = cola.poll();
            Posicion pos = actual.getClave();
            List<int[]> camino = actual.getValor();

            if (pos.equals(meta)) {
                return camino;
            }

            if (visitado[pos.x][pos.y] || camino.size() > maxPasos) {
                continue;
            }

            visitado[pos.x][pos.y] = true;

            for (int[] movimiento : MOVIMIENTOS) {
                int nuevoX = pos.x + movimiento[0];
                int nuevoY = pos.y + movimiento[1];

                if (esPosicionValida(nuevoX, nuevoY) && !obstaculos[nuevoX][nuevoY]) {
                    Posicion siguiente = new Posicion(nuevoX, nuevoY);
                    List<int[]> nuevoCamino = new ArrayList<>(camino);
                    nuevoCamino.add(movimiento);
                    cola.offer(new Par<>(siguiente, nuevoCamino));
                }
            }
        }
        return null;
    }

    static boolean esPosicionValida(int x, int y) {
        return x >= 0 && x < TAMANO_GRID && y >= 0 && y < TAMANO_GRID;
    }

    static int distanciaManhattan(Posicion a, Posicion b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    static void imprimirResultado(List<int[]> camino, Posicion inicio, String tipoBusqueda) {
        if (camino != null) {
            System.out.println("Solución encontrada con " + tipoBusqueda + " en " + camino.size() + " pasos:");
            int actualX = inicio.x;
            int actualY = inicio.y;
            for (int[] mov : camino) {
                actualX += mov[0];
                actualY += mov[1];
                System.out.println("Mover a (" + actualX + ", " + actualY + ")");
            }
        } else {
            System.out.println("No se encontró solución con " + tipoBusqueda + " dentro del límite de pasos.");
        }
    }

    static void generarObstaculosAleatorios(int cantidad) {
        Random rand = new Random();
        for (int i = 0; i < cantidad; i++) {
            int x = rand.nextInt(TAMANO_GRID);
            int y = rand.nextInt(TAMANO_GRID);
            obstaculos[x][y] = true;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Posicion inicio = solicitarPosicion(scanner, "inicial del robot");
        Posicion meta = solicitarPosicion(scanner, "del punto de montaje");
        int maxPasos = solicitarMaxPasos(scanner);

        generarObstaculosAleatorios(50);

        System.out.println("\nIniciando búsqueda BFS...");
        List<int[]> caminoBFS = busquedaBFS(inicio, meta, maxPasos);
        imprimirResultado(caminoBFS, inicio, "BFS");

        System.out.println("\nIniciando búsqueda A*...");
        List<int[]> caminoAEstrella = busquedaAEstrella(inicio, meta, maxPasos);
        imprimirResultado(caminoAEstrella, inicio, "A*");

        scanner.close();
    }

    private static Posicion solicitarPosicion(Scanner scanner, String tipo) {
        int x, y;
        do {
            System.out.print("Ingrese la posición X " + tipo + " (0-99): ");
            x = scanner.nextInt();
        } while (x < 0 || x >= TAMANO_GRID);

        do {
            System.out.print("Ingrese la posición Y " + tipo + " (0-99): ");
            y = scanner.nextInt();
        } while (y < 0 || y >= TAMANO_GRID);

        return new Posicion(x, y);
    }

    private static int solicitarMaxPasos(Scanner scanner) {
        int maxPasos;
        do {
            System.out.print("Ingrese el número máximo de pasos permitidos: ");
            maxPasos = scanner.nextInt();
        } while (maxPasos <= 0);
        return maxPasos;
    }
