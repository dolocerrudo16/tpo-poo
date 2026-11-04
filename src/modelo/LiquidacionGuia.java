package modelo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LiquidacionGuia implements Serializable {
    private static final long serialVersionUID = 1L;

    public static class Item implements Serializable {
        private static final long serialVersionUID = 1L;
        private final int idSalida;
        private final String tour;
        private final int dias;
        private final double honorarioDiario;
        private final double adicionalesVendidos;
        private final double monto;

        public Item(int idSalida, String tour, int dias, double honorarioDiario, double adicionalesVendidos, double monto) {
            this.idSalida = idSalida;
            this.tour = tour;
            this.dias = dias;
            this.honorarioDiario = honorarioDiario;
            this.adicionalesVendidos = adicionalesVendidos;
            this.monto = monto;
        }

        public int getIdSalida() { return idSalida; }
        public String getTour() { return tour; }
        public int getDias() { return dias; }
        public double getHonorarioDiario() { return honorarioDiario; }
        public double getAdicionalesVendidos() { return adicionalesVendidos; }
        public double getMonto() { return monto; }
    }

    private final int id;
    private final LocalDate fecha;
    private final Guia guia;
    private final List<Item> items = new ArrayList<>();

    LiquidacionGuia(int id, LocalDate fecha, Guia guia) {
        this.id = id;
        this.fecha = fecha;
        this.guia = guia;
    }

    void agregarItem(Item item) { items.add(item); }

    public double getTotal() { return Dinero.redondear(items.stream().mapToDouble(Item::getMonto).sum()); }

    public int getId() { return id; }
    public LocalDate getFecha() { return fecha; }
    public Guia getGuia() { return guia; }
    public List<Item> getItems() { return new ArrayList<>(items); }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Liquidación #").append(id).append(" - ").append(fecha).append(" - ").append(guia.getNombre()).append('\n');
        for (Item i : items) {
            sb.append(String.format("  Salida #%d (%s): %d día(s) x $%.2f + 5%% de adicionales $%.2f = $%.2f%n",
                    i.idSalida, i.tour, i.dias, i.honorarioDiario, i.adicionalesVendidos, i.monto));
        }
        sb.append(String.format("  TOTAL: $%.2f", getTotal()));
        return sb.toString();
    }
}
