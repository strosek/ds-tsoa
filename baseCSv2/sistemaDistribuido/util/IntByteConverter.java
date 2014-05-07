package sistemaDistribuido.util;

public class IntByteConverter {
    public static final int SIZE_INT = Integer.SIZE / Byte.SIZE;
    public static final int SIZE_SHORT = Short.SIZE / Byte.SIZE;

    public static short toShort(byte[] data) {
        short aShort = 0;

        if (data.length >= SIZE_SHORT) {
            short singleByte = 0;
            for (int i = 0; i < SIZE_SHORT; ++i) {
                singleByte = data[i];
                singleByte &= 0x00FF;
                singleByte <<= Short.SIZE - (Byte.SIZE * (i + 1));
                aShort ^= singleByte;
            }
        }

        return aShort;
    }

    public static int toInt(byte[] data) {
        int anInt = 0;

        if (data.length >= SIZE_INT) {
            int singleByte = 0;
            for (int i = 0; i < SIZE_INT; ++i) {
                singleByte = (int) data[i];
                singleByte &= 0x000000FF;
                singleByte <<= Integer.SIZE - (Byte.SIZE * (i + 1));
                anInt ^= singleByte;
            }
        }

        return anInt;
    }

    public static byte[] toBytes(short data) {
        byte[] someBytes = new byte[SIZE_SHORT];

        short singleByte = 0xFF;
        short mask;
        short shift;
        for (int i = 0; i < SIZE_SHORT; ++i) {
            shift = (short) (Short.SIZE - (Byte.SIZE * (i + 1)));
            mask = (short) (singleByte << shift);
            someBytes[i] = (byte) ((data & mask) >>> shift);
        }

        return someBytes;
    }

    public static byte[] toBytes(int data) {
        byte[] someBytes = new byte[SIZE_INT];

        int singleByte = 0xFF;
        int mask;
        int shift;
        for (int i = 0; i < SIZE_INT; ++i) {
            shift = Integer.SIZE - (Byte.SIZE * (i + 1));
            mask = singleByte << shift;
            someBytes[i] = (byte) ((data & mask) >>> shift);
        }

        return someBytes;
    }
}
