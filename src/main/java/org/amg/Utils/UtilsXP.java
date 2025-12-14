package org.amg.Utils;

public class UtilsXP {
    public static int getLevelFromExp(float exp) {
        if (exp <= 0) return 0;
        if (exp > 0 && exp < 7) return 1;
        if (exp > 6 && exp < 16) return 2;
        if (exp > 15 && exp < 27) return 3;

        // Para niveles >=4
        int level = (int) (Math.sqrt(exp + 9) - 3);
        return level;
    }
}

