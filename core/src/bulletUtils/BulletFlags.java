package bulletUtils;

public class BulletFlags {
    public final static short GHOST = 1<<7; // Fantasmos
    public final static short GROUND = 1<<8; // Flag suelo (inmovible)
    public final static short OBJECT = 1<<9; // Flag item (tiene fisicotas guays)
    public final static short ALL = -1; // Colision con todos los items
}
