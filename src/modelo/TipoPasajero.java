package modelo;

public enum TipoPasajero {
    ADULTO {
        @Override public double factorTarifa(int edad) { return 1.0; }
        @Override public boolean esCoherenteConEdad(int edad) { return edad >= 18; }
    },
    MENOR {
        @Override public double factorTarifa(int edad) {
            if (edad < 3) return 0.0;
            if (edad < 12) return 0.60;
            return 1.0;
        }
        @Override public boolean esCoherenteConEdad(int edad) { return edad >= 0 && edad < 18; }
    },
    JUBILADO {
        @Override public double factorTarifa(int edad) { return 0.90; }
        @Override public boolean esCoherenteConEdad(int edad) { return edad >= 60; }
    };

    public abstract double factorTarifa(int edad);

    public abstract boolean esCoherenteConEdad(int edad);
}
